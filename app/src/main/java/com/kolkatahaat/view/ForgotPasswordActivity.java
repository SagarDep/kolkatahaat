package com.kolkatahaat.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kolkatahaat.R;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

public class ForgotPasswordActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ImageView img_logo;
    TextInputLayout textInputEmail;
    TextInputEditText editTextEmail;

    LinearLayout llSendEmail;
    ProgressBar progressBar;
    TextView state;
    Button btnSendEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        init();
    }

    private void init() {
        img_logo = findViewById(R.id.img_logo);
        textInputEmail = findViewById(R.id.textInputEmail);
        editTextEmail = findViewById(R.id.editTextEmail);

        llSendEmail = findViewById(R.id.llSendEmail);
        progressBar = findViewById(R.id.progressBar);
        state = findViewById(R.id.state);
        btnSendEmail = findViewById(R.id.btnSendEmail);

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail()) {
                    return;
                }
                else if(validateEmail()) {
                    state.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    sendEmailVerify(editTextEmail.getText().toString().trim());
                }

            }
        });
    }

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

    private void sendEmailVerify(String email){
        if (NetUtils.isNetworkAvailable(ForgotPasswordActivity.this)) {
            fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        state.setText("Please check your email");
                        Toast.makeText(ForgotPasswordActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ForgotPasswordActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Utility.displayDialog(ForgotPasswordActivity.this, getString(R.string.common_no_internet), false);
        }
    }
}
