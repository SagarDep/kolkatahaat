package com.kolkatahaat.adapterview;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CustomerSamplePagerAdapter extends FragmentStatePagerAdapter {
    private static final int FIRST_TAB = 0;
    private static final int SECOND_TAB = 1;

    private int[] TABS;
    private Context mContext;

    public CustomerSamplePagerAdapter(final Context context, final FragmentManager fm) {
        super(fm);
        mContext = context.getApplicationContext();
        TABS = new int[]{FIRST_TAB, SECOND_TAB};
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (TABS[position]) {
            case FIRST_TAB:
                bundle.putString("param1", "FIRST TAB");
               /* BlankFragment blankFragment = new BlankFragment();
                blankFragment.setArguments(bundle);
                return blankFragment;*/

            case SECOND_TAB:
                bundle.putString("param1", "SECOND TAB");
                /*BlankFragment blankFragment1 = new BlankFragment();
                blankFragment1.setArguments(bundle);
                return blankFragment1;*/

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
    }
}

