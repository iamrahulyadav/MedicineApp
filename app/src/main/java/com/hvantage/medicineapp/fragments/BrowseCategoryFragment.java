package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.BrowseCategoryAdapter;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;


public class BrowseCategoryFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BrowseCategoryFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private BrowseCategoryAdapter adapter;
    private ArrayList<String> list = new ArrayList<String>();
    private ProgressBar progressBar;
    private String data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_browse_category, container, false);

        data = getArguments().getString("data");
        Log.e(TAG, "onCreateView: data >> " + data);
        if (intraction != null) {
            intraction.actionbarsetTitle(data);
        }
        init();
        setRecyclerView();
        getData();
        return rootView;
    }

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.CATEGORY)
                .child(data)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: postSnapshot >> " + postSnapshot);
                            list.add(postSnapshot.getKey().toString());
                        }
                        adapter.notifyDataSetChanged();
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
                    }
                });
    }

    private void init() {

    }

    private void setRecyclerView() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        adapter = new BrowseCategoryAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
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
