package com.hvantage.medicineapp.activity.business;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.BuildConfig;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AddProductCatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddProductCatActivity";
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    ArrayList<String> subCatList = new ArrayList<String>();
    private CardView btnSubmit;
    private EditText etTitle, etManufacturer, etProductType, etCategoryName, etPower, etQty, etPrice, etDescription;
    private CheckBox checkBox;
    private boolean prescription_required = false;
    private ProgressBar progressBar;
    private Context context;
    private TextView toolbar_title;
    private ProductModel data = null;
    private ImageView imageDrug;
    private String image_base64 = "";
    private Spinner spinnerCat;
    private Spinner spinnerSubCat;
    private ArrayAdapter<String> adapter;
    private String category_name = "", sub_category_name = "";
    private EditText etTotalAvailable;
    private View viewLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_cat);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setSubCatAdapter();
        if (getIntent().hasExtra("data")) {
            data = (ProductModel) getIntent().getSerializableExtra("data");
        }
        Log.e(TAG, "onCreate: data >> " + data);
        if (data != null) {
            toolbar_title.setText("Edit Product");
            etTitle.setText(data.getName());
            etManufacturer.setText(data.getManufacturer());
            etProductType.setText(data.getProduct_type());
            etCategoryName.setText(data.getCategory_name());
            etPower.setText(data.getPower());
            etQty.setText(data.getQty());
            etPrice.setText("" + data.getPrice());
            etTotalAvailable.setText("" + data.getTotal_available());
            etDescription.setText(data.getDescription());
            checkBox.setChecked(data.isPrescription_required());
            image_base64 = data.getImage();
            if (!image_base64.equalsIgnoreCase(""))
                imageDrug.setImageBitmap(Functions.base64ToBitmap(image_base64));

            if (data.getCategory_name().equalsIgnoreCase("Prescription")) {
                spinnerCat.setSelection(0);
                spinnerSubCat.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);

            } else if (!data.getCategory_name().equalsIgnoreCase("") && !data.getCategory_name().equalsIgnoreCase("")) {
                String[] arrayCat = getResources().getStringArray(R.array.categories);
                for (int i = 0; i < arrayCat.length; i++) {
                    if (arrayCat[i].equalsIgnoreCase(data.getCategory_name()))
                        spinnerCat.setSelection(i);
                }
            }


        }
    }

    private void setSubCatAdapter() {
        subCatList.add("Select Subcategory");
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, subCatList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubCat.setAdapter(adapter);
    }

    private void getCat() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CATEGORY)
                .child((String) spinnerCat.getSelectedItem())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subCatList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: data >> " + postSnapshot.getKey());
                            subCatList.add(postSnapshot.getKey());
                        }
                        adapter.notifyDataSetChanged();
                        if (data != null && !data.getSub_category_name().equalsIgnoreCase("")) {
                            for (int i = 0; i < subCatList.size(); i++) {
                                if (subCatList.get(i).equalsIgnoreCase(data.getSub_category_name()))
                                    spinnerSubCat.setSelection(i);
                            }
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
                    }
                });
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
        etTotalAvailable = (EditText) findViewById(R.id.etTotalAvailable);
        imageDrug = (ImageView) findViewById(R.id.imageDrug);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        spinnerCat = (Spinner) findViewById(R.id.spinnerCat);
        spinnerSubCat = (Spinner) findViewById(R.id.spinnerSubCat);
        viewLine = (View) findViewById(R.id.viewLine);
        btnSubmit.setOnClickListener(this);
        imageDrug.setOnClickListener(this);
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    getCat();
                    spinnerSubCat.setVisibility(View.VISIBLE);
                    viewLine.setVisibility(View.VISIBLE);
                } else {
                    spinnerSubCat.setVisibility(View.GONE);
                    viewLine.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (spinnerCat.getSelectedItemPosition() > 0) {
                    if (spinnerSubCat.getSelectedItem().toString().equalsIgnoreCase("Select Subcategory")) {
                        Toast.makeText(this, "Select Subcategory", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (TextUtils.isEmpty(etTitle.getText().toString()))
                    Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etManufacturer.getText().toString()))
                    Toast.makeText(this, "Enter Manufacturer", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etProductType.getText().toString()))
                    Toast.makeText(this, "Enter Product Type", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etManufacturer.getText().toString()))
                    Toast.makeText(this, "Enter Manufacturer", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPower.getText().toString()))
                    Toast.makeText(this, "Enter Power", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etQty.getText().toString()))
                    Toast.makeText(this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPrice.getText().toString()))
                    Toast.makeText(this, "Enter Price", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etDescription.getText().toString()))
                    Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etTotalAvailable.getText().toString()))
                    Toast.makeText(this, "Enter Total Available", Toast.LENGTH_SHORT).show();
                else {
                    if (spinnerCat.getSelectedItemPosition() == 0) {
                        category_name = spinnerCat.getSelectedItem().toString();
                        sub_category_name = "";
                    } else {
                        category_name = spinnerCat.getSelectedItem().toString();
                        sub_category_name = spinnerSubCat.getSelectedItem().toString();
                    }
                    if (data == null)
                        saveData();
                    else
                        updateData();
                }
                break;
            case R.id.imageDrug:
                selectImage();
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

        ProductModel model = new ProductModel(
                key,
                etTitle.getText().toString(),
                etManufacturer.getText().toString(),
                etProductType.getText().toString(),
                category_name,
                sub_category_name,
                etPower.getText().toString(),
                etQty.getText().toString(),
                etDescription.getText().toString(),
                image_base64,
                Double.parseDouble(etPrice.getText().toString()),
                prescription_required,
                Integer.parseInt(etTotalAvailable.getText().toString())
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

    private void updateData() {
        Log.e(TAG, "updateData: ");
        showProgressDialog();
        String key = data.getKey();
        Log.e(TAG, "updateData: key >> " + key);

        if (checkBox.isChecked())
            prescription_required = true;
        ProductModel model = new ProductModel(
                key,
                etTitle.getText().toString(),
                etManufacturer.getText().toString(),
                etProductType.getText().toString(),
                category_name,
                sub_category_name,
                etPower.getText().toString(),
                etQty.getText().toString(),
                etDescription.getText().toString(),
                image_base64,
                Double.parseDouble(etPrice.getText().toString()),
                prescription_required,
                Integer.parseInt(etTotalAvailable.getText().toString())
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

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Camera")) {
                    cameraIntent();
                } else if (items[item].equals("Gallery")) {
                    galleryIntent();
                }
            }
        });
        builder.show();
    }

    @Nullable
    private Intent createPickIntent() {
        Intent picImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (picImageIntent.resolveActivity(getPackageManager()) != null) {
            return picImageIntent;
        } else {
            return null;
        }
    }

    private void cameraIntent() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/M4D/";
        File newdir = new File(dir);
        newdir.mkdirs();
        String file = dir + "report_img.jpg";
        Log.e("imagesss cam11", file);
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Uri outputFileUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            outputFileUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Log.e(TAG, "cameraIntent: outputFileUri >> " + outputFileUri);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void galleryIntent() {
        startActivityForResult(createPickIntent(), REQUEST_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_IMAGE && data != null) {
                startCropImageActivity(data.getData());
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File croppedImageFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/M4D/" + "report_img.jpg");
                final Uri outputFileUri;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
                } else {
                    outputFileUri = Uri.fromFile(croppedImageFile1);
                }
                Log.e(TAG, " Inside REQUEST_IMAGE_CAPTURE uri :- " + outputFileUri);
                startCropImageActivity(outputFileUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageDrug.setImageBitmap(bitmap);
                    new ImageTask().execute(bitmap);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .setAspectRatio(1, 1)
                .setRequestedSize(100, 100)
                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                .start(this);
    }

    class ImageTask extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap bitmapImage = bitmaps[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            image_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d(TAG, "Picture Image :-" + image_base64);
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
        }
    }
}