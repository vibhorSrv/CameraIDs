package com.vibhorsrv.cameraids.model;

public class DerivedProperties {
    private final int id;
    private boolean isLogical;
    private String facing;
    private double angleOfView;
    private float mm35FocalLength;
    private float pixelSize;

    public float getPixelSize() {
        return pixelSize;
    }

    public void setPixelSize(float pixelSize) {
        this.pixelSize = pixelSize;
    }

    public DerivedProperties(int id) {
        this.id = id;
    }

    public boolean isLogical() {
        return isLogical;
    }

    public void setLogical(boolean logical) {
        isLogical = logical;
    }

    public int getId() {
        return id;
    }

    public String getFacing() {
        return facing;
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public double getAngleOfView() {
        return angleOfView;
    }

    public void setAngleOfView(double angleOfView) {
        this.angleOfView = angleOfView;
    }

    public float getMm35FocalLength() {
        return mm35FocalLength;
    }

    public void setMm35FocalLength(float mm35FocalLength) {
        this.mm35FocalLength = mm35FocalLength;
    }

}
