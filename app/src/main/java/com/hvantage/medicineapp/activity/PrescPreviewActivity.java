package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.PPItemAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.fragments.UploadPrecriptionFragment;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.TouchImageView;

import java.util.ArrayList;

public class PrescPreviewActivity extends AppCompatActivity {
    private static final String TAG = "PrescPreviewActivity";
    private TouchImageView imageFull;
    private ImageView back;
    private Context context;
    private CoordinatorLayout coordinatorLayout;
    private AppCompatAutoCompleteTextView etSearch;
    private ProgressBar progressBar;
    private ArrayList<String> list = new ArrayList<String>();
    private View bottomSheet;
    private RecyclerView recylcer_view_cart;
    private PPItemAdapter adapterCart;
    public static ArrayList<CartModel> cartList;
    public static int position;
    private PrescriptionModel data;

    @Override
    protected void onResume() {
        super.onResume();
        if (adapterCart != null) {
            adapterCart.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        context = this;
        init();
        cartList = new ArrayList<CartModel>();
        list = new DBHelper(context).getMedicinesSearch();
        if (getIntent().hasExtra("position")) {
            position = getIntent().getIntExtra("position", 0);
            Log.e(TAG, "onCreate: position >> " + position);
            data = UploadPrecriptionFragment.presList.get(position);
            ArrayList<CartModel> itemList = UploadPrecriptionFragment.presList.get(position).getItem_list();
            Log.e(TAG, "onCreate: itemList >> " + itemList);
            if (itemList != null)
                cartList = itemList;
            imageFull.setImageBitmap(Functions.base64ToBitmap(data.getImage_base64()));
            setBottomBar();
            setSearchBar();
            setRecyclerViewCart();
        } else {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setRecyclerViewCart() {
        adapterCart = new PPItemAdapter(context, cartList);
        recylcer_view_cart.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_cart.setAdapter(adapterCart);
        adapterCart.notifyDataSetChanged();
    }

    private void setBottomBar() {
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.e("onStateChanged", "onStateChanged:" + newState);
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void setSearchBar() {
        if (list != null) {
            Log.e(TAG, "setSearchBar: list >> " + list.size());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.auto_complete_text, list);
            etSearch.setThreshold(1);
            etSearch.setAdapter(adapter);
            etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.e(TAG, "onItemClick: text >> " + etSearch.getText().toString());
                    if (Functions.isConnectingToInternet(context)) {
                        showProgressDialog();
                        FirebaseDatabase.getInstance().getReference()
                                .child(AppConstants.APP_NAME)
                                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                                .orderByChild("name")
                                .equalTo(etSearch.getText().toString())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        hideProgressDialog();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            ProductModel data = postSnapshot.getValue(ProductModel.class);
                                            Log.e(TAG, "onDataChange: data >> " + data);
                                            Intent intent = new Intent(context, ProductDetail2Activity.class);
                                            intent.putExtra("medicine_data", data);
                                            startActivity(intent);
                                            etSearch.setText("");
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        hideProgressDialog();
                                        Log.w(TAG, "onCancelled >> ", databaseError.toException());
                                    }
                                });
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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

    private void init() {
        imageFull = (TouchImageView) findViewById(R.id.imageFull);
        etSearch = (AppCompatAutoCompleteTextView) findViewById(R.id.etSearch);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        recylcer_view_cart = (RecyclerView) findViewById(R.id.recylcer_view_items);
        back = (ImageView) findViewById(R.id.back);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
    }
}