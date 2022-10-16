package com.vibhorsrv.cameraids.api

import android.hardware.camera2.CameraCharacteristics
import com.vibhorsrv.cameraids.model.CameraModel

/**
 * Created by Vibhor Srivastava
 */
interface CameraIDs {
    interface Finder<T : Iterable<CameraModel>> {
        fun init()
        fun getCameraCharacteristics(cameraId: Int): CameraCharacteristics?
        val cameraModels: T
        val apiCameraIdList: List<String>
        val allCameraIdList: List<String>
    }

    interface Identifier<T : Iterable<CameraModel>> {
        fun init()
        fun identifyCamera(cameraModels: T)
    }

    interface Saver {
        fun saveText(path: String, text: String)
    }
}
