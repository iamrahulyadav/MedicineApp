package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class SelectPrescAdapter extends RecyclerView.Adapter<SelectPrescAdapter.ViewHolder> {

    private static final String TAG = "SelectPrescAdapter";
    Context context;
    ArrayList<PrescriptionModel> arrayList;
    private ProgressBar progressBar;

    public SelectPrescAdapter(Context context, ArrayList<PrescriptionModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_pres_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PrescriptionModel data = arrayList.get(position);
        Log.d(TAG, "onBindViewHolder: data >> " + data);
        holder.tvTitle.setText(data.getTitle());
        holder.tvDesciption.setText(data.getDescription());
        holder.tvDate.setText(data.getDate_time());
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
                // context.startActivity(new Intent(context, PrescPreviewActivity.class).putExtra("prescription_data", data));
                //showPreviewDialog(Functions.base64ToBitmap(data.getImage_base64()));
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
        ImageView image;
        TextView tvDesciption, tvTitle, tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDesciption = (TextView) itemView.findViewById(R.id.tvDesciption);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
