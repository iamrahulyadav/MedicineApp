package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = "CartActivity";
    public static PrescriptionData selectedPresc = null;
    private Context context;
    private ArrayList<CartData> list = new ArrayList<CartData>();
    private RecyclerView recylcer_view;
    private ProgressBar progressBar;
    private CartItemAdapter adapter;
    private double total = 0;
    private TextView tvTotalPrice, tvPayableAmt;
    private RelativeLayout btnSubmit;
    private LinearLayout llAmount;
    private CardView cardPrescription;
    private boolean prescription_required = false;
    private ImageView imgThumb;
    private TextView tvTitle, tvDate, tvHideShow;
    private CardView btnChange, btnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        context = this;
        selectedPresc = null;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
            list = new DBHelper(context).getCartData();
            Log.e(TAG, "onCreate: list >> " + list);
            if (list != null) {
                setRecyclerView();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isIs_prescription_required() == true) {
                        prescription_required = true;
                        break;
                    }
                }
            } else {
                btnSubmit.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prescription_required == true && selectedPresc != null) {
            prescription_required = false;
            cardPrescription.setVisibility(View.VISIBLE);
            if (!selectedPresc.getImage().equalsIgnoreCase("")) {
                if (!selectedPresc.getDiagnosisDetails().equalsIgnoreCase(""))
                    tvTitle.setText(selectedPresc.getDiagnosisDetails());
                else
                    tvTitle.setText("No Details");
                if (selectedPresc.getDate() != null)
                    tvDate.setText(selectedPresc.getDate());
                else
                    tvDate.setText(Functions.getCurrentDate());
                if (selectedPresc.getImage().contains(".jpg") || selectedPresc.getImage().contains(".png")) {
                    Glide.with(context)
                            .load(selectedPresc.getImage())
                            .crossFade()
                            .into(imgThumb);
                } else {
                    imgThumb.setImageBitmap(Functions.base64ToBitmap(selectedPresc.getImage()));
                }
            }
        } else
            cardPrescription.setVisibility(View.GONE);
    }

    private void showAlertDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Upload Your Prescription")
                .setMessage("One or more items in your cart requires a valid prescription with doctor's name and drug detail clearly visible.")
                .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(context, AddPrescActivity.class));
                    }
                })
                .setNegativeButton("Choose Existing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(context, SelectPrescActivity.class));
                    }
                })
                .show();
    }

    private void init() {
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvPayableAmt = (TextView) findViewById(R.id.tvPayableAmt);
        llAmount = (LinearLayout) findViewById(R.id.llAmount);
        btnSubmit = (RelativeLayout) findViewById(R.id.btnSubmit);
        cardPrescription = findViewById(R.id.cardPrescription);
        tvHideShow = findViewById(R.id.tvHideShow);
        imgThumb = findViewById(R.id.imgThumb);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        btnView = findViewById(R.id.btnView);
        btnChange = findViewById(R.id.btnChange);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prescription_required) {
                    showAlertDialog();
                } else {
                    AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITHOUT_PRESCRIPTION);
                    startActivity(new Intent(context, SelectAddressActivity.class));
                }
            }
        });
        tvHideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvHideShow.getText().toString().equalsIgnoreCase("Hide")) {
                    ((findViewById(R.id.rContain))).setVisibility(View.GONE);
                    tvHideShow.setText("View");
                } else {
                    ((findViewById(R.id.rContain))).setVisibility(View.VISIBLE);
                    tvHideShow.setText("Hide");
                }
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
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
