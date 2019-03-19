package com.newmusic.wangkly.newmusic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public  static final String CREATE_PLAYING = "create table playing ( Idkey integer primary key autoincrement,id text,title text,position integer,duration integer,uri text,artist text,albumArt text)";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_PLAYING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



    }
}
