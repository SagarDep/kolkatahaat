package com.kolkatahaat.view;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kolkatahaat.R;
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
    private Button btnSendOtp;

    private LinearLayout llVerifyOtp;
    private TextInputLayout textInputVerifyCode;
    private TextInputEditText editTextVerifyCode;
    private Button btnVerifyCode;

    private ProgressBar progressBar;
    private TextView txtSendingOtp;
    private String verificationId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        init();
    }

    public void init() {
        llSendOtp = findViewById(R.id.llSendOtp);
        textInputMobile = findViewById(R.id.textInputMobile);
        editTextMobile = findViewById(R.id.editTextMobile);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        llVerifyOtp = findViewById(R.id.llVerifyOtp);
        textInputVerifyCode = findViewById(R.id.textInputVerifyCode);
        editTextVerifyCode = findViewById(R.id.editTextVerifyCode);
        btnVerifyCode = findViewById(R.id.btnVerifyCode);

        progressBar = findViewById(R.id.progressBar);
        txtSendingOtp = findViewById(R.id.txtSendingOtp);

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.isNetworkAvailable(PhoneVerificationActivity.this)) {
                    if (!validateMobile()) {
                        return;
                    }
                    else if(validateMobile()) {
                        sendOtp();
                    }
                } else {
                    Utility.displayDialog(PhoneVerificationActivity.this, getString(R.string.common_no_internet), false);
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
        } else {
            textInputMobile.setErrorEnabled(false);
        }
        return true;
    }

    private void sendOtp(){
        String phoneNumber = editTextMobile.getText().toString().trim();
        if(!TextUtils.isEmpty(phoneNumber) && phoneNumber != null) {
            sendVerificationCode(phoneNumber);
        }
    }


    private void sendVerificationCode(String number) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(fireAuth)
                .setPhoneNumber(number)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        progressBar.setVisibility(View.GONE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Toast.makeText(PhoneVerificationActivity.this, verificationId+"", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editTextVerifyCode.setText(code);
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneVerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
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

                    /*Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     startActivity(intent);*/
                    Toast.makeText(PhoneVerificationActivity.this, "Success full login", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(PhoneVerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
