package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.OfferDiscountAdapter;
import com.hvantage.medicineapp.model.OffersModel;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;

import java.util.ArrayList;


public class OfferDiscountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "OfferDiscountFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private OfferDiscountAdapter adapter;
    private ArrayList<OffersModel> list = new ArrayList<OffersModel>();
    private ProgressBar progressBar;
    private String data;
    private CardView cardEmptyText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_offers_discount, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Offers & Discounts");
        }
        list.add(new OffersModel("FIRST50", "Use code FIRST50", "Get Rs. 50 off on your 1st Order. Minimun value Rs. 99. Max discount Rs. 100 per order", "31 August", ""));
        list.add(new OffersModel("FLAT20", "Use code FLAT20", "Get 20% off on every order. Minimun value Rs. 99. Max discount Rs. 100 per order", "31 August", ""));
        list.add(new OffersModel("", "Upto Rs. 350 cashback", "Get cashback upto Rs. 350 on all Medicine and Healthcare needs. Minimun value Rs. 300.", "31 August", ""));
        list.add(new OffersModel("BOGO", "Use code BOGO", "Buy 1 Daily Needs Product and Get 1 Free. Minimun value Rs. 300. Max", "25 August", ""));
        list.add(new OffersModel("", "Pay using paytm", "Get cashback upto Rs. 100 on paytm. Minimun value Rs. 200. Max discount Rs. 150 per order", "31 August", ""));
        list.add(new OffersModel("", "Pay using PhonePe", "Get cashback upto Rs. 100 on payments via PhonePe. Minimun value Rs. 200. Max discount Rs. 150 per order", "31 August", ""));
        init();
        setRecyclerView();
        return rootView;
    }

    private void init() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
    }

    private void setRecyclerView() {
        adapter = new OfferDiscountAdapter(context, list);
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        recylcer_view.addOnItemTouchListener(new RecyclerItemClickListener(context, recylcer_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
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
