package com.jw.cool.xuanmusicplayer;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Point;
import android.os.SystemClock;
import android.test.InstrumentationTestCase;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.jw.cool.xuanmusicplayer.NavigationActivity;

/**
 * Created by admin on 2015/9/21.
 */
public class NavigationActivityTest extends InstrumentationTestCase{
    private static final String TAG = "NavigationActivityTest";
    NavigationActivity navigationActivity;
    Instrumentation instrumentation;

    /**
     * android测试InstrumentationTestRunner使用网址：http://blog.sina.com.cn/s/blog_51335a00010195cp.html
     * android测试详细介绍：http://yelinsen.iteye.com/blog/977736
     * android touch事件测试工具集：touchUtils(android.test.TouchUtils)
     * */
    public void testClick(){
//        MotionEvent motionEvent;
//        motionEvent = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
//                MotionEvent.ACTION_DOWN,
//                960.0, 128.0, 0.0, 0.1, 1, 1.0, 1.0, 0, 0);

//        motionEvent = new MotionEvent.obtain(time1, time2, 0, 960, 128, 0);
        Point point = new Point();
        navigationActivity.getWindowManager().getDefaultDisplay().getSize(point);
//        Log.d(TAG, "testClick point" + point.x + " " + point.y);
//        DisplayMetrics metrics = navigationActivity.getResources().getDisplayMetrics();
//        Log.d(TAG, "testClick density " + metrics.density + " densityDpi" + metrics.densityDpi
//                + " scaledDensity" + metrics.scaledDensity + " xdpi" + metrics.xdpi + " ydpi"
//                +metrics.ydpi + " heightPixels" + metrics.heightPixels + " " + metrics.widthPixels
//        +" ");
//        for(int y = 800 ; y <= point.y ; y += 50){
//            sendTouchEvent(128, y);
//
//            SystemClock.sleep(2000);
//        }
        Log.d(TAG, "testClick will exit");
//        SystemClock.sleep(5000);
    }

    void sendTouchEvent(float x, float y){
        Log.d(TAG, "testClick (x,y) = (" + x + ","+ y + ")");
        MotionEvent downEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
        instrumentation.sendPointerSync(downEvent);
        MotionEvent upEvent = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
        instrumentation.sendPointerSync(upEvent);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d(TAG, "setUp ");
        instrumentation = getInstrumentation();
        Intent intent = new Intent();
        intent.setClassName("com.jw.cool.xuanmusicplayer", "com.jw.cool.xuanmusicplayer.NavigationActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        navigationActivity = (NavigationActivity) instrumentation.startActivitySync(intent);
    }

//    @Override
//    public void tearDown() throws Exception {
//        super.tearDown();
//    }
}
