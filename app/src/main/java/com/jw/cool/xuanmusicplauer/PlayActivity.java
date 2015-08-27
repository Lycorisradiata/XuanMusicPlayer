package com.jw.cool.xuanmusicplauer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PlayActivity extends Activity {
    TextView song, singer, album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        song = (TextView) findViewById(R.id.song);
        singer = (TextView) findViewById(R.id.singer);
        album = (TextView) findViewById(R.id.album);
        refresh(null);
    }

    void refresh(Bundle data){
        song.setText("我相信");
        singer.setText("杨安培");
        album.setText("未知专辑");
    }
}
