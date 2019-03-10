package com.newmusic.wangkly.newmusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayingFullscreenFragment extends Fragment {


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
         View view = inflater.inflate(R.layout.playing_fullscreen_fragment,container,false);

        albumImg = view.findViewById(R.id.albumImg);
        start =view.findViewById(R.id.start);
        durationMax = view.findViewById(R.id.durationMax);
        seekBar = view.findViewById(R.id.seekBar);
        last = view.findViewById(R.id.last);
        play = view.findViewById(R.id.play);
        next = view.findViewById(R.id.next);
         return view;
    }



    public void initProps(String title,int duration,String albumArt){
        String time = this.formatTime(duration);
        start.setText("00:00");
        durationMax.setText(time);
        seekBar.setMax(duration);

        Bitmap bm;
        if (albumArt == null){
            albumImg.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.music_img));
        } else{
            bm = BitmapFactory.decodeFile(albumArt);
            BitmapDrawable bmpDraw = new BitmapDrawable(bm);
            albumImg.setImageDrawable(bmpDraw);
        }

    }




    private String formatTime(int length) {

        Date date = new Date(length);//调用Date方法获值

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");//规定需要形式

        String TotalTime = simpleDateFormat.format(date);//转化为需要形式

        return TotalTime;

    }


}
