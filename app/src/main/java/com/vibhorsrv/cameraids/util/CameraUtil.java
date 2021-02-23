package com.vibhorsrv.cameraids.util;

import android.util.Size;
import android.util.SizeF;

public class CameraUtil {
    public static float calculatePixelSize(int pixelArrayWidth, float sensorWidth) {
        return (sensorWidth / ((float) pixelArrayWidth)) * 1000.0f;
    }

    public static Double calculateAngleOfView(float focalLength, SizeF sensorSize, Size pixelArraySize) {
        float pixelSize = CameraUtil.calculatePixelSize(pixelArraySize.getWidth(), sensorSize.getWidth());
        return Math.toDegrees(Math.atan(Math.sqrt(Math.pow(sensorSize.getWidth() * pixelSize, 2.0d)
                + Math.pow(sensorSize.getHeight() * pixelSize, 2.0d)) / ((double) (2.0f * focalLength))) * 2.0d);
    }

    public static float calculate35mmeqv(float focalLength, SizeF sensorSize) {
        return (36.0f / sensorSize.getWidth()) * focalLength;
    }

}
