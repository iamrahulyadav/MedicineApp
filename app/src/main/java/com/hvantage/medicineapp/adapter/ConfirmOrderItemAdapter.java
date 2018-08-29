package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.util.Functions;

import java.util.ArrayList;

public class ConfirmOrderItemAdapter extends RecyclerView.Adapter<ConfirmOrderItemAdapter.ViewHolder> {

    private static final String TAG = "CartItemAdapter";
    Context context;
    ArrayList<CartData> arrayList;

    public ConfirmOrderItemAdapter(Context context, ArrayList<CartData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_order_cart_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final CartData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getItem());
        holder.tvQty.setText("Qty. " + data.getQty());
        holder.tvTotalPrice.setText("Rs. " + Functions.roundTwoDecimals(data.getItem_total_price()));
        if (!data.getImage().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(data.getImage())
                    .crossFade()
                    .into(holder.imageThumb);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTotalPrice, tvQty;
        ImageView imageThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvTotalPrice = (TextView) itemView.findViewById(R.id.tvTotalPrice);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            imageThumb = (ImageView) itemView.findViewById(R.id.imageThumb);
        }
    }
}
