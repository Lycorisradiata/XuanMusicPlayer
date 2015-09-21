package com.jw.cool.xuanmusicplauer;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.jw.cool.xuanmusicplayer.NavigationActivity;

/**
 * Created by admin on 2015/9/21.
 */
public class NavigationActivityTest extends InstrumentationTestCase{
    private static final String TAG = "NavigationActivityTest";
    NavigationActivity navigationActivity;
    Instrumentation instrumentation;

    public void testClick(){

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d(TAG, "setUp ");
        instrumentation = getInstrumentation();
        Intent intent = new Intent();
        intent.setClassName("com.jw.cool.xuanmusicplauer", "com.jw.cool.xuanmusicplauer.NavigationActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        navigationActivity = (NavigationActivity) instrumentation.startActivitySync(intent);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
