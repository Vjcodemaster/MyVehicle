package com.autochip.myvehicle;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import app_utility.MyVehicleAsyncTask;

public class MyVehicleTrackingRVAdapter extends RecyclerView.Adapter<MyVehicleTrackingRVAdapter.MyVehicleHolder> {

    private static final int UNSELECTED = -1;

    private int selectedItem = UNSELECTED;

    private Context context;
    RecyclerView recyclerView;
    private ArrayList<String> alMakeModel;
    private ArrayList<String> alRegNo;
    private ArrayList<String> alYearOfManufacture;
    private ArrayList<Integer> alID;
    private ArrayList<Bitmap> alDisplayPicture;

    MyVehicleTrackingRVAdapter(Context context, RecyclerView recyclerView, ArrayList<Integer> alID, ArrayList<String> alMakeModel, ArrayList<String> alRegNo,
                               ArrayList<String> alYearOfManufacture, ArrayList<Bitmap> alDisplayPicture) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.alID = alID;
        this.alMakeModel = alMakeModel;
        this.alRegNo = alRegNo;
        this.alYearOfManufacture = alYearOfManufacture;
        this.alDisplayPicture = alDisplayPicture;
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
        holder.tvYOM.setText(alYearOfManufacture.get(position));

        if (alDisplayPicture.get(position) != null) {
            holder.ivCircularDp.setImageBitmap(alDisplayPicture.get(position));
        }
        holder.bind(holder);
    }

    @Override
    public int getItemCount() {
        return alMakeModel.size(); //alBeaconInfo.size()
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyVehicleHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, ExpandableLayout.OnExpansionUpdateListener {
        TextView tvMakeModel;
        TextView tvRegNo;
        TextView tvYOM;

        private LinearLayout llParentExpand;
        private ExpandableLayout expandableLayout;
        Button btnEdit, btnRemove;

        ImageView ivCircularDp;

        TextView tvRVTime;
        TextView tvRVNumber;
        TextView tvRvName;
        TextView tvRvDesignation;
        CardView cvExpand;
        boolean isSelected;
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

            ivCircularDp = itemView.findViewById(R.id.rv_my_vehicle_list_avatar);

            llParentExpand = itemView.findViewById(R.id.ll_parent_expand);
            expandableLayout = itemView.findViewById(R.id.rv_admin_expandable_layout);
            btnEdit = expandableLayout.findViewById(R.id.btn_edit);
            btnRemove = expandableLayout.findViewById(R.id.btn_remove);

            expandableLayout.setInterpolator(new FastOutLinearInInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);

            //llParentExpand.setOnClickListener(this);
            llParentExpand.setOnLongClickListener(this);
        }

        void bind(final MyVehicleHolder holder) {
            position = getAdapterPosition();
            isSelected = position == selectedItem;

            llParentExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Toast.makeText(context, "clicked view :" + position, Toast.LENGTH_SHORT).show();
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.homeInterfaceListener.onHomeCalled("EDIT_VEHICLE", position, String.valueOf(alID.get(position)), null);
                }
            });

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyVehicleAsyncTask myVehicleAsyncTask = new MyVehicleAsyncTask(context);
                    myVehicleAsyncTask.execute(String.valueOf(7), String.valueOf(alID.get(position)), String.valueOf(position));

                    //mAdapterListener.onAdapterChange("REMOVE_POSITION", position);
                    //delete item from list
                }
            });

            llParentExpand.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

        /*@Override
        public void onClick(View view) {
            MyVehicleHolder holder = (MyVehicleHolder) recyclerView.findViewHolderForAdapterPosition(getAdapterPosition());
            if (holder != null) {
                holder.llParentExpand.setSelected(false);
                holder.expandableLayout.collapse();
            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                llParentExpand.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
        }*/

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            MyVehicleHolder holder = (MyVehicleHolder) recyclerView.findViewHolderForAdapterPosition(getAdapterPosition());
            if (holder != null) {
                holder.llParentExpand.setSelected(false);
                holder.expandableLayout.collapse();
            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;
            } else {
                llParentExpand.setSelected(true);
                expandableLayout.expand();
                selectedItem = position;
            }
            //return true means that the event is consumed. It is handled. No other click events will be notified.
            //return false means the event is not consumed. Any other click events will continue to receive notifications.
            return true;
        }
    }
}
