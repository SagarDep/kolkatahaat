package com.kolkatahaat.adapterview;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class CustomerSamplePagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentTitle = new ArrayList<>();

    //private int[] TABS;
    private Context mContext;

    /*public CustomerSamplePagerAdapter(final Context context, final FragmentManager fm) {
        super(fm);
        mContext = context.getApplicationContext();
        TABS = new int[]{FIRST_TAB, SECOND_TAB};
    }*/
    public CustomerSamplePagerAdapter(final Context context, @NonNull final FragmentManager fm, int behavior) {
        super(fm, behavior);
        mContext = context.getApplicationContext();
    }

    public void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        fragmentTitle.add(title);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

   /* @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (TABS[position]) {
            case FIRST_TAB:
                bundle.putString("param1", "FIRST TAB");
               *//* BlankFragment blankFragment = new BlankFragment();
                blankFragment.setArguments(bundle);
                return blankFragment;*//*

            case SECOND_TAB:
                bundle.putString("param1", "SECOND TAB");
                *//*BlankFragment blankFragment1 = new BlankFragment();
                blankFragment1.setArguments(bundle);
                return blankFragment1;*//*

        }
        return null;

    }

    @Override
    public int getCount() {
        return TABS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (TABS[position]) {
            case FIRST_TAB:
                return "FIRST TAB";
            case SECOND_TAB:
                return "SECOND TAB";
        }
        return null;
    }*/
}

