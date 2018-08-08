package com.autochip.myvehicle;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import app_utility.SharedPreferenceClass;
import dialogs.DialogMultiple;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.autochip.myvehicle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EmissionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmissionFragment extends Fragment implements OnFragmentInteractionListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    Dialog dialog;
    TextView tvStartDateValue, tvExpiryDateValue, tvRemainderDateValue;
    LinearLayout llDate, llDateValue;
    final Calendar myCalendar = Calendar.getInstance();
    FloatingActionButton fab;
    TableLayout tlPolicy;
    TableRow row;
    private Uri outputFileUri;
    private ImageView ivPreview;

    private CircularProgressBar circularProgressBar;

    private int viewHeight = 0;
    File sdImageMainDirectory;

    public static OnFragmentInteractionListener mListener;

    DialogMultiple dialogMultiple;
    SharedPreferenceClass sharedPreferenceClass;

    //public static OnImageUtilsListener mBitmapCompressListener;

    public EmissionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmissionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmissionFragment newInstance(String param1, String param2) {
        EmissionFragment fragment = new EmissionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mBitmapCompressListener = this;
        mListener = this;
        circularProgressBar = new CircularProgressBar(getActivity(), false);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            viewHeight = Integer.valueOf(mParam1);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        /*if (isVisibleToUser) {
            dialogMultiple = new DialogMultiple(getActivity(), 2, MainActivity.mBitmapCompressListener);
        }*//* else {
            checkAndHide();
        }*/
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_emission, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getActivity());
        tlPolicy = view.findViewById(R.id.tl_policy);

        fab = view.findViewById(R.id.fab);
        if(!sharedPreferenceClass.getEditModeStatus()) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            params.bottomMargin = viewHeight + 6;
            fab.setLayoutParams(params);
        } else {
            fab.setVisibility(View.GONE);
        }

        //this statement is written in setUserVisible Hint because this dialog should be created only when fragment is visible to user
        //dialogMultiple = new DialogMultiple(getActivity(), 2, MainActivity.mBitmapCompressListener);
        dialogMultiple = new DialogMultiple(getActivity(), 2, MainActivity.mBitmapCompressListener);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMultiple.onCreate(2);
                dialogMultiple.dialog.show();
            }
        });

        /*
        inflating views dynamically for table layout where we always add the data dynamically from odoo not from xml
        here we are inflating table rows first using for loop depending on the data we receive and then add table row header
        by inflating it and setting it to 0th index of Table Layout tlPolicy.addView(trHeading, 0);. to set text we can use,
        TextView tv = (TextView) row.childAt(0);| tv.setText("Text"); or row.findViewById()
         */
        TableRow trHeading = (TableRow) inflater.inflate(R.layout.table_row_heading, null);

        for (int i = 0; i < 3; i++) {
            //LayoutInflater trInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = (TableRow) inflater.inflate(R.layout.table_row, null);
            row.setTag(i);
            tlPolicy.addView(row, i);
        }
        tlPolicy.addView(trHeading, 0);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onInteraction(String sMessage, int nCase, String sActivityName) {
        switch (sMessage) {
            case "UPDATE_TABLE_ROW":
                String[] saData = sActivityName.split(",");
                int count = tlPolicy.getChildCount();
                row = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                TextView tvSlNo = row.getChildAt(0).findViewById(R.id.tv_table_row_1);
                TextView tvEmissionNo = row.getChildAt(1).findViewById(R.id.tv_table_row_2);
                TextView tvEmissionProvider = row.getChildAt(2).findViewById(R.id.tv_table_row_3);
                TextView tvStartDate = row.getChildAt(3).findViewById(R.id.tv_table_row_4);
                TextView tvExpiryDate = row.getChildAt(4).findViewById(R.id.tv_table_row_5);
                TextView tvRemainderDate = row.getChildAt(5).findViewById(R.id.tv_table_row_6);
                tvSlNo.setText(String.valueOf(count));
                tvEmissionNo.setText(saData[0]);
                tvEmissionProvider.setText(saData[1]);
                tvStartDate.setText(saData[2]);
                tvExpiryDate.setText(saData[3]);
                tvRemainderDate.setText(saData[4]);
                tlPolicy.addView(row);
                break;
        }
    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>> lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {

    }

    /*@Override
    public void onBitmapCompressed(String sMessage, int nCase, Bitmap bitmap, Intent intent, Uri outputFileUri) {
        switch (sMessage) {
            *//*case "SHOW_PROGRESS_BAR":
                showProgressBar();
                break;*//*
            case "BITMAP_COMPRESSED": //this is triggered from ImageUtils, which gets compressed bitmap and the same is sent back to Dialog Multiple
                DialogMultiple.mListener.onBitmapCompressed("SET_BITMAP", 1, bitmap, null, null);
                //ivPreview.setImageBitmap(bitmap);
                //stopProgressBar();
                break;
        }
    }*/
}
