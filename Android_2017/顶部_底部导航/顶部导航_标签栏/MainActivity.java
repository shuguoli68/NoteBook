package com.example.mydemo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.mydemo.adapter.MyFragmentPagerAdapter;
import com.example.mydemo.ui.fragment.OneFragment;
import com.example.mydemo.ui.fragment.ThreeFragment;
import com.example.mydemo.ui.fragment.TwoFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_tabs)
    TabLayout mainTabs;
    @BindView(R.id.main_pager)
    ViewPager mainPager;

    private List<String> tabTitles;
    private List<Fragment> tabFragments;
    MyFragmentPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }

    private void initData(){
        mainTabs.setTabMode(TabLayout.MODE_FIXED);
        mainTabs.setTabTextColors(ContextCompat.getColor(this, R.color.black), ContextCompat.getColor(this, R.color.green));
        mainTabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.red));
        ViewCompat.setElevation(mainTabs,10);
        mainTabs.setupWithViewPager(mainPager);

        tabTitles = new ArrayList<>();
        for (int i=0;i<3;i++){
            tabTitles.add("tab"+i);
        }
        tabFragments = new ArrayList<>();
        tabFragments.add(new OneFragment());
        tabFragments.add(new TwoFragment());
        tabFragments.add(new ThreeFragment());

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),tabTitles,tabFragments);
        mainPager.setAdapter(adapter);
    }
}
