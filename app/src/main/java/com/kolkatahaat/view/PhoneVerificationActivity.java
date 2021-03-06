package com.kolkatahaat.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.kolkatahaat.R;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private static final String TAG = PhoneVerificationActivity.class.getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;

    private LinearLayout llSendOtp;
    private TextInputLayout textInputMobile;
    private TextInputEditText editTextMobile;
    private Button btnSendCode;

    private LinearLayout llVerifyOtp;
    private TextInputLayout textInputVerifyCode;
    private TextInputEditText editTextVerifyCode;
    private Button btnVerifyCode;

    private LinearLayout llSendingCode;
    private ProgressBar progressBar;
    private TextView txtSendingOtp;
    private TextView txtReSendingOtp;
    private String verificationId;
    private String phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        init();
    }

    public void init() {
        llSendOtp = findViewById(R.id.llSendOtp);
        textInputMobile = findViewById(R.id.textInputMobile);
        editTextMobile = findViewById(R.id.editTextMobile);
        btnSendCode = findViewById(R.id.btnSendCode);
        llSendingCode = findViewById(R.id.llSendingCode);

        llVerifyOtp = findViewById(R.id.llVerifyOtp);
        textInputVerifyCode = findViewById(R.id.textInputVerifyCode);
        editTextVerifyCode = findViewById(R.id.editTextVerifyCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);

        progressBar = findViewById(R.id.progressBar);
        txtSendingOtp = findViewById(R.id.txtSendingOtp);
        txtReSendingOtp = findViewById(R.id.txtReSendingOtp);


        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.isNetworkAvailable(PhoneVerificationActivity.this)) {
                    if (!validateMobile()) {
                        return;
                    } else if (validateMobile()) {
                        sendOtp();
                    }
                } else {
                    Utility.displayDialog(PhoneVerificationActivity.this, getString(R.string.common_no_internet), false);
                }
            }
        });

        btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetUtils.isNetworkAvailable(PhoneVerificationActivity.this)) {
                    if (TextUtils.isDigitsOnly(editTextVerifyCode.getText().toString().trim())) {

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, editTextVerifyCode.getText().toString().trim());
                        fireAuth.signInWithCredential(credential).addOnCompleteListener(PhoneVerificationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(PhoneVerificationActivity.this, "Verification Success", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(PhoneVerificationActivity.this, RegisterActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("EXTRA_USER_MOBILE", editTextMobile.getText().toString().trim());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(PhoneVerificationActivity.this, "Verification Failed, Invalid credentials", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                } else {
                    Utility.displayDialog(PhoneVerificationActivity.this, getString(R.string.common_no_internet), false);
                }
            }
        });


        txtReSendingOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(phoneNumber) && phoneNumber != null) {
                    sendVerificationCode(phoneNumber);
                } else {
                    llSendOtp.setVisibility(View.VISIBLE);
                    llVerifyOtp.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean validateMobile() {
        if (TextUtils.isEmpty(editTextMobile.getText().toString().trim())) {
            textInputMobile.setError("Required Field!");
            textInputMobile.requestFocus();
            return false;
        } else if(editTextMobile.getText().toString().trim().length() > 10){
            textInputMobile.setError("Mobile can't be less than 10 digit");
            textInputMobile.requestFocus();
            return false;
        } else if(editTextMobile.getText().toString().trim().length() < 10){
            textInputMobile.setError("Mobile can't be less than 10 digit");
            textInputMobile.requestFocus();
            return false;
        } else if (!TextUtils.isDigitsOnly(editTextMobile.getText().toString().trim())) {
            textInputMobile.setError("Enter valid mobile number");
            textInputMobile.requestFocus();
            return false;
        } else {
            textInputMobile.setErrorEnabled(false);
        }
        return true;
    }

    private void sendOtp(){
        phoneNumber = editTextMobile.getText().toString().trim();
        if(!TextUtils.isEmpty(phoneNumber) && phoneNumber != null) {
            sendVerificationCode(phoneNumber);

        }
    }


    private void sendVerificationCode(String number) {
        llSendingCode.setVisibility(View.VISIBLE);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fireAuth)
                .setPhoneNumber("+91"+number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            //Toast.makeText(PhoneVerificationActivity.this, verificationId+"", Toast.LENGTH_LONG).show();

            llSendingCode.setVisibility(View.GONE);
            llSendOtp.setVisibility(View.GONE);
            llVerifyOtp.setVisibility(View.VISIBLE);
            resendVisible();
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                llSendingCode.setVisibility(View.GONE);
                llSendOtp.setVisibility(View.GONE);
                llVerifyOtp.setVisibility(View.VISIBLE);
                resendVisible();

                editTextVerifyCode.setText(code);
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            llSendingCode.setVisibility(View.GONE);
            llSendOtp.setVisibility(View.VISIBLE);
            llVerifyOtp.setVisibility(View.GONE);
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        fireAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(PhoneVerificationActivity.this, RegisterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("EXTRA_USER_MOBILE", editTextMobile.getText().toString().trim());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PhoneVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    llSendingCode.setVisibility(View.GONE);
                    Log.e("Error==>", task.getException().getMessage());
                }
            }
        });
    }

    public void resendVisible(){
        txtReSendingOtp.postDelayed(new Runnable() {
            public void run() {
                txtReSendingOtp.setVisibility(View.VISIBLE);
            }
        }, 30000);
    }
}
