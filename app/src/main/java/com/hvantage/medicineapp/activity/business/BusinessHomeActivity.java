package com.hvantage.medicineapp.activity.business;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DrugModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class BusinessHomeActivity extends AppCompatActivity {

    private static final String TAG = "BusinessHomeActivity";
    private FloatingActionButton fabAdd;
    private RecyclerView recylcer_view;
    private ArrayList<DrugModel> list = new ArrayList<DrugModel>();
    private Context context;
    private BusinessProductAdapter adapter;
    private ProgressBar progressBar;
    private CardView cardEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setRecyclerView();
        getData();
    }

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                .orderByChild("name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            DrugModel data = postSnapshot.getValue(DrugModel.class);
                            Log.e(TAG, "onDataChange: data >> " + data);
                            list.add(data);
                        }
                        adapter.notifyDataSetChanged();
                        if (adapter.getItemCount() > 0)
                            cardEmptyText.setVisibility(View.GONE);
                        else
                            cardEmptyText.setVisibility(View.VISIBLE);
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        if (adapter.getItemCount() > 0)
                            cardEmptyText.setVisibility(View.GONE);
                        else
                            cardEmptyText.setVisibility(View.VISIBLE);
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


    private void setRecyclerView() {
        adapter = new BusinessProductAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, AddProductActivity.class);
                intent.putExtra("data", list.get(position));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapter.notifyDataSetChanged();
    }


    private void init() {
        cardEmptyText = (CardView) findViewById(R.id.cardEmptyText);
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BusinessHomeActivity.this, AddProductActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}