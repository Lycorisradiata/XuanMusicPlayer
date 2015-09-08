package com.jw.cool.xuanmusicplayer.fragments;

import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;

/**
 * Created by jw on 2015/9/1.
 */
public class BaseFragment extends Fragment implements HandlerBackPressed {

    @Override
    public boolean handleBackPressed() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
