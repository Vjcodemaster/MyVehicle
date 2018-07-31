package app_utility;

/*
 * Created by Vijay on 05-06-2018.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.autochip.myvehicle.CircularProgressBar;
import com.autochip.myvehicle.MainActivity;
import com.autochip.myvehicle.RegisterVehicleFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;
import static app_utility.StaticReferenceClass.DB_NAME;
import static app_utility.StaticReferenceClass.PASSWORD;
import static app_utility.StaticReferenceClass.PORT_NO;
import static app_utility.StaticReferenceClass.SERVER_URL;
import static app_utility.StaticReferenceClass.USER_ID;

public class MyVehicleAsyncTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Activity aActivity;
    private String res = "";
    private Boolean isConnected = false;
    private String sMsgResult;
    int type;
    private CircularProgressBar circularProgressBar;
    private double dLatitude, dLongitude;
    private Context context;
    HashMap<String, Object> object = new HashMap<>();

    HashMap<String, Object> value = new HashMap<>();

    String sBrandName,  InsuranceData, EmissionData, sModelName, sRegNo;
    int brandID, ModelID, sManufactureYear;

    private LinkedHashMap<String, ArrayList<String>> lHMFormatData;
    //use this for future delete, edit tasks
    private LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID;
    //private AsyncInterface asyncInterface;

    ArrayList<Integer> alID = new ArrayList<>();
    ArrayList<String> alModelID = new ArrayList<>();
    ArrayList<Integer> alModelIDNo = new ArrayList<>();
    ArrayList<String> alModelYear = new ArrayList<>();
    ArrayList<String> alName = new ArrayList<>();
    ArrayList<String> alLicensePlate = new ArrayList<>();
    HashSet<Integer> hsModelIDSingleValues = new HashSet<>();

    public MyVehicleAsyncTask(Activity aActivity) {
        this.aActivity = aActivity;
    }

    public MyVehicleAsyncTask(Activity aActivity, String sBrandName, int brandID,int ModelID, String InsuranceData, String EmissionData,
                              String sModelName, String sRegNo,int sManufactureYear) {
        this.aActivity = aActivity;
        this.sBrandName = sBrandName;
        this.brandID = brandID;
        this.ModelID = ModelID;
        this.InsuranceData = InsuranceData;
        this.EmissionData = EmissionData;
        this.sModelName = sModelName;
        this.sRegNo = sRegNo;
        this.sManufactureYear = sManufactureYear;
    }

    /*public MyVehicleAsyncTask(Activity aActivity, AsyncInterface asyncInterface) {
        this.aActivity = aActivity;
        this.asyncInterface = asyncInterface;
    }*/

    //update task
    public MyVehicleAsyncTask(Context context, double dLatitude, double dLongitude) {
        this.context = context;
        this.dLatitude = dLatitude;
        this.dLongitude = dLongitude;
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
                snapRoadTask(params[1]);
                break;
            case 5:
                createTask();
                break;
            case 6:
                updateOne2Many(9);
                break;
            case 7:
                delete(9);
                break;
            case 8:
                readBrandTask();
                break;
            case 9:
                readBrandModelTask();
                break;
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        JSONArray jsonArray;
        JSONArray jsonArrayLegs;
        JSONObject jsonExtract;
        JSONObject jsonResponse;
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
                Log.e("Updating done", "success");
                break;
            case 3:
                MainActivity.asyncInterface.onAsyncTaskComplete("READ_DATA_FROM_SERVER", type, alID, alModelID, alModelIDNo, alModelYear, alName, alLicensePlate, hsModelIDSingleValues);
                //asyncInterface.onResultReceived("UPDATE_LOCATION", type, dLatitude, dLongitude, 0.0, 0.0);
                break;
            case 4:


               /* try {
                    jsonResponse = new JSONObject(result);
                    jsonArray = jsonResponse.getJSONArray("routes");
                    jsonArrayLegs = jsonArray.getJSONObject(0).getJSONArray("legs");
                    jsonExtract = jsonArrayLegs.getJSONObject(0).getJSONObject("start_location");
                    String sLatStart = jsonExtract.getString("lat");
                    String sLongStart = jsonExtract.getString("lng");
                    jsonExtract = jsonArrayLegs.getJSONObject(0).getJSONObject("end_location");
                    String sLatEnd = jsonExtract.getString("lat");
                    String sLongEnd = jsonExtract.getString("lng");
                    asyncInterface.onResultReceived("UPDATE_LOCATION_ROAD", type, Double.valueOf(sLatStart), Double.valueOf(sLongStart), Double.valueOf(sLatEnd), Double.valueOf(sLongEnd));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            case 9:
                RegisterVehicleFragment.mListener.onRegisterVehicleFragment("REGISTER_DATA", type,lHMFormatData, lHMBrandNameWithIDAndModelID);
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
            String insuranceNo, insuranceVendor, insuranceStartDate, insuranceExpiryDate, insuranceRemainderDate;
            String emissionNo, emissionVendor, emissionStartDate, emissionExpiryDate, emissionRemainderDate;

            object.put("model_month", 1);
            object.put("model_year", 2018);

            if(!TextUtils.isEmpty(InsuranceData)) {
                insuranceNo = InsuranceData.split(",")[0];
                insuranceVendor = InsuranceData.split(",")[1];
                insuranceStartDate = InsuranceData.split(",")[2];
                insuranceExpiryDate = InsuranceData.split(",")[3];
                insuranceRemainderDate = InsuranceData.split(",")[4];
            }
            if(!TextUtils.isEmpty(InsuranceData)) {
                emissionNo = EmissionData.split(",")[0];
                emissionVendor = EmissionData.split(",")[1];
                emissionStartDate = EmissionData.split(",")[2];
                emissionExpiryDate = EmissionData.split(",")[3];
                emissionRemainderDate = EmissionData.split(",")[4];
            }

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


            @SuppressWarnings("unchecked") final Integer idC = oc.create("fleet.vehicle", new HashMap() {{
                put("model_id", 110);
                put("license_plate", "KA 04 KA 1611");
                put("odometer", 15);
                put("model_year", 2020);
                put("model_month", 11);
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createOne2Many(String Model, final int ID) {
        try {
            OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);

            @SuppressWarnings("unchecked")
            Integer one2Many = oc.create("web.service.child", new HashMap() {{
                put("name", "Autochip");
                put("mobile", "4103246464");
                put("service_id", ID); //one to many
            }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readBrandTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("fleet.vehicle.model.brand", new Object[]{
                new Object[]{new Object[]{"create_uid", "!=", 100}}}, "name", "id");
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
        List<HashMap<String, Object>> data = oc.search_read_brands("product.template", new Object[]{
                new Object[]{new Object[]{"brand_id", "!=", false}}}, "name", "brand_id");
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
                }
                else {
                    alTmp = new ArrayList<>(lHMFormatData.get(sBrandNameTmp));
                    //lHMAllIDS = new LinkedHashMap<>();
                    lHMAllIDS = new LinkedHashMap<>(lHMBrandNameWithIDAndModelID.get(sBrandNameTmp));
                    alModelIDTmp = new ArrayList<>(lHMAllIDS.get(lHMBrandNameWithID.get(sBrandNameTmp)));
                }
                int id = lHMBrandNameWithID.get(sBrandNameTmp);
                alTmp.add(sModelNameTmp);
                alModelIDTmp.add(alModelID.get(j));
                lHMAllIDS.put(id,alModelIDTmp);
                lHMBrandNameWithIDAndModelID.put(sBrandNameTmp, lHMAllIDS);
                lHMFormatData.put(sBrandNameTmp, alTmp);
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
            final String e = String.valueOf(dLatitude) + "," + String.valueOf(dLongitude);
            int id;

            id = 104;
            // Create record
            @SuppressWarnings("unchecked")
            /*Integer idC = oc.create("fleet.vehicle", new HashMap() {{
                put("name", n);
                put("phone", p);
                put("is_company", isComp);
            }});*/


                    Boolean idC = oc.write("fleet.vehicle", new Object[]{id}, new HashMap() {{
                //put("name", n);
                //put("phone", p);
                //put("email", e);
                put("initial_reg_no", "KA 50 YU 5110");
                //put("name", "product.template");
                //put("model_id","Audi A3");
            }});

            msgResult += "Id of customer updated: " + idC.toString();
            Log.e("Update result", msgResult);
        } catch (Exception ex) {
            //msgResult = "Error: " + ex;
            Log.e("Updating error", ex.toString());
        }
    }

    /*
    finds all the data created by this user and returns required fields, which searches for create_uid = 1 condition
     */
    private void readTask() {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        List<HashMap<String, Object>> data = oc.search_read("fleet.vehicle", new Object[]{
                new Object[]{new Object[]{"create_uid", "=", 1}}}, "id", "model_id", "model_year", "name", "license_plate");


        for (int i = 0; i < data.size(); ++i) {
            alID.add(Integer.valueOf(data.get(i).get("id").toString()));
            alModelID.add(String.valueOf(data.get(i).get("model_id").toString()));
            //alModelIDNo.add(Integer.valueOf(data.get(i).get("model_id_no").toString()));
            alModelYear.add(String.valueOf(data.get(i).get("model_year").toString()));
            alName.add(String.valueOf(data.get(i).get("name").toString()));
            alLicensePlate.add(String.valueOf(data.get(i).get("license_plate").toString()));
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

    private String snapRoadTask(String uri) {
        HttpsURLConnection urlConnection;
        String result;
        Reader rd = null;
        StringBuffer sb = null;
//http://maps.google.com/maps/api/directions/json?origin=52.0,0&destination=52.0,0&sensor=true
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000 /* milliseconds */);
            con.setConnectTimeout(15000 /* milliseconds */);
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
    }

    public void delete(int id) {
        OdooConnect oc = OdooConnect.connect(SERVER_URL, PORT_NO, DB_NAME, USER_ID, PASSWORD);
        Boolean idC = oc.unlink("web.service.child", new Object[]{id});
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
}
