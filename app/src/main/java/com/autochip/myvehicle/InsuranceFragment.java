package com.autochip.myvehicle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.autochip.myvehicle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsuranceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsuranceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private int viewHeight;

    Dialog dialog;
    TextView tvStartDate, tvExpiryDate, tvRemainderDate;
    final Calendar myCalendar = Calendar.getInstance();

    FloatingActionButton fab;
    TableLayout tlPolicy;

    private OnFragmentInteractionListener mListener;

    public InsuranceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InsuranceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InsuranceFragment newInstance(String param1, String param2) {
        InsuranceFragment fragment = new InsuranceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            viewHeight = Integer.valueOf(mParam1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insurance, container, false);

        tlPolicy = view.findViewById(R.id.tl_policy);
        fab = view.findViewById(R.id.fab);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
        params.bottomMargin = viewHeight + 6;
        fab.setLayoutParams(params);

        initReadMoreDialog();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        /*
        inflating views dynamically for table layout where we always add the data dynamically from odoo not from xml
        here we are inflating table rows first using for loop depending on the data we receive and then add table row header
        by inflating it and setting it to 0th index of Table Layout tlPolicy.addView(trHeading, 0);. to set text we can use,
        TextView tv = (TextView) row.childAt(0);| tv.setText("Text"); or row.findViewById()
         */
        TableRow trHeading = (TableRow)inflater.inflate(R.layout.table_row_heading, null);

        for(int i = 0; i < 6; i ++){
            LayoutInflater trInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TableRow row = (TableRow) trInflater.inflate(R.layout.table_row,null);
            row.setTag(i);
            tlPolicy.addView(row,i);
        }
        tlPolicy.addView(trHeading, 0);
        // Inflate the layout for this fragment
        return view;
    }

    public void initReadMoreDialog() {
        dialog = new Dialog(getActivity(), R.style.CustomDialogTheme90);
        dialog.setContentView(R.layout.dialog_add_insurance_policy);
        dialog.setCancelable(true);

        tvStartDate = dialog.findViewById(R.id.tv_start_date);
        tvExpiryDate = dialog.findViewById(R.id.tv_expiry_date);
        tvRemainderDate = dialog.findViewById(R.id.tv_remainder_date);

        View.OnClickListener l = new View.OnClickListener()
        {
            TextView textView;
            @Override
            public void onClick(View v)
            {
                switch (v.getId()){
                    case R.id.tv_start_date:
                        textView = tvStartDate;
                        break;
                    case R.id.tv_expiry_date:
                        textView = tvExpiryDate;
                        break;
                    case R.id.tv_remainder_date:
                        textView = tvRemainderDate;
                        break;

                }

                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(textView);
                    }

                };
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        };

        tvStartDate.setOnClickListener(l);

        tvExpiryDate.setOnClickListener(l);

        tvRemainderDate.setOnClickListener(l);

        /*TextView tvHeading = (TextView) dialog.findViewById(R.id.tv_readmore_heading);
        TextView tvSubHeading = (TextView) dialog.findViewById(R.id.tv_readmore_sub_heading);
        TextView tvDescription = (TextView) dialog.findViewById(R.id.tv_readmore_description);
        Typeface lightFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_light.ttf");
        Typeface regularFace = Typeface.createFromAsset(getResources().getAssets(), "fonts/myriad_pro_regular.ttf");
        tvHeading.setTypeface(regularFace);
        tvSubHeading.setTypeface(lightFace);
        tvDescription.setTypeface(lightFace);*/
    }

    private void updateLabel(TextView textView) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textView.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
