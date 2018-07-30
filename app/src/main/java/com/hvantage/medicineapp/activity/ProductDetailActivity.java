package com.hvantage.medicineapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.HomeProductAdapter;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.TouchImageView;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProductDetailActivity";
    ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
    TextView tvName, tvManufacturer, tvPrice, tvAvailable, tvQty2, tvQty3, tvPreRequired, tvProductType, tvPower, tvCategory, tvDesciption;
    private TextView toolbar_title;
    private Context context;
    private RecyclerView recylcer_view;
    private HomeProductAdapter adapter;
    private ProductModel data;
    private ImageView imageThumb;
    private TextView tvMinus, tvQty, tvPlus;
    private CardView btnAddToCart;
    private ProgressBar progressBar;
    private int qty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        if (getIntent().hasExtra("medicine_data")) {
            data = (ProductModel) getIntent().getSerializableExtra("medicine_data");
            Log.e(TAG, "onCreate: data >> " + data);
            toolbar_title.setText(data.getName());
            tvName.setText(data.getName());
            tvManufacturer.setText("by " + data.getManufacturer());
            tvPrice.setText("Rs. " + data.getPrice());
            tvAvailable.setText("In-Stock");
            if (data.isPrescription_required() == true)
                tvPreRequired.setVisibility(View.VISIBLE);
            else
                tvPreRequired.setVisibility(View.GONE);
            tvProductType.setText(data.getProduct_type());
            tvPower.setText(data.getPower());
            tvQty2.setText(data.getQty());
            tvQty3.setText("Contains " + data.getQty());
            tvCategory.setText(data.getCategory_name());
            tvDesciption.setText(data.getDescription());
            if (!data.getImage().equalsIgnoreCase("")) {
                imageThumb.setImageBitmap(Functions.base64ToBitmap(data.getImage()));
                imageThumb.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //startActivity(new Intent(context, ImagePreviewActivity.class).putExtra("product_data", data));
                        showPreviewDialog(Functions.base64ToBitmap(data.getImage()));
                        return false;
                    }
                });
            }
        }
        //setProduct();
    }

    private void showPreviewDialog(Bitmap bitmap) {
        Dialog dialog1 = new Dialog(context, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_preview_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);
        TouchImageView imgPreview = (TouchImageView) dialog1.findViewById(R.id.imgPreview);
        imgPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPreview.setImageBitmap(bitmap);
        dialog1.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_cart) {
            startActivity(new Intent(context, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setProduct() {
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapter = new HomeProductAdapter(context, productList);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recylcer_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void init() {
        tvName = (TextView) findViewById(R.id.tvName);
        tvManufacturer = (TextView) findViewById(R.id.tvManufacturer);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvAvailable = (TextView) findViewById(R.id.tvAvailable);
        tvQty2 = (TextView) findViewById(R.id.tvQty2);
        tvQty3 = (TextView) findViewById(R.id.tvQty3);
        tvPreRequired = (TextView) findViewById(R.id.tvPreRequired);
        tvProductType = (TextView) findViewById(R.id.tvProductType);
        tvPower = (TextView) findViewById(R.id.tvPower);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDesciption = (TextView) findViewById(R.id.tvDesciption);
        tvQty = (TextView) findViewById(R.id.tvQty);
        tvMinus = (TextView) findViewById(R.id.tvMinus);
        tvPlus = (TextView) findViewById(R.id.tvPlus);
        btnAddToCart = (CardView) findViewById(R.id.btnAddToCart);
        imageThumb = (ImageView) findViewById(R.id.imageThumb);
        tvMinus.setOnClickListener(this);
        tvPlus.setOnClickListener(this);
        btnAddToCart.setOnClickListener(this);
    }

    private void decrementItem() {
        if (qty > 1)
            qty--;
        tvQty.setText(String.valueOf(qty));
    }

    private void incrementItem() {
        if (qty < 10)
            qty++;
        tvQty.setText(String.valueOf(qty));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPlus:
                incrementItem();
                break;
            case R.id.tvMinus:
                decrementItem();
                break;
            case R.id.btnAddToCart:
                if (FirebaseAuth.getInstance().getCurrentUser() != null)
                    addToCart();
                else {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, LoginActivity.class));
                    finish();
                }

                break;
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

    private void addToCart() {
        CartModel model = new CartModel(data.getKey(), Integer.parseInt(tvQty.getText().toString()));
        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CART)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.CART_ITEMS)
                .child(data.getKey())
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "DocumentSnapshot successfully written!");
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
