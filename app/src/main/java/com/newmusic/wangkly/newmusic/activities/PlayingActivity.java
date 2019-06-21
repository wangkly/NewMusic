package com.newmusic.wangkly.newmusic.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.adapter.CircleViewAdapter;
import com.newmusic.wangkly.newmusic.beans.PlaylistItem;
import com.newmusic.wangkly.newmusic.fragments.CircleViewFragment;
import com.newmusic.wangkly.newmusic.service.MusicService;
import com.newmusic.wangkly.newmusic.utils.DBHelper;
import com.newmusic.wangkly.newmusic.utils.MediaUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mTopTitleBar;

    private ViewPager mViewPager;

    private LinearLayout mBottomArea;

    private ImageButton mTopBack; //返回按钮

    private TextView mTitle;//歌曲名称

    private TextView start;//当前播放时间

    private TextView durationMax;//歌曲时间

    private SeekBar seekBar; //进度条

    private ImageButton last;//上一曲

    private ImageButton play;//播放、暂停

    private ImageButton next;//下一曲


    private MusicService.MyBinder myBinder;


    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_playing);
        dbHelper = new DBHelper(PlayingActivity.this,"play.db",null,1);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        myBinder= (MusicService.MyBinder) bundle.getBinder("myBinder");


        mTopTitleBar = findViewById(R.id.topTitleBar);
        mViewPager = findViewById(R.id.playingViewPager);
        mBottomArea = findViewById(R.id.bottomArea);

        mTopBack = findViewById(R.id.play_top_back);
        mTitle = findViewById(R.id.song_title);

        start = findViewById(R.id.start);
        durationMax = findViewById(R.id.durationMax);
        seekBar = findViewById(R.id.seekBar);

        last = findViewById(R.id.last);
        play = findViewById(R.id.play);
        next = findViewById(R.id.next);


        mTopBack.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);
        play.setOnClickListener(this);


        this.initView();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    myBinder.updateProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }




    private void initView() {

        List<PlaylistItem> list = MediaUtil.getAudioList(PlayingActivity.this);

        List<CircleViewFragment> fragments = new ArrayList<>();


        for (int i = 0;i <list.size() ; i++){

            fragments.add(new CircleViewFragment());
        }

        FragmentManager fm = getSupportFragmentManager();

        CircleViewAdapter adapter = new CircleViewAdapter(fragments,fm);

        mViewPager.setAdapter(adapter);

        //获取当前正在播放的歌曲信息
        initPlayingSongInfo();
        initPlayStatus();

    }



    public void initPlayingSongInfo(){

        String title="";
        String uri="";
        String albumArt = null;
        int duration =0;
        int position =0;
//        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,"Idkey=?",new String[]{"1"},null,null,null);
        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);

        if(cursor.moveToFirst()){

            do {
                title = cursor.getString(cursor.getColumnIndex("title"));
                uri = cursor.getString(cursor.getColumnIndex("uri"));
                albumArt = cursor.getString(cursor.getColumnIndex("albumArt"));
                duration = cursor.getInt(cursor.getColumnIndex("duration"));
                position = cursor.getInt(cursor.getColumnIndex("position"));
            } while (cursor.moveToNext());

        }


        mTitle.setText(title);

        start.setText("00:00");

        seekBar.setMax(duration);

        durationMax.setText(formatTime(duration));

    }


    public void initPlayStatus(){

        if(myBinder.isPlaying()){
            myBinder.UpdateSeekBarUi(handler);
            play.setImageResource(R.drawable.ic_pause);

        }else {
            play.setImageResource(R.drawable.ic_play);
        }

        start.setText(formatTime(myBinder.getCurrentPositon()));

    }




    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.play_top_back:

                finish();

                break;

            case R.id.play:

                if(myBinder.isPlaying()){
                    play.setImageResource(R.drawable.ic_play);
                    //通知service 停止播放
                    myBinder.pause();
                }else {
                    play.setImageResource(R.drawable.ic_pause);
                    //通知service 恢复播放
                    myBinder.resume();
                }

                break;



        }
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateProgress(msg.what);
        }
    };


    public void updateProgress(int time){
        seekBar.setProgress(time);
        String text = formatTime(time);
        start.setText(text);
    }


    private String formatTime(int length) {

        Date date = new Date(length);//调用Date方法获值

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");//规定需要形式

        String TotalTime = simpleDateFormat.format(date);//转化为需要形式

        return TotalTime;

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent,R.anim.bottom_out);
    }
}
