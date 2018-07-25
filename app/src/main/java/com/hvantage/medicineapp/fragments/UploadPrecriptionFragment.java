package com.hvantage.medicineapp.fragments;

import android.app.Activity;
import android.app.Dialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.BuildConfig;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.DeliveryAddressActivity;
import com.hvantage.medicineapp.activity.LoginActivity;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.adapter.UploadedPreAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartModel;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.GridSpacingItemDecoration;
import com.hvantage.medicineapp.util.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class UploadPrecriptionFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final String TAG = "UploadPhotosActivity";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private UploadedPreAdapter adapterPres;
    private ArrayList<PrescriptionModel> presList = new ArrayList<PrescriptionModel>();
    private CardView btnUpload;
    private ProgressBar progressBar;
    private String userChoosenTask;
    private Dialog dialog;
    private int spacing = 30, spanCount = 3;
    private boolean includeEdge = true;
    private ArrayList<CartModel> cartList = new ArrayList<CartModel>();
    private CartItemAdapter adapterCart;
    private double total = 0;
    private RecyclerView recylcer_view_items;
    private TextView tvInstructions;
    private TextView tvCartItemEmpty;
    private CardView btnContinue;
    private AppCompatAutoCompleteTextView etSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_upload_prescription, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Upload Prescription");
        }
        init();
        list = new DBHelper(context).getMedicinesSearch();

        setRecyclerView();
        setRecyclerViewCart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            getData();
            getCartData();
            setSearchBar();

        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
        return rootView;
    }

    private ArrayList<String> list = new ArrayList<String>();


    private void setSearchBar() {
        if (list != null) {
            Log.e(TAG, "setSearchBar: list >> " + list.size());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.auto_complete_text, list);
            etSearch.setThreshold(1);
            etSearch.setAdapter(adapter);
            etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.e(TAG, "onItemClick: text >> " + etSearch.getText().toString());
                    if (Functions.isConnectingToInternet(context)) {
                        showProgressDialog();
                        FirebaseDatabase.getInstance().getReference()
                                .child(AppConstants.APP_NAME)
                                .child(AppConstants.FIREBASE_KEY.MEDICINE)
                                .orderByChild("name")
                                .equalTo(etSearch.getText().toString())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        hideProgressDialog();
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            ProductModel data = postSnapshot.getValue(ProductModel.class);
                                            Log.e(TAG, "onDataChange: data >> " + data);
                                            startActivity(new Intent(context, ProductDetailActivity.class).putExtra("medicine_data", data));
                                            etSearch.setText("");
                                            break;
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        hideProgressDialog();
                                        Log.w(TAG, "onCancelled >> ", databaseError.toException());
                                    }
                                });
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    private void setRecyclerViewCart() {
        recylcer_view_items = (RecyclerView) rootView.findViewById(R.id.recylcer_view_items);
        adapterCart = new CartItemAdapter(context, cartList);
        recylcer_view_items.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_items.setAdapter(adapterCart);
        adapterCart.notifyDataSetChanged();
    }

    private void getCartData() {
        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CART)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.CART_ITEMS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cartList.clear();
                        total = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final CartModel model1 = postSnapshot.getValue(CartModel.class);
                            Log.e(TAG, "onDataChange: model1 >> " + model1);
                            FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                                    .child(AppConstants.FIREBASE_KEY.MEDICINE)
                                    .child(model1.getKey())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot1) {
                                            ProductModel model2 = dataSnapshot1.getValue(ProductModel.class);
                                            Log.e(TAG, "onDataChange: model2 >> " + model2);
                                            CartModel final_model = new CartModel();
                                            final_model.setKey(model1.getKey());
                                            final_model.setQty_no(model1.getQty_no());
                                            final_model.setItem(model2.getName());
                                            final_model.setImage(model2.getImage());
                                            final_model.setItem_price(String.valueOf(model2.getPrice()));
                                            final_model.setItem_total_price(String.valueOf(model1.getQty_no() * model2.getPrice()));
                                            cartList.add(final_model);
                                            adapterCart.notifyDataSetChanged();
                                            total = total + Double.parseDouble(final_model.getItem_total_price());
                                            if (adapterCart.getItemCount() > 0)
                                                tvCartItemEmpty.setVisibility(View.GONE);
                                            //tvTotalPrice.setText("Rs. " + total);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    }
                });
    }


    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CART)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.PRESCRIPTION)
                .orderByKey()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        presList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            PrescriptionModel data = postSnapshot.getValue(PrescriptionModel.class);
                            if (data != null) {
                                presList.add(data);
                                adapterPres.notifyDataSetChanged();
                            }

                            if (adapterPres.getItemCount() > 1) {
                                tvInstructions.setVisibility(View.GONE);
                            } else
                                tvInstructions.setVisibility(View.GONE);
                            if (adapterPres.getItemCount() >= 3) {
                                btnUpload.setVisibility(View.GONE);
                            } else {
                                btnUpload.setVisibility(View.VISIBLE);
                            }
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        presList.clear();
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
                    }
                });
    }

    private void init() {
        btnUpload = (CardView) rootView.findViewById(R.id.btnUpload);
        tvInstructions = (TextView) rootView.findViewById(R.id.tvInstructions);
        tvCartItemEmpty = (TextView) rootView.findViewById(R.id.tvCartItemEmpty);
        etSearch = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.etSearch);
        btnContinue = (CardView) rootView.findViewById(R.id.btnContinue);
        btnUpload.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    private void setRecyclerView() {
        presList.clear();
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        adapterPres = new UploadedPreAdapter(context, presList);
        recylcer_view.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        recylcer_view.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recylcer_view.setAdapter(adapterPres);
        adapterPres.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentIntraction) {
            intraction = (FragmentIntraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        intraction = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUpload:
                selectImage();
                break;
            case R.id.btnContinue:
                if (presList.isEmpty())
                    showNoPresAlert();
                else {
                    AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION);
                    startActivity(new Intent(context, DeliveryAddressActivity.class));
                }
                break;
        }

    }

    private void showNoPresAlert() {
        new AlertDialog.Builder(context)
                .setMessage("Please upload atleast one presciptions to order with prescription.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectImage();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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
                    userChoosenTask = "Camera";
                    cameraIntent();
                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    galleryIntent();
                }
            }
        });
        builder.show();
    }

    @Nullable
    private Intent createPickIntent() {
        Intent picImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (picImageIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            return picImageIntent;
        } else {
            return null;
        }
    }

    private void cameraIntent() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/medicineapp/";
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_IMAGE && data != null) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new ImageTask().execute(bitmap);

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
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), outputFileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new ImageTask().execute(bitmap);
            }
        }
    }

    class ImageTask extends AsyncTask<Bitmap, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap bitmapImage = bitmaps[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            publishProgress(Encoded_userimage);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String image_base64 = values[0];
            Log.d(TAG, "onProgressUpdate: image_base64 >> " + image_base64);
            String key = FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                    .child(AppConstants.FIREBASE_KEY.CART)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .push().getKey();
            final PrescriptionModel data = new PrescriptionModel(key, image_base64);
            FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                    .child(AppConstants.FIREBASE_KEY.CART)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .child(AppConstants.FIREBASE_KEY.PRESCRIPTION)
                    .child(key)
                    .setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }
}
