package com.autochip.myvehicle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.autochip.myvehicle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterVehicleFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    EditText etMake, etModel, etRegNo, etYOM;

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
    public static RegisterVehicleFragment newInstance(String param1, String param2) {
        RegisterVehicleFragment fragment = new RegisterVehicleFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_vehicle, container, false);


        init(view);
        //ColorStateList etViewColorStateList = new ColorStateList(editTextStates, editTextColors);
        //etMake.setTextColor(etViewColorStateList);



        return view;
    }

    private void init(View view){
        etMake = view.findViewById(R.id.et_make);
        etModel = view.findViewById(R.id.et_model);
        etRegNo = view.findViewById(R.id.et_reg_no);
        etYOM = view.findViewById(R.id.et_yom);
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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
