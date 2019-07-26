package com.newmusic.wangkly.newmusic.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.newmusic.wangkly.newmusic.R;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

    private Context mContext;

    private List<Integer> list;

    public MyPagerAdapter(Context context,List<Integer> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return false;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
       View view = View.inflate(mContext, R.layout.img_view,null);

        ImageView img = view.findViewById(R.id.mImg);

        img.setImageResource(list.get(position));

        container.addView(view);
        return view;

    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
