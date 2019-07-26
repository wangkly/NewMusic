package com.newmusic.wangkly.newmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.newmusic.wangkly.newmusic.constant.Constant;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    public static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Timer timer;

    private MyBinder myBinder = new MyBinder();

    public MusicService() {

    }


    public class  MyBinder extends Binder {



        public void UpdateSeekBarUi(final Handler handler){

            timer = new Timer();
                new Thread(new Runnable() {
                @Override
                public void run() {
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Log.i("MyService", "timer run : ");
                            if(null != mediaPlayer && mediaPlayer.isPlaying()){
                                int progress = mediaPlayer.getCurrentPosition();
                                handler.sendEmptyMessage(progress);
                            }else{
                                timer.cancel();
                            }
                        }
                    },0L,1000L);
                }
            }).start();

        }


        public void initMediaPlayer(String uri){
            if(null != mediaPlayer){
                mediaPlayer.reset();
            }
            try {
                mediaPlayer.setDataSource(uri);
                mediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        public void playMusic(){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }

        }


        public void pause(){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }

        public void resume(){
            if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }

        }


        public void updateProgress(int progress){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.seekTo(progress);
            }

        }


        public boolean isPlaying(){

            return mediaPlayer.isPlaying();
        }


        public int getCurrentPositon(){

            if(null != mediaPlayer){
                return mediaPlayer.getCurrentPosition();
            }
            return 0;
        }


        public int getDuration(){

            if(null != mediaPlayer){
                return mediaPlayer.getDuration();
            }else {
                return  0;
            }
        }


        public void unbindCallback(){
            Log.i("MyService", "unbindCallback: timer cancel ");
            timer.cancel();
        }


        public MusicService getMusicService(){
            return  MusicService.this;
        }

    }



    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: trigger");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: trigger");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, " MyService onDestroy: trigger");
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, " MyService onBind: trigger");

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                Log.i(TAG,"音乐播放结束！！！");
                //发送播放下一首歌曲通知
                Intent intent = new Intent(Constant.MAIN_ACTIVITY_ACTION);

                intent.putExtra("type",Constant.PLAY_NEXT);

                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }
        });

        return myBinder;
    }
}
