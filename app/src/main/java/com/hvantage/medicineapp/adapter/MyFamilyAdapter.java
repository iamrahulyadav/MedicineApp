package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.FamilyData;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class MyFamilyAdapter extends RecyclerView.Adapter<MyFamilyAdapter.ViewHolder> {

    private static final String TAG = "CategoryAdapter";
    private final MyAdapterListener listener;
    Context context;
    ArrayList<FamilyData> arrayList;
    private ProgressBar progressBar;


    public MyFamilyAdapter(Context context, ArrayList<FamilyData> arrayList, MyAdapterListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FamilyData data = arrayList.get(position);
        Log.e(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvRelation.setText("(" + data.getRelation() + ")");
        holder.tvBG.setText(data.getBloodGroup());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.delete(view, position);

            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.select(view, position);

            }
        });
    }

    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
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

    public interface MyAdapterListener {
        void delete(View v, int position);

        void select(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvRelation, tvBG;
        ImageButton btnDelete;
        CardView item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvRelation = (TextView) itemView.findViewById(R.id.tvRelation);
            tvBG = (TextView) itemView.findViewById(R.id.tvBG);
            btnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);
            item = (CardView) itemView.findViewById(R.id.item);
        }
    }
}
