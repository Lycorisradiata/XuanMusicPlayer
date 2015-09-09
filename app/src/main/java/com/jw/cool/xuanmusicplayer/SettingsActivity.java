package com.jw.cool.xuanmusicplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.widget.FrameLayout;

/**
 * Created by cao on 2015/9/9.
 */
public class SettingsActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    AppCompatDelegate mDelegate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme();
//        setTheme(android.R.style.Theme_Material_Dialog_Presentation);
        setContentView(R.layout.activity_settings);
//        frameLayout = (FrameLayout) findViewById(R.id.settings_frameLayout);
//        getSupportFragmentManager().beginTransaction().add(R.id.settings_frameLayout,
//                new SettingsFragment()).commit();

    }

//    private AppCompatDelegate getDelegate() {
//        if (mDelegate == null) {
//            mDelegate = AppCompatDelegate.create(this, null);
//        }
//        return mDelegate;
//    }
}
