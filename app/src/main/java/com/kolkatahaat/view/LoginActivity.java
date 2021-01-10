package com.kolkatahaat.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.kolkatahaat.R;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.SharedPrefsUtils;
import com.kolkatahaat.utills.Utility;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

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

    public void init() {
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
                } else if (validateEmail() && validatePassword()) {
                    if (NetUtils.isNetworkAvailable(LoginActivity.this)) {
                        String email = editTextEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        userLogin(email, password);
                    } else {
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

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
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


    public boolean validateEmail() {
        if (TextUtils.isEmpty(editTextEmail.getText().toString().trim())) {
            textInputEmail.setError("Invalid Email address, ex: abc@example.com");
            textInputEmail.requestFocus();
            return false;
        } else {
            //
            String emailId = editTextEmail.getText().toString().trim();
            Boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
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
        } else {
            textInputPassword.setErrorEnabled(false);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    token = task.getException().getMessage();
                    Log.w("FCM TOKEN Failed", task.getException());
                } else {
                    token = task.getResult().getToken();
                    Log.i("FCM TOKEN", token);
                }
            }
        });*/


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                Log.e(TAG, "token"+ token);
            }
        });
    }

    public void userLogin(String email, String password) {

        fireAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            final FirebaseUser user = fireAuth.getCurrentUser();
                            //Users userIfo = user.getUid();

                            Log.e(TAG, user.getUid());
                            fireStore.collection("users").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        Users users = documentSnapshot.toObject(Users.class);
                                        Log.e(TAG, users.toString());

                                        Gson gson = new Gson();
                                        String json = gson.toJson(users);
                                        SharedPrefsUtils.saveToPrefs(LoginActivity.this, SharedPrefsUtils.USER_DETAIL, json);

                                        Object userDetial = SharedPrefsUtils.getFromPrefs(LoginActivity.this, SharedPrefsUtils.USER_DETAIL, "");
                                        Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);
                                        if(obj.getUserId() != "" && obj.getUserEmail() != "" && obj.getUserType() == 1){
                                            Intent intent = new Intent(LoginActivity.this,HomeAdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if(obj.getUserId() != "" && obj.getUserEmail() != "" && obj.getUserType() == 2){
                                            Intent intent = new Intent(LoginActivity.this,HomeCustomerActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
