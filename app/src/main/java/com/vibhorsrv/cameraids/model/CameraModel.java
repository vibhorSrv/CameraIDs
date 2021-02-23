package com.vibhorsrv.cameraids.model;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Locale;

public class CameraModel {
    private final int id;
    private CameraType cameraType;
    private float zoomFactor;
    private Camera2ApiProperties camera2ApiProperties;
    private DerivedProperties derivedProperties;

    public CameraModel(int id) {
        this.id = id;
    }

    public float getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(float zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    public CameraType getCameraType() {
        return cameraType;
    }

    public void setCameraType(CameraType cameraType) {
        if (this.cameraType == null) {
            this.cameraType = cameraType;
        }
    }

    public int getId() {
        return id;
    }

    public Camera2ApiProperties getCamera2ApiProperties() {
        return camera2ApiProperties;
    }

    public void setCamera2ApiProperties(Camera2ApiProperties camera2ApiProperties) {
        this.camera2ApiProperties = camera2ApiProperties;
    }

    public DerivedProperties getDerivedProperties() {
        return derivedProperties;
    }

    public void setDerivedProperties(DerivedProperties derivedProperties) {
        this.derivedProperties = derivedProperties;
    }

    public boolean isTypeSet() {
        return cameraType != null;
    }

    @Override
    @NonNull
    public String toString() {
        return "\n" +
                "CameraID = [" + id + ']' + (!cameraType.equals(CameraType.LOGICAL) ? "  \u2605" : (!camera2ApiProperties.getPhysicalIds().isEmpty() ? " = " + camera2ApiProperties.getPhysicalIds().toString().replace(", ", "+") : "")) +
                "\n" +
                "Facing = " + derivedProperties.getFacing() +
                "\n" +
                (!cameraType.equals(CameraType.LOGICAL) ?
                        "Zoom = " + String.format(Locale.ROOT, "%.2fx", zoomFactor).replace(".00", "") +
                                "\n" : "") +
                "Type = " + cameraType +
                "\n" +
                "FocalLength = " + String.format(Locale.ROOT, "%.2fmm", camera2ApiProperties.getFocalLength()) +
                "\n" +
                "35mm eqv FocalLength = " + String.format(Locale.ROOT, "%.2fmm", derivedProperties.getMm35FocalLength()) +
                "\n" +
                "Aperture = " + camera2ApiProperties.getAperture() +
                "\n" +
                "SensorSize = " + camera2ApiProperties.getSensorSize().toString() +
                "\n" +
                "PixelArray = " + camera2ApiProperties.getPixelArraySize().toString() +
                "\n" +
                "PixelSize = " + String.format(Locale.ROOT, "%.2f", derivedProperties.getPixelSize()) + "µm" +
                "\n" +
                "AngleOfView(Diagonal) = " + Math.round(derivedProperties.getAngleOfView()) + "\u00b0" +
                "\n" +
                "AEModes = " + Arrays.toString(camera2ApiProperties.getAeModes()) +
                "\n" +
                "FlashSupported = " + camera2ApiProperties.isFlashSupported() +
                "\n" +
                "RAW_SENSOR sizes = " + Arrays.deepToString(camera2ApiProperties.getRawSensorSizes()) +
                "\n" +
                "SupportedHardwareLevel = " + camera2ApiProperties.getSupportedHardwareLevelString() +
                "\n";
    }
}
