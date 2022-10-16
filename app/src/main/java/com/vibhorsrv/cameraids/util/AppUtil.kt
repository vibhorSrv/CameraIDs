package com.vibhorsrv.cameraids.util

import android.content.Context
import android.content.pm.PackageManager

object AppUtil {
    fun getVersionName(context: Context): String {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}
