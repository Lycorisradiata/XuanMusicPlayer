package com.jw.cool.xuanmusicplayer.events;

/**
 * Created by ljw on 15-9-25.
 */
public class PopupWindowEvent {
    boolean needShow;
    int id;

    public PopupWindowEvent(boolean needShow, int id) {
        this.needShow = needShow;
        this.id = id;
    }

    public boolean isNeedShow() {
        return needShow;
    }

    public int getId() {
        return id;
    }
}
