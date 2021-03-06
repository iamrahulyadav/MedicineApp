package com.hvantage.medicineapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.ProgressBar;

import java.net.URLEncoder;

import static com.hvantage.medicineapp.activity.MainActivity.menuSearch;


public class UploadPrecriptionFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final String TAG = "UploadPrecrFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;

    private RelativeLayout btnUpload, btnChoose, btnWhatsApp;
    private ProgressBar progressBar;
    private String userChoosenTask;
    private double total = 0;
    private AppCompatTextView tvInstructions;

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_upload_prescription, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Upload Prescription");
            if (menuSearch != null)
                menuSearch.setVisible(true);
        }
        init();
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
                }
            }
        });
        builder.show();
    }

    private void init() {
        btnUpload = (RelativeLayout) rootView.findViewById(R.id.btnUpload);
        btnChoose = (RelativeLayout) rootView.findViewById(R.id.btnChoose);
        btnWhatsApp = (RelativeLayout) rootView.findViewById(R.id.btnWhatsapp);
        tvInstructions = (AppCompatTextView) rootView.findViewById(R.id.tvInstructions);
        btnUpload.setOnClickListener(this);
        btnChoose.setOnClickListener(this);
        btnWhatsApp.setOnClickListener(this);
        tvInstructions.setOnClickListener(this);
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
        Fragment fragment = null;
        Bundle args;
        switch (view.getId()) {
            case R.id.btnUpload:
                fragment = new AddPrescrFragment();
                args = new Bundle();
                args.putParcelable("data", null);
                args.putString("from", "new");
                fragment.setArguments(args);
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.btnChoose:
//                startActivity(new Intent(context, SelectPrescActivity.class));
                fragment = new MyPrescriptionFragment();
                args = new Bundle();
                fragment.setArguments(args);
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
                break;
            case R.id.btnWhatsapp:
                PackageManager packageManager = context.getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    i.setPackage("com.whatsapp");
                    String url = "https://api.whatsapp.com/send?phone=" + AppConstants.WHATSAPP_NO + "&text=" + URLEncoder.encode("Hello ", "UTF-8");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        context.startActivity(i);
                    } else {
                        Log.e(TAG, "onClick: WhatsApp Not Found");
                        Toast.makeText(context, getResources().getString(R.string.whatsapp_not_found), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvInstructions:
                dialogPrescGuide();
                break;
        }
    }

    private void dialogPrescGuide() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_presc_guide, null);
        dialog.setView(dialogView);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
