package com.kolkatahaat.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.R;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public final String TAG = RegisterActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private DocumentReference fireReference;

    private ImageView img_logo;

    private TextInputLayout textInputName;
    private TextInputEditText editTextName;

    private TextInputLayout textInputEmail;
    private TextInputEditText editTextEmail;

    private TextInputLayout textInputMobile;
    private TextInputEditText editTextMobile;

    private TextInputLayout textInputPassword;
    private TextInputEditText editTextPassword;
    private Button btnRegister;
    private TextView txtLogin;
    private TextView txtInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        fireReference = fireStore.collection("users").document();

        init();
    }

    public void init(){

        textInputName = findViewById(R.id.textInputName);
        editTextName = findViewById(R.id.editTextName);

        textInputEmail = findViewById(R.id.textInputEmail);
        editTextEmail = findViewById(R.id.editTextEmail);

        textInputMobile = findViewById(R.id.textInputMobile);
        editTextMobile = findViewById(R.id.editTextMobile);

        textInputPassword = findViewById(R.id.textInputPassword);
        editTextPassword = findViewById(R.id.editTextPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
        txtInfo = findViewById(R.id.txtInfo);
    }


    public void chekcValidation(){
        Utility.hideSoftKeyboard(RegisterActivity.this);
        if (TextUtils.isEmpty(editTextName.getText().toString())) {
            textInputName.setError(getResources().getString(R.string.str_err_msg_user_user_name));
            //return true;
        } else if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
            textInputEmail.setError(getResources().getString(R.string.str_err_msg_user_email));
            //return true;
        } else if (TextUtils.isEmpty(editTextMobile.getText().toString())) {
            textInputMobile.setError(getResources().getString(R.string.str_err_msg_user_user_mobile));
            //return true;
        } else if (TextUtils.isEmpty(editTextPassword.getText().toString())) {
            textInputPassword.setError(getResources().getString(R.string.str_err_msg_user_password));
            //return true;
        } else {
            if (NetUtils.isNetworkAvailable(RegisterActivity.this)) {
               final String name = editTextName.getText().toString().trim();
               final String email = editTextEmail.getText().toString().trim();
               final String mobile = editTextMobile.getText().toString().trim();
               final String password = editTextPassword.getText().toString().trim();

                fireAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Users users = new Users();
                            users.setUserId(task.getResult().getUser().getUid());
                            users.setUserName(name);
                            users.setUserEmail(email);
                            users.setUserMobile(mobile);
                            users.setUserPassword(password);
                            users.setUserToken("Add Firebase Device Token");

                            fireReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ aVoid);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                Utility.displayDialog(RegisterActivity.this, getString(R.string.common_no_internet), false);
            }
        }
    }
}
