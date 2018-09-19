package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PreMedicineData;

import java.util.ArrayList;

public class PreMedicineItemAdapterMain extends RecyclerView.Adapter<PreMedicineItemAdapterMain.ViewHolder> {

    private static final String TAG = "PreMedicineItemAdapter";
    private MyAdapterListener listener;
    Context context;
    ArrayList<PreMedicineData> arrayList;


    public PreMedicineItemAdapterMain(Context context, ArrayList<PreMedicineData> arrayList, MyAdapterListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premedicine_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PreMedicineData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvMedType.setText(data.getType());
        holder.tvMedName.setText(data.getName());
        holder.tvMedManufacturer.setText(data.getManufacturer());
        holder.tvMedDescription.setText(data.getDescription());
        holder.tvMedQty.setText(data.getQuantity());
        holder.tvMedDoses.setText(data.getDoses());
        if (listener != null) {
            holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(context)
                            .setMessage("Remove " + data.getName())
                            .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.removeItem(v, position);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();

                }
            });

            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    listener.editItem(v, position);
                }
            });

        } else {
            holder.imgEdit.setVisibility(View.GONE);
            holder.imgRemove.setVisibility(View.GONE);
        }

        holder.imgHideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.llHideShow.getVisibility() == View.VISIBLE) {
                    holder.llHideShow.setVisibility(View.GONE);
                    holder.imgHideShow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                } else {
                    holder.llHideShow.setVisibility(View.VISIBLE);
                    holder.imgHideShow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedType, tvMedName, tvMedManufacturer, tvMedDescription, tvMedQty, tvMedDoses;
        ImageView imgRemove, imgEdit, imgHideShow;
        LinearLayout llHideShow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMedType = (TextView) itemView.findViewById(R.id.tvMedType);
            tvMedName = (TextView) itemView.findViewById(R.id.tvMedName);
            tvMedManufacturer = (TextView) itemView.findViewById(R.id.tvMedManufacturer);
            tvMedDescription = (TextView) itemView.findViewById(R.id.tvMedDescription);
            tvMedQty = (TextView) itemView.findViewById(R.id.tvMedQty);
            tvMedDoses = (TextView) itemView.findViewById(R.id.tvMedDoses);
            imgRemove = (ImageView) itemView.findViewById(R.id.imgRemove);
            imgEdit = (ImageView) itemView.findViewById(R.id.imgEdit);
            imgHideShow = (ImageView) itemView.findViewById(R.id.imgHideShow);
            llHideShow = (LinearLayout) itemView.findViewById(R.id.llHideShow);

        }
    }

    public interface MyAdapterListener {
        void removeItem(View v, int position);

        void editItem(View v, int position);

    }

}
