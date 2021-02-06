package com.kolkatahaat.view.admin;

import android.content.Context;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.AdminOrdersDetailsAdapter;
import com.kolkatahaat.interfaces.AdminOrderRejectDialogListener;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.BillItem;
import com.kolkatahaat.model.OrderCompleteModel;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.service.FirebaseIDService;
import com.kolkatahaat.view.admin.fragments.OrderRejectOrderNoteDialog;

import java.util.ArrayList;

public class AdminOrdersDetailsActivity extends AppCompatActivity implements AdminOrderRejectDialogListener {

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;

    public Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    AppBarLayout appBarLayout;

    private RecyclerView recyclerView;
    private TextView empty_view;
    private ProgressBar progressBar;
    private AdminOrdersDetailsAdapter detailsAdapter;
    private ArrayList<BillItem> productsList;
    private BillItem billItemModel;

    private TextView txtDeliveryCharges;
    private TextView txtProductAmount;
    private TextView txtTotalBill;
    private int mDeliveryCharge = 0;

    private Button btnOrderStatus;
    private Button btnOrderRejected;
    String selectedUserId;
    String productId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders_details);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            selectedUserId = getIntent().getStringExtra("SELECTED_USER_ID");
            productId = getIntent().getStringExtra("EXTRA_PRODUCT_ID");
            Log.e("===>", productId);
        }

        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        productsList = new ArrayList<>();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        txtDeliveryCharges = findViewById(R.id.txtDeliveryCharges);
        txtProductAmount = findViewById(R.id.txtProductAmount);
        txtTotalBill = findViewById(R.id.txtTotalBill);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getResources().getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });

        btnOrderStatus = findViewById(R.id.btnOrderStatus);
        btnOrderRejected = findViewById(R.id.btnOrderRejected);
        empty_view = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AdminOrdersDetailsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(AdminOrdersDetailsActivity.this, LinearLayoutManager.VERTICAL));

        getAllOrderData();

        btnOrderStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type1))){
                    //btnConform.setText(getResources().getString(R.string.order_type2));
                    ordersAccept();
                } else if(billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type2))){
                    ordersPacking();
                } else if(billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type3))){
                    ordersDelivered();
                } else if(billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type4))){
                    ordersReceived(billItemModel);
                } else if(billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type5))){
                    Log.e("===>", "recived");
                    btnOrderStatus.setText(billItemModel.getOrderStatus());
                }
            }
        });

        btnOrderRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderRejectOrderNoteDialog dialog = new OrderRejectOrderNoteDialog();
                dialog.show(getSupportFragmentManager(), "TransparentDialogFragment");
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

    public void getAllOrderData() {
        DocumentReference questionsRef = fireStore.collection("order_confirm").document(selectedUserId);
        DocumentReference reference = questionsRef.collection(selectedUserId).document(productId);


        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                billItemModel = value.toObject(BillItem.class);
                productsList.add(billItemModel);

                txtDeliveryCharges.setText(String.valueOf(billItemModel.getProductDeliveryChange()));
                mDeliveryCharge = billItemModel.getProductDeliveryChange();

                RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();
                    }
                };
                detailsAdapter = new AdminOrdersDetailsAdapter(AdminOrdersDetailsActivity.this, productsList.get(0).getItemArrayList(),listener);
                recyclerView.setAdapter(detailsAdapter);

                txtProductAmount.setText(String.valueOf(detailsAdapter.grandTotal(productsList.get(0).getItemArrayList())));
                txtTotalBill.setText(String.valueOf(mDeliveryCharge + detailsAdapter.grandTotal(productsList.get(0).getItemArrayList())));

                if(billItemModel != null) {
                    if (billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type1))) {
                        btnOrderStatus.setText(getResources().getString(R.string.order_type2));
                    } else if (billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type2))) {
                        btnOrderStatus.setText(getResources().getString(R.string.order_type3));
                    } else if (billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type3))) {
                        btnOrderStatus.setText(getResources().getString(R.string.order_type4));
                    } else if (billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type4))) {
                        btnOrderStatus.setText(getResources().getString(R.string.order_type5));
                    } else if (billItemModel.getOrderStatus().equals(getResources().getString(R.string.order_type5))) {
                        btnOrderStatus.setText(getResources().getString(R.string.order_type5));
                    }
                }
            }
        });
    }

    public void ordersAccept(){
        DocumentReference questionsRef = fireStore.collection("order_confirm").document(selectedUserId);
        DocumentReference reference = questionsRef.collection(selectedUserId).document(productId);
        reference.update("orderStatus",getResources().getString(R.string.order_type2)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendNotify("Your order is "+getResources().getString(R.string.order_type2));
            }
        });
    }

    public void ordersPacking(){
        DocumentReference questionsRef = fireStore.collection("order_confirm").document(selectedUserId);
        DocumentReference reference = questionsRef.collection(selectedUserId).document(productId);
        reference.update("orderStatus",getResources().getString(R.string.order_type3)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendNotify("Your order is "+getResources().getString(R.string.order_type3));
            }
        });
    }

    public void ordersDelivered(){
        DocumentReference questionsRef = fireStore.collection("order_confirm").document(selectedUserId);
        DocumentReference reference = questionsRef.collection(selectedUserId).document(productId);
        reference.update("orderStatus",getResources().getString(R.string.order_type4)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendNotify("Your order is "+getResources().getString(R.string.order_type4));
            }
        });
    }

    public void ordersReceived(BillItem billModel){
        DocumentReference questionsRef = fireStore.collection("order_confirm").document(selectedUserId);
        DocumentReference receivedReference = questionsRef.collection(selectedUserId).document();
        receivedReference.update("orderStatus",getResources().getString(R.string.order_type5)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendNotify("Your order is "+getResources().getString(R.string.order_type4));

                final FieldValue productCreatedDate = FieldValue.serverTimestamp();

                OrderCompleteModel completeModel = new OrderCompleteModel();

                completeModel.setBillCreatedDate(productCreatedDate);
                completeModel.setBillItem(billModel);

                DocumentReference questionsRef = fireStore.collection("ordered_completed").document(selectedUserId);
                DocumentReference receivedReference = questionsRef.collection(productId).document();

                completeModel.setUserId(selectedUserId);
                completeModel.setDocId(receivedReference.getId());
                receivedReference.set(completeModel);
            }
        });
    }

    @Override
    public void onDialogPositive(String mRejectOrder) {
        if (!TextUtils.isEmpty(mRejectOrder) && mRejectOrder != null) {
            DocumentReference questionsRef = fireStore.collection("order_confirm").document(selectedUserId);
            DocumentReference reference = questionsRef.collection(selectedUserId).document(productId);
            reference.update("orderStatus", getResources().getString(R.string.order_type6),
                    "rejectionNote",mRejectOrder,
                    "rejectionStatus",true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }
    }

    @Override
    public void onDialogNegative(Object object) {

    }




    public void sendNotify(String nMessage){
        DocumentReference query = fireStore.collection("users").document(selectedUserId);
        query.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Users usersInfo = task.getResult().toObject(Users.class);

                    FirebaseIDService idService = new FirebaseIDService();
                    idService.sendWithOtherThreadOrderStatus(usersInfo.getUserToken(), nMessage, false);
                }
            }
        });
    }
}

