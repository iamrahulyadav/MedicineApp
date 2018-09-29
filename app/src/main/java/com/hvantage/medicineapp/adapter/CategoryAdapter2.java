package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.CategoryData;

import java.util.ArrayList;

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.ViewHolder> {

    private static final String TAG = "CategoryAdapter";
    Context context;
    ArrayList<CategoryData> arrayList;

    public CategoryAdapter2(Context context, ArrayList<CategoryData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout2, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CategoryData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getCatName());
        Glide.with(context)
                .load(data.getCatImage())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(100, 100)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView img;
        AppCompatTextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
