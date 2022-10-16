package com.vibhorsrv.cameraids.model

import java.util.Locale
import kotlin.math.roundToInt

class CameraModel(val id: Int) {
    var cameraType: CameraType? = null
    var zoomFactor = 0f
    lateinit var camera2ApiProperties: Camera2ApiProperties
    lateinit var derivedProperties: DerivedProperties

    val isTypeSet: Boolean
        get() = cameraType != null

    override fun toString(): String {
        return mutableMapOf<String, String>().apply {
            this["CameraID"] = "[$id]"
            if (cameraType != CameraType.LOGICAL) {
                this["CameraID"] += "  \u2605"
            } else if (camera2ApiProperties.physicalIds.isNotEmpty()) {
                this["CameraID"] +=
                    " = ${camera2ApiProperties.physicalIds.toString().replace(", ", "+")}"
            }

            this["Facing"] = "${derivedProperties.facing}"

            if (cameraType != CameraType.LOGICAL) {
                this["Zoom"] = String.format(Locale.ROOT, "%.2fx", zoomFactor)
                    .replace(".00", "")
            }

            this["Type"] = "$cameraType"
            this["FocalLength"] = String.format(
                Locale.ROOT, "%.2fmm", camera2ApiProperties.focalLength)
            this["35mm eqv FocalLength"] = String.format(
                Locale.ROOT, "%.2fmm", derivedProperties.mm35FocalLength)
            this["Aperture"] = "${camera2ApiProperties.aperture}"
            this["SensorSize"] = "${camera2ApiProperties.sensorSize}"
            this["PixelArray"] = "${camera2ApiProperties.pixelArraySize}"
            this["PixelSize"] = String.format(Locale.ROOT, "%.2fµm", derivedProperties.pixelSize)
            this["AngleOfView(Diagonal)"] = "${derivedProperties.angleOfView.roundToInt()}°"
            this["AEModes"] = camera2ApiProperties.aeModes.contentToString()
            this["FlashSupported"] = "${camera2ApiProperties.isFlashSupported}"
            this["RAW_SENSOR sizes"] = camera2ApiProperties.rawSensorSizes.contentDeepToString()
            this["SupportedHardwareLevel"] = camera2ApiProperties.supportedHardwareLevelString
        }.flatMap { (k, v) -> listOf("$k = $v") }.joinToString("\n")
    }
}
