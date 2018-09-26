package com.hvantage.medicineapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
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
import com.hvantage.medicineapp.activity.AddAddressActivity;
import com.hvantage.medicineapp.adapter.AddressAdapter;
import com.hvantage.medicineapp.model.AddressData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hvantage.medicineapp.activity.MainActivity.menuSearch;


public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MyAccountFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private ProgressBar progressBar;
    private String data;
    private CardView cardEmptyText;
    private ArrayList<AddressData> list;
    private AddressAdapter adapter;
    private AppCompatButton btnSubmit;
    private AppCompatTextView tvNameTag, tvName, tvPhoneNo, tvEmail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("My Account");
            if (menuSearch != null)
                menuSearch.setVisible(true);
        }
        list = new ArrayList<AddressData>();
        init();
        setRecyclerView();
        if (Functions.isConnectingToInternet(context))
            new GetAddressTask().execute();
        else
            Toast.makeText(context, "Connect to internet", Toast.LENGTH_SHORT).show();
        return rootView;
    }


    private void init() {
        btnSubmit = (AppCompatButton) rootView.findViewById(R.id.btnSubmit);
        tvNameTag = (AppCompatTextView) rootView.findViewById(R.id.tvNameTag);
        tvName = (AppCompatTextView) rootView.findViewById(R.id.tvName);
        tvPhoneNo = (AppCompatTextView) rootView.findViewById(R.id.tvPhoneNo);
        tvEmail = (AppCompatTextView) rootView.findViewById(R.id.tvEmail);
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddAddressActivity.class));
            }
        });

        tvNameTag.setText(AppPreferences.getUserName(context).charAt(0) + "".toUpperCase());
        tvName.setText(AppPreferences.getUserName(context));
        tvPhoneNo.setText(AppPreferences.getMobileNo(context));
        tvEmail.setText(AppPreferences.getEmail(context));
    }

    private void setRecyclerView() {
        list.clear();
        adapter = new AddressAdapter(context, list, new AddressAdapter.MyAdapterListener() {
            @Override
            public void delete(View v, int position) {
                deleteAddress(list.get(position), position);
            }

            @Override
            public void select(View v, int position) {
            }
        });
        recylcer_view.setLayoutManager(new LinearLayoutManager(context));
        recylcer_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void deleteAddress(final AddressData data, final int position) {
        new AlertDialog.Builder(context)
                .setMessage("Delete this address")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAddress(data, position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void removeAddress(final AddressData data, final int position) {
        showProgressDialog();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method", AppConstants.METHODS.DELETE_ADDRESS);
        jsonObject.addProperty("address_id", data.getAddressId());
        Log.e(TAG, "SaveTask: Request >> " + jsonObject.toString());
        MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
        Call<JsonObject> call = apiService.address(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG, "SaveTask: Response >> " + response.body().toString());
                String resp = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                        hideProgressDialog();
                    } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                        String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");

                        hideProgressDialog();
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }

    class GetAddressTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_MY_ADDRESSES);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));

            Log.e(TAG, "GetAddressTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.address(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "GetAddressTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    list.clear();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                AddressData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), AddressData.class);
                                Log.e(TAG, "onResponse: data >> " + data);
                                list.add(data);
                            }
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
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
            hideProgressDialog();
            adapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
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
