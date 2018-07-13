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
import com.hvantage.medicineapp.model.FamilyModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;


public class AddFamilyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AddFamilyFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private Spinner spinnerRelationship, spinnerGender, spinnerBG;
    private EditText etName, etEmail, etPhoneNo, etHeight, etWeight, etAllergy;
    private CardView btnSubmit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_add_family, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Add Family Mother");
        }
        init();
        if (!Functions.isConnectingToInternet(context))
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        return rootView;
    }


    private void init() {
        spinnerRelationship = (Spinner) rootView.findViewById(R.id.spinnerRelationship);
        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerGender);
        spinnerBG = (Spinner) rootView.findViewById(R.id.spinnerBG);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etPhoneNo = (EditText) rootView.findViewById(R.id.etPhoneNo);
        etHeight = (EditText) rootView.findViewById(R.id.etHeight);
        etWeight = (EditText) rootView.findViewById(R.id.etWeight);
        etAllergy = (EditText) rootView.findViewById(R.id.etAllergy);
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
                if (spinnerRelationship.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Relationship", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etName.getText().toString()))
                    Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPhoneNo.getText().toString()))
                    Toast.makeText(context, "Enter Phone No.", Toast.LENGTH_SHORT).show();
                else if (spinnerGender.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Gender", Toast.LENGTH_SHORT).show();
                else if (spinnerBG.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Blood Group", Toast.LENGTH_SHORT).show();
                else {
                    FamilyModel model = new FamilyModel(
                            etName.getText().toString(),
                            etEmail.getText().toString(),
                            etPhoneNo.getText().toString(),
                            String.valueOf(spinnerRelationship.getSelectedItem()),
                            String.valueOf(spinnerGender.getSelectedItem()),
                            String.valueOf(spinnerBG.getSelectedItem()),
                            etHeight.getText().toString(),
                            etWeight.getText().toString(),
                            etAllergy.getText().toString(),
                            "");
                    saveData(model);
                }
                break;
        }
    }

    private void saveData(FamilyModel model) {
        showProgressDialog();
        String key = FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.MY_FAMILY)
                .push().getKey();

        FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.MY_FAMILY)
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
