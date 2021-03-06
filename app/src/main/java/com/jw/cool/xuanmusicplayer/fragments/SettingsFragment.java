package com.jw.cool.xuanmusicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import com.jw.cool.xuanmusicplayer.LockActivity;
import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.constant.PatternLockStatus;

/**
 * Created by ljw on 2015/9/9.
 */
public class SettingsFragment extends PreferenceFragmentCompat
    implements Preference.OnPreferenceChangeListener{
    private static final String TAG = "SettingsFragment";
    ListPreference playModeList;
    CheckBoxPreference setPatternLock;
    Preference modifyPatternLock;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.settings);
//        getPreferenceManager().findPreference()
//        playModeList = (ListPreference) findPreference("play_mode");
        Log.d(TAG, "onCreatePreferences " + playModeList.getSummary());
        String modeIndexString = getPreferenceManager().getSharedPreferences().getString("play_mode", "0");
//        Log.d(TAG, "onCreatePreferences play_mode " + modeIndexString);
        String mode = getResources()
                .getStringArray(R.array.play_mode_list)[Integer.parseInt(modeIndexString)];
        playModeList.setSummary(mode);
//        playModeList.setSummary(playModeList.getValue());
        playModeList.setOnPreferenceChangeListener(this);

        setPatternLock = (CheckBoxPreference) findPreference("setup_pattern_lock");
        setPatternLock.setOnPreferenceChangeListener(this);

        modifyPatternLock =  findPreference("modify_pattern_lock");

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {


        boolean returnValue = super.onPreferenceTreeClick(preference);
        Log.d(TAG, "onPreferenceTreeClick " + preference + "    returnValue " + returnValue);
        Log.d(TAG, "onPreferenceTreeClick " + preference.getKey() + " " + playModeList.getKey());
        if(playModeList.getKey().equals(preference.getKey())){
            String values = ((ListPreference) preference).getValue();
            Log.d(TAG, "onPreferenceTreeClick values " + values);
        }else if(modifyPatternLock.getKey().equals(preference.getKey())){
            Intent intent = new Intent();
            intent.setClass(getContext(), LockActivity.class);
            intent.putExtra(PatternLockStatus.KEY, PatternLockStatus.MODIFY_LOCK);
            startActivityForResult(intent, PatternLockStatus.MODIFY_LOCK);
        }
        return returnValue;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        super.onDisplayPreferenceDialog(preference);
        Log.d(TAG, "onDisplayPreferenceDialog " + preference);
        if(playModeList.getKey().equals(preference.getKey())){
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
        if(playModeList.getKey().equals(preference.getKey())){
            String values = ((ListPreference) preference).getValue();
            String entry = (String) ((ListPreference)preference).getEntry();
            Log.d(TAG, "onPreferenceTreeClick values " + values + " " + entry);

            String mode = getResources()
                            .getStringArray(R.array.play_mode_list)[Integer.parseInt((String)o)];
            playModeList.setSummary(mode);
            return true;
        }else if(setPatternLock.getKey().equals(preference.getKey())){
            Log.d(TAG, "onPreferenceChange isLocked" + o);
            Intent intent = new Intent();
            intent.setClass(getContext(), LockActivity.class);
            int requestCode;
            if(setPatternLock.isChecked()){
                requestCode = PatternLockStatus.CLEAR_LOCK;
            }else{
                requestCode = PatternLockStatus.SET_LOCK;
            }

            intent.putExtra(PatternLockStatus.KEY, requestCode);
            startActivityForResult(intent, requestCode);
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode" + requestCode + " resultCode"+ resultCode);
        boolean isLocked = false;
        switch (requestCode){
            case PatternLockStatus.SET_LOCK:
                isLocked = resultCode == 0;
                setPatternLock.setChecked(isLocked);
                break;
            case PatternLockStatus.CLEAR_LOCK:
                isLocked = resultCode != 0;
                setPatternLock.setChecked(isLocked);
                break;
            default:
                break;
        }
        Log.d(TAG, "onActivityResult isLocked " + isLocked);
    }
}
