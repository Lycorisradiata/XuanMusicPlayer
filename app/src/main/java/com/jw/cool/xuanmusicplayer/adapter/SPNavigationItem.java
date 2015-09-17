package com.jw.cool.xuanmusicplayer.adapter;

import java.util.List;

/**
 * Created by Administrator on 15-9-16.
 */
public class SPNavigationItem {
    public final static int TYPE_ONE = 0;
    public final static int TYPE_SECOND = 1;
    String name;
    int type;

    public SPNavigationItem(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
