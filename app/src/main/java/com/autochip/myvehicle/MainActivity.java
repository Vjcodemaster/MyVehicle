package com.autochip.myvehicle;

import android.animation.Animator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    Menu menu;
    View popupView;
    String sBackStackParent;
    private TextView tvTitle, tvSubtitle;
    private View viewActionBar;

    Toolbar toolbar;
    int nUserDisplayWidth;
    int nUserDisplayHeight;
    int[] nOffSetLocation;
    int nDisplayDDXOffSet; //display drop down x off set
    int nDisplayOffSetD3;
    int viewHeight;

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

        init();

        ArrayList<String> alMakeModel = new ArrayList<>();
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
        alYearOfManufacture.add(2018);

        myVehicleTrackingRVAdapter = new MyVehicleTrackingRVAdapter(MainActivity.this, recyclerView, alMakeModel, alRegNo, alYearOfManufacture);
        recyclerView.setAdapter(myVehicleTrackingRVAdapter);
    }

    void init() {
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        viewActionBar = View.inflate(MainActivity.this, R.layout.toolbar_textview, null);
        tvTitle = viewActionBar.findViewById(R.id.tv_actionbar_app_name);
        tvTitle.setText(R.string.app_name);

        tvSubtitle = viewActionBar.findViewById(R.id.tv_actionbar_navigation);
        tvSubtitle.setText(R.string.title_insurance);

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
        navigation = findViewById(R.id.navigation);
        ColorStateList navigationViewColorStateList = new ColorStateList(states, colors);
        navigation.setItemIconTintList(navigationViewColorStateList);
        navigation.setItemTextColor(navigationViewColorStateList);
        navigation.setVisibility(View.GONE);
        //navigation.getMenu().getItem(0).setCheckable(false);

        setUpBottomNavigationContent(navigation);

        /*
        we will use the height of bottom navigation view to set as margin for insuranceFragment floating button.
        its height is sent via bundle
         */
        ViewTreeObserver viewTreeObserver = navigation.getViewTreeObserver();
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
        }
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        recyclerView = findViewById(R.id.rv_vehicle_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);

    }

    private void setUpBottomNavigationContent(final BottomNavigationView navigationView) {

        //String backStateName;

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment newFragment = null;
            FragmentTransaction transaction;
            Bundle bundle;
            String sBackStack = "";
            int nMenuVisibility;
            boolean isFragmentVisible;
            View view;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_register:
                        //item.setCheckable(true);
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
                        newFragment = EmissionFragment.newInstance("", "");
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
                    transaction = getSupportFragmentManager().beginTransaction();

                    show(view, findViewById(R.id.container));
                    transaction.replace(R.id.container, newFragment, null);
                    transaction.addToBackStack(sBackStack);
                    transaction.commit();
                }
                /*if(fm.getFragments().size()>1){
                    getSupportFragmentManager().popBackStack(getSupportFragmentManager().getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }*/
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toobar_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                navigation.setVisibility(View.VISIBLE);
                //startCircularReveal(findViewById(R.id.action_add));
                Fragment newFragment;
                FragmentTransaction transaction;
                newFragment = RegisterVehicleFragment.newInstance("", "");
                sBackStackParent = newFragment.getClass().getName();
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.t2b, R.anim.b2t);
                transaction.replace(R.id.container, newFragment, null);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {
        int size = getSupportFragmentManager().getFragments().size();
        FragmentManager fm = getSupportFragmentManager();
        *//*if (getSupportFragmentManager().getFragments().size() > 1) {

            //for(int i=0; i<size; i++){
            //Fragment fragment = fm.getFragments().get(size-1).getTag();
            //}
            fm.popBackStackImmediate();
            fm.popBackStack(fm.getFragments().get(size - 1).getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (getSupportFragmentManager().getFragments().size() == 1) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }*//*
    }*/

    /*
     To reveal a previously invisible view using this effect:
     below method show is used to produce circular animation effect on home screen buttons.
     */
    private void show(final View view, final View mParentView) {
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
            anim.setDuration(250);
            mParentView.setVisibility(View.VISIBLE);
            anim.start();
        }
    }
}
