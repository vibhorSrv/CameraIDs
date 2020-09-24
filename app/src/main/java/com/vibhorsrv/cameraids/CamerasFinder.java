package com.vibhorsrv.cameraids;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vibhor on 23/09/2020
 */
public class CamerasFinder {
    private static final String STRING_REPEAT = "(Repeat)";
    private static final String STRING_LOGICAL = "(Logical)";
    private static final String STRING_PROFILE = "(Profile)";
    private static final String STRING_FRONT_ID = "FRONT ID [";
    private static final String STRING_BACK_ID = "BACK ID [";
    private final Map<String, String> mPrintMap = new LinkedHashMap<>(); //stores usable data to be printed on screen
    private final Map<String, String> mCheckMap = new LinkedHashMap<>(); //used to check repeated camera properties
    private final CameraManager mCameraManager;
    private String mFileName;

    public CamerasFinder(CameraManager mCameraManager) {
        this.mCameraManager = mCameraManager;
    }

    private void scanAllCameras(CameraManager cameraManager) {
        for (int id = 0; id < 512; id++) {
            CameraCharacteristics cameraCharacteristics;
            try {
                cameraCharacteristics = cameraManager.getCameraCharacteristics(String.valueOf(id));
                float[] focalLength = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                float[] aperture = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                if (focalLength != null && aperture != null) {
                    int[] aeModes = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
                    boolean flashAvailable = cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);

                    //if this string is found to be repeated camera ID is marked as "(Repeat)"
                    String checkValue = "[Focal = " + focalLength[0] + "] [Aperture = " + aperture[0] + "] [AEMODES = " + Arrays.toString(aeModes) + "]";

                    Size[] rawSizes = getRawSizes(cameraCharacteristics);
                    String rawSizesAsString = "Not Supported";
                    if (rawSizes != null)
                        rawSizesAsString = Arrays.deepToString(rawSizes).replace(", ", " ");

                    String hwSupportLevel = getSupportedHWlevel(cameraCharacteristics);

                    String printValue = "\n\t\t\t" +
                            "[FocalLength = " + focalLength[0] + "] " + "[Aperture = " + aperture[0] + "]" +
                            "\n\t\t\t" +
                            "[AeModes = " + Arrays.toString(aeModes).replace(", ", ",") + "]" +
                            "\n\t\t\t" +
                            "[FlashAvailable = " + flashAvailable + "]" +
                            "\n\t\t\t" +
                            "[RAW_SENSOR sizes: " + rawSizesAsString + "]" +
                            "\n\t\t\t" +
                            "[INFO_SUPPORTED_HARDWARE_LEVEL: " + hwSupportLevel + "]" +
                            "\n";

                    String prefix = getPrefix(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING));
                    String prefix2 = "";

                    Set<String> physicalIDsSet = cameraCharacteristics.getPhysicalCameraIds();
                    if (!physicalIDsSet.isEmpty()) {
                        prefix = STRING_LOGICAL.concat(prefix); //if physical IDs are attached with a camera ID, it is marked as (Logical)
                        prefix2 = " == ID".concat(physicalIDsSet.toString().replace(", ", " + "));
                    } else {
                        if (mCheckMap.containsValue(checkValue)) {
                            prefix = STRING_REPEAT.concat(prefix);
                        }
                        if (rawSizes != null)
                            if (rawSizes.length > 1)
                                prefix = STRING_PROFILE.concat(prefix); //if more than one raw sizes are found, camera ID is marked as (Profile)
                    }
                    mCheckMap.put(String.valueOf(id), checkValue);
                    mPrintMap.put(prefix + id + "] " + prefix2, printValue);
                }

            } catch (IllegalArgumentException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getPrefix(Integer lensFacing) {
        String prefix = "ID [";
        if (lensFacing == 0)
            prefix = STRING_FRONT_ID;
        else if (lensFacing == 1)
            prefix = STRING_BACK_ID;
        return prefix;
    }

    private Size[] getRawSizes(CameraCharacteristics cameraCharacteristics) {
        StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        return streamConfigurationMap.getOutputSizes(ImageFormat.RAW_SENSOR);

    }

    private String getSupportedHWlevel(CameraCharacteristics cameraCharacteristics) {
        return hwLevelName(cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL));
    }

    private String hwLevelName(int level) {
        return level == 0 ? "LIMITED" : level == 1 ? "FULL" : level == 2 ? "LEGACY" : level == 3 ? "3" : level == 4 ? "EXTERNAL" : "";
    }

    /**
     * @return filename based on the Device's brand and model name
     */
    public String getFileName() {
        return mFileName;
    }

    private void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    /**
     * Scans all IDs and returns usable string of data
     *
     * @return the formatted displayable string of data
     */
    public String getResultString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.BRAND).append(", ").append(Build.MODEL).append(", ").append(Build.MANUFACTURER).append(", ").append(Build.DEVICE);
        setFileName("CameraIDs-".concat(sb.toString().replace(", ", "-")));
        sb.append("\n\n");
        sb.append("Android ").append(Build.VERSION.RELEASE).append(" - ").append(System.getProperty("os.version"));
        sb.append("\n");

        scanAllCameras(mCameraManager);
        try {
            sb.append("\n===============\n");
            sb.append("\nCamera IDs visible to Apps = ");
            sb.append(Arrays.toString(mCameraManager.getCameraIdList()));
            sb.append("\n\n===============\n");
            sb.append("All Cameras IDs = ").append(mCheckMap.keySet()).append("\n");
            sb.append("\n");
            sb.append(mPrintMap.toString().replace("{", "").replace("}", "").replace(", ", "\n"));
            sb.append("===============\n");

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

