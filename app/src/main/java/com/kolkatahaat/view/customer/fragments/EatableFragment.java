package com.kolkatahaat.view.customer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.ProductListAdapter;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.view.customer.ProductPurchaseActivity;

import java.util.ArrayList;
import java.util.List;

public class EatableFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;

    private FirebaseFirestore fireStore;
    private CollectionReference collectReference;

    private RecyclerView recyclerView;
    private ProductListAdapter mAdapter;
    private List<Product> messages;

    private String mParam1;
    private String mParam2;

    public EatableFragment() {
        fireStore = FirebaseFirestore.getInstance();
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
        View view = inflater.inflate(R.layout.fragment_eatable, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        Query capitalCities = fireStore.collection("products").whereEqualTo("productCategory", "Eatable");
        capitalCities.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Product note = documentSnapshot.toObject(Product.class);
                    messages.add(note);
                }


                RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(getContext(), "Position " + messages.get(position).getProductId(), Toast.LENGTH_SHORT).show();

                        if(!TextUtils.isEmpty(messages.get(position).getProductId()) &&
                                messages.get(position).getProductId() != null) {
                            Intent intent = new Intent(getActivity(), ProductPurchaseActivity.class);
                            intent.putExtra("EXTRA_PRODUCT_ID", messages.get(position).getProductId());
                            startActivity(intent);
                        }
                    }
                };
                mAdapter = new ProductListAdapter(getActivity(), messages, listener);
                recyclerView.setAdapter(mAdapter);
            }
        });

        return view;
    }
}
