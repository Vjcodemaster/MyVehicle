package app_utility;

/*
 * Created by Vijay on 05-06-2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.autochip.myvehicle.CircularProgressBar;
import com.autochip.myvehicle.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import static app_utility.StaticReferenceClass.DB_NAME;
import static app_utility.StaticReferenceClass.MODEL_EMISSION_FIELDS;
import static app_utility.StaticReferenceClass.MODEL_EMISSION_HISTORY;
import static app_utility.StaticReferenceClass.MODEL_INSURANCE_FIELDS;
import static app_utility.StaticReferenceClass.MODEL_INSURANCE_HISTORY;
import static app_utility.StaticReferenceClass.MODEL_OWNER_FIELDS;
import static app_utility.StaticReferenceClass.MODEL_OWNER_HISTORY;
import static app_utility.StaticReferenceClass.MODEL_SERVICE_FIELDS;
import static app_utility.StaticReferenceClass.MODEL_SERVICE_HISTORY;
import static app_utility.StaticReferenceClass.NETWORK_ERROR_CODE;
import static app_utility.StaticReferenceClass.PASSWORD;
import static app_utility.StaticReferenceClass.PORT_NO;
import static app_utility.StaticReferenceClass.SERVER_URL;
import static app_utility.StaticReferenceClass.USER_ID;
import static java.util.Arrays.asList;

public class MyVehicleAsyncTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private static int createUID = 113; //107; //
    private Activity aActivity;
    private String res = "";
    private int createdId = -1;
    private Integer[] iaOne2ManyID = new Integer[4];
    private Boolean isConnected = false;
    private String sMsgResult;
    private int type;
    private CircularProgressBar circularProgressBar;
    private Context context;
    private HashMap<String, Object> object = new HashMap<>();

    private String sBrandName, InsuranceData, EmissionData, sModelName, sRegNo, RCFCData, ServiceData;
    private int brandID, ModelID, sManufactureYear;

    private LinkedHashMap<String, ArrayList<String>> lHMFormatData;
    //use this for future delete, edit tasks
    private LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID;
    //private AsyncInterface asyncInterface;
    private HashMap mHMEditedList = new HashMap<>();

    private int vehicleID;
    private int deletedPosition;
    private int deletedID;

    private ArrayList<Integer> alID = new ArrayList<>();
    private ArrayList<String> alModelID = new ArrayList<>();
    private ArrayList<Integer> alModelIDNo = new ArrayList<>();
    private ArrayList<String> alModelYear = new ArrayList<>();
    private ArrayList<String> alName = new ArrayList<>();
    private ArrayList<String> alLicensePlate = new ArrayList<>();
    private ArrayList<Bitmap> alDisplayPicture = new ArrayList<>();
    private ArrayList<String> alEncodedDisplayPicture = new ArrayList<>();
    private HashSet<Integer> hsModelIDSingleValues = new HashSet<>();

    private String insuranceNo, insuranceVendor, insuranceStartDate, insuranceExpiryDate, insuranceRemainderDate;

    private String emissionNo, emissionVendor, emissionStartDate, emissionExpiryDate, emissionRemainderDate;
    private String customerName, address, mobile, dateOfOwnership;
    private String roNo, serviceType, mileage, date, nextServiceDate, remainderDate;

    private String base64Bitmap;
    private DatabaseHandler db;
    private ArrayList<String> alOne2ManyModelNames;
    private ArrayList<String> alModelNamesToFetch;

    //private ArrayList<String[]> alInsuranceHistory = new ArrayList<>();
    //private ArrayList<String[]> alEmissionHistory = new ArrayList<>();

    private int ERROR_CODE = 0;

    public MyVehicleAsyncTask(Activity aActivity) {
        this.aActivity = aActivity;
    }

    public MyVehicleAsyncTask(Context context) {
        this.context = context;
    }

    public MyVehicleAsyncTask(Activity aActivity, String sBrandName, int brandID, int ModelID, String InsuranceData, String EmissionData,
                              String RCFCData, String ServiceData, String sModelName, String sRegNo, int sManufactureYear, String base64Bitmap,
                              ArrayList<String> alOne2ManyModelNames) {
        this.aActivity = aActivity;
        this.sBrandName = sBrandName;
        this.brandID = brandID;
        this.ModelID = ModelID;
        this.InsuranceData = InsuranceData;
        this.EmissionData = EmissionData;
        this.RCFCData = RCFCData;
        this.ServiceData = ServiceData;
        this.sModelName = sModelName;
        this.sRegNo = sRegNo;
        this.sManufactureYear = sManufactureYear;
        this.base64Bitmap = base64Bitmap;
        this.alOne2ManyModelNames = alOne2ManyModelNames;
    }

    public MyVehicleAsyncTask(Activity aActivity, HashMap mHMEditedList, int vehicleID, DatabaseHandler db) {
        this.aActivity = aActivity;
        this.mHMEditedList = mHMEditedList;
        this.vehicleID = vehicleID;
        this.db = db;
    }

    //update task
    public MyVehicleAsyncTask(Activity aActivity, ArrayList<String> alModelNamesToFetch, DatabaseHandler db) {
        this.aActivity = aActivity;
        this.alModelNamesToFetch = alModelNamesToFetch;
        this.db = db;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setProgressBar();
    }

    @Override
    protected String doInBackground(String... params) {
        type = Integer.parseInt(params[0]);
        switch (type) {
            case 1:
                loginTask();
                break;
            case 2:
                updateTask();
                break;
            case 3:
                readTask();
                break;
            case 4:
                //snapRoadTask(params[1]);
                break;
            case 5:
                createTask();
                break;
            case 6:
                updateOne2Many(9);
                break;
            case 7:
                deletedID = Integer.valueOf(params[1]);
                delete(deletedID);
                deletedPosition = Integer.valueOf(params[2]); //this is the position of the data that should be deleted
                break;
            case 8:
                readBrandTask();
                break;
            case 9:
                readBrandModelTask();
                break;
            case 10:
                ArrayList<String[]> alModelNameFields = new ArrayList<>();
                //if (InsuranceData != null)
                alModelNameFields.add(MODEL_INSURANCE_FIELDS);
                //if (EmissionData != null)
                alModelNameFields.add(MODEL_EMISSION_FIELDS);
                //if (RCFCData != null)
                alModelNameFields.add(MODEL_OWNER_FIELDS);
                //if (ServiceData != null)
                alModelNameFields.add(MODEL_SERVICE_FIELDS);
                for (int i = 0; i < alModelNamesToFetch.size(); i++) {
                    readVehicleHistoryTask(alModelNamesToFetch.get(i), alModelNameFields.get(i));
                }
                //createOne2Many();
                break;
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        /*JSONArray jsonArray;
        JSONArray jsonArrayLegs;
        JSONObject jsonExtract;
        JSONObject jsonResponse;*/
        if (ERROR_CODE != 0) {
            switch (ERROR_CODE) {
                case NETWORK_ERROR_CODE:
                    unableToConnectServer(ERROR_CODE);
                    break;
            }
            ERROR_CODE = 0;
            return;
        }
        switch (type) {
            case 1:
                if (isConnected) {
                    /*Intent i = new Intent(aActivity, MapsActivity.class);
                    //i.putExtra("ConnData", cd);
                    SharedPreferenceClass sharedPreferenceClass = new SharedPreferenceClass(aActivity);
                    sharedPreferenceClass.setUserLogStatus(true);
                    aActivity.startActivity(i);
                    aActivity.finish();*/
                } else {
                    Toast.makeText(aActivity, sMsgResult, Toast.LENGTH_LONG).show();
                    sMsgResult = "";
                }
                break;
            case 2:
                String sLicensePlate = "";
                String sModelYear = "";
                String base64Image = "";
                if (mHMEditedList.containsKey("license_plate"))
                    sLicensePlate = mHMEditedList.get("license_plate").toString();
                if (mHMEditedList.containsKey("model_year"))
                    sModelYear = mHMEditedList.get("model_year").toString();
                if (mHMEditedList.containsKey("image_medium"))
                    base64Image = mHMEditedList.get("image_medium").toString();
                db.updateRowDataByVehicleID(new DataBaseHelper(sLicensePlate, base64Image, sModelYear), vehicleID);
                MainActivity.homeInterfaceListener.onHomeCalled("EDIT_CONDITION_SATISFIED", type, "", null);
                Log.e("Updating done", "success");
                break;
            case 3:
                MainActivity.asyncInterface.onAsyncTaskComplete("READ_DATA_FROM_SERVER", type, alID, alModelID, alModelIDNo, alModelYear,
                        alName, alLicensePlate, alDisplayPicture, alEncodedDisplayPicture, hsModelIDSingleValues);
                //asyncInterface.onResultReceived("UPDATE_LOCATION", type, dLatitude, dLongitude, 0.0, 0.0);
                break;
            case 4:
                break;
            case 5:
                ArrayList<String[]> alModelArray = new ArrayList<>();

                if (InsuranceData != null && !InsuranceData.equals("")) {
                    String[] saInsuranceData = new String[8];
                    saInsuranceData[0] = iaOne2ManyID[0].toString();
                    saInsuranceData[1] = InsuranceData.split(",")[0];
                    saInsuranceData[2] = "Autochip";
                    saInsuranceData[3] = InsuranceData.split(",")[2];
                    saInsuranceData[4] = InsuranceData.split(",")[3];
                    saInsuranceData[5] = InsuranceData.split(",")[4];
                    saInsuranceData[6] = String.valueOf(createdId);
                    saInsuranceData[7] = "194";
                    alModelArray.add(saInsuranceData);
                } else {
                    alModelArray.add(null);
                }
                //String sJoinedInsuranceInfo = TextUtils.join(",", saInsuranceData);
                //db.updateInsuranceInfoByVehicleID(new DataBaseHelper(sJoinedInsuranceInfo, Integer.valueOf(saInsuranceData[0])), Integer.valueOf(saInsuranceData[6]));
                if (EmissionData != null && !EmissionData.equals("")) {
                    String[] saEmissionData = new String[7];
                    saEmissionData[0] = iaOne2ManyID[1].toString();
                    saEmissionData[1] = EmissionData.split(",")[0];
                    saEmissionData[2] = EmissionData.split(",")[1];
                    saEmissionData[3] = EmissionData.split(",")[2];
                    saEmissionData[4] = EmissionData.split(",")[3];
                    saEmissionData[5] = EmissionData.split(",")[4];
                    saEmissionData[6] = String.valueOf(createdId);
                    alModelArray.add(saEmissionData);
                } else {
                    alModelArray.add(null);
                }

                if (RCFCData != null && !RCFCData.equals("")) {
                    String[] saRCFCData = new String[6];
                    saRCFCData[0] = iaOne2ManyID[2].toString();
                    saRCFCData[1] = RCFCData.split(",")[0];
                    saRCFCData[2] = RCFCData.split(",")[1];
                    saRCFCData[3] = RCFCData.split(",")[2];
                    saRCFCData[4] = RCFCData.split(",")[3];
                    saRCFCData[5] = String.valueOf(createdId);
                    alModelArray.add(saRCFCData);
                } else {
                    alModelArray.add(null);
                }

                if (ServiceData != null && !ServiceData.equals("")) {
                    String[] saServiceData = new String[8];
                    saServiceData[0] = iaOne2ManyID[3].toString();
                    saServiceData[1] = ServiceData.split(",")[0];
                    saServiceData[2] = ServiceData.split(",")[1];
                    saServiceData[3] = ServiceData.split(",")[2];
                    saServiceData[4] = ServiceData.split(",")[3];
                    saServiceData[5] = ServiceData.split(",")[4];
                    saServiceData[6] = ServiceData.split(",")[5];
                    saServiceData[7] = String.valueOf(createdId);
                    alModelArray.add(saServiceData);
                } else {
                    alModelArray.add(null);
                }
                //String sJoinedEmissionInfo = TextUtils.join(",", saEmissionData);
                //db.updateEmissionInfoByVehicleID(new DataBaseHelper(sJoinedEmissionInfo, Integer.valueOf(saEmissionData[0]), ""), Integer.valueOf(saEmissionData[6]));

                String saAddedData;
                saAddedData = String.valueOf(createdId) + "," + sBrandName + "," + brandID + "," + sModelName + "," + ModelID + "," + sRegNo + ","
                        + base64Bitmap + "," + sManufactureYear;
                //MainActivity.homeInterfaceListener.onHomeCalled("CREATE_CONDITION_SATISFIED", 10, this.getClass().getName(), null);
                //saAddedData = String.valueOf(createdId) + "," + sModelName + "," + sRegNo + "," + sManufactureYear;
                MainActivity.asyncInterface.onAsyncTaskCompleteGeneral("ADDED_NEW_DATA", type, type, saAddedData, alModelArray);
                Toast.makeText(aActivity, "Vehicle Registered", Toast.LENGTH_LONG).show();
                break;
            case 7:
                MainActivity.asyncInterface.onAsyncTaskCompleteGeneral("REMOVE_POSITION", type, deletedPosition, String.valueOf(deletedID), null);
                break;
            case 9:
                //RegisterVehicleFragment.mListener.onRegisterVehicleFragment("REGISTER_DATA", type, lHMFormatData, lHMBrandNameWithIDAndModelID);
                MainActivity.asyncInterface.onRegisterVehicleFragment("REGISTER_DATA", type, lHMFormatData, lHMBrandNameWithIDAndModelID);
                break;
            case 10:
                Toast.makeText(aActivity, "App is all set and ready to use", Toast.LENGTH_LONG).show();
                break;
        }

        if (circularProgressBar != null && circularProgressBar.isShowing()) {
            circularProgressBar.dismiss();
        }

    }

    private void loginTask() {
        //if (isConnected) {
        try {
            isConnected = OdooConnect.testConnection(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
            if (isConnected) {
                isConnected = true;
                //return true;
            } else {
                isConnected = false;
                sMsgResult = "Connection error";
            }
        } catch (Exception ex) {
            ERROR_CODE = NETWORK_ERROR_CODE;
            // Any other exception
            sMsgResult = "Error: " + ex;
        }
        // }
        //return isConnected;
    }

    /*private void updateTask() {
        String msgResult = "";
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);

            *//*String list = sC.getSelectedItem().toString();
            Boolean iscomp;
            if (list.equals("Individual")) {
                iscomp = false;
            } else {
                iscomp = true;
            }*//*

            final Boolean isComp = false;
            final String n = "Vijay";
            final String p = "";
            final String e = String.valueOf(dLatitude) + "," + String.valueOf(dLongitude);
            int id;

            *//*if (!list.isEmpty())
                id = Integer.parseInt(list.substring(4, 6));
            else {
                Toast.makeText(getApplicationContext(), "Empty List", Toast.LENGTH_LONG).show();
                return;
            }*//*
            id = 483;
            // Create record
            @SuppressWarnings("unchecked")
            *//*Integer idC = oc.create("fleet.vehicle", new HashMap() {{
                put("name", n);
                put("phone", p);
                put("is_company", isComp);
            }});*//*


                    Boolean idC = oc.write("res.partner", new Object[]{id}, new HashMap() {{
                put("name", n);
                put("phone", p);
                put("email", e);
                put("is_company", isComp);
            }});

            msgResult += "Id of customer updated: " + idC.toString();
            Log.e("Update result", msgResult);
        } catch (Exception ex) {
            //msgResult = "Error: " + ex;
            Log.e("Updating error", ex.toString());
        }
    }*/

    private void createTask() {

        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);


            object.put("model_month", 1);
            object.put("model_year", 2018);

            if (!TextUtils.isEmpty(InsuranceData)) {
                insuranceNo = InsuranceData.split(",")[0];
                insuranceVendor = InsuranceData.split(",")[1];
                insuranceStartDate = InsuranceData.split(",")[2];
                insuranceExpiryDate = InsuranceData.split(",")[3];
                insuranceRemainderDate = InsuranceData.split(",")[4];
            }
            if (!TextUtils.isEmpty(EmissionData)) {
                emissionNo = EmissionData.split(",")[0];
                emissionVendor = EmissionData.split(",")[1];
                emissionStartDate = EmissionData.split(",")[2];
                emissionExpiryDate = EmissionData.split(",")[3];
                emissionRemainderDate = EmissionData.split(",")[4];
            }
            if (!TextUtils.isEmpty(RCFCData)) {
                customerName = RCFCData.split(",")[0];
                address = RCFCData.split(",")[1];
                mobile = RCFCData.split(",")[2];
                dateOfOwnership = RCFCData.split(",")[3];
            }
            if (!TextUtils.isEmpty(ServiceData)) {
                roNo = ServiceData.split(",")[0];
                serviceType = ServiceData.split(",")[1];
                mileage = ServiceData.split(",")[2];
                date = ServiceData.split(",")[3];
                nextServiceDate = ServiceData.split(",")[4];
                remainderDate = ServiceData.split(",")[5];
            }
            //final int insuranceID = createOne2Many("insurance.history", 194);
            //int partner_id= Many2one.getMany2One(object, "model_id").getId();
            /*@SuppressWarnings("unchecked")
            Integer idC = oc.create("res.partner", new HashMap() {{
                put("name", "Vijay");
                put("phone", "1231231231");
            }});*/

            //this is the correct one
            /*@SuppressWarnings("unchecked")
            Integer idC = oc.create("fleet.vehicle", new HashMap() {{
                put("name", "Audi");
                put("mvariant_id", 5); //
            }});*/
            /*@SuppressWarnings("unchecked") final Integer idC = oc.create("web.service", new HashMap() {{
                put("name", "asdasdasd");
                put("value", 2); //
                put("partner_id", 9); //many2One field. here we are using pre defined value i,e setting a value of other relation model to partner_id
                //put("session_ids", value);
            }});
            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create("web.service.child", new HashMap() {{
                put("name", "TTTTTTT");
                put("mobile", "555555555");
                put("service_id", idC); //one to many
                //put("session_ids", value);
            }});*/
            //below 2 is what we were using in supreeth server
            /*@SuppressWarnings("unchecked") final Integer idC = oc.create("web.service", new HashMap() {{
                put("name", "Final Test");
                put("value", 29); //
                put("partner_id", 9); //many2One field. here we are using pre defined value i,e setting a value of other relation model to partner_id
                //put("session_ids", value);
            }});

            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create("web.service.child", new HashMap() {{
                put("name", "Autochip");
                put("mobile", "4103246464");
                put("service_id", idC); //one to many
                //put("session_ids", value);
            }});*/

            //working code 1-08-2018
            /*@SuppressWarnings("unchecked") final Integer idC = oc.create("fleet.vehicle", new HashMap() {{
                put("model_id", 110);
                put("license_plate", "KA 04 KA 1611");
                put("odometer", 15);
                put("model_year", 2020);
                put("model_month", 11);
            }});*/

            @SuppressWarnings("unchecked") final Integer idC = oc.create("fleet.vehicle", new HashMap() {{
                put("model_id", ModelID);
                put("license_plate", sRegNo);
                //put("odometer", 15);
                put("model_year", sManufactureYear);
                put("model_month", 10);
                put("image_medium", base64Bitmap);
                //put("insurance_ids", insuranceID);
            }});
            createdId = idC;
            //String[] saModelNames = {MODEL_INSURANCE_HISTORY, MODEL_EMISSION_HISTORY};

            createOne2Many(alOne2ManyModelNames, "insurance.history", idC);

        } catch (Exception e) {
            ERROR_CODE = NETWORK_ERROR_CODE;
            //Toast.makeText(context, "Unable to contact server, please try again later", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void createOne2Many(ArrayList<String> alOne2ManyModelNames, String Model, final int ID) {

        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
/*
            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create("web.service.child", new HashMap() {{
                put("name", "Autochip");
                put("mobile", "4103246464");
                put("service_id", ID); //one to many
            }});*/

            if (alOne2ManyModelNames.size() >= 1) {
                @SuppressWarnings("unchecked")
                Integer one2ManyInsurance = oc.create(alOne2ManyModelNames.get(0), new HashMap() {{
                    put("insurance_doc_no", insuranceNo);
                    put("vender_name", 194); //one to many  //vender id to fetch is 194
                    put("insurance_start", insuranceStartDate);
                    put("insurance_end", insuranceExpiryDate);
                    put("set_reminder", insuranceRemainderDate);
                    put("vehicle_id", ID);
                }});
                iaOne2ManyID[0] = one2ManyInsurance;

                /*String[] saInsuranceData = new String[8];
                saInsuranceData[0] = iaOne2ManyID[0].toString();
                saInsuranceData[1] = InsuranceData.split(",")[0];
                saInsuranceData[2] = "Autochip";
                saInsuranceData[3] = InsuranceData.split(",")[2];
                saInsuranceData[4] = InsuranceData.split(",")[3];
                saInsuranceData[5] = InsuranceData.split(",")[4];
                saInsuranceData[6] = String.valueOf(createdId);
                saInsuranceData[7] = "194";
                String sJoinedInsuranceInfo = TextUtils.join(",", saInsuranceData);
                db.updateInsuranceInfoByVehicleID(new DataBaseHelper(sJoinedInsuranceInfo, Integer.valueOf(saInsuranceData[0])), Integer.valueOf(saInsuranceData[6]));*/
            }


            if (alOne2ManyModelNames.size() >= 2) {
                @SuppressWarnings("unchecked")
                Integer one2ManyEmission = oc.create(alOne2ManyModelNames.get(1), new HashMap() {{
                    put("emission_doc_no", emissionNo);
                    put("agency_name", emissionVendor); //one to many  //vender id to fetch is 194
                    put("emission_start", emissionStartDate);
                    put("emision_end", emissionExpiryDate);
                    put("set_reminder", emissionRemainderDate);
                    put("vehicle_id", ID);
                }});
                iaOne2ManyID[1] = one2ManyEmission;

                /*String[] saEmissionData = new String[7];
                saEmissionData[0] = iaOne2ManyID[1].toString();
                saEmissionData[1] = EmissionData.split(",")[0];
                saEmissionData[2] = EmissionData.split(",")[1];
                saEmissionData[3] = EmissionData.split(",")[2];
                saEmissionData[4] = EmissionData.split(",")[3];
                saEmissionData[5] = EmissionData.split(",")[4];
                saEmissionData[6] = String.valueOf(createdId);
                String sJoinedEmissionInfo = TextUtils.join(",", saEmissionData);
                db.updateEmissionInfoByVehicleID(new DataBaseHelper(sJoinedEmissionInfo, Integer.valueOf(saEmissionData[0]), ""), Integer.valueOf(saEmissionData[6]));*/
            }

            if (alOne2ManyModelNames.size() >= 3) {
                @SuppressWarnings("unchecked")
                Integer one2ManyRCFC = oc.create(alOne2ManyModelNames.get(2), new HashMap() {{
                    put("custmer_name", 84); //customerName
                    put("address", address); //one to many  //vender id to fetch is 194
                    put("mobile", mobile);
                    put("date_of_ownership", dateOfOwnership);
                    //put("set_reminder", emissionRemainderDate);
                    put("vehicle_id", ID);
                }});
                iaOne2ManyID[2] = one2ManyRCFC;
            }

            if (alOne2ManyModelNames.size() >= 4) {
                @SuppressWarnings("unchecked")
                Integer one2ManyService = oc.create(alOne2ManyModelNames.get(3), new HashMap() {{
                    put("order", 141); //roNo
                    put("servicetype", serviceType); //one to many  //vender id to fetch is 194
                    put("mileage", mileage);
                    put("date", date);
                    put("next_service_due", nextServiceDate);
                    put("set_reminder", remainderDate);
                    put("vehicle_id", ID);
                }});
                iaOne2ManyID[3] = one2ManyService;
            }
        } catch (Exception e) {
            ERROR_CODE = NETWORK_ERROR_CODE;
            e.printStackTrace();
        }
        //return one2Many;
    }

    private void readBrandTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("fleet.vehicle.model.brand", new Object[]{
                new Object[]{new Object[]{"create_uid", "!=", createUID}}}, "name", "id");
        alID = new ArrayList<>();
        alName = new ArrayList<>();
        for (int i = 0; i < data.size(); ++i) {
            alID.add(Integer.valueOf(data.get(i).get("id").toString()));
            alName.add(String.valueOf(data.get(i).get("name").toString()));
        }

        readBrandModelTask();
        /*String sEmail = data.get(0).get("email").toString();
        String[] sLatLng = sEmail.split(",");
        dLatitude = Double.valueOf(sLatLng[0]);
        dLongitude = Double.valueOf(sLatLng[1]);*/
    }

    private void readBrandModelTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        /*
        below line is commented and Objects of conditions has been added so that we can differentiate between one condition with another.
        and also improves readability of code which can be easily read. we are adding 2 conditions to get the data
        1. to make sure brand_id is  not false. (reject brands which do not have data)
        2. show and update brands and models only with vehicle types, not with spare parts or other materials.
         */
        Object[] conditions = new Object[2];
        conditions[0] = new Object[]{"brand_id", "!=", false};
        conditions[1] = new Object[]{"catalog_type", "=", "Vehicle"};
        List<HashMap<String, Object>> data = oc.search_read_brands("product.template", new Object[]{conditions}, "name", "brand_id");
        /*List<HashMap<String, Object>> data = oc.search_read_brands("product.template", new Object[]{
                new Object[]{new Object[]{"brand_id", "!=", false}}}, "name", "brand_id");*/
        ArrayList<Integer> alBrandID = new ArrayList<>();
        ArrayList<String> alBrandName = new ArrayList<>();
        ArrayList<String> alModelName = new ArrayList<>();
        ArrayList<Integer> alModelID = new ArrayList<>();
        LinkedHashSet<Integer> lHSBrandIDSingleValues = new LinkedHashSet<>();
        LinkedHashSet<String> lHSBrandNameSingleValues = new LinkedHashSet<>();
        LinkedHashMap<String, Integer> lHMBrandNameWithID = new LinkedHashMap<>();
        //use this for future delete, edit tasks
        lHMBrandNameWithIDAndModelID = new LinkedHashMap<>();

        //this contains BrandName, <ModelID, list of modelNames>
        lHMFormatData = new LinkedHashMap<>();

        int nCount = 0;
        //extract brandID and brandName and then add all the data to alBrandID, alModelName, alModelID normally to perform mapping in next loop
        for (int i = 0; i < data.size(); ++i) {
            int modelID = Integer.valueOf(data.get(i).get("id").toString());
            int brandIDNo = Integer.valueOf(data.get(i).get("brand_id_no").toString());
            String brandName = data.get(i).get("brand_id").toString();
            String modelName = data.get(i).get("name").toString();
            lHSBrandNameSingleValues.add(brandName);

            if (lHSBrandNameSingleValues.size() != nCount) {
                LinkedHashMap<Integer, ArrayList<Integer>> lHMAllIDS = new LinkedHashMap<>();
                lHMAllIDS.put(brandIDNo, null);

                lHSBrandIDSingleValues.add(brandIDNo);
                lHMBrandNameWithID.put(brandName, brandIDNo);
                lHMBrandNameWithIDAndModelID.put(brandName, lHMAllIDS);
                lHMFormatData.put(brandName, null);
            }

            alBrandID.add(brandIDNo);
            alBrandName.add(brandName);
            alModelName.add(modelName);
            alModelID.add(modelID);
            nCount = lHSBrandNameSingleValues.size();
        }

        //ArrayList<String> alBrandNameExtraction = new ArrayList<>(lHMFormatData.keySet());
        //organise data by multiple queries and add list of data to particular brandName
        for (int j = 0; j < alBrandID.size(); j++) {
            if (lHSBrandIDSingleValues.contains(alBrandID.get(j))) {
                LinkedHashMap<Integer, ArrayList<Integer>> lHMAllIDS;
                ArrayList<Integer> alModelIDTmp;
                //LinkedHashMap<Integer, ArrayList<String>> lHMListOfModelName = new LinkedHashMap<>();
                String sBrandNameTmp = alBrandName.get(j);
                String sModelNameTmp = alModelName.get(j);
                ArrayList<String> alTmp;
                if (lHMFormatData.get(sBrandNameTmp) == null) {
                    alTmp = new ArrayList<>();
                    lHMAllIDS = new LinkedHashMap<>();
                    alModelIDTmp = new ArrayList<>();
                } else {
                    alTmp = new ArrayList<>(lHMFormatData.get(sBrandNameTmp));
                    //lHMAllIDS = new LinkedHashMap<>();
                    lHMAllIDS = new LinkedHashMap<>(lHMBrandNameWithIDAndModelID.get(sBrandNameTmp));
                    alModelIDTmp = new ArrayList<>(lHMAllIDS.get(lHMBrandNameWithID.get(sBrandNameTmp)));
                }
                int id = lHMBrandNameWithID.get(sBrandNameTmp);
                alTmp.add(sModelNameTmp);
                alModelIDTmp.add(alModelID.get(j));
                lHMAllIDS.put(id, alModelIDTmp);
                lHMBrandNameWithIDAndModelID.put(sBrandNameTmp, lHMAllIDS);
                lHMFormatData.put(sBrandNameTmp, alTmp);
            }
        }
    }

    private void readVehicleHistoryTask(String sModelName, String[] saFields) {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read(sModelName, new Object[]{
                new Object[]{new Object[]{"create_uid", "=", createUID}}}, saFields);
        /*{"id", "insurance_doc_no", "vender_name", "insurance_start",
                "insurance_end", "set_reminder", "vehicle_id"}*/
        for (int i = 0; i < data.size(); i++) {
            switch (sModelName) {
                case MODEL_INSURANCE_HISTORY:
                    String[] saInsuranceData = new String[8];
                    saInsuranceData[0] = data.get(i).get("id").toString();
                    saInsuranceData[1] = data.get(i).get("insurance_doc_no").toString();
                    saInsuranceData[2] = data.get(i).get("vender_name").toString();
                    saInsuranceData[3] = data.get(i).get("insurance_start").toString();
                    saInsuranceData[4] = data.get(i).get("insurance_end").toString();
                    saInsuranceData[5] = data.get(i).get("set_reminder").toString();

                    if (data.get(i).containsKey("vehicle_id_no")) {
                        saInsuranceData[6] = data.get(i).get("vehicle_id_no").toString();
                    } else if (!data.get(i).get("vehicle_id").equals(false)) {
                        HashMap<String, Object> listFields = new HashMap<>();
                        listFields.put("vehicle_id", data.get(i).get("vehicle_id"));
                        List fRelation = asList((Object[]) listFields.get("vehicle_id"));
                        Object f0 = fRelation.get(0); //this will get id of brands
                        saInsuranceData[6] = f0.toString();
                    } else {
                        saInsuranceData[6] = "";
                    }

                    //slightly modified for tab because this is acting weird with yureka and lenovo tablet
                    if (!saInsuranceData[6].equals("") && data.get(i).containsKey("vender_name_no")) {
                        saInsuranceData[7] = data.get(i).get("vender_name_no").toString();
                        //alInsuranceHistory.add(saInsuranceData);
                        String sJoinedInsuranceInfo = TextUtils.join(",", saInsuranceData);
                        db.updateInsuranceInfoByVehicleID(new DataBaseHelper(sJoinedInsuranceInfo, Integer.valueOf(saInsuranceData[0])), Integer.valueOf(saInsuranceData[6]));
                    } else {
                        if (!saInsuranceData[6].equals("")) {
                            HashMap<String, Object> listFields = new HashMap<>();
                            listFields.put("vender_name", data.get(i).get("vender_name"));
                            List fRelation = asList((Object[]) listFields.get("vender_name"));
                            Object f0 = fRelation.get(0); //this will get id of brands
                            Object f1 = fRelation.get(1);
                            saInsuranceData[2] = f1.toString();
                            saInsuranceData[7] = f0.toString();
                            String sJoinedInsuranceInfo = TextUtils.join(",", saInsuranceData);
                            db.updateInsuranceInfoByVehicleID(new DataBaseHelper(sJoinedInsuranceInfo, Integer.valueOf(saInsuranceData[0])), Integer.valueOf(saInsuranceData[6]));
                        }
                    }
                    break;
                case MODEL_EMISSION_HISTORY:
                    String[] saEmissionData = new String[7];
                    saEmissionData[0] = data.get(i).get("id").toString();
                    saEmissionData[1] = data.get(i).get("emission_doc_no").toString();
                    saEmissionData[2] = data.get(i).get("agency_name").toString();
                    saEmissionData[3] = data.get(i).get("emission_start").toString();
                    saEmissionData[4] = data.get(i).get("emision_end").toString();
                    saEmissionData[5] = data.get(i).get("set_reminder").toString();

                    if (data.get(i).containsKey("vehicle_id_no")) {
                        saEmissionData[6] = data.get(i).get("vehicle_id_no").toString();
                    } else {
                        saEmissionData[6] = "";
                    }
                    if (!saEmissionData[6].equals("")) {
                        //alEmissionHistory.add(saEmissionData);
                        String sJoinedEmissionInfo = TextUtils.join(",", saEmissionData);
                        db.updateEmissionInfoByVehicleID(new DataBaseHelper(sJoinedEmissionInfo, Integer.valueOf(saEmissionData[0]), ""), Integer.valueOf(saEmissionData[6]));
                    }
                    break;
                case MODEL_OWNER_HISTORY:
                    if (data.get(i).get("id").toString() != null && !data.get(i).get("vehicle_id").toString().equals("false") &&
                            !data.get(i).get("custmer_name").toString().equals("false")) {
                        String[] saRCFCData = new String[7];

                        saRCFCData[0] = data.get(i).get("id").toString();
                        saRCFCData[1] = data.get(i).get("custmer_name").toString();
                        //edit here to get customer_name_no
                        saRCFCData[2] = data.get(i).get("address").toString();
                        saRCFCData[3] = data.get(i).get("mobile").toString();
                        saRCFCData[4] = data.get(i).get("date_of_ownership").toString();
                        //saRCFCData[5] = data.get(i).get("vehicle_id_no").toString();
                        HashMap<String, Object> listFields = new HashMap<>();
                        listFields.put("vehicle_id", data.get(i).get("vehicle_id"));
                        List fRelation = asList((Object[]) listFields.get("vehicle_id"));
                        Object f0 = fRelation.get(0); //this will get id of brands
                        saRCFCData[6] = f0.toString();
                        //saRCFCData[6] = data.get(i).get("vehicle_id").toString();
                        String sJoinedOwnerInfo = TextUtils.join(",", saRCFCData);
                        db.updateRCFCInfoByVehicleID(new DataBaseHelper(sJoinedOwnerInfo, Integer.valueOf(saRCFCData[0]), 3), Integer.valueOf(saRCFCData[6]));
                    }
                    break;
                case MODEL_SERVICE_HISTORY:
                    if (data.get(i).get("id").toString() != null && !data.get(i).get("vehicle_id").toString().equals("false")) {
                        String[] saServiceData = new String[8];

                        saServiceData[0] = data.get(i).get("id").toString();
                        saServiceData[1] = data.get(i).get("order").toString();
                        saServiceData[2] = data.get(i).get("servicetype").toString();
                        saServiceData[3] = data.get(i).get("mileage").toString();
                        saServiceData[4] = data.get(i).get("date").toString();
                        saServiceData[5] = data.get(i).get("next_serv_due").toString();
                        saServiceData[6] = data.get(i).get("set_reminder").toString();
                        //saServiceData[7] = data.get(i).get("vehicle_id_no").toString();
                        if (data.get(i).containsKey("vehicle_id_no")) {
                            saServiceData[7] = data.get(i).get("vehicle_id_no").toString();
                        } else {
                            HashMap<String, Object> listFields = new HashMap<>();
                            listFields.put("vehicle_id", data.get(i).get("vehicle_id"));
                            List fRelation = asList((Object[]) listFields.get("vehicle_id"));
                            Object f0 = fRelation.get(0); //this will get id of brands
                            saServiceData[7] = f0.toString();
                        }
                        //saServiceData[8] = data.get(i).get("vehicle_id").toString();

                        String sJoinedServiceInfo = TextUtils.join(",", saServiceData);
                        db.updateServiceInfoByVehicleID(new DataBaseHelper(sJoinedServiceInfo, Integer.valueOf(saServiceData[0]), 4), Integer.valueOf(saServiceData[7]));
                    }
            }
        }
    }

    /*
    int id = get id of the one2many from the main model which it is connected to.
    here we are editing one of the data of web.service.child
     */
    private void updateOne2Many(int id) {
        String msgResult = "";
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);

            Boolean idC = oc.write("web.service.child", new Object[]{id}, new HashMap() {{
                //put("name", n);
                //put("phone", p);
                //put("email", e);
                put("name", "Supreeth");
                //put("mobile", "9847794944");
                //put("name", "product.template");
                //put("model_id","Audi A3");
            }});

            msgResult += "Id of customer updated: " + idC.toString();
            Log.e("Update result", msgResult);
        } catch (Exception ex) {
            //msgResult = "Error: " + ex;
            ERROR_CODE = NETWORK_ERROR_CODE;
            Log.e("Updating error", ex.toString());
        }
    }

    private void updateTask() {
        String msgResult = "";
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);

            /*String list = sC.getSelectedItem().toString();
            Boolean iscomp;
            if (list.equals("Individual")) {
                iscomp = false;
            } else {
                iscomp = true;
            }*/

            final Boolean isComp = false;
            //final String n = "Vijay";
            final String p = "";
            //final String e = String.valueOf(dLatitude) + "," + String.valueOf(dLongitude);
            //int id;

            //id = 104;
            // Create record
            @SuppressWarnings("unchecked")
            /*Integer idC = oc.create("fleet.vehicle", new HashMap() {{
                put("name", n);
                put("phone", p);
                put("is_company", isComp);
            }});*/


                    /*Boolean idC = oc.write("fleet.vehicle", new Object[]{id}, new HashMap() {{
                //put("name", n);
                //put("phone", p);
                //put("email", e);
                put("initial_reg_no", "KA 50 YU 5110");
                //put("name", "product.template");
                //put("model_id","Audi A3");
            }});*/

                    Boolean idC = oc.write("fleet.vehicle", new Object[]{vehicleID}, mHMEditedList);

            msgResult += "Id of customer updated: " + idC.toString();
            Log.e("Update result", msgResult);
        } catch (Exception ex) {
            //msgResult = "Error: " + ex;
            ERROR_CODE = NETWORK_ERROR_CODE;
            Log.e("Updating error", ex.toString());
        }
    }

    /*
    finds all the data created by this user and returns required fields, which searches for create_uid = 107 condition
     */
    private void readTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("fleet.vehicle", new Object[]{
                new Object[]{new Object[]{"create_uid", "=", createUID}}}, "id", "image_medium", "model_id", "model_year", "name", "license_plate");


        for (int i = 0; i < data.size(); ++i) {
            alID.add(Integer.valueOf(data.get(i).get("id").toString()));
            alModelID.add(String.valueOf(data.get(i).get("model_id").toString()));
            alModelIDNo.add(Integer.valueOf(data.get(i).get("model_id_no").toString()));
            alModelYear.add(String.valueOf(data.get(i).get("model_year").toString()));
            alName.add(String.valueOf(data.get(i).get("name").toString()));
            alLicensePlate.add(String.valueOf(data.get(i).get("license_plate").toString()));
            String encodedBitmap = data.get(i).get("image_medium").toString();

            if (!encodedBitmap.equalsIgnoreCase("false")) {
                byte[] decodedString = Base64.decode(encodedBitmap, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                alDisplayPicture.add(decodedByte);
                alEncodedDisplayPicture.add(encodedBitmap);
            } else {
                alDisplayPicture.add(null);
                alEncodedDisplayPicture.add(null);
            }
        }
        hsModelIDSingleValues.addAll(alModelIDNo);
        /*String sEmail = data.get(0).get("email").toString();
        String[] sLatLng = sEmail.split(",");
        dLatitude = Double.valueOf(sLatLng[0]);
        dLongitude = Double.valueOf(sLatLng[1]);*/
    }

    //read according to the condition customer = true in res.users
    /*private void readTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("res.users", new Object[]{
                new Object[]{new Object[]{"customer", "=", true}}}, "name", "email");

        for (int i = 0; i < data.size(); ++i) {
            if (data.get(i).get("id").toString().length() > 1) {
                //listD.add("Id: " + data.get(i).get("id").toString() + " - " + data.get(i).get("name").toString());
            } else {
                //listD.add("Id: 0" + data.get(i).get("id").toString() + " - " + data.get(i).get("name").toString());
            }
        }
        String sEmail = data.get(0).get("email").toString();
        String[] sLatLng = sEmail.split(",");
        dLatitude = Double.valueOf(sLatLng[0]);
        dLongitude = Double.valueOf(sLatLng[1]);
    }*/

    /*private void readTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("fleet.vehicle", new Object[]{
                new Object[]{new Object[]{"initial_reg_no", "=", "KA 50 YU 5110"}}}, "model_id");
                //new Object[]{new Object[]{"licence_plate", "=", "KA04JJ5555"}}}, "model_id");

                *//*for (int i = 0; i < data.size(); ++i) {
                    if (data.get(i).get("id").toString().length()>1) {
                        listD.add("Id: " + data.get(i).get("id").toString() + " - " + data.get(i).get("name").toString());
                    }else{
                        listD.add("Id: 0" + data.get(i).get("id").toString() + " - " + data.get(i).get("name").toString());
                    }
                }*//*
        //String driver_id = data.get(0).get("driver_id").toString();
        String d = data.get(0).toString();
        String regNo = data.get(0).get("initial_reg_no").toString();
        //dLatitude = Double.valueOf(sLatLng[0]);
        //dLongitude = Double.valueOf(sLatLng[1]);
    }*/

    /*private String snapRoadTask(String uri) {
        HttpsURLConnection urlConnection;
        String result;
        Reader rd = null;
        StringBuffer sb = null;
//http://maps.google.com/maps/api/directions/json?origin=52.0,0&destination=52.0,0&sensor=true
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000 *//* milliseconds *//*);
            con.setConnectTimeout(15000 *//* milliseconds *//*);
            con.connect();
            if (con.getResponseCode() == 200) {

                rd = new InputStreamReader(con.getInputStream());
                sb = new StringBuffer();
                final char[] buf = new char[1024];
                int read;
                while ((read = rd.read(buf)) > 0) {
                    sb.append(buf, 0, read);
                }
                //res = sb.toString();
                Log.v(TAG, sb.toString());
            }
            con.disconnect();
        } catch (Exception e) {
            Log.e("foo", "bar", e);
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                    res = sb.toString();
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
        return res;
    }*/

    private void delete(int id) {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        //Boolean idC = oc.unlink("web.service.child", new Object[]{id});
        Boolean idC = oc.unlink("fleet.vehicle", new Object[]{id});
    }

    /*private List<HashMap<String, Object>> search_read(String model, final Integer offset,
                                                      final Integer limit, Object[] conditions,
                                                      final Object[] field) {
        List<HashMap<String, Object>> result = null;
        try {
            URL mUrl;
            mUrl = new URL(String.format("http://%s:%s/xmlrpc/2/object", SERVER_URL, PORT_NO));
            XMLRPCClient client = new XMLRPCClient(mUrl);

            Object[] parameters = {DB_NAME, USER_ID, PASSWORD,
                    model, "search_read", conditions, new HashMap() {{
                put("fields", field);
                put("limit", limit);
                put("offset", offset);
            }}};

            Object[] record = (Object[]) client.call("execute_kw", parameters);
            result = new ArrayList<>(record.length);

            for (Object oField : record) {

                HashMap<String, Object> listFields = (HashMap<String, Object>) oField;
                Set<String> keys = listFields.keySet();

                Object[] param = {DB_NAME, USER_ID, PASSWORD,
                        model, "fields_get", new Object[]{keys}, new HashMap() {{
                    put("attributes", new Object[]{"relation", "type"});
                }}};
                Map<String, Map<String, Object>> attrRelation =
                        (Map<String, Map<String, Object>>) client.call("execute_kw", param);

                for (String key : keys) {

                    if (attrRelation.get(key).containsValue("many2one")) {
                        List fRelation = asList((Object[]) listFields.get(key));
                        Object f = (Object) fRelation.get(1); // 1 => name
                        listFields.put(key, f);

                    } else if (attrRelation.get(key).containsValue("many2many") ||
                            attrRelation.get(key).containsValue("one2many")) {
                        List fRelation = asList((Object[]) listFields.get(key));

                        String modelR = attrRelation.get(key).get("relation").toString();
                        final Object[] fieldR = {"name"};

                        Object[] parame = {DB_NAME, USER_ID, PASSWORD,
                                modelR, "read", new Object[]{fRelation}, new HashMap() {{
                            put("fields", fieldR);
                        }}};
                        Object[] recordd = (Object[]) client.call("execute_kw", parame);

                        *//*
     * You can change the string format of this result like you prefer.
     *//*
                        String extra = "";
                        for (Object r : recordd){
                            extra += r;
                        }
                        Object fResult = (Object) extra;
                        listFields.put(key, fResult);
                    }
                }
                result.add((HashMap<String, Object>) oField);
            }
        } catch (XMLRPCException e) {
            Log.d("CONNECTOR_NAME", e.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }*/

    private void setProgressBar() {
        if (aActivity == null)
            circularProgressBar = new CircularProgressBar(context);
        else
            circularProgressBar = new CircularProgressBar(aActivity);
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }

    private void unableToConnectServer(int errorCode) {
        MainActivity.asyncInterface.onAsyncTaskCompleteGeneral("SERVER_ERROR", 2001, errorCode, "", null);
    }

    /*private void createOne2Many(String Model, final int ID) {
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
*//*
            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create("web.service.child", new HashMap() {{
                put("name", "Autochip");
                put("mobile", "4103246464");
                put("service_id", ID); //one to many
            }});*//*

            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create(Model, new HashMap() {{
                put("insurance_doc_no", insuranceNo);
                put("vender_name", ID); //one to many
                put("insurance_start", insuranceStartDate);
                put("insurance_end", insuranceExpiryDate);
                put("set_reminder", insuranceRemainderDate);
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
