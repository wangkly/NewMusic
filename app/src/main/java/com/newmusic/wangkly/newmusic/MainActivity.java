package com.newmusic.wangkly.newmusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private final int WRITE_PERMISSION_REQUEST =1;

    ViewPager viewPager;

//    TabLayout tabLayout;

    Toolbar toolbar;

    FragmentManager fragmentManager;


    MyFragmentPageAdapter fadapter;

    Boolean isPlayingOnTop = false;

    LinearLayout frame ;

    private List<String> titles;


    PlayListFragment playListFragment;
    OnlinePlayListFragment onlinePlayListFragment;
    PlayingFragment playingFragment;

    LocalBroadcastManager localBroadcastManager;

    MusicService.MyBinder myBinder;

    DBHelper dbHelper;

    ServiceConnection  connection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            playingFragment.updateProgress(msg.what);
        }
    };


    public void seekTo(int progress){
        myBinder.updateProgress(progress);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab =  findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });



        dbHelper = new DBHelper(MainActivity.this,"play",null,1);


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION_REQUEST);
        }else{
            this.remainOperation();
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    public void remainOperation(){
        List<Fragment> fragments = new ArrayList<>();
        fragmentManager= getSupportFragmentManager();
        viewPager= findViewById(R.id.viewPager);
        frame = findViewById(R.id.frame);

        titles= new ArrayList<>();
        titles.add("本地音乐");
        titles.add("网络资源");


        playListFragment = new PlayListFragment();
        onlinePlayListFragment = new OnlinePlayListFragment();
        playingFragment = new PlayingFragment();

        fragments.add(playListFragment);
        fragments.add(onlinePlayListFragment);


        fadapter = new MyFragmentPageAdapter(fragmentManager,fragments,titles);
        viewPager.setAdapter(fadapter);

//        tabLayout = findViewById(R.id.tabLayout);

        MagicIndicator magicIndicator = findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundResource(R.drawable.round_indicator_bg);
        CommonNavigator commonNavigator = new CommonNavigator(this);

        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setTextColor(Color.GRAY);
                clipPagerTitleView.setClipColor(Color.WHITE);
                clipPagerTitleView.setText(titles.get(index));
                clipPagerTitleView.setTextSize(55);
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                float navigatorHeight = context.getResources().getDimension(R.dimen.common_navigator_height);
                float borderWidth = UIUtil.dip2px(context, 1);
                float lineHeight = navigatorHeight - 2 * borderWidth;
                indicator.setLineHeight(lineHeight);
                indicator.setRoundRadius(lineHeight / 2);
                indicator.setYOffset(borderWidth);
                indicator.setColors(Color.parseColor("#bc2a2a"));
                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);

        ViewPagerHelper.bind(magicIndicator,viewPager);



//        viewPager.setCurrentItem(0);
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.list);
//        tabLayout.getTabAt(1).setIcon(R.drawable.music);



        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frame,playingFragment);
        fragmentTransaction.commit();

        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                isPlayingOnTop =true;
//
//                playingFragment.hideMiniShowFull();
//                toolbar.setVisibility(View.GONE);
//                ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
//                int fullHeight = getWindowManager().getDefaultDisplay().getHeight();
//                ValueAnimator valueAnimator = ValueAnimator.ofInt(layoutParams.height,fullHeight);
//                valueAnimator.setDuration(1000);
//                valueAnimator.start();
//                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                       int height = (int) animation.getAnimatedValue();
//                        frame.getLayoutParams().height = height;
//                        frame.requestLayout();
//                    }
//                });
//                return  false;

                ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
                layoutParams.height =WindowManager.LayoutParams.MATCH_PARENT;
                frame.setLayoutParams(layoutParams);
                isPlayingOnTop =true;

                playingFragment.hideMiniShowFull();
                toolbar.setVisibility(View.GONE);
                return false;

            }
        });


        Intent ServiceIntent = new Intent(MainActivity.this,MusicService.class);
        bindService(ServiceIntent,connection,BIND_AUTO_CREATE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    this.remainOperation();

                }else{
                    Toast.makeText(MainActivity.this,"未授予权限",Toast.LENGTH_LONG).show();
                    return;
                }
        }
    }




    public void play(String uri,String albumArt,String title,int duration,int position){
        myBinder.initMediaPlayer(uri);
        myBinder.playMusic();
        myBinder.UpdateSeekBarUi(handler);
        playingFragment.setPlayingInfo(albumArt,title,position);
        playingFragment.initFullScreenProps(title,duration,albumArt);
    }


    public void pause(){
        myBinder.pause();
    }


    public void resume(){
        myBinder.resume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(isPlayingOnTop){
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
            layoutParams.height = (int)(50*scale+0.5f);

            frame.setLayoutParams(layoutParams);
            isPlayingOnTop =false;

            playingFragment.hideFullShowMini();
            toolbar.setVisibility(View.VISIBLE);
//            FragmentTransaction ft= fragmentManager.beginTransaction();
//            ft.replace(R.id.frame,playingFragment);
//            ft.commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }



}
