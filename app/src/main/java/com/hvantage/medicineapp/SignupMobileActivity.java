package com.hvantage.medicineapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hvantage.medicineapp.util.Functions;

public class SignupMobileActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView btnSubmit;
    private EditText etMobNo;
    private Context context;
    private TextView btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_mobile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

    }

    private void init() {
        etMobNo = (EditText) findViewById(R.id.etMobNo);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(SignupMobileActivity.this, view);
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
                if (TextUtils.isEmpty(etMobNo.getText().toString()))
                    Toast.makeText(this, "Enter Mobile No.", Toast.LENGTH_SHORT).show();
                else if (etMobNo.getText().toString().length() < 10)
                    Toast.makeText(this, "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
                else {
                    startActivity(new Intent(context, SignupOTPActivity.class).putExtra("data", etMobNo.getText().toString()));
                    finish();
                }
                break;
            case R.id.btnLogin:
                startActivity(new Intent(context, LoginActivity.class));
                finish();
                break;
        }
    }
}
