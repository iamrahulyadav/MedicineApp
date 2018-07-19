package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.services.DBService;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 5 * 1000;
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
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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

    private void getData() {
        if (Functions.isConnectingToInternet(context)) {
            Log.e(TAG, "getDataFromServer: deleteMedicineData() >> " + new DBHelper(context).deleteMedicineData());
            FirebaseDatabase.getInstance().getReference()
                    .child(AppConstants.APP_NAME)
                    .child(AppConstants.FIREBASE_KEY.MEDICINE)
                    .orderByChild("name")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                ProductModel data = postSnapshot.getValue(ProductModel.class);
                                new DBHelper(context).saveMedicine(data);
                            }
                            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "deleteMedicineData:onCancelled", databaseError.toException());

                        }
                    });
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

}
