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
        return """
     
     CameraID = [$id]${
            if (cameraType != CameraType.LOGICAL) "  \u2605" else if (camera2ApiProperties.physicalIds.isNotEmpty()
            ) " = " + camera2ApiProperties.physicalIds.toString().replace(", ", "+") else ""
        }
     Facing = ${derivedProperties.facing}

     """.trimIndent() +
                (if (cameraType != CameraType.LOGICAL) """
     Zoom = ${String.format(Locale.ROOT, "%.2fx", zoomFactor).replace(".00", "")}
     
     """.trimIndent() else "") +
                "Type = " + cameraType +
                "\n" +
                "FocalLength = " + String.format(
            Locale.ROOT,
            "%.2fmm",
            camera2ApiProperties.focalLength
        ) +
                "\n" +
                "35mm eqv FocalLength = " + String.format(
            Locale.ROOT,
            "%.2fmm",
            derivedProperties.mm35FocalLength
        ) +
                "\n" +
                "Aperture = " + camera2ApiProperties.aperture +
                "\n" +
                "SensorSize = " + camera2ApiProperties.sensorSize.toString() +
                "\n" +
                "PixelArray = " + camera2ApiProperties.pixelArraySize.toString() +
                "\n" +
                "PixelSize = " + String.format(
            Locale.ROOT,
            "%.2f",
            derivedProperties.pixelSize
        ) + "µm" +
                "\n" +
                "AngleOfView(Diagonal) = " + derivedProperties.angleOfView.roundToInt() + "\u00b0" +
                "\n" +
                "AEModes = " + camera2ApiProperties.aeModes.contentToString() +
                "\n" +
                "FlashSupported = " + camera2ApiProperties.isFlashSupported +
                "\n" +
                "RAW_SENSOR sizes = " + camera2ApiProperties.rawSensorSizes.contentDeepToString() +
                "\n" +
                "SupportedHardwareLevel = " + camera2ApiProperties.supportedHardwareLevelString +
                "\n"
    }
}
