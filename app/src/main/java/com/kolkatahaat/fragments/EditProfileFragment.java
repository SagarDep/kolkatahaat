package com.kolkatahaat.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.gson.Gson;
import com.kolkatahaat.R;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.SharedPrefsUtils;
import com.kolkatahaat.utills.Utility;
import com.kolkatahaat.view.LoginActivity;

public class EditProfileFragment extends Fragment {

    public final String TAG = EditProfileFragment.this.getClass().getSimpleName();

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

    public EditProfileFragment() {
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        init(view);
        return view;
    }

    public void init(View view) {
        textInputName = view.findViewById(R.id.textInputName);
        editTextName = view.findViewById(R.id.editTextName);

        textInputAddress = view.findViewById(R.id.textInputAddress);
        editTextAddress = view.findViewById(R.id.editTextAddress);

        textInputEmail = view.findViewById(R.id.textInputEmail);
        editTextEmail = view.findViewById(R.id.editTextEmail);

        textInputMobile = view.findViewById(R.id.textInputMobile);
        editTextMobile = view.findViewById(R.id.editTextMobile);

        btnUserUpdate = view.findViewById(R.id.btnUserUpdate);
        progressBar = view.findViewById(R.id.progressBar);

        Gson gson = new Gson();
        Object userDetial = SharedPrefsUtils.getFromPrefs(getActivity(), SharedPrefsUtils.USER_DETAIL, "");
        Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);
        if(obj.getUserType() == 1){
            editTextMobile.setEnabled(true);
        } else {
            editTextMobile.setEnabled(false);
        }

        getUserData();

        btnUserUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUserName() && !validateAddress() && !validateEmail() && !validateMobile()) {
                    return;
                } else if (validateUserName() && validateAddress() && validateEmail() && validateMobile()) {
                    userUpdate();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set title of this fragment
        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.str_title_profile));
        }
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


    private void getUserData() {
        Utility.hideSoftKeyboard(getActivity());
        progressBar.setVisibility(View.VISIBLE);
        if (fireAuth.getCurrentUser() != null) {
            DocumentReference documentReference = fireStore.collection("users")
                    .document(fireAuth.getCurrentUser().getUid());
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
                        }
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    public void userUpdate() {
        if (NetUtils.isNetworkAvailable(getActivity())) {
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

                fireStore.collection("users").document(fireAuth.getCurrentUser().getUid()).set(users)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Update Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            } else {
                progressBar.setVisibility(View.GONE);
                Utility.displayDialog(getActivity(), "Please login! After you complete your profile", false);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }
}
