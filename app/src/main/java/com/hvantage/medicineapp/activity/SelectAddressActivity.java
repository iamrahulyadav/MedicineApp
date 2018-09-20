package com.hvantage.medicineapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.AddressAdapter;
import com.hvantage.medicineapp.model.AddressData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectAddressActivity extends AppCompatActivity {
    private static final String TAG = "SelectAddressActivity";
    private Context context;
    private ArrayList<AddressData> list = new ArrayList<AddressData>();
    private AddressAdapter adapter;
    private RecyclerView recylcer_view;
    private ProgressBar progressBar;
    private CardView btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Functions.isConnectingToInternet(context))
            new GetDataTask().execute();
        else
            Toast.makeText(context, "Connect to internet", Toast.LENGTH_SHORT).show();
    }

    private void init() {
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddAddressActivity.class));
            }
        });
    }

    private void setRecyclerView() {
        list.clear();
        recylcer_view = (RecyclerView) findViewById(R.id.recylcer_view);
        adapter = new AddressAdapter(context, list, new AddressAdapter.MyAdapterListener() {
            @Override
            public void delete(View v, int position) {
                deleteAddress(list.get(position), position);
            }

            @Override
            public void select(View v, int position) {
                Log.e(TAG, "select: " + String.valueOf(new Gson().toJsonTree(list.get(position), AddressData.class)));
                AppPreferences.setSelectedAddId(context, list.get(position).getAddressId());
                AppPreferences.setSelectedAdd(context, String.valueOf(new Gson().toJsonTree(list.get(position), AddressData.class)));
                Intent intent = new Intent(context, ConfirmOrderActivity.class);
                intent.putExtra("data", list.get(position));
                startActivity(intent);
                finish();
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

    private void removeAddress(AddressData data, final int position) {
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
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
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

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.ADDRESS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            AddressData data = postSnapshot.getValue(AddressData.class);
                            if (data != null) {
                                list.add(data);
                                adapter.notifyDataSetChanged();
                            }

                            /*if (adapter.getItemCount() > 1) {
                                tvInstructions.setVisibility(View.GONE);
                            } else
                                tvInstructions.setVisibility(View.GONE);
                            if (adapter.getItemCount() >= 3) {
                                btnUpload.setVisibility(View.GONE);
                            } else {
                                btnUpload.setVisibility(View.VISIBLE);
                            }*/
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        list.clear();
                        // Getting Post failed, log a message
                        Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                        hideProgressDialog();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_cart) {
        }
        return super.onOptionsItemSelected(item);
    }

    class GetDataTask extends AsyncTask<Void, String, Void> {
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

            Log.e(TAG, "GetDataTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.address(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "GetDataTask: Response >> " + response.body().toString());
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
}
