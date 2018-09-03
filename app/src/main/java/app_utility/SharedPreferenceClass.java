package app_utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class SharedPreferenceClass {

    private SharedPreferences sharedPreferences;
    private Context _context;

    private static final String APP_PREFERENCES = "MY_VEHICLE_PREFERENCES";

    private static final int PRIVATE_MODE = 0;

    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";

    private static final String IS_TRACEABLE = "IS_TRACEABLE";

    private static final String USER_PHONE_NO = "USER_PHONE_NO";

    //private static final String IS_PROFILE_SET = "IS_PROFILE_SET";

    private static final String USER_NAME = "USER_NAME";

    private static final String USER_TYPE = "USER_TYPE";

    private static final String USER_LIST = "USERS_LIST";

    private static final String USER_LIST_NAME = "USERS_LIST_NAME";

    private static final String ADMIN_PERMISSION_LIST = "ADMIN_PERMISSION_LIST";

    private static final String USER_AUTO_START_PERMISSION = "USER_AUTO_START_PERMISSION";

    private static final String IS_BRAND_FETCHED_FROM_ODOO = "IS_BRAND_FETCHED_FROM_ODOO";

    private static final String INSURANCE_DATA = "INSURANCE_DATA";

    private static final String EMISSION_DATA = "EMISSION_DATA";

    private static final String RCFC_DATA = "RCFC_DATA";

    private static final String SERVICE_DATA = "SERVICE_DATA";

    private static final String VEHICLE_INFO = "VEHICLE_INFO";

    private static final String EDIT_MODE = "EDIT_MODE";

    private static final String IS_FROM_NOTIFICATION = "FROM_NOTIFICATION";

    // Constructor
    public SharedPreferenceClass(Context context) {
        this._context = context;

        sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.apply();
    }

    public void setFetchedBrandsFromOdooFirstTime(boolean isFetched) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_BRAND_FETCHED_FROM_ODOO, isFetched);
        editor.apply();
    }

    public boolean getFetchedBrandsFromOdooFirstTime() {
        return sharedPreferences.getBoolean(IS_BRAND_FETCHED_FROM_ODOO, false);
    }

    public void setInsuranceData(String sInsurance) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(INSURANCE_DATA, sInsurance);
        editor.apply();
    }

    public String getInsuranceData() {
        return sharedPreferences.getString(INSURANCE_DATA, null);
    }

    public void setEmissionData(String sEmission) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(EMISSION_DATA, sEmission);
        editor.apply();
    }

    public String getEmissionData() {
        return sharedPreferences.getString(EMISSION_DATA, null);
    }

    public void setRcfcData(String sRcfc) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(RCFC_DATA, sRcfc);
        editor.apply();
    }

    public String getRcfcData() {
        return sharedPreferences.getString(RCFC_DATA, null);
    }

    public void setServiceData(String sService) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(SERVICE_DATA, sService);
        editor.apply();
    }

    public String getServiceData() {
        return sharedPreferences.getString(SERVICE_DATA, null);
    }

    public void setVehicleInfo(String sVehicleInfo) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(VEHICLE_INFO, sVehicleInfo);

        editor.apply();
    }

    public void setAllVehicleInfoToNull(String sVehicleInfo, String sInsurance, String sEmission) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putString(VEHICLE_INFO, sVehicleInfo);
        editor.putString(INSURANCE_DATA, sInsurance);
        editor.putString(EMISSION_DATA, sEmission);

        editor.apply();
    }

    public String getVehicleInfo() {
        return sharedPreferences.getString(VEHICLE_INFO, null);
    }


    public void setEditMode(boolean isEditMode) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(EDIT_MODE, isEditMode);
        editor.apply();
    }

    public boolean getEditModeStatus() {
        return sharedPreferences.getBoolean(EDIT_MODE, false);
    }

    public void setIsFromNotification(boolean isFromNotification) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_FROM_NOTIFICATION, isFromNotification);
        editor.apply();
    }

    public boolean getIsFromNotification() {
        return sharedPreferences.getBoolean(IS_FROM_NOTIFICATION, false);
    }

    public void setUserLogStatus(boolean isLoggedIn, String name, String number) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        editor.putString(USER_NAME, name);
        editor.putString(USER_PHONE_NO, number);
        editor.apply();
    }

    public boolean getUserLogStatus() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return sharedPreferences.getString(USER_NAME, "");
    }

    public String getUserID() {
        return sharedPreferences.getString(USER_PHONE_NO, "");
    }

    public void setIfUserIsTraceable(boolean isTraceable) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_TRACEABLE, isTraceable);
        editor.apply();
    }

    public void setUserType(int userType) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putInt(USER_TYPE, userType);
        editor.apply();
    }

    public int getUserType() {
        return sharedPreferences.getInt(USER_TYPE, 1);
    }

    public boolean getUserTraceableInfo() {
        return sharedPreferences.getBoolean(IS_TRACEABLE, false);
    }

    public void setUserList(ArrayList<String> alUsers, ArrayList<String> alNames, ArrayList<String> alAdminPermissionList) {
        Set<String> set = new LinkedHashSet<>(alUsers);
        Set<String> setName = new LinkedHashSet<>(alNames);

        StringBuilder sb = new StringBuilder(alAdminPermissionList.size());
        String sPermissionList;
        for (int i = 0; i < alAdminPermissionList.size(); i++) {
            sb.append(alAdminPermissionList.get(i));
            sb.append(",");
        }
        sPermissionList = sb.toString();

        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putStringSet(USER_LIST, set);
        editor.putStringSet(USER_LIST_NAME, setName);
        editor.putString(ADMIN_PERMISSION_LIST, sPermissionList);
        editor.apply();
    }

    public Set<String> getUserList() {
        return sharedPreferences.getStringSet(USER_LIST, null);
    }

    public Set<String> getNamesList() {
        return sharedPreferences.getStringSet(USER_LIST_NAME, null);
    }

    public ArrayList<String> getAdminPermissionList() {
        if (sharedPreferences.getString(ADMIN_PERMISSION_LIST, null) != null) {
            String[] s = Objects.requireNonNull(sharedPreferences.getString(ADMIN_PERMISSION_LIST, null)).split(",");
            return new ArrayList<>(Arrays.asList(s));
        }
        return null;
    }

    //only for xiaomi devices
    public void setUserAutoStartPermission(boolean isGranted) {
        SharedPreferences sharedPreferences = _context.getSharedPreferences(APP_PREFERENCES, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(USER_AUTO_START_PERMISSION, isGranted);
        editor.apply();
    }

    public boolean getUserAutoStartPermission() {
        return sharedPreferences.getBoolean(USER_AUTO_START_PERMISSION, false);
    }
}
