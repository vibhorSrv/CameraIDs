package com.vibhorsrv.cameraids.api;

import android.hardware.camera2.CameraCharacteristics;

import com.vibhorsrv.cameraids.model.CameraModel;

import java.util.List;

/**
 * Created by Vibhor Srivastava
 */
public interface CameraIDs {
    interface Finder<T extends Iterable<? super CameraModel>> {
        void init();

        CameraCharacteristics getCameraCharacteristics(int cameraId);

        T getCameraModels();

        List<String> getApiCameraIdList();

        List<String> getAllCameraIdList();
    }

    interface Identifier<T extends Iterable<? super CameraModel>> {
        void init();

        void identifyCamera(T cameraModels);
    }

    interface Saver {
        void saveText(String path, String text);
    }
}
