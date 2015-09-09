package com.jw.cool.xuanmusicplayer.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.jw.cool.xuanmusicplayer.R;

/**
 * Created by cao on 2015/9/9.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
    }
}
