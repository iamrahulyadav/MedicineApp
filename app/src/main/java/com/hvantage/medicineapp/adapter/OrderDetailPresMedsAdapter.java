package com.hvantage.medicineapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PreMedicineData;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.List;

public class OrderDetailPresMedsAdapter extends RecyclerView.Adapter<OrderDetailPresMedsAdapter.ViewHolder> {

    private static final String TAG = "OrderDetailPresMedsAdapter";
    Context context;
    List<PreMedicineData> arrayList;
    private ProgressBar progressBar;

    public OrderDetailPresMedsAdapter(Context context, List<PreMedicineData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_pres_med_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PreMedicineData data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvQty.setText("Quantity : " + data.getQuantity());
        if (!data.getImage().equalsIgnoreCase(""))
            Glide.with(context)
                    .load(data.getImage())
                    .placeholder(R.drawable.no_image_placeholder)
                    .crossFade()
                    .into(holder.imageThumb);
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


    private void showPreviewDialog(String url) {

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
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
