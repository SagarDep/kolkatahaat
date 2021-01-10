package com.kolkatahaat.view.customer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;

import com.kolkatahaat.adapterview.OrdersListAdapter;
import com.kolkatahaat.interfaces.ItemClickListener;
import com.kolkatahaat.model.OrdersItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersFragment extends Fragment implements ItemClickListener {

    public final String TAG = OrdersFragment.this.getClass().getSimpleName();


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private CollectionReference collectReference;

    private RecyclerView recyclerView;
    private OrdersListAdapter mAdapter;
     private List<OrdersItem> messages;

    private String mParam1;
    private String mParam2;

    public OrdersFragment() {
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        messages = new ArrayList<>();
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

        mAdapter = new OrdersListAdapter(getActivity());
        mAdapter.setClickListener(this);
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
                    messages = (List<OrdersItem>) document.get("order");

                    /*for (OrdersItem item : list) {
                        Log.d("TAG", item.getProductName());
                    }*/
                }

                mAdapter = new OrdersListAdapter(getActivity(),messages);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Log.d(TAG, "onSuccess:" + messages.get(position).getProductName());
    }
}
