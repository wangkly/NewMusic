package com.newmusic.wangkly.newmusic;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newmusic.wangkly.newmusic.activities.PlayingActivity;
import com.newmusic.wangkly.newmusic.adapter.MyFragmentPageAdapter;
import com.newmusic.wangkly.newmusic.fragments.OnlinePlayListFragment;
import com.newmusic.wangkly.newmusic.fragments.PlayListFragment;
import com.newmusic.wangkly.newmusic.fragments.PlayingFragment;
import com.newmusic.wangkly.newmusic.service.MusicService;
import com.newmusic.wangkly.newmusic.utils.DBHelper;
import com.newmusic.wangkly.newmusic.utils.PermissionHelper;

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



    ViewPager viewPager;


    Toolbar toolbar;

    FragmentManager fragmentManager;


    MyFragmentPageAdapter fadapter;

    Boolean isfullScreenMode = false;//是否全屏播放

    LinearLayout frame ;

    private List<String> titles;

    private ImageView mini_img;

    private TextView mini_playing_title;

    private ImageButton mini_playing_btn;



    PlayListFragment playListFragment;
    OnlinePlayListFragment onlinePlayListFragment;
//    PlayingFragment playingFragment;


    MusicService.MyBinder myBinder;

    public DBHelper dbHelper;


    private PermissionHelper mPermissionHelper;

    ServiceConnection  connection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;
            getLastPlayingInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(MainActivity.this,"play.db",null,1);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //请求应用权限交由PermissionHelper处理
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {

                remainOperation();
            }
        });


        if(Build.VERSION.SDK_INT < 23){ //Android 6.0 以下
                // 如果系统版本低于23，直接跑应用的逻辑
            remainOperation();
        }else{

            if(mPermissionHelper.isAllRequestedPermissionGranted()){
                //所有权限都被允许，执行后面的逻辑
                remainOperation();

            }else {
                //非所有权限被允许。请求权限
                mPermissionHelper.applyPermissions();
            }
        }

    }



    @SuppressLint("ClickableViewAccessibility")
    public void remainOperation(){
        List<Fragment> fragments = new ArrayList<>();
        fragmentManager= getSupportFragmentManager();
        viewPager= findViewById(R.id.viewPager);
        frame = findViewById(R.id.frame);


        mini_img = findViewById(R.id.mini_img);

        mini_playing_title = findViewById(R.id.mini_playing_title);

        mini_playing_btn = findViewById(R.id.mini_playing_btn);

        titles= new ArrayList<>();
        titles.add("本地音乐");
        titles.add("热门歌单");


        playListFragment = new PlayListFragment();
        onlinePlayListFragment = new OnlinePlayListFragment();
//        playingFragment = new PlayingFragment();

        fragments.add(playListFragment);
        fragments.add(onlinePlayListFragment);


        fadapter = new MyFragmentPageAdapter(fragmentManager,fragments,titles);
        viewPager.setAdapter(fadapter);


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
//                clipPagerTitleView.setTextSize(55);
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


//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.frame,playingFragment);
//        fragmentTransaction.commit();

        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

//                ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
//                layoutParams.height =WindowManager.LayoutParams.MATCH_PARENT;
//                frame.setLayoutParams(layoutParams);
//                isfullScreenMode =true;
//
//                playingFragment.hideMiniShowFull();
//                toolbar.setVisibility(View.GONE);
//                return false;


                Intent intent = new Intent(MainActivity.this, PlayingActivity.class);

                Bundle bundle = new Bundle();

                bundle.putBinder("myBinder",myBinder);

                intent.putExtras(bundle);

                startActivity(intent);

                overridePendingTransition(R.anim.bottom_in,R.anim.bottom_silent);

                return false;
            }
        });


        Intent ServiceIntent = new Intent(MainActivity.this,MusicService.class);
        bindService(ServiceIntent,connection,BIND_AUTO_CREATE);



        mini_playing_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myBinder.isPlaying()){
                    mini_playing_btn.setImageResource(R.drawable.ic_play);
                    //通知service 停止播放
                    pause();
                }else {
                    mini_playing_btn.setImageResource(R.drawable.ic_pause);
                    //通知service 恢复播放
                    resume();
                }
            }
        });


    }




    public void getLastPlayingInfo(){
        String title="";
        String uri="";
        String albumArt = null;

//        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,"Idkey=?",new String[]{"1"},null,null,null);
        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);

        if(cursor.moveToFirst()){

        do {
                title = cursor.getString(cursor.getColumnIndex("title"));
                uri = cursor.getString(cursor.getColumnIndex("uri"));
                albumArt = cursor.getString(cursor.getColumnIndex("albumArt"));

            } while (cursor.moveToNext());

        }


        if(null != uri && !"".equals(uri)){
            myBinder.initMediaPlayer(uri);
//            myBinder.UpdateSeekBarUi(handler);

            if(null == albumArt){
                mini_img.setImageResource(R.drawable.music_img);
            }else{
                Bitmap bm = BitmapFactory.decodeFile(albumArt);
                mini_img.setImageBitmap(bm);
            }

            mini_playing_title.setText(title);


//            playingFragment.preparePlayingInfo(albumArt,title,position);
//            playingFragment.initFullScreenProps(title,duration,albumArt);


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode,resultCode,data);
    }



    public void seekTo(int progress){
        myBinder.updateProgress(progress);
    }


    public void play(String uri, String albumArt, String title, int duration, int position){
        myBinder.initMediaPlayer(uri);
        myBinder.playMusic();
//        myBinder.UpdateSeekBarUi(handler);



//        playingFragment.setPlayingInfo(albumArt,title,position);
//        playingFragment.initFullScreenProps(title,duration,albumArt);
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
        }else if(isfullScreenMode){
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
            layoutParams.height = (int)(50*scale+0.5f);

            frame.setLayoutParams(layoutParams);
            isfullScreenMode =false;

//            playingFragment.hideFullShowMini();
            toolbar.setVisibility(View.VISIBLE);

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
