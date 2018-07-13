package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.FamilyModel;

import java.util.ArrayList;

public class MyFamilyAdapter extends RecyclerView.Adapter<MyFamilyAdapter.ViewHolder> {

    private static final String TAG = "CategoryAdapter";
    Context context;
    ArrayList<FamilyModel> arrayList;


    public MyFamilyAdapter(Context context, ArrayList<FamilyModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FamilyModel data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvRelation.setText("(" + data.getRelation() + ")");
        holder.tvBG.setText(data.getBlood_group());


    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvRelation, tvBG;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvRelation = (TextView) itemView.findViewById(R.id.tvRelation);
            tvBG = (TextView) itemView.findViewById(R.id.tvBG);
        }
    }
}
