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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kolkatahaat.R;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

import java.io.IOException;
import java.util.UUID;

public class AdminUpdateSliderImageActivity extends AppCompatActivity {

    private Toolbar toolbar;

    public final String TAG = AdminUpdateSliderImageActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;

    private DocumentReference documentReference;
    private CollectionReference collectionReference;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    private ImageView mImageView;
    private ProgressBar progressBar;
    private ImageButton btnDelete;
    private Button btnChoose;
    private Button btnUpdateImg;

    private String mSliderImgItemId = "";
    private String mSliderImgURL = "";
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_slider_image);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        collectionReference = fireStore.collection("slider");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSliderImgItemId = bundle.getString("EXTRA_SLIDER_IMG_ITEM_ID");
            mSliderImgURL = bundle.getString("EXTRA_SLIDER_IMG_URL");
        }


        mImageView = findViewById(R.id.mImageView);
        progressBar = findViewById(R.id.progressBar);

        btnDelete = findViewById(R.id.btnDelete);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpdateImg = findViewById(R.id.btnUpdateImg);


        Glide.with(AdminUpdateSliderImageActivity.this).load(mSliderImgURL)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mImageView);


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpdateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chekcValidation()) {
                    return;
                } else if (chekcValidation()) {
                    addImage();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetUtils.isNetworkAvailable(AdminUpdateSliderImageActivity.this)) {
                    if (!TextUtils.isEmpty(mSliderImgItemId) && mSliderImgItemId != null) {
                        onClickDelete(mSliderImgItemId);
                    } else {
                        Utility.displayDialog(AdminUpdateSliderImageActivity.this, "Slider image not found", false);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Utility.displayDialog(AdminUpdateSliderImageActivity.this, getString(R.string.common_no_internet), false);
                }
            }
        });
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

    public boolean chekcValidation() {
        Utility.hideSoftKeyboard(AdminUpdateSliderImageActivity.this);
        if (bitmap == null) {
            Utility.displayDialog(AdminUpdateSliderImageActivity.this, "Please choose your product image", false);
            return false;
        } else {
            return true;
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void addImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(AdminUpdateSliderImageActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("slider/" + UUID.randomUUID().toString());
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
                    Toast.makeText(AdminUpdateSliderImageActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void uploadProduct(String UploadUrl) {
        if (NetUtils.isNetworkAvailable(AdminUpdateSliderImageActivity.this)) {
            progressBar.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mSliderImgItemId) && mSliderImgItemId != null) {
                documentReference = fireStore.collection("slider").document(mSliderImgItemId);

                if (UploadUrl != null && !UploadUrl.isEmpty() && UploadUrl != "") {
                    int index = UploadUrl.lastIndexOf('/');
                    String imgNm = UploadUrl.substring(index);

                    documentReference.update("imgName",imgNm, "imgUrl",UploadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            bitmap = null;
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "onSuccess: user Profile is created for fgjDq4TqJeHpzwNNzxCY " + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Utility.displayDialog(AdminUpdateSliderImageActivity.this, "Please try again", false);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                Utility.displayDialog(AdminUpdateSliderImageActivity.this, "Slider image not found", false);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            Utility.displayDialog(AdminUpdateSliderImageActivity.this, getString(R.string.common_no_internet), false);
        }
    }

    public void onClickDelete(String positionId) {
        StorageReference imageRef = storage.getReferenceFromUrl(mSliderImgURL);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: deleted file");
                collectionReference.document(positionId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onBackPressed();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.d(TAG, "onFailure: did not delete file");
                collectionReference.document(positionId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onBackPressed();
                    }
                });
            }
        });
    }
}
