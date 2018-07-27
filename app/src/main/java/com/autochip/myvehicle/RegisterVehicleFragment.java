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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;


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
    private static final String ARG_LIST_MAKE = "ARG_LIST_MAKE";
    private static final String ARG_LIST_MODEL = "ARG_LIST_MODEL";

    private ArrayList<String> alMake = new ArrayList<>();
    private ArrayList<String> alModel = new ArrayList<>();

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    EditText etMake, etModel, etRegNo, etYOM;
    Spinner spinnerVehicle, spinnerMake, spinnerModel;

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
    public static RegisterVehicleFragment newInstance(String param1, String param2, ArrayList<String> alMake, ArrayList<String> alModel) {
        RegisterVehicleFragment fragment = new RegisterVehicleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putStringArrayList(ARG_LIST_MAKE, alMake);
        args.putStringArrayList(ARG_LIST_MODEL, alModel);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            alMake.add(getArguments().getString(ARG_LIST_MAKE));
            alModel.add(getArguments().getString(ARG_LIST_MODEL));
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

        spinnerVehicle= view.findViewById(R.id.spinner_vehicle);
        ArrayAdapter<String> adapterVehicle = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.vehicle_array));
        adapterVehicle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicle.setAdapter(adapterVehicle);

        spinnerMake = view.findViewById(R.id.spinner_make);
        ArrayAdapter<String> adapterMake = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, alMake);
        adapterMake.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMake.setAdapter(adapterMake);

        spinnerModel = view.findViewById(R.id.spinner_model);
        ArrayAdapter<String> adapterModel = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, alModel);
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
