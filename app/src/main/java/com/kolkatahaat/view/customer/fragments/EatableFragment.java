package com.kolkatahaat.view.customer.fragments;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.CustomerAddProductAdapter;
import com.kolkatahaat.interfaces.RecyclerViewProductClickListener;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.utills.CartCounterActionView;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;
import com.kolkatahaat.view.customer.ProductCartDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class EatableFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FirebaseFirestore fireStore;
    private FirebaseAuth fireAuth;

    private RecyclerView recyclerView;
    private CustomerAddProductAdapter mAdapter;
    private List<Product> messages;
    private ProgressBar progressBar;
    private TextView empty_view;

    private String mParam1;
    private String mParam2;

    private CartCounterActionView rootView;
    private int cartCount = 0;

    public EatableFragment() {
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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eatable, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        empty_view = view.findViewById(R.id.empty_view);
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

                FirebaseUser user = fireAuth.getCurrentUser();
                DocumentReference fireRefe = fireStore.collection("orders").document(user.getUid());
                CollectionReference firee = fireRefe.collection(fireRefe.getId());
                firee.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Product cartProduct = document.toObject(Product.class);

                                cartCount = cartCount + cartProduct.getProductQuantity();

                                for (final ListIterator<Product> i = messages.listIterator(); i.hasNext();) {
                                    final Product element = i.next();
                                    if(element.getProductId().equals(cartProduct.getProductId())) {
                                        i.set(cartProduct);
                                    }
                                }
                            }
                            rootView.setCount(cartCount);
                        }

                        RecyclerViewProductClickListener listener = new RecyclerViewProductClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                //Toast.makeText(getContext(), "Position " + messages.get(position).getProductId(), Toast.LENGTH_SHORT).show();
                                /*if(!TextUtils.isEmpty(messages.get(position).getProductId()) &&
                                        messages.get(position).getProductId() != null) {
                                    Intent intent = new Intent(getActivity(), ProductPurchaseActivity.class);
                                    intent.putExtra("EXTRA_PRODUCT_ID", messages.get(position).getProductId());
                                    startActivity(intent);
                                }*/
                            }

                            @Override
                            public void onClickDecrease(View view, int position) {
                                //Toast.makeText(getContext(), "onClickDecrease " + messages.get(position).getProductId(), Toast.LENGTH_SHORT).show();
                                try {
                                    if(messages.get(position).getProductQuantity() == 1) {
                                        removeProduct(messages.get(position).getProductId(), position);
                                        cartCountRemove();
                                    } else {
                                        Product product = messages.get(position);
                                        if(product.getProductQuantity() != 0) {
                                            product.setProductQuantity(product.getProductQuantity() - 1);
                                            //product.setProductPrice(product.getProductQuantity() * product.getProductPrice());
                                            UpdateCartProduct(product, position);

                                            cartCountRemove();
                                        }
                                    }
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onClickIncrease(View view, int position) {
                                try {
                                    if(messages.get(position).getProductQuantity() == 0) {
                                        Product product = messages.get(position);
                                        product.setProductQuantity(1);
                                        //product.setProductPrice(product.getProductQuantity() * product.getProductPrice());
                                        AddCartProduct(product, position);

                                        cartCountAdd();
                                    } else {
                                        Toast.makeText(getContext(), "onClickIncrease " + messages.get(position).getProductQuantity(), Toast.LENGTH_SHORT).show();
                                        Product product = messages.get(position);
                                        product.setProductQuantity(1 + product.getProductQuantity());
                                        //product.setProductPrice(product.getProductQuantity() * product.getProductPrice());
                                        UpdateCartProduct(product, position);

                                        cartCountAdd();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        if(messages.size() != 0 && messages != null) {
                            mAdapter = new CustomerAddProductAdapter(getActivity(), messages, listener);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            empty_view.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            empty_view.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });

        checkStatus();
        return view;
    }


    public void checkStatus() {
        FirebaseUser user = fireAuth.getCurrentUser();
        DocumentReference fireRefe = fireStore.collection("orders").document(user.getUid());
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
                            Log.d("TAG", "Add New Msg: " + dc.getDocument().getId());
                            break;*/
                        case MODIFIED:
                            if (dc.getDocument() != null) {
                                Product cartProduct = dc.getDocument().toObject(Product.class);
                                for (final ListIterator<Product> i = messages.listIterator(); i.hasNext();) {
                                    final Product element = i.next();
                                    if(element.getProductId().equals(cartProduct.getProductId())) {
                                        i.set(cartProduct);
                                    }
                                }
                                mAdapter.updateDataVal(messages);
                                mAdapter.notifyDataSetChanged();
                            }
                            break;

                        case REMOVED:
                            Product cartProduct = dc.getDocument().toObject(Product.class);
                            cartProduct.setProductQuantity(0);
                            for (final ListIterator<Product> i = messages.listIterator(); i.hasNext();) {
                                final Product element = i.next();
                                if(element.getProductId().equals(cartProduct.getProductId())) {
                                    i.set(cartProduct);
                                }
                            }
                            mAdapter.updateDataVal(messages);
                            mAdapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
    }





    private void AddCartProduct(Product selectProduct, final int indPosition) {
        if (NetUtils.isNetworkAvailable(getActivity())) {
            final FieldValue productCreatedDate = FieldValue.serverTimestamp();
            OrdersItem ordersItem =  new OrdersItem();
            ordersItem.setProductId(selectProduct.getProductId());
            ordersItem.setProductImg(selectProduct.getProductImg());
            ordersItem.setProductCategory(selectProduct.getProductCategory());
            ordersItem.setProductName(selectProduct.getProductName());

            ordersItem.setProductPacking(selectProduct.getProductPacking());
            ordersItem.setProductPrice(selectProduct.getProductPrice());
            ordersItem.setProductQuantity(selectProduct.getProductQuantity());

            //ordersItem.setProductDeliveryChange(selectProduct.getProductDeliveryChange());

            ordersItem.setProductItemTotal(String.valueOf(
                    (Float.valueOf(selectProduct.getProductQuantity()) *
                            Float.valueOf(selectProduct.getProductPrice()))));

            ordersItem.setProductTotalAmount(Float.valueOf(ordersItem.getProductItemTotal()));
                   // + Float.valueOf(selectProduct.getProductDeliveryChange()));

            ordersItem.setProductCreatedDate(productCreatedDate);

            FirebaseUser user = fireAuth.getCurrentUser();

            DocumentReference fireRefe = fireStore.collection("orders").document(user.getUid());
            //collectReference = fireStore.collection("orders").document(user.getUid()).collection(fireReference.getId());
            //CollectionReference firee = fireRefe.collection(fireRefe.getId());
            DocumentReference firee = fireRefe.collection(fireRefe.getId()).document(selectProduct.getProductId());
            ordersItem.setDocId(firee.getId());
            ordersItem.setUuId(user.getUid());
            firee.set(ordersItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    messages.set(indPosition,selectProduct);
                    mAdapter.updatePositionData(selectProduct.getProductQuantity(),indPosition);
                }
            });

        } else {
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }

    public void UpdateCartProduct(Product selectProduct, final int indPosition){
        if (NetUtils.isNetworkAvailable(getActivity())) {

            FirebaseUser user = fireAuth.getCurrentUser();
            DocumentReference fireRefe = fireStore.collection("orders").document(user.getUid());
            DocumentReference firee = fireRefe.collection(fireRefe.getId()).document(selectProduct.getProductId());

            String itemTotal = String.valueOf(
                    (Float.valueOf(selectProduct.getProductQuantity()) *
                            Float.valueOf(selectProduct.getProductPrice())));

            float totalAmount = (Float.valueOf(selectProduct.getProductQuantity()) *
                    Float.valueOf(selectProduct.getProductPrice()));
                    //+ Float.valueOf(selectProduct.getProductDeliveryChange());

            firee.update("productItemTotal",itemTotal,
                    "productQuantity",selectProduct.getProductQuantity(),
                    "productTotalAmount",totalAmount).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    messages.set(indPosition,selectProduct);
                    mAdapter.updatePositionData(selectProduct.getProductQuantity(),indPosition);
                }
            });
        } else {
            Utility.displayDialog(getActivity(), getString(R.string.common_no_internet), false);
        }
    }

    private void removeProduct(String id, final int indPosition){
        //Toast.makeText(getContext(), "delete " + id, Toast.LENGTH_SHORT).show();
        FirebaseUser user = fireAuth.getCurrentUser();
        DocumentReference fireRefe = fireStore.collection("orders").document(user.getUid());
        DocumentReference firee = fireRefe.collection(fireRefe.getId()).document(id);
        firee.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully deleted!");
                Product product = messages.get(indPosition);
                product.setProductQuantity(0);
                messages.set(indPosition, product);
                mAdapter.updatePositionData(0,indPosition);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error deleting document", e);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cart, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_addcart:
                Intent intent = new Intent(getActivity(), ProductCartDetailsActivity.class);
                startActivityForResult(intent,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem itemData = menu.findItem(R.id.action_addcart);
        //actionView = itemData.getActionView() as CartCounterActionView
        rootView = (CartCounterActionView) itemData.getActionView();
        rootView.setItemData(menu, itemData);
        //rootView.setCount(cartCount);
        super.onPrepareOptionsMenu(menu);
    }

    public void cartCountAdd(){
        cartCount = cartCount + 1;
        rootView.setCount(cartCount);
    }

    public void cartCountRemove(){
        cartCount = cartCount - 1;
        rootView.setCount(cartCount);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            String strPurchase = data.getStringExtra("purchase");
            if(!TextUtils.isEmpty(strPurchase) && strPurchase.equals("success")){

                for (final ListIterator<Product> i = messages.listIterator(); i.hasNext();) {
                    final Product element = i.next();
                    if(element.getProductQuantity() != 0) {
                        element.setProductQuantity(0);
                        i.set(element);
                    }
                }

                mAdapter.updateDataVal(messages);
                mAdapter.notifyDataSetChanged();

                cartCount = 0;
                rootView.setCount(cartCount);
            }
        }
        cartCount = 0;
        for (Product ip : messages) {
            cartCount += ip.getProductQuantity();
        }
        rootView.setCount(cartCount);
    }
}
