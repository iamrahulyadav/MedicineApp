package com.hvantage.medicineapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.AddressModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private static final String TAG = "UploadedPreAdapter";
    Context context;
    ArrayList<AddressModel> arrayList;
    private ProgressBar progressBar;

    public AddressAdapter(Context context, ArrayList<AddressModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AddressModel data = arrayList.get(position);
        Log.d(TAG, "onBindViewHolder: data >> " + data);
        holder.tvName.setText(data.getName());
        holder.tvPhoneNo.setText("+91" + data.getContact_no());
        holder.tvAddress.setText(data.getAddress());
        holder.tvLandmark.setText(data.getLandmark());
        holder.tvCity.setText(data.getCity() + ", " + data.getPincode());
        holder.tvPincode.setText(data.getState() + ", India");
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAddress(data);
            }
        });
    }

    private void deleteAddress(final AddressModel data) {
        new AlertDialog.Builder(context)
                .setMessage("Delete this address")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog();
                        FirebaseDatabase.getInstance().getReference()
                                .child(AppConstants.APP_NAME)
                                .child(AppConstants.FIREBASE_KEY.ADDRESS)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                .child(data.getKey())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hideProgressDialog();
                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hideProgressDialog();
                                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhoneNo, tvAddress, tvLandmark, tvCity, tvPincode, tvDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPhoneNo = (TextView) itemView.findViewById(R.id.tvPhoneNo);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvLandmark = (TextView) itemView.findViewById(R.id.tvLandmark);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            tvPincode = (TextView) itemView.findViewById(R.id.tvPincode);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
        }
    }
}
