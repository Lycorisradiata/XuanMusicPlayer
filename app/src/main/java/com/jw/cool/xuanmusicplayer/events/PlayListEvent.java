package com.jw.cool.xuanmusicplayer.events;

/**
 * Created by Administrator on 15-9-18.
 */
public class PlaylistEvent {
    boolean isAddPlaylist;

    public PlaylistEvent(boolean isAddPlaylist) {
        this.isAddPlaylist = isAddPlaylist;
    }

    public boolean isAddPlaylist() {
        return isAddPlaylist;
    }
}
