package com.jw.cool.xuanmusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;
import com.jw.cool.xuanmusicplayer.events.SlidingPaneLayoutEvent;
import com.jw.cool.xuanmusicplayer.events.SwitchFragmentEvent;
import com.jw.cool.xuanmusicplayer.fragments.BaseFragment;
import com.jw.cool.xuanmusicplayer.fragments.NavigationLeftFragment;
import com.jw.cool.xuanmusicplayer.fragments.PlayListSongsFragment;
import com.jw.cool.xuanmusicplayer.fragments.SettingsFragment;
import com.jw.cool.xuanmusicplayer.fragments.SongListFragment;
import com.jw.cool.xuanmusicplayer.utils.HandlerScreen;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 15-9-16.
 */
public class NavigationActivity extends AppCompatActivity{
    private static final String TAG = "NavigationActivity";
    SlidingPaneLayout spl;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Fragment playListSongsFragment;
    Fragment songsListFragment;
    Fragment settingsFragment;
    String currentFragment = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        HandlerScreen.setStatusAndNavigationBarTranslucent(this);
        Intent intent = new Intent(this,MusicService.class);
        startService(intent);
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_sp_left,
                new NavigationLeftFragment()).commit();
        songsListFragment = new SongListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.navigation_sp_right,
                songsListFragment).commit();
        currentFragment = SongListFragment.class.getName();
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("PlayList");
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout_2);
        spl.openPane();
//        spl.setParallaxDistance(200);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    public void onEvent(SwitchFragmentEvent event) {
        Log.d(TAG, "onEvent name " + event.getFragmentName());
        String name = event.getFragmentName();

        if(SongListFragment.class.getName().equals(name)){
            toSongsListFragment();
        }else if(PlayListSongsFragment.class.getName().equals(name)){
            toPlayListSongsFragment(event);
        }else if (SettingsFragment.class.getName().equals(name)){
            toSettingsFragment();
        }
        currentFragment = name;
        Log.d(TAG, "onEvent ");
    }

    void toPlayListSongsFragment(SwitchFragmentEvent event){
        Log.d(TAG, "toPlayListSongsFragment currentFragment" + currentFragment);
        if(playListSongsFragment == null){
            playListSongsFragment = new PlayListSongsFragment();
        }

        PlayListSongsFragment.setPlayListId(event.getData().getLong(PlayListSongsFragment.PLAY_LIST_ID, 0));
        if(currentFragment.equals(event.getFragmentName())){
            ((PlayListSongsFragment)playListSongsFragment).refreshSongList();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.navigation_sp_right,
                    playListSongsFragment).commit();
        }
    }

    void toSongsListFragment(){
        Log.d(TAG, "toSongsListFragment ");
        if(songsListFragment == null){
            songsListFragment = new SongListFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_sp_right,
                songsListFragment).commit();
    }

    void toSettingsFragment(){
        if(settingsFragment == null){
            settingsFragment = new SettingsFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_sp_right,
                settingsFragment).commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed currentFragment " + currentFragment);
        boolean isSame = currentFragment.equals(SongListFragment.class.getName());

        if(isSame){
            if(((BaseFragment)songsListFragment).handleBackPressed()){
                Log.d(TAG, "onBackPressed songsListFragment consume");
                return;
            }
        }

        super.onBackPressed();
    }

    public void onEvent(SlidingPaneLayoutEvent event){
        Log.d(TAG, "onEvent SlidingPaneLayoutEvent " + event.isNeedClose());
        if(event.isNeedClose() && spl.isOpen()){
            spl.closePane();
        }
    }
}
