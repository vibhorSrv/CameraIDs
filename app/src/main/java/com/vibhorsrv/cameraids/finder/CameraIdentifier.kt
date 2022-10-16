package com.vibhorsrv.cameraids.finder

import android.hardware.camera2.CameraCharacteristics
import com.vibhorsrv.cameraids.api.CameraIdentifierAbstract
import com.vibhorsrv.cameraids.model.Camera2ApiProperties
import com.vibhorsrv.cameraids.model.CameraModel
import com.vibhorsrv.cameraids.model.CameraType
import java.util.TreeSet
import java.util.function.Consumer
import kotlin.Comparator
import kotlin.collections.ArrayList

class CameraIdentifier(cameraModels: ArrayList<CameraModel>) :
    CameraIdentifierAbstract<ArrayList<CameraModel>>(cameraModels) {
    private val frontCameraModels = ArrayList<CameraModel>()
    private val backCameraModels = ArrayList<CameraModel>()
    private val widerThanMain = TreeSet(SORT_BY_ANGLE_OF_VIEW)
    private val narrowerThanMain = ArrayList<CameraModel>()
    private val propertiesArrayList = ArrayList<Camera2ApiProperties>()
    override fun init() {
        cameraModels.forEach(Consumer {
            propertiesArrayList.add(it.camera2ApiProperties)
        })
        frontOrBack(cameraModels)
        identifyCamera(frontCameraModels)
        identifyCamera(backCameraModels)
    }

    private fun frontOrBack(cameraModels: Collection<CameraModel>) {
        for (model in cameraModels) {
            if (model.camera2ApiProperties.facing == CameraCharacteristics.LENS_FACING_BACK) {
                backCameraModels.add(model)
            }
            if (model.camera2ApiProperties.facing == CameraCharacteristics.LENS_FACING_FRONT) {
                frontCameraModels.add(model)
            }
        }
    }

    override fun identifyCamera(cameraModels: ArrayList<CameraModel>) {
        if (cameraModels.isNotEmpty()) {
            val main = cameraModels[0]
            main.cameraType = CameraType.MAIN
            main.zoomFactor = 1f
            cameraModels.removeAt(0)

            //Determine whether camera is logical
            cameraModels.forEach(Consumer { model: CameraModel ->
                if (model.derivedProperties.isLogical || getBit(6, model.id)) {
                    model.cameraType = CameraType.LOGICAL
                }
                propertiesArrayList.forEach(Consumer {
                    if (model.id != it.id && model.camera2ApiProperties == it) {
                        model.cameraType = CameraType.LOGICAL
                    }
                })
            })
            cameraModels.removeIf { it.isTypeSet }
            cameraModels.sortWith(SORT_BY_ANGLE_OF_VIEW)
            cameraModels.forEach(Consumer {
                val zoom =
                    it.derivedProperties.mm35FocalLength / main.derivedProperties.mm35FocalLength
                it.zoomFactor = zoom

                //Determine whether camera is Depth or Other
                it.cameraType = CameraType.OTHER
                if (!it.camera2ApiProperties.isFlashSupported) {
                    it.cameraType = CameraType.DEPTH
                } else {
                    if (it.derivedProperties.angleOfView > main.derivedProperties.angleOfView) {
                        widerThanMain.add(it)
                    } else {
                        narrowerThanMain.add(it)
                    }
                }
            })

            //Determine whether camera is Ultrawide or Macro
            widerThanMain.forEach(Consumer {
                it.cameraType = if (
                    it.derivedProperties.angleOfView ==
                    widerThanMain.last().derivedProperties.angleOfView
                ) {
                    CameraType.ULTRAWIDE
                } else {
                    CameraType.MACRO
                }
            })

            // Determine whether camera is Tele
            narrowerThanMain.forEach(Consumer { cameraModel: CameraModel ->
                cameraModel.cameraType = CameraType.TELE
            })
        }
    }

    private fun getBit(num: Int, `val`: Int): Boolean {
        return `val` shr num - 1 and 1 == 1
    }

    companion object {
        private val SORT_BY_ANGLE_OF_VIEW =
            Comparator.comparingDouble { cameraModel: CameraModel -> cameraModel.derivedProperties.angleOfView }
    }
}
