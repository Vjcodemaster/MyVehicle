package com.autochip.myvehicle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.MyVehicleAsyncTask;
import app_utility.SharedPreferenceClass;
import dialogs.DialogMultiple;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.autochip.myvehicle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterVehicleFragment extends Fragment implements OnFragmentInteractionListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_LIST_MAKE = "ARG_LIST_MAKE";
    private static final String ARG_LIST_MODEL = "ARG_LIST_MODEL";

    SharedPreferenceClass sharedPreferenceClass;

    DatabaseHandler db;

    int saveOnDetachFlag = 1;
    //ArrayList<DataBaseHelper> dbDataHelper;

    //LinkedHashMap<String, ArrayList<String>> lHMFormatData;
    //LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID;

    ArrayAdapter<String> adapterVehicle;
    ArrayAdapter<String> adapterMake;
    ArrayAdapter<String> adapterModel;

    //boolean isDBUpdated = false;

    private ArrayList<String> alMake = new ArrayList<>();
    private ArrayList<String> alModel = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    public static OnFragmentInteractionListener mListener;

    EditText etMake, etModel, etRegNo, etYOM;
    Spinner spinnerVehicle, spinnerMake, spinnerModel;

    boolean isVisibleToUser = false;

    public RegisterVehicleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterVehicleFragment.
     */
    //, ArrayList<String> alMake, ArrayList<String> alModel //add these 2 parameters if required
    public static RegisterVehicleFragment newInstance(String param1, String param2) {
        RegisterVehicleFragment fragment = new RegisterVehicleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        //args.putStringArrayList(ARG_LIST_MAKE, alMake);
        //args.putStringArrayList(ARG_LIST_MODEL, alModel);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            //alMake.add(getArguments().getString(ARG_LIST_MAKE));
            //alModel.add(getArguments().getString(ARG_LIST_MODEL));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_vehicle, container, false);
        mListener = this;
        init(view);

        if (!sharedPreferenceClass.getFetchedBrandsFromOdooFirstTime()) {
            MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(getActivity());
            myVehicleAsyncTask.execute(String.valueOf(9), "");
        } else {
            alMake.addAll(db.getAllBrands());
            adapterMake.notifyDataSetChanged();
            /*dbDataHelper = new ArrayList<>(db.getAllBrands());
            for(int i=0;i<dbDataHelper.size(); i++) {
                alMake.add(dbDataHelper.get(i).get_brand_name());
            }*/
        }
        if (isVisibleToUser && MainActivity.hasToBePreparedToCreate) {
            prepareToCreate();
        }
        //ColorStateList etViewColorStateList = new ColorStateList(editTextStates, editTextColors);
        //etMake.setTextColor(etViewColorStateList);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(View view) {
        sharedPreferenceClass = new SharedPreferenceClass(getActivity());
        db = new DatabaseHandler(getActivity());
        etMake = view.findViewById(R.id.et_make);
        etModel = view.findViewById(R.id.et_model);
        etRegNo = view.findViewById(R.id.et_reg_no);
        etYOM = view.findViewById(R.id.et_yom);

        //below code was implemented to listen for swipe. it was working fine
        /*LinearLayout llParentSwipe = view.findViewById(R.id.ll_parent_register);

        llParentSwipe.setOnTouchListener(new OnSwipeTouchListener(getActivity()){
            @Override
            public void onSwipeLeft() {
                MainActivity.homeInterfaceListener.onHomeCalled("LEFT_SWIPE", 1, this.getClass().getName(), null);
                //Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
            }
        });*/

        spinnerVehicle = view.findViewById(R.id.spinner_vehicle);
        adapterVehicle = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.vehicle_array));
        adapterVehicle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicle.setAdapter(adapterVehicle);
        spinnerVehicle.setSelection(2); //selects 4 wheeler by default

        spinnerMake = view.findViewById(R.id.spinner_make);
        adapterMake = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, alMake);
        adapterMake.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMake.setAdapter(adapterMake);

        spinnerModel = view.findViewById(R.id.spinner_model);
        adapterModel = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, alModel);
        adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModel.setAdapter(adapterModel);

        try {
            @SuppressLint("ResourceType") XmlResourceParser parser = getResources().getXml(R.drawable.eee);
            ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
            etMake.setTextColor(colors);
            etModel.setTextColor(colors);
            etRegNo.setTextColor(colors);
            etYOM.setTextColor(colors);
        } catch (Exception e) {
            // handle exceptions
        }

        spinnerVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                //if (isDBUpdated) {
                ArrayList<String> alModelNames = db.getIdForStringTabAll(spinnerMake.getSelectedItem().toString());

                //dbDataHelper = new ArrayList<>(db.getAllModels(spinnerMake.getSelectedItem().toString()));
                //ArrayList<DataBaseHelper> alDataBase = new ArrayList<>(dbDataHelper);
                alModel.clear();
                if (alModelNames != null) {
                    alModel.addAll(alModelNames);
                    adapterModel.notifyDataSetChanged();
                }
                //}
                /*alModel.clear();
                alModel.addAll(lHMFormatData.get(spinnerMake.getSelectedItem().toString()));
                adapterModel.notifyDataSetChanged();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void prepareToCreate() {
        sharedPreferenceClass = new SharedPreferenceClass(getActivity());
        String[] saVehicleInfo = sharedPreferenceClass.getVehicleInfo().split(",");
        //int sBrandID = spinnerMake.getSelectedItemPosition();
        String sBrandName = saVehicleInfo[0];
        int brandID = Integer.valueOf(saVehicleInfo[1]);

        int sModelPosition = Integer.valueOf(saVehicleInfo[2]);

        int ModelID = Integer.valueOf(saVehicleInfo[3]);

        String InsuranceData = sharedPreferenceClass.getInsuranceData();

        String EmissionData = sharedPreferenceClass.getEmissionData();

        String sModelName = saVehicleInfo[4];

        String sRegNo = etRegNo.getText().toString().trim();

        if (TextUtils.isEmpty(sRegNo) || TextUtils.isEmpty(etYOM.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please fill all information", Toast.LENGTH_SHORT).show();
            saveOnDetachFlag = 1;
        } else {
            int sManufactureYear = Integer.valueOf(etYOM.getText().toString().trim());
            MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(getActivity(), sBrandName, brandID, ModelID, InsuranceData, EmissionData, sModelName, sRegNo, sManufactureYear);
            myVehicleAsyncTask.execute(String.valueOf(5), "");
            MainActivity.homeInterfaceListener.onHomeCalled("CREATE_CONDITION_SATISFIED", 10, this.getClass().getName(), null);
            saveOnDetachFlag = 0;
            isVisibleToUser = false;
        }

    }


    /*private void prepareToCreate() {

        //String sVehicleInfo = sharedPreferenceClass.getVehicleInfo();
        //int sBrandID = spinnerMake.getSelectedItemPosition();
        String sBrandName = spinnerMake.getSelectedItem().toString();
        int brandID = db.getBrandIDFromString(sBrandName);

        int sModelPosition = spinnerModel.getSelectedItemPosition();
        int ModelID = db.getModelIDFromSelectedModelName(sBrandName, sModelPosition);

        String InsuranceData = sharedPreferenceClass.getInsuranceData();

        *//*if(!TextUtils.isEmpty(InsuranceData)) {
            String insuranceNo = InsuranceData.split(",")[0];
            String insuranceVendor = InsuranceData.split(",")[1];
            String insuranceStartDate = InsuranceData.split(",")[2];
            String insuranceExpiryDate = InsuranceData.split(",")[3];
            String insuranceRemainderDate = InsuranceData.split(",")[4];
        }*//*

        String EmissionData = sharedPreferenceClass.getEmissionData();
        *//*if(!TextUtils.isEmpty(InsuranceData)) {
            String emissionNo = EmissionData.split(",")[0];
            String emissionVendor = EmissionData.split(",")[1];
            String emissionStartDate = EmissionData.split(",")[2];
            String emissionExpiryDate = EmissionData.split(",")[3];
            String emissionRemainderDate = EmissionData.split(",")[4];
        }*//*

        String sModelName = spinnerModel.getSelectedItem().toString().trim();

        String sRegNo = etRegNo.getText().toString().trim();

        if (TextUtils.isEmpty(sRegNo) || TextUtils.isEmpty(etYOM.getText().toString().trim())) {
            Toast.makeText(getActivity(), "Please fill all information", Toast.LENGTH_SHORT).show();
        } else {
            int sManufactureYear = Integer.valueOf(etYOM.getText().toString().trim());
            MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(getActivity(), sBrandName, brandID, ModelID, InsuranceData, EmissionData, sModelName, sRegNo, sManufactureYear);
            myVehicleAsyncTask.execute(String.valueOf(5), "");
            MainActivity.homeInterfaceListener.onHomeCalled("CREATE_CONDITION_SATISFIED", 10, this.getClass().getName(), null);
        }

    }*/

    private void saveStateBeforeDetach() {
        String sBrandName = spinnerMake.getSelectedItem().toString().trim();
        int brandID = db.getBrandIDFromString(sBrandName);

        int sModelPosition = spinnerModel.getSelectedItemPosition();
        int ModelID = db.getModelIDFromSelectedModelName(sBrandName, sModelPosition);
        String sModelName = spinnerModel.getSelectedItem().toString().trim();

        sharedPreferenceClass.setVehicleInfo(sBrandName + "," + brandID + "," + sModelPosition + "," + ModelID + "," + sModelName);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(saveOnDetachFlag==1) {
            saveStateBeforeDetach();
        }
        mListener = null;
    }

    @Override
    public void onInteraction(String sMessage, int nCase, String sActivityName) {
        switch (sMessage) {
            case "PREPARE_TO_CREATE":
                if (isVisibleToUser && MainActivity.hasToBePreparedToCreate)
                    prepareToCreate();
                break;
        }
    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, final LinkedHashMap<String, ArrayList<String>> lHMFormatData, final LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {
        switch (sMessage) {
            case "REGISTER_DATA":
                alMake.addAll(lHMFormatData.keySet());
                adapterMake.notifyDataSetChanged();

                //ArrayList<String> alModel = new ArrayList<>(lHMFormatData.keySet());
                //this.lHMFormatData = lHMFormatData;
                //this.lHMBrandNameWithIDAndModelID = lHMBrandNameWithIDAndModelID;

                /*AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //can perform the below for loop task here if we want it in background thread
                        //isDBUpdated = true;
                    }
                });*/

                LinkedHashMap<Integer, ArrayList<Integer>> arrayListLinkedHashMap;
                ArrayList<String> alModelName;
                StringBuilder sbModelName, sbModelID;
                String sModelName = null, sModelID = null;

                for (int i = 0; i < alMake.size(); i++) {
                    String sBrandKey = alMake.get(i);
                    arrayListLinkedHashMap = new LinkedHashMap<>(lHMBrandNameWithIDAndModelID.get(alMake.get(i)));
                    ArrayList<Integer> alBrandKey = new ArrayList<>(arrayListLinkedHashMap.keySet());
                    alModelName = new ArrayList<>(lHMFormatData.get(sBrandKey));
                    sbModelName = new StringBuilder();
                    sbModelID = new StringBuilder();
                    for (int j = 0; j < alModelName.size(); j++) {
                        //LinkedHashMap<Integer, ArrayList<Integer>> lHMAllIDS = new LinkedHashMap<>(arrayListLinkedHashMap);
                        sbModelName.append(alModelName.get(j));
                        if (j < alModelName.size() - 1)
                            sbModelName.append(",");
                        sModelName = sbModelName.toString();

                        ArrayList<Integer> alModelID = new ArrayList<>(arrayListLinkedHashMap.get(alBrandKey.get(0)));
                        sbModelID.append(alModelID.get(j));
                        if (j < alModelName.size() - 1)
                            sbModelID.append(",");
                        sModelID = sbModelID.toString();
                        //db.updateMultipleData(new DataBaseHelper(alModelName.get(j), nID), sBrandKey);
                    }
                    db.addData(new DataBaseHelper(sBrandKey, String.valueOf(alBrandKey.get(0)), sModelName, sModelID));
                }
                sharedPreferenceClass.setFetchedBrandsFromOdooFirstTime(true);
                break;
        }
    }
}
