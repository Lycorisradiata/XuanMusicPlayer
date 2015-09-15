package com.jw.cool.xuanmusicplayer.utils;

import android.view.MotionEvent;

/**
 * Created by Administrator on 15-9-14.
 */
public class HandleMotionEvent {
    public static int NO_MOVE = 0;
    public static int TO_LEFT = 1;
    public static int TO_RIGHT = 3;
    public static int TO_UP = 2;
    public static int TO_DOWN = 4;
    public static float MINIMUM_DISTANCE = 0;
    public static int getOrientation(MotionEvent start, MotionEvent end){
        return getOrientation(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public static int getOrientation(float startX,  float startY, float endX,  float endY){
        int orientation = NO_MOVE;
        float xDiff = endX - startX;
        float yDiff = endY - startY;
        if(Math.abs(xDiff) > MINIMUM_DISTANCE || Math.abs(yDiff) > MINIMUM_DISTANCE){
            boolean isXMove = Math.abs(xDiff) > Math.abs(yDiff);
            if(isXMove){
                orientation = xDiff > 0 ? TO_RIGHT : TO_LEFT;
            }else{
                orientation = yDiff > 0 ? TO_DOWN : TO_UP;
            }
        }
        return orientation;
    }
}
