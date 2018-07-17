package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.util.fastscrollbars.FastScrollRecyclerViewInterface;

import java.util.ArrayList;
import java.util.HashMap;

public class AllPrescriptionAdapter extends RecyclerView.Adapter<AllPrescriptionAdapter.ViewHolder> implements FastScrollRecyclerViewInterface {

    private static final String TAG = "CategoryAdapter";
    private final HashMap<String, Integer> mapIndex;
    Context context;
    ArrayList<String> arrayList;


    public AllPrescriptionAdapter(Context context, ArrayList<String> arrayList, HashMap<String, Integer> mapIndex) {
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
        final String data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText(data);
        if (position == 0) {
            holder.tvStickyHeader.setVisibility(View.VISIBLE);
            holder.tvStickyHeader.setText(data.charAt(0) + "");
        } else if (position > 0) {
            if (arrayList.get(position).charAt(0) != arrayList.get(position - 1).charAt(0)) {
                holder.tvStickyHeader.setVisibility(View.VISIBLE);
                holder.tvStickyHeader.setText(data.charAt(0) + "");
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
