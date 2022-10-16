package com.vibhorsrv.cameraids.saver

import android.os.Build
import com.vibhorsrv.cameraids.api.CameraIDs
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

class Saver : CameraIDs.Saver {
    override fun saveText(path: String, text: String) {
        writeToFile(path, text.toByteArray())
    }

    private fun writeToFile(path: String, dataToWrite: ByteArray) {
        try {
            Files.write(Paths.get(path), dataToWrite)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun generateFileName(prefix: String, ext: String): String {
            return prefix + '-' + Build.BRAND + "-" + Build.MODEL + "-" + Build.MANUFACTURER + "-" + Build.DEVICE + '.' + ext
        }
    }
}
