package com.hvantage.medicineapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.SelectPrescAdapter;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;
import com.hvantage.medicineapp.util.TouchImageView;

import java.util.ArrayList;


public class SelectPrescFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SelectPrescFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private SelectPrescAdapter adapter;
    private ArrayList<PrescriptionModel> list = new ArrayList<PrescriptionModel>();
    private ArrayList<PrescriptionModel> selectedlist = new ArrayList<PrescriptionModel>();
    private ProgressBar progressBar;
    private String data;
    private CardView cardEmptyText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_select_prescription, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Select Prescriptions");
        }


        init();
        setRecyclerView();
        if (getArguments() != null) {
            selectedlist = getArguments().getParcelableArrayList("data");
            if (selectedlist != null)
                Log.e(TAG, "onCreateView: data >> " + selectedlist.size());
            /*presList.add(data);*/
        }

        return rootView;
    }


    private void init() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
    }

    private void setRecyclerView() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        adapter = new SelectPrescAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                showPreviewDialog(list.get(position));

               /* final CharSequence[] items = {"Select Prescription", "View Prescription"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("View Prescription")) {
                            showPreviewDialog(list.get(position));
                        } else if (items[item].equals("Select Prescription")) {
                            selectedlist.add(list.get(position));
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction ft = manager.beginTransaction();
                            Fragment fragment = new UploadPrecriptionFragment();
                            Bundle args = new Bundle();
                            args.putParcelableArrayList("data", selectedlist);
                            fragment.setArguments(args);
                            ft.replace(R.id.main_container, fragment);
//                ft.addToBackStack(null);
                            getActivity().onBackPressed();

                            ft.commitAllowingStateLoss();
                        }
                    }
                });
//        builder.setCancelable(false);
                builder.show();*/

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        adapter.notifyDataSetChanged();
    }

    private void showPreviewDialog(final PrescriptionModel model) {
        final Dialog dialog1 = new Dialog(context, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.presr_preview_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        TouchImageView imgPreview = (TouchImageView) dialog1.findViewById(R.id.imgPreview);
        ImageView imgBack = (ImageView) dialog1.findViewById(R.id.imgBack);
        TextView tvTitle = (TextView) dialog1.findViewById(R.id.tvTitle);
        TextView tvDesciption = (TextView) dialog1.findViewById(R.id.tvDesciption);
        TextView tvDate = (TextView) dialog1.findViewById(R.id.tvDate);
        CardView btnSubmit = (CardView) dialog1.findViewById(R.id.btnSubmit);
        CardView btnCancel = (CardView) dialog1.findViewById(R.id.btnCancel);

//        imgPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imgPreview.setImageBitmap(Functions.base64ToBitmap(model.getImage_base64()));
        tvTitle.setText(model.getTitle());

        tvDesciption.setText(model.getDescription());
        tvDate.setText("Uploaded on : " + model.getDate_time());
        if (model.getDescription().equalsIgnoreCase(""))
            tvDesciption.setVisibility(View.GONE);
        dialog1.show();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                selectedlist.add(model);
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment fragment = new UploadPrecriptionFragment();
                Bundle args = new Bundle();
                args.putParcelableArrayList("data", selectedlist);
                fragment.setArguments(args);
                ft.replace(R.id.main_container, fragment);
//                ft.addToBackStack(null);
                getActivity().onBackPressed();

                ft.commitAllowingStateLoss();
            }
        });

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
            case R.id.fabAdd:
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.main_container, new AddPrescrFragment());
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
