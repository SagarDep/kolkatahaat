package com.kolkatahaat.view.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.ProductListAdapter;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.interfaces.RecyclerViewRemoveClickListener;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.view.admin.AdminAddProductActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminProductListFragment extends Fragment {

    private FirebaseFirestore fireStore;
    private CollectionReference collectReference;
    private FirebaseAuth fireAuth;

    private TabLayout tabLayout;
    private FloatingActionButton mAddFab;
    private RecyclerView mRecyclerView;
    private ProductListAdapter mAdapter;
    private List<Product> messages;

    private ProgressBar progressBar;

    private int selectTab = 0;

    public AdminProductListFragment() {
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        collectReference = fireStore.collection("products");
        messages = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_product_list, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        progressBar = view.findViewById(R.id.progressBar);
        mRecyclerView = view.findViewById(R.id.mRecyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        //getAllItem();
        tabLayout.getTabAt(selectTab).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        selectTab = 0;
                        tabLayout.getTabAt(selectTab).select();

                        mAdapter = new ProductListAdapter(getActivity(), messages, listener);
                        mRecyclerView.setAdapter(mAdapter);
                        break;

                    case 1:
                        selectTab = 1;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewRemoveClickListener listener2 = new RecyclerViewRemoveClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Toast.makeText(getContext(), "Position " + getEatableItem().get(position).getProductName(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminAddProductActivity.class);
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_ID", getEatableItem().get(position).getProductId());
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_EDIT", true);
                                startActivity(intent);
                            }
                            @Override
                            public void onRemoveItem(View view, int position) {
                                if (getEatableItem().get(position) != null && getEatableItem().get(position).getProductId() != null
                                        && !TextUtils.isEmpty(getEatableItem().get(position).getProductId())) {
                                    productRemove(getEatableItem().get(position).getProductId());
                                }
                            }
                        };
                        mAdapter = new ProductListAdapter(getActivity(), getEatableItem(), listener2);
                        mRecyclerView.setAdapter(mAdapter);
                        break;

                    case 2:
                        selectTab = 2;
                        tabLayout.getTabAt(selectTab).select();


                        RecyclerViewRemoveClickListener listener4 = new RecyclerViewRemoveClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Toast.makeText(getContext(), "Position " + getPujaItemsItem().get(position).getProductName(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminAddProductActivity.class);
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_ID", getPujaItemsItem().get(position).getProductId());
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_EDIT", true);
                                startActivity(intent);
                            }
                            @Override
                            public void onRemoveItem(View view, int position) {
                                if (getPujaItemsItem().get(position) != null && getPujaItemsItem().get(position).getProductId() != null
                                        && !TextUtils.isEmpty(getPujaItemsItem().get(position).getProductId())) {
                                    productRemove(getPujaItemsItem().get(position).getProductId());
                                }
                            }
                        };
                        mAdapter = new ProductListAdapter(getActivity(), getPujaItemsItem(), listener4);
                        mRecyclerView.setAdapter(mAdapter);
                        break;

                    case 3:
                        selectTab = 3;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewRemoveClickListener listener3 = new RecyclerViewRemoveClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Toast.makeText(getContext(), "Position " + getClothingItem().get(position).getProductName(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminAddProductActivity.class);
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_ID", getClothingItem().get(position).getProductId());
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_EDIT", true);
                                startActivity(intent);
                            }
                            @Override
                            public void onRemoveItem(View view, int position) {
                                if (getClothingItem().get(position) != null && getClothingItem().get(position).getProductId() != null
                                        && !TextUtils.isEmpty(getClothingItem().get(position).getProductId())) {
                                    productRemove(getClothingItem().get(position).getProductId());
                                }
                            }
                        };
                        mAdapter = new ProductListAdapter(getActivity(), getClothingItem(), listener3);
                        mRecyclerView.setAdapter(mAdapter);
                        break;

                    case 4:
                        selectTab = 4;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewRemoveClickListener listener5 = new RecyclerViewRemoveClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Toast.makeText(getContext(), "Position " + getOthersItem().get(position).getProductName(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminAddProductActivity.class);
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_ID", getOthersItem().get(position).getProductId());
                                intent.putExtra("EXTRA_ADMIN_PRODUCT_EDIT", true);
                                startActivity(intent);
                            }
                            @Override
                            public void onRemoveItem(View view, int position) {
                                if (getOthersItem().get(position) != null && getOthersItem().get(position).getProductId() != null
                                        && !TextUtils.isEmpty(getOthersItem().get(position).getProductId())) {
                                    productRemove(getOthersItem().get(position).getProductId());
                                }
                            }
                        };
                        mAdapter = new ProductListAdapter(getActivity(), getOthersItem(), listener5);
                        mRecyclerView.setAdapter(mAdapter);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        collectReference.orderBy("productCreatedDate", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Product note = documentSnapshot.toObject(Product.class);
                    messages.add(note);
                }


                /*RecyclerViewRemoveClickListener listener = new RecyclerViewRemoveClickListener() {
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
                };*/
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
            if (messages.get(position) != null && messages.get(position).getProductId() != null
                    && !TextUtils.isEmpty(messages.get(position).getProductId())) {
                productRemove(messages.get(position).getProductId());
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.menu_product));
        }
    }


    public List<Product> getEatableItem() {
        List<Product> productList = new ArrayList<>();
        for (Product pojoOfJsonArray : messages) {
            if (pojoOfJsonArray.getProductCategory().equals("Eatable")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    public List<Product> getClothingItem() {
        List<Product> productList = new ArrayList<>();
        for (Product pojoOfJsonArray : messages) {
            if (pojoOfJsonArray.getProductCategory().equals("Clothing")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    public List<Product> getPujaItemsItem() {
        List<Product> productList = new ArrayList<>();
        for (Product pojoOfJsonArray : messages) {
            if (pojoOfJsonArray.getProductCategory().equals("Puja Items")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    public List<Product> getOthersItem() {
        List<Product> productList = new ArrayList<>();
        for (Product pojoOfJsonArray : messages) {
            if (pojoOfJsonArray.getProductCategory().equals("Others")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_sorting, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_new_atoz:
                if (selectTab == 0) {
                    Collections.sort(messages, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return lhs.getProductName().compareToIgnoreCase(rhs.getProductName());
                        }
                    });
                    mAdapter.updateDataVal(messages);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 1) {
                    List<Product> products1 = getEatableItem();
                    Collections.sort(products1, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return lhs.getProductName().compareToIgnoreCase(rhs.getProductName());
                        }
                    });
                    mAdapter.updateDataVal(products1);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 2) {
                    List<Product> products2 = getClothingItem();
                    Collections.sort(products2, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return lhs.getProductName().compareToIgnoreCase(rhs.getProductName());
                        }
                    });
                    mAdapter.updateDataVal(products2);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 3) {
                    List<Product> products3 = getPujaItemsItem();
                    Collections.sort(products3, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return lhs.getProductName().compareToIgnoreCase(rhs.getProductName());
                        }
                    });
                    mAdapter.updateDataVal(products3);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 4) {
                    List<Product> products4 = getOthersItem();
                    Collections.sort(products4, new Comparator<Product>() {
                        @Override
                        public int compare(Product lhs, Product rhs) {
                            return lhs.getProductName().compareToIgnoreCase(rhs.getProductName());
                        }
                    });
                    mAdapter.updateDataVal(products4);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem itemData = menu.findItem(R.id.action_addcart);
        //actionView = itemData.getActionView() as CartCounterActionView
        rootView = (CartCounterActionView) itemData.getActionView();
        rootView.setItemData(menu, itemData);
        //rootView.setCount(cartCount);
        super.onPrepareOptionsMenu(menu);
    }*/

    public void checkListUpdate() {
        collectReference.orderBy("productCreatedDate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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


    public void productRemove(String mAdminProductId) {
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
