package com.vibhorsrv.cameraids.api

import com.vibhorsrv.cameraids.model.CameraModel

/**
 * Base class for camera identifier mechanism
 *
 *
 * Created by Vibhor Srivastava
 *
 * @param <T> Iterable implementation for storing CameraModel objects
</T> */
abstract class CameraIdentifierAbstract<T : Iterable<CameraModel>>(
    protected val cameraModels: T
) : CameraIDs.Identifier<T>
