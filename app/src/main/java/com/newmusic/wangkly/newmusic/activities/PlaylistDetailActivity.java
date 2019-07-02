package com.newmusic.wangkly.newmusic.activities;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.adapter.PlaylistDetailAdapter;
import com.newmusic.wangkly.newmusic.beans.OnlineSongItem;
import com.newmusic.wangkly.newmusic.beans.PlaylistItem;
import com.newmusic.wangkly.newmusic.constant.Constant;
import com.newmusic.wangkly.newmusic.interfaces.QueryResultListener;
import com.newmusic.wangkly.newmusic.service.MusicService;
import com.newmusic.wangkly.newmusic.tasks.OnlineListTask;
import com.newmusic.wangkly.newmusic.utils.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";

    private RecyclerView detail_list;


    private PlaylistDetailAdapter detailAdapter;

    private LocalBroadcastManager localBroadcastManager;


    private PlaylistDetailReceiver receiver;


    private DBHelper dbHelper;


    private ImageView mini_img;

    private TextView mini_playing_title;

    private ImageButton mini_playing_btn;


    public MusicService.MyBinder myBinder;


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();

            final int position = viewHolder.getAdapterPosition();

            final OnlineSongItem item  = detailAdapter. getItemAtPosition(position);

            long songId = item.getId();

            StringBuilder sb = new StringBuilder(Constant.HOST_URL);

            sb.append("/song/url?id=");

            OnlineListTask task = new OnlineListTask(new QueryResultListener() {
                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray data = json.getJSONArray("data");
                        JSONObject obj =  data.getJSONObject(0);
                        String url = obj.getString("url");

                        Log.i(TAG,url);

                        if(null != url){


                            //保存当前播放歌曲信息
                            ContentValues values = new ContentValues();
                            values.put("title",item.getName());
                            values.put("position",position);
                            values.put("duration",0);
                            values.put("uri",url);
                            values.put("artist",item.getAuthorName());
                            values.put("albumArt",item.getAlbumPicUrl());


                            Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);
                            if(cursor.getCount() > 0){
                                dbHelper.getWritableDatabase().delete("playing",null,null);
                            }

                            dbHelper.getWritableDatabase().insert("playing",null,values);

//                            //通知刷新正在播放音乐信息
//                            Intent refresh = new Intent(Constant.MAIN_ACTIVITY_ACTION);
//                            intent.putExtra("type",Constant.REFRESH_PALYINGINFO);
//                            localBroadcastManager.sendBroadcast(refresh);



                            //获取当前歌曲播放列表
                            List<OnlineSongItem> mlist =detailAdapter.getmList();
                            //存到数据库中


                            Intent intent = new Intent(Constant.MAIN_ACTIVITY_ACTION);
                            intent.putExtra("type",Constant.ONLINE_MUSIC_PLAY_ACTION);
                            intent.putExtra("url",url);
                            localBroadcastManager.sendBroadcast(intent);



                        }else {
                            Toast.makeText(PlaylistDetailActivity.this,"无法播放",Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(String result) {

                }
            });


            task.execute(sb.toString()+String.valueOf(songId));

        }
    };



    private QueryResultListener listener = new QueryResultListener() {


        @Override
        public void onSuccess(String result) {
            Log.i(TAG,result);

            List<OnlineSongItem> list = new ArrayList<>();

            try {
                JSONObject json = new JSONObject(result);
                JSONObject playlist = json.getJSONObject("playlist");

                JSONArray tracks = playlist.getJSONArray("tracks");

                for (int i = 0 ;i < tracks.length(); i++){

                    JSONObject item = tracks.getJSONObject(i);

                    OnlineSongItem song = new OnlineSongItem();

                    song.setId(item.getLong("id"));
                    song.setName(item.getString("name"));
                    JSONArray ar = item.getJSONArray("ar");
                    JSONObject ar0 = ar.getJSONObject(0);
                    song.setAuthorName(ar0.getString("name"));

                    JSONObject al = item.getJSONObject("al");
                    song.setAlbumName(al.getString("name"));
                    song.setAlbumPicUrl(al.getString("picUrl"));

                    list.add(song);

                }


                detailAdapter.addItems(list);

                detailAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onFailure(String result) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        detail_list = findViewById(R.id.detail_list);
        mini_img = findViewById(R.id.mini_img);
        mini_playing_title = findViewById(R.id.mini_playing_title);
        mini_playing_btn = findViewById(R.id.mini_playing_btn);

        dbHelper = DBHelper.getInstance(PlaylistDetailActivity.this);


        receiver = new PlaylistDetailReceiver();

        localBroadcastManager = LocalBroadcastManager.getInstance(PlaylistDetailActivity.this);


        Intent intent = getIntent();

        Bundle bundle =intent.getExtras();

        myBinder= (MusicService.MyBinder) bundle.getBinder("myBinder");

        long listId = bundle.getLong("listId",-1l);

        String cover = bundle.getString("cover");

        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaylistDetailActivity.this);

        detail_list.setLayoutManager(layoutManager);


        detailAdapter = new PlaylistDetailAdapter(new ArrayList<OnlineSongItem>(),cover,clickListener);

        detail_list.setAdapter(detailAdapter);

        detail_list.addItemDecoration(new DividerItemDecoration(PlaylistDetailActivity.this,DividerItemDecoration.VERTICAL));


        StringBuilder sb = new StringBuilder(Constant.HOST_URL);

        sb.append("/playlist/detail?id=");


        OnlineListTask task = new OnlineListTask(listener);

        task.execute(sb.toString()+String.valueOf(listId));


        IntentFilter filter = new IntentFilter();

        filter.addAction(Constant.MAIN_ACTIVITY_ACTION);

        localBroadcastManager.registerReceiver(receiver,filter);



//        refreshPlayingInfo(false);

    }



    //更新播放窗口
    class  PlaylistDetailReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");


            switch (type){

                case Constant.REFRESH_PALYINGINFO:

                    refreshPlayingInfo(false);

                    break;


                default:


                    break;
            }


        }
    }



    public void refreshPlayingInfo(Boolean initFlag){
        String title="";
        String uri="";
        String albumArt = null;

//        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,"Idkey=?",new String[]{"1"},null,null,null);
        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);

        if(cursor.moveToFirst()){

            do {
                title = cursor.getString(cursor.getColumnIndex("title"));
                uri = cursor.getString(cursor.getColumnIndex("uri"));
                albumArt = cursor.getString(cursor.getColumnIndex("albumArt"));

            } while (cursor.moveToNext());

        }


        if(null != uri && !"".equals(uri)){

            if(initFlag){
                myBinder.initMediaPlayer(uri);
            }
            //是否正在播放
            if(myBinder.isPlaying()){
                mini_playing_btn.setImageResource(R.drawable.ic_pause);
            }else{
                mini_playing_btn.setImageResource(R.drawable.ic_play);
            }

            if(null == albumArt){
                mini_img.setImageResource(R.drawable.music_img);
            }else{
                Bitmap bm = BitmapFactory.decodeFile(albumArt);
                mini_img.setImageBitmap(bm);
            }

            mini_playing_title.setText(title);

        }

    }




}
