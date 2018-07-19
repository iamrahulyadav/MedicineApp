package com.hvantage.medicineapp.activity.business;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;

import java.util.ArrayList;

public class BusinessProductAdapter extends RecyclerView.Adapter<BusinessProductAdapter.ViewHolder> {

    private static final String TAG = "BusinessProductAdapter";
    Context context;
    ArrayList<ProductModel> arrayList;
    private Typeface typeface;
    private ArrayList<ProductModel> contactListFiltered;


    public BusinessProductAdapter(Context context, ArrayList<ProductModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.contactListFiltered = arrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_product_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProductModel data = contactListFiltered.get(position);
        Log.d(TAG, position + " data : " + data);
        holder.tvTitle.setText(data.getName());
        holder.tvQty.setText("(Qty. " + data.getTotal_available() + ")");
        if (data.getTotal_available() == 0)
            holder.switchNA.setChecked(true);
        else
            holder.switchNA.setChecked(false);
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddProductCatActivity.class);
                intent.putExtra("data", data);
                context.startActivity(intent);
            }
        });

        holder.switchNA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        holder.switchNA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.switchNA.isChecked()) {
                    new AlertDialog.Builder(context)
                            .setMessage("Do you want to make " + data.getName() + " not available?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseDatabase.getInstance()
                                            .getReference(AppConstants.APP_NAME)
                                            .child(AppConstants.FIREBASE_KEY.MEDICINE)
                                            .child(data.getKey())
                                            .child("total_available")
                                            .setValue(0)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    holder.switchNA.setChecked(true);
                                                    Log.e(TAG, "DocumentSnapshot successfully written!");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error writing document", e);
                                        }
                                    });
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            holder.switchNA.setChecked(false);
                        }
                    }).show();
                } else {
                    Toast.makeText(context, "Enter total available quantity", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, AddProductCatActivity.class);
                    intent.putExtra("data", data);
                    context.startActivity(intent);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void updateList(ArrayList<ProductModel> list) {
        arrayList = list;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvQty;
        SwitchCompat switchNA;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            switchNA = (SwitchCompat) itemView.findViewById(R.id.switchNA);
            typeface = ResourcesCompat.getFont(context, R.font.quicksand);
            switchNA.setTypeface(typeface);
        }
    }

}
