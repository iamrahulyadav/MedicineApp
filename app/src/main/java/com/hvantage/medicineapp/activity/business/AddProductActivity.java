package com.hvantage.medicineapp.activity.business;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DrugModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.ProgressBar;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddProductActivity";
    private CardView btnSubmit;
    private EditText etTitle, etManufacturer, etProductType, etCategoryName, etPower, etQty, etPrice, etDescription;
    private CheckBox checkBox;
    private boolean prescription_required = false;
    private ProgressBar progressBar;
    private Context context;
    private TextView toolbar_title;
    private DrugModel data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

        if (getIntent().hasExtra("data")) {
            data = (DrugModel) getIntent().getSerializableExtra("data");
        }
        Log.e(TAG, "onCreate: data >> " + data);
        if (data != null) {
            toolbar_title.setText("Edit Drug");
            etTitle.setText(data.getName());
            etManufacturer.setText(data.getManufacturer());
            etProductType.setText(data.getProduct_type());
            etCategoryName.setText(data.getCategory_name());
            etPower.setText(data.getPower());
            etQty.setText(data.getQty());
            etPrice.setText("" + data.getPrice());
            etDescription.setText(data.getDesciption());
            checkBox.setChecked(data.getPrescription_required());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        etTitle = (EditText) findViewById(R.id.etTitle);
        etManufacturer = (EditText) findViewById(R.id.etManufacturer);
        etProductType = (EditText) findViewById(R.id.etProductType);
        etCategoryName = (EditText) findViewById(R.id.etCategoryName);
        etPower = (EditText) findViewById(R.id.etPower);
        etQty = (EditText) findViewById(R.id.etQty);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etDescription = (EditText) findViewById(R.id.etDescription);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (TextUtils.isEmpty(etTitle.getText().toString()))
                    Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etManufacturer.getText().toString()))
                    Toast.makeText(this, "Enter Manufacturer", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etProductType.getText().toString()))
                    Toast.makeText(this, "Enter Product Type", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etManufacturer.getText().toString()))
                    Toast.makeText(this, "Enter Manufacturer", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etCategoryName.getText().toString()))
                    Toast.makeText(this, "Enter Category Name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPower.getText().toString()))
                    Toast.makeText(this, "Enter Power", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etQty.getText().toString()))
                    Toast.makeText(this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPrice.getText().toString()))
                    Toast.makeText(this, "Enter Price", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etDescription.getText().toString()))
                    Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
                else {
                    saveData();
                }
                break;
        }

    }

    private void saveData() {
        Log.e(TAG, "saveData: ");
        showProgressDialog();
        String key = FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                .push().getKey();
        if (checkBox.isChecked())
            prescription_required = true;
        DrugModel model = new DrugModel(
                key,
                etTitle.getText().toString(),
                etManufacturer.getText().toString(),
                etProductType.getText().toString(),
                etCategoryName.getText().toString(),
                etPower.getText().toString(),
                etQty.getText().toString(),
                Double.parseDouble(etPrice.getText().toString()),
                etDescription.getText().toString(),
                prescription_required
        );

        FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.MEDICINE)
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

}