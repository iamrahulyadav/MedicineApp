package com.hvantage.medicineapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.adapter.MyFamilyAdapter;
import com.hvantage.medicineapp.model.FamilyData;
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


public class MyFamilyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MyFamilyFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private RecyclerView recylcer_view;
    private MyFamilyAdapter adapter;
    private ArrayList<FamilyData> list = new ArrayList<FamilyData>();
    private ProgressBar progressBar;
    private String data;
    private CardView cardEmptyText;
    private AppCompatButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_my_family, container, false);
        if (intraction != null) {
            intraction.actionbarsetTitle("My Family");
            if (menuSearch != null)
                menuSearch.setVisible(true);
        }
        init();
        setRecyclerView();
        if (Functions.isConnectingToInternet(context))
            new GetDataTask().execute();
        else
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        return rootView;
    }

    private void getData() {
        showProgressDialog();
        FirebaseDatabase.getInstance().getReference()
                .child(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child("+91" + AppPreferences.getMobileNo(context))
                .child(AppConstants.FIREBASE_KEY.MY_FAMILY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            FamilyData data = postSnapshot.getValue(FamilyData.class);
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
        btnAdd = (AppCompatButton) rootView.findViewById(R.id.btnAdd);
        cardEmptyText = (CardView) rootView.findViewById(R.id.cardEmptyText);
        btnAdd.setOnClickListener(this);
    }

    private void setRecyclerView() {
        adapter = new MyFamilyAdapter(context, list, new MyFamilyAdapter.MyAdapterListener() {
            @Override
            public void delete(final View v, final int position) {
                new AlertDialog.Builder(context)
                        .setMessage("Delete " + list.get(position).getName() + "?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showProgressDialog();
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("method", AppConstants.METHODS.DELETE_FAMILY_MEMBER);
                                jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
                                jsonObject.addProperty("family_member_id", list.get(position).getFamilyMemberId());
                                Log.e(TAG, "GetDataTask: Request >> " + jsonObject.toString());
                                MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
                                Call<JsonObject> call = apiService.vault(jsonObject);
                                call.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        Log.e(TAG, "GetDataTask: Response >> " + response.body().toString());
                                        String resp = response.body().toString();
                                        try {
                                            JSONObject jsonObject = new JSONObject(resp);
                                            if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                                                list.remove(position);
                                                adapter.notifyDataSetChanged();
                                            } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                                                String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                            }
                                            hideProgressDialog();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            hideProgressDialog();
                                            Toast.makeText(context, context.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        Log.e(TAG, "onFailure: " + t.getMessage());
                                        hideProgressDialog();
                                        Toast.makeText(context, context.getResources().getString(R.string.api_error_msg), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }

            @Override
            public void select(View v, int position) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                Fragment fragment = new AddFamilyFragment();
                Bundle args = new Bundle();
                args.putParcelable("data", list.get(position));
                fragment.setArguments(args);
                ft.replace(R.id.main_container, fragment);
                ft.addToBackStack(null);
                ft.commitAllowingStateLoss();
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
            case R.id.btnAdd:
                FragmentManager manager = getFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(R.id.main_container, new AddFamilyFragment());
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

    class GetDataTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.GET_MY_FAMILY);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            Log.e(TAG, "GetDataTask: Request >> " + jsonObject.toString());
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.vault(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "GetDataTask: Response >> " + response.body().toString());
                    list.clear();
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                FamilyData data = gson.fromJson(jsonArray.getJSONObject(i).toString(), FamilyData.class);
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
            hideProgressDialog();
            String status = values[0];
            String msg = values[1];
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() > 0)
                cardEmptyText.setVisibility(View.GONE);
            else
                cardEmptyText.setVisibility(View.VISIBLE);
            if (status.equalsIgnoreCase("200")) {
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
