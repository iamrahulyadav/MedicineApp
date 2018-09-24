package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.ProductDetailActivity;
import com.hvantage.medicineapp.adapter.AllPrescriptionAdapter;
import com.hvantage.medicineapp.model.ProductData;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;
import com.hvantage.medicineapp.util.fastscrollbars.FastScrollRecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class AllPrescriptionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AllPrescriptionFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private String data;
    private CardView cardEmptyText;
    private RecyclerView mRecyclerView;
    private AllPrescriptionAdapter adapter;
    private Typeface typeface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_all_prescription, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("All Prescriptions");
        }
        init();
//        ArrayList<ProductData> list = new DBHelper(context).getMedicinesAll();
        ArrayList<ProductData> list = new ArrayList<>();
        Log.e(TAG, "onCreateView: list >> " + list);
        if (list != null) setRecyclerView(list);
        return rootView;
    }

    private void init() {
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
    }

    private void setRecyclerView(final ArrayList<ProductData> list) {
        HashMap<String, Integer> mapIndex = calculateIndexesForName(list);
        Log.e(TAG, "setRecyclerView: mapIndex >> " + mapIndex);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new AllPrescriptionAdapter(context, list, mapIndex);
        mRecyclerView.setAdapter(adapter);
        FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(context);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Functions.isConnectingToInternet(context)) {
                    context.startActivity(new Intent(context, ProductDetailActivity.class)
                            .putExtra("medicine_data", list.get(position)));
                   /* showProgressDialog();
                    FirebaseDatabase.getInstance().getReference()
                            .child(AppConstants.APP_NAME)
                            .child(AppConstants.FIREBASE_KEY.MEDICINE)
                            .orderByChild("name")
                            .equalTo(list.get(position).getName())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    hideProgressDialog();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        ProductModel data = postSnapshot.getValue(ProductModel.class);
                                        Log.e(TAG, "onDataChange: data >> " + data);
                                        startActivity(new Intent(context, ProductDetailActivity.class).putExtra("medicine_data", data));
                                        break;
                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    hideProgressDialog();
                                    Log.w(TAG, "onCancelled >> ", databaseError.toException());
                                }
                            });*/
                } else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


    }

    private HashMap<String, Integer> calculateIndexesForName(ArrayList<ProductData> items) {
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            String name = items.get(i).getName();
            String index = name.substring(0, 1);
            index = index.toUpperCase();

            if (!mapIndex.containsKey(index)) {
                mapIndex.put(index, i);
            }
        }
        return mapIndex;
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
