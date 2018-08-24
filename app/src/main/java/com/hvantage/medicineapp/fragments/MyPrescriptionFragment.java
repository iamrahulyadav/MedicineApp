package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.activity.SelectAddressActivity;
import com.hvantage.medicineapp.adapter.AllUploadedPreAdapter;
import com.hvantage.medicineapp.model.PrescriptionData;
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


public class MyPrescriptionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MyDoctorsFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private AllUploadedPreAdapter adapter;
    private ArrayList<PrescriptionData> list = new ArrayList<PrescriptionData>();
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
        if (Functions.isConnectingToInternet(context)) {
            new GetDataTask().execute();
        }//getData();
        else
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        return rootView;
    }

    class GetDataTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_MY_PRESCRIPTIONS);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));

            Log.e(TAG, "GetDataTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.order(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    try {
                        Log.e(TAG, "GetDataTask: Response >> " + response.body().toString());
                        String resp = response.body().toString();
                        list.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                PrescriptionData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), PrescriptionData.class);
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
            //hideProgressDialog();
            adapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void init() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        fabAdd = (FloatingActionButton) rootView.findViewById(R.id.fabAdd);
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
        fabAdd.setOnClickListener(this);
        setRecyclerView();

    }

    private void setRecyclerView() {
        recylcer_view = (RecyclerView) rootView.findViewById(R.id.recylcer_view);
        adapter = new AllUploadedPreAdapter(context, list, new AllUploadedPreAdapter.MyAdapterListener() {
            @Override
            public void viewOrder(View v, int position) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment fragment = new AddPrescrFragment();
                Bundle args = new Bundle();
                args.putParcelable("data", list.get(position));
                args.putString("from", "view");
                fragment.setArguments(args);
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
            }

            @Override
            public void placeOrder(View v, int position) {
                AppPreferences.setOrderType(context, AppConstants.ORDER_TYPE.ORDER_WITH_PRESCRIPTION);
                AppPreferences.setSelectedPresId(context, list.get(position).getPrescription_id());
                Intent intent = new Intent(getActivity(), SelectAddressActivity.class);
                startActivity(intent);
            }
        });
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
