package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.AllUploadedPreAdapter;
import com.hvantage.medicineapp.adapter.UploadedPreAdapter;
import com.hvantage.medicineapp.model.PrescriptionModel;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.GridSpacingItemDecoration;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;


public class MyPrescriptionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MyDoctorsFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private AllUploadedPreAdapter adapter;
    private ArrayList<PrescriptionModel> list = new ArrayList<PrescriptionModel>();
    private ProgressBar progressBar;
    private String data;
    private CardView cardEmptyText;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_my_prescription, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("My Prescriptions");
        }
        init();
        setRecyclerView();
        if (Functions.isConnectingToInternet(context))
            getData();
        else
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        return rootView;
    }

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .child(AppConstants.FIREBASE_KEY.MY_PRESCRIPTIONS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            PrescriptionModel data = postSnapshot.getValue(PrescriptionModel.class);
                            Log.e(TAG, "onDataChange: data >> " + data);
                            list.add(data);
                        }
                        adapter.notifyDataSetChanged();
                        if (adapter.getItemCount() > 0)
                            cardEmptyText.setVisibility(View.GONE);
                        else
                            cardEmptyText.setVisibility(View.VISIBLE);
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "loadPost:onCancelled", databaseError.toException());
                        if (adapter.getItemCount() > 0)
                            cardEmptyText.setVisibility(View.GONE);
                        else
                            cardEmptyText.setVisibility(View.VISIBLE);
                        hideProgressDialog();
                    }
                });
    }

    private void init() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        fabAdd = (FloatingActionButton) rootView.findViewById(R.id.fabAdd);
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
        fabAdd.setOnClickListener(this);
    }

    private void setRecyclerView() {
        int spacing = 30, spanCount = 3;
        boolean includeEdge = true;
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        adapter = new AllUploadedPreAdapter (context, list);
        recylcer_view.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        recylcer_view.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment fragment = new AddPrescrFragment();
                Bundle args = new Bundle();
                args.putSerializable("data", list.get(position));
                fragment.setArguments(args);
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
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
