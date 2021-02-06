package com.kolkatahaat.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.kolkatahaat.R;
import com.kolkatahaat.fragments.AboutUsFragment;
import com.kolkatahaat.fragments.EditProfileFragment;
import com.kolkatahaat.model.Product;
import com.kolkatahaat.model.Users;
import com.kolkatahaat.utills.CartCounterActionView;
import com.kolkatahaat.utills.SharedPrefsUtils;
import com.kolkatahaat.view.customer.ProductCartDetailsActivity;
import com.kolkatahaat.view.customer.fragments.CustomerProductCategoryFragment;
import com.kolkatahaat.view.customer.fragments.OrdersFragment;

import java.util.ListIterator;

public class HomeCustomerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Toolbar toolbar;
    private DrawerLayout drawer;
    private FirebaseAuth fireAuth;
    private FirebaseFirestore fireStore;

    private CartCounterActionView rootView;
    private int cartCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);

        fireStore = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null); //disable tint on each icon to use color icon svg
        if (navigationView.getHeaderCount() > 0) {
            View headerLayout = navigationView.getHeaderView(0);

            Gson gson = new Gson();
            Object userDetial = SharedPrefsUtils.getFromPrefs(HomeCustomerActivity.this, SharedPrefsUtils.USER_DETAIL, "");
            Users obj = gson.fromJson(String.valueOf(userDetial), Users.class);

            TextView txtName = headerLayout.findViewById(R.id.txtName);
            TextView txtEmailAddress = headerLayout.findViewById(R.id.txtEmailAddress);

            txtName.setText(obj.getUserName());
            txtEmailAddress.setText(obj.getUserEmail());
        }

        loadFragment(new CustomerProductCategoryFragment());
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.content_frame, fragment);
            //fts.addToBackStack("optional tag");
            fts.commit();
            return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.nav_product:
                fragment = new CustomerProductCategoryFragment(); //<----- enable this line
                break;
            case R.id.nav_users_list:
                //fragment = new AdminAllUserListFragment();  //<----- enable this line
                break;

            case R.id.nav_users_orders:
                fragment = new OrdersFragment();
                //fragment = new EditProfileFragment();  //<----- enable this line
                break;

            case R.id.nav_profile:
                fragment = new EditProfileFragment();  //<----- enable this line
                break;

            case R.id.nav_about_us:
                fragment = new AboutUsFragment();  //<----- enable this line
                break;

            case R.id.nav_logout:
                fireAuth.signOut();
                SharedPrefsUtils.removeFromPrefs(HomeCustomerActivity.this, SharedPrefsUtils.USER_DETAIL);
                Intent intent = new Intent(HomeCustomerActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return loadFragment(fragment);
    }


    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }*/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_addcart:
                Intent intent = new Intent(HomeCustomerActivity.this, ProductCartDetailsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemData = menu.findItem(R.id.action_addcart);
        //actionView = itemData.getActionView() as CartCounterActionView
        rootView = (CartCounterActionView) itemData.getActionView();
        rootView.setItemData(menu, itemData);
        return super.onPrepareOptionsMenu(menu);
    }

    public void getCountProduct(){
        try {
            cartCount = 0;
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
                        }
                        rootView.setCount(cartCount);
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCountProduct();
    }
}
