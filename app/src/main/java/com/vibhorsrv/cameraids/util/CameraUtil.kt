package com.vibhorsrv.cameraids.util

import android.util.Size
import android.util.SizeF
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

object CameraUtil {
    fun calculatePixelSize(pixelArrayWidth: Int, sensorWidth: Float): Float {
        return sensorWidth / pixelArrayWidth.toFloat() * 1000.0f
    }

    fun calculateAngleOfView(
        focalLength: Float, sensorSize: SizeF, pixelArraySize: Size
    ): Double {
        val pixelSize = calculatePixelSize(pixelArraySize.width, sensorSize.width)
        return Math.toDegrees(atan(sqrt((sensorSize.width * pixelSize).toDouble().pow(2.0)
                + (sensorSize.height * pixelSize).toDouble().pow(2.0)
        ) / (2.0f * focalLength).toDouble()) * 2.0)
    }

    fun calculate35mmeqv(focalLength: Float, sensorSize: SizeF): Float {
        return 36.0f / sensorSize.width * focalLength
    }
}
