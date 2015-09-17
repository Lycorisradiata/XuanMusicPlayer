package com.jw.cool.xuanmusicplayer.events;

import android.os.Bundle;

/**
 * Created by Administrator on 15-9-17.
 */
public class SwitchFragmentEvent {
    private String fragmentName;
    private Bundle data;

    public SwitchFragmentEvent(String fragmentName, Bundle data) {
        this.fragmentName = fragmentName;
        this.data = data;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public Bundle getData() {
        return data;
    }
}
