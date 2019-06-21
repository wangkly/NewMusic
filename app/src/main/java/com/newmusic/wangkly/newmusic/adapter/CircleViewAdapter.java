package com.newmusic.wangkly.newmusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.newmusic.wangkly.newmusic.fragments.CircleViewFragment;

import java.util.List;

public class CircleViewAdapter extends FragmentStatePagerAdapter {


    private List<CircleViewFragment> mFragments;

    public CircleViewAdapter(List<CircleViewFragment> fragments,FragmentManager fm) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragments.get(i);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }




}
