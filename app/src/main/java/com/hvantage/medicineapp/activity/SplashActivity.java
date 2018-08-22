package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.services.DBService;
import com.hvantage.medicineapp.util.AppPreferences;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 3 * 1000;
    private static final String TAG = "SplashActivity";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            startService(new Intent(context, DBService.class));
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
}
