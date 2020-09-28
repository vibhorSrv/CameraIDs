package com.vibhorsrv.cameraids;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Vibhor on 23/09/2020
 */
public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        FloatingActionButton floatingActionButton = findViewById(R.id.share);

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        CamerasFinder camerasFinder = new CamerasFinder(cameraManager);

        mTextView.setText(camerasFinder.getResultString());
        floatingActionButton.setOnClickListener(v -> writeToFileAndShare(camerasFinder.getFileName(), mTextView.getText().toString()));
    }

    private void writeToFileAndShare(String filename, String textToWrite) {
        File newFile = new File(getExternalFilesDir(null).getAbsolutePath(), filename + ".txt");
        try {
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(textToWrite.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri fileUri = FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", newFile);
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setType("text/txt");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public void onInfoClicked(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_info);
        String br = getString(R.string.line_break);
        String html =
                getString(R.string.info_repeat,br) +
                getString(R.string.info_logical,br) +
//                getString(R.string.info_profile,br) +
                getString(R.string.github_link,br);
        builder.setMessage(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView messageTextView = dialog.findViewById(android.R.id.message);
        if (messageTextView != null) {
            messageTextView.setLinkTextColor(getResources().getColor(R.color.colorPrimary, null));
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}