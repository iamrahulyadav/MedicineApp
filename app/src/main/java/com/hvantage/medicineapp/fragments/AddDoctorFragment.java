package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DoctorModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;


public class AddDoctorFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AddDoctorFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private Spinner spinnerGender;
    private EditText etName, etEmail, etPhoneNo, etAddress;
    private CardView btnSubmit;
    private DoctorModel data = null;
    private Spinner spinnerSpecialization;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_add_doctor, container, false);

        if (getArguments() != null) {
            data = (DoctorModel) getArguments().getSerializable("data");
            Log.e(TAG, "onCreateView: data >> " + data);
        }

        if (intraction != null) {
            if (data == null)
                intraction.actionbarsetTitle("Add Doctor");
            else
                intraction.actionbarsetTitle("Doctor Details");
        }


        init();
        if (!Functions.isConnectingToInternet(context))
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();

        if (data != null) {
            etName.setText(data.getName());
            etEmail.setText(data.getEmail());
            etPhoneNo.setText(data.getMobile_no());
            etAddress.setText(data.getAddress());

            String[] arrayGender = getResources().getStringArray(R.array.gender);
            String[] arraySpe = getResources().getStringArray(R.array.specialization);

            for (int i = 0; i < arraySpe.length; i++) {
                if (arraySpe[i].equalsIgnoreCase(data.getSpecialization()))
                    spinnerSpecialization.setSelection(i);
            }

            for (int i = 0; i < arrayGender.length; i++) {
                if (arrayGender[i].equalsIgnoreCase(data.getGender()))
                    spinnerGender.setSelection(i);
            }
        }
        return rootView;
    }


    private void init() {
        spinnerSpecialization = (Spinner) rootView.findViewById(R.id.spinnerSpecialization);
        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerGender);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etPhoneNo = (EditText) rootView.findViewById(R.id.etPhoneNo);
        etAddress = (EditText) rootView.findViewById(R.id.etAddress);
        btnSubmit = (CardView) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
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
                if (TextUtils.isEmpty(etName.getText().toString()))
                    Toast.makeText(context, "Enter Doctor Name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPhoneNo.getText().toString()))
                    Toast.makeText(context, "Enter Phone No.", Toast.LENGTH_SHORT).show();
                else if (spinnerGender.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Gender", Toast.LENGTH_SHORT).show();
                else if (spinnerSpecialization.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Specialization", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etAddress.getText().toString()))
                    Toast.makeText(context, "Enter Address", Toast.LENGTH_SHORT).show();
                else {
                    if (data == null)
                        saveData();
                    else
                        updateData();
                }
                break;
        }
    }

    private void updateData() {
        Log.e(TAG, "updateData: ");
        Log.e(TAG, "updateData: key >> " + data.getKey());
        showProgressDialog();
        String key = data.getKey();
        DoctorModel model = new DoctorModel(
                key,
                etName.getText().toString(),
                etEmail.getText().toString(),
                etPhoneNo.getText().toString(),
                String.valueOf(spinnerGender.getSelectedItem()),
                String.valueOf(spinnerSpecialization.getSelectedItem()),
                etAddress.getText().toString());
        FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child("+91"+ AppPreferences.getMobileNo(context))
                .child(AppConstants.FIREBASE_KEY.MY_DOCTORS)
                .child(data.getKey())
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
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

    private void saveData() {
        Log.e(TAG, "saveData: ");
        showProgressDialog();
        String key = FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child("+91"+ AppPreferences.getMobileNo(context))
                .child(AppConstants.FIREBASE_KEY.MY_DOCTORS)
                .push().getKey();
        DoctorModel model = new DoctorModel(
                key,
                etName.getText().toString(),
                etEmail.getText().toString(),
                etPhoneNo.getText().toString(),
                String.valueOf(spinnerGender.getSelectedItem()),
                String.valueOf(spinnerSpecialization.getSelectedItem()),
                etAddress.getText().toString());
        FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child("+91"+ AppPreferences.getMobileNo(context))
                .child(AppConstants.FIREBASE_KEY.MY_DOCTORS)
                .child(key)
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
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
