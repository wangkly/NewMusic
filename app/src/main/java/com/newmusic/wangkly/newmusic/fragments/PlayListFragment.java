package com.newmusic.wangkly.newmusic.fragments;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.newmusic.wangkly.newmusic.MainActivity;
import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.adapter.PlainRecyclerViewAdapter;
import com.newmusic.wangkly.newmusic.beans.PlaylistItem;
import com.newmusic.wangkly.newmusic.constant.Constant;
import com.newmusic.wangkly.newmusic.listener.RecyclerViewItemTouchListener;
import com.newmusic.wangkly.newmusic.utils.MediaUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayListFragment extends Fragment {

    private List<PlaylistItem> audioList;

    private RecyclerView mRecyclerView;


    MainActivity mainActivity;

    LocalBroadcastManager localBroadcastManager;
    ChangeMediaReceiver  changeMediaReceiver;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

         mainActivity = (MainActivity) getActivity();

        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.CHANGE_MUSIC_LOCAL);
        localBroadcastManager= LocalBroadcastManager.getInstance(getActivity());
        changeMediaReceiver = new ChangeMediaReceiver();
        localBroadcastManager.registerReceiver(changeMediaReceiver,intentFilter);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.play_list_fragment,container,false);

        this.initAudioList(view);

        return view;

    }



    public  void initAudioList(View view){

        mRecyclerView = view.findViewById(R.id.playlist);

        List<PlaylistItem> audioList  = MediaUtil.getAudioList(getContext());

        final PlainRecyclerViewAdapter adapter = new PlainRecyclerViewAdapter(audioList);

        mRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        //分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));


        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemTouchListener(getContext(),
                    mRecyclerView, new RecyclerViewItemTouchListener.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                PlaylistItem item = adapter.getItemAtPosition(position);

                ContentValues values = new ContentValues();

                values.put("title",item.getTitle());
                values.put("position",position);
                values.put("duration",item.getDuration());
                values.put("uri",item.getData());
                values.put("artist",item.getArtist());
                values.put("albumArt",item.getAlbumArt());

                Cursor cursor = mainActivity.dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);
                if(cursor.getCount() > 0){
                    mainActivity.dbHelper.getWritableDatabase().delete("playing",null,null);
                }

                mainActivity.dbHelper.getWritableDatabase().insert("playing",null,values);


                String uri = item.getData();
                mainActivity.play(uri);


                //通知刷新正在播放音乐信息
                Intent intent = new Intent(Constant.MAIN_ACTIVITY_ACTION);
                intent.putExtra("type",Constant.REFRESH_PALYINGINFO);
                localBroadcastManager.sendBroadcast(intent);

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }




    class ChangeMediaReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");
            int position = intent.getIntExtra("position",0);
            if("previous".equalsIgnoreCase(type)){
                Toast.makeText(mainActivity,"broadcast receive previous",Toast.LENGTH_SHORT).show();
                findMediaAndPlay(position >= 1 ? position -1 : 0);
            }else{
                Toast.makeText(mainActivity,"broadcast receive next",Toast.LENGTH_SHORT).show();
                findMediaAndPlay(position +1 >= audioList.size() ? audioList.size()-1: position +1);
            }

        }
    }


    public void findMediaAndPlay(int position){
        PlaylistItem target = audioList.get(position);


        ContentValues values = new ContentValues();

        values.put("title",target.getTitle());
        values.put("position",position);
        values.put("duration",target.getDuration());
        values.put("uri",target.getData());
        values.put("artist",target.getArtist());
        values.put("albumArt",target.getAlbumArt());


        Cursor cursor = mainActivity.dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);
        if(cursor.getCount() > 0){
            mainActivity.dbHelper.getWritableDatabase().delete("playing",null,null);
        }

        mainActivity.dbHelper.getWritableDatabase().insert("playing",null,values);


//        int duration=target.getDuration();
//        String title =target.getTitle();
//        String albumArt =target.getAlbumArt();
        String uri = target.getData();
        mainActivity.play(uri);

    }



    @Override
    public void onDestroy() {
        localBroadcastManager.unregisterReceiver(changeMediaReceiver);
        super.onDestroy();
    }
}
