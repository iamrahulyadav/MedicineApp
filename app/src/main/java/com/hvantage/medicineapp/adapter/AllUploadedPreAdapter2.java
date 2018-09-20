package com.hvantage.medicineapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.TouchImageView;

import java.util.ArrayList;

public class AllUploadedPreAdapter2 extends RecyclerView.Adapter<AllUploadedPreAdapter2.ViewHolder> {
    private static final String TAG = "UploadedPreAdapter";
    private final MyAdapterListener listener;
    Context context;
    ArrayList<PrescriptionData> arrayList;
    private ProgressBar progressBar;

    public AllUploadedPreAdapter2(Context context, ArrayList<PrescriptionData> arrayList, MyAdapterListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_uploaded_pres_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PrescriptionData data = arrayList.get(position);
        if (!data.getPrescription_title().equalsIgnoreCase(""))
            holder.tvDeases.setText(data.getPrescription_title());
        else
            holder.tvDeases.setText("Rx_NO_NAME");
        holder.tvDate.setText(data.getDate());
        if (!data.getImage().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(data.getImage())
                    .crossFade()
                    .placeholder(R.drawable.action_upload_rx)
                    .into(holder.imgThumb);
        }

      /*  holder.tvDeases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.viewOrder(view, position);
            }
        });
        holder.imgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.viewOrder(view, position);
            }
        });*/
        holder.tvOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.viewOrder(view, position);
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

    public interface MyAdapterListener {
        void viewOrder(View v, int position);

        void placeOrder(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imgThumb;
        AppCompatTextView tvDeases, tvDate, /*tvView, */
                tvOrder;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumb = (AppCompatImageView) itemView.findViewById(R.id.imgThumb);
            tvDeases = (AppCompatTextView) itemView.findViewById(R.id.tvDeases);
            tvDate = (AppCompatTextView) itemView.findViewById(R.id.tvDate);
//            tvView = (TextView) itemView.findViewById(R.id.tvView);
            tvOrder = (AppCompatTextView) itemView.findViewById(R.id.tvOrder);
        }
    }

}
