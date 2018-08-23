package com.autochip.myvehicle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import app_utility.DataBaseHelper;
import app_utility.DatabaseHandler;
import app_utility.SharedPreferenceClass;
import dialogs.DialogMultiple;

import static com.autochip.myvehicle.MainActivity.editModeVehicleID;
import static com.autochip.myvehicle.MainActivity.mBitmapCompressListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.autochip.myvehicle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServiceHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceHistoryFragment extends Fragment implements OnFragmentInteractionListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static OnFragmentInteractionListener mListener;
    DatabaseHandler databaseHandler;

    FloatingActionButton fab;
    TableLayout tlPolicy;
    TableRow[] rows;
    Button[] baButtonDelete;
    TableRow row;
    private boolean isInEditMode = false;
    LayoutInflater layoutInflater;

    ArrayList<DataBaseHelper> alDBData;

    private int viewHeight = 0;
    int rowLength = 0;

    private SharedPreferenceClass sharedPreferenceClass;

    DialogMultiple dialogMultiple;

    public ServiceHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RCFCFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceHistoryFragment newInstance(String param1, String param2) {
        ServiceHistoryFragment fragment = new ServiceHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListener =  this;
        databaseHandler = new DatabaseHandler(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            viewHeight = Integer.valueOf(mParam1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layoutInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_service_history, container, false);
        sharedPreferenceClass = new SharedPreferenceClass(getActivity());
        tlPolicy = view.findViewById(R.id.tl_policy);

        dialogMultiple = new DialogMultiple(getActivity(), 4, mBitmapCompressListener);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMultiple.onCreate(4);
                dialogMultiple.dialog.show();
            }
        });

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


        TableRow trHeading = (TableRow) inflater.inflate(R.layout.table_row_service_heading, container, false);
        trHeading.setTag(-1);
        rows = new TableRow[1];
        baButtonDelete = new Button[1];
        for (int i = 0; i <rowLength; i++) { // for (int i = 0; i < rows.length; i++) {
            //LayoutInflater trInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = (TableRow) inflater.inflate(R.layout.table_row, null);
            TextView tv = row.findViewById(R.id.tv_table_row_7);
            tv.setVisibility(View.VISIBLE);
            baButtonDelete[i] = row.findViewById(R.id.btn_table_row_delete);
            rows[i] = row;
            rows[i].setTag(i);
            final int finalI = i;
            if (isInEditMode) {
                rows[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int nRowIndex = Integer.valueOf(rows[finalI].getTag().toString()); //index of row
                        dialogMultiple.onCreate(4);
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

    private void prepareDialogToEdit(final int index) {
        //TextView tv;
        row = rows[index];
        String[] saServiceData = alDBData.get(index).get_service_info().split(",");

        String sRONo = saServiceData[1];

        String sServiceType = saServiceData[2];

        String sMileage = saServiceData[3];

        String sDate = saServiceData[4];

        String sNextDueDate = saServiceData[4];

        String sRemainderDate = saServiceData[4];


        //String sRemainderDate = saRCFCData[5];

        dialogMultiple.tvTitle.setText(getActivity().getResources().getString(R.string.title_edit_insurance));
        dialogMultiple.etCustomOne.getEditText().setText(sRONo);
        dialogMultiple.etCustomTwo.getEditText().setText(sServiceType);
        dialogMultiple.etCustomThree.getEditText().setText(sMileage);
        dialogMultiple.tvStartDateValue.setText(sDate);
        dialogMultiple.tvExpiryDateValue.setText(sNextDueDate);
        dialogMultiple.tvRemainderDateValue.setText(sRemainderDate);
        dialogMultiple.llDateValue.setVisibility(View.VISIBLE);
    }


    private void loadDataToTable(final int index){
        TextView tv;
        row = rows[index];
        if(alDBData!=null && alDBData.get(index).get_service_info()!=null) {
            String[] saServiceData = alDBData.get(index).get_service_info().split(",");

            tv = row.findViewById(R.id.tv_table_row_1);
            tv.setText("1");
            tv = row.findViewById(R.id.tv_table_row_2);
            String sRONo = saServiceData[1];
            tv.setText(sRONo);

            tv = row.findViewById(R.id.tv_table_row_3);
            String sServiceType = saServiceData[2];
            tv.setText(sServiceType);

            tv = row.findViewById(R.id.tv_table_row_4);
            String sMileage = saServiceData[3];
            tv.setText(sMileage);

            tv = row.findViewById(R.id.tv_table_row_5);
            String sDate = saServiceData[4];
            tv.setText(sDate);

            tv = row.findViewById(R.id.tv_table_row_6);
            String sNextDueDate = saServiceData[4];
            tv.setText(sNextDueDate);

            tv = row.findViewById(R.id.tv_table_row_7);
            String sReminderDate = saServiceData[4];
            tv.setText(sReminderDate);
        }
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
                TextView tvRoNo = row.getChildAt(1).findViewById(R.id.tv_table_row_2);
                TextView tvServiceType = row.getChildAt(2).findViewById(R.id.tv_table_row_3);
                TextView tvMileage = row.getChildAt(3).findViewById(R.id.tv_table_row_4);
                TextView tvDate = row.getChildAt(4).findViewById(R.id.tv_table_row_5);

                TextView tvNextDueDate = row.getChildAt(5).findViewById(R.id.tv_table_row_6);
                TextView tvRemainderDate = row.getChildAt(6).findViewById(R.id.tv_table_row_7);
                tvNextDueDate.setVisibility(View.VISIBLE);
                tvRemainderDate.setVisibility(View.VISIBLE);

                tvRoNo.setText(saData[0]);
                tvServiceType.setText(saData[1]);
                tvMileage.setText(saData[2]);
                tvDate.setText(saData[3]);
                tvNextDueDate.setText(saData[4]);
                tvRemainderDate.setText(saData[5]);

                if (!isInEditMode) {
                    tlPolicy.addView(row);
                }
        }
    }

    @Override
    public void onRegisterVehicleFragment(String sMessage, int nCase, LinkedHashMap<String, ArrayList<String>> lHMFormatData, LinkedHashMap<String, LinkedHashMap<Integer, ArrayList<Integer>>> lHMBrandNameWithIDAndModelID) {

    }
}
