package com.jw.cool.xuanmusicplayer.utils;

import com.jw.cool.xuanmusicplayer.R;

/**
 * Created by admin on 2015/8/30.
 */
public class HandlerTime {
    /**
     * 计算连个时间之间的秒数
     */

    public static int totalSeconds(String time) {
        String[] et = time.split(":");
        int size = et.length;
        int et_h = 0;
        if(size > 2){
            et_h = Integer.valueOf(et[0]);
        }
        int et_m = Integer.valueOf(et[size - 2]);
        int et_s = Integer.valueOf(et[size - 1]);
        int totalSeconds = et_h * 3600 +  et_m * 60 + et_s;
        return totalSeconds;
    }

    /**
     * 整型秒数转化为hh:mm:ss类型字符串
     *
     * @param totalSeconds 总秒数
     * @return hh:mm:ss类型的字符串
     */

    public static String seconds2HHMMSS(long totalSeconds) {
        String time;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if(hours == 0){
            time = "";
        }else if(hours < 10){
            time = "0" + hours  + ":";
        }else{
            time = "" + hours  + ":";
        }

        if(minutes < 10){
            time = time + "0" + minutes  + ":";
        }else{
            time = time + ":" + minutes  + ":";
        }

        if(seconds < 10){
            time = time + "0" + seconds;
        }else{
            time = time + seconds;
        }

        return time;
    }
}
