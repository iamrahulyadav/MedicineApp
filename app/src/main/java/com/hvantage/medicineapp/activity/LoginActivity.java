package com.hvantage.medicineapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hvantage.medicineapp.R;
import com.hvantage.medicineapp.model.User;
import com.hvantage.medicineapp.util.AppConstants;
import com.hvantage.medicineapp.util.Functions;
import com.hvantage.medicineapp.util.ProgressBar;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_ALL_PERMISSIONS = 100;
    EditText etName, etEmail;
    CardView btnSave;
    private CardView btnSubmit;
    private EditText etPhoneNo;
    private Context context;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private CardView btnVerify;
    private EditText etOTP;
    private TextView tvOtpText;
    private TextView btnResend;
    private LinearLayout viewMobile;
    private LinearLayout viewVerify;
    private ProgressBar progressBar;
    private TextView toolbar_title;
    private LinearLayout viewProfile;
    private TextView btnSkip;
    private CheckBox checkBox;

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
                tvOtpText.setText("We have sent an OTP to your number +91" + etPhoneNo.getText().toString() + ".");
                mVerificationId = verificationId;
                mResendToken = token;
                viewMobile.setVisibility(View.GONE);
                viewVerify.setVisibility(View.VISIBLE);
                toolbar_title.setText("Verify");
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
        etPhoneNo = (EditText) findViewById(R.id.etMobNo);
        etOTP = (EditText) findViewById(R.id.etOTP);
        tvOtpText = (TextView) findViewById(R.id.tvOtpText);
        btnSkip = (TextView) findViewById(R.id.btnSkip);
        btnSubmit = (CardView) findViewById(R.id.btnSubmit);
        btnVerify = (CardView) findViewById(R.id.btnVerify);
        btnResend = (TextView) findViewById(R.id.btnResend);
        viewMobile = (LinearLayout) findViewById(R.id.viewMobile);
        viewVerify = (LinearLayout) findViewById(R.id.viewVerify);
        viewProfile = (LinearLayout) findViewById(R.id.viewProfile);
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
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
                Functions.hideSoftKeyboard(LoginActivity.this, view);
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
                break;
            case R.id.btnVerify:
                String code = etOTP.getText().toString();
                if (TextUtils.isEmpty(code)) {
//                    etOTP.setError("Cannot be empty.");
                    Snackbar.make(findViewById(android.R.id.content), "Enter otp.", Snackbar.LENGTH_SHORT).show();
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
                startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
                            FirebaseDatabase.getInstance().getReference().child(AppConstants.APP_NAME).child(AppConstants.FIREBASE_KEY.USERS)
                                    .child(mAuth.getCurrentUser().getPhoneNumber())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            hideProgressDialog();
                                            Log.e(TAG, "onDataChange: dataSnapshot >> " + dataSnapshot.getValue());

                                            if (dataSnapshot.getValue() != null) {
                                                User data = dataSnapshot.getValue(User.class);
                                                etName.setText(data.getName());
                                                etEmail.setText(data.getEmail());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            hideProgressDialog();
                                        }
                                    });
                        } else {
                            hideProgressDialog();
                            // Sign in failed, display a message and update the UI
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                //Toast.makeText(context, "Invalid code.", Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content), "Invalid code.", Snackbar.LENGTH_SHORT).show();
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
            Snackbar.make(findViewById(android.R.id.content), "Enter First Name", Snackbar.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(etEmail.getText().toString()))
            Snackbar.make(findViewById(android.R.id.content), "Enter Email", Snackbar.LENGTH_SHORT).show();
        else if (!Functions.isEmailValid(etEmail))
            Snackbar.make(findViewById(android.R.id.content), "Invalid Email", Snackbar.LENGTH_SHORT).show();
        else {

            showProgressDialog();
            Log.e(TAG, "saveData: name >> " + etName.getText().toString());

            User data = new User(
                    mAuth.getCurrentUser().getUid(),
                    etName.getText().toString(),
                    etEmail.getText().toString(),
                    etPhoneNo.getText().toString()
            );

            DatabaseReference database = FirebaseDatabase.getInstance().getReference(AppConstants.APP_NAME).child(AppConstants.FIREBASE_KEY.USERS);

            database.child(mAuth.getCurrentUser().getPhoneNumber()).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(etName.getText().toString())
                            .build();
                    user.updateProfile(profileUpdates);
                    hideProgressDialog();
                    Snackbar.make(findViewById(android.R.id.content), "Registered.", Snackbar.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    Log.e(TAG, "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                    Log.e(TAG, "Error writing document", e);
                    Snackbar.make(findViewById(android.R.id.content), "Failed, try again.", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean checkPermission() {
        if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    && (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(LoginActivity.this,
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

}
