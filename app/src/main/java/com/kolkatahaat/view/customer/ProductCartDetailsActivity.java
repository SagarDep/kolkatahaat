package com.kolkatahaat.view.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.ProductCartAdapter;
import com.kolkatahaat.adapterview.ProductListAdapter;
import com.kolkatahaat.interfaces.ItemClickListener;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductCartDetailsActivity extends AppCompatActivity implements ItemClickListener {

    public final String TAG = ProductCartDetailsActivity.this.getClass().getSimpleName();


    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private DocumentReference fireReference;
    private CollectionReference collectReference;

    public Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView txtTotalBill;
    private Button btnPurchase;
    private ProductCartAdapter mAdapter;
    private List<OrdersItem> itemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart_details);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        itemList = new ArrayList<>();

        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.mRecyclerView);
        txtTotalBill = findViewById(R.id.txtTotalBill);
        btnPurchase = findViewById(R.id.btnPurchase);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(ProductCartDetailsActivity.this, LinearLayoutManager.VERTICAL));
        getProductDetails();

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlaceYourOrder();
            }
        });
    }


    private void getProductDetails() {
        FirebaseUser user = fireAuth.getCurrentUser();
        CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());
        fireRefe.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    OrdersItem note = documentSnapshot.toObject(OrdersItem.class);
                    note.setDocId(documentSnapshot.getId());

                    itemList.add(note);
                }
                if (itemList.size() != 0) {
                    mAdapter = new ProductCartAdapter(ProductCartDetailsActivity.this, itemList);
                    mAdapter.setClickListener(ProductCartDetailsActivity.this);
                    recyclerView.setAdapter(mAdapter);
                    txtTotalBill.setText(String.valueOf(mAdapter.grandTotal(itemList)));
                }
            }
        });



       /* fireReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        OrdersItem note = document.toObject(OrdersItem.class);
                        //Use the the list
                        Log.d(TAG, "onSuccess:"+note.getProductName());
                    }
                }
            }
        });*/

        /*
        fireReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    OrdersItem ordersItem = documentSnapshot.toObject(OrdersItem.class);


                    Log.d(TAG, "onSuccess:"+ordersItem.getProductName());
                } else {
                    Log.d(TAG, "onSuccess: LIST EMPTY");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });*/
    }

    @Override
    public void onClick(View view, int position) {
        Log.d(TAG, "onSuccess:" + itemList.get(position).getProductName());

        FirebaseUser user = fireAuth.getCurrentUser();
        CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());

        fireRefe.document(itemList.get(position).getDocId()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        mAdapter.removeAt(position);
                        txtTotalBill.setText(String.valueOf(mAdapter.grandTotal(itemList)));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }


    public void setPlaceYourOrder(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Get new FCM registration token
                String token = task.getResult();
                Log.e(TAG, "token"+ token);

                //userToken


                FirebaseUser user = fireAuth.getCurrentUser();
                fireReference = fireStore.collection("users").document(user.getUid());
                fireReference.update("userToken", token).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            }
        });

        //enable this comment for order purchase ---------------->
        /*List<OrdersItem> allItemList = new ArrayList<>();
        FirebaseUser user = fireAuth.getCurrentUser();
        CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());
        fireRefe.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    OrdersItem note = documentSnapshot.toObject(OrdersItem.class);
                    note.setDocId(documentSnapshot.getId());

                    allItemList.add(note);
                }


                if(allItemList.size() == queryDocumentSnapshots.size()) {
                    FirebaseUser user = fireAuth.getCurrentUser();
                    DocumentReference fireRefe = fireStore.collection("order_confirm").document(user.getUid());
                    //collectReference = fireStore.collection("orders").document(user.getUid()).collection(fireReference.getId());
                    CollectionReference firee = fireRefe.collection(fireRefe.getId());

                    Map<String, Object> updateMap = new HashMap();
                    updateMap.put("order", allItemList);

                    firee.add(updateMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //getProductDetails();
                        }
                    });
                }
            }
        });*/
    }
}
