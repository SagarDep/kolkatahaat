package com.kolkatahaat.view.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kolkatahaat.R;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

public class AdminEditCustomerProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    public final String TAG = AdminEditCustomerProfileActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;

    private ImageView img_logo;

    private TextInputLayout textInputName;
    private TextInputEditText editTextName;

    private TextInputLayout textInputAddress;
    private TextInputEditText editTextAddress;

    private TextInputLayout textInputEmail;
    private TextInputEditText editTextEmail;

    private TextInputLayout textInputMobile;
    private TextInputEditText editTextMobile;

    private Button btnUserUpdate;
    private ProgressBar progressBar;

    private Users usersInfo = null;
    private String mEditUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_customer_profile);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mEditUserId = bundle.getString("EXTRA_EDIT_USER_ID");
        }

        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInputName = findViewById(R.id.textInputName);
        editTextName = findViewById(R.id.editTextName);

        textInputAddress = findViewById(R.id.textInputAddress);
        editTextAddress = findViewById(R.id.editTextAddress);

        textInputEmail = findViewById(R.id.textInputEmail);
        editTextEmail = findViewById(R.id.editTextEmail);

        textInputMobile = findViewById(R.id.textInputMobile);
        editTextMobile = findViewById(R.id.editTextMobile);

        btnUserUpdate = findViewById(R.id.btnUserUpdate);
        progressBar = findViewById(R.id.progressBar);

        if(!TextUtils.isEmpty(mEditUserId) && mEditUserId != null && mEditUserId != "") {
            Utility.hideSoftKeyboard(AdminEditCustomerProfileActivity.this);
            getUserData(mEditUserId);
        }

        btnUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUserName() && !validateAddress() && !validateEmail() && !validateMobile()) {
                    return;
                } else if (validateUserName() && validateAddress() && validateEmail() && validateMobile()) {
                    if(!TextUtils.isEmpty(mEditUserId) && mEditUserId != null && mEditUserId != "") {
                        userUpdate(mEditUserId);
                    }
                }
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

    private boolean validateMobile() {
        if (TextUtils.isEmpty(editTextMobile.getText().toString().trim())) {
            textInputMobile.setError("Required Field!");
            textInputMobile.requestFocus();
            return false;
        } else if (editTextMobile.getText().toString().trim().length() > 10) {
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set title of this fragment
        if (AdminEditCustomerProfileActivity.this != null) {
            setTitle(getResources().getString(R.string.str_title_profile));
        }
    }



    private void getUserData(String editUserId) {
        progressBar.setVisibility(View.VISIBLE);
        if (NetUtils.isNetworkAvailable(AdminEditCustomerProfileActivity.this)) {
            if (fireAuth.getCurrentUser() != null) {
                DocumentReference documentReference = fireStore.collection("users").document(editUserId);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            DocumentSnapshot snapshot = task.getResult();
                            usersInfo = snapshot.toObject(Users.class);
                            assert snapshot != null;
                            if (snapshot.exists()) {

                                editTextName.setText(usersInfo.getUserName());
                                editTextEmail.setText(usersInfo.getUserEmail());
                                editTextMobile.setText(usersInfo.getUserMobile());
                                editTextAddress.setText(usersInfo.getUserAddress());
                                Utility.hideSoftKeyboard(AdminEditCustomerProfileActivity.this);
                            }
                        }
                    }
                });
            } else {
                Utility.displayDialog(AdminEditCustomerProfileActivity.this, getString(R.string.common_no_internet), false);
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void userUpdate(String editUserId) {
        if (NetUtils.isNetworkAvailable(AdminEditCustomerProfileActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            final String name = editTextName.getText().toString().trim();
            final String email = editTextEmail.getText().toString().trim();
            final String mobile = editTextMobile.getText().toString().trim();
            final String address = editTextAddress.getText().toString().trim();

            if (fireAuth.getCurrentUser() != null) {
                final FieldValue updateCreatedDate = FieldValue.serverTimestamp();

                Users users = new Users();

                users.setUserId(usersInfo.getUserId());
                users.setUserUId(usersInfo.getUserUId());
                users.setUserName(name);
                users.setUserEmail(email);
                users.setUserMobile(mobile);
                users.setUserAddress(address);
                users.setUserToken("Add Firebase Device Token");
                users.setUserType(usersInfo.getUserType());
                users.setUserCreatedDate(usersInfo.getUserCreatedDate());
                users.setUserUpdateDate(updateCreatedDate);

                fireStore.collection("users").document(editUserId).set(users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AdminEditCustomerProfileActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminEditCustomerProfileActivity.this, "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else {
                progressBar.setVisibility(View.GONE);
                Utility.displayDialog(AdminEditCustomerProfileActivity.this, "Please login! After you complete your profile", false);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Utility.displayDialog(AdminEditCustomerProfileActivity.this, getString(R.string.common_no_internet), false);
        }
    }
}
