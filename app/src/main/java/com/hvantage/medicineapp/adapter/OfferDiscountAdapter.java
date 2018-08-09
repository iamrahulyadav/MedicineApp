package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.OffersModel;

import java.util.ArrayList;

public class OfferDiscountAdapter extends RecyclerView.Adapter<OfferDiscountAdapter.ViewHolder> {

    private static final String TAG = "OfferDiscountAdapter";
    Context context;
    ArrayList<OffersModel> arrayList;


    public OfferDiscountAdapter(Context context, ArrayList<OffersModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_discount_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final OffersModel data = arrayList.get(position);
        if (!data.getCoupon().equalsIgnoreCase("")) {
            holder.tvCode.setVisibility(View.VISIBLE);
            holder.tvCode.setText(data.getCoupon());
        } else {
            holder.tvCode.setVisibility(View.GONE);
        }
        holder.tvTitle.setText(data.getTitle());
        holder.tvDate.setText("(valid till - " + data.getValidity() + ")");
        holder.tvDesciption.setText(data.getDesciption());

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvCode, tvDesciption, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvCode = (TextView) itemView.findViewById(R.id.tvCode);
            tvDesciption = (TextView) itemView.findViewById(R.id.tvDesciption);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }
}
