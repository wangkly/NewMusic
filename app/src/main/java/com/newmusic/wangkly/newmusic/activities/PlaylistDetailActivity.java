package com.newmusic.wangkly.newmusic.activities;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.adapter.PlaylistDetailAdapter;
import com.newmusic.wangkly.newmusic.beans.OnlineSongItem;
import com.newmusic.wangkly.newmusic.beans.PlaylistItem;
import com.newmusic.wangkly.newmusic.interfaces.QueryResultListener;
import com.newmusic.wangkly.newmusic.tasks.OnlineListTask;

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


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();

            int postion = viewHolder.getAdapterPosition();

            OnlineSongItem item  = detailAdapter. getItemAtPosition(postion);

            long songId = item.getId();

            StringBuilder sb = new StringBuilder("http://172.19.8.35:3000/song/url?id=");

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
                            Intent intent = new Intent();
                            intent.setAction("com.newmusic.wangkly.newmusic.MainActivity.onlineMusic");
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

        localBroadcastManager = LocalBroadcastManager.getInstance(PlaylistDetailActivity.this);


        detail_list = findViewById(R.id.detail_list);

        Intent intent = getIntent();

        long listId = intent.getLongExtra("listId",-1l);


        String cover = intent.getStringExtra("cover");


        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaylistDetailActivity.this);

        detail_list.setLayoutManager(layoutManager);


        detailAdapter = new PlaylistDetailAdapter(new ArrayList<OnlineSongItem>(),cover,clickListener);

        detail_list.setAdapter(detailAdapter);

        detail_list.addItemDecoration(new DividerItemDecoration(PlaylistDetailActivity.this,DividerItemDecoration.VERTICAL));


        StringBuilder sb = new StringBuilder("http://172.19.8.35:3000/playlist/detail?id=");


        OnlineListTask task = new OnlineListTask(listener);

        task.execute(sb.toString()+String.valueOf(listId));

    }
}
