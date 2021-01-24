package com.kolkatahaat.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kolkatahaat.R;
import com.kolkatahaat.model.AboutUs;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

public class AboutUsFragment extends Fragment {

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private FirebaseUser currentUser;
    private Users users;

    private ProgressDialog progressDialog;

    private LinearLayout sub_wrapper;
    private TextView txtAboutDetails;
    private TextView txtAboutPhone;
    private TextView txtAboutWeb;
    private TextView txtAboutEmail;

    private LinearLayout sub_admin_wrapper;
    private TextInputLayout textAboutDetails;
    private TextInputEditText editAboutDetails;
    private TextInputLayout textInputPhone;
    private TextInputEditText editTextPhone;
    private TextInputLayout textInputWeb;
    private TextInputEditText editTextWeb;
    private TextInputLayout textInputEmail;
    private TextInputEditText editTextEmail;
    private Button btnUpdate;

    public AboutUsFragment() {
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        currentUser = fireAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        init(view);

        progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_ProgressDialog_Theme);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        fireStore.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    users = documentSnapshot.toObject(Users.class);
                    progressDialog.dismiss();

                    getAboutInfo();

                    if (users != null) {
                        if (users.getUserType() == 1) {
                            sub_wrapper.setVisibility(View.GONE);
                            sub_admin_wrapper.setVisibility(View.VISIBLE);
                        } else {
                            sub_wrapper.setVisibility(View.VISIBLE);
                            sub_admin_wrapper.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        return view;
    }

    private void init(View view) {
        sub_wrapper = view.findViewById(R.id.sub_wrapper);
        txtAboutDetails = view.findViewById(R.id.txtAboutDetails);
        txtAboutPhone = view.findViewById(R.id.txtAboutPhone);
        txtAboutWeb = view.findViewById(R.id.txtAboutWeb);
        txtAboutEmail = view.findViewById(R.id.txtAboutEmail);

        sub_admin_wrapper = view.findViewById(R.id.sub_admin_wrapper);
        textAboutDetails = view.findViewById(R.id.textAboutDetails);
        editAboutDetails = view.findViewById(R.id.editAboutDetails);
        textInputPhone = view.findViewById(R.id.textInputPhone);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        textInputWeb = view.findViewById(R.id.textInputWeb);
        editTextWeb = view.findViewById(R.id.editTextWeb);
        textInputEmail = view.findViewById(R.id.textInputEmail);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateAboutDetails() && !validatePhone() && !validateWeb() && !validateEmail()) {
                    return;
                } else if (validateAboutDetails() && validatePhone() && validateWeb() && validateEmail()) {
                    updateAboutInfo();
                }
            }
        });
    }

    private void getAboutInfo() {
        if (NetUtils.isNetworkAvailable(getActivity())) {
            DocumentReference documentReference = fireStore.collection("aboutus").document("aboutinfo");
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot snapshot = task.getResult();
                        AboutUs aboutUs = snapshot.toObject(AboutUs.class);
                        assert snapshot != null;
                        if (snapshot.exists()) {

                            if (users != null) {
                                if (users.getUserType() == 1) {
                                    editAboutDetails.setText(aboutUs.getAboutDetails());
                                    editTextPhone.setText(aboutUs.getAboutPhone());
                                    editTextWeb.setText(aboutUs.getAboutWeb());
                                    editTextEmail.setText(aboutUs.getAboutEmail());
                                } else {
                                    txtAboutDetails.setText(aboutUs.getAboutDetails());
                                    txtAboutPhone.setText(aboutUs.getAboutPhone());
                                    txtAboutWeb.setText(aboutUs.getAboutWeb());
                                    txtAboutEmail.setText(aboutUs.getAboutEmail());
                                }
                            }
                        }
                    }
                }
            });
        } else {
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }

    private void updateAboutInfo() {
        if (NetUtils.isNetworkAvailable(getActivity())) {
            final FieldValue createdDate = FieldValue.serverTimestamp();
            final String aboutDetails = editAboutDetails.getText().toString().trim();
            final String phone = editTextPhone.getText().toString().trim();
            final String web = editTextWeb.getText().toString().trim();
            final String email = editTextEmail.getText().toString().trim();

            AboutUs city = new AboutUs();
            city.setAboutDetails(aboutDetails);
            city.setAboutPhone(phone);
            city.setAboutEmail(web);
            city.setAboutWeb(email);
            city.setBillCreatedDate(createdDate);

            fireStore.collection("aboutus").document("aboutinfo")
                    .set(city)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(getActivity(), "successfully update ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error writing document", e);
                            Toast.makeText(getActivity(), "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }


    private boolean validateAboutDetails() {
        if (TextUtils.isEmpty(editTextWeb.getText().toString().trim())) {
            textInputWeb.setError("Required Field!");
            textInputWeb.requestFocus();
            return false;
        } else {
            textInputWeb.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validatePhone() {
        if (TextUtils.isEmpty(editTextPhone.getText().toString().trim())) {
            textInputPhone.setError("Required Field!");
            textInputPhone.requestFocus();
            return false;
        } else if (editTextPhone.getText().toString().trim().length() > 10) {
            textInputPhone.setError("Mobile can't be less than 10 digit");
            textInputPhone.requestFocus();
            return false;
        } else if (editTextPhone.getText().toString().trim().length() < 10) {
            textInputPhone.setError("Mobile can't be less than 10 digit");
            textInputPhone.requestFocus();
            return false;
        } else {
            textInputPhone.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateWeb() {
        if (TextUtils.isEmpty(editTextWeb.getText().toString().trim())) {
            textInputWeb.setError("Required Field!");
            textInputWeb.requestFocus();
            return false;
        } else {
            textInputWeb.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateEmail() {
        if (TextUtils.isEmpty(editTextEmail.getText().toString().trim())) {
            textInputEmail.setError("Invalid Email address, ex: kolkatahaat@gmail.com");
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



    @Override
    public void onResume() {
        super.onResume();
        //Set title of this fragment
        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.menu_about_us));
        }
    }
}
