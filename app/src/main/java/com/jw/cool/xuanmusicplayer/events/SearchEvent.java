package com.jw.cool.xuanmusicplayer.events;

/**
 * Created by cao on 2015/8/31.
 */
public class SearchEvent {
    public boolean isNeedQuery;
    public String searchText;

    public SearchEvent(String searchText, boolean isNeedQuery){
        this.isNeedQuery = isNeedQuery;
        this.searchText = searchText;
    }
}
