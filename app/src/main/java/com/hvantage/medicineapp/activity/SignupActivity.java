package com.hvantage.medicineapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.UserData;
import com.hvantage.medicineapp.retrofit.ApiClient;
import com.hvantage.medicineapp.retrofit.MyApiEndpointInterface;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.AppPreferences;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignupActivity";
    private static final int REQUEST_ALL_PERMISSIONS = 100;
    AppCompatEditText etName, etEmail;
    CardView btnSave;
    private CardView btnSubmit;
    private AppCompatEditText etPhoneNo;
    private Context context;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private CardView btnVerify;
    private AppCompatEditText etOTP;
    private TextView tvOtpText;
    private TextView btnResend;
    private LinearLayout viewMobile;
    private LinearLayout viewVerify;
    private ProgressBar progressBar;
    private TextView toolbar_title;
    private LinearLayout viewProfile;
    private TextView btnSkip;
    private CheckBox checkBox;
//    private AppCompatEditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_mobile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        context = this;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.e(TAG, "onVerificationCompleted:" + credential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                hideProgressDialog();
                Log.e(TAG, "onVerificationFailed", e);
                mVerificationInProgress = false;
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Invalid mobile no.", Snackbar.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded, please contact developer.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                hideProgressDialog();
                Log.e(TAG, "onCodeSent:" + verificationId);
                tvOtpText.setText("We sent an OTP to your number +91" + etPhoneNo.getText().toString() + ".");
                mVerificationId = verificationId;
                mResendToken = token;
                viewMobile.setVisibility(View.GONE);
                viewVerify.setVisibility(View.VISIBLE);
                toolbar_title.setText("Verify OTP");
            }
        };

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

    private void init() {
        etPhoneNo = (AppCompatEditText) findViewById(R.id.etMobNo);
        etOTP = (AppCompatEditText) findViewById(R.id.etOTP);
        tvOtpText = (TextView) findViewById(R.id.tvOtpText);
        btnSkip = (TextView) findViewById(R.id.btnSkip);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnVerify = (CardView) findViewById(R.id.btnVerify);
        btnResend = (TextView) findViewById(R.id.btnResend);
        viewMobile = (LinearLayout) findViewById(R.id.viewMobile);
        viewVerify = (LinearLayout) findViewById(R.id.viewVerify);
        viewProfile = (LinearLayout) findViewById(R.id.viewProfile);
        etName = (AppCompatEditText) findViewById(R.id.etName);
        etEmail = (AppCompatEditText) findViewById(R.id.etEmail);
//        etPassword = (AppCompatEditText) findViewById(R.id.etPassword);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        btnSave = (CardView) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        String checkBoxText = "I agree to <u> <a href='http://www.hvantagetechnologies.com'>Terms of Use</a></u> and " +
                "<a href='http://www.hvantagetechnologies.com/'>Privacy Policy</a>" + ".";
        checkBox.setText(Html.fromHtml(checkBoxText));
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
        btnSkip.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        btnSave.setEnabled(false);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    btnSave.setEnabled(true);
                    btnSave.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    btnSave.setEnabled(false);
                    btnSave.setCardBackgroundColor(getResources().getColor(R.color.colorGray));
                }
            }
        });
        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Functions.hideSoftKeyboard(SignupActivity.this, view);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification("+91" + etPhoneNo.getText().toString());
                //new CheckMobileNoTask().execute();
                break;
            case R.id.btnVerify:
                String code = etOTP.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    Snackbar.make(findViewById(android.R.id.content), "Enter OTP", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.btnResend:
                resendVerificationCode("+91" + etPhoneNo.getText().toString(), mResendToken);
                break;
            case R.id.btnSave:
                saveData();
                break;
            case R.id.btnSkip:
                startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification("+91" + etPhoneNo.getText().toString());
        }
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        showProgressDialog();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        showProgressDialog();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            toolbar_title.setText("Your Details");
                            viewMobile.setVisibility(View.GONE);
                            viewVerify.setVisibility(View.GONE);
                            viewProfile.setVisibility(View.VISIBLE);
                            hideProgressDialog();

                        } else {
                            hideProgressDialog();
                            // Sign in failed, display a message and update the UI
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                //Toast.makeText(context, "Invalid code.", Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content), "Incorrect OTP", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = etPhoneNo.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            //etPhoneNo.setError("Invalid phone number.");
            Snackbar.make(findViewById(android.R.id.content), "Invalid mobile no.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveData() {
        if (TextUtils.isEmpty(etName.getText().toString()))
            Snackbar.make(findViewById(android.R.id.content), "Enter Your Name", Snackbar.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(etEmail.getText().toString()))
            Snackbar.make(findViewById(android.R.id.content), "Enter Email", Snackbar.LENGTH_SHORT).show();
        else if (!Functions.isEmailValid(etEmail))
            Snackbar.make(findViewById(android.R.id.content), "Invalid Email", Snackbar.LENGTH_SHORT).show();
        /*else if (TextUtils.isEmpty(etPassword.getText().toString()))
            Snackbar.make(findViewById(android.R.id.content), "Enter Password", Snackbar.LENGTH_SHORT).show();
        */
        else {
            new SignupTask().execute();
        }
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(SignupActivity.this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                        },
                        REQUEST_ALL_PERMISSIONS);
            }
            return false;
        } else {
            return true;
        }
    }

    class CheckMobileNoTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.METHODS.CHECK_PHONE_NO);
            jsonObject.addProperty("phone_no", etPhoneNo.getText().toString());
            Log.e(TAG, "CheckMobileNoTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_register(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "CheckMobileNoTask: Response >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status").getAsString().equals("200")) {
                        publishProgress("200", "");

                    } else {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        publishProgress("400", result.get("msg").getAsString());
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
                startPhoneNumberVerification("+91" + etPhoneNo.getText().toString());
            } else if (status.equalsIgnoreCase("400")) {
                /*LENGTH_INDEFINITE*/
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SignupTask().execute();
                    }
                }).show();
            }
        }
    }

    class SignupTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("method", AppConstants.METHODS.USER_SIGNUP);
            jsonObject.addProperty("method", AppConstants.METHODS.USER_OTP_LOGIN);
            jsonObject.addProperty("phone_no", etPhoneNo.getText().toString());
            jsonObject.addProperty("name", etName.getText().toString());
            jsonObject.addProperty("email", etEmail.getText().toString());
//            jsonObject.addProperty("password", etPassword.getText().toString());
            jsonObject.addProperty("fcm_token", FirebaseInstanceId.getInstance().getToken());

            Log.e(TAG, "SignupTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_register(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SignupTask: Response >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status").getAsString().equals("200")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        UserData data = new Gson().fromJson(result, UserData.class);
                        Log.e(TAG, "onResponse: data >> " + data);
                        AppPreferences.setUserId(context, data.getUser_id());
                        AppPreferences.setMobileNo(context, data.getPhoneNo());
                        AppPreferences.setUserName(context, etName.getText().toString());
                        AppPreferences.setEmail(context, etEmail.getText().toString());
                        publishProgress("200", "");
                    } else {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        publishProgress("400", result.get("msg").getAsString());
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
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
            } else if (status.equalsIgnoreCase("400")) {
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SignupTask().execute();
                    }
                }).show();
            }
        }
    }

}
