package com.kolkatahaat.view.customer;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kolkatahaat.R;
import com.kolkatahaat.model.OrdersItem;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.model.QuantityPrice;
import com.kolkatahaat.utills.CartCounterActionView;
import com.kolkatahaat.utills.NetUtils;
import com.kolkatahaat.utills.Utility;

import java.util.ArrayList;

public class ProductPurchaseActivity extends AppCompatActivity {


    public final String TAG = ProductPurchaseActivity.this.getClass().getSimpleName();

    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;
    private DocumentReference fireReference;
    private CollectionReference collectReference;

    private Toolbar toolbar;

    private ImageView imgProduct;
    private TextView txtProductName;
    private TextView txtProductDiscount;
    private TextView txtProductDescription;
    private ChipGroup chipGroup;
    //private TextView txtProductDelivery;
    private TextView txtProductTotal;
    private FloatingActionButton fabAdd;
    private Button btnAddToCart;

    private String mProductIde;
    private Product selectProduct;
    private ArrayList<QuantityPrice> quantityPrices;
    private OrdersItem ordersItem;

    private int cartCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_purchase);

        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        selectProduct = new Product();
        quantityPrices = new ArrayList<>();
        ordersItem = new OrdersItem();

        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_PRODUCT_ID")) {
            mProductIde = getIntent().getStringExtra("EXTRA_PRODUCT_ID");
            if (!TextUtils.isEmpty(mProductIde) && mProductIde != null) {
                getProductDetails(mProductIde);
            }
        }

        init();
    }

    public void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtProductDiscount = findViewById(R.id.txtProductDiscount);
        txtProductDescription = findViewById(R.id.txtProductDescription);
        chipGroup = findViewById(R.id.chipGroup);
        //txtProductDelivery = findViewById(R.id.txtProductDelivery);
        txtProductTotal = findViewById(R.id.txtProductTotal);
        fabAdd = findViewById(R.id.fabAdd);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //++cartCount;
                //CartCounterActionView.setCountStep(ProductPurchaseActivity.this, 1);

                Intent intent = new Intent(ProductPurchaseActivity.this, ProductCartDetailsActivity.class);
                startActivityForResult(intent,1);
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProduct();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_addcart:
                Intent intent = new Intent(ProductPurchaseActivity.this, ProductCartDetailsActivity.class);
                startActivityForResult(intent,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemData = menu.findItem(R.id.action_addcart);
        //actionView = itemData.getActionView() as CartCounterActionView
        cartCount = 0;
        getProductDetails();
        CartCounterActionView rootView = (CartCounterActionView) itemData.getActionView();
        rootView.setItemData(menu, itemData);
        rootView.setCount(cartCount);
        return super.onPrepareOptionsMenu(menu);
    }


    private void getProductDetails(String mProductIde) {
        fireReference = fireStore.collection("products").document(mProductIde);
        fireReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    selectProduct = documentSnapshot.toObject(Product.class);
                    setProductData(selectProduct);
                } else {
                    Log.d(TAG, "onSuccess: LIST EMPTY");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String strPurchase = data.getStringExtra("purchase");
                if(strPurchase.equals("success")){

                }
            }
        }
    }

    public void setProductData(Product mSelectProduct) {
        Glide.with(ProductPurchaseActivity.this).load(mSelectProduct.getProductImg())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProduct);
        txtProductName.setText(mSelectProduct.getProductName());
        //txtProductDelivery.setText(mSelectProduct.getProductDeliveryChange());
        //setTag(mSelectProduct.getProductQuantityPrice());
    }

    /*public void setTag(final ArrayList<QuantityPrice> tagList) {
        for (int index = 0; index < tagList.size(); index++) {
            final String tagName = tagList.get(index).getQuantityName();
            //final Chip chip = new Chip(this);
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.row_chip_view, chipGroup, false);

            final String tagDisplay = tagList.get(index).getQuantityName() + " , " + tagList.get(index).getPrice();

            chip.setId(index);
            chip.setTag(index);
            chip.setText(tagDisplay);
            chip.setCloseIconResource(android.R.drawable.ic_menu_close_clear_cancel);
            // chip.setChipBackgroundColorResource(R.drawable.bg_chip_state_list);
            //chip.setCloseIconVisible(true);
            //chip.setChecked(true);
            chip.setClickable(true);
            //chip.setFocusable(true);
            chip.setCheckedIconVisible(true);
            chip.setTextColor(getResources().getColor(R.color.colorBlack));


            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    float total = 0;
                    for (int i = 0; i < getCheckedChipIds().size(); i++) {
                        Log.i("inside if ", i + " chip = " + getCheckedChipIds().get(i).getQuantityName());
                        total = total + getCheckedChipIds().get(i).getPrice();
                    }
                    txtProductTotal.setText(String.valueOf(total));
                }
            });
            chipGroup.addView(chip);
        }
        chipGroup.invalidate();
    }*/

    /*public ArrayList<QuantityPrice> getCheckedChipIds() {
        ArrayList<QuantityPrice> mQuantityPrices = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            //Log.e("outside if ", i+ " chip = " + chip.getText().toString());
            if (chip.isChecked()) {
                //Log.e("inside if ", i + " chip = " + chip.getText().toString());
                QuantityPrice quantityPrice = new QuantityPrice();
                quantityPrice.setQuantityName(selectProduct.getProductQuantityPrice().get(i).getQuantityName());
                quantityPrice.setPrice(selectProduct.getProductQuantityPrice().get(i).getPrice());
                mQuantityPrices.add(quantityPrice);
            }
        }

        return mQuantityPrices;
    }*/


    private void uploadProduct() {

        if (NetUtils.isNetworkAvailable(ProductPurchaseActivity.this)) {

            //if (getCheckedChipIds().size() != 0) {
                final FieldValue productCreatedDate = FieldValue.serverTimestamp();

                ordersItem.setProductId(selectProduct.getProductId());
                ordersItem.setProductImg(selectProduct.getProductImg());
                ordersItem.setProductCategory(selectProduct.getProductCategory());
                ordersItem.setProductName(selectProduct.getProductName());
                //ordersItem.setProductQuantityPrice(getCheckedChipIds());
                //ordersItem.setProductDeliveryChange(selectProduct.getProductDeliveryChange());
                ordersItem.setProductItemTotal(txtProductTotal.getText().toString().trim());
                /*ordersItem.setProductTotalAmount(Float.valueOf(txtProductTotal.getText().toString().trim()) +
                        Float.valueOf(selectProduct.getProductDeliveryChange()));*/
                ordersItem.setProductCreatedDate(productCreatedDate);

                FirebaseUser user = fireAuth.getCurrentUser();


                DocumentReference fireRefe = fireStore.collection("orders").document(user.getUid());
                //collectReference = fireStore.collection("orders").document(user.getUid()).collection(fireReference.getId());
                //CollectionReference firee = fireRefe.collection(fireRefe.getId());
                DocumentReference firee = fireRefe.collection(fireRefe.getId()).document();
                ordersItem.setDocId(firee.getId());
                ordersItem.setUuId(user.getUid());
                firee.set(ordersItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        getProductDetails();
                    }
                });

            /*} else {
                Utility.displayDialog(ProductPurchaseActivity.this, "Please try again", false);
            }*/

        } else {
            Utility.displayDialog(ProductPurchaseActivity.this, getString(R.string.common_no_internet), false);
        }
    }


    private void getProductDetails() {
        FirebaseUser user = fireAuth.getCurrentUser();
        CollectionReference fireRefe = fireStore.collection("orders").document(user.getUid()).collection(user.getUid());
        fireRefe.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                cartCount = 0;
                cartCount = queryDocumentSnapshots.size();
                CartCounterActionView.setCountStep(ProductPurchaseActivity.this, cartCount);
                Log.i("inside if ", ""+cartCount);
            }
        });
    }
}
