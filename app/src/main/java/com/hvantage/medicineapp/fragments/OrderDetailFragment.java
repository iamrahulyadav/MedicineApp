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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.OrderDetailItemAdapter;
import com.hvantage.medicineapp.adapter.OrderDetailPresMedsAdapter;
import com.hvantage.medicineapp.model.AddressData;
import com.hvantage.medicineapp.model.Item;
import com.hvantage.medicineapp.model.OrderData;
import com.hvantage.medicineapp.model.PreMedicineData;
import com.hvantage.medicineapp.model.PrescriptionData;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import static com.hvantage.medicineapp.activity.MainActivity.menuSearch;


public class OrderDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "OrderDetailFragment";
    TextView tvOrderID, tvDateTime, tvAddress1, tvAddress2, tvAddress3, tvAddress4, tvPayableAmt1;
    TextView tvTotalPrice, tvPayableAmt, tvStatus, tvPayMode;
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private OrderDetailItemAdapter adapter;
    private List<Item> listItems;
    private ProgressBar progressBar;
    private OrderData data;
    private RecyclerView recylcer_view_items;
    private LinearLayout llAmount;
    private RecyclerView recylcer_view_precs_meds;
    private OrderDetailPresMedsAdapter adapterPresMeds;
    private List<PrescriptionData> presList;
    private PrescriptionData prescriptionData;
    private ImageView imgPrescription;
    private ArrayList<PreMedicineData> medList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("Order Detail");
            if (menuSearch != null)
                menuSearch.setVisible(true);
        }
        if (getArguments() != null) {
            data = (OrderData) getArguments().getParcelable("data");
            Log.e(TAG, "onCreateView: data >> " + data);
        }
        listItems = new ArrayList<Item>();
        presList = new ArrayList<PrescriptionData>();
        medList = new ArrayList<PreMedicineData>();
        Log.e(TAG, "onCreateView: prescriptionData >> " + prescriptionData);

        init();

        if (data != null) {
            tvOrderID.setText("ORD" + data.getOrderId().replace(",", ""));
            tvStatus.setText(data.getOrderStatus());
            tvDateTime.setText(data.getDatdateTimee());
            tvPayableAmt1.setText("Rs. " + data.getPayableAmount());
            tvPayableAmt.setText("Rs. " + data.getPayableAmount());
            tvTotalPrice.setText("Rs. " + data.getTotalAmount());
            tvPayMode.setText(data.getPaymentMode());
            AddressData deliveryData = data.getAddressData();
            if (deliveryData != null) {
                tvAddress1.setText(deliveryData.getName() + ", " + deliveryData.getContactNo());
                tvAddress2.setText(deliveryData.getAddress() + ", " + deliveryData.getLandmark());
                tvAddress3.setText(deliveryData.getCity() + ", " + deliveryData.getPincode());
                tvAddress4.setText(deliveryData.getState() + ", " + "India");
            }
            listItems = data.getItems();
            if (listItems.contains(null))
                listItems.remove(null);
            presList = data.getPrescriptionData();
            prescriptionData = presList.get(0);
            medList = prescriptionData.getMedicineDetails();
            if (medList.contains(null))
                medList.remove(null);
            llAmount.setVisibility(View.VISIBLE);
            setRecyclerView();
            setRecyclerViewPrecsMeds();
            if (!prescriptionData.getImage().equalsIgnoreCase("")) {
                Glide.with(context)
                        .load(prescriptionData.getImage())
                        .crossFade()
                        .placeholder(R.drawable.no_image_placeholder)
                        .into(imgPrescription);
            }
        }
        return rootView;
    }

    private void init() {
        tvOrderID = (TextView) rootView.findViewById(R.id.tvOrderID);
        tvStatus = (TextView) rootView.findViewById(R.id.tvStatus);
        tvDateTime = (TextView) rootView.findViewById(R.id.tvDateTime);
        tvPayableAmt1 = (TextView) rootView.findViewById(R.id.tvPayableAmt1);
        tvAddress1 = (TextView) rootView.findViewById(R.id.tvAddress1);
        tvAddress2 = (TextView) rootView.findViewById(R.id.tvAddress2);
        tvAddress3 = (TextView) rootView.findViewById(R.id.tvAddress3);
        tvAddress4 = (TextView) rootView.findViewById(R.id.tvAddress4);
        tvPayableAmt1 = (TextView) rootView.findViewById(R.id.tvPayableAmt1);
        tvPayableAmt = (TextView) rootView.findViewById(R.id.tvPayableAmt);
        tvTotalPrice = (TextView) rootView.findViewById(R.id.tvTotalPrice);
        tvPayMode = (TextView) rootView.findViewById(R.id.tvPayMode);
        llAmount = (LinearLayout) rootView.findViewById(R.id.llAmount);
        recylcer_view_items = (RecyclerView) rootView.findViewById(R.id.recylcer_view_items);
        recylcer_view_precs_meds = (RecyclerView) rootView.findViewById(R.id.recylcer_view_precs_meds);
        imgPrescription = (ImageView) rootView.findViewById(R.id.imgPrescription);
    }

    private void setRecyclerView() {
        adapter = new OrderDetailItemAdapter(context, listItems);
        recylcer_view_items.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_items.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setRecyclerViewPrecsMeds() {
        adapterPresMeds = new OrderDetailPresMedsAdapter(context, medList);
        recylcer_view_precs_meds.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view_precs_meds.setAdapter(adapterPresMeds);
        adapterPresMeds.notifyDataSetChanged();
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
