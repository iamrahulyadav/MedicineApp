package com.hvantage.medicineapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.MainActivity;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.util.Functions;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {

    private static final String TAG = "CartItemAdapter";
    Context context;
    ArrayList<CartData> arrayList;

    public CartItemAdapter(Context context, ArrayList<CartData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final CartData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getItem());
        holder.tvQty.setText("" + data.getQty());
        holder.tvPrice.setText("Rs. " + Functions.roundTwoDecimals(data.getItem_price()));
        holder.tvTotalPrice.setText("Rs. " + Functions.roundTwoDecimals(data.getItem_total_price()));

        if (!data.getImage().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(data.getImage())
                    .crossFade()
                    .into(holder.imageThumb);
        }


        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tvQty.getText().toString());
                if (qty > 1) {
                    qty--;
                    holder.tvQty.setText(String.valueOf(qty));
                    double item_total = Functions.roundTwoDecimals(qty * data.getItem_price());
                    holder.tvTotalPrice.setText(String.valueOf(item_total));
                    CartData data1 = data;
                    data1.setQty(qty);
                    data1.setItem_total_price(item_total);
                    new DBHelper(context).updateCartItem(data1);
                }
            }
        });

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tvQty.getText().toString());
                if (qty < 10) {
                    qty++;
                    holder.tvQty.setText(String.valueOf(qty));
                    double item_total = Functions.roundTwoDecimals(qty * data.getItem_price());
                    holder.tvTotalPrice.setText(String.valueOf(item_total));
                    CartData data1 = data;
                    data1.setQty(qty);
                    data1.setItem_total_price(item_total);
                    new DBHelper(context).updateCartItem(data1);
                }
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(data, position);
            }
        });
    }

    private void deleteItem(final CartData data, final int position) {
        new AlertDialog.Builder(context)
                .setMessage("Remove " + data.getItem())
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (new DBHelper(context).deleteCartItem(data.getItem_id()) > 0) {
                            remove(position);
                            MainActivity.setupBadge();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void remove(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice, tvTotalPrice, tvQty, tvMinus, tvPlus, tvDelete;
        ImageView imageThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvTotalPrice = (TextView) itemView.findViewById(R.id.tvTotalPrice);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            imageThumb = (ImageView) itemView.findViewById(R.id.imageThumb);
        }
    }
}
