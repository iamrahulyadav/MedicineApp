package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.PrescriptionCatAdapter;
import com.hvantage.medicineapp.model.CategoryData;
import com.hvantage.medicineapp.model.SubCategoryData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;
import com.hvantage.medicineapp.util.RecyclerItemClickListener;
import com.hvantage.medicineapp.util.fastscrollbars.FastScrollRecyclerViewItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PrescriptionCatFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PrescriptionCatFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private CardView cardEmptyText;
    private RecyclerView mRecyclerView;
    private PrescriptionCatAdapter adapter;
    private Typeface typeface;
    private ArrayList<SubCategoryData> list;
    private CategoryData data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_all_prescription, container, false);
        data = (CategoryData) getArguments().getParcelable("data");
        Log.e(TAG, "onCreateView: data >> " + data);
        if (intraction != null) {
            intraction.actionbarsetTitle(data.getCatName());
        }
        init();
//        ArrayList<ProductData> list = new DBHelper(context).getMedicinesAll();
        /*Log.e(TAG, "onCreateView: list >> " + list);
        if (list != null)*/
        list = new ArrayList<SubCategoryData>();
        //setRecyclerView(list);
        if (Functions.isConnectingToInternet(context))
            new SubCategoryTask().execute();
        else {
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    private void init() {
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
    }

    class SubCategoryTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_ALL_SUBCATEGORIES);
            jsonObject.addProperty("cat_id", data.getCatId());
            Log.e(TAG, "SubCategoryTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.products(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    response = null;
                    try {
                        Log.e(TAG, "SubCategoryTask: Response >> " + response.body().toString());
                        String resp = response.body().toString();
                        list.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                SubCategoryData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), SubCategoryData.class);
                                list.add(data);
                            }
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                    } catch (Exception e) {
                        e.printStackTrace();
                        publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
//            hideProgressDialog();
            Log.e(TAG, "onProgressUpdate: list.size >> " + list.size());
            String status = values[0];
            String msg = values[1];
            /*if (adapter.getItemCount() > 0)
                cardEmptyText.setVisibility(View.GONE);*/
            if (status.equalsIgnoreCase("200")) {
                cardEmptyText.setVisibility(View.GONE);
                setRecyclerView(list);
            } else if (status.equalsIgnoreCase("400")) {
                cardEmptyText.setVisibility(View.VISIBLE);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setRecyclerView(final ArrayList<SubCategoryData> list) {
        HashMap<String, Integer> mapIndex = calculateIndexesForName(list);
        Log.e(TAG, "setRecyclerView: mapIndex >> " + mapIndex);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new PrescriptionCatAdapter(context, list, mapIndex);
        mRecyclerView.setAdapter(adapter);
        FastScrollRecyclerViewItemDecoration decoration = new FastScrollRecyclerViewItemDecoration(context);
        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (Functions.isConnectingToInternet(context)) {
                    /*context.startActivity(new Intent(context, ProductDetailActivity.class)
                            .putExtra("medicine_data", list.get(position)));*/
                    ProductsFragment fragment = new ProductsFragment();
                    Bundle args = new Bundle();
                    args.putParcelable("data", list.get(position));
                    fragment.setArguments(args);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.main_container, fragment);
                    ft.addToBackStack(null);
                    ft.commitAllowingStateLoss();
                } else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


    }

    private HashMap<String, Integer> calculateIndexesForName(ArrayList<SubCategoryData> items) {
        HashMap<String, Integer> mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < items.size(); i++) {
            String name = items.get(i).getSubCatName();
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
