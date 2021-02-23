package com.vibhorsrv.cameraids.saver;

import android.os.Build;

import com.vibhorsrv.cameraids.api.CameraIDs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Saver implements CameraIDs.Saver {

    public static String generateFileName(String prefix, String ext) {
        return prefix + '-' + Build.BRAND + "-" + Build.MODEL + "-" + Build.MANUFACTURER + "-" + Build.DEVICE + '.' + ext;
    }

    @Override
    public void saveText(String path, String text) {
        writeToFile(path, text.getBytes());
    }

    private void writeToFile(String path, byte[] dataToWrite) {
        try {
            Files.write(Paths.get(path), dataToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
