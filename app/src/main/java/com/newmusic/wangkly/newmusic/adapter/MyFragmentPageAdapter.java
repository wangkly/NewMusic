package com.newmusic.wangkly.newmusic.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentPageAdapter extends FragmentPagerAdapter {

    List<String> tabTiltles;

    List<Fragment> listfragment;

    FragmentManager fragmentManager ;


    public MyFragmentPageAdapter(FragmentManager fm,List<Fragment> fragments,List<String> titles) {
        super(fm);

        fragmentManager = fm;

        listfragment = fragments;

        tabTiltles =titles;

    }

    @Override
    public Fragment getItem(int i) {
        return listfragment.get(i);
    }

    @Override
    public int getCount() {
        return listfragment.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return tabTiltles.get(position);
    }
}
