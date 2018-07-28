package com.autochip.myvehicle;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface OnFragmentInteractionListener {
    void onInteraction(String sMessage, int nCase, String sActivityName);

    void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>>
            lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID);
}
