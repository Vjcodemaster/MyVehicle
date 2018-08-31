package com.autochip.myvehicle;

import android.content.Context;
import android.graphics.Bitmap;
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

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

public class RemainderRVAdapter extends RecyclerView.Adapter<RemainderRVAdapter.RemainderTabHolder> {

    //private static final int UNSELECTED = -1;

    //private int selectedItem = UNSELECTED;

    //View previousView;
    private Context context;
    RecyclerView recyclerView;
    private ArrayList<String> alMakeModel;
    private ArrayList<String> alRegNo;
    private ArrayList<String> alYearOfManufacture;
    private ArrayList<Integer> alID;
    private ArrayList<Bitmap> alDisplayPicture;

    RemainderRVAdapter(Context context, RecyclerView recyclerView, ArrayList<Integer> alID, ArrayList<String> alMakeModel, ArrayList<String> alRegNo,
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
    public RemainderTabHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_remainder, parent, false);

        return new RemainderTabHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemainderTabHolder holder, final int position) {

        holder.tvMakeModel.setText(alMakeModel.get(position));
        holder.tvRegNo.setText(alRegNo.get(position));
        holder.tvYOM.setText(alYearOfManufacture.get(position));

        if (alDisplayPicture.get(position) != null) {
            holder.ivCircularDp.setImageBitmap(alDisplayPicture.get(position));
        }
        holder.llParentExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.homeInterfaceListener.onHomeCalled("VIEW_VEHICLE_INFO", position, String.valueOf(alID.get(position)), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alID.size(); //alBeaconInfo.size()
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class RemainderTabHolder extends RecyclerView.ViewHolder {
        TextView tvMakeModel;
        TextView tvRegNo;
        TextView tvYOM;

        private LinearLayout llParentExpand;
        private ExpandableLayout expandableLayout;
        Button btnEdit, btnRemove;

        ImageView ivCircularDp;

        RemainderTabHolder(View itemView) {
            super(itemView);
            tvMakeModel = itemView.findViewById(R.id.tv_make_model_rv);
            //tvRVNumber = (TextView) itemView.findViewById(R.id.recent_rc_number);
            tvRegNo = itemView.findViewById(R.id.tv_reg_no_rv);
            tvYOM = itemView.findViewById(R.id.tv_yom_rv);

            ivCircularDp = itemView.findViewById(R.id.rv_my_vehicle_list_avatar);

            llParentExpand = itemView.findViewById(R.id.ll_parent_expand);
            expandableLayout = itemView.findViewById(R.id.rv_admin_expandable_layout);
            btnEdit = expandableLayout.findViewById(R.id.btn_edit);
            btnRemove = expandableLayout.findViewById(R.id.btn_remove);

        }
    }

}
