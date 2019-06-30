package com.newmusic.wangkly.newmusic.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.newmusic.wangkly.newmusic.MainActivity;
import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.constant.Constant;
import com.newmusic.wangkly.newmusic.view.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayingFragment extends Fragment implements View.OnClickListener {

    MainActivity mainActivity;
    View view;

    LinearLayout mini_win;

    ConstraintLayout full_screen;

    ImageView mini_img;

    TextView mini_playing_title;

    ImageButton mini_playing_btn;

    boolean playing = false;

    CircleImageView albumImg;

    TextView start;

    TextView durationMax;

    SeekBar seekBar;

    ImageButton last;

    ImageButton play;

    ImageButton next;

    Animation rotation;

    LocalBroadcastManager localBroadcastManager;

    int currentPosition =0;

    ObjectAnimator rotateAnim = null;

    //用于记录当前动画执行到哪里
    long mCurrentPlayTime  =0l;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.playing_fragment,container,false);

        mini_win = view.findViewById(R.id.mini_win);

        full_screen = view.findViewById(R.id.full_screen);

        mainActivity = (MainActivity) getActivity();

        mini_img = view.findViewById(R.id.mini_img);

        mini_playing_title = view.findViewById(R.id.mini_playing_title);

        mini_playing_btn = view.findViewById(R.id.mini_playing_btn);

        albumImg = view.findViewById(R.id.albumImg);
        start =view.findViewById(R.id.start);
        durationMax = view.findViewById(R.id.durationMax);
        seekBar = view.findViewById(R.id.seekBar);
        last = view.findViewById(R.id.last);
        play = view.findViewById(R.id.play);
        next = view.findViewById(R.id.next);


        mini_playing_btn.setOnClickListener(this);
        play.setOnClickListener(this);
        last.setOnClickListener(this);
        next.setOnClickListener(this);


        localBroadcastManager = LocalBroadcastManager.getInstance(this.getActivity());

        //旋转动画
//        rotation = AnimationUtils.loadAnimation(mainActivity,R.anim.rotation);
//        LinearInterpolator interpolator = new LinearInterpolator();
//        rotation.setInterpolator(interpolator);

        rotateAnim = ObjectAnimator.ofFloat(albumImg,"rotation",0f,360f);
        rotateAnim.setDuration(8000);
        rotateAnim.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnim.setRepeatMode(ValueAnimator.RESTART);
        rotateAnim.setInterpolator(new LinearInterpolator());

        return view;
    }


    public void setPlayingInfo(String albumArt,String title,int position){
        if(null == albumArt){
            mini_img.setImageResource(R.drawable.music_img);
        }else{
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
//            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            mini_img.setImageBitmap(bm);
        }
        mini_playing_title.setText(title);
        mini_playing_btn.setImageResource(R.drawable.ic_pause);
        this.playing = true;

        this.currentPosition =position;
    }



    public void preparePlayingInfo(String albumArt,String title,int position){
        if(null == albumArt){
            mini_img.setImageResource(R.drawable.music_img);
        }else{
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            mini_img.setImageBitmap(bm);
        }
        mini_playing_title.setText(title);

        this.currentPosition =position;
    }





    public void initFullScreenProps(String title,int duration,String albumArt){
        String time = this.formatTime(duration);
        start.setText("00:00");
        durationMax.setText(time);
        seekBar.setMax(duration);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mainActivity.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Bitmap bm;
        if (albumArt == null){
            albumImg.setImageResource(R.drawable.music_img);
        } else{
            bm = BitmapFactory.decodeFile(albumArt);
            albumImg.setImageBitmap(bm);
        }

    }



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


    public void hideMiniShowFull(){
        mini_win.setVisibility(View.GONE);
        full_screen.setVisibility(View.VISIBLE);
        if(this.playing){
//            albumImg.startAnimation(rotation);
            rotateAnim.start();
            play.setImageResource(R.drawable.ic_pause);
        }else{
            play.setImageResource(R.drawable.ic_play);
        }
    }


    public void hideFullShowMini(){
//        albumImg.clearAnimation();
//        rotateAnim.pause();
        
        mCurrentPlayTime = rotateAnim.getCurrentPlayTime();
        rotateAnim.cancel();

        mini_win.setVisibility(View.VISIBLE);
        full_screen.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mini_playing_btn:
            case R.id.play:
                if(playing){
                    mini_playing_btn.setImageResource(R.drawable.ic_play);
                    play.setImageResource(R.drawable.ic_play);
                    //通知service 停止播放
                    mainActivity.pause();
//                    albumImg.clearAnimation();

//                    rotateAnim.pause();//需要api到19才能支持

                    mCurrentPlayTime = rotateAnim.getCurrentPlayTime();
                    rotateAnim.cancel();

                    playing =false;
                }else {
                    mini_playing_btn.setImageResource(R.drawable.ic_pause);
                    play.setImageResource(R.drawable.ic_pause);
                    //通知service 恢复播放
                    mainActivity.resume();
//                    albumImg.startAnimation(rotation);
//                    rotateAnim.resume();


                    rotateAnim.start();
                    rotateAnim.setCurrentPlayTime(mCurrentPlayTime);

                    playing =true;
                }

                break;



            case R.id.last:
                Intent intentLast = new Intent(Constant.CHANGE_MUSIC_LOCAL);
                intentLast.putExtra("type","previous");
                intentLast.putExtra("position",this.currentPosition);
                localBroadcastManager.sendBroadcast(intentLast);

                break;

            case R.id.next:
                Intent intentNext = new Intent(Constant.CHANGE_MUSIC_LOCAL);
                intentNext.putExtra("type","next");
                intentNext.putExtra("position",this.currentPosition);
                localBroadcastManager.sendBroadcast(intentNext);

                break;

            default:


                break;

        }




    }



}
