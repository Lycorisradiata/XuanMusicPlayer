package com.jw.cool.xuanmusicplayer.events;

/**
 * Created by Administrator on 15-9-18.
 */
public class SlidingPaneLayoutEvent {
    boolean needClose;

    public SlidingPaneLayoutEvent(boolean needClose) {
        this.needClose = needClose;
    }

    public boolean isNeedClose() {
        return needClose;
    }
}
