package com.newmusic.wangkly.newmusic.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.tasks.OnlineListTask;

public class OnlinePlayListFragment extends Fragment {


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

           Bundle bundle = msg.getData();

            String resp =  bundle.getString("resp");

            System.out.println(resp);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

            View view  = inflater.inflate(R.layout.online_play_list,container,false);

            OnlineListTask task = new OnlineListTask(handler);



            task.execute("http://172.19.8.49:3000/user/playlist?uid=32953014");

            return view;
    }





}
