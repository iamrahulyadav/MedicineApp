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
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.BuildConfig;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.UploadedPreAdapter;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
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
    private static final String TAG = "UploadPhotosActivity";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private UploadedPreAdapter adapter;
    private ArrayList<Uri> catList = new ArrayList<Uri>();
    private CardView btnUpload;
    private ProgressBar progressBar;
    private String userChoosenTask;
    private Dialog dialog;
    private int spacing = 30, spanCount = 3;
    private boolean boolean_folder = false, includeEdge = true;
    private CardView btnPlaceOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_upload_prescription, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Upload Presciption");
        }
        init();
        setCategory();
        return rootView;
    }

    private void init() {
        btnUpload = (CardView) rootView.findViewById(R.id.btnUpload);
        btnPlaceOrder = (CardView) rootView.findViewById(R.id.btnPlaceOrder);
        btnUpload.setOnClickListener(this);
    }

    private void setCategory() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        adapter = new UploadedPreAdapter(context, catList);
        recylcer_view.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        recylcer_view.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recylcer_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo");
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
                catList.add(data.getData());
                adapter.notifyDataSetChanged();
                new ImageTask().execute(bitmap);
                if (adapter.getItemCount() > 0)
                    btnPlaceOrder.setVisibility(View.VISIBLE);
//                startCropImageActivity(data.getData());
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
                catList.add(outputFileUri);
                adapter.notifyDataSetChanged();
                new ImageTask().execute(bitmap);
                if (adapter.getItemCount() > 0)
                    btnPlaceOrder.setVisibility(View.VISIBLE);
                //startCropImageActivity(outputFileUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), result.getUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showPreviewDialog(bitmap);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .setAspectRatio(3, 4)
                .setRequestedSize(320, 240)
                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                .start((Activity) context);
    }

    private void showPreviewDialog(final Bitmap bitmap) {
        dialog = new Dialog(getActivity(), R.style.image_preview_dialog);
        dialog.setContentView(R.layout.image_setup_layout);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.image);

        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        imageView.setImageBitmap(bitmap);

        btnSave.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
//                                           catList.add(bitmap);
                                           adapter.notifyDataSetChanged();
                                           dialog.dismiss();
                                           new ImageTask().execute(bitmap);
                                       }
                                   }
        );


        dialog.show();


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
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 30, byteArrayOutputStream);
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
            PrescriptionModel data = new PrescriptionModel(key, image_base64);
            FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME)
                    .child(AppConstants.FIREBASE_KEY.CART)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .child(key)
                    .setValue(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            // ...
                            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            // ...
                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });


            hideProgressDialog();

        }

    }
}
