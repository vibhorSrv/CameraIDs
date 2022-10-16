package com.vibhorsrv.cameraids.model

import android.util.Size
import android.util.SizeF
import java.util.Objects

class Camera2ApiProperties(val id: Int) {
    var facing = 0
    var focalLength = 0f
    var aperture = 0f
    lateinit var aeModes: IntArray
    lateinit var rawSensorSizes: Array<Size>
    lateinit var sensorSize: SizeF
    lateinit var pixelArraySize: Size
    var isFlashSupported = false
    var supportedHardwareLevel = 0
    lateinit var supportedHardwareLevelString: String
    lateinit var physicalIds: Set<String>

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Camera2ApiProperties
        return facing == that.facing && that.focalLength.compareTo(focalLength) == 0 && that.aperture.compareTo(
            aperture
        ) == 0 && isFlashSupported == that.isFlashSupported &&
                aeModes.contentEquals(that.aeModes) && sensorSize == that.sensorSize
    }

    override fun hashCode(): Int {
        var result = Objects.hash(facing, focalLength, aperture, sensorSize, isFlashSupported)
        result = 31 * result + aeModes.contentHashCode()
        return result
    }
}
