package com.vibhorsrv.cameraids.api

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.vibhorsrv.cameraids.model.Camera2ApiProperties
import com.vibhorsrv.cameraids.model.CameraModel
import com.vibhorsrv.cameraids.model.DerivedProperties
import com.vibhorsrv.cameraids.reflection.ReflectionProvider
import java.util.Arrays
import java.util.stream.Collectors

/**
 * Base class for camera finder mechanism
 *
 *
 * Created by Vibhor Srivastava
 *
 * @param <T> Iterable implementation for storing CameraModel objects
</T> */
abstract class CameraFinderAbstract<T : Iterable<CameraModel>>(
    private val cameraManager: CameraManager
) : CameraIDs.Finder<T> {
    protected val validCameraIds: MutableList<String> = ArrayList()
    protected val reflectionProvider = ReflectionProvider()
    override lateinit var cameraModels: T
        protected set

    override fun init() {
        scanCameras(cameraManager)
        createModels()
    }

    protected abstract fun createModels()
    protected abstract fun findProperties(
        cameraId: Int,
        characteristics: CameraCharacteristics
    ): Camera2ApiProperties

    protected abstract fun deriveProperties(
        cameraId: Int,
        camera2ApiProperties: Camera2ApiProperties
    ): DerivedProperties

    private fun scanCameras(cameraManager: CameraManager) {
        for (id in 0..511) {
            try {
                cameraManager.getCameraCharacteristics(id.toString())
                validCameraIds.add(id.toString())
            } catch (ignored: IllegalArgumentException) {
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getCameraCharacteristics(cameraId: Int): CameraCharacteristics? {
        try {
            return cameraManager.getCameraCharacteristics(cameraId.toString())
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return null
    }

    override val apiCameraIdList: List<String>
        get() {
            try {
                return Arrays.stream(cameraManager.cameraIdList).collect(Collectors.toList())
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
            return ArrayList()
        }
    override val allCameraIdList: List<String>
        get() = validCameraIds
}
