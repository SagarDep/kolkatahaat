package com.kolkatahaat.view.customer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kolkatahaat.R;
import com.kolkatahaat.adapterview.CustomerSamplePagerAdapter;

public class ProductListActivity extends AppCompatActivity {

    public static FragmentManager fragmentManager;
    private ViewPager viewpager;
    private TabLayout sliding_tabs;
    private int indicatorWidth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_list);

        viewpager = findViewById(R.id.viewpager);
        sliding_tabs = findViewById(R.id.sliding_tabs);
        fragmentManager=getSupportFragmentManager();


        bindViewPagerAdapter(viewpager);
        bindViewPagerTabs(sliding_tabs, viewpager);
        tabSettings();
    }

    public void bindViewPagerAdapter(final ViewPager view) {
        final CustomerSamplePagerAdapter adapter = new CustomerSamplePagerAdapter(view.getContext(), fragmentManager);
        view.setAdapter(adapter);
    }

    public void bindViewPagerTabs(final TabLayout view, final ViewPager pagerView) {
        view.setupWithViewPager(pagerView, true);

    }

    private void tabSettings() {

        sliding_tabs.post(new Runnable() {
            @Override
            public void run() {
                indicatorWidth = sliding_tabs.getWidth() / sliding_tabs.getTabCount();
                /*FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                indicatorParams.width = indicatorWidth;
                indicator.setLayoutParams(indicatorParams);*/
            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetP
            ) {
                /*FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                float translationOffset = (positionOffset + position) * indicatorWidth;
                params.leftMargin = (int) translationOffset;
                indicator.setLayoutParams(params);*/
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

   
}
