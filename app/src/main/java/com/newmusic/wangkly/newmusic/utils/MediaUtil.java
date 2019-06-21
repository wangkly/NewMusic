package com.newmusic.wangkly.newmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.newmusic.wangkly.newmusic.beans.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

public class MediaUtil {


    /**
     * 获取本地歌曲列表
     * @param context
     * @return
     */
    public static List<PlaylistItem> getAudioList(Context context){

        List<PlaylistItem> list = new ArrayList<>();

        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor =context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);


        if(cursor != null && cursor.moveToFirst()){

            while (cursor.moveToNext()){

                PlaylistItem item = new PlaylistItem();
                item.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                item.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                item.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                item.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                item.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));

                int album_id =cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String albumArt =getAlbumArt(context,album_id);

                item.setAlbumArt(albumArt);


                if(item.getDuration() >= 60000){
                    list.add(item);
                }
            }
        }

        return list;

    }



    private static  String getAlbumArt(Context context,int album_id) {
        String mUriAlbums = "content://media/external/audio/albums";
        String[] projection = new String[] { "album_art" };
        Cursor cur = context.getContentResolver().query(Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),projection, null, null, null);
        String album_art = null;
        if (cur.getCount() > 0 && cur.getColumnCount() > 0){
            cur.moveToNext();
            album_art = cur.getString(0);
        }
        cur.close();
        return album_art;
    }


}
