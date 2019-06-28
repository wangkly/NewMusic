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
        intentFilter.addAction("com.newmusic.wangkly.newmusic.MainActivity.changeMedia");
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
//        ListView listview = view.findViewById(R.id.playlist);
//        mRecyclerView = view.findViewById(R.id.playlist);

//        audioList = this.getAudioList();

//        PlaylistSimpleAdapter playlistSimpleAdapter = new PlaylistSimpleAdapter(view.getContext(),audioList,
//                R.layout.audio_item,new String[]{"title","artist"},new int[]{R.id.audio_title,R.id.audio_author});

//        listview.setAdapter(playlistSimpleAdapter);
//
//        listview.setOnItemClickListener(new ListView.OnItemClickListener(){
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ListView listview = (ListView) parent;
//
//                Map<String,Object> data = (Map<String, Object>) listview.getItemAtPosition(position);
//
//                ContentValues values = new ContentValues();
//
//                values.put("title",(String)data.get("title"));
//                values.put("position",position);
//                values.put("duration",(int) data.get("duration"));
//                values.put("uri",(String)data.get("data"));
//                values.put("artist",(String)data.get("artist"));
//                values.put("albumArt",(String)data.get("albumArt"));
//
//                Cursor cursor = mainActivity.dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);
//                if(cursor.getCount() > 0){
//                    mainActivity.dbHelper.getWritableDatabase().delete("playing",null,null);
//                }
//
//                mainActivity.dbHelper.getWritableDatabase().insert("playing",null,values);
//
//
//                int duration=(int) data.get("duration");
//                String title =(String)data.get("title");
//                String albumArt =(String)data.get("albumArt");
//                String uri = (String)data.get("data");
//                mainActivity.play(uri,albumArt,title,duration,position);
//            }
//        });


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

//                int duration=item.getDuration();
//                String title = item.getTitle();
//                String albumArt =item.getAlbumArt();
                String uri = item.getData();
                mainActivity.play(uri);

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
