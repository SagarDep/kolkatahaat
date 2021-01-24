package com.kolkatahaat.view.customer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kolkatahaat.BuildConfig;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.ProductCartAdapter;
import com.kolkatahaat.interfaces.RecyclerViewRemoveClickListener;
import com.kolkatahaat.model.BillItem;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.service.FirebaseIDService;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.SharedPrefsUtils;
import com.kolkatahaat.utills.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductCartDetailsActivity extends AppCompatActivity {

    public final String TAG = ProductCartDetailsActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private DocumentReference fireReference;
    private CollectionReference collectReference;

    public Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView empty_view;
    private TextView txtTotalBill;
    private Button btnPurchase;
    private ProductCartAdapter mAdapter;
    private List<OrdersItem> itemList;

    private List<Users> adminItemList;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart_details);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();

         //clCsEidfQyiqjdEeH8ecZs:APA91bEnNvYSJ1DZkGTc_RaV2aXoZSSh08MMEcREQ6LpTviWluQPWhUsQXx7QlYJ561JmWeGwsjuaJphlzK60Tu-3-34rRH_0xgGV0xhUWem9c0F4C8uCMHks2ckt1B4vezaXCBancfO
    }

    private void init() {
        itemList = new ArrayList<>();
        adminItemList = new ArrayList<>();
        recyclerView = findViewById(R.id.mRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        empty_view = findViewById(R.id.empty_view);

        txtTotalBill = findViewById(R.id.txtTotalBill);
        btnPurchase = findViewById(R.id.btnPurchase);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ProductCartDetailsActivity.this, LinearLayoutManager.VERTICAL));
        getProductDetails();

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllAdminUser();
                setPlaceYourOrder();

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

    private void getProductDetails() {
        FirebaseUser user = fireAuth.getCurrentUser();
        CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());
        fireRefe.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    OrdersItem note = documentSnapshot.toObject(OrdersItem.class);
                    note.setDocId(documentSnapshot.getId());
                    itemList.add(note);
                }

                RecyclerViewRemoveClickListener listener = new RecyclerViewRemoveClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //Toast.makeText(getContext(), "Position " + messages.get(position).getUserUId(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRemoveItem(View view, int position) {
                        //Toast.makeText(getContext(), "Position " + messages.get(position).getUserUId(), Toast.LENGTH_SHORT).show();
                        if (NetUtils.isNetworkAvailable(ProductCartDetailsActivity.this)) {
                            if (itemList.size() != 0 && itemList != null) {
                                removeCartItem(position);
                            } else {
                                Utility.displayDialog(ProductCartDetailsActivity.this, getString(R.string.common_no_user_available), false);
                            }
                        } else {
                            Utility.displayDialog(ProductCartDetailsActivity.this, getString(R.string.common_no_internet), false);
                        }
                    }
                };

                if (itemList.size() != 0 && itemList != null) {
                    mAdapter = new ProductCartAdapter(ProductCartDetailsActivity.this, itemList, listener);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    empty_view.setVisibility(View.GONE);
                    txtTotalBill.setText(String.valueOf(mAdapter.grandTotal(itemList)));
                    btnPurchase.setEnabled(true);
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    empty_view.setVisibility(View.VISIBLE);
                    btnPurchase.setEnabled(false);
                }
            }
        });
    }


    public void removeCartItem(final int position) {
        Log.d(TAG, "onSuccess:" + itemList.get(position).getProductName());
        FirebaseUser user = fireAuth.getCurrentUser();
        CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());

        fireRefe.document(itemList.get(position).getDocId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                mAdapter.removeAt(position);
                txtTotalBill.setText(String.valueOf(mAdapter.grandTotal(itemList)));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }


    public void setPlaceYourOrder() {
        //enable this comment for order purchase ---------------->
        ArrayList<OrdersItem> allItemList = new ArrayList<>();
        FirebaseUser user = fireAuth.getCurrentUser();
        CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());
        fireRefe.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    OrdersItem note = documentSnapshot.toObject(OrdersItem.class);
                    note.setDocId(documentSnapshot.getId());

                    allItemList.add(note);
                }


                if (allItemList.size() == queryDocumentSnapshots.size()) {
                    final FieldValue billDate = FieldValue.serverTimestamp();
                    FirebaseUser user = fireAuth.getCurrentUser();
                    DocumentReference fireRefe = fireStore.collection("order_confirm").document(user.getUid());
                    //collectReference = fireStore.collection("orders").document(user.getUid()).collection(fireReference.getId());
                    //CollectionReference firee = fireRefe.collection(fireRefe.getId());
                    DocumentReference firee = fireRefe.collection(fireRefe.getId()).document();

                    BillItem billItem = new BillItem();
                    billItem.setDocId(firee.getId());
                    billItem.setUserId(user.getUid());
                    billItem.setUuId(user.getUid());
                    billItem.setOrderStatus(getResources().getString(R.string.order_type1));  //Pending , Accept, Dispatch, Delivered
                    billItem.setBillCreatedDate(billDate);
                    billItem.setItemArrayList(allItemList);

                    firee.set(billItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            for (int i = 0; i < adminItemList.size(); i++) {
                                Log.e("send notification","of all admin user");
                                if(!TextUtils.isEmpty(adminItemList.get(i).getUserToken()) &&
                                        adminItemList.get(i).getUserToken() != null) {

                                    /*FirebaseIDService idService = new FirebaseIDService();
                                    idService.sendWithOtherThread(adminItemList.get(i).getUserToken(), String.valueOf(itemList.size()));
                                    break;*/
                                }
                            }

                            CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());
                            fireRefe.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        documentSnapshot.getReference().delete();
                                    }
                                    Intent intent = new Intent();
                                    intent.putExtra("purchase", "success");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });
    }


    public void getAllAdminUser() {
        Query capitalCities = fireStore.collection("users").whereEqualTo("userType", 1);
        capitalCities.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Users note = documentSnapshot.toObject(Users.class);
                    adminItemList.add(note);
                }
            }
        });
    }




}
