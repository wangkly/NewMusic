package com.newmusic.wangkly.newmusic.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.adapter.PlaylistDetailAdapter;
import com.newmusic.wangkly.newmusic.beans.PlaylistItem;

import java.util.ArrayList;

public class PlaylistDetailActivity extends AppCompatActivity {

    private RecyclerView detail_list;


    private PlaylistDetailAdapter detailAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);


        detail_list = findViewById(R.id.detail_list);


        detailAdapter = new PlaylistDetailAdapter(new ArrayList<PlaylistItem>());


        detail_list.setAdapter(detailAdapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaylistDetailActivity.this);

        detail_list.setLayoutManager(layoutManager);

    }
}
