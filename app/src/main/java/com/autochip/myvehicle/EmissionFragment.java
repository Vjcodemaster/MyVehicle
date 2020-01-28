package com.autochip.myvehicle;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.SharedPreferenceClass;
import dialogs.DialogMultiple;

import static com.autochip.myvehicle.MainActivity.editModeVehicleID;


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
    int rowLength = 0;
    File sdImageMainDirectory;

    public static OnFragmentInteractionListener mListener;

    DialogMultiple dialogMultiple;
    SharedPreferenceClass sharedPreferenceClass;

    DatabaseHandler databaseHandler;
    ArrayList<DataBaseHelper> alDBData;

    private boolean isInEditMode = false;

    TableRow[] rows;
    Button[] baButtonDelete;

    String[] saEmissionData;
    //TableRow row;

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
        databaseHandler = new DatabaseHandler(getActivity());
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
        isInEditMode = sharedPreferenceClass.getEditModeStatus();
        if (!isInEditMode) { //!sharedPreferenceClass.getEditModeStatus()
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
            params.bottomMargin = viewHeight + 6;
            fab.setLayoutParams(params);
            rowLength = 0;
        } else {
            alDBData = new ArrayList<>(databaseHandler.getSingleVehicleHistoryByVehicleID(editModeVehicleID));
            fab.setVisibility(View.GONE);
            rowLength = 1;
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
        trHeading.setTag(-1);
        rows = new TableRow[1];
        baButtonDelete = new Button[5];
        for (int i = 0; i < rowLength; i++) {
            //LayoutInflater trInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = (TableRow) inflater.inflate(R.layout.table_row, null);
            baButtonDelete[i] = row.findViewById(R.id.btn_table_row_delete);
            rows[i] = row;
            rows[i].setTag(i);
            final int finalI = i;
            if (isInEditMode) {
                rows[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int nRowIndex = Integer.valueOf(rows[finalI].getTag().toString()); //index of row
                        dialogMultiple.onCreate(2);
                        prepareDialogToEdit(nRowIndex);
                        dialogMultiple.dialog.show();
                    }
                });

                baButtonDelete[finalI].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        row = rows[finalI];
                        rows[finalI].removeAllViews();
                        tlPolicy.removeView(row);
                    }
                });
            } else {
                baButtonDelete[finalI].setVisibility(View.GONE);
            }
            loadDataToTable(i);
            tlPolicy.addView(rows[i], i);
        }
        tlPolicy.addView(trHeading, 0);
        return view;
    }

    private void loadDataToTable(final int index){
        TextView tv;
        row = rows[index];
        if(alDBData.size()>=1 && alDBData.get(index).get_emission_info()!=null) {
            saEmissionData = alDBData.get(index).get_emission_info().split(",");

            tv = row.findViewById(R.id.tv_table_row_1);
            tv.setText("1");

            tv = row.findViewById(R.id.tv_table_row_2);
            String sEmissionNo = saEmissionData[1];
            tv.setText(sEmissionNo);

            tv = row.findViewById(R.id.tv_table_row_3);
            String sAgencyName = saEmissionData[2];
            tv.setText(sAgencyName);

            tv = row.findViewById(R.id.tv_table_row_4);
            String sStartDate = saEmissionData[3];
            tv.setText(sStartDate);

            tv = row.findViewById(R.id.tv_table_row_5);
            String sExpiryDate = saEmissionData[4];
            tv.setText(sExpiryDate);

            tv = row.findViewById(R.id.tv_table_row_6);
            String sRemainderDate = saEmissionData[5];
            tv.setText(sRemainderDate);
        }
    }

    private void prepareDialogToEdit(final int index) {
        //TextView tv;
        row = rows[index];

        //tv = row.findViewById(R.id.tv_table_row_2);
        String sEmissionNo = saEmissionData[1];

        //tv = row.findViewById(R.id.tv_table_row_3);
        String sAgencyName = saEmissionData[2];

        //tv = row.findViewById(R.id.tv_table_row_4);
        String sStartDate = saEmissionData[3];

        //tv = row.findViewById(R.id.tv_table_row_5);
        String sExpiryDate = saEmissionData[4];

        //tv = row.findViewById(R.id.tv_table_row_6);
        String sRemainderDate = saEmissionData[5];

        dialogMultiple.tvTitle.setText(getActivity().getResources().getString(R.string.title_edit_emission));
        dialogMultiple.etCustomOne.getEditText().setText(sEmissionNo);
        dialogMultiple.etCustomTwo.getEditText().setText(sAgencyName);
        dialogMultiple.tvStartDateValue.setText(sStartDate);
        dialogMultiple.tvExpiryDateValue.setText(sExpiryDate);
        dialogMultiple.tvRemainderDateValue.setText(sRemainderDate);
        dialogMultiple.llDateValue.setVisibility(View.VISIBLE);
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
            case "ADD_TABLE_ROW":
                String[] saData = sActivityName.split(",");
                if (!isInEditMode) {
                    int count = tlPolicy.getChildCount();
                    row = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                    TextView tvSlNo = row.getChildAt(0).findViewById(R.id.tv_table_row_1);
                    tvSlNo.setText(String.valueOf(count));
                }
                TextView tvEmissionNo = row.getChildAt(1).findViewById(R.id.tv_table_row_2);
                TextView tvEmissionProvider = row.getChildAt(2).findViewById(R.id.tv_table_row_3);
                TextView tvStartDate = row.getChildAt(3).findViewById(R.id.tv_table_row_4);
                TextView tvExpiryDate = row.getChildAt(4).findViewById(R.id.tv_table_row_5);
                TextView tvRemainderDate = row.getChildAt(5).findViewById(R.id.tv_table_row_6);

                tvEmissionNo.setText(saData[0]);
                tvEmissionProvider.setText(saData[1]);
                tvStartDate.setText(saData[2]);
                tvExpiryDate.setText(saData[3]);
                tvRemainderDate.setText(saData[4]);

                if (!isInEditMode) {
                    tlPolicy.addView(row);
                }
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
