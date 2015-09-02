package com.jw.cool.xuanmusicplayer;

import android.app.Application;

import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;

/**
 * Created by cao on 2015/9/2.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        MusicRetriever.initInstance(getApplicationContext());
    }
}
