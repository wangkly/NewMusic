package com.newmusic.wangkly.newmusic;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentPageAdapter extends FragmentPagerAdapter {

    List<Fragment> listfragment;

    FragmentManager fragmentManager ;


    public MyFragmentPageAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);

        fragmentManager = fm;

        listfragment = fragments;

    }

    @Override
    public Fragment getItem(int i) {
        return listfragment.get(i);
    }

    @Override
    public int getCount() {
        return listfragment.size();
    }
}
