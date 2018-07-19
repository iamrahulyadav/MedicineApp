package com.hvantage.medicineapp.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.ImagePreviewActivity;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.TouchImageView;

import java.util.ArrayList;

public class UploadedPreAdapter extends RecyclerView.Adapter<UploadedPreAdapter.ViewHolder> {

    private static final String TAG = "UploadedPreAdapter";
    Context context;
    ArrayList<PrescriptionModel> arrayList;
    private ProgressBar progressBar;

    public UploadedPreAdapter(Context context, ArrayList<PrescriptionModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_pres_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PrescriptionModel data = arrayList.get(position);
        Log.d(TAG, "onBindViewHolder: data >> " + data);
        byte[] imageByteArray = Base64.decode(data.getImage_base64(), Base64.DEFAULT);
        Glide.with(context)
                .load(imageByteArray)
                .crossFade()
                .centerCrop()
                .override(100, 100)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
//        holder.image.setImageBitmap(data);


        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onBindViewHolder: data >> " + data);
               // context.startActivity(new Intent(context, ImagePreviewActivity.class).putExtra("prescription_data", data));
                showPreviewDialog(Functions.base64ToBitmap(data.getImage_base64()));

            }
        });
        holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onBindViewHolder: data >> " + data);
                new AlertDialog.Builder(context)
                        .setMessage("Delete this prescription?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showProgressDialog();
                                FirebaseDatabase.getInstance().getReference()
                                        .child(AppConstants.APP_NAME)
                                        .child(AppConstants.FIREBASE_KEY.CART)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                        .child(AppConstants.FIREBASE_KEY.PRESCRIPTION)
                                        .child(data.getKey())
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                hideProgressDialog();
                                                // removeAt(position);
                                                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                                                // Write was successful!
                                                // ...

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                hideProgressDialog();
                                                // Write failed
                                                // ...
                                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
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


    private void showPreviewDialog(Bitmap bitmap) {
        Dialog dialog1 = new Dialog(context, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_preview_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);
        TouchImageView imgPreview = (TouchImageView) dialog1.findViewById(R.id.imgPreview);
        imgPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPreview.setImageBitmap(bitmap);
        dialog1.show();
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
        ImageView image, imgRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            imgRemove = (ImageView) itemView.findViewById(R.id.imgRemove);
        }
    }
}
