package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
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

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.AllPrescriptionAdapter;
import com.hvantage.medicineapp.database.DBHelper;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.fastscrollbars.FastScrollRecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class AllPrescriptionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BrowseCategoryFragment";
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
        ArrayList<String> list = new DBHelper(context).getMedicinesSearch();
        Log.e(TAG, "onCreateView: list >> " + list);
        setRecyclerView(list);
        return rootView;
    }

    private void init() {
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
    }

    private void setRecyclerView(ArrayList<String> list) {
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
    }

    private HashMap<String, Integer> calculateIndexesForName(ArrayList<String> items) {
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            String name = items.get(i);
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
