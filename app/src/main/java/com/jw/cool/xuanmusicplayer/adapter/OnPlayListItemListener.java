package com.jw.cool.xuanmusicplayer.adapter;

import android.view.View;

/**
 * Created by admin on 2015/9/13.
 */
public interface OnPlayListItemListener {
    void onPlayListItemClick(View v, int pos);
    void onPlayListDeleteButtonClick(View v, int pos);
}
