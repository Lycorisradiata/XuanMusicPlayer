package com.jw.cool.xuanmusicplayer.events;

/**
 * Created by admin on 2015/8/30.
 */
public class CompletionEvent {
    public final boolean isCompleted;

    public CompletionEvent(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
