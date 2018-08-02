package com.autochip.myvehicle;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
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

import app_utility.AsyncInterface;
import app_utility.MyVehicleAsyncTask;
import dialogs.DialogMultiple;

import static app_utility.StaticReferenceClass.REGISTER_IMAGE_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements HomeInterfaceListener, OnImageUtilsListener, AsyncInterface {

    public static HomeInterfaceListener homeInterfaceListener;
    public static OnImageUtilsListener mBitmapCompressListener;
    public static AsyncInterface asyncInterface;

    int fileUriRequestCodeFlag = -1;
    public static final int PICTURE_REQUEST_CODE = 1414;
    private TextView mTextMessage;
    Menu menu;
    View popupView;
    String sBackStackParent;
    private TextView tvTitle, tvSubtitle, tvUpdate;
    private View viewActionBar;

    VehicleDataStorage vehicleDataStorage;
    private Uri outputFileUri;
    Intent data;
    Bitmap bitmapImageUtils;


    File root;

    Toolbar toolbar;

    int nUserDisplayWidth;
    int nUserDisplayHeight;
    int[] nOffSetLocation;
    int nDisplayDDXOffSet; //display drop down x off set
    int nDisplayOffSetD3;
    int viewHeight;

    public static boolean hasToBePreparedToCreate = false;
    private CircularProgressBar circularProgressBar;

    private RecyclerView recyclerView;
    public MyVehicleTrackingRVAdapter myVehicleTrackingRVAdapter;

    // FOR NAVIGATION VIEW ITEM TEXT COLOR
    int[][] states = new int[][]{
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
    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        homeInterfaceListener = this;
        mBitmapCompressListener = this;
        asyncInterface = this;

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

        MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(MainActivity.this);
        myVehicleAsyncTask.execute(String.valueOf(3), "");
    }

    void init() {
        circularProgressBar = new CircularProgressBar(MainActivity.this, false);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        viewActionBar = View.inflate(MainActivity.this, R.layout.toolbar_textview, null);
        tvTitle = viewActionBar.findViewById(R.id.tv_actionbar_app_name);
        tvTitle.setText(R.string.app_name);

        tvSubtitle = viewActionBar.findViewById(R.id.tv_actionbar_navigation);
        tvSubtitle.setText(R.string.title_insurance);

        tvUpdate = viewActionBar.findViewById(R.id.toolbar_tv_update);

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*View addView = findViewById(R.id.action_add);
                tvUpdate.setVisibility(View.GONE);
                addView.setVisibility(View.VISIBLE);*/

                RegisterFragment.mListener.onInteraction("SELECT_TAB_1", 10, this.getClass().getName());
                //RegisterVehicleFragment.mListener.onInteraction("PREPARE_TO_CREATE", 10, this.getClass().getName());

                hasToBePreparedToCreate = true;
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
                View addView = findViewById(R.id.action_add);
                tvUpdate.setVisibility(View.GONE);
                addView.setVisibility(View.VISIBLE);
                FragmentManager fm = getSupportFragmentManager();
                fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                MainActivity.hasToBePreparedToCreate = false;
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
                                    ArrayList<String> alModelYear, ArrayList<String> alName, ArrayList<String> alLicensePlate, HashSet<Integer> hsModelIDSingleValues) {
        switch (sMessage) {
            case "READ_DATA_FROM_SERVER":
                vehicleDataStorage.alID = alID;
                vehicleDataStorage.alModelID = alModelID;
                vehicleDataStorage.alModelIDNo = alModelIDNo;
                vehicleDataStorage.alModelYear = alModelYear;
                vehicleDataStorage.alName = alName;
                vehicleDataStorage.alLicensePlate = alLicensePlate;
                vehicleDataStorage.hsModelIDSingleValues = hsModelIDSingleValues;

                myVehicleTrackingRVAdapter = new MyVehicleTrackingRVAdapter(MainActivity.this, recyclerView, alID, alModelID, alLicensePlate, alModelYear);
                recyclerView.setAdapter(myVehicleTrackingRVAdapter);

                break;
        }
    }

    @Override
    public void onAsyncTaskCompleteGeneral(String sMessage, int nCase, int position, String sData) {
        switch (sMessage) {
            case "REMOVE_POSITION":
                vehicleDataStorage.alID.remove(position);
                vehicleDataStorage.alModelID.remove(position);
                vehicleDataStorage.alLicensePlate.remove(position);
                vehicleDataStorage.alModelYear.remove(position);

                myVehicleTrackingRVAdapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this, "Removed selected vehicle permanently", Toast.LENGTH_LONG).show();
                break;
            case "ADDED_NEW_DATA": //adds new data added to server to recyclerview
                String[] saAddedData = sData.split(",");
                vehicleDataStorage.alID.add(Integer.valueOf(saAddedData[0]));
                vehicleDataStorage.alModelID.add(saAddedData[1]);
                vehicleDataStorage.alLicensePlate.add(saAddedData[2]);
                vehicleDataStorage.alModelYear.add(saAddedData[3]);

                myVehicleTrackingRVAdapter.notifyItemInserted(vehicleDataStorage.alID.size() - 1);
                break;
        }
    }


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

    class VehicleDataStorage {
        ArrayList<Integer> alID = new ArrayList<>();
        ArrayList<String> alModelID = new ArrayList<>();
        ArrayList<Integer> alModelIDNo = new ArrayList<>();
        ArrayList<String> alModelYear = new ArrayList<>();
        ArrayList<String> alName = new ArrayList<>();
        ArrayList<String> alLicensePlate = new ArrayList<>();
        HashSet<Integer> hsModelIDSingleValues = new HashSet<>();

        ArrayList<String> alMake = new ArrayList<>();
        ArrayList<String> alModel = new ArrayList<>();
    }
}
