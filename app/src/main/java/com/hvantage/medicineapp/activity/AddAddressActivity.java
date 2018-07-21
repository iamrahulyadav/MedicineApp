package com.hvantage.medicineapp.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.AddressModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.ProgressBar;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddAddressActivity";
    private CardView btnSubmit;
    private ProgressBar progressBar;
    private Context context;

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

    private EditText etAddress, etLandmark, etState, etCity, etPostalCode, etName, etPhoneNo;

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
                    saveData();

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

    private void saveData() {
        Log.e(TAG, "saveData: ");
        showProgressDialog();
        String key = FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                .push().getKey();

        AddressModel model = new AddressModel(
                etName.getText().toString(),
                etPhoneNo.getText().toString(),
                key,
                etPostalCode.getText().toString(),
                etAddress.getText().toString(),
                etLandmark.getText().toString(),
                etCity.getText().toString(),
                etState.getText().toString(),
                false
        );

        FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.ADDRESS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(key)
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                        finish();
                        Log.e(TAG, "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error writing document", e);
            }
        });
    }
}
