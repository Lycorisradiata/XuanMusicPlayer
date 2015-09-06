package com.jw.cool.xuanmusicplayer.coreservice;

/**
 * Created by admin on 2015/9/6.
 */
public class PlayList {
    long id;
    String name;
    String data;
    long dateAdd;
    long dateModified;
    int count;

    public PlayList(long id, String name, String data, long dateAdd, long dateModified, int count) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.dateAdd = dateAdd;
        this.dateModified = dateModified;
        this.count = count;
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "count=" + count +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", data='" + data + '\'' +
                ", dateAdd=" + dateAdd +
                ", dateModified=" + dateModified +
                '}';
    }

    public int getCount() {
        return count;
    }

    public String getData() {
        return data;
    }

    public long getDateAdd() {
        return dateAdd;
    }

    public long getDateModified() {
        return dateModified;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
