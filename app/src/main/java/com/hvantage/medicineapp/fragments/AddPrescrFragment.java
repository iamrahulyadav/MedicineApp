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
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.BuildConfig;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.CartActivity;
import com.hvantage.medicineapp.activity.ConfirmOrderActivity;
import com.hvantage.medicineapp.activity.MainActivity;
import com.hvantage.medicineapp.activity.SelectAddressActivity;
import com.hvantage.medicineapp.adapter.DialogMultipleChoiceAdapter;
import com.hvantage.medicineapp.adapter.PreMedicineItemAdapterEditable;
import com.hvantage.medicineapp.adapter.PreMedicineItemAdapterMain;
import com.hvantage.medicineapp.model.DoctorDetails;
import com.hvantage.medicineapp.model.DoseData;
import com.hvantage.medicineapp.model.PatientDetails;
import com.hvantage.medicineapp.model.PreMedicineData;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.model.ProductData;
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

import org.json.JSONArray;
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
    public static ArrayList<PreMedicineData> medListMain;
    AppCompatAutoCompleteTextView etMedName1;
    EditText etDName1, etAddress1, etEmail1, etPhoneNo1, etPName1, etAge1,
            etWeight1, etDiagnosis1, etMedType1, etMedManufacturer1,
            etMedDescription1, etMedQty1, etMedDoses1, etMedPrice1, etMedImgUrl1;
    RadioGroup rgGender1;
    List<DoseData> dosesList = new ArrayList<DoseData>();
    String selectedDosesId = "";
    private ArrayList<PreMedicineData> medListEditable;
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private AppCompatButton btnAdd1, btnCancel1;
    private PrescriptionData data = null;
    private ImageView imgArrow, imgArrow1;
    private String image_base64;
    private TextView tvAdd1, tvAdd2, tvAdd3;
    private RecyclerView recylcer_view_editable, recylcer_view_main;
    private PreMedicineItemAdapterEditable adapter_editable;
    private PreMedicineItemAdapterMain adapter_main;
    private CoordinatorLayout coordinatorLayout;
    private View bottomsheet_bottom;
    private BottomSheetBehavior<View> behavior;
    private NestedScrollView layout_add_med1, layout_main1;
    private TouchImageView imgThumb;
    private byte[] byteArray;
    private boolean isExpand = false;
    private String from;
    private CardView cardDoctor1, cardPatient1, cardMedicines1;
    private AppCompatTextView tvTabDoctor1, tvTabPatient1, tvTabMedicine1;
    private LinearLayout bsAction, bsAction1;
    private AppCompatButton btnOrderCall, btnOrderWithDetail;

    private LinearLayout bottomSheetTop;
    private boolean isEdit = false;
    private int last_edit_position = 0;
    private AppCompatButton btnAllNext, btnAllPrev, btnContinueOrder, btnAddPD, btnOrderNow, btnExit;
    private Spinner spinnerType1;
    private View bottomsheet_top;
    private ArrayList<ProductData> searchList;
    private SearchBarAdapter searchAdapter;

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

        medListEditable = new ArrayList<>();
        medListMain = new ArrayList<>();
        init();
        setRecyclerViewEditable();
        setRecyclerViewMain();
        setBottomBar();
        if (!Functions.isConnectingToInternet(context))
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        if (data != null) {
            if (!data.getImage().equalsIgnoreCase("")) {
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
                                Toast.makeText(context, "Image Error", Toast.LENGTH_SHORT).show();
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
            bottomsheet_bottom.setVisibility(View.VISIBLE);
            bsAction.setVisibility(View.VISIBLE);
            DoctorDetails doctorData = data.getDoctorDetails();
            etDName1.setText(doctorData.getName());
            etEmail1.setText(doctorData.getEmail());
            etPhoneNo1.setText(doctorData.getPhoneNo());
            etAddress1.setText(doctorData.getAddress());

            PatientDetails patientData = data.getPatientDetails();
            etPName1.setText(patientData.getName());
            etAge1.setText(patientData.getAge());
            etWeight1.setText(patientData.getWeight());
            if (patientData.getGender() != null)
                if (patientData.getGender().equalsIgnoreCase("Male"))
                    ((RadioButton) rootView.findViewById(R.id.rbMale1)).setChecked(true);
                else if (patientData.getGender().equalsIgnoreCase("Female"))
                    ((RadioButton) rootView.findViewById(R.id.rbfemale1)).setChecked(true);

            etDiagnosis1.setText(data.getDiagnosisDetails());

            for (PreMedicineData data : data.getMedicineDetails()) {
                medListMain.add(data);
                medListEditable.add(data);
            }

            Log.e(TAG, "onCreateView: medListMain.size() >> " + medListMain.size());
            if (medListMain.contains(null)) {
                medListMain.remove(null);
            }
            if (medListEditable.contains(null)) {
                medListEditable.remove(null);
            }

            Log.d(TAG, "onCreateView: medListMain >> " + medListMain.size());
            Log.d(TAG, "onCreateView: medListEditable >> " + medListEditable.size());
            setRecyclerViewMain();
            setRecyclerViewEditable();
        } else {
            selectImage();
        }
        return rootView;
    }

    private void setBottomBar() {
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.main_content);
        bottomsheet_bottom = coordinatorLayout.findViewById(R.id.bottom_sheet);
        bottomsheet_top = coordinatorLayout.findViewById(R.id.bottomsheet_top);
        behavior = BottomSheetBehavior.from(bottomsheet_bottom);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomsheet_bottom, int newState) {
                Log.e("onStateChanged", "onStateChanged:" + newState);
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        isExpand = true;
                        Log.d(TAG, "onStateChanged: STATE_EXPANDED");
                        bsAction.setVisibility(View.GONE);
                        imgArrow.setVisibility(View.VISIBLE);
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        bottomSheetTop.setVisibility(View.GONE);
                        imgArrow1.setVisibility(View.GONE);
                        bsAction1.setVisibility(View.VISIBLE);
                        bottomsheet_top.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        isExpand = false;
                        Log.d(TAG, "onStateChanged: STATE_COLLAPSED");
                        imgArrow.setVisibility(View.GONE);
                        bsAction.setVisibility(View.VISIBLE);
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        bottomsheet_top.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.d(TAG, "onStateChanged: STATE_DRAGGING");
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.d(TAG, "onStateChanged: STATE_SETTLING");
                        Functions.hideSoftKeyboard(context, bottomsheet_bottom);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomsheet_bottom, float slideOffset) {
                animateBottomSheetArrows(slideOffset);
            }
        });
        bottomsheet_bottom.setVisibility(View.VISIBLE);
        bsAction = (LinearLayout) rootView.findViewById(R.id.bsAction);
        bsAction1 = (LinearLayout) rootView.findViewById(R.id.bsAction1);
        btnOrderCall = rootView.findViewById(R.id.btnOrderCall);
        btnOrderWithDetail = rootView.findViewById(R.id.btnOrderWithDetail);

        btnAllNext = (AppCompatButton) rootView.findViewById(R.id.btnAllNext);
        btnAllPrev = (AppCompatButton) rootView.findViewById(R.id.btnAllPrev);
        btnContinueOrder = (AppCompatButton) rootView.findViewById(R.id.btnContinueOrder);
        btnOrderNow = (AppCompatButton) rootView.findViewById(R.id.btnOrderNow);
        btnExit = (AppCompatButton) rootView.findViewById(R.id.btnExit);

        bottomSheetTop = (LinearLayout) rootView.findViewById(R.id.bottomSheetTop);
        btnAddPD = rootView.findViewById(R.id.btnAddPD);

        cardDoctor1 = (CardView) rootView.findViewById(R.id.cardDoctor1);
        cardPatient1 = (CardView) rootView.findViewById(R.id.cardPatient1);
        cardMedicines1 = (CardView) rootView.findViewById(R.id.cardMedicines1);

        tvTabDoctor1 = (AppCompatTextView) rootView.findViewById(R.id.tvTabDoctor1);
        tvTabPatient1 = (AppCompatTextView) rootView.findViewById(R.id.tvTabPatient1);
        tvTabMedicine1 = (AppCompatTextView) rootView.findViewById(R.id.tvTabMedicine1);

        tvTabDoctor1.setOnClickListener(this);
        tvTabPatient1.setOnClickListener(this);
        tvTabMedicine1.setOnClickListener(this);

        btnOrderCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrescriptionData tempData = new PrescriptionData();
                DoctorDetails dData = new DoctorDetails();
                PatientDetails pData = new PatientDetails();


                dData.setName(etDName1.getText().toString());
                dData.setAddress(etAddress1.getText().toString());
                dData.setEmail(etEmail1.getText().toString());
                dData.setPhoneNo(etPhoneNo1.getText().toString());

                pData.setName(etPName1.getText().toString());
                pData.setAge(etAge1.getText().toString());
                pData.setWeight(etWeight1.getText().toString());
                pData.setGender(((RadioButton) rootView.findViewById(rgGender1.getCheckedRadioButtonId())).getText().toString());

                if (data != null) {
                    tempData.setPrescription_id(data.getPrescription_id());
                    tempData.setPrescription_title(data.getPrescription_title());
                    tempData.setImage(data.getImage());
                } else {
                    tempData.setPrescription_id("0");
                    tempData.setPrescription_title("");
                    tempData.setImage(image_base64);
                }
                tempData.setDoctorDetails(dData);
                tempData.setPatientDetails(pData);
                tempData.setMedicineDetails(null);
                tempData.setDiagnosisDetails(etDiagnosis1.getText().toString());
                tempData.setNotes("");

                CartActivity.selectedPresc = tempData;
                AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION);
                Log.e(TAG, "onClick: CartActivity.selectedPresc  >> " + CartActivity.selectedPresc);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (!AppPreferences.getSelectedAddId(context).equalsIgnoreCase("") && !AppPreferences.getSelectedAdd(context).equalsIgnoreCase("")) {
                    startActivity(new Intent(context, ConfirmOrderActivity.class));
                } else
                    startActivity(new Intent(context, SelectAddressActivity.class));
            }
        });

        cardDoctor1.setVisibility(View.VISIBLE);
        cardPatient1.setVisibility(View.GONE);
        cardMedicines1.setVisibility(View.GONE);
        btnAllPrev.setVisibility(View.GONE);

        btnAllNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardDoctor1.getVisibility() == View.VISIBLE) {
                    if (TextUtils.isEmpty(etDName1.getText().toString())) {
                        etDName1.requestFocus();
                        Toast.makeText(context, "Enter Doctor's Name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(etAddress1.getText().toString())) {
                        etAddress1.requestFocus();
                        Toast.makeText(context, "Enter Doctor's Address", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(etPhoneNo1.getText().toString())) {
                        etPhoneNo1.requestFocus();
                        Toast.makeText(context, "Enter Doctor's Phone", Toast.LENGTH_SHORT).show();
                    } else {
                        /*if (data == null) {
                            if (TextUtils.isEmpty(image_base64))
                                Toast.makeText(context, "Select Image", Toast.LENGTH_SHORT).show();
                            else new SaveTask().execute();
                        } else
                            new UpdateTask().execute();*/
                        tvTabDoctor1.setTextColor(getResources().getColor(R.color.colorGray));
                        tvTabPatient1.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvTabMedicine1.setTextColor(getResources().getColor(R.color.colorGray));
                        cardDoctor1.setVisibility(View.GONE);
                        cardPatient1.setVisibility(View.VISIBLE);
                        cardMedicines1.setVisibility(View.GONE);
                        btnAllPrev.setVisibility(View.VISIBLE);
                    }
                } else if (cardPatient1.getVisibility() == View.VISIBLE) {
                    if (TextUtils.isEmpty(etPName1.getText().toString())) {
                        etPName1.requestFocus();
                        Toast.makeText(context, "Enter Patients's Name", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(etAge1.getText().toString())) {
                        etAge1.requestFocus();
                        Toast.makeText(context, "Enter Patients's Age", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(etDiagnosis1.getText().toString())) {
                        etDiagnosis1.requestFocus();
                        Toast.makeText(context, "Enter Patients's Diagnosis Info", Toast.LENGTH_SHORT).show();
                    } else {
                       /* if (data == null) {
                            if (TextUtils.isEmpty(image_base64))
                                Toast.makeText(context, "Select Image", Toast.LENGTH_SHORT).show();
                            else new SaveTask().execute();
                        } else
                            new UpdateTask().execute();*/
                        tvTabDoctor1.setTextColor(getResources().getColor(R.color.colorGray));
                        tvTabPatient1.setTextColor(getResources().getColor(R.color.colorGray));
                        tvTabMedicine1.setTextColor(getResources().getColor(R.color.colorPrimary));
                        cardPatient1.setVisibility(View.GONE);
                        cardDoctor1.setVisibility(View.GONE);
                        cardMedicines1.setVisibility(View.VISIBLE);
                        btnAllNext.setText("Save");
                    }
                } else if (cardMedicines1.getVisibility() == View.VISIBLE) {
                    /*if (medListMain.size() == 0) {
                        Toast.makeText(context, "Please add alteast one prescription medicine.", Toast.LENGTH_SHORT).show();
                    } else {*/
                    if (data == null) {
                        if (TextUtils.isEmpty(image_base64))
                            Toast.makeText(context, "Select Image", Toast.LENGTH_SHORT).show();
                        else new SaveTask().execute();
                    } else
                        new UpdateTask().execute();
                    /*}*/
                }
            }
        });

        btnAllPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardDoctor1.getVisibility() == View.VISIBLE) {

                } else if (cardPatient1.getVisibility() == View.VISIBLE) {
                    {
                        tvTabDoctor1.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvTabPatient1.setTextColor(getResources().getColor(R.color.colorGray));
                        tvTabMedicine1.setTextColor(getResources().getColor(R.color.colorGray));
                        cardPatient1.setVisibility(View.GONE);
                        cardDoctor1.setVisibility(View.VISIBLE);
                        cardMedicines1.setVisibility(View.GONE);
                        btnAllPrev.setVisibility(View.GONE);
                        btnAllNext.setVisibility(View.VISIBLE);
                        btnAllNext.setText("Next");
                    }
                } else if (cardMedicines1.getVisibility() == View.VISIBLE) {
                    tvTabDoctor1.setTextColor(getResources().getColor(R.color.colorGray));
                    tvTabPatient1.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvTabMedicine1.setTextColor(getResources().getColor(R.color.colorGray));
                    cardPatient1.setVisibility(View.VISIBLE);
                    cardDoctor1.setVisibility(View.GONE);
                    cardMedicines1.setVisibility(View.GONE);
                    btnAllNext.setText("Next");
                }
            }
        });

        btnOrderWithDetail.setOnClickListener(this);
        btnContinueOrder.setOnClickListener(this);
        btnOrderNow.setOnClickListener(this);
        btnAddPD.setOnClickListener(this);
        btnExit.setOnClickListener(this);
    }


    private void animateBottomSheetArrows(float slideOffset) {
        imgArrow.setRotation(slideOffset * -180);
    }

    private void init() {
        tvAdd1 = rootView.findViewById(R.id.tvAdd1);
        tvAdd2 = rootView.findViewById(R.id.tvAdd2);
        tvAdd3 = rootView.findViewById(R.id.tvAdd3);
        btnAdd1 = rootView.findViewById(R.id.btnAdd1);
        btnCancel1 = rootView.findViewById(R.id.btnCancel1);
        imgThumb = rootView.findViewById(R.id.imgThumb);
        imgArrow = rootView.findViewById(R.id.imgArrow);
        imgArrow1 = rootView.findViewById(R.id.imgArrow1);
        recylcer_view_main = rootView.findViewById(R.id.recylcer_view_main);
        recylcer_view_editable = rootView.findViewById(R.id.recylcer_view_editable1);

        etDName1 = rootView.findViewById(R.id.etDName1);
        etAddress1 = rootView.findViewById(R.id.etAddress1);
        etEmail1 = rootView.findViewById(R.id.etEmail1);
        etPhoneNo1 = rootView.findViewById(R.id.etPhoneNo1);
        etPName1 = rootView.findViewById(R.id.etPName1);
        etAge1 = rootView.findViewById(R.id.etAge1);
        etWeight1 = rootView.findViewById(R.id.etWeight1);
        rgGender1 = rootView.findViewById(R.id.rgGender1);
        etDiagnosis1 = rootView.findViewById(R.id.etDiagnosis1);

        layout_add_med1 = rootView.findViewById(R.id.layout_add_med1);
        layout_main1 = rootView.findViewById(R.id.layout_main1);


        etMedType1 = rootView.findViewById(R.id.etMedType1);
        etMedName1 = rootView.findViewById(R.id.etMedName1);
        etMedManufacturer1 = rootView.findViewById(R.id.etMedManufacturer1);
        etMedDescription1 = rootView.findViewById(R.id.etMedDescription1);
        etMedQty1 = rootView.findViewById(R.id.etMedQty1);
        etMedDoses1 = rootView.findViewById(R.id.etMedDoses1);
        etMedPrice1 = rootView.findViewById(R.id.etMedPrice1);
        etMedImgUrl1 = rootView.findViewById(R.id.etMedImgUrl1);
        spinnerType1 = (Spinner) rootView.findViewById(R.id.spinnerType1);

        spinnerType1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etMedType1.setText((String) spinnerType1.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etMedType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerType1.performClick();
            }
        });

        etMedDoses1.setOnClickListener(new View.OnClickListener() {
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
                                etMedDoses1.setText(selectedDosage);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).show();
            }
        });
        tvAdd1.setOnClickListener(this);
        tvAdd2.setOnClickListener(this);
        tvAdd3.setOnClickListener(this);
        btnAdd1.setOnClickListener(this);
        btnCancel1.setOnClickListener(this);
        imgArrow.setOnClickListener(this);
        imgArrow1.setOnClickListener(this);
        initDoses();
        searchList = new ArrayList<>();
        etMedName1.setThreshold(4);
        searchAdapter = new SearchBarAdapter(context, R.layout.auto_complete_text, searchList);
        etMedName1.setAdapter(searchAdapter);
        etMedName1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    new SearchData().execute();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private void setRecyclerViewEditable() {
        recylcer_view_editable.setLayoutManager(new LinearLayoutManager(context));
        adapter_editable = new PreMedicineItemAdapterEditable(context, medListEditable,
                new PreMedicineItemAdapterEditable.MyAdapterListenerEditable() {
                    @Override
                    public void removeItem(View v, int position) {
                        Log.e(TAG, "setRecyclerViewEditable removeItem: ");
                        medListEditable.remove(position);
                        adapter_editable.notifyDataSetChanged();
                        Log.e(TAG, "removeItem: medListMain >> " + medListMain);
                        Log.e(TAG, "removeItem: medListEditable >> " + medListEditable);
                    }

                    @Override
                    public void editItem(View v, int position, String qty) {
                        medListEditable.get(position).setQuantity(qty);
                    }
                });

        recylcer_view_editable.setAdapter(adapter_editable);
        adapter_editable.notifyDataSetChanged();
    }

    private void setRecyclerViewMain() {
        recylcer_view_main.setLayoutManager(new LinearLayoutManager(context));
        adapter_main = new PreMedicineItemAdapterMain(context, medListMain,
                new PreMedicineItemAdapterMain.MyAdapterListener() {
                    @Override
                    public void removeItem(View v, int position) {
                        Log.e(TAG, "setRecyclerViewMain removeItem: ");
                        medListMain.remove(position);
                        adapter_main.notifyDataSetChanged();
                        adapter_editable.notifyDataSetChanged();
                        Log.e(TAG, "removeItem: medListMain >> main " + medListMain);
                        Log.e(TAG, "removeItem: medListEditable >> main " + medListEditable);
                    }

                    @Override
                    public void editItem(View v, int position) {
                        isEdit = true;
                        last_edit_position = position;
                        etMedName1.setText(medListMain.get(position).getName());
                        etMedType1.setText(medListMain.get(position).getType());
                        etMedDescription1.setText(medListMain.get(position).getDescription());
                        etMedQty1.setText(medListMain.get(position).getQuantity());
                        etMedDoses1.setText(medListMain.get(position).getDoses());
                        layout_main1.setVisibility(View.GONE);
                        layout_add_med1.setVisibility(View.VISIBLE);
                    }
                });
        recylcer_view_main.setAdapter(adapter_main);
        adapter_main.notifyDataSetChanged();
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
            case R.id.tvAdd1:
                layout_main1.setVisibility(View.GONE);
                layout_add_med1.setVisibility(View.VISIBLE);
                isEdit = false;
                break;
            case R.id.tvAdd2:
                layout_main1.setVisibility(View.GONE);
                layout_add_med1.setVisibility(View.VISIBLE);
                isEdit = false;
                break;
            case R.id.btnCancel1:
                isEdit = false;
                layout_add_med1.setVisibility(View.GONE);
                layout_main1.setVisibility(View.VISIBLE);
                break;
            case R.id.imgArrow:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.btnAdd1:
                PreMedicineData predata1;
                if (TextUtils.isEmpty(etMedType1.getText().toString())) {
                    Toast.makeText(context, "Select medicine type", Toast.LENGTH_SHORT).show();
                    spinnerType1.performClick();
                } else if (TextUtils.isEmpty(etMedName1.getText().toString())) {
                    etMedName1.requestFocus();
                    Toast.makeText(context, "Enter medicine name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etMedManufacturer1.getText().toString())) {
                    etMedManufacturer1.requestFocus();
                    Toast.makeText(context, "Enter medicine manufacturer", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etMedDoses1.getText().toString())) {
                    etMedDoses1.requestFocus();
                    Toast.makeText(context, "Select medicine doses", Toast.LENGTH_SHORT).show();
                } else if (isEdit) {
                    layout_add_med1.setVisibility(View.GONE);
                    layout_main1.setVisibility(View.VISIBLE);
                    predata1 = medListMain.get(last_edit_position);
                    predata1.setType(etMedType1.getText().toString());
                    predata1.setName(etMedName1.getText().toString());
                    predata1.setManufacturer(etMedManufacturer1.getText().toString());
                    predata1.setDescription(etMedDescription1.getText().toString());
                    predata1.setQuantity(etMedQty1.getText().toString());
                    predata1.setPrice(etMedPrice1.getText().toString());
                    predata1.setImage(etMedImgUrl1.getText().toString());
                    predata1.setDoses(etMedDoses1.getText().toString());
                    predata1.setDoses_id(selectedDosesId);
                    medListMain.set(last_edit_position, predata1);
                    adapter_editable.notifyDataSetChanged();
                    adapter_main.notifyDataSetChanged();
                    etMedType1.setText("");
                    etMedName1.setText("");
                    etMedManufacturer1.setText("");
                    etMedDescription1.setText("");
                    etMedQty1.setText("");
                    etMedPrice1.setText("");
                    etMedImgUrl1.setText("");
                    etMedDoses1.setText("");
                    selectedDosesId = "";
                } else {
                    layout_add_med1.setVisibility(View.GONE);
                    layout_main1.setVisibility(View.VISIBLE);
                    predata1 = new PreMedicineData();
                    predata1.setPresc_medicine_id("0");
                    predata1.setType(etMedType1.getText().toString());
                    predata1.setName(etMedName1.getText().toString());
                    predata1.setManufacturer(etMedManufacturer1.getText().toString());
                    predata1.setDescription(etMedDescription1.getText().toString());
                    predata1.setQuantity(etMedQty1.getText().toString());
                    predata1.setPrice(etMedPrice1.getText().toString());
                    predata1.setImage(etMedImgUrl1.getText().toString());
                    predata1.setDoses(etMedDoses1.getText().toString());
                    predata1.setDoses_id(selectedDosesId);
                    medListMain.add(predata1);
                    medListEditable.add(predata1);
                    last_edit_position = 0;
                    adapter_editable.notifyDataSetChanged();
                    adapter_main.notifyDataSetChanged();
                    etMedType1.setText("");
                    etMedName1.setText("");
                    etMedManufacturer1.setText("");
                    etMedDescription1.setText("");
                    etMedQty1.setText("");
                    etMedPrice1.setText("");
                    etMedImgUrl1.setText("");
                    etMedDoses1.setText("");
                    selectedDosesId = "";
                }

                break;
            case R.id.btnContinueOrder:
                if (medListEditable.size() == 0) {
                    new AlertDialog.Builder(context)
                            .setMessage(getString(R.string.prescription_details_missing))
                            .setNegativeButton("Add Details", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnAddPD.performClick();
                                }
                            })
                            .setPositiveButton("Skip & Order", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                    btnOrderCall.performClick();
                                }
                            })
                            .show();
                } else {
                    PrescriptionData tempData = new PrescriptionData();
                    DoctorDetails dData = new DoctorDetails();
                    PatientDetails pData = new PatientDetails();


                    dData.setName(etDName1.getText().toString());
                    dData.setAddress(etAddress1.getText().toString());
                    dData.setEmail(etEmail1.getText().toString());
                    dData.setPhoneNo(etPhoneNo1.getText().toString());

                    pData.setName(etPName1.getText().toString());
                    pData.setAge(etAge1.getText().toString());
                    pData.setWeight(etWeight1.getText().toString());
                    pData.setGender(((RadioButton) rootView.findViewById(rgGender1.getCheckedRadioButtonId())).getText().toString());

                    if (data != null) {
                        tempData.setPrescription_id(data.getPrescription_id());
                        tempData.setPrescription_title(data.getPrescription_title());
                        tempData.setImage(data.getImage());
                    } else {
                        tempData.setPrescription_id("0");
                        tempData.setPrescription_title("");
                        tempData.setImage(image_base64);
                    }
                    tempData.setDoctorDetails(dData);
                    tempData.setPatientDetails(pData);
                    tempData.setMedicineDetails(medListEditable);
                    tempData.setDiagnosisDetails(etDiagnosis1.getText().toString());
                    tempData.setNotes("");

                    CartActivity.selectedPresc = tempData;
                    Log.e(TAG, "onClick: CartActivity.selectedPresc  >> " + CartActivity.selectedPresc);
                    AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION);
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    if (!AppPreferences.getSelectedAddId(context).equalsIgnoreCase("") && !AppPreferences.getSelectedAdd(context).equalsIgnoreCase("")) {
                        startActivity(new Intent(context, ConfirmOrderActivity.class));
                    } else
                        startActivity(new Intent(context, SelectAddressActivity.class));
                }
                break;
            case R.id.btnOrderNow:
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                behavior.setHideable(false);
                btnOrderNow.setVisibility(View.GONE);
                bottomsheet_bottom.setVisibility(View.VISIBLE);

                break;
            case R.id.btnExit:
                new AlertDialog.Builder(context)
                        .setMessage("Do you want to discard all changes and want to exit?")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(context, MainActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;
            case R.id.btnOrderWithDetail:
                bsAction.setVisibility(View.GONE);
                imgArrow.setVisibility(View.VISIBLE);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetTop.setVisibility(View.GONE);
                imgArrow1.setVisibility(View.GONE);
                bsAction1.setVisibility(View.VISIBLE);
                break;
            case R.id.btnAddPD:
                bottomSheetTop.setVisibility(View.VISIBLE);
                bsAction1.setVisibility(View.GONE);
                imgArrow1.setVisibility(View.VISIBLE);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                btnOrderNow.setVisibility(View.GONE);
                bottomsheet_bottom.setVisibility(View.GONE);
                break;
            case R.id.imgArrow1:
                /*if (cardMedicines1.getVisibility() == View.VISIBLE)
                    if (data == null) {
                        if (TextUtils.isEmpty(image_base64))
                            Toast.makeText(context, "Select Image", Toast.LENGTH_SHORT).show();
                        else new SaveTask().execute();
                    } else
                        new UpdateTask().execute();*/
                bottomSheetTop.setVisibility(View.GONE);
                bsAction1.setVisibility(View.VISIBLE);
                imgArrow1.setVisibility(View.GONE);
                btnOrderNow.setVisibility(View.VISIBLE);
                bottomsheet_bottom.setVisibility(View.VISIBLE);
                break;
            case R.id.tvTabDoctor1:
                tvTabDoctor1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTabPatient1.setTextColor(getResources().getColor(R.color.colorGray));
                tvTabMedicine1.setTextColor(getResources().getColor(R.color.colorGray));
                cardDoctor1.setVisibility(View.VISIBLE);
                cardPatient1.setVisibility(View.GONE);
                cardMedicines1.setVisibility(View.GONE);
                btnAllPrev.setVisibility(View.GONE);
                break;
            case R.id.tvTabPatient1:
                tvTabDoctor1.setTextColor(getResources().getColor(R.color.colorGray));
                tvTabPatient1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTabMedicine1.setTextColor(getResources().getColor(R.color.colorGray));
                cardDoctor1.setVisibility(View.GONE);
                cardPatient1.setVisibility(View.VISIBLE);
                cardMedicines1.setVisibility(View.GONE);
                btnAllPrev.setVisibility(View.VISIBLE);
                break;
            case R.id.tvTabMedicine1:
                tvTabDoctor1.setTextColor(getResources().getColor(R.color.colorGray));
                tvTabPatient1.setTextColor(getResources().getColor(R.color.colorGray));
                tvTabMedicine1.setTextColor(getResources().getColor(R.color.colorPrimary));
                cardDoctor1.setVisibility(View.GONE);
                cardPatient1.setVisibility(View.GONE);
                cardMedicines1.setVisibility(View.VISIBLE);
                btnAllPrev.setVisibility(View.VISIBLE);
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
        final CharSequence[] items = {"Take Photo", "Choose From Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose From Gallery")) {
                    galleryIntent();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });
        builder.setCancelable(false);
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
                    bottomsheet_bottom.setVisibility(View.VISIBLE);
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
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setAutoZoomEnabled(false)
                .start(getContext(), this);
    }

    class SaveTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final JsonObject jsonDoctor = new JsonObject();
            jsonDoctor.addProperty("name", etDName1.getText().toString());
            jsonDoctor.addProperty("address", etAddress1.getText().toString());
            jsonDoctor.addProperty("email", etEmail1.getText().toString());
            jsonDoctor.addProperty("phone_no", etPhoneNo1.getText().toString());

            JsonObject jsonPatient = new JsonObject();
            jsonPatient.addProperty("name", etPName1.getText().toString());
            jsonPatient.addProperty("age", etAge1.getText().toString());
            jsonPatient.addProperty("weight", etWeight1.getText().toString());
            jsonPatient.addProperty("gender", "" + ((RadioButton) rootView.findViewById(rgGender1.getCheckedRadioButtonId())).getText());

            JsonArray jsonMedicine = new GsonBuilder().create().toJsonTree(medListMain).getAsJsonArray();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.ADD_PRESCRIPTION);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("image", image_base64);
            jsonObject.add("doctor_details", jsonDoctor);
            jsonObject.add("patient_details", jsonPatient);
            jsonObject.add("medicine_details", jsonMedicine);
            jsonObject.addProperty("diagnosis_details", etDiagnosis1.getText().toString());
            jsonObject.addProperty("notes", "");

            Log.e(TAG, "SaveTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SaveTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            PrescriptionData newData = new Gson().fromJson(String.valueOf(jsonObject1), PrescriptionData.class);
                            data = newData;
                            Log.e(TAG, "onResponse: newData >> " + data);
                            medListMain.clear();
                            medListEditable.clear();
                            for (PreMedicineData data : data.getMedicineDetails()) {
                                medListMain.add(data);
                                medListEditable.add(data);
                            }
                            if (medListMain.contains(null)) {
                                medListMain.remove(null);
                            }
                            if (medListEditable.contains(null)) {
                                medListEditable.remove(null);
                            }
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
            adapter_editable.notifyDataSetChanged();
            adapter_main.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                bottomSheetTop.setVisibility(View.GONE);
                bsAction1.setVisibility(View.VISIBLE);
                imgArrow1.setVisibility(View.GONE);
                btnOrderNow.setVisibility(View.VISIBLE);
                bottomsheet_bottom.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UpdateTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final JsonObject jsonDoctor = new JsonObject();
            jsonDoctor.addProperty("name", etDName1.getText().toString());
            jsonDoctor.addProperty("address", etAddress1.getText().toString());
            jsonDoctor.addProperty("email", etEmail1.getText().toString());
            jsonDoctor.addProperty("phone_no", etPhoneNo1.getText().toString());

            JsonObject jsonPatient = new JsonObject();
            jsonPatient.addProperty("name", etPName1.getText().toString());
            jsonPatient.addProperty("age", etAge1.getText().toString());
            jsonPatient.addProperty("weight", etWeight1.getText().toString());
            jsonPatient.addProperty("gender", "" + ((RadioButton) rootView.findViewById(rgGender1.getCheckedRadioButtonId())).getText());

            JsonArray jsonMedicine = new GsonBuilder().create().toJsonTree(medListMain).getAsJsonArray();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.UPDATE_PRESCRIPTION);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("prescription_id", data.getPrescription_id());
            jsonObject.addProperty("image", "");
            jsonObject.add("doctor_details", jsonDoctor);
            jsonObject.add("patient_details", jsonPatient);
            jsonObject.add("medicine_details", jsonMedicine);
            jsonObject.addProperty("diagnosis_details", etDiagnosis1.getText().toString());
            jsonObject.addProperty("notes", "");

            Log.e(TAG, "UpdateTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "UpdateTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            PrescriptionData newData = new Gson().fromJson(String.valueOf(jsonObject1), PrescriptionData.class);
                            data = newData;
                            Log.e(TAG, "onResponse: newData >> " + newData);
                            medListMain.clear();
                            medListEditable.clear();
                            for (PreMedicineData data : data.getMedicineDetails()) {
                                medListMain.add(data);
                                medListEditable.add(data);
                            }
                            if (medListMain.contains(null)) {
                                medListMain.remove(null);
                            }
                            if (medListEditable.contains(null)) {
                                medListEditable.remove(null);
                            }
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
            adapter_editable.notifyDataSetChanged();
            adapter_main.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
//                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                bottomSheetTop.setVisibility(View.GONE);
                bsAction1.setVisibility(View.VISIBLE);
                imgArrow1.setVisibility(View.GONE);
                btnOrderNow.setVisibility(View.VISIBLE);
                bottomsheet_bottom.setVisibility(View.VISIBLE);
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

    public class SearchBarAdapter extends ArrayAdapter<ProductData> {

        Context context;
        int resource;
        ArrayList<ProductData> items, tempItems, suggestions;
        /**
         * Custom Filter implementation for custom suggestions we provide.
         */
        Filter nameFilter = new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String str = ((ProductData) resultValue).getName();
                return str;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (ProductData tempdata : tempItems) {
                        if (tempdata.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(tempdata);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<ProductData> filterList = (ArrayList<ProductData>) results.values;
                if (results != null && results.count > 0) {
                    clear();
                    for (ProductData tempdata : filterList) {
                        add(tempdata);
                        notifyDataSetChanged();
                    }
                }
            }
        };

        public SearchBarAdapter(Context context, int resource, ArrayList<ProductData> items) {
            super(context, resource, items);
            this.context = context;
            this.resource = resource;
            this.items = items;
            tempItems = new ArrayList<ProductData>(items); // this makes the difference.
            suggestions = new ArrayList<ProductData>();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.auto_complete_text_single_line, parent, false);
            }
            ProductData tempdata = items.get(position);
            if (tempdata != null) {
                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                if (tvName != null) {
                    tvName.setText(tempdata.getName());
                    tvName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProductData data = searchList.get(position);
                            etMedName1.setText(data.getName());
                            etMedDescription1.setText(data.getShortDescription());
                            etMedManufacturer1.setText(data.getManufacturer());
                            etMedType1.setText(data.getProductType());
                            etMedPrice1.setText(data.getPriceDiscount());
                            etMedImgUrl1.setText(data.getImage());
//                            etMedName1.setSelection(0, etMedName1.getText().length());
                            etMedQty1.requestFocus();
                            etMedName1.dismissDropDown();
                        }
                    });
                }
            }
            return view;
        }

        @Override
        public Filter getFilter() {
            return nameFilter;
        }
    }

    class SearchData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.SEARCH_PRODUCT);
            jsonObject.addProperty("key", etMedName1.getText().toString());
            Log.e(TAG, "SearchData: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.products(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SearchData: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            searchList.clear();
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                ProductData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), ProductData.class);
                                searchList.add(data);
                            }
                            searchAdapter.notifyDataSetChanged();
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            Log.e(TAG, "onResponse: " + msg);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }

}
