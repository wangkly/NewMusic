package com.newmusic.wangkly.newmusic.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //正在播放
    public  static final String CREATE_PLAYING = "create table playing ( Idkey integer primary key autoincrement,id text,title text,position integer,duration integer,uri text,artist text,albumArt text)";

    //网络歌单
    public static final String CREATE_ONLINE_LIST = "create table online_list( id integer primary key,name text,coverImgUrl text,description text , subscribedCount integer ) ";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PLAYING);
            db.execSQL(CREATE_ONLINE_LIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists playing ");
//        db.execSQL("create table if not exists playing (Idkey integer)");

        onCreate(db);

    }
}
