package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.AddressData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddAddressActivity";
    private CardView btnSubmit;
    private ProgressBar progressBar;
    private Context context;
    private EditText etAddress, etLandmark, etState, etCity, etPostalCode, etName, etPhoneNo;
    private AddressData newData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init() {
        etName = (EditText) findViewById(R.id.etName);
        etPhoneNo = (EditText) findViewById(R.id.etPhoneNo);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etLandmark = (EditText) findViewById(R.id.etLandmark);
        etState = (EditText) findViewById(R.id.etState);
        etCity = (EditText) findViewById(R.id.etCity);
        etPostalCode = (EditText) findViewById(R.id.etPostalCode);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        etName.setText(AppPreferences.getUserName(context));
        etPhoneNo.setText(AppPreferences.getMobileNo(context));
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
            case R.id.btnSubmit:
                if (TextUtils.isEmpty(etName.getText().toString()))
                    Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPhoneNo.getText().toString()))
                    Toast.makeText(this, "Enter Contact No", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etAddress.getText().toString()))
                    Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etLandmark.getText().toString()))
                    Toast.makeText(this, "Enter Landmark", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etState.getText().toString()))
                    Toast.makeText(this, "Enter State", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etCity.getText().toString()))
                    Toast.makeText(this, "Enter City", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPostalCode.getText().toString()))
                    Toast.makeText(this, "Enter Postal Code", Toast.LENGTH_SHORT).show();
                else
                    new SaveTask().execute();
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

    class SaveTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.ADD_ADDRESS);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("address", etAddress.getText().toString());
            jsonObject.addProperty("city", etCity.getText().toString());
            jsonObject.addProperty("contact_no", etPhoneNo.getText().toString());
            jsonObject.addProperty("is_default", 0);
            jsonObject.addProperty("landmark", etLandmark.getText().toString());
            jsonObject.addProperty("name", etName.getText().toString());
            jsonObject.addProperty("pincode", etPostalCode.getText().toString());
            jsonObject.addProperty("state", etState.getText().toString());

            Log.e(TAG, "SaveTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.address(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SaveTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            newData = new Gson().fromJson(String.valueOf(jsonObject1), AddressData.class);
                            AppPreferences.setSelectedAddId(context, newData.getAddressId());
                            AppPreferences.setSelectedAdd(context, String.valueOf(newData));
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
                Log.e(TAG, "onProgressUpdate: AppPreferences.getSelectedAdd >> " + AppPreferences.getSelectedAdd(context));
                Intent intent = new Intent(context, ConfirmOrderActivity.class);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
