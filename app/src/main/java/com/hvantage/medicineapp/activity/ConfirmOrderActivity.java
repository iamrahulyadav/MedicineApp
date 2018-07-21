package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.adapter.UploadedPreAdapter;
import com.hvantage.medicineapp.model.AddressModel;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.GridSpacingItemDecoration;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmOrderActivity";
    private Context context;
    private ProgressBar progressBar;
    private TextView tvTotalPrice,tvPayableAmt;
    private TextView tvCheckout;
    private LinearLayout llPrescription, llMedicine, llAmount;
    private LinearLayout llPayMode;
    private AddressModel addressData;
    private TextView tvAddress1, tvAddress2, tvAddress3, tvAddress4;
    private TextView tvChangeAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_item);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra("data"))
            addressData = (AddressModel) getIntent().getSerializableExtra("data");
        else {
            startActivity(new Intent(context, DeliveryAddressActivity.class));
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
                startActivity(new Intent(context, DeliveryAddressActivity.class));
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


    private RecyclerView recylcer_view;
    private UploadedPreAdapter adapterPres;
    private ArrayList<PrescriptionModel> presList = new ArrayList<PrescriptionModel>();

    private ArrayList<CartModel> cartList = new ArrayList<CartModel>();
    private CartItemAdapter adapterCart;

    private double total = 0;
    private RecyclerView recylcer_view_items;

    private int spacing = 30, spanCount = 3;
    private boolean includeEdge = true;

    private void setRecyclerView() {
        presList.clear();
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
                                            Log.e(TAG, "onDataChange: model2 >> " + model2);
                                            CartModel final_model = new CartModel();
                                            final_model.setKey(model1.getKey());
                                            final_model.setQty_no(model1.getQty_no());
                                            final_model.setItem(model2.getName());
                                            final_model.setImage(model2.getImage());
                                            final_model.setItem_price(model2.getPrice());
                                            final_model.setItem_total_price(model1.getQty_no() * model2.getPrice());
                                            cartList.add(final_model);
                                            adapterCart.notifyDataSetChanged();
                                            total = total + final_model.getItem_total_price();
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
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CART)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.PRESCRIPTION)
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
                if (presList.isEmpty()) {
                    showNoPresAlert();
                } else if (cartList.isEmpty() && !presList.isEmpty()) {
                    showNoItemsAlert();
                }
                showVaryAlert();
                break;
            case R.id.tvChangeAddress:
                startActivity(new Intent(context, DeliveryAddressActivity.class));
                break;

        }
    }

    private void showNoItemsAlert() {
        new AlertDialog.Builder(context)
                .setMessage("No items added, place order with uploaded prescriptions only.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void showNoPresAlert() {
        new AlertDialog.Builder(context)
                .setMessage("No presciption uploaded, continue shopping without prescription.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void showVaryAlert() {
        new AlertDialog.Builder(context)
                .setMessage("Total amount can be vary after reviewing your uploaded prescriptions by our pharmacist if any uploads.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
}
