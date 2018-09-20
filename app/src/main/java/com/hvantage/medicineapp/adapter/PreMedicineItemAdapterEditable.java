package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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

public class PreMedicineItemAdapterEditable extends RecyclerView.Adapter<PreMedicineItemAdapterEditable.ViewHolder> {

    private static final String TAG = "PreMedicineItemAdapter";
    private MyAdapterListenerEditable listener;
    Context context;
    ArrayList<PreMedicineData> arrayList;


    public PreMedicineItemAdapterEditable(Context context, ArrayList<PreMedicineData> arrayList, MyAdapterListenerEditable listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.premedicine_item_layout_editable, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PreMedicineData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        if (data != null) {
            holder.tvMedType.setText(data.getType());
            holder.tvMedName.setText(data.getName());
            holder.tvMedManufacturer.setText(data.getManufacturer());
            holder.tvMedDescription.setText(data.getDescription());
            holder.etMedQty.setText(data.getQuantity());
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

            } else {
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

            holder.etMedQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.editItem(null, position, s + "");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedType, tvMedName, tvMedManufacturer, tvMedDescription, tvMedDoses;
        ImageView imgRemove, imgHideShow;
        LinearLayout llHideShow;
        AppCompatEditText etMedQty;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMedType = (TextView) itemView.findViewById(R.id.tvMedType);
            tvMedName = (TextView) itemView.findViewById(R.id.tvMedName);
            tvMedManufacturer = (TextView) itemView.findViewById(R.id.tvMedManufacturer);
            tvMedDescription = (TextView) itemView.findViewById(R.id.tvMedDescription);
            etMedQty = (AppCompatEditText) itemView.findViewById(R.id.etMedQty);
            tvMedDoses = (TextView) itemView.findViewById(R.id.tvMedDoses);
            imgRemove = (ImageView) itemView.findViewById(R.id.imgRemove);
            imgHideShow = (ImageView) itemView.findViewById(R.id.imgHideShow);
            llHideShow = (LinearLayout) itemView.findViewById(R.id.llHideShow);
        }
    }

    public interface MyAdapterListenerEditable {
        void removeItem(View v, int position);

        void editItem(View v, int position, String qty);

    }

}
