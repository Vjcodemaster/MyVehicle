package com.autochip.myvehicle;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import app_utility.BitmapBase64;
import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.MyVehicleAsyncTask;
import app_utility.SharedPreferenceClass;

import static app_utility.StaticReferenceClass.REGISTER_IMAGE_REQUEST_CODE;
import static com.autochip.myvehicle.MainActivity.PICTURE_REQUEST_CODE;
import static com.autochip.myvehicle.MainActivity.editModeVehicleID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.autochip.myvehicle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterVehicleFragment extends Fragment implements OnFragmentInteractionListener, OnImageUtilsListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private static final String ARG_LIST_MAKE = "ARG_LIST_MAKE";
    //private static final String ARG_LIST_MODEL = "ARG_LIST_MODEL";

    SharedPreferenceClass sharedPreferenceClass;

    DatabaseHandler db;

    int saveOnDetachFlag = 1;
    //ArrayList<DataBaseHelper> dbDataHelper;

    //LinkedHashMap<String, ArrayList<String>> lHMFormatData;
    //LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID;

    ArrayAdapter<String> adapterVehicle;
    ArrayAdapter<String> adapterMake;
    ArrayAdapter<String> adapterModel;

    String sPreviousMake, sPreviousModel, sPreviousRegNo, sPreviousYOM;
    Bitmap mPreviousBitmap = null;

    //boolean isDBUpdated = false;

    private ArrayList<String> alMake = new ArrayList<>();
    private ArrayList<String> alModel = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    public static OnFragmentInteractionListener mListener;

    public static OnImageUtilsListener onImageUtilsListener;

    EditText etMake, etModel, etRegNo, etYOM;
    TextView tvAddPhoto;
    Spinner spinnerVehicle, spinnerMake, spinnerModel;

    boolean isVisibleToUser = false;

    private File sdImageMainDirectory;
    private Uri outputFileUri;
    private ImageView ivPreview;

    ArrayList<DataBaseHelper> alDBData;

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
        onImageUtilsListener = this;
        init(view);

        if (!sharedPreferenceClass.getFetchedBrandsFromOdooFirstTime()) {
            MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(getActivity());
            myVehicleAsyncTask.execute(String.valueOf(9), "");
        } else {
            if (sharedPreferenceClass.getEditModeStatus()) {
                alMake.addAll(db.getAllBrands());
                adapterMake.notifyDataSetChanged();
                int vehicleID = MainActivity.editModeVehicleID;
                alDBData = new ArrayList<>(db.getRowDataFromVehicleTable(vehicleID));
                sPreviousMake = alDBData.get(0).get_brand_name();
                spinnerMake.setSelection(alMake.indexOf(alDBData.get(0).get_brand_name()));
                //spinnerModel.setSelection(alModel.indexOf(alDBData.get(0).get_model_name()));

                sPreviousRegNo = alDBData.get(0).get_license_plate();
                etRegNo.setText(alDBData.get(0).get_license_plate());

                sPreviousYOM = alDBData.get(0).get_model_year();
                etYOM.setText(alDBData.get(0).get_model_year());

                if (alDBData.get(0).get_image_base64() != null) {
                    Bitmap bitmap = BitmapBase64.convertToBitmap(alDBData.get(0).get_image_base64());

                    //if (bitmap != null) {
                    ivPreview.setImageBitmap(bitmap);
                    mPreviousBitmap = bitmap;
                    //}
                }

            } else {
                alMake.addAll(db.getAllBrands());
                adapterMake.notifyDataSetChanged();
            }
            /*dbDataHelper = new ArrayList<>(db.getAllBrands());
            for(int i=0;i<dbDataHelper.size(); i++) {
                alMake.add(dbDataHelper.get(i).get_brand_name());
            }*/
        }
        if (MainActivity.hasToBePreparedToCreate) {
            prepareToCreate();
        }
        //ColorStateList etViewColorStateList = new ColorStateList(editTextStates, editTextColors);
        //etMake.setTextColor(etViewColorStateList);

        return view;
    }

    //@SuppressLint("ClickableViewAccessibility")
    private void init(View view) {
        sharedPreferenceClass = new SharedPreferenceClass(getActivity());
        db = new DatabaseHandler(getActivity());
        etMake = view.findViewById(R.id.et_make);
        etModel = view.findViewById(R.id.et_model);
        etRegNo = view.findViewById(R.id.et_reg_no);
        etYOM = view.findViewById(R.id.et_yom);

        tvAddPhoto = view.findViewById(R.id.tv_add_photo);
        ivPreview = view.findViewById(R.id.iv_preview);

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
                    if (sharedPreferenceClass.getEditModeStatus()) {
                        spinnerModel.setSelection(alModel.indexOf(alDBData.get(0).get_model_name()));
                        sPreviousModel = alDBData.get(0).get_model_name();
                    }
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

        tvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        });
    }

    private void prepareToCreate() {
        //sharedPreferenceClass = new SharedPreferenceClass(getActivity());
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


        if (TextUtils.isEmpty(sRegNo) || TextUtils.isEmpty(etYOM.getText().toString().trim()) || ((BitmapDrawable) ivPreview.getDrawable()).getBitmap() == null) {
            Toast.makeText(getActivity(), "Please fill all information including image", Toast.LENGTH_SHORT).show();
            saveOnDetachFlag = 1;
        } else {
            int sManufactureYear = Integer.valueOf(etYOM.getText().toString().trim());

            //convert image to base64 before sending it to server
            Bitmap bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) ivPreview.getDrawable()).getBitmap(), 128, 128, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);

            MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(getActivity(), sBrandName, brandID, ModelID, InsuranceData, EmissionData, sModelName, sRegNo, sManufactureYear, encodedBitmap);
            myVehicleAsyncTask.execute(String.valueOf(5), "");
            MainActivity.homeInterfaceListener.onHomeCalled("CREATE_CONDITION_SATISFIED", 10, this.getClass().getName(), null);
            saveOnDetachFlag = 0;
            isVisibleToUser = false;
        }

    }

    private void compareData() {
        String sNewMake = spinnerMake.getSelectedItem().toString();
        String sNewModel = spinnerModel.getSelectedItem().toString();
        String sNewYOM = etYOM.getText().toString().trim();
        String sNewRegNo = etRegNo.getText().toString().trim();

        Bitmap newBitmap = ((BitmapDrawable) ivPreview.getDrawable()).getBitmap();

        //sharedPreferenceClass = new SharedPreferenceClass(getActivity());
        //String[] saVehicleInfo = sharedPreferenceClass.getVehicleInfo().split(",");

        HashMap<String, Object> mHMEditedList = new HashMap<>();
        newBitmap.sameAs(mPreviousBitmap);
        if (!sNewMake.equals(sPreviousMake)) {
            mHMEditedList.put("make", sNewMake);
        }

        String sBrandName = spinnerMake.getSelectedItem().toString().trim();
        int sModelPosition = spinnerModel.getSelectedItemPosition();
        int ModelID = db.getModelIDFromSelectedModelName(sBrandName, sModelPosition);

        if (!sNewModel.equals(sPreviousModel)) {
            //int ModelID = Integer.valueOf(saVehicleInfo[3]);
            mHMEditedList.put("model_id", ModelID);
        }

        if (!sNewYOM.equals(sPreviousYOM)) {
            mHMEditedList.put("model_month", 11);
            mHMEditedList.put("model_year", Integer.valueOf(sNewYOM));
        }

        if (!sNewRegNo.equals(sPreviousRegNo)) {
            mHMEditedList.put("license_plate", sNewRegNo);
        }

        if (!newBitmap.sameAs(mPreviousBitmap)) {
            Bitmap bitmap = Bitmap.createScaledBitmap(((BitmapDrawable) ivPreview.getDrawable()).getBitmap(), 128, 128, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);
            mHMEditedList.put("image_medium", encodedBitmap);
        }
        if (mHMEditedList.size() == 0) {
            Toast.makeText(getActivity(), "Nothing has changed", Toast.LENGTH_LONG).show();
        } else {
            MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(getActivity(), mHMEditedList, editModeVehicleID, db);
            myVehicleAsyncTask.execute(String.valueOf(2), "");
        }

    }

    private void openImageIntent() {
        //onImageUtilsListener.onBitmapCompressed("SHOW_PROGRESS_BAR",1,null, null, null);
        MainActivity.homeInterfaceListener.onHomeCalled("SHOW_PROGRESS_BAR", 0, null, null);

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + File.separator + getActivity().getPackageName() + File.separator);
        root.mkdirs();
        final String fname = System.currentTimeMillis() + "insurance";
        sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Choose");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        MainActivity.homeInterfaceListener.onHomeCalled("FILE_URI", REGISTER_IMAGE_REQUEST_CODE, null, outputFileUri);
        getActivity().startActivityForResult(chooserIntent, PICTURE_REQUEST_CODE);
        //onImageUtilsListener.onBitmapCompressed("START_ACTIVITY_FOR_RESULT",1,null, chooserIntent, outputFileUri);
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
        if (spinnerMake.getSelectedItem() != null && spinnerModel.getSelectedItem() != null) {
            final String sBrandName = spinnerMake.getSelectedItem().toString().trim();
            final String sModelName = spinnerModel.getSelectedItem().toString().trim();

            int brandID = db.getBrandIDFromString(sBrandName);

            int sModelPosition = spinnerModel.getSelectedItemPosition();
            int ModelID = db.getModelIDFromSelectedModelName(sBrandName, sModelPosition);
            sharedPreferenceClass.setVehicleInfo(sBrandName + "," + brandID + "," + sModelPosition + "," + ModelID + "," + sModelName);
        }
        /*String sBrandName = spinnerMake.getSelectedItem().toString().trim();
        int brandID = db.getBrandIDFromString(sBrandName);

        int sModelPosition = spinnerModel.getSelectedItemPosition();
        int ModelID = db.getModelIDFromSelectedModelName(sBrandName, sModelPosition);
        String sModelName = spinnerModel.getSelectedItem().toString().trim();*/


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

        if (saveOnDetachFlag == 1) {
            saveStateBeforeDetach();
        }
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onInteraction(String sMessage, int nCase, String sActivityName) {
        switch (sMessage) {
            case "PREPARE_TO_CREATE":
                if (isVisibleToUser && MainActivity.hasToBePreparedToCreate)
                    prepareToCreate();
                break;
            case "PREPARE_TO_EDIT":
                if (isVisibleToUser && MainActivity.hasToBePreparedToCreate)
                    compareData();
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

    @Override
    public void onBitmapCompressed(String sMessage, int nCase, Bitmap bitmap, Intent intent, Uri outputFileUri) {
        switch (sMessage) {
            case "SET_BITMAP":
                ivPreview.setImageBitmap(bitmap);
                break;
        }
    }
}
