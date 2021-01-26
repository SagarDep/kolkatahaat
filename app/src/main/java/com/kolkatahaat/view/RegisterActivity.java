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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.R;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.SharedPrefsUtils;
import com.kolkatahaat.utills.Utility;

public class RegisterActivity extends AppCompatActivity {

    public final String TAG = RegisterActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private DocumentReference fireReference;

    private ImageView img_logo;

    private TextInputLayout textInputName;
    private TextInputEditText editTextName;

    private TextInputLayout textInputAddress;
    private TextInputEditText editTextAddress;

    private TextInputLayout textInputEmail;
    private TextInputEditText editTextEmail;

    private TextInputLayout textInputMobile;
    private TextInputEditText editTextMobile;

    private TextInputLayout textInputPassword;
    private TextInputEditText editTextPassword;
    private Button btnRegister;
    private TextView txtLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        fireReference = fireStore.collection("users").document();

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("EXTRA_USER_MOBILE")) &&
                    getIntent().getStringExtra("EXTRA_USER_MOBILE") != null) {
                String userMobile = getIntent().getStringExtra("EXTRA_USER_MOBILE");
                editTextMobile.setText(userMobile);
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    public void init(){

        textInputName = findViewById(R.id.textInputName);
        editTextName = findViewById(R.id.editTextName);

        textInputAddress = findViewById(R.id.textInputAddress);
        editTextAddress = findViewById(R.id.editTextAddress);

        textInputEmail = findViewById(R.id.textInputEmail);
        editTextEmail = findViewById(R.id.editTextEmail);

        textInputMobile = findViewById(R.id.textInputMobile);
        editTextMobile = findViewById(R.id.editTextMobile);

        textInputPassword = findViewById(R.id.textInputPassword);
        editTextPassword = findViewById(R.id.editTextPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUserName() && !validateAddress() && !validateEmail() && !validateMobile() && !validatePassword()) {
                    return;
                }
                else if(validateUserName() && validateAddress() && validateEmail() && validateMobile() && validatePassword()) {
                    userRegister();
                }

            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateUserName() {
        if (TextUtils.isEmpty(editTextName.getText().toString().trim())) {
            textInputName.setError("Required Field!");
            textInputName.requestFocus();
            return false;
        } else {
            textInputName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress() {
        if (TextUtils.isEmpty(editTextAddress.getText().toString().trim())) {
            textInputAddress.setError("Required Field!");
            textInputAddress.requestFocus();
            return false;
        } else {
            textInputAddress.setErrorEnabled(false);
        }
        return true;
    }

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

    private boolean validatePassword() {
        if (TextUtils.isEmpty(editTextPassword.getText().toString().trim())) {
            textInputPassword.setError("Password is required");
            textInputPassword.requestFocus();
            return false;
        }
        else {
            textInputPassword.setErrorEnabled(false);
        }
        return true;
    }

    public void userRegister(){
        if (NetUtils.isNetworkAvailable(RegisterActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            final String name = editTextName.getText().toString().trim();
            final String email = editTextEmail.getText().toString().trim();
            final String mobile = editTextMobile.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();
            final String address = editTextAddress.getText().toString().trim();

            fireAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        final FieldValue productCreatedDate = FieldValue.serverTimestamp();

                        Users users = new Users();
                        users.setUserId(fireReference.getId());
                        users.setUserUId(task.getResult().getUser().getUid());
                        users.setUserName(name);
                        users.setUserEmail(email);
                        users.setUserMobile(mobile);
                        users.setUserAddress(address);
                        users.setUserToken("Add Firebase Device Token");
                        users.setUserType(2);
                        users.setUserPassword(password);
                        users.setUserCreatedDate(productCreatedDate);
                        users.setUserUpdateDate(productCreatedDate);

                        fireReference = fireStore.collection("users").document(task.getResult().getUser().getUid());

                        fireReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: user Profile is created for "+ aVoid);
                                progressBar.setVisibility(View.GONE);

                                Gson gson = new Gson();
                                String json = gson.toJson(users);
                                SharedPrefsUtils.saveToPrefs(RegisterActivity.this, SharedPrefsUtils.USER_DETAIL, json);


                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        final FirebaseUser user = fireAuth.getCurrentUser();
                                        if (!task.isSuccessful()) {
                                            String token = task.getException().getMessage();
                                            Log.w("FCM TOKEN Failed", task.getException());
                                            fireStore.collection("users").document(user.getUid()).update("userToken",token);
                                        } else {
                                            String token = task.getResult().getToken();
                                            fireStore.collection("users").document(user.getUid()).update("userToken",token);
                                            Log.i("FCM TOKEN", token);
                                        }
                                    }
                                });


                                Object userDetial = SharedPrefsUtils.getFromPrefs(RegisterActivity.this, SharedPrefsUtils.USER_DETAIL, "");
                                Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);
                                if (obj.getUserId() != "" && obj.getUserEmail() != "" && obj.getUserType() == 1) {
                                    Intent intent = new Intent(RegisterActivity.this, HomeAdminActivity.class);
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                                        startActivity(intent);
                                    finish();
                                } else if (obj.getUserId() != "" && obj.getUserEmail() != "" && obj.getUserType() == 2) {
                                    Intent intent = new Intent(RegisterActivity.this, HomeCustomerActivity.class);
                                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);                                        startActivity(intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Utility.displayDialog(RegisterActivity.this, task.getException().getMessage(), false);
                    }
                }
            });

        } else {
            Utility.displayDialog(RegisterActivity.this, getString(R.string.common_no_internet), false);
        }
    }

}
