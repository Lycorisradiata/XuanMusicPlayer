package com.jw.cool.xuanmusicplayer.fragments;

import android.support.v4.app.Fragment;

/**
 * Created by cao on 2015/9/1.
 */
public class BaseFragment extends Fragment implements HandleBackPressed{

    @Override
    public boolean handleBackPressed() {
        return false;
    }
}
