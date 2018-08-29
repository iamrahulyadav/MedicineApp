package com.hvantage.medicineapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.TouchImageView;

import java.util.ArrayList;

public class ConfirmOrderPrescAdapter extends RecyclerView.Adapter<ConfirmOrderPrescAdapter.ViewHolder> {
    private final MyAdapterListener listener;
    private static final String TAG = "ConfirmOrderPrescAdapter";
    Context context;
    ArrayList<PrescriptionData> arrayList;
    private ProgressBar progressBar;

    public ConfirmOrderPrescAdapter(Context context, ArrayList<PrescriptionData> arrayList, MyAdapterListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_order_pres_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PrescriptionData data = arrayList.get(position);
        Log.d(TAG, "onBindViewHolder: data >> " + data);
        if (!data.getDiagnosisDetails().equalsIgnoreCase(""))
            holder.tvDeases.setText(data.getDiagnosisDetails());
        else
            holder.tvDeases.setText("No Details");
        if (data.getDate() != null)
            holder.tvDate.setText(data.getDate());
        else
            holder.tvDate.setText(Functions.getCurrentDate());
        if (data.getImage().contains(".jpg") || data.getImage().contains(".png")) {
            Glide.with(context)
                    .load(data.getImage())
                    .crossFade()
                    .into(holder.imgThumb);
        } else {
            holder.imgThumb.setImageBitmap(Functions.base64ToBitmap(data.getImage()));
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
        ImageView imgThumb;
        TextView tvDeases, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumb = (ImageView) itemView.findViewById(R.id.imgThumb);
            tvDeases = (TextView) itemView.findViewById(R.id.tvDeases);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }
    }

    public interface MyAdapterListener {
        void viewOrder(View v, int position);

        void placeOrder(View v, int position);
    }

}
