package com.vibhorsrv.cameraids.checkroot

import android.util.Log
import java.io.DataOutputStream
import java.io.File
import java.io.IOException

object CheckRoot {
    private val pathList = arrayOf(
        "/sbin/",
        "/system/bin/",
        "/system/xbin/",
        "/data/local/xbin/",
        "/data/local/bin/",
        "/system/sd/xbin/",
        "/system/bin/failsafe/",
        "/data/local/"
    )
    private const val SU = "su"
    private const val TAG = "CheckRoot"

    val isRooted: Boolean
        get() = binariesExist(SU)

    private fun binariesExist(binaryName: String): Boolean {
        for (path in pathList) {
            if (File(path, binaryName).exists()) return true
        }
        return false
    }

    fun hasRootPermission(): Boolean {
        var hasRoot = false
        if (isRooted) {
            hasRoot = try {
                val p = Runtime.getRuntime().exec(SU)
                val os = DataOutputStream(p.outputStream)
                Log.d(TAG, "hasRootPermission(): Checking...")
                os.writeBytes("exit\n")
                os.flush()
                try {
                    p.waitFor()
                    p.exitValue() != 255
                } catch (e: InterruptedException) {
                    false
                }
            } catch (e: IOException) {
                false
            }
        }
        Log.d(TAG, "hasRootPermission(): hasRoot = $hasRoot")
        return hasRoot
    }
}
