package com.autochip.myvehicle;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Stack;

import app_utility.BottomNavigationViewHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */

public class RegisterFragment extends Fragment implements OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    int viewHeight = 0;
    int index = 0;
    int previousItemID;
    MenuItem mItemLinker;
    Menu menu;
    // int editMode = 0;
    // int editModeVehicleID = -1;

    //creating views to hold the view of textView of tablayout
    /*View registerTab;
    View sessionsTab;
    View boothsTab;
    View productsTab;
    View speakersTab;*/

    //NavigationView navigationView;


    Stack<Integer> pageHistory;
    int currentPage;
    boolean saveToHistory;
    public static OnFragmentInteractionListener mListener;
    BottomNavigationView navigation;
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
    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * //@param param1 Parameter 1.
     * //@param param2 Parameter 2.
     * //@return A new instance of fragment RegisterFragment.
     */

   /* public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt("index");
            //viewHeight = getArguments().getInt("view_height");
           /* editMode = getArguments().getInt("edit_mode");
            editModeVehicleID = getArguments().getInt("vehicle_id");
*/
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home_screen, container, false);
        // Inflate the layout for this fragment
        //setHasOptionsMenu(true);

        //menu.findItem(R.id.nav_logout).setTitle(null);
        //creates shadow effect under appbar layout below lollipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View viewShadow = view.findViewById(R.id.view_shadow);
            viewShadow.setVisibility(View.VISIBLE);
        }

        navigation = view.findViewById(R.id.navigation);
        ColorStateList navigationViewColorStateList = new ColorStateList(states, colors);
        navigation.setItemIconTintList(navigationViewColorStateList);
        navigation.setItemTextColor(navigationViewColorStateList);
        //navigation.setVisibility(View.GONE);
        //navigation.getMenu().getItem(0).setCheckable(false);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        setUpBottomNavigationContent(navigation);
        menu = navigation.getMenu();
        int resourceId = getResources().getIdentifier("design_bottom_navigation_height", "dimen", getActivity().getPackageName());

        if (resourceId > 0) {
            viewHeight = getResources().getDimensionPixelSize(resourceId);
        }

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
        /*if (getArguments() != null) {
            index = getArguments().getInt("index");
        }*/

        pageHistory = new Stack<>();

        // Set up the ViewPager with the sections adapter.
        mViewPager = view.findViewById(R.id.viewpager_container);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (saveToHistory)
                    pageHistory.push(currentPage);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        saveToHistory = true;

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*
        will help to leave a gap between each tabs or gap in the beginning of tab.
         */
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            drawable.setSize(20, 5);
            ((LinearLayout) root).setDividerPadding(1);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        //Adding the tabs using addTab() method tabLayout.addTab(tabLayout.newTab().setText("Sessions"));
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        //MenuItem bedMenuItem = menu.findItem(R.id.bedSwitch);
        //sets the size of textview of tablayout same
        //tabLayout.setTabGravity(TabLayout.MODE_FIXED);

        /*registerTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
        boothsTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
        productsTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2);
        speakersTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(3);
        sessionsTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(4);*/

        //listener
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
            @Override

            public void onTabSelected(TabLayout.Tab tab) {
                MenuItem menuItem;

                //HomeScreen homeScreen = (HomeScreen) getActivity();
                //if (homeScreen != null && !homeScreen.isNavHomeChecked) {
                mViewPager.setCurrentItem(tab.getPosition());

                int selectedTabPosition = tab.getPosition(); //tab position

                //firs tab and second tab contains view of tabLayout tabs, which can be used to set drawables.
                /*View firstTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0);
                View secondTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1);
                View thirdTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2);
                View fourthTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(3);
                View fifthTab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(4);*/
                    /*if (container != null) {
                        navigationDrawerView = container.getRootView();
                        navigationView = (NavigationView) navigationDrawerView.findViewById(R.id.nav_view);
                    }*/
                //if (navigationView != null) {
                    switch (selectedTabPosition) {
                        case 0:
                            menuItem = menu.findItem(R.id.navigation_register);
                            menuItem.setChecked(true);
                            /*firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                            secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fifthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));*/
                            break;
                        case 1:
                            menuItem = menu.findItem(R.id.navigation_insurance);
                            menuItem.setChecked(true);
                                /*if (!navigationView.isSelected()) {
                                    navigationView.setCheckedItem(R.id.nav_booths);
                                }*/
                            /*firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                            thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fifthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));*/
                            break;
                        case 2:
                            menuItem = menu.findItem(R.id.navigation_Emission);
                            menuItem.setChecked(true);
                                /*if (!navigationView.isSelected()) {
                                    navigationView.setCheckedItem(R.id.nav_products);
                                }*/
                            /*firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                            fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fifthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));*/
                            break;
                        case 3:
                            menuItem = menu.findItem(R.id.navigation_rc_fc);
                            menuItem.setChecked(true);
                                /*if (!navigationView.isSelected()) {
                                    navigationView.setCheckedItem(R.id.nav_speakers);
                                }*/
                            /*firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));
                            fifthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));*/
                            break;

                        case 4:
                            menuItem = menu.findItem(R.id.navigation_service_history);
                            menuItem.setChecked(true);
                            /*firstTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            secondTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            thirdTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fourthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_unselected, null));
                            fifthTab.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.home_tab_selected, null));*/
                            break;
                    }
                //}
                super.onTabSelected(tab);
                //}
            }
        });

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), 5, viewHeight);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //call index of the tab depending upon what user clicks on home screen.(index is sent via arguments)
        /*if (index > 0) {
            selectPage(index);
        }*/

        return view;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.navigation, menu);
        this.menu = menu;
    }
