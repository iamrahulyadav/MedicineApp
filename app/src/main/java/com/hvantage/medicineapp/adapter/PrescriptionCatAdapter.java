package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.SubCategoryData;
import com.hvantage.medicineapp.util.fastscrollbars.FastScrollRecyclerViewInterface;

import java.util.ArrayList;
import java.util.HashMap;

public class PrescriptionCatAdapter extends RecyclerView.Adapter<PrescriptionCatAdapter.ViewHolder> implements FastScrollRecyclerViewInterface {

    private static final String TAG = "CategoryAdapter";
    private final HashMap<String, Integer> mapIndex;
    Context context;
    ArrayList<SubCategoryData> arrayList;


    public PrescriptionCatAdapter(Context context, ArrayList<SubCategoryData> arrayList, HashMap<String, Integer> mapIndex) {
        this.context = context;
        this.arrayList = arrayList;
        this.mapIndex = mapIndex;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_prescription_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SubCategoryData data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getSubCatName());
        if (position == 0) {
            holder.tvStickyHeader.setVisibility(View.VISIBLE);
            holder.tvStickyHeader.setText(data.getSubCatName().charAt(0) + "");
        } else if (position > 0) {
            if (arrayList.get(position).getSubCatName().charAt(0) != arrayList.get(position - 1).getSubCatName().charAt(0)) {
                holder.tvStickyHeader.setVisibility(View.VISIBLE);
                holder.tvStickyHeader.setText(data.getSubCatName().charAt(0) + "");
            } else {
                holder.tvStickyHeader.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public HashMap<String, Integer> getMapIndex() {
        return this.mapIndex;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvStickyHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvStickyHeader = (TextView) itemView.findViewById(R.id.tvStickyHeader);
        }
    }
}
