package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private Context context;
    private ArrayList<CartModel> list = new ArrayList<CartModel>();
    private RecyclerView recylcer_view;
    private ProgressBar progressBar;
    private CartItemAdapter adapter;
    private double total = 0;
    private TextView tvTotalPrice, tvPayableAmt;
    private RelativeLayout btnSubmit;
    private LinearLayout llAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setRecyclerView();
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            getData();
        else {
            Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }
    }

    private void init() {
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvPayableAmt = (TextView) findViewById(R.id.tvPayableAmt);
        llAmount = (LinearLayout) findViewById(R.id.llAmount);
        btnSubmit = (RelativeLayout) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITHOUT_PRESCRIPTION);
                startActivity(new Intent(context, DeliveryAddressActivity.class));
            }
        });
    }

    private void setRecyclerView() {
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapter = new CartItemAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CART)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.CART_ITEMS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        total = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final CartModel model1 = postSnapshot.getValue(CartModel.class);
                            Log.e(TAG, "onDataChange: model1 >> " + model1);
                            FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                                    .child(AppConstants.FIREBASE_KEY.MEDICINE)
                                    .child(model1.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot1) {
                                            ProductModel model2 = dataSnapshot1.getValue(ProductModel.class);
                                            Log.e(TAG, "onDataChange: model2 >> " + model2);
                                            CartModel final_model = new CartModel();
                                            final_model.setKey(model1.getKey());
                                            final_model.setQty_no(model1.getQty_no());
                                            final_model.setItem(model2.getName());
                                            final_model.setImage(model2.getImage());
                                            final_model.setItem_price(String.valueOf(model2.getPrice()));
                                            final_model.setItem_total_price(String.valueOf(model1.getQty_no() * model2.getPrice()));
                                            list.add(final_model);
                                            total = total + Double.parseDouble(final_model.getItem_total_price());
                                            adapter.notifyDataSetChanged();
                                            tvTotalPrice.setText("Rs. " + Functions.roundTwoDecimals(total));
                                            tvPayableAmt.setText("Rs. " + Functions.roundTwoDecimals(total));
                                            if (adapter.getItemCount() == 0) {
                                                llAmount.setVisibility(View.GONE);
                                                btnSubmit.setVisibility(View.GONE);
                                            } else {
                                                llAmount.setVisibility(View.VISIBLE);
                                                btnSubmit.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            adapter.notifyDataSetChanged();
                                            if (adapter.getItemCount() == 0) {
                                                llAmount.setVisibility(View.GONE);
                                                btnSubmit.setVisibility(View.GONE);
                                            } else {
                                                llAmount.setVisibility(View.VISIBLE);
                                                btnSubmit.setVisibility(View.VISIBLE);
                                            }
                                            Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                                        }
                                    });

                        }
                        hideProgressDialog();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (adapter.getItemCount() == 0) {
                            llAmount.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.GONE);
                        } else {
                            llAmount.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.VISIBLE);
                        }
                        Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_cart) {
        }
        return super.onOptionsItemSelected(item);
    }
}
