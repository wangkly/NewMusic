package com.newmusic.wangkly.newmusic;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newmusic.wangkly.newmusic.activities.PlayingActivity;
import com.newmusic.wangkly.newmusic.adapter.MyFragmentPageAdapter;
import com.newmusic.wangkly.newmusic.constant.Constant;
import com.newmusic.wangkly.newmusic.fragments.OnlinePlayListFragment;
import com.newmusic.wangkly.newmusic.fragments.PlayListFragment;
import com.newmusic.wangkly.newmusic.interfaces.QueryResultListener;
import com.newmusic.wangkly.newmusic.service.MusicService;
import com.newmusic.wangkly.newmusic.tasks.OnlineListTask;
import com.newmusic.wangkly.newmusic.utils.DBHelper;
import com.newmusic.wangkly.newmusic.utils.PermissionHelper;
import com.squareup.picasso.Picasso;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPager viewPager;

    private Toolbar toolbar;

    private FragmentManager fragmentManager;

    private MyFragmentPageAdapter fadapter;

    private Boolean isfullScreenMode = false;//是否全屏播放

    private LinearLayout frame ;

    private List<String> titles;

    private ImageView mini_img;

    private TextView mini_playing_title;

    private ImageButton mini_playing_btn;


    PlayListFragment playListFragment;
    OnlinePlayListFragment onlinePlayListFragment;


    public MusicService.MyBinder myBinder;


    public DBHelper dbHelper;


    private PermissionHelper mPermissionHelper;


    private LocalBroadcastManager broadcastManager;

    private MainActivityReceiver receiver;


    ServiceConnection  connection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MusicService.MyBinder) service;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshPlayingInfo(true);

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
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(MainActivity.this);

        broadcastManager = LocalBroadcastManager.getInstance(MainActivity.this);


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

        //注册网络歌单播放监听
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.MAIN_ACTIVITY_ACTION);

        receiver= new MainActivityReceiver();
        broadcastManager.registerReceiver(receiver,filter);

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

        titles = new ArrayList<>();
        titles.add("本地音乐");
        titles.add("热门歌单");
        titles.add("电台");


        playListFragment = new PlayListFragment();
        onlinePlayListFragment = new OnlinePlayListFragment();


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


        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Intent intent = new Intent(MainActivity.this, PlayingActivity.class);

                Bundle bundle = new Bundle();

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


    /**
     * 刷新activity 下方的mini播放窗口播放信息
     * @param initFlag 初始化时 为true,之后为false
     */
    public void refreshPlayingInfo(Boolean initFlag){
        String title="";
        String uri="";
        String albumArt = null;

//        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,"Idkey=?",new String[]{"1"},null,null,null);
        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);

        if(cursor.moveToFirst()){

            title = cursor.getString(cursor.getColumnIndex("title"));
            uri = cursor.getString(cursor.getColumnIndex("uri"));
            albumArt = cursor.getString(cursor.getColumnIndex("albumArt"));

        }

        cursor.close();


        if(null != uri && !"".equals(uri)){

            if(initFlag){
                myBinder.initMediaPlayer(uri);
            }
            //是否正在播放
            if(myBinder.isPlaying()){
                mini_playing_btn.setImageResource(R.drawable.ic_pause);
            }else{
                mini_playing_btn.setImageResource(R.drawable.ic_play);
            }

            if(null == albumArt){
                mini_img.setImageResource(R.drawable.music_img);
            }else{

                if(albumArt.indexOf("http") > -1){
                    Picasso.get().load(albumArt).resize(2048, 1600).onlyScaleDown().into(mini_img);
                }else{
                    Bitmap bm = BitmapFactory.decodeFile(albumArt);
                    mini_img.setImageBitmap(bm);

                }

            }

            mini_playing_title.setText(title);

        }

        //通知播放页做刷新
        Intent intent = new Intent(Constant.PLAYING_ACTIVITY_REFRESH);
        broadcastManager.sendBroadcast(intent);

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


    public void play(String uri){
        myBinder.initMediaPlayer(uri);
        myBinder.playMusic();

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
        }else {
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

        broadcastManager.unregisterReceiver(receiver);

        if (connection != null) {
            unbindService(connection);
        }

        super.onDestroy();
    }




    //播放网络音乐
    class MainActivityReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            String type = intent.getStringExtra("type");

            switch (type){


                case Constant.ONLINE_MUSIC_PLAY_ACTION :

                    String url =   intent.getStringExtra("url");
                    myBinder.initMediaPlayer(url);
                    myBinder.playMusic();

                    refreshPlayingInfo(false);

                    break;

                case Constant.REFRESH_PALYINGINFO:

                    refreshPlayingInfo(false);

                    break;


                case Constant.PLAY_NEXT:

                    findPreviousOrNext(true);


                    break;

                case Constant.PLAY_PREVIOUS:

                    findPreviousOrNext(false);

                    break;

                default:


                    break;


            }
        }
    }


    /**
     * 找到下一曲或者上一曲
     * @param isNext 是否下一曲
     */
    public void findPreviousOrNext(Boolean isNext){

        Log.i("Main","findPreviousOrNext");
        //查询正在播放的歌曲信息
        Integer type;
        Integer position;
        String id;

        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);
        if(cursor.moveToFirst()){

            id = cursor.getString(cursor.getColumnIndex("id"));
            position = cursor.getInt(cursor.getColumnIndex("position"));
            type = cursor.getInt(cursor.getColumnIndex("type"));


            if(type == 1){
                //播放的是网络歌单的歌曲
              Cursor onlineListCusor =  dbHelper.getWritableDatabase().query("online_playing_list",null,null,null,null,null,null);
              onlineListCusor.moveToFirst();

              do{
                   Long itemId = onlineListCusor.getLong(onlineListCusor.getColumnIndex("id"));

                   if(itemId.equals(Long.valueOf(id)) ){

                       if(isNext){

                           onlineListCusor.moveToNext();
                           Long tId =  onlineListCusor.getLong(onlineListCusor.getColumnIndex("id"));
                           String title = onlineListCusor.getString(onlineListCusor.getColumnIndex("name"));
                           String authorName = onlineListCusor.getString(onlineListCusor.getColumnIndex("authorName"));
                           String albumPicUrl = onlineListCusor.getString(onlineListCusor.getColumnIndex("albumPicUrl"));

                           queryUrlAndPlay(tId,position,title,authorName,albumPicUrl);


                       }else {

                           onlineListCusor.moveToPrevious();
                           Long tId =  onlineListCusor.getLong(onlineListCusor.getColumnIndex("id"));
                           String title = onlineListCusor.getString(onlineListCusor.getColumnIndex("name"));
                           String authorName = onlineListCusor.getString(onlineListCusor.getColumnIndex("authorName"));
                           String albumPicUrl = onlineListCusor.getString(onlineListCusor.getColumnIndex("albumPicUrl"));

                           queryUrlAndPlay(tId,position,title,authorName,albumPicUrl);
                       }

                       break;

                   }

                }while (onlineListCusor.moveToNext());



            }else {
                //播放的本地歌曲

                Intent intent = new Intent(Constant.CHANGE_MUSIC_LOCAL);
                intent.putExtra("type",isNext ? "next":"previous");
                intent.putExtra("position",position);

                broadcastManager.sendBroadcast(intent);
            }


        }


    }


    /**
     * 查询网络歌单播放url
     * @param songId 歌曲id
     */
    public void queryUrlAndPlay(final Long songId, final Integer position, final String title, final String authorName, final String albumPicUrl){
        StringBuilder sb = new StringBuilder(Constant.HOST_URL);

        sb.append("/song/url?id=");

        OnlineListTask task = new OnlineListTask(new QueryResultListener() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray data = json.getJSONArray("data");
                    JSONObject obj =  data.getJSONObject(0);
                    String url = obj.getString("url");

                    if(null != url){

                        //保存当前播放歌曲信息
                        ContentValues values = new ContentValues();
                        values.put("id",songId);
                        values.put("title",title);
                        values.put("position",position);
                        values.put("duration",0);
                        values.put("uri",url);
                        values.put("artist",authorName);
                        values.put("albumArt",albumPicUrl);
                        values.put("type",1);//type 1:网络歌单播放，0：本地音乐播放


                        Cursor cursor = dbHelper.getWritableDatabase().query("playing",null,null,null,null,null,null);
                        if(cursor.getCount() > 0){
                            dbHelper.getWritableDatabase().delete("playing",null,null);
                        }

                        //存储正在播放歌曲
                        dbHelper.getWritableDatabase().insert("playing",null,values);


                        //通知播放该url歌曲
                        Intent intent = new Intent(Constant.MAIN_ACTIVITY_ACTION);
                        intent.putExtra("type",Constant.ONLINE_MUSIC_PLAY_ACTION);
                        intent.putExtra("url",url);
                        broadcastManager.sendBroadcast(intent);


                    }else {
                        Toast.makeText(MainActivity.this,"无法播放",Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(String result) {

            }
        });


        task.execute(sb.toString()+String.valueOf(songId));


    }



}
