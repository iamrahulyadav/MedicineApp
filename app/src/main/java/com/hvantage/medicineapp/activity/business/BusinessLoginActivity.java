package com.hvantage.medicineapp.activity.business;

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
import android.widget.Toast;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.util.Functions;


public class BusinessLoginActivity extends AppCompatActivity {

    private CardView btnLogin;
    private EditText etPhoneNo;
    private EditText etPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        etPhoneNo = (EditText) findViewById(R.id.etPhoneNo);
        etPwd = (EditText) findViewById(R.id.etPwd);
        btnLogin = (CardView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(etPhoneNo.getText().toString()))
                    Toast.makeText(BusinessLoginActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPwd.getText().toString()))
                    Toast.makeText(BusinessLoginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                else if (etPhoneNo.getText().toString().equalsIgnoreCase("rk123") && etPwd.getText().toString().equalsIgnoreCase("123")) {
                    startActivity(new Intent(BusinessLoginActivity.this, BusinessHomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    Toast.makeText(BusinessLoginActivity.this, "Check your business credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(BusinessLoginActivity.this, view);
                return false;
            }
        });
    }
}
