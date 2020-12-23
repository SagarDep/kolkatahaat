package com.kolkatahaat.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.kolkatahaat.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    TextInputEditText editTextName;
    TextInputEditText editTextEmail;
    TextInputEditText editTextMobile;
    TextInputEditText editTextPassword;
    Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
