package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.OfferDiscountAdapter;
import com.hvantage.medicineapp.model.OffersModel;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;

import static com.hvantage.medicineapp.activity.MainActivity.menuSearch;

public class OffersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OffersActivity";
    private Context context;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private OfferDiscountAdapter adapter;
    private ArrayList<OffersModel> list = new ArrayList<OffersModel>();
    private ProgressBar progressBar;
    private String data;
    private CardView cardEmptyText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (intraction != null) {
            intraction.actionbarsetTitle("Offers & Discounts");
            if (menuSearch != null)
                menuSearch.setVisible(true);
        }
        list.add(new OffersModel("FIRST50", "Use code FIRST50", "Get Rs. 50 off on your 1st Order. Minimun value Rs. 99. Max discount Rs. 100 per order", "31 August", ""));
        list.add(new OffersModel("FLAT20", "Use code FLAT20", "Get 20% off on every order. Minimun value Rs. 99. Max discount Rs. 100 per order", "31 August", ""));
        list.add(new OffersModel("", "Upto Rs. 350 cashback", "Get cashback upto Rs. 350 on all Medicine and Healthcare needs. Minimun value Rs. 300.", "31 August", ""));
        list.add(new OffersModel("BOGO", "Use code BOGO", "Buy 1 Daily Needs Product and Get 1 Free. Minimun value Rs. 300. Max", "25 August", ""));
        list.add(new OffersModel("", "Pay using paytm", "Get cashback upto Rs. 100 on paytm. Minimun value Rs. 200. Max discount Rs. 150 per order", "31 August", ""));
        list.add(new OffersModel("", "Pay using PhonePe", "Get cashback upto Rs. 100 on payments via PhonePe. Minimun value Rs. 200. Max discount Rs. 150 per order", "31 August", ""));
        init();
        setRecyclerView();
    }

    private void init() {
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        cardEmptyText = (CardView) findViewById(R.id.cardEmptyText);
    }

    private void setRecyclerView() {
        adapter = new OfferDiscountAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }
}
