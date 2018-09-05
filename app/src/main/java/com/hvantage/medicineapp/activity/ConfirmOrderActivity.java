package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.ConfirmOrderItemAdapter;
import com.hvantage.medicineapp.adapter.ConfirmOrderPrescAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.AddressData;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmOrderActivity";
    //    ArrayList<PrescriptionModel> presList = new ArrayList<PrescriptionModel>();
    private Context context;
    private ProgressBar progressBar;
    private TextView tvTotalPrice, tvPayableAmt;
    private TextView tvCheckout;
    private LinearLayout llPrescription, llMedicine, llAmount;
    private LinearLayout llPayMode;
    private AddressData addressData;
    private TextView tvAddress1, tvAddress2, tvAddress3, tvAddress4;
    private TextView tvChangeAddress;
    private Double taxes = 0.0;
    private Double delivery_fee = 30.0;
    private String payment_mode = "Cash On Delivery";
    private RecyclerView recylcer_view;
    private ConfirmOrderPrescAdapter adapterPres;
    private ConfirmOrderItemAdapter adapterCart;
    private double total = 0;
    private RecyclerView recylcer_view_items;
    private int spacing = 30, spanCount = 3;
    private boolean includeEdge = true;
    private String selected_pres_id = "", selected_add_id = "";
    private EditText etNote;
    private RadioGroup rgOrderType;
    private String orderType = "1";
    private ArrayList<CartData> cartList = new ArrayList<CartData>();
    private double payable_amt = 0.0, total_amt = 0.0;
    private ArrayList<PrescriptionData> prescList = new ArrayList<PrescriptionData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_item);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selected_pres_id = AppPreferences.getSelectedPresId(context);
        selected_add_id = AppPreferences.getSelectedAddId(context);
        Log.e(TAG, "onCreate: selected_pres_id >> " + selected_pres_id);
        Log.e(TAG, "onCreate: selected_add_id >> " + selected_add_id);
        if (!AppPreferences.getUserId(context).equalsIgnoreCase("")) {
            if (getIntent().hasExtra("data"))
                addressData = (AddressData) getIntent().getParcelableExtra("data");
            else {
                startActivity(new Intent(context, SelectAddressActivity.class));
                finish();
            }

            Log.e(TAG, "onCreate: addressData >> " + addressData);
            init();
            if (CartActivity.selectedPresc != null) {
                prescList.add(CartActivity.selectedPresc);
                Log.e(TAG, "onCreate: prescList >> " + prescList);
                setRecyclerView();
                llPrescription.setVisibility(View.VISIBLE);
                llAmount.setVisibility(View.GONE);
                llPayMode.setVisibility(View.GONE);
            } else {
                llPrescription.setVisibility(View.GONE);
                llAmount.setVisibility(View.VISIBLE);
                llPayMode.setVisibility(View.VISIBLE);
            }

            cartList = new DBHelper(context).getCartData();
            if (cartList != null) {
                if (cartList.size() > 0) {
                    Log.e(TAG, "onCreate: cartList >> " + cartList);
                    llMedicine.setVisibility(View.VISIBLE);
                    setRecyclerViewCart();
                    for (int i = 0; i < cartList.size(); i++) {
                        payable_amt = Functions.roundTwoDecimals(payable_amt + cartList.get(i).getItem_total_price());
                    }
                    payable_amt = payable_amt + delivery_fee;
                    total_amt = payable_amt;
                    tvPayableAmt.setText("Rs. " + payable_amt + "");
                    tvTotalPrice.setText("Rs. " + total_amt + "");
                } else {
                    llMedicine.setVisibility(View.GONE);
                    llAmount.setVisibility(View.GONE);
                    llPayMode.setVisibility(View.GONE);
                }
            } else {
                llMedicine.setVisibility(View.GONE);
                llAmount.setVisibility(View.GONE);
                llPayMode.setVisibility(View.GONE);
            }

           /* if (AppPreferences.getOrderType(context) == AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION) {
                llPayMode.setVisibility(View.GONE);
            } else {
                llPayMode.setVisibility(View.VISIBLE);
            }*/

            if (addressData != null) {
                tvAddress1.setText(addressData.getName() + ", +91" + addressData.getContactNo());
                tvAddress2.setText(addressData.getAddress() + ", " + addressData.getLandmark());
                tvAddress3.setText(addressData.getCity() + ", " + addressData.getPincode());
                tvAddress4.setText(addressData.getState() + ", India");
            } else {
                startActivity(new Intent(context, SelectAddressActivity.class));
                finish();
            }
        } else {
            Toast.makeText(context, "Please Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(context, LoginActivity.class));
        }
    }

    private void init() {
        llPrescription = (LinearLayout) findViewById(R.id.llPrescription);
        etNote = (EditText) findViewById(R.id.etNote);
        tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        tvPayableAmt = (TextView) findViewById(R.id.tvPayableAmt);
        tvCheckout = (TextView) findViewById(R.id.tvCheckout);
        tvAddress1 = (TextView) findViewById(R.id.tvAddress1);
        tvAddress2 = (TextView) findViewById(R.id.tvAddress2);
        tvAddress3 = (TextView) findViewById(R.id.tvAddress3);
        tvAddress4 = (TextView) findViewById(R.id.tvAddress4);
        rgOrderType = findViewById(R.id.rgOrderType);
        tvChangeAddress = (TextView) findViewById(R.id.tvChangeAddress);
        llMedicine = (LinearLayout) findViewById(R.id.llMedicine);
        llAmount = (LinearLayout) findViewById(R.id.llAmount);
        llPayMode = (LinearLayout) findViewById(R.id.llPayMode);
        tvCheckout.setOnClickListener(this);
        tvChangeAddress.setOnClickListener(this);
        rgOrderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rgOrderType.getCheckedRadioButtonId() == R.id.rbDelivery)
                    orderType = "1";
                else if (rgOrderType.getCheckedRadioButtonId() == R.id.rbTakeAway)
                    orderType = "2";
            }
        });
    }


    private void setRecyclerView() {
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapterPres = new ConfirmOrderPrescAdapter(context, prescList, null);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapterPres);
        adapterPres.notifyDataSetChanged();
    }

    private void setRecyclerViewCart() {
        recylcer_view_items = (RecyclerView) findViewById(R.id.recylcer_view_items);
        adapterCart = new ConfirmOrderItemAdapter(context, cartList);
        recylcer_view_items.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_items.setAdapter(adapterCart);
        adapterCart.notifyDataSetChanged();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCheckout:
                dialogOrderSent();
               /* if (Functions.isConnectingToInternet(context)) {
                    if (AppPreferences.getSelectedPresId(context).equalsIgnoreCase("")) {
                        new OrderTaskWithout().execute();
                    } else {
                        new OrderTask().execute();
                    }
                } else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                }*/
                break;
            case R.id.tvChangeAddress:
                startActivity(new Intent(context, SelectAddressActivity.class));
                break;
        }
    }

    class OrderTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.PLACE_ORDER_WITH_PRESCRIPTION);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("prescription_id", selected_pres_id);
            jsonObject.addProperty("address_id", selected_add_id);
            jsonObject.addProperty("order_type", selected_add_id);
            jsonObject.addProperty("payment_mode", orderType);
            jsonObject.addProperty("payment_type", "cash");
            jsonObject.addProperty("note", etNote.getText().toString());
            jsonObject.add("additional_items", new GsonBuilder().create().toJsonTree(cartList).getAsJsonArray());
            jsonObject.addProperty("payable_amount", 0);
            jsonObject.addProperty("cod_charges ", 0);
            jsonObject.addProperty("gst_tax_amt", 0);
            jsonObject.addProperty("other_tax_amt", 0);
            jsonObject.addProperty("total_amount", 0);
            jsonObject.addProperty("promo_code_applied", "");

            Log.e(TAG, "OrderTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "OrderTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class OrderTaskWithout extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.PLACE_ORDER_WITHOUT_PRESCRIPTION);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("prescription_id", "0");
            jsonObject.addProperty("address_id", selected_add_id);
            jsonObject.addProperty("order_type", selected_add_id);
            jsonObject.addProperty("payment_mode", orderType);
            jsonObject.addProperty("payment_type", "cash");
            jsonObject.addProperty("note", etNote.getText().toString());
            jsonObject.add("additional_items", new GsonBuilder().create().toJsonTree(cartList).getAsJsonArray());
            jsonObject.addProperty("payable_amount", payable_amt);
            jsonObject.addProperty("cod_charges ", delivery_fee);
            jsonObject.addProperty("gst_tax_amt", 0);
            jsonObject.addProperty("other_tax_amt", 0);
            jsonObject.addProperty("total_amount", total_amt);
            jsonObject.addProperty("promo_code_applied", "");

            Log.e(TAG, "OrderTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "OrderTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dialogOrderSent() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_order_sent, null);
        AppCompatButton btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        alertDialog.show();
    }


}
