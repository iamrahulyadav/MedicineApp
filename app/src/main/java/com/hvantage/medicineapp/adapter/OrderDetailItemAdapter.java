package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.Item;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.List;

public class OrderDetailItemAdapter extends RecyclerView.Adapter<OrderDetailItemAdapter.ViewHolder> {

    private static final String TAG = "CartItemAdapter";
    Context context;
    List<Item> arrayList;
    private ProgressBar progressBar;


    public OrderDetailItemAdapter(Context context, List<Item> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Item data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getItem());
        holder.tvQty.setText("Quantity : " + data.getQty());
        holder.tvPrice.setText("Rs. " + Functions.roundTwoDecimals(Double.parseDouble(data.getItemTotalPrice())));
        holder.tvTotalPrice.setText("Rs. " + Functions.roundTwoDecimals(Double.parseDouble(data.getItemTotalPrice())));
        /*if (!data.getImage().equalsIgnoreCase(""))
            holder.imageThumb.setImageBitmap(Functions.base64ToBitmap(data.getImage()));
        else*/

        holder.imageThumb.setImageResource(R.drawable.no_image_placeholder);

    }

    private void showProgressDialog() {
        progressBar = ProgressBar.show(context, "Processing...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void hideProgressDialog() {
        if (progressBar != null)
            progressBar.dismiss();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice, tvTotalPrice, tvQty;
        ImageView imageThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvTotalPrice = (TextView) itemView.findViewById(R.id.tvTotalPrice);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            imageThumb = (ImageView) itemView.findViewById(R.id.imageThumb);
        }
    }
}
