package com.hvantage.medicineapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.PreMedicineData;

import java.util.ArrayList;

public class CartMedicineItemAdapter extends RecyclerView.Adapter<CartMedicineItemAdapter.ViewHolder> {

    private static final String TAG = "CartItemAdapter";
    Context context;
    ArrayList<PreMedicineData> arrayList;

    public CartMedicineItemAdapter(Context context, ArrayList<PreMedicineData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_med_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final PreMedicineData data = arrayList.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvSubtitle.setText(data.getManufacturer());
        holder.tvQty.setText("" + data.getQuantity());


        holder.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tvQty.getText().toString());
                if (qty > 1) {
                    qty--;
                    holder.tvQty.setText(String.valueOf(qty));
                }
            }
        });

        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tvQty.getText().toString());
                qty++;
                holder.tvQty.setText(String.valueOf(qty));
            }
        });

        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(data, position);
            }
        });
    }

    private void deleteItem(final PreMedicineData data, final int position) {
        new AlertDialog.Builder(context)
                .setMessage("Remove " + data.getName())
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void remove(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
        Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvSubtitle, tvQty, tvMinus, tvPlus, tvDelete;
        ImageView imageThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSubtitle = (TextView) itemView.findViewById(R.id.tvSubtitle);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvPlus = (TextView) itemView.findViewById(R.id.tvPlus);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            imageThumb = (ImageView) itemView.findViewById(R.id.imageThumb);
        }
    }
}
