package com.jw.cool.xuanmusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnPlayListItemListener;
import com.jw.cool.xuanmusicplayer.adapter.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.adapter.PlayListAdapter;
import com.jw.cool.xuanmusicplayer.adapter.SPNavigationItem;
import com.jw.cool.xuanmusicplayer.adapter.SongListPlayListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;
import com.jw.cool.xuanmusicplayer.events.SwitchFragmentEvent;
import com.jw.cool.xuanmusicplayer.fragments.PlayListSongsFragment;
import com.jw.cool.xuanmusicplayer.fragments.SettingsFragment;
import com.jw.cool.xuanmusicplayer.fragments.SongListFragment;
import com.jw.cool.xuanmusicplayer.utils.HandlerScreen;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by Administrator on 15-9-16.
 */
public class NavigationActivity extends AppCompatActivity{
    private static final String TAG = "NavigationActivity";
    SlidingPaneLayout spl;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Fragment playListSongsFragment;
    Fragment songsListFragment;
    boolean hasAddRightFragment;
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
        toSongsListFragment(null);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("PlayList");
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout_2);
        EventBus.getDefault().register(this);
        spl.openPane();
//        spl.setParallaxDistance(200);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onEvent(SwitchFragmentEvent event) {
        Log.d(TAG, "onEvent name " + event.getFragmentName());
        String name = event.getFragmentName();

        if(SongListFragment.class.getName().equals(name)){
            toSongsListFragment(event);
        }else if(PlayListSongsFragment.class.getName().equals(name)){
            toPlayListSongsFragment(event);
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

    void toSongsListFragment(SwitchFragmentEvent event){
        Log.d(TAG, "toSongsListFragment ");
        if(event != null && songsListFragment != null
                && currentFragment.equals(event.getFragmentName())){
            return;
        }

        if(songsListFragment == null){
            songsListFragment = new SongListFragment();
        }

        if(hasAddRightFragment){
            getSupportFragmentManager().beginTransaction().replace(R.id.navigation_sp_right,
                    songsListFragment).commit();
        }else{
            hasAddRightFragment = true;
            getSupportFragmentManager().beginTransaction().add(R.id.navigation_sp_right,
                    songsListFragment).commit();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
