package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DoctorData;

import java.util.ArrayList;

public class MyDoctorAdapter extends RecyclerView.Adapter<MyDoctorAdapter.ViewHolder> {

    private static final String TAG = "CategoryAdapter";
    private final MyAdapterListener listener;
    Context context;
    ArrayList<DoctorData> arrayList;


    public MyDoctorAdapter(Context context, ArrayList<DoctorData> arrayList, MyAdapterListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DoctorData data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText("Dr. " + data.getName());
        holder.tvBG.setText(data.getSpecialization());
        holder.tvBG.setText(data.getSpecialization());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.delete(view, position);
            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.select(view, position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface MyAdapterListener {
        void delete(View v, int position);

        void select(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvBG;
        ImageButton btnDelete;
        CardView item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvBG = (TextView) itemView.findViewById(R.id.tvBG);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
            item = (CardView) itemView.findViewById(R.id.item);

        }
    }
}
