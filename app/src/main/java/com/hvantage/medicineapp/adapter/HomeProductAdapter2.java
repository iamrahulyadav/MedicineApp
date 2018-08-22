package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.LoginActivity;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class HomeProductAdapter2 extends RecyclerView.Adapter<HomeProductAdapter2.ViewHolder> {

    private static final String TAG = "HomeProductAdapter2";
    Context context;
    ArrayList<ProductData> arrayList;
    private ProgressBar progressBar;


    public HomeProductAdapter2(Context context, ArrayList<ProductData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout2, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvPrice.setText("Rs. " + data.getPriceDiscount());
        holder.tvPriceDrop.setText("Rs. " + data.getPriceMrp());
        holder.tvOffers.setText(data.getDiscountText());
        holder.tvPriceDrop.setPaintFlags(holder.tvPriceDrop.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //double discount_amt = data.getPrice() * 10 / 100;
        //holder.tvPrice.setText("Rs. " + Functions.roundTwoDecimals(data.getPrice() - discount_amt));

        if (!data.getImage().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(data.getImage())
                    .crossFade()
                    .into(holder.img);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ProductDetailActivity.class)
                        .putExtra("medicine_data", arrayList.get(position)));
            }
        });

        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tvQty.getText().toString());
                if (qty > 1)
                    qty--;
                holder.tvQty.setText(String.valueOf(qty));
            }
        });

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tvQty.getText().toString());
                if (qty < 10)
                    qty++;
                holder.tvQty.setText(String.valueOf(qty));
            }
        });

       /* holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                    double item_total = Integer.parseInt(holder.tvQty.getText().toString()) * Double.parseDouble(data.getPriceDiscount());
                    CartData model = new CartData(
                            data.getProductId(),
                            data.getName(),
                            data.getImage(),
                            Integer.parseInt(holder.tvQty.getText().toString()),
                            Double.parseDouble(data.getPriceDiscount()),
                            item_total
                    );
                    if (new DBHelper(context).addToCart(model))
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });*/
        if (data.getTotalAvailable() > 0) {
            holder.btnAddToCart.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.tvBtnText.setText("Add To Cart");
            holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                        double item_total = Integer.parseInt(holder.tvQty.getText().toString()) * Double.parseDouble(data.getPriceDiscount());
                        CartData model = new CartData(
                                data.getProductId(),
                                data.getName(),
                                data.getImage(),
                                Integer.parseInt(holder.tvQty.getText().toString()),
                                Double.parseDouble(data.getPriceDiscount()),
                                item_total
                        );
                        if (new DBHelper(context).addToCart(model))
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, LoginActivity.class));
                    }
                }
            });
        } else {
            holder.btnAddToCart.setCardBackgroundColor(context.getResources().getColor(R.color.colorSuccess));
            holder.tvBtnText.setText("Sold Out");
            holder.btnAddToCart.setOnClickListener(null);
        }
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

        ImageView img;
        TextView tvTitle, tvPriceDrop, tvOffers, tvPrice, tvQty, tvMinus, tvPlus, tvBtnText;
        RelativeLayout container;
        CardView btnAddToCart;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPriceDrop = (TextView) itemView.findViewById(R.id.tvPriceDrop);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvOffers = (TextView) itemView.findViewById(R.id.tvOffers);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
            tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
            btnAddToCart = (CardView) itemView.findViewById(R.id.btnAddToCart);
            tvBtnText = (TextView) itemView.findViewById(R.id.tvBtnText);
        }
    }
}
