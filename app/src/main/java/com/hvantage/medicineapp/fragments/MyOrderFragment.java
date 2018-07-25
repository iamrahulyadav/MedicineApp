package com.hvantage.medicineapp.fragments;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.MyOrdersAdapter;
import com.hvantage.medicineapp.model.OrderData;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;


public class MyOrderFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MyOrderFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private MyOrdersAdapter adapter;
    private ArrayList<OrderData> list = new ArrayList<OrderData>();
    private RecyclerView recylcer_view;
    private CardView cardEmptyText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_my_orders, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("My Orders");
        }
        init();
        setRecyclerView();
        if (Functions.isConnectingToInternet(context))
            getData();
        else
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        return rootView;
    }

    private void setRecyclerView() {
        adapter = new MyOrdersAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment fragment = new AddDoctorFragment();
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


    private void init() {
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
    }

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.ORDERS)
//                .orderByChild("date_time_server")
                .orderByChild("by")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: " + postSnapshot.getValue());
                            OrderData data = postSnapshot.getValue(OrderData.class);
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
