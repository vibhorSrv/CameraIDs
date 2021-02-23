package com.vibhorsrv.cameraids.checkroot;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class CheckRoot {
    private static final String[] pathList;
    private static final String SU = "su";
    private static final String TAG = "CheckRoot";

    static {
        pathList = new String[]{
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/"
        };
    }

    public static boolean isRooted() {
        return binariesExist(SU);
    }

    private static boolean binariesExist(String binaryName) {
        for (String path : pathList) {
            if (new File(path, binaryName).exists())
                return true;
        }
        return false;
    }

    public static boolean hasRootPermission() {
        boolean hasRoot = false;
        if (isRooted()) {
            try {
                Process p = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                Log.d(TAG, "hasRootPermission(): Checking...");
                os.writeBytes("exit\n");
                os.flush();
                try {
                    p.waitFor();
                    hasRoot = p.exitValue() != 255;
                } catch (InterruptedException e) {
                    hasRoot = false;
                }
            } catch (IOException e) {
                hasRoot = false;
            }
        }
        Log.d(TAG, "hasRootPermission(): hasRoot = " + hasRoot);
        return hasRoot;
    }
}
