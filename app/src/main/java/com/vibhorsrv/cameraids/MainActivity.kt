package com.vibhorsrv.cameraids

import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vibhorsrv.cameraids.checkroot.CheckRoot
import com.vibhorsrv.cameraids.finder.CameraFinder
import com.vibhorsrv.cameraids.finder.CameraIdentifier
import com.vibhorsrv.cameraids.model.CameraModel
import com.vibhorsrv.cameraids.saver.Saver
import com.vibhorsrv.cameraids.share.Share
import com.vibhorsrv.cameraids.util.AppUtil
import com.vibhorsrv.cameraids.util.CameraDumpUtil
import java.io.File
import java.text.DateFormat
import java.util.*
import java.util.function.Consumer

/**
 * Created by Vibhor on 23/09/2020
 */
class MainActivity : AppCompatActivity() {
    private val saver = Saver()
    private val textUtil = TextUtil()
    private var stringBuilder: StringBuilder? = null
    private val mTextView by lazy { findViewById<TextView>(R.id.textView) }
    private val floatingActionButton by lazy { findViewById<FloatingActionButton>(R.id.share) }
    private val cameraManager by lazy { getSystemService(CAMERA_SERVICE) as CameraManager }
    private val cameraFinder by lazy { CameraFinder(cameraManager) }
    private val cameraIdentifier by lazy { CameraIdentifier(cameraFinder.cameraModels) }
    private var bufferText = ""
    private var cameraDumpText = ""
    private val contentLoadingProgressBar by lazy { findViewById<ContentLoadingProgressBar>(R.id.progress_circular) }
    private var cameraDumpMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraFinder.init()
        cameraIdentifier.init()

        showCameraIDsInfo()
    }

    override fun onBackPressed() {
        if (cameraDumpMode) {
            showCameraIDsInfo()
        } else {
            super.onBackPressed()
        }
    }

    private fun showCameraIDsInfo() {
        mTextView.text = "".also { bufferText = it }
        cameraDumpMode = false
        title = CAMERA_IDS
        val newFile = File(
            getExternalFilesDir(null)!!.absolutePath, Saver.generateFileName(
                CAMERA_IDS, "txt"
            )
        )
        val fileUri = FileProvider.getUriForFile(
            this, applicationContext.packageName + ".provider", newFile)
        if (bufferText == "") {
            contentLoadingProgressBar.show()
            stringBuilder = StringBuilder()
            stringBuilder?.let {
                textUtil.addTimeStamp(it)
                textUtil.addDeviceInfo(it)
                textUtil.addBasicInfo(it)
                cameraFinder.cameraModels.forEach(Consumer { cameraModel: CameraModel ->
                    it
                        .append(cameraModel)
                        .append("\n============================\n")
                })
                bufferText = it.toString()
                saver.saveText(newFile.absolutePath, bufferText)
                mTextView.text = bufferText
            }

            contentLoadingProgressBar.hide()
        }
        floatingActionButton.setOnClickListener {
            Share.shareUri(this, fileUri, textUtil.generateExtraShareText(CAMERA_IDS))
        }
    }

    private fun showCameraDump() {
        mTextView.text = "".also { bufferText = it }
        cameraDumpMode = true
        title = CAMERA_DUMP
        val newFile = File(
            getExternalFilesDir(null)!!.absolutePath, Saver.generateFileName(
                CAMERA_DUMP, "txt"
            )
        )
        val fileUri = FileProvider.getUriForFile(
                this, "${applicationContext.packageName}.provider", newFile)
        val t = HandlerThread("CameraDumpThread")
        t.start()
        val h = Handler(t.looper)
        h.post {
            if (bufferText == "") {
                contentLoadingProgressBar.show()
                stringBuilder = StringBuilder()
                stringBuilder?.let {
                    textUtil.addTimeStamp(it)
                    textUtil.addDeviceInfo(it)
                    cameraDumpText = java.lang.String.join("\n", CameraDumpUtil.cameraDump)
                    it.append(cameraDumpText)
                    bufferText = it.toString()
                    mTextView.post {
                        if (cameraDumpText != "") {
                            if (mTextView.text.toString() == "") {
                                mTextView.text = bufferText
                                saver.saveText(newFile.absolutePath, bufferText)
                            }
                            floatingActionButton.setOnClickListener {
                                Share.shareUri(
                                    this, fileUri, textUtil.generateExtraShareText(
                                        CAMERA_DUMP
                                    )
                                )
                            }
                        } else {
                            Toast.makeText(
                                this, R.string.root_data_na_warn, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
                contentLoadingProgressBar.post { contentLoadingProgressBar.hide() }
            }
        }
        t.quitSafely()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        menu.findItem(R.id.action_camera_dump)?.apply {
            isVisible = CheckRoot.isRooted
        }
        return true
    }

    //Menu click listeners
    fun onInfoClicked(item: MenuItem) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.action_info)
        val br = getString(R.string.line_break)
        val html = (getString(R.string.app_info)
                + br
                + getString(R.string.app_version, AppUtil.getVersionName(this))
                + br
                + getString(R.string.github_link))
        builder.setMessage(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT))
        val dialog = builder.create()
        dialog.show()
        dialog.findViewById<TextView>(android.R.id.message)?.apply {
            setLinkTextColor(resources.getColor(R.color.colorPrimary, null))
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    fun onCameraDumpClicked(item: MenuItem) {
        if (CheckRoot.hasRootPermission()) {
            showCameraDump()
        } else {
            Toast.makeText(this, R.string.root_na_warn, Toast.LENGTH_SHORT).show()
        }
    }

    private inner class TextUtil {
        fun generateExtraShareText(prefix: String): String {
            val stringBuilder = StringBuilder()
            stringBuilder
                .append(prefix).append("\n")
                .append("\n============================\n")
            addDeviceInfo(stringBuilder)
            return stringBuilder.toString()
        }

        fun addTimeStamp(stringBuilder: StringBuilder) {
            stringBuilder
                .append(
                    DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.ROOT)
                        .format(Date())
                )
                .append("\n")
                .append("\n============================\n")
        }

        fun addDeviceInfo(stringBuilder: StringBuilder) {
            stringBuilder
                .append("\n")
                .append("Device : ${Build.BRAND} ${Build.MODEL} (${Build.DEVICE})")
                .append("\n")
                .append("Manufacturer : ${Build.MANUFACTURER}")
                .append("\n")
                .append("Android : ${Build.VERSION.RELEASE}")
                .append("\n")
                .append("Fingerprint : ${Build.FINGERPRINT}")
                .append("\n")
                .append("\n============================\n")
        }

        fun addBasicInfo(stringBuilder: StringBuilder) {
            stringBuilder
                .append("\n")
                .append("Camera IDs Visible to Apps = ${cameraFinder.apiCameraIdList}")
                .append("\n")
                .append("\n============================\n")
                .append("\n")
                .append("All Camera IDs = ${cameraFinder.allCameraIdList}")
                .append("\n")
                .append("\n============================\n")
        }
    }

    companion object {
        private const val CAMERA_DUMP = "CameraDump"
        private const val CAMERA_IDS = "CameraIDs"
    }
}
