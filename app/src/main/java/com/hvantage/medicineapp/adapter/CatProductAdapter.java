package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.Functions;

import java.util.ArrayList;

public class CatProductAdapter extends RecyclerView.Adapter<CatProductAdapter.ViewHolder> {

    private static final String TAG = "CategoryAdapter";
    Context context;
    ArrayList<ProductModel> arrayList;


    public CatProductAdapter(Context context, ArrayList<ProductModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductModel data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvPrice.setText("Rs. " + data.getPrice());
        if (!data.getImage().equalsIgnoreCase(""))
            holder.imageThumb.setImageBitmap(Functions.base64ToBitmap(data.getImage()));
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvPrice;
        ImageView imageThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            imageThumb = (ImageView) itemView.findViewById(R.id.imageThumb);
        }
    }
}
