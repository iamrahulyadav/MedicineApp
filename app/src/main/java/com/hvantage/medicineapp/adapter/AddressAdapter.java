package com.hvantage.medicineapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.AddressData;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private static final String TAG = "UploadedPreAdapter";
    Context context;
    ArrayList<AddressData> arrayList;
    private MyAdapterListener listener;
    private ProgressBar progressBar;

    public AddressAdapter(Context context, ArrayList<AddressData> arrayList, MyAdapterListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AddressData data = arrayList.get(position);
        Log.d(TAG, "onBindViewHolder: data >> " + data);
        holder.tvName.setText(data.getName());
        holder.tvPhoneNo.setText("+91" + data.getContactNo());
        holder.tvAddress.setText(data.getAddress());
        holder.tvLandmark.setText(data.getLandmark());
        holder.tvCity.setText(data.getCity() + ", " + data.getPincode());
        holder.tvPincode.setText(data.getState() + ", India");
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteAddress(data, position);
                listener.delete(v, position);
            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.select(v, position);

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

    public void removeAt(int position) {
        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());
    }

    public interface MyAdapterListener {
        void delete(View v, int position);

        void select(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhoneNo, tvAddress, tvLandmark, tvCity, tvPincode, tvDelete;
        CardView item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPhoneNo = (TextView) itemView.findViewById(R.id.tvPhoneNo);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvLandmark = (TextView) itemView.findViewById(R.id.tvLandmark);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            tvPincode = (TextView) itemView.findViewById(R.id.tvPincode);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            item = (CardView) itemView.findViewById(R.id.item);
        }
    }


}
