package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.AddressAdapter;
import com.hvantage.medicineapp.model.AddressModel;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class SelectAddressActivity extends AppCompatActivity {

    private static final String TAG = "SelectAddressActivity";
    ArrayList<PrescriptionModel> presList = new ArrayList<PrescriptionModel>();
    private Context context;
    private ArrayList<AddressModel> list = new ArrayList<AddressModel>();
    private AddressAdapter adapter;
    private RecyclerView recylcer_view;
    private ProgressBar progressBar;
    private CardView btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       /* if (getIntent().hasExtra("data"))
            presList = getIntent().getParcelableArrayListExtra("data");*/

       /* Bundle bundle = getIntent().getBundleExtra("data");   //<< get Bundle from Intent
        presList = bundle.getParcelableArrayList("listPresc");
        Log.e(TAG, "onCreate: presList >> " + presList);*/
        init();
        setRecyclerView();
        getData();

//        Toast.makeText(context, "Select Delivery Address", Toast.LENGTH_SHORT).show();
    }

    private void init() {
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddAddressActivity.class));
            }
        });
    }

    private void setRecyclerView() {
        list.clear();
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapter = new AddressAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, ConfirmOrderActivity.class);
                intent.putExtra("data", list.get(position));
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapter.notifyDataSetChanged();
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

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.ADDRESS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            AddressModel data = postSnapshot.getValue(AddressModel.class);
                            if (data != null) {
                                list.add(data);
                                adapter.notifyDataSetChanged();
                            }

                            /*if (adapter.getItemCount() > 1) {
                                tvInstructions.setVisibility(View.GONE);
                            } else
                                tvInstructions.setVisibility(View.GONE);
                            if (adapter.getItemCount() >= 3) {
                                btnUpload.setVisibility(View.GONE);
                            } else {
                                btnUpload.setVisibility(View.VISIBLE);
                            }*/
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        list.clear();
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
                    }
                });
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
