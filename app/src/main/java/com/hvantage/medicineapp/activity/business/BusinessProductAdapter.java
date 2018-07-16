package com.hvantage.medicineapp.activity.business;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DrugModel;

import java.util.ArrayList;

public class BusinessProductAdapter extends RecyclerView.Adapter<BusinessProductAdapter.ViewHolder> {

    private static final String TAG = "BusinessProductAdapter";
    Context context;
    ArrayList<DrugModel> arrayList;


    public BusinessProductAdapter(Context context, ArrayList<DrugModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_product_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DrugModel data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText( data.getName());

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvBG;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
