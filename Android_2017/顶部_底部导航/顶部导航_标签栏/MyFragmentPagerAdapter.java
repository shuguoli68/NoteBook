package com.example.mydemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Admin on 2018/2/1.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{

    private List<String> tabTitles;
    private List<Fragment> tabFragments;

    public MyFragmentPagerAdapter(FragmentManager fm,List<String> tabTitles,List<Fragment> tabFragments) {
        super(fm);
        this.tabFragments = tabFragments;
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return tabFragments.get(position);
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
