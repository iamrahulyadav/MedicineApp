package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    private Context context;
    private ArrayList<CartData> list = new ArrayList<CartData>();
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
        if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
            list = new DBHelper(context).getCartData();
            if (list != null)
                setRecyclerView();
            else {
                btnSubmit.setVisibility(View.GONE);
            }
        } else {
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
                startActivity(new Intent(context, SelectAddressActivity.class));
            }
        });
    }

    private void setRecyclerView() {
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapter = new CartItemAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 0)
            btnSubmit.setVisibility(View.VISIBLE);

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
}
