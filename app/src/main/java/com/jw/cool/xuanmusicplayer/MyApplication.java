package com.jw.cool.xuanmusicplayer;

import android.app.Application;
import android.support.v7.preference.PreferenceManager;

import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;

/**
 * Created by cao on 2015/9/2.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        MusicRetriever.initInstance(getApplicationContext());
        String playMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("play_mode", "");
        if(!playMode.isEmpty()){
            MusicRetriever.getInstance().setPlayMode(Integer.valueOf(playMode));
        }
    }
}
