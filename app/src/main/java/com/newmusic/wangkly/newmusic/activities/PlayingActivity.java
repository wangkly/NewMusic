package com.newmusic.wangkly.newmusic.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.adapter.CircleViewAdapter;
import com.newmusic.wangkly.newmusic.adapter.MyPagerAdapter;
import com.newmusic.wangkly.newmusic.constant.Constant;
import com.newmusic.wangkly.newmusic.fragments.CircleViewFragment;
import com.newmusic.wangkly.newmusic.service.MusicService;
import com.newmusic.wangkly.newmusic.utils.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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


    private LocalBroadcastManager broadcastManager ;

    private RefreshSongInfoReceiver receiver;



    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //获取当前正在播放的歌曲信息
                    initPlayingSongInfo();
                }
            },1000);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playing);

        broadcastManager = LocalBroadcastManager.getInstance(PlayingActivity.this);

        dbHelper = new DBHelper(PlayingActivity.this);


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

        //bind 服务，获取myBinder
        Intent serviceIntent = new Intent(PlayingActivity.this,MusicService.class);
        bindService(serviceIntent,connection,BIND_AUTO_CREATE);


        receiver = new RefreshSongInfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.PLAYING_ACTIVITY_REFRESH);
        broadcastManager.registerReceiver(receiver,filter);

    }




    private void initView() {

//        final List<CircleViewFragment> fragments = new ArrayList<>();
//
//        CircleViewFragment fg1 = new CircleViewFragment();
//        CircleViewFragment fg2 = new CircleViewFragment();
//        CircleViewFragment fg3 = new CircleViewFragment();

//        fragments.addAll(Arrays.asList(fg3,fg1,fg2,fg3,fg1));
//
//        FragmentManager fm = getSupportFragmentManager();
//
//        CircleViewAdapter adapter = new CircleViewAdapter(fragments,fm);


        final List<Integer> list = new ArrayList<>();

        list.add(R.drawable.b);
        list.add(R.drawable.a);
        list.add(R.drawable.music_img);
        list.add(R.drawable.b);
        list.add(R.drawable.a);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(this,list);


        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int currentPosition;

            @Override
            public void onPageScrolled(int i, float v, int i1) {
                Log.d("scrolled","");
            }

            @Override
            public void onPageSelected(int i) {
                currentPosition = i;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if(ViewPager.SCROLL_STATE_IDLE != state) return;
                if(currentPosition == 0){
                    mViewPager.setCurrentItem(list.size()- 2,false);
                }else if(currentPosition == list.size() - 1){
                    mViewPager.setCurrentItem(1,false);
                }

            }
        });

    }



    public void initPlayingSongInfo(){

        String title="";
        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);

        if(cursor.moveToFirst()){

                title = cursor.getString(cursor.getColumnIndex("title"));
        }


        mTitle.setText(title);

        seekBar.setMax(myBinder.getDuration());

        durationMax.setText(formatTime(myBinder.getDuration()));

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


            case R.id.last:

                Intent lastIntent = new Intent(Constant.MAIN_ACTIVITY_ACTION);
                lastIntent.putExtra("type",Constant.PLAY_PREVIOUS);

                broadcastManager.sendBroadcast(lastIntent);

                break;


            case R.id.next:

                Intent nextIntent = new Intent(Constant.MAIN_ACTIVITY_ACTION);
                nextIntent.putExtra("type",Constant.PLAY_NEXT);

                broadcastManager.sendBroadcast(nextIntent);


                break;


             default:


                 break;



        }
    }


    /**
     * 注册监听 切换歌曲时刷新当前播放信息
     */
    class RefreshSongInfoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            initPlayingSongInfo();

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


    @Override
    protected void onDestroy() {

        broadcastManager.unregisterReceiver(receiver);

        if (connection != null) {
            unbindService(connection);
        }

        super.onDestroy();
    }
}
