package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.adapter.UploadedPreAdapter;
import com.hvantage.medicineapp.model.AddressModel;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.model.OrderData;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.GridSpacingItemDecoration;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;
import java.util.Map;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmOrderActivity";
    ArrayList<PrescriptionModel> presList = new ArrayList<PrescriptionModel>();
    private Context context;
    private ProgressBar progressBar;
    private TextView tvTotalPrice, tvPayableAmt;
    private TextView tvCheckout;
    private LinearLayout llPrescription, llMedicine, llAmount;
    private LinearLayout llPayMode;
    private AddressModel addressData;
    private TextView tvAddress1, tvAddress2, tvAddress3, tvAddress4;
    private TextView tvChangeAddress;
    private Map<String, String> timestamp;
    private Double taxes = 0.0;
    private Double delivery_fee = 0.0;
    private String payment_mode = "Cash On Delivery";
    private RecyclerView recylcer_view;
    private UploadedPreAdapter adapterPres;

    private ArrayList<CartModel> cartList = new ArrayList<CartModel>();
    private CartItemAdapter adapterCart;

    private double total = 0;
    private RecyclerView recylcer_view_items;

    private int spacing = 30, spanCount = 3;
    private boolean includeEdge = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_item);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timestamp = ServerValue.TIMESTAMP;
        Log.e(TAG, "onCreate: ServerValue.TIMESTAMP >> " + ServerValue.TIMESTAMP);
        if (getIntent().hasExtra("data"))
            addressData = (AddressModel) getIntent().getSerializableExtra("data");
        else {
            startActivity(new Intent(context, SelectAddressActivity.class));
            finish();
        }

        Log.e(TAG, "onCreate: addressData >> " + addressData);
        init();
        setRecyclerView();
        setRecyclerViewCart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (AppPreferences.getOrderType(context) == AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION) {
                llPrescription.setVisibility(View.VISIBLE);
                llMedicine.setVisibility(View.VISIBLE);
                llPayMode.setVisibility(View.GONE);
                llAmount.setVisibility(View.GONE);
                getData();
                getCartData();
            } else {
                llPrescription.setVisibility(View.GONE);
                llPayMode.setVisibility(View.VISIBLE);
                llMedicine.setVisibility(View.VISIBLE);
                llAmount.setVisibility(View.VISIBLE);
                getCartData();
            }


            if (addressData != null) {
                tvAddress1.setText(addressData.getName() + ", +91" + addressData.getContact_no());
                tvAddress2.setText(addressData.getAddress() + ", " + addressData.getLandmark());
                tvAddress3.setText(addressData.getCity() + ", " + addressData.getPincode());
                tvAddress4.setText(addressData.getState() + ", India");
            } else {
                startActivity(new Intent(context, SelectAddressActivity.class));
                finish();
            }

        } else {
            startActivity(new Intent(context, LoginActivity.class));
        }
    }

    private void init() {
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvPayableAmt = (TextView) findViewById(R.id.tvPayableAmt);
        tvCheckout = (TextView) findViewById(R.id.tvCheckout);
        tvAddress1 = (TextView) findViewById(R.id.tvAddress1);
        tvAddress2 = (TextView) findViewById(R.id.tvAddress2);
        tvAddress3 = (TextView) findViewById(R.id.tvAddress3);
        tvAddress4 = (TextView) findViewById(R.id.tvAddress4);
        tvChangeAddress = (TextView) findViewById(R.id.tvChangeAddress);
        llPrescription = (LinearLayout) findViewById(R.id.llPrescription);
        llMedicine = (LinearLayout) findViewById(R.id.llMedicine);
        llAmount = (LinearLayout) findViewById(R.id.llAmount);
        llPayMode = (LinearLayout) findViewById(R.id.llPayMode);
        tvCheckout.setOnClickListener(this);
        tvChangeAddress.setOnClickListener(this);
    }


    private void setRecyclerView() {
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapterPres = new UploadedPreAdapter(context, presList);
        recylcer_view.setLayoutManager(new GridLayoutManager(context, spanCount));
        recylcer_view.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recylcer_view.setAdapter(adapterPres);
        adapterPres.notifyDataSetChanged();
    }

    private void setRecyclerViewCart() {
        recylcer_view_items = (RecyclerView) findViewById(R.id.recylcer_view_items);
        adapterCart = new CartItemAdapter(context, cartList);
        recylcer_view_items.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_items.setAdapter(adapterCart);
        adapterCart.notifyDataSetChanged();
    }

    private void getCartData() {
        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CART)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.CART_ITEMS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartList.clear();
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
                                            Log.d(TAG, "onDataChange: model2 >> " + model2);
                                            CartModel final_model = new CartModel();
                                            final_model.setKey(model1.getKey());
                                            final_model.setQty_no(model1.getQty_no());
                                            final_model.setItem(model2.getName());
                                            final_model.setImage(model2.getImage());
                                            final_model.setItem_price(String.valueOf(model2.getPrice()));
                                            final_model.setItem_total_price(String.valueOf(model1.getQty_no() * model2.getPrice()));
                                            cartList.add(final_model);
                                            adapterCart.notifyDataSetChanged();
                                            total = total + Double.parseDouble(final_model.getItem_total_price());
                                            tvTotalPrice.setText("Rs. " + total);
                                            tvPayableAmt.setText("Rs. " + total);
                                            if (adapterCart.getItemCount() == 0) {
                                                llMedicine.setVisibility(View.GONE);
                                            } else {
                                                llMedicine.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            adapterCart.notifyDataSetChanged();
                                            if (adapterCart.getItemCount() == 0) {
                                                llMedicine.setVisibility(View.GONE);
                                            } else {
                                                llMedicine.setVisibility(View.VISIBLE);
                                            }
                                            Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        adapterCart.notifyDataSetChanged();
                        if (adapterCart.getItemCount() == 0) {
                            llMedicine.setVisibility(View.GONE);
                        } else {
                            llMedicine.setVisibility(View.VISIBLE);
                        }
                        Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.TEMP_PRESCRIPTION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        presList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            PrescriptionModel data = postSnapshot.getValue(PrescriptionModel.class);
                            if (data != null) {
                                presList.add(data);
                                adapterPres.notifyDataSetChanged();
                            }
                            adapterPres.notifyDataSetChanged();
                            if (adapterPres.getItemCount() == 0) {
                                llPrescription.setVisibility(View.GONE);
                            } else {
                                llPrescription.setVisibility(View.VISIBLE);
                            }
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        adapterPres.notifyDataSetChanged();
                        if (adapterPres.getItemCount() == 0) {
                            llPrescription.setVisibility(View.GONE);
                        } else {
                            llPrescription.setVisibility(View.VISIBLE);
                        }
                        Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_cart) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheckout:
                if (Functions.isConnectingToInternet(context))
                    sendData();
                else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tvChangeAddress:
                startActivity(new Intent(context, SelectAddressActivity.class));
                break;

        }
    }

    private void sendData() {
        showProgressDialog();


        String key = FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.ORDERS)
                .push().getKey();

        OrderData orderData = new OrderData();
//        orderData.setDate_time_server(ServerValue.TIMESTAMP);
        orderData.setDate(Functions.getCurrentDate());
        orderData.setTime(Functions.getCurrentTime());
        orderData.setTaxes(String.valueOf(taxes));
        orderData.setDelivery_fee(String.valueOf(delivery_fee));
        orderData.setTotal_amount(String.valueOf(total));
        orderData.setPayable_amount(String.valueOf(total));

        //delivery details
        orderData.setDelivery_details(addressData);
        //cart items
        orderData.setItems(cartList);
        //presriptions
        orderData.setPrescriptions(presList);

        orderData.setKey(key);
        orderData.setPayment_mode(payment_mode);
        orderData.setStatus("In-Progress");
        orderData.setOrder_type(String.valueOf(AppPreferences.getOrderType(context)));
        orderData.setBy(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        Log.e(TAG, "sendData: orderData >> " + orderData);

        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.ORDERS)
                .child(key)
                .setValue(orderData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                                .child(AppConstants.FIREBASE_KEY.CART)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hideProgressDialog();
                                        Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmOrderActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
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
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error writing document", e);
            }
        });

    }

}
