package com.jw.cool.xuanmusicplayer.popupWindows;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.R;

/**
 * Created by cao on 2015/9/8.
 */
public class PopWin {
    public static PopupWindow getOperateWindow(Context context, View.OnClickListener listener){
        View layout =  LayoutInflater.from(context).inflate(R.layout.operate_popup_window_song_list, null);
        Button addToPlaylist = (Button) layout.findViewById(R.id.add_to_playlist);
        addToPlaylist.setOnClickListener(listener);
        Button remove = (Button) layout.findViewById(R.id.remove);
        remove.setOnClickListener(listener);
        Button more = (Button) layout.findViewById(R.id.more);
        more.setOnClickListener(listener);
        PopupWindow operatePopupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        operatePopupWindow.setTouchable(true);
        operatePopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch ");
                return false;
            }
        });
//            operatePopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.song_list_background, null)));
        return operatePopupWindow;
    }

    public static PopupWindow getSelectWindow(Context context, View.OnClickListener listener){
        View layout =  LayoutInflater.from(context).inflate(R.layout.select_popup_window_song_list, null);
        Button selectAll = (Button) layout.findViewById(R.id.select_all);
        selectAll.setOnClickListener(listener);
        Button selectOthers = (Button) layout.findViewById(R.id.select_others);
        selectOthers.setOnClickListener(listener);
        Button selectCancel = (Button) layout.findViewById(R.id.select_cancel);
        selectCancel.setOnClickListener(listener);
        Button selectNumber = (Button) layout.findViewById(R.id.select_number);
        PopupWindow selectPopupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectPopupWindow.setTouchable(true);
        selectPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch ");
                return false;
            }
        });
//            selectPopupWindow.setBackgroundDrawable(
//                    new ColorDrawable(getResources().getDrawable(R.color.select_popup_window_background));

        return selectPopupWindow;
    }

    public static PopupWindow getPlayWindow(Context context, View.OnClickListener listener){
        View layout =  LayoutInflater.from(context).inflate(R.layout.play_popup_window, null);
//        ImageButton playOrPause = (ImageButton) layout.findViewById(R.id.play_popup_play_or_pause);
//        playOrPause.setOnClickListener(listener);
//        TextView textView = (TextView) layout.findViewById(R.id.play_popup_text);
        PopupWindow playPopupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        selectPopupWindow.setTouchable(true);
        playPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch ");
                return false;
            }
        });
//        Drawable drawable = new ColorDrawable(context.getColor(R.color.play_popup_window));
//        playPopupWindow.setBackgroundDrawable(drawable);
//        playPopupWindow.update();
//        playPopupWindow.getOverlapAnchor()

        return playPopupWindow;
    }
}
