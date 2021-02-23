package com.vibhorsrv.cameraids.util;

import android.util.Log;

import com.vibhorsrv.cameraids.checkroot.CheckRoot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CameraDumpUtil {
    /**
     * @return list of lines as displayed on executing the dumpsys command
     */
    public static List<CharSequence> getCameraDump() {
        if (CheckRoot.hasRootPermission()) {
            try {
                Process process = Runtime.getRuntime().exec("su -c dumpsys media.camera");
                return new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("CameraDumpUtil", "getCameraDump(): Root Not Available");
        return new ArrayList<>();
    }
}
