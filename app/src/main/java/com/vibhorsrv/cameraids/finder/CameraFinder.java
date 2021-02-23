package com.vibhorsrv.cameraids.finder;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Log;
import android.util.Size;

import com.vibhorsrv.cameraids.api.CameraFinderAbstract;
import com.vibhorsrv.cameraids.model.Camera2ApiProperties;
import com.vibhorsrv.cameraids.model.CameraModel;
import com.vibhorsrv.cameraids.model.DerivedProperties;
import com.vibhorsrv.cameraids.util.CameraUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class CameraFinder extends CameraFinderAbstract<ArrayList<CameraModel>> {

    public CameraFinder(CameraManager cameraManager) {
        super(cameraManager);
        cameraModels = new ArrayList<>();
    }

    @Override
    public void createModels() {
        validCameraIds.forEach(id -> cameraModels.add(new CameraModel(Integer.parseInt(id))));
        cameraModels.forEach(cameraModel -> cameraModel.setCamera2ApiProperties(findProperties(cameraModel.getId(), getCameraCharacteristics(cameraModel.getId()))));
        cameraModels.forEach(cameraModel -> cameraModel.setDerivedProperties(deriveProperties(cameraModel.getId(), cameraModel.getCamera2ApiProperties())));
    }


    @Override
    public Camera2ApiProperties findProperties(int cameraId, CameraCharacteristics characteristics) {
        Camera2ApiProperties properties = new Camera2ApiProperties(cameraId);
        properties.setFacing(characteristics.get(CameraCharacteristics.LENS_FACING));
        properties.setFocalLength(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0]);
        properties.setAperture(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)[0]);
        properties.setAeModes(characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES));
        properties.setFlashSupported(characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE));
        properties.setRawSensorSizes(getRawSizes(characteristics, ImageFormat.RAW_SENSOR));
        properties.setSensorSize(characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE));
        properties.setPixelArraySize(characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE));
        properties.setSupportedHardwareLevel(characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL));
        properties.setSupportedHardwareLevelString(reflectionProvider.getResultFieldName(CameraMetadata.class, "INFO_SUPPORTED_HARDWARE_LEVEL_", properties.getSupportedHardwareLevel()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            properties.setPhysicalIds(characteristics.getPhysicalCameraIds());
        } else
            properties.setPhysicalIds(Collections.EMPTY_SET);
        return properties;
    }

    private Size[] getRawSizes(CameraCharacteristics cameraCharacteristics, int imageFormat) {
        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        return streamConfigurationMap.getOutputSizes(imageFormat);

    }

    @Override
    public DerivedProperties deriveProperties(int cameraId, Camera2ApiProperties properties) {
        DerivedProperties derivedProperties = new DerivedProperties(cameraId);
        derivedProperties.setFacing(reflectionProvider.getResultFieldName(CameraMetadata.class, "LENS_FACING_", properties.getFacing()));
        derivedProperties.setLogical(!properties.getPhysicalIds().isEmpty());
        derivedProperties.setAngleOfView(CameraUtil.calculateAngleOfView(properties.getFocalLength(), properties.getSensorSize(), properties.getPixelArraySize()));
        derivedProperties.setPixelSize(CameraUtil.calculatePixelSize(properties.getPixelArraySize().getWidth(), properties.getSensorSize().getWidth()));
        derivedProperties.setMm35FocalLength(CameraUtil.calculate35mmeqv(properties.getFocalLength(), properties.getSensorSize()));
        return derivedProperties;
    }
}
