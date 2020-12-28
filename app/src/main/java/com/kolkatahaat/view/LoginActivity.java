package com.kolkatahaat.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kolkatahaat.R;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    DocumentReference fireReference;

    ImageView img_logo;
    TextInputLayout textInputEmail;
    TextInputEditText editTextEmail;
    TextInputLayout textInputPassword;
    TextInputEditText editTextPassword;

    TextView txtForgotPassword;
    Button btnLogin;
    TextView txtNewRegister;
    //TextView txtInfo;

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        fireReference = fireStore.collection("users").document();


        init();
    }

    public void init(){
        img_logo = findViewById(R.id.img_logo);

        textInputEmail = findViewById(R.id.textInputEmail);
        editTextEmail = findViewById(R.id.editTextEmail);

        textInputPassword = findViewById(R.id.textInputPassword);
        editTextPassword = findViewById(R.id.editTextPassword);

        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        btnLogin = findViewById(R.id.btnLogin);
        txtNewRegister = findViewById(R.id.txtNewRegister);
        //txtInfo = findViewById(R.id.txtInfo);

        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() && !validatePassword()) {
                    return;
                }
                else if(validateEmail() && validatePassword()) {
                    if (NetUtils.isNetworkAvailable(LoginActivity.this)) {
                        userLogin();
                    }  else {
                        Utility.displayDialog(LoginActivity.this, getString(R.string.common_no_internet), false);
                    }
                }

            }
        });

        txtNewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /*public void chekcValidation(){
        Utility.hideSoftKeyboard(LoginActivity.this);
        if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
            textInputEmail.setError(getResources().getString(R.string.str_err_msg_user_email));
            //return true;
        } else if (TextUtils.isEmpty(editTextPassword.getText().toString())) {
            txtForgotPassword.setError(getResources().getString(R.string.str_err_msg_user_password));
            //return true;
        } else {
            if (NetUtils.isNetworkAvailable(LoginActivity.this)) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);

            } else {
                Utility.displayDialog(LoginActivity.this, getString(R.string.common_no_internet), false);
            }
            //return false;
        }
    }*/



    public boolean validateEmail(){
        if (TextUtils.isEmpty(editTextEmail.getText().toString().trim())) {
            textInputEmail.setError("Invalid Email address, ex: abc@example.com");
            textInputEmail.requestFocus();
            return false;
        } else {
            //
            String emailId = editTextEmail.getText().toString().trim();
            Boolean  isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
            if (!isValid) {
                //email.setError("Invalid Email address, ex: abc@example.com");
                textInputEmail.setError(getResources().getString(R.string.str_err_msg_user_email));
                textInputEmail.requestFocus();
                return false;
            } else {
                textInputEmail.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(editTextPassword.getText().toString().trim())) {
            textInputPassword.setError("Password is required");
            textInputPassword.requestFocus();
            return false;
        } else if(editTextPassword.getText().toString().trim().length() < 10){
            textInputPassword.setError("Password can't be less than 10 digit");
            textInputPassword.requestFocus();
            return false;
        }
        else {
            textInputPassword.setErrorEnabled(false);
        }
        return true;
    }



    public void userLogin(){

    }

}
