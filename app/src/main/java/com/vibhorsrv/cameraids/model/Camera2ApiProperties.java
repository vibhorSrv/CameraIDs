package com.vibhorsrv.cameraids.model;

import android.util.Size;
import android.util.SizeF;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class Camera2ApiProperties {
    private final int id;
    private int facing;
    private float focalLength;
    private float aperture;
    private int[] aeModes;
    private Size[] rawSensorSizes;
    private SizeF sensorSize;
    private Size pixelArraySize;

    public Size getPixelArraySize() {
        return pixelArraySize;
    }

    public void setPixelArraySize(Size pixelArraySize) {
        this.pixelArraySize = pixelArraySize;
    }

    private boolean flashSupported;
    private int supportedHardwareLevel;
    private String supportedHardwareLevelString;
    private Set<String> physicalIds;
    public Camera2ApiProperties(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Camera2ApiProperties that = (Camera2ApiProperties) o;
        return
                facing == that.facing &&
                        Float.compare(that.focalLength, focalLength) == 0 &&
                        Float.compare(that.aperture, aperture) == 0 &&
                        flashSupported == that.flashSupported &&
                        Arrays.equals(aeModes, that.aeModes) &&
                        sensorSize.equals(that.sensorSize);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(facing, focalLength, aperture, sensorSize, flashSupported);
        result = 31 * result + Arrays.hashCode(aeModes);
        return result;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public float getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(float focalLength) {
        this.focalLength = focalLength;
    }

    public float getAperture() {
        return aperture;
    }

    public void setAperture(float aperture) {
        this.aperture = aperture;
    }

    public int[] getAeModes() {
        return aeModes;
    }

    public void setAeModes(int[] aeModes) {
        this.aeModes = aeModes;
    }

    public Size[] getRawSensorSizes() {
        return rawSensorSizes;
    }

    public void setRawSensorSizes(Size[] rawSensorSizes) {
        this.rawSensorSizes = rawSensorSizes;
    }

    public SizeF getSensorSize() {
        return sensorSize;
    }

    public void setSensorSize(SizeF sensorSize) {
        this.sensorSize = sensorSize;
    }

    public boolean isFlashSupported() {
        return flashSupported;
    }

    public void setFlashSupported(boolean flashSupported) {
        this.flashSupported = flashSupported;
    }

    public int getSupportedHardwareLevel() {
        return supportedHardwareLevel;
    }

    public void setSupportedHardwareLevel(int supportedHardwareLevel) {
        this.supportedHardwareLevel = supportedHardwareLevel;
    }

    public String getSupportedHardwareLevelString() {
        return supportedHardwareLevelString;
    }

    public void setSupportedHardwareLevelString(String supportedHardwareLevelString) {
        this.supportedHardwareLevelString = supportedHardwareLevelString;
    }

    public Set<String> getPhysicalIds() {
        return physicalIds;
    }

    public void setPhysicalIds(Set<String> physicalIds) {
        this.physicalIds = physicalIds;
    }

}
