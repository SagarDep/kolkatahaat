package com.kolkatahaat.view.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kolkatahaat.R;

import com.kolkatahaat.adapterview.OrdersItemListAdapter;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.BillItem;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.view.customer.OrderDetailsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersFragment extends Fragment {

    public final String TAG = OrdersFragment.this.getClass().getSimpleName();


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private CollectionReference collectReference;

    private RecyclerView recyclerView;
    private OrdersItemListAdapter mAdapter;
     private List<BillItem> billItems;
     //private List<OrdersItem> messages;

    private String mParam1;
    private String mParam2;

    public OrdersFragment() {
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        billItems = new ArrayList<>();
    }


    public static EatableFragment newInstance(String param1, String param2) {
        EatableFragment fragment = new EatableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        mAdapter = new OrdersItemListAdapter(getActivity());
        getAllOrders();

        return view;
    }

    public void getAllOrders(){

        FirebaseUser user = fireAuth.getCurrentUser();
        DocumentReference fireRefe = fireStore.collection("order_confirm").document(user.getUid());
        //collectReference = fireStore.collection("orders").document(user.getUid()).collection(fireReference.getId());
        CollectionReference firee = fireRefe.collection(fireRefe.getId());


        firee.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (DocumentSnapshot document : task.getResult()) {
                    //messages = (ArrayList<BillItem>) document.get("order");

                    BillItem ordersItem = document.toObject(BillItem.class);

                    /*for (OrdersItem item : list) {
                        Log.d("TAG", item.getProductName());
                    }*/
                    //messages.addAll(ordersItem.getItemArrayList());
                    billItems.add(ordersItem);

                }

                RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        //Toast.makeText(getContext(), "Position " + billItems.get(position).getOrderStatus(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                        intent.putExtra("EXTRA_BILL_ITEM_ID", billItems.get(position).getDocId());
                        if(billItems.get(position).getRejectionStatus()){
                            intent.putExtra("EXTRA_REJECT_STATUS", billItems.get(position).getRejectionStatus());
                            intent.putExtra("EXTRA_REJECT_NOTE", billItems.get(position).getRejectionNote());
                        }
                        getActivity().startActivity(intent);
                    }
                };
                mAdapter = new OrdersItemListAdapter(getActivity(),billItems, listener);
                recyclerView.setAdapter(mAdapter);
            }
        });

        checkStatus();

    }


    public void checkStatus() {
        FirebaseUser user = fireAuth.getCurrentUser();
        DocumentReference fireRefe = fireStore.collection("order_confirm").document(user.getUid());
        //collectReference = fireStore.collection("orders").document(user.getUid()).collection(fireReference.getId());
        CollectionReference firee = fireRefe.collection(fireRefe.getId());
        firee.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        /*case ADDED:
                            Log.d("TAG", "New Msg: " + dc.getDocument().toObject(BillModel.class));
                            break;*/
                        case MODIFIED:

                            if (billItems.size() != 0) {
                                for (int i = 0; i < billItems.size(); i++) {
                                    if (billItems.get(i).getDocId().equals(dc.getDocument().getId())) {
                                        BillItem billModel = dc.getDocument().toObject(BillItem.class);
                                        billItems.set(i, billModel);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                            break;

                        case REMOVED:
                            //Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(BillModel.class));
                            break;
                    }
                }
            }
        });
    }
}
