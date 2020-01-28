package com.autochip.myvehicle;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import app_utility.BitmapBase64;
import app_utility.CircleImageView;

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
    private ArrayList<Bitmap> alDisplayPicture = new ArrayList<>();
    private LinkedHashMap<Integer, ArrayList<String>> lhmRemainderData;
    private ArrayList<ArrayList<String>> alExtractedRemainderData = new ArrayList<>();

    RemainderRVAdapter(Context context, RecyclerView recyclerView, LinkedHashMap<Integer, ArrayList<String>> lhmRemainderData) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.lhmRemainderData = lhmRemainderData;
        this.alExtractedRemainderData.addAll(this.lhmRemainderData.values());
    }


    @NonNull
    @Override
    public RemainderTabHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_remainder, parent, false);

        return new RemainderTabHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RemainderTabHolder holder, final int position) {
        holder.tvHeading.setText(String.valueOf(position + 1));
        holder.tvBrandName.setText(alExtractedRemainderData.get(position).get(1));
        holder.tvModelName.setText(alExtractedRemainderData.get(position).get(2));
        holder.tvRegNo.setText(alExtractedRemainderData.get(position).get(4));
        holder.tvYOM.setText(alExtractedRemainderData.get(position).get(5));

        if (!alExtractedRemainderData.get(position).get(3).equals("")) {
            Bitmap bitmap = BitmapBase64.convertToBitmap(alExtractedRemainderData.get(position).get(3));
            //alDisplayPicture.add(position, bitmap);
            holder.civDP.setImageBitmap(bitmap);
        }

        if (alExtractedRemainderData.get(position).size() > 6) {
            ArrayList<String[]> alHistoryDates = new ArrayList<>();
            String[] saDates = alExtractedRemainderData.get(position).get(6).split(",");
            alHistoryDates.add(saDates);

            if (alExtractedRemainderData.get(position).size() > 7) {
                saDates = alExtractedRemainderData.get(position).get(7).split(",");
                alHistoryDates.add(saDates);
            }

            if (alExtractedRemainderData.get(position).size() > 8) {
                saDates = alExtractedRemainderData.get(position).get(8).split(",");
                alHistoryDates.add(saDates);
            }

            for (int i = 0; i < alHistoryDates.size(); i++) {
                //saDates = alExtractedRemainderData.get(position).get(6).split(",");
                //alHistoryDates.add(saDates);
                saDates = alHistoryDates.get(i);
                int nCase = Integer.valueOf(saDates[0]);
                switch (nCase) {
                    case 0:
                        holder.tvInsuranceExpiryValue.setText(saDates[1]);
                        holder.tvInsuranceExpiryValue.setVisibility(View.VISIBLE);
                        holder.tvInsuranceExpiry.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        holder.tvEmissionExpiryValue.setText(saDates[1]);
                        holder.tvEmissionExpiryValue.setVisibility(View.VISIBLE);
                        holder.tvEmissionExpiry.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        holder.tvServiceDueValue.setText(saDates[1]);
                        holder.tvServiceDueValue.setVisibility(View.VISIBLE);
                        holder.tvServiceDue.setVisibility(View.VISIBLE);
                        break;
                }
            }

            //String[] saDate = alExtractedRemainderData.get(position).get(6).split(",");
            //int nCase = Integer.valueOf(saDate[0]);

        }
        /*holder.llParentExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.homeInterfaceListener.onHomeCalled("VIEW_VEHICLE_INFO", position, String.valueOf(alID.get(position)), null);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return alExtractedRemainderData.size(); //alBeaconInfo.size()
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class RemainderTabHolder extends RecyclerView.ViewHolder {
        CircleImageView civDP;
        ImageButton ibEdit;
        ImageButton ibClose;
        TextView tvBrandName;
        TextView tvModelName;
        TextView tvRegNo;
        TextView tvYOM;
        TextView tvHeading;
        TextView tvInsuranceExpiry;
        TextView tvEmissionExpiry;
        TextView tvServiceDue;
        TextView tvInsuranceExpiryValue;
        TextView tvEmissionExpiryValue;
        TextView tvServiceDueValue;
        //private LinearLayout llParentExpand;
        //private ExpandableLayout expandableLayout;
        //Button btnEdit, btnRemove;

        //ImageView ivCircularDp;

        RemainderTabHolder(View itemView) {
            super(itemView);
            civDP = itemView.findViewById(R.id.civ_dp);
            ibEdit = itemView.findViewById(R.id.ib_edit);
            ibClose = itemView.findViewById(R.id.ib_close);
            tvBrandName = itemView.findViewById(R.id.tv_brand_name);
            tvModelName = itemView.findViewById(R.id.tv_model_name);
            tvRegNo = itemView.findViewById(R.id.tv_license_plate);
            tvYOM = itemView.findViewById(R.id.tv_yom);
            tvHeading = itemView.findViewById(R.id.tv_heading);
            tvInsuranceExpiry = itemView.findViewById(R.id.tv_insurance_expiry);
            tvEmissionExpiry = itemView.findViewById(R.id.tv_emission_expiry);
            tvServiceDue = itemView.findViewById(R.id.tv_service_due);
            tvInsuranceExpiryValue = itemView.findViewById(R.id.tv_insurance_expiry_value);
            tvEmissionExpiryValue = itemView.findViewById(R.id.tv_emission_expiry_value);
            tvServiceDueValue = itemView.findViewById(R.id.tv_service_due_value);

            ibEdit.setVisibility(View.GONE);
            ibClose.setVisibility(View.GONE);
        }
    }

}
