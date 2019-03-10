package com.newmusic.wangkly.newmusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayingFragment extends Fragment {

    MainActivity mainActivity;
    View view;

    ImageView mini_img;

    TextView mini_playing_title;

    ImageButton mini_playing_btn;

    boolean playing = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.playing_fragment,container,false);

        mainActivity = (MainActivity) getActivity();

        mini_img = view.findViewById(R.id.mini_img);

        mini_playing_title = view.findViewById(R.id.mini_playing_title);

        mini_playing_btn = view.findViewById(R.id.mini_playing_btn);

        mini_playing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playing){
                    mini_playing_btn.setImageDrawable(view.getResources().getDrawable(R.drawable.play));
                    //通知service 停止播放
                    mainActivity.pause();
                    playing =false;
                }else {
                    mini_playing_btn.setImageDrawable(view.getResources().getDrawable(R.drawable.pause));
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




}
