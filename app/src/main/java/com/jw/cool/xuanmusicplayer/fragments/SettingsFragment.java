package com.jw.cool.xuanmusicplayer.fragments;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import com.jw.cool.xuanmusicplayer.R;

/**
 * Created by cao on 2015/9/9.
 */
public class SettingsFragment extends PreferenceFragmentCompat
    implements Preference.OnPreferenceChangeListener{
    private static final String TAG = "SettingsFragment";
    ListPreference playModeList;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
//        getPreferenceManager().findPreference()
        playModeList = (ListPreference) findPreference("play_mode");
        Log.d(TAG, "onCreatePreferences " + playModeList.getSummary());

        playModeList.setSummary(playModeList.getValue());
        playModeList.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {


        boolean returnValue = super.onPreferenceTreeClick(preference);
        Log.d(TAG, "onPreferenceTreeClick " + preference + "    returnValue " + returnValue);
        Log.d(TAG, "onPreferenceTreeClick " + preference.getKey() + " " + playModeList.getKey());
        if(preference.getKey() == playModeList.getKey()){
            String values = ((ListPreference) preference).getValue();
            Log.d(TAG, "onPreferenceTreeClick values " + values);
        }
        return returnValue;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        super.onDisplayPreferenceDialog(preference);
        Log.d(TAG, "onDisplayPreferenceDialog " + preference);
        if(preference.getKey() == playModeList.getKey()){
            String values = ((ListPreference) preference).getValue();
            Log.d(TAG, "onPreferenceTreeClick values " + values);
        }
    }

    @Override
    public void onNavigateToScreen(PreferenceScreen preferenceScreen) {
//        Log.d(TAG, "onNavigateToScreen " + preferenceScreen);
        super.onNavigateToScreen(preferenceScreen);
    }

    //返回false，则不保存修改，反之亦然,
    //Object o 为修改后的值， preference 为当前值（修改前的值）
    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        Log.d(TAG, "onPreferenceChange preference " + preference);
        Log.d(TAG, "onPreferenceChange " + o);
        if(preference.getKey() == playModeList.getKey()){
            String values = ((ListPreference) preference).getValue();
            String entry = (String) ((ListPreference)preference).getEntry();
            Log.d(TAG, "onPreferenceTreeClick values " + values + " " + entry);

            playModeList.setSummary((String)o);
        }
//        return false;

        return true;
    }
}
