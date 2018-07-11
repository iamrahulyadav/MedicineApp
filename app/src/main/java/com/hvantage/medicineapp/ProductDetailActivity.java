package com.hvantage.medicineapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hvantage.medicineapp.adapter.HomeProductAdapter;
import com.hvantage.medicineapp.model.ProductModel;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    ArrayList<ProductModel> productList = new ArrayList<ProductModel>();
    private TextView toolbar_title;
    private Context context;
    private RecyclerView recylcer_view;
    private HomeProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText("Horlicks Chocolate Delight");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
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

        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        productList.add(new ProductModel("1", "Horlicks Chocolate Delight", "199", "500gm", ""));
        adapter.notifyDataSetChanged();
    }


    private void init() {
      /*  btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(context, view);
                return false;
            }
        });*/
    }

}
