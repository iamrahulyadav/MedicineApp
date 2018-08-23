package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PreMedicineData;

import java.util.ArrayList;

public class PreMedicineItemAdapter extends RecyclerView.Adapter<PreMedicineItemAdapter.ViewHolder> {

    private static final String TAG = "PreMedicineItemAdapter";
    Context context;
    ArrayList<PreMedicineData> arrayList;


    public PreMedicineItemAdapter(Context context, ArrayList<PreMedicineData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premedicine_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        PreMedicineData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvMedType.setText(data.getType());
        holder.tvMedName.setText(data.getName());
        holder.tvMedDescription.setText(data.getDescription());
        holder.tvMedQty.setText(data.getQuantity());
        holder.tvMedDoses.setText(data.getDoses());
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedType, tvMedName, tvMedDescription, tvMedQty, tvMedDoses;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMedType = (TextView) itemView.findViewById(R.id.tvMedType);
            tvMedName = (TextView) itemView.findViewById(R.id.tvMedName);
            tvMedDescription = (TextView) itemView.findViewById(R.id.tvMedDescription);
            tvMedQty = (TextView) itemView.findViewById(R.id.tvMedQty);
            tvMedDoses = (TextView) itemView.findViewById(R.id.tvMedDoses);
        }
    }
}
