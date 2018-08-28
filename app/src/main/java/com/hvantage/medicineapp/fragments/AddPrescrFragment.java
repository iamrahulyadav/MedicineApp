package com.hvantage.medicineapp.fragments;

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
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.BuildConfig;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.SelectAddressActivity;
import com.hvantage.medicineapp.adapter.DialogMultipleChoiceAdapter;
import com.hvantage.medicineapp.adapter.PreMedicineItemAdapter;
import com.hvantage.medicineapp.model.DoctorDetails;
import com.hvantage.medicineapp.model.DoseData;
import com.hvantage.medicineapp.model.PatientDetails;
import com.hvantage.medicineapp.model.PreMedicineData;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.TouchImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class AddPrescrFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final String TAG = "AddPrescrFragment";
    ArrayList<PreMedicineData> medList = new ArrayList<PreMedicineData>();
    EditText etDName, etAddress, etEmail, etPhoneNo, etPName, etAge,
            etWeight, etDiagnosis, etNote, etMedType, etMedName,
            etMedDescription, etMedQty, etMedDoses;
    RadioGroup rgGender;
    List<DoseData> dosesList = new ArrayList<DoseData>();
    String selectedDosesId = "";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private CardView btnSubmit, btnAdd, btnCancel;
    private PrescriptionData data = null;
    private ImageView imgArrow;
    private String image_base64;
    private TextView tvAdd;
    private RecyclerView recylcer_view;
    private PreMedicineItemAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private View bottomSheet;
    private BottomSheetBehavior<View> behavior;
    private NestedScrollView layout_add_med, layout_main;
    private TouchImageView imgThumb;
    private TextView tvUpload;
    private byte[] byteArray;
    private boolean isExpand = false;
    private String from;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_add_prescr, container, false);
        if (getArguments() != null) {
            data = getArguments().getParcelable("data");
            from = getArguments().getString("from");
            Log.e(TAG, "onCreateView: data >> " + data);
            Log.e(TAG, "onCreateView: from >> " + from);
        }

        if (intraction != null) {
            if (data == null)
                intraction.actionbarsetTitle("Upload Prescription");
            else
                intraction.actionbarsetTitle("Prescription Details");
        }

        init();
        setRecyclerView();
        setBottomBar();
        if (!Functions.isConnectingToInternet(context))
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        if (data != null) {
            if (!data.getImage().equalsIgnoreCase("")) {
               /* Picasso.with(context)
                        .load(data.getImage())
                        .into(imgThumb);*/
                /*Glide.with(getContext()).load(data.getImage()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imgThumb.setImageBitmap(resource);
                    }
                });*/
                imgThumb.setImageResource(R.drawable.no_image_ph);
                showProgressDialog();
                Glide.with(getActivity()).load(data.getImage())
                        .thumbnail(0.5f)
                        .crossFade()
                        .placeholder(R.drawable.no_image_ph)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                hideProgressDialog();
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                hideProgressDialog();
                                return false;
                            }
                        })
                        .into(imgThumb);


            }
            bottomSheet.setVisibility(View.VISIBLE);
            tvUpload.setVisibility(View.VISIBLE);
            tvUpload.setText("Prescription is loading...");
            tvUpload.setClickable(false);
            DoctorDetails doctorData = data.getDoctorDetails();
            etDName.setText(doctorData.getName());
            etEmail.setText(doctorData.getEmail());
            etPhoneNo.setText(doctorData.getPhoneNo());
            etAddress.setText(doctorData.getAddress());
            PatientDetails patientData = data.getPatientDetails();
            etPName.setText(patientData.getName());
            etAge.setText(patientData.getAge());
            etWeight.setText(patientData.getWeight());
            if (patientData.getGender().equalsIgnoreCase("Male"))
                ((RadioButton) rootView.findViewById(R.id.rbMale)).setChecked(true);
            else if (patientData.getGender().equalsIgnoreCase("Female"))
                ((RadioButton) rootView.findViewById(R.id.rbfemale)).setChecked(true);

            medList = data.getMedicineDetails();
            Log.e(TAG, "onCreateView: data.getMedicineDetails() >> " + medList);
            if (medList.contains(null)) {
                medList.remove(null);
                Log.e(TAG, "onCreateView: data.getMedicineDetails() >> " + medList.size());
            }
            setRecyclerView();

        } else
            selectImage();
        return rootView;
    }

    private void setBottomBar() {
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.main_content);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.e("onStateChanged", "onStateChanged:" + newState);
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                        Functions.hideSoftKeyboard(context, bottomSheet);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        isExpand = true;
                        Log.d(TAG, "onStateChanged: STATE_EXPANDED");
                        Functions.hideSoftKeyboard(context, bottomSheet);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        isExpand = false;
                        Log.d(TAG, "onStateChanged: STATE_COLLAPSED");
                        Functions.hideSoftKeyboard(context, bottomSheet);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "onStateChanged: STATE_DRAGGING");
                        Functions.hideSoftKeyboard(context, bottomSheet);

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(TAG, "onStateChanged: STATE_SETTLING");
                        Functions.hideSoftKeyboard(context, bottomSheet);

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (isAdded()) {
                    animateBottomSheetArrows(slideOffset);
                }
            }
        });
    }

    private void animateBottomSheetArrows(float slideOffset) {
        // Animate counter-clockwise
        imgArrow.setRotation(slideOffset * -180);
        // Animate clockwise
//        imgArrow.setRotation(slideOffset * 180);
    }

    private void init() {
        tvAdd = rootView.findViewById(R.id.tvAdd);
        btnSubmit = rootView.findViewById(R.id.btnSubmit);
        btnAdd = rootView.findViewById(R.id.btnAdd);
        btnCancel = rootView.findViewById(R.id.btnCancel);
        imgThumb = rootView.findViewById(R.id.imgThumb);
        imgArrow = rootView.findViewById(R.id.imgArrow);
        recylcer_view = rootView.findViewById(R.id.recylcer_view);
        etDName = rootView.findViewById(R.id.etDName);
        etAddress = rootView.findViewById(R.id.etAddress);
        etEmail = rootView.findViewById(R.id.etEmail);
        etPhoneNo = rootView.findViewById(R.id.etPhoneNo);
        etPName = rootView.findViewById(R.id.etPName);
        etAge = rootView.findViewById(R.id.etAge);
        etWeight = rootView.findViewById(R.id.etWeight);
        rgGender = rootView.findViewById(R.id.rgGender);
        etDiagnosis = rootView.findViewById(R.id.etDiagnosis);
        etNote = rootView.findViewById(R.id.etNote);
        layout_add_med = rootView.findViewById(R.id.layout_add_med);
        layout_main = rootView.findViewById(R.id.layout_main);
        etMedType = rootView.findViewById(R.id.etMedType);
        etMedName = rootView.findViewById(R.id.etMedName);
        etMedDescription = rootView.findViewById(R.id.etMedDescription);
        etMedQty = rootView.findViewById(R.id.etMedQty);
        etMedDoses = rootView.findViewById(R.id.etMedDoses);
        tvUpload = rootView.findViewById(R.id.tvUpload);
        tvUpload.setOnClickListener(this);
        etMedDoses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogMultipleChoiceAdapter adapter1 = new DialogMultipleChoiceAdapter(context, dosesList);
                new android.app.AlertDialog.Builder(context).setTitle("Select Doses?")
                        .setAdapter(adapter1, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectedDosesId = TextUtils.join(",", adapter1.getSelectedItemIds());
                                String selectedDosage = TextUtils.join(", ", adapter1.getSelectedItemNames());
                                Log.e(TAG, "onClick: selectedTableIds >> " + selectedDosesId);
                                Log.e(TAG, "onClick: selectedDosage >> " + selectedDosage);
                                etMedDoses.setText(selectedDosage);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        });
        btnSubmit.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        imgArrow.setOnClickListener(this);
        initDoses();
    }

    private void initDoses() {
        dosesList.add(new DoseData("1", "After Wake-Up"));
        dosesList.add(new DoseData("2", "Before Sleeping"));
        dosesList.add(new DoseData("3", "After Breakfast"));
        dosesList.add(new DoseData("4", "Before Breakfast"));
        dosesList.add(new DoseData("5", "After Lunch"));
        dosesList.add(new DoseData("6", "Before Lunch"));
        dosesList.add(new DoseData("7", "After High-Tea"));
        dosesList.add(new DoseData("8", "Before High-Tea"));
        dosesList.add(new DoseData("9", "After Dinner"));
        dosesList.add(new DoseData("10", "Before Dinner"));
    }

    private void setRecyclerView() {
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        adapter = new PreMedicineItemAdapter(context, medList);
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
            case R.id.btnSubmit:
                if (TextUtils.isEmpty(image_base64))
                    Toast.makeText(context, "Select Image", Toast.LENGTH_SHORT).show();
                else {
                    if (data == null)
                        saveData();
                    else
                        updateData();
                }
                break;
            case R.id.cardUpload:
                break;
            case R.id.tvUpload:
                selectImage();
                break;
            case R.id.tvAdd:
                layout_main.setVisibility(View.GONE);
                layout_add_med.setVisibility(View.VISIBLE);
                break;
            case R.id.btnCancel:
                layout_add_med.setVisibility(View.GONE);
                layout_main.setVisibility(View.VISIBLE);
                break;
            case R.id.imgArrow:
                if (isExpand)
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                else
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.btnAdd:
                layout_add_med.setVisibility(View.GONE);
                layout_main.setVisibility(View.VISIBLE);
                PreMedicineData data = new PreMedicineData();
                data.setType(etMedType.getText().toString());
                data.setName(etMedName.getText().toString());
                data.setDescription(etMedDescription.getText().toString());
                data.setQuantity(etMedQty.getText().toString());
                data.setDoses(etMedDoses.getText().toString());
                data.setDoses_id(selectedDosesId);
                medList.add(data);
                adapter.notifyDataSetChanged();
                etMedType.setText("");
                etMedName.setText("");
                etMedDescription.setText("");
                etMedQty.setText("");
                etMedDoses.setText("");
                selectedDosesId = "";
                break;
        }
    }

    private void updateData() {
    }

    private void saveData() {
        Log.e(TAG, "saveData: ");
        new UploadTask().execute();

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
                    imgThumb.setImageBitmap(bitmap);
                    bottomSheet.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
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
                .setRequestedSize(768, 1024)
                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                .start(getContext(), this);
    }

    class UploadTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final JsonObject jsonDoctor = new JsonObject();
            jsonDoctor.addProperty("name", etDName.getText().toString());
            jsonDoctor.addProperty("address", etAddress.getText().toString());
            jsonDoctor.addProperty("email", etEmail.getText().toString());
            jsonDoctor.addProperty("phone_no", etPhoneNo.getText().toString());

            JsonObject jsonPatient = new JsonObject();
            jsonPatient.addProperty("name", etPName.getText().toString());
            jsonPatient.addProperty("age", etAge.getText().toString());
            jsonPatient.addProperty("weight", etWeight.getText().toString());
            jsonPatient.addProperty("gender", "" + ((RadioButton) rootView.findViewById(rgGender.getCheckedRadioButtonId())).getText());

            JsonArray jsonMedicine = new GsonBuilder().create().toJsonTree(medList).getAsJsonArray();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.ADD_PRESCRIPTION);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("image", image_base64);
            jsonObject.add("doctor_details", jsonDoctor);
            jsonObject.add("patient_details", jsonPatient);
            jsonObject.add("medicine_details", jsonMedicine);
            jsonObject.addProperty("diagnosis_details", etDiagnosis.getText().toString());
            jsonObject.addProperty("notes", etNote.getText().toString());

            Log.e(TAG, "UploadTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "UploadTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            String prescription_id = jsonObject.getJSONArray("result").getJSONObject(0).getString("prescription_id");
                            AppPreferences.setSelectedPresId(context, prescription_id);
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            adapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                if (from == null) {
                    getActivity().onBackPressed();
                } else if (from.equalsIgnoreCase("new")) {
                    startActivity(new Intent(context, SelectAddressActivity.class));
                } else getActivity().onBackPressed();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
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
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
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
