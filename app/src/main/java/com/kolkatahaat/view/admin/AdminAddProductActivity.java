package com.kolkatahaat.view.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kolkatahaat.R;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;
import com.kolkatahaat.view.RegisterActivity;

public class AdminAddProductActivity extends AppCompatActivity {

    public final String TAG = AdminAddProductActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private DocumentReference fireReference;

    private ImageView imgProduct;
    private Button btnChoose;

    private TextInputLayout textInputItemCategory;
    private AppCompatAutoCompleteTextView autoCompleteCategory;

    private TextInputLayout textInputItemName;
    private TextInputEditText editTextItemName;

    private TextInputLayout textInputItemPacking;
    private AppCompatAutoCompleteTextView autoCompletePacking;

    private TextInputLayout textInputItemPrice;
    private TextInputEditText editTextItemPrice;

    private TextInputLayout textInputItemDeliveryChrg;
    private TextInputEditText editTextItemDeliveryChrg;

    private Button btnAppProduct;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        fireReference = fireStore.collection("products").document();

        init();
    }

    public void init() {
        imgProduct = findViewById(R.id.imgProduct);
        btnChoose = findViewById(R.id.btnChoose);

        textInputItemCategory = findViewById(R.id.textInputItemCategory);
        autoCompleteCategory = findViewById(R.id.autoCompleteCategory);

        textInputItemName = findViewById(R.id.textInputItemName);
        editTextItemName = findViewById(R.id.editTextItemName);

        textInputItemPacking = findViewById(R.id.textInputItemPacking);
        autoCompletePacking = findViewById(R.id.autoCompletePacking);

        textInputItemPrice = findViewById(R.id.textInputItemPrice);
        editTextItemPrice = findViewById(R.id.editTextItemPrice);

        textInputItemDeliveryChrg = findViewById(R.id.textInputItemDeliveryChrg);
        editTextItemDeliveryChrg = findViewById(R.id.editTextItemDeliveryChrg);

        btnAppProduct = findViewById(R.id.btnAppProduct);
    }


    public void chekcValidation() {
        Utility.hideSoftKeyboard(AdminAddProductActivity.this);
        if (TextUtils.isEmpty(editTextItemName.getText().toString())) {
            textInputItemName.setError(getResources().getString(R.string.str_err_msg_user_user_name));
            //return true;
        } else if (TextUtils.isEmpty(editTextItemPrice.getText().toString())) {
            textInputItemPrice.setError(getResources().getString(R.string.str_err_msg_user_email));
            //return true;
        } else if (TextUtils.isEmpty(editTextItemDeliveryChrg.getText().toString())) {
            textInputItemDeliveryChrg.setError(getResources().getString(R.string.str_err_msg_user_user_mobile));
            //return true;
        } else {
            if (NetUtils.isNetworkAvailable(AdminAddProductActivity.this)) {
               /* final String productId = editTextName.getText().toString().trim();
                final String productImg = editTextEmail.getText().toString().trim();
                final String productName = editTextItemName.getText().toString().trim();
                final String productPacking = editTextPassword.getText().toString().trim();
                final String productPrice = editTextItemPrice.getText().toString().trim();
                final String productDeliveryChange = editTextItemDeliveryChrg.getText().toString().trim();*/
                final FieldValue productCreatedDate = FieldValue.serverTimestamp();

                Product product = new Product();
                product.setProductId(fireReference.getId());
                product.setProductImg("");
                product.setProductCategory("");
                product.setProductName("");
                product.setProductPacking("");
                product.setProductPrice("");
                product.setProductDeliveryChange("Add Firebase Device Token");
                product.setProductCreatedDate(productCreatedDate);

                fireReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                Utility.displayDialog(AdminAddProductActivity.this, getString(R.string.common_no_internet), false);
            }
        }
    }
}
