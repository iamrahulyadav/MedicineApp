package com.hvantage.medicineapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.hvantage.medicineapp.util.Functions;

public class SignupOTPActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupOTPActivity";
    private String mobileNo;
    private Context context;
    private TextView tvOtpText;
    private CardView btnSubmit;
    private EditText etOTP;
    private TextView btnResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_otp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        mobileNo = getIntent().getStringExtra("data");
        Log.e(TAG, "onCreate: mobileNo >> " + mobileNo);
        tvOtpText.setText("Enter the OTP sent to number +91" + mobileNo);
    }

    private void init() {
        tvOtpText = (TextView) findViewById(R.id.tvOtpText);
        btnResend = (TextView) findViewById(R.id.btnResend);
        etOTP = (EditText) findViewById(R.id.etOTP);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(context, view);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (TextUtils.isEmpty(etOTP.getText().toString()))
                    Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
                else {
                    startActivity(new Intent(context, SignupProfileActivity.class));
                    finish();
                }
                break;
            case R.id.btnResend:
                startActivity(new Intent(context, SignupMobileActivity.class));
                finish();
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
