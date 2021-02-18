package com.kolkatahaat.view.admin.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.AdminOrdersAdapter;
import com.kolkatahaat.interfaces.RecyclerViewClickListener;
import com.kolkatahaat.model.BillItem;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.view.admin.AdminOrdersDetailsActivity;
import com.kolkatahaat.view.customer.fragments.EatableFragment;
import com.kolkatahaat.view.customer.fragments.OrdersFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class AdminOrdersFragment extends Fragment {

    public final String TAG = AdminOrdersFragment.this.getClass().getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;

    private TabLayout tabLayout;
    private TextView empty_view;
    private RecyclerView recyclerView;
    private AdminOrdersAdapter mAdapter;
    private List<BillItem> billItemsList;
    //private List<OrdersItem> messages;
    private ProgressBar progressBar;

    private String mParam1;
    private String mParam2;

    private int selectTab = 0;

    public AdminOrdersFragment() {
        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        billItemsList = new ArrayList<>();
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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_orders, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        progressBar = view.findViewById(R.id.progressBar);
        empty_view = view.findViewById(R.id.empty_view);
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


        getAllOrder();
        getDocumentData();
        return view;
    }


    public void getAllOrder() {
        billItemsList.clear();
        Query query = fireStore.collection("users");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot userDocument : task.getResult()) {
                        Log.d("LOG_TAG", userDocument.getId() + " => " + userDocument.getData());

                        DocumentReference questionsRef = fireStore.collection("order_confirm").document(userDocument.getId());
                        CollectionReference reference = questionsRef.collection(userDocument.getId());

                        reference.orderBy("billCreatedDate", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                for (DocumentSnapshot document : task.getResult()) {
                                    BillItem billModel = document.toObject(BillItem.class);

                                    Users usersInfo = userDocument.toObject(Users.class);
                                    billModel.setItemUsers(usersInfo);
                                    billItemsList.add(billModel);

                                   /* DocumentReference documentReference = fireStore.collection("users").document(billModel.getUserId());
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                DocumentSnapshot snapshot = task.getResult();
                                                Users usersInfo = snapshot.toObject(Users.class);
                                                billModel.setItemUsers(usersInfo);

                                                billItemsList.add(billModel);
                                            }
                                        }
                                    });*/
                                }


                                if(billItemsList.size() != 0 && billItemsList != null) {
                                    mAdapter = new AdminOrdersAdapter(getActivity(), billItemsList, listener);
                                    recyclerView.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                    recyclerView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    empty_view.setVisibility(View.GONE);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    empty_view.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });



        tabLayout.getTabAt(selectTab).select();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        selectTab = 0;
                        tabLayout.getTabAt(selectTab).select();

                        mAdapter = new AdminOrdersAdapter(getActivity(), billItemsList, listener);
                        recyclerView.setAdapter(mAdapter);
                        break;

                    case 1:
                        selectTab = 1;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewClickListener listener1 = new RecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                //Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();

                                Log.e("===>", getPendingItem().get(position).getOrderStatus());
                                //Toast.makeText(MainActivity.this, "Click on " + position + " data = " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminOrdersDetailsActivity.class);
                                intent.putExtra("SELECTED_USER_ID", getPendingItem().get(position).getUuId());
                                intent.putExtra("EXTRA_PRODUCT_ID", getPendingItem().get(position).getDocId());
                                getActivity().startActivity(intent);
                            }
                        };

                        mAdapter = new AdminOrdersAdapter(getActivity(), getPendingItem(), listener1);
                        recyclerView.setAdapter(mAdapter);
                        break;

                    case 2:
                        selectTab = 2;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewClickListener listener2 = new RecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                //Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();

                                Log.e("===>", getAcceptedItem().get(position).getOrderStatus());
                                //Toast.makeText(MainActivity.this, "Click on " + position + " data = " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminOrdersDetailsActivity.class);
                                intent.putExtra("SELECTED_USER_ID", getAcceptedItem().get(position).getUuId());
                                intent.putExtra("EXTRA_PRODUCT_ID", getAcceptedItem().get(position).getDocId());
                                getActivity().startActivity(intent);
                            }
                        };

                        mAdapter = new AdminOrdersAdapter(getActivity(), getAcceptedItem(), listener2);
                        recyclerView.setAdapter(mAdapter);
                        break;

                    case 3:
                        selectTab = 3;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewClickListener listener3 = new RecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                //Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();

                                Log.e("===>", getPackingItem().get(position).getOrderStatus());
                                //Toast.makeText(MainActivity.this, "Click on " + position + " data = " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminOrdersDetailsActivity.class);
                                intent.putExtra("SELECTED_USER_ID", getPackingItem().get(position).getUuId());
                                intent.putExtra("EXTRA_PRODUCT_ID", getPackingItem().get(position).getDocId());
                                getActivity().startActivity(intent);
                            }
                        };

                        mAdapter = new AdminOrdersAdapter(getActivity(), getPackingItem(), listener3);
                        recyclerView.setAdapter(mAdapter);
                        break;

                    case 4:
                        selectTab = 4;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewClickListener listener4 = new RecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                //Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();

                                Log.e("===>", getDeliveredItem().get(position).getOrderStatus());
                                //Toast.makeText(MainActivity.this, "Click on " + position + " data = " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminOrdersDetailsActivity.class);
                                intent.putExtra("SELECTED_USER_ID", getDeliveredItem().get(position).getUuId());
                                intent.putExtra("EXTRA_PRODUCT_ID", getDeliveredItem().get(position).getDocId());
                                getActivity().startActivity(intent);
                            }
                        };

                        mAdapter = new AdminOrdersAdapter(getActivity(), getDeliveredItem(), listener4);
                        recyclerView.setAdapter(mAdapter);
                        break;

                    case 5:
                        selectTab = 5;
                        tabLayout.getTabAt(selectTab).select();

                        RecyclerViewClickListener listener5 = new RecyclerViewClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                //Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();

                                Log.e("===>", getReceivedItem().get(position).getOrderStatus());
                                //Toast.makeText(MainActivity.this, "Click on " + position + " data = " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminOrdersDetailsActivity.class);
                                intent.putExtra("SELECTED_USER_ID", getReceivedItem().get(position).getUuId());
                                intent.putExtra("EXTRA_PRODUCT_ID", getReceivedItem().get(position).getDocId());
                                getActivity().startActivity(intent);
                            }
                        };

                        mAdapter = new AdminOrdersAdapter(getActivity(), getReceivedItem(), listener5);
                        recyclerView.setAdapter(mAdapter);
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
    }


    RecyclerViewClickListener listener = new RecyclerViewClickListener() {
        @Override
        public void onClick(View view, int position) {
            //Toast.makeText(getContext(), "Position " + messages.get(position).getProductName(), Toast.LENGTH_SHORT).show();

            Log.e("===>", billItemsList.get(position).getOrderStatus());
            //Toast.makeText(MainActivity.this, "Click on " + position + " data = " + adapter.getItem(position), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), AdminOrdersDetailsActivity.class);
            intent.putExtra("SELECTED_USER_ID", billItemsList.get(position).getUuId());
            intent.putExtra("EXTRA_PRODUCT_ID", billItemsList.get(position).getDocId());
            getActivity().startActivity(intent);
        }
    };

    public List<BillItem> getPendingItem() {
        List<BillItem> productList = new ArrayList<>();
        for (BillItem pojoOfJsonArray : billItemsList) {
            if (pojoOfJsonArray.getOrderStatus().equals("Pending")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    public List<BillItem> getAcceptedItem() {
        List<BillItem> productList = new ArrayList<>();
        for (BillItem pojoOfJsonArray : billItemsList) {
            if (pojoOfJsonArray.getOrderStatus().equals("Accepted")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    public List<BillItem> getPackingItem() {
        List<BillItem> productList = new ArrayList<>();
        for (BillItem pojoOfJsonArray : billItemsList) {
            if (pojoOfJsonArray.getOrderStatus().equals("Packing")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    public List<BillItem> getDeliveredItem() {
        List<BillItem> productList = new ArrayList<>();
        for (BillItem pojoOfJsonArray : billItemsList) {
            if (pojoOfJsonArray.getOrderStatus().equals("Delivered")) {
                productList.add(pojoOfJsonArray);
            }
        }
        return productList;
    }

    public List<BillItem> getReceivedItem() {
        List<BillItem> productList = new ArrayList<>();
        for (BillItem pojoOfJsonArray : billItemsList) {
            if (pojoOfJsonArray.getOrderStatus().equals("Received")) {
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
                    Collections.sort(billItemsList, new Comparator<BillItem>() {
                        @Override
                        public int compare(BillItem lhs, BillItem rhs) {
                            return lhs.getOrderStatus().compareToIgnoreCase(rhs.getOrderStatus());
                        }
                    });
                    mAdapter.updateDataVal(billItemsList);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 1) {
                    List<BillItem> products1 = getPendingItem();
                    Collections.sort(products1, new Comparator<BillItem>() {
                        @Override
                        public int compare(BillItem lhs, BillItem rhs) {
                            return lhs.getOrderStatus().compareToIgnoreCase(rhs.getOrderStatus());
                        }
                    });
                    mAdapter.updateDataVal(products1);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 2) {
                    List<BillItem> products2 = getAcceptedItem();
                    Collections.sort(products2, new Comparator<BillItem>() {
                        @Override
                        public int compare(BillItem lhs, BillItem rhs) {
                            return lhs.getOrderStatus().compareToIgnoreCase(rhs.getOrderStatus());
                        }
                    });
                    mAdapter.updateDataVal(products2);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 3) {
                    List<BillItem> products3 = getPackingItem();
                    Collections.sort(products3, new Comparator<BillItem>() {
                        @Override
                        public int compare(BillItem lhs, BillItem rhs) {
                            return lhs.getOrderStatus().compareToIgnoreCase(rhs.getOrderStatus());
                        }
                    });
                    mAdapter.updateDataVal(products3);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 4) {
                    List<BillItem> products4 = getDeliveredItem();
                    Collections.sort(products4, new Comparator<BillItem>() {
                        @Override
                        public int compare(BillItem lhs, BillItem rhs) {
                            return lhs.getOrderStatus().compareToIgnoreCase(rhs.getOrderStatus());
                        }
                    });
                    mAdapter.updateDataVal(products4);
                    mAdapter.notifyDataSetChanged();
                } else if (selectTab == 5) {
                    List<BillItem> products5 = getReceivedItem();
                    Collections.sort(products5, new Comparator<BillItem>() {
                        @Override
                        public int compare(BillItem lhs, BillItem rhs) {
                            return lhs.getOrderStatus().compareToIgnoreCase(rhs.getOrderStatus());
                        }
                    });
                    mAdapter.updateDataVal(products5);
                    mAdapter.notifyDataSetChanged();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public void getDocumentData() {
        Query query = fireStore.collection("users");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        DocumentReference questionsRef = fireStore.collection("order_confirm").document(document.getId());
                        CollectionReference reference = questionsRef.collection(document.getId());

                        reference.orderBy("billCreatedDate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w("TAG", "listen:error", e);
                                    return;
                                }


                                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                    switch (dc.getType()) {
                                        case MODIFIED:
                                            String mkey = dc.getDocument().getId();
                                            if (billItemsList.size() != 0) {
                                                for (int i = 0; i < billItemsList.size(); i++) {
                                                    if (billItemsList.get(i).getDocId().equals(dc.getDocument().getId())) {
                                                        BillItem mbillModel = dc.getDocument().toObject(BillItem.class);
                                                        mbillModel.setItemUsers(billItemsList.get(i).getItemUsers());
                                                        billItemsList.set(i, mbillModel);
                                                        mAdapter.notifyDataSetChanged();
                                                        break;
                                                    }
                                                }
                                            }
                                            break;

                                        case REMOVED:
                                            if (billItemsList.size() != 0) {
                                                for (int i = 0; i < billItemsList.size(); i++) {
                                                    if (billItemsList.get(i).getDocId().equals(dc.getDocument().getId())) {
                                                        billItemsList.remove(i);
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
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //Set title of this fragment
        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.menu_users_orders));
        }
    }
}
