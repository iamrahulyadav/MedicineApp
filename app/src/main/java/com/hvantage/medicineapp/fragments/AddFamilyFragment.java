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
import com.hvantage.medicineapp.model.FamilyData;
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


public class AddFamilyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AddFamilyFragment";
    private Context context;
    private View rootView;
    private FragmentIntraction intraction;
    private ProgressBar progressBar;
    private Spinner spinnerRelationship, spinnerGender, spinnerBG;
    private EditText etName, etEmail, etPhoneNo, etHeight, etWeight, etAllergy;
    private CardView btnSubmit;
    private FamilyData data = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_add_family, container, false);

        if (getArguments() != null) {
            data = (FamilyData) getArguments().getParcelable("data");
            Log.e(TAG, "onCreateView: data >> " + data);
        }

        if (intraction != null) {
            if (data == null)
                intraction.actionbarsetTitle("Add Member");
            else
                intraction.actionbarsetTitle("Member Details");
        }


        init();
        if (!Functions.isConnectingToInternet(context))
            Toast.makeText(context, getResources().getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();

        if (data != null) {
            etName.setText(data.getName());
            etEmail.setText(data.getEmail());
            etPhoneNo.setText(data.getMobileNo());
            etHeight.setText(data.getHeight());
            etWeight.setText(data.getWeight());
            etAllergy.setText(data.getKnownAllergies());

            String[] arrayRelation = getResources().getStringArray(R.array.relation);
            String[] arrayGender = getResources().getStringArray(R.array.gender);
            String[] arrayBG = getResources().getStringArray(R.array.blood_groups);

            for (int i = 0; i < arrayRelation.length; i++) {
                if (arrayRelation[i].equalsIgnoreCase(data.getRelation()))
                    spinnerRelationship.setSelection(i);
            }

            for (int i = 0; i < arrayGender.length; i++) {
                if (arrayGender[i].equalsIgnoreCase(data.getGender()))
                    spinnerGender.setSelection(i);
            }

            for (int i = 0; i < arrayBG.length; i++) {
                if (arrayBG[i].equalsIgnoreCase(data.getBloodGroup()))
                    spinnerBG.setSelection(i);
            }
        }
        return rootView;
    }


    private void init() {
        spinnerRelationship = (Spinner) rootView.findViewById(R.id.spinnerRelationship);
        spinnerGender = (Spinner) rootView.findViewById(R.id.spinnerGender);
        spinnerBG = (Spinner) rootView.findViewById(R.id.spinnerBG);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etPhoneNo = (EditText) rootView.findViewById(R.id.etPhoneNo);
        etHeight = (EditText) rootView.findViewById(R.id.etHeight);
        etWeight = (EditText) rootView.findViewById(R.id.etWeight);
        etAllergy = (EditText) rootView.findViewById(R.id.etAllergy);
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
                if (spinnerRelationship.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Relationship", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etName.getText().toString()))
                    Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(etPhoneNo.getText().toString()))
                    Toast.makeText(context, "Enter Phone No.", Toast.LENGTH_SHORT).show();
                else if (spinnerGender.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Gender", Toast.LENGTH_SHORT).show();
                else if (spinnerBG.getSelectedItemPosition() == 0)
                    Toast.makeText(context, "Select Blood Group", Toast.LENGTH_SHORT).show();
                else {
                    if (data == null)
                        new SaveTask().execute();
                    else
                        new UpdateTask().execute();
                }
                break;
        }
    }

    /*private void updateData() {
        Log.e(TAG, "updateData: ");
        Log.e(TAG, "updateData: key >> " + data.getKey());
        showProgressDialog();
        String key = data.getKey();
        FamilyData model = new FamilyData(
                key,
                etName.getText().toString(),
                etEmail.getText().toString(),
                etPhoneNo.getText().toString(),
                String.valueOf(spinnerRelationship.getSelectedItem()),
                String.valueOf(spinnerGender.getSelectedItem()),
                String.valueOf(spinnerBG.getSelectedItem()),
                etHeight.getText().toString(),
                etWeight.getText().toString(),
                etAllergy.getText().toString(),
                "");
        FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child("+91" + AppPreferences.getMobileNo(context))
                .child(AppConstants.FIREBASE_KEY.MY_FAMILY)
                .child(data.getKey())
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                        Log.e(TAG, "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error writing document", e);
            }
        });
    }

    private void saveData() {
        Log.e(TAG, "saveData: ");
        showProgressDialog();
        String key = FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child("+91" + AppPreferences.getMobileNo(context))
                .child(AppConstants.FIREBASE_KEY.MY_FAMILY)
                .push().getKey();
        FamilyData model = new FamilyData(
                key,
                etName.getText().toString(),
                etEmail.getText().toString(),
                etPhoneNo.getText().toString(),
                String.valueOf(spinnerRelationship.getSelectedItem()),
                String.valueOf(spinnerGender.getSelectedItem()),
                String.valueOf(spinnerBG.getSelectedItem()),
                etHeight.getText().toString(),
                etWeight.getText().toString(),
                etAllergy.getText().toString(),
                "");
        FirebaseDatabase.getInstance()
                .getReference(AppConstants.APP_NAME)
                .child(AppConstants.FIREBASE_KEY.VAULT)
                .child("+91" + AppPreferences.getMobileNo(context))
                .child(AppConstants.FIREBASE_KEY.MY_FAMILY)
                .child(key)
                .setValue(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                        Log.e(TAG, "DocumentSnapshot successfully written!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error writing document", e);
            }
        });
    }*/

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
            jsonObject.addProperty("method", AppConstants.METHODS.ADD_FAMILY_MEMBER);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("blood_group", String.valueOf(spinnerBG.getSelectedItem()));
            jsonObject.addProperty("email", etEmail.getText().toString());
            jsonObject.addProperty("gender", String.valueOf(spinnerGender.getSelectedItem()));
            jsonObject.addProperty("height", etHeight.getText().toString());
            jsonObject.addProperty("image", "");
            jsonObject.addProperty("known_allergies", etAllergy.getText().toString());
            jsonObject.addProperty("mobile_no", etPhoneNo.getText().toString());
            jsonObject.addProperty("name", etName.getText().toString());
            jsonObject.addProperty("relation", String.valueOf(spinnerRelationship.getSelectedItem()));
            jsonObject.addProperty("weight", etWeight.getText().toString());
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
            jsonObject.addProperty("method", AppConstants.METHODS.UPDATE_FAMILY_MEMBER);
            jsonObject.addProperty("user_id", AppPreferences.getUserId(context));
            jsonObject.addProperty("family_member_id", data.getFamilyMemberId());
            jsonObject.addProperty("blood_group", String.valueOf(spinnerBG.getSelectedItem()));
            jsonObject.addProperty("email", etEmail.getText().toString());
            jsonObject.addProperty("gender", String.valueOf(spinnerGender.getSelectedItem()));
            jsonObject.addProperty("height", etHeight.getText().toString());
            jsonObject.addProperty("image", "");
            jsonObject.addProperty("known_allergies", etAllergy.getText().toString());
            jsonObject.addProperty("mobile_no", etPhoneNo.getText().toString());
            jsonObject.addProperty("name", etName.getText().toString());
            jsonObject.addProperty("relation", String.valueOf(spinnerRelationship.getSelectedItem()));
            jsonObject.addProperty("weight", etWeight.getText().toString());
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
