package com.newmusic.wangkly.newmusic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayListFragment extends Fragment {

    List<Map<String,Object>> audioList = new ArrayList<>();

    MainActivity mainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

         mainActivity = (MainActivity) getActivity();

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
        ListView listview = view.findViewById(R.id.playlist);

        audioList = this.getAudioList();

        PlaylistSimpleAdapter playlistSimpleAdapter = new PlaylistSimpleAdapter(view.getContext(),audioList,
                R.layout.audio_item,new String[]{"title","artist"},new int[]{R.id.audio_title,R.id.audio_author});

        listview.setAdapter(playlistSimpleAdapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listview = (ListView) parent;

                Map<String,Object> data = (Map<String, Object>) listview.getItemAtPosition(position);


//                Intent intent = new Intent(getContext(),MainActivity.class);
//                intent.putExtra("position",position);
//                intent.putExtra("id",(String)data.get("id"));
//                intent.putExtra("uri",(String)data.get("data"));
//                intent.putExtra("duration",(int) data.get("duration"));
//                intent.putExtra("title",(String)data.get("title"));
//                intent.putExtra("artist",(String)data.get("artist"));
//                intent.putExtra("albumArt",(String)data.get("albumArt"));
//                startActivity(intent);
                int duration=(int) data.get("duration");
                String title =(String)data.get("title");
                String albumArt =(String)data.get("albumArt");
                String uri = (String)data.get("data");
                mainActivity.play(uri,albumArt,title,duration);
            }
        });

    }



    public List<Map<String,Object>> getAudioList(){

        List<Map<String,Object>> list = new ArrayList<>();

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor =this.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);


        if(cursor != null && cursor.moveToFirst()){

            while (cursor.moveToNext()){

                Map<String,Object> map = new HashMap<>();
                map.put("id",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                map.put("title",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                map.put("data",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                map.put("artist",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                map.put("displayName",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                map.put("duration",cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));


                int album_id =cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                String albumArt =getAlbumArt(album_id);

                map.put("albumArt",albumArt);

                if((int)map.get("duration") >= 60000){
                    list.add(map);
                }
            }
        }

        return list;

    }



    private String getAlbumArt(int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = this.getContext().getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0){
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        cur = null;
        return album_art;
    }



}
