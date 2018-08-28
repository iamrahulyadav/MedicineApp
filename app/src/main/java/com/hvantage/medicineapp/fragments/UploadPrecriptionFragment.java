package com.hvantage.medicineapp.fragments;

import android.annotation.SuppressLint;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.hvantage.medicineapp.activity.PrescPreviewActivity;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.activity.SelectAddressActivity;
import com.hvantage.medicineapp.adapter.CartItemAdapter;
import com.hvantage.medicineapp.adapter.UploadedPreAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.model.CartData;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.model.ProductModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.GridSpacingItemDecoration;
import com.hvantage.medicineapp.util.ProgressBar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class UploadPrecriptionFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final String TAG = "UploadPrecrFragment";
    public static ArrayList<PrescriptionModel> presList = new ArrayList<PrescriptionModel>();
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private UploadedPreAdapter adapterPres;
    private ArrayList<String> list = new ArrayList<String>();

    private CardView btnUpload;
    private ProgressBar progressBar;
    private String userChoosenTask;
    private int spacing = 30, spanCount = 3;
    private boolean includeEdge = true;
    private ArrayList<CartData> cartList;
    private CartItemAdapter adapterCart;
    private double total = 0;
    private RecyclerView recylcer_view_cart;
    private TextView tvInstructions;
    private TextView tvCartItemEmpty;
    private CardView btnContinue;
    private AppCompatAutoCompleteTextView etSearch;
    private PrescriptionModel data;

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_upload_prescription, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Upload Prescription");
        }
        cartList = new ArrayList<CartData>();
        init();
        setRecyclerView();
        setRecyclerViewCart();
        list = new DBHelper(context).getMedicinesSearch();
        if (getArguments() != null) {
            presList = getArguments().getParcelableArrayList("data");
            setRecyclerView();
        }
        Log.e(TAG, "onCreateView: data >> " + presList.size());
        if (adapterPres.getItemCount() == 0)
            askAlert();
        else {
            btnContinue.setOnClickListener(this);
        }
        setSearchBar();
        cartList = new DBHelper(context).getCartData();
        return rootView;
    }

    private void askAlert() {
        final CharSequence[] items = {"Choose Existing", "Upload New"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose Existing")) {
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    Fragment fragment = new MyPrescriptionFragment();
                    Bundle args = new Bundle();
                    args.putParcelableArrayList("data", presList);
                    fragment.setArguments(args);
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } else if (items[item].equals("Upload New")) {
                    userChoosenTask = "Upload New";
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    Fragment fragment = new AddPrescrFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("data", null);
                    args.putString("from", "new");
                    fragment.setArguments(args);
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } /*else if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    cameraIntent();
                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    galleryIntent();
                }*/
            }
        });
//      builder.setCancelable(false);
        builder.show();
    }


    @SuppressLint("LongLogTag")
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
                                            Intent intent = new Intent(context, ProductDetailActivity.class);
                                            intent.putExtra("medicine_data", data);
                                            startActivity(intent);
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
        recylcer_view_cart = (RecyclerView) rootView.findViewById(R.id.recylcer_view_items);
        adapterCart = new CartItemAdapter(context, cartList);
        recylcer_view_cart.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_cart.setAdapter(adapterCart);
        adapterCart.notifyDataSetChanged();
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
//                selectImage();
                askAlert();
                break;
            case R.id.btnContinue:
                if (presList.isEmpty())
                    showNoPresAlert();
                else {
                    /*showProgressDialog();
                    Log.e(TAG, "onClick: presList >> " + presList.size());
                    FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                            .child(AppConstants.FIREBASE_KEY.TEMP_PRESCRIPTION)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            .setValue(presList)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, "onSuccess: saved");
                                    hideProgressDialog();
                                    AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION);
                                    Intent intent = new Intent(getActivity(), SelectAddressActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "onSuccess: failed");
                                }
                            });*/

                    AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION);
                    Intent intent = new Intent(getActivity(), SelectAddressActivity.class);
                    startActivity(intent);


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
                        //selectImage();
                        askAlert();
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
        String file = dir + "prescription_img.jpg";
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
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOAD_IMAGE && data != null) {
                startCropImageActivity(data.getData());
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File croppedImageFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/medicineapp/" + "prescription_img.jpg");
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
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), result.getUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    new ImageTask().execute(bitmap);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .setAspectRatio(3, 4)
                .setOutputCompressQuality(50)
//                .setRequestedSize(200, 200)
                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                .start(getContext(), this);
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
            final PrescriptionModel data = new PrescriptionModel("", image_base64);
            Log.d(TAG, "onProgressUpdate: image_base64 >> " + image_base64);
            presList.add(data);
            adapterPres.notifyDataSetChanged();
            hideProgressDialog();
           /* String key = FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                    .child(AppConstants.FIREBASE_KEY.TEMP_PRESCRIPTION)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .push().getKey();*/
            //saveList();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to add prescription medicines")
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(
                                    new Intent(context, PrescPreviewActivity.class)
                                            .putExtra("position", presList.size() - 1));
                        }
                    })
                    .setNegativeButton("Submit Anyway", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


//        builder.setCancelable(false);
            builder.show();


        }
    }

    private void saveList() {
        FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.TEMP_PRESCRIPTION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .setValue(presList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideProgressDialog();
                        Log.e(TAG, "onSuccess: added");
                        Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: failed");
                        hideProgressDialog();
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
