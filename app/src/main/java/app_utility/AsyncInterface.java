package app_utility;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public interface AsyncInterface {
    void onAsyncTaskComplete(String sMessage, int nCase, ArrayList<Integer> alID,
                             ArrayList<String> alModelID,
                             ArrayList<Integer> alModelIDNo,
                             ArrayList<String> alModelYear,
                             ArrayList<String> alName,
                             ArrayList<String> alLicensePlate,
                             ArrayList<Bitmap> alDisplayPicture,
                             ArrayList<String> alEncodedDisplayPicture,
                             HashSet<Integer> hsModelIDSingleValues);

    void onAsyncTaskCompleteGeneral(String sMessage, int nCase, int position, String sData);

    void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>>
            lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID);
}
