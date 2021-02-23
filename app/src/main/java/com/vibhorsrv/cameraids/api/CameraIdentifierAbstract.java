package com.vibhorsrv.cameraids.api;

import com.vibhorsrv.cameraids.model.CameraModel;

/**
 * Base class for camera identifier mechanism
 * <p>
 * Created by Vibhor Srivastava
 *
 * @param <T> Iterable implementation for storing CameraModel objects
 */
public abstract class CameraIdentifierAbstract<T extends Iterable<? super CameraModel>> implements CameraIDs.Identifier<T> {
    protected T cameraModels;

    public CameraIdentifierAbstract(T cameraModels) {
        this.cameraModels = cameraModels;
    }

}
