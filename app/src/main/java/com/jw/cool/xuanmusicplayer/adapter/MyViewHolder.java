package com.jw.cool.xuanmusicplayer.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public int position;

    public MyViewHolder(View view) {
        super(view);
        mView = view;
    }
}