package com.autochip.myvehicle;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MyVehicleTrackingRVAdapter extends RecyclerView.Adapter<MyVehicleTrackingRVAdapter.MyVehicleHolder> {

    private Context context;
    RecyclerView recyclerView;
    private ArrayList<String> alMakeModel;
    private ArrayList<String> alRegNo;
    private ArrayList<Integer> alYearOfManufacture;

    MyVehicleTrackingRVAdapter(Context context, RecyclerView recyclerView, ArrayList<String> alMakeModel, ArrayList<String> alRegNo,
                               ArrayList<Integer> alYearOfManufacture) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.alMakeModel = alMakeModel;
        this.alRegNo = alRegNo;
        this.alYearOfManufacture = alYearOfManufacture;
    }


    @NonNull
    @Override
    public MyVehicleHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_my_vehicle, parent, false);
        return new MyVehicleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyVehicleHolder holder, final int position) {
        holder.tvMakeModel.setText(alMakeModel.get(position));
        holder.tvRegNo.setText(alRegNo.get(position));
        holder.tvYOM.setText(String.valueOf(alYearOfManufacture.get(position)));
    }

    @Override
    public int getItemCount() {
        return alMakeModel.size(); //alBeaconInfo.size()
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyVehicleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvMakeModel;
        TextView tvRegNo;
        TextView tvYOM;
        TextView tvRVTime;
        TextView tvRVNumber;
        ImageView mImageView;
        TextView tvRvName;
        TextView tvRvDesignation;
        CardView cvExpand;
        private LinearLayout llParentExpand;
        boolean isSelected;
        Button btnTrack, btnRemove;
        //TextView tvTrack;
        int position;
        ImageView ivOnline;
        //ExpandableLayout expandableLayout;
        //TextView tvExpand;

        public MyVehicleHolder(View itemView) {
            super(itemView);
            //tvRVEmail = (TextView) itemView.findViewById(R.id.recent_rc_email);
            tvMakeModel = itemView.findViewById(R.id.tv_make_model_rv);
            //tvRVNumber = (TextView) itemView.findViewById(R.id.recent_rc_number);
            tvRegNo = itemView.findViewById(R.id.tv_reg_no_rv);
            tvYOM = itemView.findViewById(R.id.tv_yom_rv);
        }

        @Override
        public void onClick(View view) {

        }
    }}
