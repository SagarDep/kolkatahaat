package com.kolkatahaat.view.admin.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.AdminSliderImageAdapter;
import com.kolkatahaat.interfaces.OnStartDragListener;
import com.kolkatahaat.model.SliderImgItem;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.EditItemTouchHelperCallback;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class AdminAddOtherContentFragment extends Fragment implements OnStartDragListener {

    private static final String TAG = AdminAddOtherContentFragment.class.getSimpleName();

    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;
    private CollectionReference collectionReference;
    private DocumentReference collectionReferenceDelivery;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    private RecyclerView mRecyclerView;
    private AdminSliderImageAdapter imageAdapter;

    private TextInputLayout textInputItemDeliveryChrg;
    private TextInputEditText editTextItemDeliveryChrg;

    private TextView empty_view;
    private ProgressBar progressBar;
    private CircleImageView imgProduct;
    private Button btnChoose;
    private Button btnAddImg;
    private Button btnDeliveryCharge;
    private Bitmap bitmap;

    private List<SliderImgItem> messages;
    ItemTouchHelper mItemTouchHelper;

    public AdminAddOtherContentFragment() {
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        collectionReference = fireStore.collection("slider");
        collectionReferenceDelivery = fireStore.collection("delivery_charge").document("deliveryCharge");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        messages = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_add_other_content, container, false);

        empty_view = view.findViewById(R.id.empty_view);
        btnChoose = view.findViewById(R.id.btnChoose);
        btnAddImg = view.findViewById(R.id.btnAddImg);
        imgProduct = view.findViewById(R.id.imgProduct);
        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
        btnDeliveryCharge = view.findViewById(R.id.btnDeliveryCharge);

        textInputItemDeliveryChrg = view.findViewById(R.id.textInputItemDeliveryChrg);
        editTextItemDeliveryChrg = view.findViewById(R.id.editTextItemDeliveryChrg);

        getAllImage();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        imageAdapter = new AdminSliderImageAdapter(getActivity(), messages, this);
        ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(imageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(imageAdapter);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chekcValidation()) {
                    return;
                } else if (chekcValidation()) {
                    uploadImage();
                }
            }
        });

        btnDeliveryCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateDeliveryChr()) {
                    return;
                } else if (validateDeliveryChr()) {
                    int deliveryChag = Integer.parseInt(editTextItemDeliveryChrg.getText().toString().trim());
                    updateDeliveryChrarge(deliveryChag);
                }
            }
        });
        getDeliveryChrarge();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        //Set title of this fragment
        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.menu_other_content));
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public boolean chekcValidation() {
        Utility.hideSoftKeyboard(getActivity());
        if (bitmap == null) {
            Utility.displayDialog(getActivity(), "Please choose your product image", false);
            return false;
        } else {
            return true;
        }
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imgProduct.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                    Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (NetUtils.isNetworkAvailable(getActivity())) {
            if (UploadUrl != null && !UploadUrl.isEmpty() && UploadUrl != "") {
                final FieldValue createdDate = FieldValue.serverTimestamp();
                int index = UploadUrl.lastIndexOf('/');
                String imgNm = UploadUrl.substring(index);

                FirebaseUser user = fireAuth.getCurrentUser();

                SliderImgItem sliderImgItem = new SliderImgItem();
                sliderImgItem.setUserUId(user.getUid());
                sliderImgItem.setDocId(collectionReference.getId());
                sliderImgItem.setImgUrl(UploadUrl);
                sliderImgItem.setImgName(imgNm);
                sliderImgItem.setCurrentDate(createdDate);

                collectionReference.add(sliderImgItem).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        bitmap = null;
                        Log.d(TAG, "onSuccess: user Profile is created for ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
            } else {
                Utility.displayDialog(getActivity(), "Please try again", false);
            }
        } else {
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }


    private void getAllImage() {
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }
                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(SliderImgItem.class));
                            SliderImgItem note = dc.getDocument().toObject(SliderImgItem.class);
                            messages.add(note);
                            imageAdapter.notifyDataSetChanged();

                            if (messages.size() != 0 && messages != null) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                empty_view.setVisibility(View.GONE);
                            } else {
                                mRecyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                empty_view.setVisibility(View.VISIBLE);
                            }
                            break;
                        case MODIFIED:
                            String key = dc.getDocument().getId();
                            Log.d("TAG", "Modified Msg: " + key);

                            if (messages.size() != 0) {
                                for (int i = 0; i < messages.size(); i++) {
                                    if (messages.get(i).getUserUId().equals(dc.getDocument().getId())) {

                                        SliderImgItem billModel = dc.getDocument().toObject(SliderImgItem.class);
                                        messages.set(i, billModel);
                                        imageAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                            if (messages.size() != 0 && messages != null) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                empty_view.setVisibility(View.GONE);
                            } else {
                                mRecyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                empty_view.setVisibility(View.VISIBLE);
                            }
                            break;

                        case REMOVED:
                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(SliderImgItem.class));

                            if (messages.size() != 0) {
                                for (int i = 0; i < messages.size(); i++) {
                                    if (messages.get(i).getUserUId().equals(dc.getDocument().getId())) {

                                        //BillModel billModel = dc.getDocument().toObject(BillModel.class);
                                        messages.remove(i);
                                        imageAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                            if (messages.size() != 0 && messages != null) {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                empty_view.setVisibility(View.GONE);
                            } else {
                                mRecyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                empty_view.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                }
            }
        });
    }


    private void updateDeliveryChrarge(int deliveryChag) {
        Map<String, Integer> product = new HashMap<>();
        product.put("deliveryCharges", deliveryChag);
        collectionReferenceDelivery.set(product);
    }

    private void getDeliveryChrarge(){
        collectionReferenceDelivery.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        int data = Integer.valueOf(document.getData().get("deliveryCharges").toString());
                        editTextItemDeliveryChrg.setText(""+data);
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
