package com.newmusic.wangkly.newmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class MusicService extends Service {
    public static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private Timer timer;

    public MusicService() {

    }

    public class  MyBinder extends Binder {


        public void showToast(){
            Toast.makeText(MusicService.this.getApplicationContext(),"MyService",Toast.LENGTH_LONG);
        }

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
                            }
                        }
                    },0L,1000L);
                }
            }).start();

        }


        public void initMediaPlayer(String uri){
            if(mediaPlayer.isPlaying()){
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
        return new MyBinder();
    }
}
