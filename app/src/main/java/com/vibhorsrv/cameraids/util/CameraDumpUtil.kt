package com.vibhorsrv.cameraids.util

import android.util.Log
import com.vibhorsrv.cameraids.checkroot.CheckRoot
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.stream.Collectors

object CameraDumpUtil {
    /**
     * @return list of lines as displayed on executing the dumpsys command
     */
    val cameraDump: List<CharSequence>
        get() {
            if (CheckRoot.hasRootPermission()) {
                try {
                    val process = Runtime.getRuntime().exec("su -c dumpsys media.camera")
                    return BufferedReader(InputStreamReader(process.inputStream)).lines().collect(
                        Collectors.toList()
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            Log.d("CameraDumpUtil", "getCameraDump(): Root Not Available")
            return ArrayList()
        }
}
