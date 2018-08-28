package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;


public class VaultFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "VaultFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private CardView cardFamily, cardDoctors, cardReports, cardUploads;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_vault, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Vault");
        }
        init();
        if (!Functions.isConnectingToInternet(context))
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        return rootView;
    }


    private void init() {
        cardFamily = (CardView) rootView.findViewById(R.id.cardFamily);
        cardDoctors = (CardView) rootView.findViewById(R.id.cardDoctors);
        cardReports = (CardView) rootView.findViewById(R.id.cardReports);
        cardUploads = (CardView) rootView.findViewById(R.id.cardUploads);

        cardFamily.setOnClickListener(this);
        cardDoctors.setOnClickListener(this);
        cardReports.setOnClickListener(this);
        cardUploads.setOnClickListener(this);
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
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        switch (view.getId()) {
            case R.id.cardFamily:
                ft.replace(R.id.main_container, new MyFamilyFragment());
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.cardDoctors:
                ft.replace(R.id.main_container, new MyDoctorsFragment());
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.cardUploads:
                ft.replace(R.id.main_container, new MyPrescriptionFragment());
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.cardReports:
                ft.replace(R.id.main_container, new MyReportsFragment());
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
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

}
