package com.kolkatahaat.view.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kolkatahaat.R;
import com.kolkatahaat.interfaces.AdminQuantityDialogListener;
import com.kolkatahaat.model.QuantityPrice;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;
import com.kolkatahaat.view.admin.fragments.AddQuantityDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminAddProductActivity extends AppCompatActivity {// implements AdminQuantityDialogListener {

    public final String TAG = AdminAddProductActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private DocumentReference fireReference;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    private String mAdminProductId = "";
    private boolean mAdminProductEdit = false;

    private Toolbar toolbar;
    private CircleImageView imgProduct;
    private Button btnChoose;

    private Spinner spinnerCategory;
    private String strCategoryName;

    private TextInputLayout textInputItemName;
    private TextInputEditText editTextItemName;

    //private TextView txtAddQuantityPrice;
    //private ChipGroup chipGroup;

    private TextInputLayout textInputItemQuantity;
    private TextInputEditText editTextItemQuantity;

    private TextInputLayout textInputItemPrice;
    private TextInputEditText editTextItemPrice;

    private TextInputLayout textInputItemDeliveryChrg;
    private TextInputEditText editTextItemDeliveryChrg;

    private Button btnAppProduct;

    //private ArrayList<QuantityPrice> priceArrayList;

    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        fireReference = fireStore.collection("products").document();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_ADMIN_PRODUCT_ID")) {
            mAdminProductId = getIntent().getStringExtra("EXTRA_ADMIN_PRODUCT_ID");
            mAdminProductEdit = getIntent().getBooleanExtra("EXTRA_ADMIN_PRODUCT_EDIT", false);
        }

        init();

        if(mAdminProductEdit){

            getProductDetials();
        } else {
            setupAutoCompleteView();
        }
    }


    public void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgProduct = findViewById(R.id.imgProduct);
        btnChoose = findViewById(R.id.btnChoose);

        spinnerCategory = findViewById(R.id.spinnerCategory);

        //txtAddQuantityPrice = findViewById(R.id.txtAddQuantityPrice);
        //chipGroup = findViewById(R.id.chipGroup);

        textInputItemQuantity = findViewById(R.id.textInputItemQuantity);
        editTextItemQuantity= findViewById(R.id.editTextItemQuantity);

        textInputItemPrice= findViewById(R.id.textInputItemPrice);
        editTextItemPrice= findViewById(R.id.editTextItemPrice);

        textInputItemName = findViewById(R.id.textInputItemName);
        editTextItemName = findViewById(R.id.editTextItemName);


        textInputItemDeliveryChrg = findViewById(R.id.textInputItemDeliveryChrg);
        editTextItemDeliveryChrg = findViewById(R.id.editTextItemDeliveryChrg);

        btnAppProduct = findViewById(R.id.btnAppProduct);

        //priceArrayList = new ArrayList<>();

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        btnAppProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateItemName() && !validateQuantity() && !validatePrice() && !validateDeliveryChr() && !chekcValidation()) {
                    return;
                }
                else if(validateItemName() && validateQuantity() && validatePrice() && validateDeliveryChr() && chekcValidation()) {
                    uploadImage();
                }
            }
        });

        /*txtAddQuantityPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuantityDialog dialog = new AddQuantityDialog();
                dialog.show(getSupportFragmentManager(), "TransparentDialogFragment");
            }
        });*/
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

    private void setupAutoCompleteView() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.product_category, R.layout.list_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(AdminAddProductActivity.this,parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorGray));
                    strCategoryName = null;
                } else {
                    strCategoryName = parent.getItemAtPosition(position).toString().trim();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                strCategoryName = null;
            }
        });
    }


    /*@Override
    public void onDialogPositive(String quantityName, float price) {
        if (quantityName != null && quantityName.length() != 0 && price != 0.0f && price != 0) {
            chipGroup.removeAllViews();

            QuantityPrice quantityPrice = new QuantityPrice();
            quantityPrice.setQuantityName(quantityName);
            quantityPrice.setPrice(price);
            priceArrayList.add(quantityPrice);



            setTag(priceArrayList);
        } else {
            Toast.makeText(this, "Please enter valid QuantityName and price ", Toast.LENGTH_SHORT).show();
        }
    }*/

    /*@Override
    public void onDialogNegative(Object object) {
        Toast.makeText(this, "Custom negative Dialog " + getString(R.string.app_name), Toast.LENGTH_SHORT).show();

    }*/


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgProduct.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("products/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    final Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e(TAG, "onSuccess: uri= " + uri.toString());
                            uploadProduct(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AdminAddProductActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }


    /*public void setTag(final ArrayList<QuantityPrice> tagList) {
        for (int index = 0; index < tagList.size(); index++) {
            final String tagName = tagList.get(index).getQuantityName();
            final Chip chip = new Chip(this);

            final String tagDisplay = tagList.get(index).getQuantityName() + " , " + tagList.get(index).getPrice();

            chip.setText(tagDisplay);
            chip.setCloseIconResource(android.R.drawable.ic_menu_close_clear_cancel);
            chip.setChipBackgroundColorResource(R.color.colorAccent);
            chip.setCloseIconVisible(true);
            chip.setTextColor(getResources().getColor(R.color.colorBlack));

            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tagList.remove(tagName);
                    chipGroup.removeView(chip);

                    QuantityPrice userToRemove = null;
                    for (QuantityPrice usr : priceArrayList) {
                        if (usr.getQuantityName().equals(tagName)) {
                            userToRemove = usr;
                            break;
                        }
                    }
                    priceArrayList.remove(userToRemove);

                }
            });
            chipGroup.addView(chip);
        }
        chipGroup.invalidate();
    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean validateItemName() {
        if (TextUtils.isEmpty(editTextItemName.getText().toString().trim())) {
            textInputItemName.setError(getResources().getString(R.string.str_err_msg_user_user_name));
            textInputItemName.requestFocus();
            return false;
        } else {
            textInputItemName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDeliveryChr() {
        if (TextUtils.isEmpty(editTextItemDeliveryChrg.getText().toString().trim())) {
            textInputItemDeliveryChrg.setError(getResources().getString(R.string.str_err_msg_user_user_mobile));
            textInputItemDeliveryChrg.requestFocus();
            return false;
        } else {
            textInputItemDeliveryChrg.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateQuantity() {
        if (TextUtils.isEmpty(editTextItemQuantity.getText().toString().trim())) {
            textInputItemQuantity.setError("Please Enter Valid Quantity Name!");
            textInputItemQuantity.requestFocus();
            return false;
        } else {
            textInputItemQuantity.setErrorEnabled(false);
        }
        return true;
    }


    private boolean validatePrice() {
        if (TextUtils.isEmpty(editTextItemPrice.getText().toString().trim())) {
            textInputItemPrice.setError("Please Enter Valid Price!");
            textInputItemPrice.requestFocus();
            return false;
        } else {
            textInputItemPrice.setErrorEnabled(false);
        }
        return true;
    }

    public boolean chekcValidation() {
        Utility.hideSoftKeyboard(AdminAddProductActivity.this);
        if (bitmap == null) {
            Utility.displayDialog(AdminAddProductActivity.this, "Please choose your product image", false);
            return false;
        }/* else if (priceArrayList == null || priceArrayList.size() == 0 || priceArrayList.isEmpty()) {
            Utility.displayDialog(AdminAddProductActivity.this, "Please add your product package size and price", false);
            return false;
        }*/ else if (spinnerCategory.getSelectedItem() == "Select" || spinnerCategory.getSelectedItem().equals("Select") || strCategoryName == null || strCategoryName.isEmpty()) {
            Utility.displayDialog(AdminAddProductActivity.this, "Please select your product category", false);
            return false;
        } else {
            return true;
        }
    }

    private void uploadProduct(String UploadUrl) {

        if (NetUtils.isNetworkAvailable(AdminAddProductActivity.this)) {

            if(UploadUrl != null && !UploadUrl.isEmpty() && UploadUrl != "") {
                final String productId = fireReference.getId();
                //final String productImg = UploadUrl;
                final String productName = editTextItemName.getText().toString().trim();

                final String productPacking = editTextItemQuantity.getText().toString().trim();
                final String productPrice = editTextItemPrice.getText().toString().trim();

                final String productDeliveryChange = editTextItemDeliveryChrg.getText().toString().trim();
                final FieldValue productCreatedDate = FieldValue.serverTimestamp();

                Product product = new Product();
                product.setProductId(productId);
                product.setProductImg(UploadUrl);
                product.setProductCategory(strCategoryName);
                product.setProductName(productName);

                /*product.setProductPacking("");
                product.setProductPrice("");*/

                product.setProductPacking(productPacking);
                product.setProductPrice(Float.parseFloat(productPrice));

                product.setProductDeliveryChange(productDeliveryChange);
                product.setProductCreatedDate(productCreatedDate);

                fireReference.set(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: user Profile is created for " + aVoid);
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
            } else {
                Utility.displayDialog(AdminAddProductActivity.this, "Please try again", false);
            }

        } else {
            Utility.displayDialog(AdminAddProductActivity.this, getString(R.string.common_no_internet), false);
        }
    }




    private void getProductDetials() {
        fireReference = fireStore.collection("products").document(mAdminProductId);
        fireReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Product product = document.toObject(Product.class);

                        editTextItemName.setText(product.getProductName());
                        editTextItemQuantity.setText(String.valueOf(product.getProductQuantity()));
                        editTextItemPrice.setText(String.valueOf(product.getProductPrice()));
                        editTextItemDeliveryChrg.setText(String.valueOf(product.getProductDeliveryChange()));

                        RequestOptions options = new RequestOptions()
                                .centerCrop()
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round);
                        Glide.with(AdminAddProductActivity.this).asBitmap().load(product.getProductImg()).apply(options).into(imgProduct);


                        String compareValue = product.getProductCategory();
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AdminAddProductActivity.this, R.array.product_category, R.layout.list_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                        if (compareValue != null) {
                            int spinnerPosition = adapter.getPosition(compareValue);
                            spinnerCategory.setSelection(spinnerPosition);
                        }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
