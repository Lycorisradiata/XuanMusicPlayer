package com.jw.cool.xuanmusicplayer.customWidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.utils.HandleMotionEvent;

/**
 * Created by admin on 2015/9/18.
 */
public class MyTextView extends TextView implements NestedScrollingChild {
    private static final String TAG = "MyTextView";
    private final NestedScrollingChildHelper mChildHelper;
    private int mInitialTouchX, mLastTouchX , mInitialTouchY, mLastTouchY ;
    private int mScrollPointerId;
    private int[] mScrollConsumed;
    private int[] mScrollOffset;

    public MyTextView(Context context) {
        super(context);
        mChildHelper = new NestedScrollingChildHelper(this);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mChildHelper = new NestedScrollingChildHelper(this);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mChildHelper = new NestedScrollingChildHelper(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mChildHelper = new NestedScrollingChildHelper(this);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    
    

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        final MotionEvent vtev = MotionEvent.obtain(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mScrollPointerId = MotionEventCompat.getPointerId(ev, 0);
                mInitialTouchX = mLastTouchX = (int) (ev.getX() + 0.5f);
                mInitialTouchY = mLastTouchY = (int) (ev.getY() + 0.5f);

                int nestedScrollAxis = ViewCompat.SCROLL_AXIS_NONE;
//                if (canScrollHorizontally) {
//                    nestedScrollAxis |= ViewCompat.SCROLL_AXIS_HORIZONTAL;
//                }
//                if (canScrollVertically) {
                    nestedScrollAxis |= ViewCompat.SCROLL_AXIS_VERTICAL;
//                }
                startNestedScroll(nestedScrollAxis);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int index = MotionEventCompat.findPointerIndex(ev, mScrollPointerId);
                if (index < 0) {
                    Log.e(TAG, "Error processing scroll; pointer index for id " +
                            mScrollPointerId + " not found. Did any MotionEvents get skipped?");
                    return false;
                }

                final int x = (int) (MotionEventCompat.getX(ev, index) + 0.5f);
                final int y = (int) (MotionEventCompat.getY(ev, index) + 0.5f);
                int dx = mLastTouchX - x;
                int dy = mLastTouchY - y;

                if (dispatchNestedPreScroll(dx, dy, mScrollConsumed, mScrollOffset)) {
                    dx -= mScrollConsumed[0];
                    dy -= mScrollConsumed[1];
                    vtev.offsetLocation(mScrollOffset[0], mScrollOffset[1]);
                    // Updated the nested offsets
//                    mNestedOffsets[0] += mScrollOffset[0];
//                    mNestedOffsets[1] += mScrollOffset[1];
                }

//                if(isOpen() && mInitialMotionX > getChildAt(0).getWidth() &&
//                        HandleMotionEvent.getOrientation(mInitialMotionX, mInitialMotionY, x, y) == HandleMotionEvent.TO_LEFT){
////                    MotionEvent cancelEvent = MotionEvent.obtain(ev);
////                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
////                    return super.onInterceptTouchEvent(cancelEvent);
//                    closePane();
//                    return true;
//                }
            }
        }
        return true;
//        return super.onTouchEvent(event);
    }
}
