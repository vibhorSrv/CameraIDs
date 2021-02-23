package com.vibhorsrv.cameraids.api;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import com.vibhorsrv.cameraids.model.Camera2ApiProperties;
import com.vibhorsrv.cameraids.model.CameraModel;
import com.vibhorsrv.cameraids.model.DerivedProperties;
import com.vibhorsrv.cameraids.reflection.ReflectionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base class for camera finder mechanism
 * <p>
 * Created by Vibhor Srivastava
 *
 * @param <T> Iterable implementation for storing CameraModel objects
 */
public abstract class CameraFinderAbstract<T extends Iterable<? super CameraModel>> implements CameraIDs.Finder<T> {
    protected final List<String> validCameraIds = new ArrayList<>();
    protected final ReflectionProvider reflectionProvider = new ReflectionProvider();
    protected final CameraManager cameraManager;
    protected T cameraModels;

    public CameraFinderAbstract(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public void init() {
        scanCameras(cameraManager);
        createModels();
    }

    protected abstract void createModels();

    protected abstract Camera2ApiProperties findProperties(int cameraId, CameraCharacteristics characteristics);

    protected abstract DerivedProperties deriveProperties(int cameraId, Camera2ApiProperties camera2ApiProperties);

    private void scanCameras(CameraManager cameraManager) {
        if (cameraManager != null) {
            for (int id = 0; id < 512; id++) {
                try {
                    cameraManager.getCameraCharacteristics(String.valueOf(id));
                    validCameraIds.add(String.valueOf(id));
                } catch (IllegalArgumentException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public CameraCharacteristics getCameraCharacteristics(int cameraId) {
        try {
            if (cameraManager != null) {
                return cameraManager.getCameraCharacteristics(String.valueOf(cameraId));
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T getCameraModels() {
        return cameraModels;
    }


    @Override
    public List<String> getApiCameraIdList() {
        try {
            if (cameraManager != null) {
                return Arrays.stream(cameraManager.getCameraIdList()).collect(Collectors.toList());
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getAllCameraIdList() {
        return validCameraIds;
    }
}
