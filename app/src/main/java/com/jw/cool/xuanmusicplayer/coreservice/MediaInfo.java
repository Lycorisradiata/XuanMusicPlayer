package com.jw.cool.xuanmusicplayer.coreservice;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by jw on 2015/9/2.
 */
public class MediaInfo {
    long id;
    String artist;
    String title;
    String album;
    long duration;
    String displayName;
    long albumId;

    public MediaInfo(long id, String artist, String title, String album,
                     long duration, String displayName, long albumId) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.duration = duration;
        this.displayName = displayName;
        this.albumId = albumId;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public long getDuration() {
        return duration;
    }
    public String getDisplayName() {
        return displayName;
    }

    public long getAlbumId() {
        return albumId;
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "id=" + id +
                ", artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", duration=" + duration +
                ", displayName='" + displayName + '\'' +
                ", albumId=" + albumId +
                '}';
    }

    public Uri getURI() {
        return ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    }
}
