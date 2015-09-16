package com.jw.cool.xuanmusicplayer.customWidget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.jw.cool.xuanmusicplayer.utils.HandleMotionEvent;

/**
 * Created by apaojun on 2015/2/12.
 */
public class PagerEnabledSlidingPaneLayout extends SlidingPaneLayout {

    private static final String TAG = "PagerSlidingPane";
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mEdgeSlop;

    public PagerEnabledSlidingPaneLayout(Context context) {
        this(context, null);
    }

    public PagerEnabledSlidingPaneLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerEnabledSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ViewConfiguration config = ViewConfiguration.get(context);
        mEdgeSlop = config.getScaledEdgeSlop();
    }



//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
////        Log.d(TAG, "onTouchEvent ev " + ev.getAction() + " " + ev.getX());
//        switch (MotionEventCompat.getActionMasked(ev)) {
//            case MotionEvent.ACTION_DOWN: {
//                mInitialMotionX = ev.getX();
//                mInitialMotionY = ev.getY();
//                break;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
//                final float x = ev.getX();
//                final float y = ev.getY();
////                Log.d(TAG, "onInterceptTouchEvent x " + x);
//                int size = ev.getHistorySize();
////                for(int i= 0; i < size; i++){
////                    float xold = ev.getHistoricalAxisValue(MotionEvent.AXIS_X, i);
////                    Log.d(TAG, "onInterceptTouchEvent xold " + xold);
////                }
//
//                int orientation = 0;
//                if(size > 0){
//                    float startX = ev.getHistoricalAxisValue(MotionEvent.AXIS_X, size - 1);
//                    float startY = ev.getHistoricalAxisValue(MotionEvent.AXIS_Y, size - 1);
//                    orientation = HandleMotionEvent.getOrientation(startX, startY, x, y);
//                    Log.d(TAG, "onTouchEvent size " + size + " orientation " + orientation);
//                    if(isOpen()){
//                        //打开状态，只处理向左滑动作
//                        if(orientation == HandleMotionEvent.TO_LEFT){
//                            closePane();
//                            ev.setAction(MotionEvent.ACTION_CANCEL);
//                            super.onTouchEvent(ev);
//                            return true;
//                        }
//                    }else{
//                        //非打开状态，只处理向右滑动作
//                        if(orientation == HandleMotionEvent.TO_RIGHT){
//                            openPane();
//                            ev.setAction(MotionEvent.ACTION_CANCEL);
//                            super.onTouchEvent(ev);
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return super.onTouchEvent(ev);
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                // The user should always be able to "close" the pane, so we only check
                // for child scrollability if the pane is currently closed.
//                if (mInitialMotionX > mEdgeSlop && !isOpen() && canScroll(this, false,
//                        Math.round(x - mInitialMotionX), Math.round(x), Math.round(y))) {
//
//                    // How do we set super.mIsUnableToDrag = true?
//
//                    // send the parent a cancel event
//                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
//                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
//                    return super.onInterceptTouchEvent(cancelEvent);
//                }

//                int w0 = ;
//                int h0 = getChildAt(0).getHeight();
//                int w1 = getChildAt(1).getWidth();
//                int h1 = getChildAt(1).getHeight();
//                Log.d(TAG, "onInterceptTouchEvent " + w0 + " " + h0 + " " + w1 + " " + h1 );
//                Log.d(TAG, "onInterceptTouchEvent " + w0 + " " + mInitialMotionX);
                if(isOpen() && mInitialMotionX > getChildAt(0).getWidth() &&
                    HandleMotionEvent.getOrientation(mInitialMotionX, mInitialMotionY, x, y) == HandleMotionEvent.TO_LEFT){
//                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
//                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
//                    return super.onInterceptTouchEvent(cancelEvent);
                    closePane();
                    return true;
                }
            }
        }

        return super.onInterceptTouchEvent(ev);
    }
}