package com.kolkatahaat.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.kolkatahaat.R;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

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

    private TextInputLayout textInputPassword;
    private TextInputEditText editTextPassword;
    private Button btnUserUpdate;

    private Users usersInfo = null;

    public EditProfileFragment() {
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        textInputPassword = view.findViewById(R.id.textInputPassword);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        btnUserUpdate = view.findViewById(R.id.btnUserUpdate);

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
        } else if (editTextMobile.getText().toString().trim().length() < 6) {
            textInputMobile.setError("Mobile can't be less than 6 digit");
            textInputMobile.requestFocus();
            return false;
        } else {
            textInputMobile.setErrorEnabled(false);
        }
        return true;
    }


    private void getUserData() {
        if (fireAuth.getCurrentUser() != null) {

            DocumentReference documentReference = fireStore.collection("users")
                    .document(fireAuth.getCurrentUser().getUid());

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
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
        }
    }


    public void userUpdate() {
        if (NetUtils.isNetworkAvailable(getActivity())) {

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
                                Toast.makeText(getActivity(), "Update Successful", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();

                            }
                        });
            } else {
                Utility.displayDialog(getActivity(), "Please login! After you complete your profile", false);
            }
        } else {
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }
}
