package com.jw.cool.xuanmusicplayer.events;

/**
 * Created by Administrator on 15-9-18.
 */
public class MediaPlayerStatusEvent {
    boolean isPrepared;

    public MediaPlayerStatusEvent(boolean isPrepared) {
        this.isPrepared = isPrepared;
    }

    public boolean isPrepared() {
        return isPrepared;
    }
}