*/

    private void setUpBottomNavigationContent(final BottomNavigationView navigationView) {

        //String backStateName;

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            View view;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                mItemLinker = item;
                if (previousItemID != itemID) {
                    switch (itemID) {
                        case R.id.navigation_register:
                            //item.setCheckable(true);
                            //commented frm here 31-07
                            view = getView().findViewById(R.id.navigation_register);
                            item.setChecked(true);
                            //RegisterFragment.mListener.onInteraction("SELECT_TAB_ONLY", 0, this.getClass().getName());
                            selectPage(0);
                            break;
                        case R.id.navigation_insurance:
                            //item.setCheckable(true);
                            view = getView().findViewById(R.id.navigation_insurance);
                            item.setChecked(true);
                            //RegisterFragment.mListener.onInteraction("SELECT_TAB_ONLY", 1, String.valueOf(viewHeight));
                            selectPage(1);
                            break;
                        case R.id.navigation_Emission:
                            //item.setCheckable(true);
                            view = getView().findViewById(R.id.navigation_Emission);
                            item.setChecked(true);
                            selectPage(2);
                            break;
                        case R.id.navigation_rc_fc:
                            //item.setCheckable(true);
                            view = getView().findViewById(R.id.navigation_rc_fc);
                            item.setChecked(true);
                            selectPage(3);
                            break;
                        case R.id.navigation_service_history:
                            //item.setCheckable(true);
                            view = getView().findViewById(R.id.navigation_service_history);
                            item.setChecked(true);
                            selectPage(4);
                            break;
                    }

                    //show(view, findViewById(R.id.container));
                    previousItemID = itemID;
                }
                return false;
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */

    void selectPage(int pageIndex) {
        tabLayout.setScrollPosition(pageIndex, 0f, true);
        mViewPager.setCurrentItem(pageIndex);
    }


   /* public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            return true;
        }

        return true;

    }*/

    /*
    handleBackPressed handles complete back press that is triggered from HomeScreen Activity.
    this method calls onBackPressed based on the condition.
     */
    public static boolean handleBackPressed(FragmentManager fm) {
        if (fm.getFragments() != null) {
            for (Fragment frag : fm.getFragments()) {
                if (frag != null && frag.isVisible() && frag instanceof RegisterFragment) {
                    if (((RegisterFragment) frag).onBackPressed()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected boolean onBackPressed() {
        FragmentManager fm = getChildFragmentManager();
        if (handleBackPressed(fm)) {
            return true;
        } else if (getUserVisibleHint() && fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = this;
        /*
        OnFragmentInteractionListener is  used for communication between activity or fragments.
        OnFragmentInteractionListener is a interface implemented from utility package which is custom.
         */
    }

    @Override
    public void onInteraction(String sMessage, int nCase, String sActivityName) {
        switch (sMessage) {
            case "SELECT_TAB_1":
                selectPage(0);
                if (nCase == 101)
                    RegisterVehicleFragment.mListener.onInteraction("PREPARE_TO_CREATE", 101, this.getClass().getName());
                else
                    RegisterVehicleFragment.mListener.onInteraction("PREPARE_TO_EDIT", 102, this.getClass().getName());
                break;
            /*case "SELECT_TAB_ONLY":
                String regexStr = "^[0-9]*$";
                if (sActivityName.matches(regexStr))
                    viewHeight = Integer.valueOf(sActivityName);
                selectPage(nCase);
                break;*/
        }
    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>> lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {

    }
}
