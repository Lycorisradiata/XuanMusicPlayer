package com.jw.cool.xuanmusicplayer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.constant.PatternLockStatus;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.events.PopupWindowEvent;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;
import com.jw.cool.xuanmusicplayer.events.SlidingPaneLayoutEvent;
import com.jw.cool.xuanmusicplayer.events.SwitchFragmentEvent;
import com.jw.cool.xuanmusicplayer.fragments.BaseFragment;
import com.jw.cool.xuanmusicplayer.fragments.NavigationLeftFragment;
import com.jw.cool.xuanmusicplayer.fragments.PlayListSongsFragment;
import com.jw.cool.xuanmusicplayer.fragments.SettingsFragment;
import com.jw.cool.xuanmusicplayer.fragments.SongListFragment;
import com.jw.cool.xuanmusicplayer.popupWindows.PopWin;
import com.jw.cool.xuanmusicplayer.utils.HandlerScreen;

import de.greenrobot.event.EventBus;

/**
 * Created by ljw on 15-9-16.
 */
public class NavigationActivity extends AppCompatActivity{
    private static final String TAG = "NavigationActivity";
    SlidingPaneLayout spl;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Fragment playListSongsFragment;
    Fragment songsListFragment;
    Fragment settingsFragment;
    String currentFragment = "";
    PopupWindow playPopupWindow;
    ImageButton playOrPause;
    TextView songName;
    boolean isPlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate ");
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
        handlePatterLock();
//        spl.setParallaxDistance(200);
    }

    void handlePatterLock(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isLocked = sharedPreferences.getBoolean("setup_pattern_lock", false);
        Log.d(TAG, "handlePatterLock isLocked " + isLocked);
        if (isLocked){
            Intent intent = new Intent();
            intent.setClass(this, LockActivity.class);
            int requestCode = PatternLockStatus.UNLOCK;
            intent.putExtra(PatternLockStatus.KEY, requestCode);
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode " + requestCode + " resultCode " + resultCode);
        if(requestCode == PatternLockStatus.UNLOCK){
            if(resultCode == 0){
                Log.d(TAG, "onActivityResult unlock success ");
            }else{
                Log.d(TAG, "onActivityResult unlock failed, need finish");
                finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    //    PopupWindow playPopupWindow;
    void showPlayPopupWindow(){
        if(MusicRetriever.getInstance().getCurrentPos() < 0){
            return;
        }

        if (playPopupWindow == null) {
            playPopupWindow = PopWin.getPlayWindow(this, null);
            songName = (TextView) playPopupWindow.getContentView()
                    .findViewById(R.id.play_popup_text);
            songName.setText(MusicRetriever.getInstance().getCurrentItem().getDisplayName());
            playOrPause = (ImageButton) playPopupWindow.getContentView()
                    .findViewById(R.id.play_popup_play_or_pause);
            playOrPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isPlaying){
                        //暂停播放，切换button背景，
                        playOrPause.setBackgroundResource(R.drawable.btn_pause);
                    }else{
                        //播放指定歌曲，切换button背景，
                        playOrPause.setBackgroundResource(R.drawable.btn_play);
                    }
                    isPlaying = !isPlaying;
                    Intent intent = new Intent();
                    intent.setAction(MusicService.ACTION_TOGGLE_PLAYBACK);
                    intent.setPackage(getPackageName());//这里你需要设置你应用的包名
                    startService(intent);
                }
            });
        }

        if(!playPopupWindow.isShowing()){
            playPopupWindow.showAtLocation(spl, Gravity.BOTTOM, 0, 0);
        }
    }

    void dismissPlayPopupWindow(){
        if(playPopupWindow != null && playPopupWindow.isShowing()){
            playPopupWindow.dismiss();
        }
    }

    public void onEventMainThread(PopupWindowEvent event){
        Log.d(TAG, "onEvent event.isNeedShow() " + event.isNeedShow());
        if(event.isNeedShow()){
            showPlayPopupWindow();
        }else{
            dismissPlayPopupWindow();
        }
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
