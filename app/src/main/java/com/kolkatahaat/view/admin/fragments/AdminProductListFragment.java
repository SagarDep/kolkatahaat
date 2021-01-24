package com.kolkatahaat.view.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.ProductListAdapter;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.interfaces.RecyclerViewRemoveClickListener;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.view.admin.AdminAddProductActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminProductListFragment extends Fragment {

    private FirebaseFirestore fireStore;
    private CollectionReference collectReference;
    private FirebaseAuth fireAuth;

    private FloatingActionButton mAddFab;
    private RecyclerView mRecyclerView;
    private ProductListAdapter mAdapter;
    private List<Product> messages;
    private ProgressBar progressBar;

    public AdminProductListFragment() {
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        collectReference = fireStore.collection("products");
        messages = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_product_list, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        collectReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Product note = documentSnapshot.toObject(Product.class);
                    messages.add(note);
                }


                RecyclerViewRemoveClickListener listener = new RecyclerViewRemoveClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), AdminAddProductActivity.class);
                        intent.putExtra("EXTRA_ADMIN_PRODUCT_ID", messages.get(position).getProductId());
                        intent.putExtra("EXTRA_ADMIN_PRODUCT_EDIT", true);
                        startActivity(intent);
                    }

                    @Override
                    public void onRemoveItem(View view, int position) {
                        if(messages.get(position) != null && messages.get(position).getProductId() != null
                        && !TextUtils.isEmpty(messages.get(position).getProductId())){
                            productRemove(messages.get(position).getProductId());
                        }
                    }
                };
                mAdapter = new ProductListAdapter(getActivity(), messages, listener);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

        mAddFab = view.findViewById(R.id.btnFab);
        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdminAddProductActivity.class);
                intent.putExtra("EXTRA_ADMIN_PRODUCT_EDIT", false);
                startActivity(intent);
            }
        });

        checkListUpdate();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set title of this fragment
        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.menu_product));
        }
    }


    public void checkListUpdate() {
        collectReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            String key = dc.getDocument().getId();
                            Log.d("TAG", "Modified Msg: " + key);

                            if (messages.size() != 0) {
                                for (int i = 0; i < messages.size(); i++) {
                                    if (messages.get(i).getProductId().equals(dc.getDocument().getId())) {

                                        Product billModel = dc.getDocument().toObject(Product.class);
                                        messages.set(i, billModel);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                            break;

                        case REMOVED:
                            Log.d("TAG", "Removed Msg: " + dc.getDocument().toObject(Product.class));

                            if (messages.size() != 0) {
                                for (int i = 0; i < messages.size(); i++) {
                                    if (messages.get(i).getProductId().equals(dc.getDocument().getId())) {

                                        //BillModel billModel = dc.getDocument().toObject(BillModel.class);
                                        messages.remove(i);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        });
    }


    public void productRemove(String mAdminProductId){
        //Toast.makeText(getContext(), "delete " + id, Toast.LENGTH_SHORT).show();
        DocumentReference fireReference = fireStore.collection("products").document(mAdminProductId);
        fireReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                /*adapter.remove(adapter.getItem(position));
                messages.remove(task);
                mAdapter.notifyDataSetChanged();*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error deleting document", e);
            }
        });
    }
}
