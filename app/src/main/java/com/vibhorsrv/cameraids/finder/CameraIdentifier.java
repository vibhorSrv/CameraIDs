package com.vibhorsrv.cameraids.finder;

import android.hardware.camera2.CameraCharacteristics;

import androidx.annotation.NonNull;

import com.vibhorsrv.cameraids.api.CameraIdentifierAbstract;
import com.vibhorsrv.cameraids.model.Camera2ApiProperties;
import com.vibhorsrv.cameraids.model.CameraModel;
import com.vibhorsrv.cameraids.model.CameraType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class CameraIdentifier extends CameraIdentifierAbstract<ArrayList<CameraModel>> {
    private static final Comparator<CameraModel> SORT_BY_ANGLE_OF_VIEW = Comparator.comparingDouble(cameraModel -> cameraModel.getDerivedProperties().getAngleOfView());
    private static final Comparator<CameraModel> SORT_BY_35MM_FOCAL = Comparator.comparingDouble(cameraModel -> cameraModel.getDerivedProperties().getMm35FocalLength());
    private final ArrayList<CameraModel> frontCameraModels = new ArrayList<>();
    private final ArrayList<CameraModel> backCameraModels = new ArrayList<>();
    private final TreeSet<CameraModel> widerThanMain = new TreeSet<>(SORT_BY_ANGLE_OF_VIEW);
    private final ArrayList<CameraModel> narrowerThanMain = new ArrayList<>();
    private final ArrayList<Camera2ApiProperties> propertiesArrayList = new ArrayList<>();


    public CameraIdentifier(ArrayList<CameraModel> cameraModels) {
        super(cameraModels);
    }

    public void init() {
        cameraModels.forEach(cameraModel -> propertiesArrayList.add(cameraModel.getCamera2ApiProperties()));
        frontOrBack(cameraModels);
        identifyCamera(frontCameraModels);
        identifyCamera(backCameraModels);
    }

    private void frontOrBack(@NonNull Collection<CameraModel> cameraModels) {
        for (CameraModel model : cameraModels) {
            if (model.getCamera2ApiProperties().getFacing() == CameraCharacteristics.LENS_FACING_BACK) {
                backCameraModels.add(model);
            }
            if (model.getCamera2ApiProperties().getFacing() == CameraCharacteristics.LENS_FACING_FRONT) {
                frontCameraModels.add(model);
            }
        }
    }

    @Override
    public void identifyCamera(@NonNull ArrayList<CameraModel> cameraModels) {
        if (!cameraModels.isEmpty()) {
            CameraModel main = cameraModels.get(0);
            main.setCameraType(CameraType.MAIN);
            main.setZoomFactor(1f);
            cameraModels.remove(0);

            //Determine whether camera is logical
            cameraModels.forEach(model -> {
                if (model.getDerivedProperties().isLogical() || getBit(6, model.getId())) {
                    model.setCameraType(CameraType.LOGICAL);
                }
                propertiesArrayList.forEach(camera2ApiProperties -> {
                    if (model.getId() != camera2ApiProperties.getId() && model.getCamera2ApiProperties().equals(camera2ApiProperties)) {
                        model.setCameraType(CameraType.LOGICAL);
                    }
                });
            });
            cameraModels.removeIf(CameraModel::isTypeSet);

            cameraModels.sort(SORT_BY_ANGLE_OF_VIEW);

            cameraModels.forEach(model -> {
                float zoom = model.getDerivedProperties().getMm35FocalLength() / main.getDerivedProperties().getMm35FocalLength();
                model.setZoomFactor(zoom);

                //Determine whether camera is Depth or Other
                if (model.getCamera2ApiProperties().getAeModes() == null) {
                    model.setCameraType(CameraType.OTHER);
                }
                if (!model.getCamera2ApiProperties().isFlashSupported()) {
                    model.setCameraType(CameraType.DEPTH);
                } else {
                    if (model.getDerivedProperties().getAngleOfView() > main.getDerivedProperties().getAngleOfView()) {
                        widerThanMain.add(model);
                    } else {
                        narrowerThanMain.add(model);
                    }
                }
            });

            //Determine whether camera is Ultrawide or Macro
            widerThanMain.forEach(cameraModel -> {
                if (cameraModel.getDerivedProperties().getAngleOfView() == widerThanMain.last().getDerivedProperties().getAngleOfView()) {
                    cameraModel.setCameraType(CameraType.ULTRAWIDE);
                } else {
                    cameraModel.setCameraType(CameraType.MACRO);
                }
            });

            //Determine whether camera is Tele
            narrowerThanMain.forEach(cameraModel -> cameraModel.setCameraType(CameraType.TELE));
        }
    }

    private boolean getBit(int num, int val) {
        return ((val >> (num - 1)) & 1) == 1;
    }
}
