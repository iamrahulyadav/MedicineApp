package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.LoginActivity;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class HomeProductAdapter2 extends RecyclerView.Adapter<HomeProductAdapter2.ViewHolder> {

    private static final String TAG = "HomeProductAdapter";
    Context context;
    ArrayList<ProductModel> arrayList;
    private ProgressBar progressBar;


    public HomeProductAdapter2(Context context, ArrayList<ProductModel> arrayList) {
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
        final ProductModel data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvPriceDrop.setText("Rs. " + data.getPrice());
        holder.tvPriceDrop.setPaintFlags(holder.tvPriceDrop.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        double discount_amt = data.getPrice() * 10 / 100;
        holder.tvPrice.setText("Rs. " + Functions.roundTwoDecimals(data.getPrice() - discount_amt));

        if (!data.getImage().equalsIgnoreCase("")) {
            Bitmap bitmap = Functions.base64ToBitmap(data.getImage());
            if (bitmap != null) {
                holder.img.setImageBitmap(bitmap);
            }
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

        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    showProgressDialog();
                    CartModel model = new CartModel(data.getKey(), Integer.parseInt(holder.tvQty.getText().toString()));
                    FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                            .child(AppConstants.FIREBASE_KEY.CART)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .child(AppConstants.FIREBASE_KEY.CART_ITEMS)
                            .child(data.getKey())
                            .setValue(model)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                    Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "DocumentSnapshot successfully written!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error writing document", e);
                        }
                    });
                } else {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, LoginActivity.class));
                }
            }
        });
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
        TextView tvTitle, tvPriceDrop, tvPrice, tvQty, tvMinus, tvPlus;
        RelativeLayout container;
        CardView btnAddToCart;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPriceDrop = (TextView) itemView.findViewById(R.id.tvPriceDrop);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            container = (RelativeLayout) itemView.findViewById(R.id.container);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
            tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
            btnAddToCart = (CardView) itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
