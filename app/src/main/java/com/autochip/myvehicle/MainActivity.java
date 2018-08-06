package com.autochip.myvehicle;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import app_utility.AsyncInterface;
import app_utility.BitmapBase64;
import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.MyVehicleAsyncTask;
import app_utility.SharedPreferenceClass;
import dialogs.DialogMultiple;

import static app_utility.StaticReferenceClass.REGISTER_IMAGE_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements HomeInterfaceListener, OnImageUtilsListener, AsyncInterface {

    public static HomeInterfaceListener homeInterfaceListener;
    public static OnImageUtilsListener mBitmapCompressListener;
    public static AsyncInterface asyncInterface;

    int fileUriRequestCodeFlag = -1;
    public static int editModeVehicleID, adapterPosition;
    public static final int PICTURE_REQUEST_CODE = 1414;
    //private TextView mTextMessage;
    Menu menu;
    //View popupView;
    String sBackStackParent;
    private TextView tvTitle, tvSubtitle, tvUpdate;
    private View viewActionBar;

    VehicleDataStorage vehicleDataStorage;
    private Uri outputFileUri;
    Intent data;
    Bitmap bitmapImageUtils;

    DatabaseHandler db;
    File root;

    Toolbar toolbar;
    MyVehicleAsyncTask myVehicleAsyncTask;

    //int nUserDisplayWidth;
    int nUserDisplayHeight;
    int[] nOffSetLocation;
    int nDisplayDDXOffSet; //display drop down x off set
    int nDisplayOffSetD3;
    //int viewHeight;

    public static boolean hasToBePreparedToCreate = false;
    private CircularProgressBar circularProgressBar;

    private RecyclerView recyclerView;
    public MyVehicleTrackingRVAdapter myVehicleTrackingRVAdapter;

    private SharedPreferenceClass sharedPreferenceClass;

    // FOR NAVIGATION VIEW ITEM TEXT COLOR
    /*int[][] states = new int[][]{
            new int[]{-android.R.attr.state_checked},  // unchecked
            new int[]{android.R.attr.state_checked},   // checked
            new int[]{}                                // default
    };

    // Fill in color corresponding to state defined in state
    int[] colors = new int[]{
            Color.parseColor("#757575"),
            Color.parseColor("#03A9F4"),
            Color.parseColor("#757575"),
    };
    BottomNavigationView navigation;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        homeInterfaceListener = this;
        mBitmapCompressListener = this;
        asyncInterface = this;
        sharedPreferenceClass = new SharedPreferenceClass(MainActivity.this);
        db = new DatabaseHandler(MainActivity.this);
        vehicleDataStorage = new VehicleDataStorage();

        /*MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(MainActivity.this);
        myVehicleAsyncTask.execute(String.valueOf(1), "");*/

        init();

        /*ArrayList<String> alMakeModel = new ArrayList<>();
        alMakeModel.add("Mahindra Xuv 500");
        alMakeModel.add("Audi A8");
        alMakeModel.add("BMW X6");
        alMakeModel.add("Tesla X");
        ArrayList<String> alRegNo = new ArrayList<>();
        alRegNo.add("KA 04 H 5515");
        alRegNo.add("KA 02 LL 9999");
        alRegNo.add("KA 51 BI 3546");
        alRegNo.add("KA 04 UU 340");

        ArrayList<Integer> alYearOfManufacture = new ArrayList<>();
        alYearOfManufacture.add(2012);
        alYearOfManufacture.add(2017);
        alYearOfManufacture.add(2016);
        alYearOfManufacture.add(2018);*/

        /*myVehicleTrackingRVAdapter = new MyVehicleTrackingRVAdapter(MainActivity.this, recyclerView, alMakeModel, alRegNo, alYearOfManufacture);
        recyclerView.setAdapter(myVehicleTrackingRVAdapter);*/
        if (!sharedPreferenceClass.getFetchedBrandsFromOdooFirstTime()) {
            myVehicleAsyncTask = new MyVehicleAsyncTask(MainActivity.this);
            myVehicleAsyncTask.execute(String.valueOf(9), "");
        } else {
            /*myVehicleAsyncTask = new MyVehicleAsyncTask(MainActivity.this);
            myVehicleAsyncTask.execute(String.valueOf(3), "");*/
            //get data from sqlite and show it in this else condition
            ArrayList<DataBaseHelper> alDBData = new ArrayList<>(db.getAllUserVehicleData());
            vehicleDataStorage = new VehicleDataStorage();
            for (int i = 0; i < alDBData.size(); i++) {
                vehicleDataStorage.alID.add(alDBData.get(i).get_vehicle_id());
                vehicleDataStorage.alModelName.add(alDBData.get(i).get_model_name());
                vehicleDataStorage.alLicensePlate.add(alDBData.get(i).get_license_plate());
                vehicleDataStorage.alModelYear.add(alDBData.get(i).get_model_year());

                //BitmapBase64 is a custom class to convert string to bitmap and vice versa
                //Bitmap bitmap = BitmapBase64.convertToBitmap(alDBData.get(i).get_image_base64());
                String base64 = alDBData.get(i).get_image_base64();
                if (base64 != null) {
                    Bitmap bitmap = BitmapBase64.convertToBitmap(base64);
                    //byte[] decodedString = Base64.decode(alDBData.get(i).get_image_base64(), Base64.DEFAULT);
                    //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    vehicleDataStorage.alDisplayPicture.add(bitmap);
                } else {
                    vehicleDataStorage.alDisplayPicture.add(null);
                }
            }

            myVehicleTrackingRVAdapter = new MyVehicleTrackingRVAdapter(MainActivity.this, recyclerView, vehicleDataStorage.alID, vehicleDataStorage.alModelName,
                    vehicleDataStorage.alLicensePlate, vehicleDataStorage.alModelYear, vehicleDataStorage.alDisplayPicture);
            recyclerView.setAdapter(myVehicleTrackingRVAdapter);
        }

        /*myVehicleAsyncTask = new MyVehicleAsyncTask(MainActivity.this);
        myVehicleAsyncTask.execute(String.valueOf(3), "");*/
    }

    void init() {
        circularProgressBar = new CircularProgressBar(MainActivity.this, false);
        sharedPreferenceClass.setEditMode(false); //sets edit mode to false on every start to avoid problems with adding / updating
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        viewActionBar = View.inflate(MainActivity.this, R.layout.toolbar_textview, null);
        tvTitle = viewActionBar.findViewById(R.id.tv_actionbar_app_name);
        tvTitle.setText(R.string.app_name);

        tvSubtitle = viewActionBar.findViewById(R.id.tv_actionbar_navigation);
        //tvSubtitle.setText(R.string.title_insurance);

        tvUpdate = viewActionBar.findViewById(R.id.toolbar_tv_update);

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*View addView = findViewById(R.id.action_add);
                tvUpdate.setVisibility(View.GONE);
                addView.setVisibility(View.VISIBLE);*/
                hasToBePreparedToCreate = true;
                if (sharedPreferenceClass.getEditModeStatus()) {
                    RegisterFragment.mListener.onInteraction("SELECT_TAB_1", 102, this.getClass().getName());
                    //hasToBePreparedToCreate = true;
                } else {
                    RegisterFragment.mListener.onInteraction("SELECT_TAB_1", 101, this.getClass().getName());
                    //hasToBePreparedToCreate = true;
                }

                //RegisterVehicleFragment.mListener.onInteraction("PREPARE_TO_CREATE", 10, this.getClass().getName());


                /*FragmentManager fm = getSupportFragmentManager();
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);*/
            }
        });


        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        /*
        below code sets custom textview to the actionbar title
         */
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//set gravity to the textview of the ActionBar !
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.START);
        getSupportActionBar().setCustomView(viewActionBar, params);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        //mTextMessage = findViewById(R.id.message);
        /*navigation = findViewById(R.id.navigation);
        ColorStateList navigationViewColorStateList = new ColorStateList(states, colors);
        navigation.setItemIconTintList(navigationViewColorStateList);
        navigation.setItemTextColor(navigationViewColorStateList);
        navigation.setVisibility(View.GONE);*/
        //navigation.getMenu().getItem(0).setCheckable(false);

        //setUpBottomNavigationContent(navigation);

        /*
        we will use the height of bottom navigation view to set as margin for insuranceFragment floating button.
        its height is sent via bundle
         */
        /*ViewTreeObserver viewTreeObserver = navigation.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    viewHeight = navigation.getHeight();
                    if (viewHeight != 0)
                        navigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    //viewWidth = view.getWidth();
                }
            });
        }*/
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = findViewById(R.id.rv_vehicle_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);
    }

    /*private void setUpBottomNavigationContent(final BottomNavigationView navigationView) {

        //String backStateName;

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment newFragment = null;
            FragmentTransaction transaction;
            //Bundle bundle;
            String sBackStack = "";
            Bundle bundle;

            //int nMenuVisibility;
            //boolean isFragmentVisible;
            View view;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_register:
                        //item.setCheckable(true);

                        //commented frm here 31-07
                        view = findViewById(R.id.navigation_register);
                        item.setChecked(true);
                        newFragment = RegisterVehicleFragment.newInstance("", "");
                        sBackStack = newFragment.getClass().getName();
                        tvSubtitle.setText(R.string.register);
                        break;
                    case R.id.navigation_insurance:
                        //item.setCheckable(true);
                        view = findViewById(R.id.navigation_insurance);
                        item.setChecked(true);
                        newFragment = InsuranceFragment.newInstance(String.valueOf(viewHeight), "");
                        sBackStack = newFragment.getClass().getName();
                        tvSubtitle.setText(R.string.title_insurance);
                        break;
                    case R.id.navigation_Emission:
                        //item.setCheckable(true);
                        view = findViewById(R.id.navigation_Emission);
                        item.setChecked(true);
                        newFragment = EmissionFragment.newInstance(String.valueOf(viewHeight), "");
                        sBackStack = newFragment.getClass().getName();
                        tvSubtitle.setText(R.string.title_emission);
                        break;
                    case R.id.navigation_rc_fc:
                        //item.setCheckable(true);
                        view = findViewById(R.id.navigation_rc_fc);
                        item.setChecked(true);
                        newFragment = RCFCFragment.newInstance("", "");
                        sBackStack = newFragment.getClass().getName();
                        tvSubtitle.setText(R.string.title_rc);
                        break;
                    case R.id.navigation_service_history:
                        //item.setCheckable(true);
                        view = findViewById(R.id.navigation_service_history);
                        item.setChecked(true);
                        newFragment = ServiceHistoryFragment.newInstance("", "");
                        sBackStack = newFragment.getClass().getName();
                        tvSubtitle.setText(R.string.title_service_history);
                        break;
                }
                //FragmentManager fm = getSupportFragmentManager();
                if (newFragment != null && !newFragment.isVisible()) {
                    newFragment.setArguments(bundle);
                    transaction = getSupportFragmentManager().beginTransaction();

                    //this is extra line added for smoother animation and reveal effects. not sure why replace is helping it.
                    transaction.replace(R.id.container, newFragment, null);

                    transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
                    transaction.replace(R.id.container, newFragment, null);
                    transaction.addToBackStack(sBackStack);
                    transaction.commit();
                    show(view, findViewById(R.id.container));
                }
                *//*if(fm.getFragments().size()>1){
                    getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }*//*
                return false;
            }
        });
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toobar_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view;
        switch (item.getItemId()) {
            case R.id.action_add:
                //startCircularReveal(findViewById(R.id.action_add));
                Fragment newFragment;
                FragmentTransaction transaction;
                Bundle bundle = new Bundle();
                bundle.putInt("index", 0);
                //bundle.putInt("edit_mode", editMode);
                //bundle.putInt("vehicle_id", editModeVehicleID);
                newFragment = new RegisterFragment();
                newFragment.setArguments(bundle);

                //newFragment = RegisterVehicleFragment.newInstance("", "");
                sBackStackParent = newFragment.getClass().getName();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.t2b, R.anim.b2t);
                transaction.replace(R.id.container, newFragment, null);
                transaction.addToBackStack(null);
                transaction.commit();

                view = findViewById(R.id.action_add);
                show(view, findViewById(R.id.container));

                //30-07
                view.setVisibility(View.GONE);
                tvUpdate.setVisibility(View.VISIBLE);
                tvSubtitle.setText(R.string.register);
                //navigation.setVisibility(View.VISIBLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int size = getSupportFragmentManager().getFragments().size();
        if (size == 1) {
            View view = findViewById(R.id.action_add);
            tvUpdate.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            sharedPreferenceClass.setEditMode(false); //sets edit mode false so that normal create mode works fine
        }
        super.onBackPressed();
    }

    /*
     To reveal a previously invisible view using this effect:
     below method show is used to produce circular animation effect on home screen buttons.
     */
    private void show(final View view, final View mParentView) {
        //mParentView.setVisibility(View.INVISIBLE);
        // get the center for the clipping circle
        //int cx = (mAnimView.getLeft() + mAnimView.getRight()) / 2;
        //int cy = (mAnimView.getTop() + mParentView.getBottom()) / 2;

        nUserDisplayHeight = getResources().getDisplayMetrics().heightPixels; //holds height of screen in pixels

        nOffSetLocation = new int[2];
        view.getLocationInWindow(nOffSetLocation);
        nDisplayOffSetD3 = (nUserDisplayHeight + nOffSetLocation[1]) / 10;

        nDisplayDDXOffSet = (nOffSetLocation[0] / 2) + nDisplayOffSetD3;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the final radius for the clipping circle
            int finalRadius = Math.max(mParentView.getWidth(), mParentView.getHeight());

            //create the animator for this view (the start radius is zero)
            Animator anim;
            anim = ViewAnimationUtils.createCircularReveal(mParentView, nDisplayDDXOffSet, nOffSetLocation[1],
                    0, finalRadius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(350);
            mParentView.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICTURE_REQUEST_CODE) {
                this.data = data;
                new attractionNameAsyncTask().execute();
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final boolean isCamera;
                        if (data == null) {
                            isCamera = true;
                        } else {
                            final String action = data.getAction();
                            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                        root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + File.separator + getPackageName() + File.separator);
                        Uri selectedImageUri;
                        if (isCamera) {
                            selectedImageUri = outputFileUri;
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1920) {
                                ImageUtils imageUtils = new ImageUtils(root, selectedImageUri);
                            } else {
                                DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                            }
                        } else {
                            selectedImageUri = data.getData();
                            //Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());
                            saveFileAsBitmap(selectedImageUri);
                    *//*Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*//*
                 *//*if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1920) {
                        ImageUtils imageUtils = new ImageUtils(root, selectedImageUri);
                    } else {*//*
                            //DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                            //}
                        }
                        stopProgressBar();
                    }

                });*/
            }

        } else {
            stopProgressBar();
        }
    }

    private void saveFileAsBitmap(Uri selectedImageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap(selectedImageUri.getPath());
        FileOutputStream fileOutputStream = null;

        final String fname = System.currentTimeMillis() + "insurance";
        final File root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + File.separator + getPackageName() + File.separator);
        File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        try {
            fileOutputStream = new FileOutputStream(sdImageMainDirectory);
            // PNG is a loss less format, the compression factor (100) is ignored
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1920) {
            ImageUtils imageUtils = new ImageUtils(root, outputFileUri);
        } else {
            bitmapImageUtils = bitmap;
            //DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
        }
    }

    private void showProgressBar() {
        circularProgressBar.setCanceledOnTouchOutside(false);
        circularProgressBar.setCancelable(false);
        circularProgressBar.show();
    }

    private void stopProgressBar() {
        if (circularProgressBar != null && circularProgressBar.isShowing())
            circularProgressBar.dismiss();
    }


    @Override
    public void onHomeCalled(String sMessage, int nCase, String sActivityName, Uri outputFileUri) {
        FragmentManager fm;
        View addView;
        switch (sMessage) {
            case "SHOW_PROGRESS_BAR":
                showProgressBar();
                break;
            case "FILE_URI":
                this.outputFileUri = outputFileUri;
                fileUriRequestCodeFlag = nCase;
                break;
            case "CREATE_CONDITION_SATISFIED":
                /*this is called from RegisterVehicleFragment to let know that all the information user has entered is correct and can proceed
                to following operations
                 */
                addView = findViewById(R.id.action_add);
                tvUpdate.setVisibility(View.GONE);
                addView.setVisibility(View.VISIBLE);
                fm = getSupportFragmentManager();
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                hasToBePreparedToCreate = false;
                break;
            case "EDIT_VEHICLE":
                sharedPreferenceClass.setEditMode(true);
                View view = findViewById(R.id.action_add);
                adapterPosition = nCase;
                editModeVehicleID = Integer.valueOf(sActivityName); //this is the id of data to fetch from sql lite database
                view.performClick();
                //editMode = 1; //this means true and we need to consider for editing not creating in RegisterFragment
                break;
            case "EDIT_CONDITION_SATISFIED":
                sharedPreferenceClass.setEditMode(false);
                hasToBePreparedToCreate = false;
                addView = findViewById(R.id.action_add);
                tvUpdate.setVisibility(View.GONE);
                addView.setVisibility(View.VISIBLE);
                fm = getSupportFragmentManager();
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                ArrayList<DataBaseHelper> alDBData = new ArrayList<>(db.getAllUserVehicleData());
                vehicleDataStorage = new VehicleDataStorage();
                for (int i = 0; i < alDBData.size(); i++) {
                    vehicleDataStorage.alID.add(alDBData.get(i).get_vehicle_id());
                    vehicleDataStorage.alModelName.add(alDBData.get(i).get_model_name());
                    vehicleDataStorage.alLicensePlate.add(alDBData.get(i).get_license_plate());
                    vehicleDataStorage.alModelYear.add(alDBData.get(i).get_model_year());

                    //BitmapBase64 is a custom class to convert string to bitmap and vice versa
                    //Bitmap bitmap = BitmapBase64.convertToBitmap(alDBData.get(i).get_image_base64());
                    String base64 = alDBData.get(i).get_image_base64();
                    if (base64 != null) {
                        Bitmap bitmap = BitmapBase64.convertToBitmap(base64);
                        //byte[] decodedString = Base64.decode(alDBData.get(i).get_image_base64(), Base64.DEFAULT);
                        //Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        vehicleDataStorage.alDisplayPicture.add(bitmap);
                    } else {
                        vehicleDataStorage.alDisplayPicture.add(null);
                    }
                }

                myVehicleTrackingRVAdapter = new MyVehicleTrackingRVAdapter(MainActivity.this, recyclerView, vehicleDataStorage.alID, vehicleDataStorage.alModelName,
                        vehicleDataStorage.alLicensePlate, vehicleDataStorage.alModelYear, vehicleDataStorage.alDisplayPicture);
                recyclerView.setAdapter(myVehicleTrackingRVAdapter);
                break;
            default:

                break;
        }
    }

    @Override
    public void onBitmapCompressed(String sMessage, int nCase, Bitmap bitmap, Intent intent, Uri outputFileUri) {
        switch (sMessage) {
            case "BITMAP_COMPRESSED": //this is triggered from ImageUtils, which gets compressed bitmap and the same is sent back to Dialog Multiple
                bitmapImageUtils = bitmap;
                //DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                break;

        }
    }

    @Override
    public void onAsyncTaskComplete(String sMessage, int nCase, ArrayList<Integer> alID, ArrayList<String> alModelID, ArrayList<Integer> alModelIDNo,
                                    ArrayList<String> alModelYear, ArrayList<String> alName, ArrayList<String> alLicensePlate,
                                    ArrayList<Bitmap> alDisplayPicture, ArrayList<String> alEncodedDisplayPicture, HashSet<Integer> hsModelIDSingleValues) {
        switch (sMessage) {
            case "READ_DATA_FROM_SERVER":
                vehicleDataStorage.alID = alID;
                vehicleDataStorage.alModelName = alModelID;
                vehicleDataStorage.alModelIDNo = alModelIDNo;
                vehicleDataStorage.alModelYear = alModelYear;
                vehicleDataStorage.alName = alName;
                vehicleDataStorage.alLicensePlate = alLicensePlate;
                vehicleDataStorage.alDisplayPicture = alDisplayPicture;
                vehicleDataStorage.alEncodedDisplayPicture = alEncodedDisplayPicture;
                vehicleDataStorage.hsModelIDSingleValues = hsModelIDSingleValues;

                myVehicleTrackingRVAdapter = new MyVehicleTrackingRVAdapter(MainActivity.this, recyclerView, alID, alModelID,
                        alLicensePlate, alModelYear, alDisplayPicture);
                recyclerView.setAdapter(myVehicleTrackingRVAdapter);

                new addVehicleListAsyncTask().execute();
                break;
        }
    }

    @Override
    public void onAsyncTaskCompleteGeneral(String sMessage, int nCase, int position, String sData) {
        switch (sMessage) {
            case "REMOVE_POSITION":
                vehicleDataStorage.alID.remove(position);
                vehicleDataStorage.alModelName.remove(position);
                vehicleDataStorage.alLicensePlate.remove(position);
                //vehicleDataStorage.alEncodedDisplayPicture.remove(position);
                vehicleDataStorage.alModelYear.remove(position);

                myVehicleTrackingRVAdapter.notifyItemRemoved(position);

                if (!sData.equalsIgnoreCase(""))
                    db.deleteVehicleData(Integer.valueOf(sData));
                Toast.makeText(MainActivity.this, "Removed selected vehicle permanently", Toast.LENGTH_LONG).show();
                break;
            case "ADDED_NEW_DATA": //adds new data added to server to recyclerView
                String[] saAddedData = sData.split(",");
                int vehicleID, brandID, modelID;
                String sBrandName, sModelName, sLicensePlate, sEncodedDP, sModelYear;

                vehicleID = Integer.valueOf(saAddedData[0]);
                sBrandName = saAddedData[1];
                brandID = Integer.valueOf(saAddedData[2]);
                sModelName = saAddedData[3];
                modelID = Integer.valueOf(saAddedData[4]);
                sLicensePlate = saAddedData[5];
                sEncodedDP = saAddedData[6];
                sModelYear = saAddedData[7];

                vehicleDataStorage.alID.add(vehicleID);
                vehicleDataStorage.alModelName.add(sModelName);
                vehicleDataStorage.alLicensePlate.add(sLicensePlate);
                vehicleDataStorage.alEncodedDisplayPicture.add(sEncodedDP);
                vehicleDataStorage.alModelYear.add(sModelYear);

                Bitmap bitmap = BitmapBase64.convertToBitmap(sEncodedDP);
                vehicleDataStorage.alDisplayPicture.add(bitmap);
                myVehicleTrackingRVAdapter.notifyItemInserted(vehicleDataStorage.alID.size() - 1);

                db.addDataToUserVehicle(new DataBaseHelper(vehicleID, sBrandName, brandID, sModelName, modelID, sLicensePlate, sEncodedDP, sModelYear));
                break;
        }
    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>> lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {
        switch (sMessage) {
            case "REGISTER_DATA":

                ArrayList<String> alMake = new ArrayList<>(lHMFormatData.keySet());

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
                myVehicleAsyncTask = new MyVehicleAsyncTask(MainActivity.this);
                myVehicleAsyncTask.execute(String.valueOf(3), "");
                break;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class attractionNameAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
            root = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Android/data/" + File.separator + getPackageName() + File.separator);
            Uri selectedImageUri;
            if (isCamera) {
                selectedImageUri = outputFileUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1920) {
                    ImageUtils imageUtils = new ImageUtils(root, selectedImageUri);
                } else {
                    //DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                    bitmapImageUtils = bitmap;
                }
            } else {
                selectedImageUri = data.getData();
                //Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());
                saveFileAsBitmap(selectedImageUri);
                    /*Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    /*if (bitmap.getWidth() > 1080 && bitmap.getHeight() > 1920) {
                        ImageUtils imageUtils = new ImageUtils(root, selectedImageUri);
                    } else {*/
                //DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                //}
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (bitmapImageUtils != null && fileUriRequestCodeFlag == REGISTER_IMAGE_REQUEST_CODE) { //this flag checks if the request is from dialog or register fragment
                RegisterVehicleFragment.onImageUtilsListener.onBitmapCompressed("SET_BITMAP", fileUriRequestCodeFlag, bitmapImageUtils, null, null);
            } else if (bitmapImageUtils != null) {
                DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmapImageUtils, null, null);
            }
            stopProgressBar();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class addVehicleListAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < vehicleDataStorage.alID.size(); i++) {
                String[] saBrandNameSplit = vehicleDataStorage.alName.get(i).split("/");
                String sBrandID = String.valueOf(db.getBrandIDFromString(saBrandNameSplit[0]));
                db.addDataToUserVehicle(new DataBaseHelper(vehicleDataStorage.alID.get(i), saBrandNameSplit[0], Integer.valueOf(sBrandID), vehicleDataStorage.alModelName.get(i),
                        vehicleDataStorage.alModelIDNo.get(i), vehicleDataStorage.alLicensePlate.get(i), vehicleDataStorage.alEncodedDisplayPicture.get(i),
                        vehicleDataStorage.alModelYear.get(i)));
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    class VehicleDataStorage {
        ArrayList<Integer> alID = new ArrayList<>();
        ArrayList<String> alModelName = new ArrayList<>();
        ArrayList<Integer> alModelIDNo = new ArrayList<>();
        ArrayList<String> alModelYear = new ArrayList<>();
        ArrayList<String> alName = new ArrayList<>();
        ArrayList<String> alLicensePlate = new ArrayList<>();
        ArrayList<Bitmap> alDisplayPicture = new ArrayList<>();

        ArrayList<String> alEncodedDisplayPicture = new ArrayList<>();

        HashSet<Integer> hsModelIDSingleValues = new HashSet<>();

        //ArrayList<String> alMake = new ArrayList<>();
        //ArrayList<String> alModel = new ArrayList<>();
    }
}
