package com.kolkatahaat.view.customer;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.CustomerSamplePagerAdapter;
import com.kolkatahaat.view.customer.fragments.ClothingFragment;
import com.kolkatahaat.view.customer.fragments.EatableFragment;
import com.kolkatahaat.view.customer.fragments.PujaItemsFragment;

public class ProductListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;

    private EatableFragment eatableFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_list);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tab_layout);

        eatableFragment = new EatableFragment();
        loadFragment(eatableFragment);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;

                switch (tab.getPosition()) {
                    case 0:
                        fragment = new EatableFragment();
                        break;

                    case 1:
                        fragment = new PujaItemsFragment();
                        break;

                    case 2:
                        fragment = new ClothingFragment();
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
}
