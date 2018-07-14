package com.hvantage.medicineapp.activity.business;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.hvantage.medicineapp.R;

public class AddProductActivity extends AppCompatActivity {

    private CardView btnSubmit;
    private EditText etTitle, etManufacturer, etProductType, etCategoryName, etPower, etQty, etPrice, etDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        etTitle = (EditText) findViewById(R.id.etTitle);
        etManufacturer = (EditText) findViewById(R.id.etManufacturer);
        etProductType = (EditText) findViewById(R.id.etProductType);
        etCategoryName = (EditText) findViewById(R.id.etCategoryName);
        etPower = (EditText) findViewById(R.id.etPower);
        etQty = (EditText) findViewById(R.id.etQty);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etDescription = (EditText) findViewById(R.id.etDescription);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
    }
}