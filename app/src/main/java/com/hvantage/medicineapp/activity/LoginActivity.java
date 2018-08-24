package com.hvantage.medicineapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.UserData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_ALL_PERMISSIONS = 100;
    private EditText etPhoneNo;
    private EditText etPassword;
    private Context context;
    private ProgressBar progressBar;
    private CardView btnSubmit;
    private TextView btnSkip, btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        checkPermission();
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

    private void init() {
        etPhoneNo = (EditText) findViewById(R.id.etMobNo);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSkip = (TextView) findViewById(R.id.btnSkip);
        btnEnter = (TextView) findViewById(R.id.btnEnter);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnSkip.setOnClickListener(this);
        btnEnter.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(LoginActivity.this, view);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                performLogin();
                break;
            case R.id.btnSkip:
                startActivity(new Intent(context, SignupActivity.class));
                break;
            case R.id.btnEnter:
                startActivity(new Intent(context, MainActivity.class));
                break;
        }
    }


    private void performLogin() {
        if (TextUtils.isEmpty(etPhoneNo.getText().toString()))
            Snackbar.make(findViewById(android.R.id.content), "Enter Username", Snackbar.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(etPassword.getText().toString()))
            Snackbar.make(findViewById(android.R.id.content), "Enter Password", Snackbar.LENGTH_SHORT).show();
        else {
            new LoginTask().execute();
        }
    }

    class LoginTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.USER_LOGIN);
            jsonObject.addProperty("username", etPhoneNo.getText().toString());
            jsonObject.addProperty("password", etPassword.getText().toString());
            jsonObject.addProperty("fcm_token", FirebaseInstanceId.getInstance().getToken());

            Log.e(TAG, "LoginTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_register(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "LoginTask: Response >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status").getAsString().equals("200")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        UserData data = new Gson().fromJson(result, UserData.class);
                        Log.e(TAG, "onResponse: data >> " + data);
                        AppPreferences.setUserId(context, data.getUser_id());
                        AppPreferences.setMobileNo(context, data.getPhoneNo());
                        AppPreferences.setUserName(context, data.getName());
                        publishProgress("200", "");
                    } else {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        publishProgress("400", result.get("msg").getAsString());
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
            } else if (status.equalsIgnoreCase("400")) {
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                        },
                        REQUEST_ALL_PERMISSIONS);
            }
            return false;
        } else {
            return true;
        }
    }

}
