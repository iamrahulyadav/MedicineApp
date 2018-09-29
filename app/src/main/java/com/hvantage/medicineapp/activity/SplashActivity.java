package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 3 * 1000;
    private static final String TAG = "SplashActivity";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_splash);
        context = this;
        try {

            if (Functions.isConnectingToInternet(context)) {
                new AsyncTask<Void, String, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("method", AppConstants.METHODS.GET_SLIDER_IMAGES);
                        Log.e(TAG, "GetSliderTask: Request >> " + jsonObject.toString());

                        MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
                        Call<JsonObject> call = apiService.general(jsonObject);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    Log.e(TAG, "GetSliderTask: Response >> " + response.body().toString());
                                    String resp = response.body().toString();
                                    JSONObject jsonObject = new JSONObject(resp);
                                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                                        //AppPreferences.setSliderData(context, String.valueOf(jsonArray));
                                    } else {
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "GetSliderTask: onFailure: " + e.getMessage());
                                } catch (Exception e) {
                                    Log.e(TAG, "GetSliderTask: onFailure: " + e.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e(TAG, "GetSliderTask: onFailure: " + t.getMessage());
                            }
                        });
                        return null;
                    }
                }.execute();
            } else {
//                ErrorDialog.setDialog(SplashActivity.this, getString(R.string.api_error_msg), errorListener);
            }
        } catch (Exception ex) {
            Log.e(TAG, "run: exc >> " + ex.getMessage());
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppPreferences.getUserId(context).equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    DialogInterface.OnClickListener errorListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(context, "Clieckeddddd", Toast.LENGTH_SHORT).show();
        }

    };


}
