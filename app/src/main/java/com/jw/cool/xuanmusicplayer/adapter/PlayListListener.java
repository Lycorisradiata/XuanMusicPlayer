package com.jw.cool.xuanmusicplayer.adapter;

import android.view.View;

/**
 * Created by admin on 2015/9/13.
 */
public interface PlayListListener {
    void onItemClick(View v, int pos);
    void onDeleteButtonClick(View v, int pos);
}
