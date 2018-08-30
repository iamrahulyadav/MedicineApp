package com.hvantage.medicineapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.TouchImageView;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProductDetailActivity";
    ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
    TextView tvName, tvManufacturer, tvDiscount, tvPrice, tvPriceDrop, tvAvailable, tvQty2, tvQty3, tvPreRequired, tvProductType, tvPower, tvCategory, tvDesciption;
    private TextView toolbar_title;
    private Context context;
    private RecyclerView recylcer_view;
    private ProductData data;
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
            data = (ProductData) getIntent().getParcelableExtra("medicine_data");
            Log.e(TAG, "onCreate: data >> " + data);
            toolbar_title.setText(data.getName());
            tvName.setText(data.getName());
            tvManufacturer.setText("by " + data.getManufacturer());
            tvDiscount.setText(data.getDiscountText());
            tvPriceDrop.setText("Rs. " + data.getPriceMrp());
            tvPriceDrop.setPaintFlags(tvPriceDrop.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvPrice.setText("Rs. " + data.getPriceDiscount());
            if (data.getTotalAvailable() > 0)
                tvAvailable.setText("In-Stock");
            else
                tvAvailable.setText("Out Of Stock");
            if (data.getPrescriptionRequired() == true)
                tvPreRequired.setVisibility(View.VISIBLE);
            else
                tvPreRequired.setVisibility(View.GONE);
            tvProductType.setText(data.getProductType());
            tvPower.setText(data.getPower());
            tvQty2.setText(data.getPackagingContain());
            tvQty3.setText("Contains " + data.getPackagingContain());
            tvCategory.setText(data.getCategoryName());
            tvDesciption.setText(data.getShortDescription());
            if (!data.getImage().equalsIgnoreCase("")) {
                Glide.with(context)
                        .load(data.getImage())
                        .crossFade()
                        .into(imageThumb);
                imageThumb.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        showPreviewDialog();
                        return false;
                    }
                });
            }
        }
    }

    private void showPreviewDialog() {
        Dialog dialog1 = new Dialog(context, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_preview_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);
        TouchImageView imgPreview = (TouchImageView) dialog1.findViewById(R.id.imgPreview);
        imgPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPreview.setImageResource(R.drawable.no_image_placeholder);
        Glide.with(context)
                .load(data.getImage())
                .placeholder(R.drawable.no_image_placeholder)
                .into(imgPreview);
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
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private void init() {
        tvName = (TextView) findViewById(R.id.tvName);
        tvManufacturer = (TextView) findViewById(R.id.tvManufacturer);
        tvDiscount = (TextView) findViewById(R.id.tvDiscount);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPriceDrop = (TextView) findViewById(R.id.tvPriceDrop);
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
                if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                    double item_total = Integer.parseInt(tvQty.getText().toString()) * Double.parseDouble(data.getPriceDiscount());
                    CartData model = new CartData(
                            data.getProductId(),
                            data.getName(),
                            data.getImage(),
                            Integer.parseInt(tvQty.getText().toString()),
                            Double.parseDouble(data.getPriceDiscount()),
                            item_total,
                            data.getPrescriptionRequired()
                    );
                    if (new DBHelper(context).addToCart(model)) {
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        MainActivity.setupBadge();
                    } else
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, LoginActivity.class));
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
}
