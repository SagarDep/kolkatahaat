package com.kolkatahaat.view.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.kolkatahaat.R;
import com.kolkatahaat.view.customer.fragments.ClothingFragment;
import com.kolkatahaat.view.customer.fragments.GroceryFragment;
import com.kolkatahaat.view.customer.fragments.OthersFragment;
import com.kolkatahaat.view.customer.fragments.PujaItemsFragment;

public class ProductListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;

    private GroceryFragment groceryFragment;
    private PujaItemsFragment pujaItemsFragment;
    private ClothingFragment clothingFragment;
    private OthersFragment othersFragment;
    private int selectTab = 0;

   /* private int cartCount = 0;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_list);


        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_TAB_POSITION")) {
            selectTab = getIntent().getIntExtra("EXTRA_TAB_POSITION",0);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        if(selectTab == 0) {
            groceryFragment = new GroceryFragment();
            loadFragment(groceryFragment);
            tabLayout.getTabAt(selectTab).select();
        } else if(selectTab == 1) {
            pujaItemsFragment = new PujaItemsFragment();
            loadFragment(pujaItemsFragment);
            tabLayout.getTabAt(selectTab).select();
        } else if(selectTab == 2) {
            clothingFragment = new ClothingFragment();
            loadFragment(clothingFragment);
            tabLayout.getTabAt(selectTab).select();
        } else {
            othersFragment = new OthersFragment();
            loadFragment(othersFragment);
            tabLayout.getTabAt(selectTab).select();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;

                switch (tab.getPosition()) {
                    case 0:
                        fragment = new GroceryFragment();
                        break;

                    case 1:
                        fragment = new PujaItemsFragment();
                        break;

                    case 2:
                        fragment = new ClothingFragment();
                        break;

                    case 3:
                        fragment = new OthersFragment();
                        break;
                }

               /* FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();*/
                loadFragment(fragment);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
            fts.replace(R.id.content_frame, fragment);
            fts.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fts.commit();
            return true;
        }
        return false;
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

   /* @Override
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
                Intent intent = new Intent(ProductListActivity.this, ProductCartDetailsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemData = menu.findItem(R.id.action_addcart);
        //actionView = itemData.getActionView() as CartCounterActionView
        cartCount = 2;
        CartCounterActionView rootView = (CartCounterActionView) itemData.getActionView();
        rootView.setItemData(menu, itemData);
        rootView.setCount(cartCount);
        return super.onPrepareOptionsMenu(menu);
    }*/
}
