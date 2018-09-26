package com.hvantage.medicineapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.DoctorData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.FragmentIntraction;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hvantage.medicineapp.activity.MainActivity.menuSearch;


public class AddDoctorFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AddDoctorFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private Spinner spinnerGender;
    private EditText etName, etEmail, etPhoneNo, etAddress;
    private CardView btnSubmit;
    private DoctorData data = null;
    private Spinner spinnerSpecialization;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_add_doctor, container, false);

        if (getArguments() != null) {
            data = (DoctorData) getArguments().getParcelable("data");
            Log.e(TAG, "onCreateView: data >> " + data);
        }

        if (intraction != null) {
            if (data == null)
                intraction.actionbarsetTitle("Add Doctor");
            else
                intraction.actionbarsetTitle("Doctor Details");
            if (menuSearch != null)
                menuSearch.setVisible(true);
        }


        init();
        if (!Functions.isConnectingToInternet(context))
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();

        if (data != null) {
            etName.setText(data.getName());
            etEmail.setText(data.getEmail());
            etPhoneNo.setText(data.getMobileNo());
            etAddress.setText(data.getAddress());

            String[] arrayGender = getResources().getStringArray(R.array.gender);
            String[] arraySpe = getResources().getStringArray(R.array.specialization);

            for (int i = 0; i < arraySpe.length; i++) {
                if (arraySpe[i].equalsIgnoreCase(data.getSpecialization()))
                    spinnerSpecialization.setSelection(i);
            }

            for (int i = 0; i < arrayGender.length; i++) {
                if (arrayGender[i].equalsIgnoreCase(data.getGender()))
                    spinnerGender.setSelection(i);
            }
        }
        return rootView;
    }


    private void init() {
        spinnerSpecialization = (Spinner) rootView.findViewById(R.id.spinnerSpecialization);
        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerGender);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etPhoneNo = (EditText) rootView.findViewById(R.id.etPhoneNo);
        etAddress = (EditText) rootView.findViewById(R.id.etAddress);
        btnSubmit = (CardView) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
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
                if (TextUtils.isEmpty(etName.getText().toString()))
                    Toast.makeText(context, "Enter Doctor Name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPhoneNo.getText().toString()))
                    Toast.makeText(context, "Enter Phone No.", Toast.LENGTH_SHORT).show();
                else if (spinnerGender.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Gender", Toast.LENGTH_SHORT).show();
                else if (spinnerSpecialization.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Specialization", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etAddress.getText().toString()))
                    Toast.makeText(context, "Enter Address", Toast.LENGTH_SHORT).show();
                else {
                    if (data == null)
                        new SaveTask().execute();
                    else
                        new UpdateTask().execute();
                }
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

    class SaveTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.ADD_DOCTOR);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("address", etAddress.getText().toString());
            jsonObject.addProperty("email", etEmail.getText().toString());
            jsonObject.addProperty("gender", String.valueOf(spinnerGender.getSelectedItem()));
            jsonObject.addProperty("mobile_no", etPhoneNo.getText().toString());
            jsonObject.addProperty("name", etName.getText().toString());
            jsonObject.addProperty("specialization", String.valueOf(spinnerSpecialization.getSelectedItem()));
            Log.e(TAG, "SaveTask: Request >> " + jsonObject.toString());
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.vault(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SaveTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
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
            if (status.equalsIgnoreCase("200")) {
                getActivity().onBackPressed();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UpdateTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.UPDATE_DOCTOR);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("doctor_id", data.getDoctorId());
            jsonObject.addProperty("address", etAddress.getText().toString());
            jsonObject.addProperty("email", etEmail.getText().toString());
            jsonObject.addProperty("gender", String.valueOf(spinnerGender.getSelectedItem()));
            jsonObject.addProperty("mobile_no", etPhoneNo.getText().toString());
            jsonObject.addProperty("name", etName.getText().toString());
            jsonObject.addProperty("specialization", String.valueOf(spinnerSpecialization.getSelectedItem()));
            Log.e(TAG, "UpdateTask: Request >> " + jsonObject.toString());
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.vault(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "UpdateTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
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
            if (status.equalsIgnoreCase("200")) {
                getActivity().onBackPressed();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
