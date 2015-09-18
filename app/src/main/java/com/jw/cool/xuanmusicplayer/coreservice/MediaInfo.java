package com.jw.cool.xuanmusicplayer.coreservice;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jw on 2015/9/2.
 */
public class MediaInfo implements Parcelable {
    long id;
    String artist;
    String title;
    String album;
    long duration;
    String displayName;
    long albumId;
    String path;

    public MediaInfo(long id, String artist, String title, String album,
                     long duration, String displayName, long albumId, String path) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.duration = duration;
        this.displayName = displayName;
        this.albumId = albumId;
        this.path = path;
    }

    public String getPath() {
        return path;
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
    public void setDuration(long duration) {
        this.duration = duration;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.artist);
        dest.writeString(this.title);
        dest.writeString(this.album);
        dest.writeLong(this.duration);
        dest.writeString(this.displayName);
        dest.writeLong(this.albumId);
        dest.writeString(this.path);
    }

    protected MediaInfo(Parcel in) {
        this.id = in.readLong();
        this.artist = in.readString();
        this.title = in.readString();
        this.album = in.readString();
        this.duration = in.readLong();
        this.displayName = in.readString();
        this.albumId = in.readLong();
        this.path = in.readString();
    }

    public static final Parcelable.Creator<MediaInfo> CREATOR = new Parcelable.Creator<MediaInfo>() {
        public MediaInfo createFromParcel(Parcel source) {
            return new MediaInfo(source);
        }

        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };
}
