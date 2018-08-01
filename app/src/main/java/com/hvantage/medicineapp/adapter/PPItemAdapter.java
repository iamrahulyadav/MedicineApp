package com.hvantage.medicineapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class PPItemAdapter extends RecyclerView.Adapter<PPItemAdapter.ViewHolder> {

    private static final String TAG = "CartItemAdapter";
    Context context;
    ArrayList<CartModel> arrayList;
    private ProgressBar progressBar;


    public PPItemAdapter(Context context, ArrayList<CartModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pp_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final CartModel data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getItem());
        holder.tvQty.setText("" + data.getQty_no());
        if (!data.getImage().equalsIgnoreCase(""))
            holder.imageThumb.setImageBitmap(Functions.base64ToBitmap(data.getImage()));
        else
            holder.imageThumb.setImageResource(R.drawable.no_image_placeholder);

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(data);
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

    private void deleteItem(final CartModel data) {
        new AlertDialog.Builder(context)
                .setMessage("Remove " + data.getItem())
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog();
                        FirebaseDatabase.getInstance().getReference()
                                .child(AppConstants.APP_NAME)
                                .child(AppConstants.FIREBASE_KEY.CART)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                .child(AppConstants.FIREBASE_KEY.CART_ITEMS)
                                .child(data.getKey())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hideProgressDialog();
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        notifyDataSetChanged();
                                        hideProgressDialog();
                                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvQty, tvDelete;
        ImageView imageThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            imageThumb = (ImageView) itemView.findViewById(R.id.imageThumb);
        }
    }
}
