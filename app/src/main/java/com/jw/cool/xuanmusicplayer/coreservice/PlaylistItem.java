package com.jw.cool.xuanmusicplayer.coreservice;

/**
 * Created by admin on 2015/9/6.
 */
public class PlaylistItem {
    long playListId;
    long audioId;
    long playOrder;
    long _Id;
    String contentDirectory;

    public PlaylistItem(long playListId, long audioId, long playOrder, long _Id, String contentDirectory) {
        this.playListId = playListId;
        this.audioId = audioId;
        this.playOrder = playOrder;
        this._Id = _Id;
        this.contentDirectory = contentDirectory;
    }
}
