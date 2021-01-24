package com.kolkatahaat.view.customer;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.OrdersItemDetailsAdapter;
import com.kolkatahaat.model.BillItem;
import com.kolkatahaat.model.OrdersItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private CollectionReference collectReference;

    private Context context;
    public Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView empty_view;

    private String billItemId;
    private List<OrdersItem> billItems;
    private OrdersItemDetailsAdapter mAdapter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            billItemId = bundle.getString("EXTRA_BILL_ITEM_ID");
        }

        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        billItems = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);
        empty_view = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OrderDetailsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(OrderDetailsActivity.this, LinearLayoutManager.VERTICAL));

        getAllOrders(billItemId);
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

    public void getAllOrders(String billItemId){
        FirebaseUser user = fireAuth.getCurrentUser();
        DocumentReference fireRefe = fireStore.collection("order_confirm").document(user.getUid());
        //collectReference = fireStore.collection("orders").document(user.getUid()).collection(fireReference.getId());
        DocumentReference firee = fireRefe.collection(fireRefe.getId()).document(billItemId);

        firee.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    BillItem ordersItem = document.toObject(BillItem.class);
                    billItems.addAll(ordersItem.getItemArrayList());
                }

                if (billItems.size() != 0 && billItems != null) {
                    mAdapter = new OrdersItemDetailsAdapter(OrderDetailsActivity.this, billItems);
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    empty_view.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    empty_view.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
