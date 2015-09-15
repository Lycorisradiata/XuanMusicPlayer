package com.jw.cool.xuanmusicplayer.adapter;

import android.view.View;

/**
 * Created by jw on 2015/9/7.
 */
public interface OnSongListItemClickListener {
    void onSongListItemClick(View view, int position);
    void onSongListItemLongClick(View view, int position);
    void onSongListDeleteButtonClick(View view, int position);
}
