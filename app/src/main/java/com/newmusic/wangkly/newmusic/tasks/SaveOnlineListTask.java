package com.newmusic.wangkly.newmusic.tasks;

import android.os.AsyncTask;

import com.newmusic.wangkly.newmusic.beans.OnlinePlaylistItem;

import java.util.List;

public class SaveOnlineListTask extends AsyncTask< List<OnlinePlaylistItem>,Void,Void> {


    @Override
    protected Void doInBackground(List<OnlinePlaylistItem>... lists) {

        List<OnlinePlaylistItem> list = lists[0];




        return null;
    }
}
