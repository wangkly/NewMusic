package com.newmusic.wangkly.newmusic.listener;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewItemTouchListener implements RecyclerView.OnItemTouchListener {


    private GestureDetectorCompat mGestureDetector;

    private ItemClickListener listener;

    public RecyclerViewItemTouchListener(Context context, final RecyclerView recyclerView, ItemClickListener itemClickListener) {

        this.listener = itemClickListener;

        this.mGestureDetector = new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){


            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                View childViewUnder = recyclerView.findChildViewUnder(e.getX(),e.getY());

                if(childViewUnder != null && listener !=null){

                    listener.onItemClick(childViewUnder,recyclerView.getChildLayoutPosition(childViewUnder));
                    return  true;
                }

                return  false;
            }


            @Override
            public void onLongPress(MotionEvent e) {

                View childViewUnder = recyclerView.findChildViewUnder(e.getX(),e.getY());

                if(childViewUnder != null && listener !=null){

                    listener.onItemClick(childViewUnder,recyclerView.getChildLayoutPosition(childViewUnder));
                }

            }
        });





    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        if(mGestureDetector.onTouchEvent(motionEvent)){

            return  true;
        }

        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }



    public interface ItemClickListener {

         void onItemClick(View view, int position);

         void onItemLongClick(View view, int position);
    }


}
