package com.jw.cool.xuanmusicplayer.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by jw on 2015/9/7.
 */
public class HandlerScreen {
    private static final  String TAG = "HandlerScreen" ;

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        Log.d(TAG, "getStatusBarHeight result " + result);
        return result;
    }
}
