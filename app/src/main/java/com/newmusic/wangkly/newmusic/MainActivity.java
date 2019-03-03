package com.newmusic.wangkly.newmusic;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ViewPager viewPager;

    TabLayout tabLayout;

    FragmentManager fragmentManager;


    MyFragmentPageAdapter fadapter;

    Boolean isPlayingOnTop = false;

    FrameLayout frame ;


    PlayListFragment playListFragment;
    OnlinePlayListFragment onlinePlayListFragment;
    PlayingFragment playingFragment;
    PlayingFullscreenFragment fullscreenFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        List<Fragment> fragments = new ArrayList<>();
        viewPager= findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        frame = findViewById(R.id.frame);

         playListFragment = new PlayListFragment();
         onlinePlayListFragment = new OnlinePlayListFragment();
         playingFragment = new PlayingFragment();
         fullscreenFragment = new PlayingFullscreenFragment();


        fragments.add(playListFragment);
        fragments.add(onlinePlayListFragment);

        List<String> titles = new ArrayList<>();
        titles.add("本地");
        titles.add("网络");
//        tabLayout.addTab(tabLayout.newTab().setText("本地"));
//        tabLayout.addTab(tabLayout.newTab().setText("网络"));




//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                Log.i("tablayout","onTabSelected:"+tab.getText());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


        fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fadapter = new MyFragmentPageAdapter(fragmentManager,fragments,titles);
        viewPager.setAdapter(fadapter);
        viewPager.setCurrentItem(0);

        tabLayout.setupWithViewPager(viewPager);

//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

//        final LinearLayout miniTop = findViewById(R.id.miniTop);
//        final ImageButton backMini = findViewById(R.id.backMini);

//        backMini.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
//                layoutParams.height =50;
//                frame.setLayoutParams(layoutParams);
//
////                miniTop.setVisibility(View.GONE);
//            }
//        });



        fragmentTransaction.add(R.id.frame,playingFragment);
        fragmentTransaction.commit();

        frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
                layoutParams.height =WindowManager.LayoutParams.MATCH_PARENT;
                frame.setLayoutParams(layoutParams);
                isPlayingOnTop =true;
//                FragmentTransaction ft= fragmentManager.beginTransaction();
//                ft.replace(R.id.frame,fullscreenFragment);
//                ft.commit();
                return false;

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(isPlayingOnTop){
            ViewGroup.LayoutParams layoutParams = frame.getLayoutParams();
            layoutParams.height =50;
            frame.setLayoutParams(layoutParams);
            isPlayingOnTop =false;
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
