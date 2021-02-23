package com.vibhorsrv.cameraids;

import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vibhorsrv.cameraids.api.CameraIDs;
import com.vibhorsrv.cameraids.checkroot.CheckRoot;
import com.vibhorsrv.cameraids.finder.CameraFinder;
import com.vibhorsrv.cameraids.finder.CameraIdentifier;
import com.vibhorsrv.cameraids.model.CameraModel;
import com.vibhorsrv.cameraids.saver.Saver;
import com.vibhorsrv.cameraids.share.Share;
import com.vibhorsrv.cameraids.util.AppUtil;
import com.vibhorsrv.cameraids.util.CameraDumpUtil;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vibhor on 23/09/2020
 */
public class MainActivity extends AppCompatActivity {
    private static final String CAMERA_DUMP = "CameraDump";
    private static final String CAMERA_IDS = "CameraIDs";
    private final Saver saver = new Saver();
    private final TextUtil textUtil = new TextUtil();
    private StringBuilder stringBuilder;
    private TextView mTextView;
    private FloatingActionButton floatingActionButton;
    private CameraManager cameraManager;
    private CameraIDs.Finder<ArrayList<CameraModel>> cameraFinder;
    private CameraIDs.Identifier<ArrayList<CameraModel>> cameraIdentifier;
    private String bufferText = "";
    private String cameraDumpText = "";
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private boolean cameraDumpMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.textView);
        floatingActionButton = findViewById(R.id.share);
        contentLoadingProgressBar = findViewById(R.id.progress_circular);

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        cameraFinder = new CameraFinder(cameraManager);
        cameraFinder.init();
        cameraIdentifier = new CameraIdentifier(cameraFinder.getCameraModels());
        cameraIdentifier.init();

        showCameraIDsInfo();
    }

    @Override
    public void onBackPressed() {
        if (cameraDumpMode) {
            showCameraIDsInfo();
        } else {
            super.onBackPressed();
        }
    }

    private void showCameraIDsInfo() {
        mTextView.setText(bufferText = "");
        cameraDumpMode = false;
        setTitle(CAMERA_IDS);

        File newFile = new File(getExternalFilesDir(null).getAbsolutePath(), Saver.generateFileName(CAMERA_IDS, "txt"));
        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", newFile);

        if (bufferText.equals("")) {
            contentLoadingProgressBar.show();

            stringBuilder = new StringBuilder();
            textUtil.addTimeStamp(stringBuilder);
            textUtil.addDeviceInfo(stringBuilder);
            textUtil.addBasicInfo(stringBuilder);
            cameraFinder.getCameraModels().forEach(cameraModel ->
                    stringBuilder
                            .append(cameraModel)
                            .append("\n============================\n"));
            bufferText = stringBuilder.toString();

            saver.saveText(newFile.getAbsolutePath(), bufferText);
            mTextView.setText(bufferText);
            contentLoadingProgressBar.hide();
        }
        floatingActionButton.setOnClickListener(v -> Share.shareUri(this, fileUri, textUtil.generateExtraShareText(CAMERA_IDS)));
    }


    private void showCameraDump() {
        mTextView.setText(bufferText = "");
        cameraDumpMode = true;
        setTitle(CAMERA_DUMP);

        File newFile = new File(getExternalFilesDir(null).getAbsolutePath(), Saver.generateFileName(CAMERA_DUMP, "txt"));
        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", newFile);

        HandlerThread t = new HandlerThread("CameraDumpThread");
        t.start();
        Handler h = new Handler(t.getLooper());
        h.post(() -> {
            if (bufferText.equals("")) {
                contentLoadingProgressBar.show();

                stringBuilder = new StringBuilder();
                textUtil.addTimeStamp(stringBuilder);
                textUtil.addDeviceInfo(stringBuilder);
                cameraDumpText = String.join("\n", CameraDumpUtil.getCameraDump());
                stringBuilder.append(cameraDumpText);
                bufferText = stringBuilder.toString();

                mTextView.post(() -> {
                    if (!cameraDumpText.equals("")) {
                        if (mTextView.getText().toString().equals("")) {
                            mTextView.setText(bufferText);
                            saver.saveText(newFile.getAbsolutePath(), bufferText);
                        }
                        floatingActionButton.setOnClickListener(v -> Share.shareUri(this, fileUri, textUtil.generateExtraShareText(CAMERA_DUMP)));
                    } else {
                        Toast.makeText(this, R.string.root_data_na_warn, Toast.LENGTH_LONG).show();
                    }
                });

                contentLoadingProgressBar.post(contentLoadingProgressBar::hide);
            }
        });
        t.quitSafely();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem cameraDumpOption = menu.findItem(R.id.action_camera_dump);
        if (cameraDumpOption != null) {
            cameraDumpOption.setVisible(CheckRoot.isRooted());
        }
        return true;
    }

    //Menu click listeners

    public void onInfoClicked(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_info);
        String br = getString(R.string.line_break);
        String html =
                getString(R.string.app_info)
                        + br
                        + getString(R.string.app_version, AppUtil.getVersionName(this))
                        + br
                        + getString(R.string.github_link);
        builder.setMessage(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView messageTextView = dialog.findViewById(android.R.id.message);
        if (messageTextView != null) {
            messageTextView.setLinkTextColor(getResources().getColor(R.color.colorPrimary, null));
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void onCameraDumpClicked(MenuItem item) {
        if (CheckRoot.hasRootPermission()) {
            showCameraDump();
        } else {
            Toast.makeText(this, R.string.root_na_warn, Toast.LENGTH_SHORT).show();
        }
    }

    private class TextUtil {
        private String generateExtraShareText(String prefix) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder
                    .append(prefix).append("\n")
                    .append("\n============================\n");
            addDeviceInfo(stringBuilder);
            return stringBuilder.toString();
        }

        private void addTimeStamp(StringBuilder stringBuilder) {
            stringBuilder
                    .append(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.ROOT).format(new Date()))
                    .append("\n")
                    .append("\n============================\n");
        }

        private void addDeviceInfo(StringBuilder stringBuilder) {
            stringBuilder
                    .append("\n")
                    .append("Device : ").append(Build.BRAND).append(" ").append(Build.MODEL).append(" (").append(Build.DEVICE).append(')')
                    .append("\n")
                    .append("Manufacturer : ").append(Build.MANUFACTURER)
                    .append("\n")
                    .append("Android : ").append(Build.VERSION.RELEASE)
                    .append("\n")
                    .append("Fingerprint : ").append(Build.FINGERPRINT)
                    .append("\n")
                    .append("\n============================\n");
        }

        private void addBasicInfo(StringBuilder stringBuilder) {
            stringBuilder
                    .append("\n")
                    .append("Camera IDs Visible to Apps = ").append(cameraFinder.getApiCameraIdList())
                    .append("\n")
                    .append("\n============================\n")
                    .append("\n")
                    .append("All Camera IDs = ").append(cameraFinder.getAllCameraIdList())
                    .append("\n")
                    .append("\n============================\n");
        }

    }
}