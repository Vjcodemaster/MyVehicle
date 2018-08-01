package app_utility;

import java.util.ArrayList;
import java.util.HashSet;

public interface AsyncInterface {
    void onAsyncTaskComplete(String sMessage, int nCase, ArrayList<Integer> alID,
                             ArrayList<String> alModelID,
                             ArrayList<Integer> alModelIDNo,
                             ArrayList<String> alModelYear,
                             ArrayList<String> alName,
                             ArrayList<String> alLicensePlate,
                             HashSet<Integer> hsModelIDSingleValues);

    void onAsyncTaskCompleteGeneral(String sMessage, int nCase, int position, String sData);
}
