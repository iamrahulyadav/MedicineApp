package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.HomeProductAdapter;
import com.hvantage.medicineapp.model.ItemModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.Functions;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String TAG = "ProductDetailActivity";
    ArrayList<ItemModel> productList = new ArrayList<ItemModel>();
    TextView tvName, tvManufacturer, tvPrice, tvAvailable, tvQty, tvPreRequired, tvProductType, tvPower, tvCategory, tvDesciption;
    private TextView toolbar_title;
    private Context context;
    private RecyclerView recylcer_view;
    private HomeProductAdapter adapter;
    private ProductModel data;
    private ImageView imageThumb;

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
            tvQty.setText(data.getQty());
            tvCategory.setText(data.getCategory_name());
            tvDesciption.setText(data.getDescription());
            if (!data.getImage().equalsIgnoreCase(""))
                imageThumb.setImageBitmap(Functions.base64ToBitmap(data.getImage()));
        }
        setProduct();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
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

        productList.add(new ItemModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ItemModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ItemModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ItemModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ItemModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        adapter.notifyDataSetChanged();
    }

    private void init() {
        tvName = (TextView) findViewById(R.id.tvName);
        tvManufacturer = (TextView) findViewById(R.id.tvManufacturer);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvAvailable = (TextView) findViewById(R.id.tvAvailable);
        tvQty = (TextView) findViewById(R.id.tvQty2);
        tvPreRequired = (TextView) findViewById(R.id.tvPreRequired);
        tvProductType = (TextView) findViewById(R.id.tvProductType);
        tvPower = (TextView) findViewById(R.id.tvPower);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDesciption = (TextView) findViewById(R.id.tvDesciption);
        imageThumb = (ImageView) findViewById(R.id.imageThumb);
    }
}
