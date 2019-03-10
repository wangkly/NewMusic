package com.newmusic.wangkly.newmusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayingFragment extends Fragment {

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

        mini_playing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing){
                    mini_playing_btn.setImageDrawable(view.getResources().getDrawable(R.drawable.play));
                    play.setImageDrawable(view.getResources().getDrawable(R.drawable.play));
                    //通知service 停止播放
                    mainActivity.pause();
                    playing =false;
                }else {
                    mini_playing_btn.setImageDrawable(view.getResources().getDrawable(R.drawable.pause));
                    play.setImageDrawable(view.getResources().getDrawable(R.drawable.pause));
                    //通知service 停止播放
                    mainActivity.resume();
                    playing =true;
                }
            }
        });



        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing){
                    mini_playing_btn.setImageDrawable(view.getResources().getDrawable(R.drawable.play));
                    play.setImageDrawable(view.getResources().getDrawable(R.drawable.play));
                    //通知service 停止播放
                    mainActivity.pause();
                    playing =false;
                }else {
                    mini_playing_btn.setImageDrawable(view.getResources().getDrawable(R.drawable.pause));
                    play.setImageDrawable(view.getResources().getDrawable(R.drawable.pause));
                    //通知service 停止播放
                    mainActivity.resume();
                    playing =true;
                }
            }
        });


        return view;
    }


    public void setPlayingInfo(String albumArt,String title){
        if(null == albumArt){
            mini_img.setImageDrawable(getResources().getDrawable(R.drawable.music_img));
        }else{
            Bitmap bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            mini_img.setImageDrawable(bmpDraw);
        }
        mini_playing_title.setText(title);
        mini_playing_btn.setImageDrawable(view.getResources().getDrawable(R.drawable.pause));
        this.playing = true;
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
            albumImg.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.music_img));
        } else{
            bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            albumImg.setImageDrawable(bmpDraw);
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
    }


    public void hideFullShowMini(){
        mini_win.setVisibility(View.VISIBLE);
        full_screen.setVisibility(View.GONE);
    }



}
