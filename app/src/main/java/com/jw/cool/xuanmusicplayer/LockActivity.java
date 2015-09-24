package com.jw.cool.xuanmusicplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jw.cool.xuanmusicplayer.constant.PatternLockStatus;

import java.util.List;

import me.zhanghai.patternlock.PatternUtils;
import me.zhanghai.patternlock.PatternView;

public class LockActivity extends AppCompatActivity
    implements PatternView.OnPatternListener, View.OnClickListener{
    private static final String TAG = "LockActivity";
    private final int MINIMUM_CELL_COUNT = 6;
    private static final int CLEAR_PATTERN_DELAY_MILLI = 2000;
    private TextView mSetupPatterLockTextView;
    private PatternView mViewPatternView;
    private Button mRedrawPatternViewButton;
    private Button mConfirmPatternViewButton;
//    private LinearLayout mClearAndConfirmLinearLayout;
    List<PatternView.Cell> patternViewCells;
    int lockStatus;
    int resultCode = -1;
    String savedPatternLockString;
    String firstPatternLockString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        mSetupPatterLockTextView = (TextView) findViewById(R.id.setup_patter_lock);
        mViewPatternView = (PatternView) findViewById(R.id.patter_view);
        mRedrawPatternViewButton = (Button) findViewById(R.id.redraw_pattern_view);
        mConfirmPatternViewButton = (Button) findViewById(R.id.confirm_pattern_view);
//        mClearAndConfirmLinearLayout = (LinearLayout) findViewById(R.id.clear_and_confirm);

        mViewPatternView.setOnPatternListener(this);
        mRedrawPatternViewButton.setOnClickListener(this);
        mConfirmPatternViewButton.setOnClickListener(this);

        Intent intent = getIntent();
        lockStatus = intent.getIntExtra(PatternLockStatus.KEY, -1);
        resultCode = -1;
        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        savedPatternLockString = sharedPreferences.getString(PatternLockStatus.KEY, "");
        Log.d(TAG, "onCreate savedPatternLockString " + savedPatternLockString
                + " lockStatus " + lockStatus);
        switchLockStatus();
    }

    void setButtonsVisibility(int visibility){
        mRedrawPatternViewButton.setVisibility(visibility);
        mConfirmPatternViewButton.setVisibility(visibility);
    }

    int switchLockStatus(){
        int returnValue = -1;
        Log.d(TAG, "switchLockStatus lockStatus " + lockStatus);
        switch (lockStatus){
            case PatternLockStatus.SET_LOCK:
            case PatternLockStatus.MODIFY_LOCK_CONFIRM_LOCK_FINISH:
                mSetupPatterLockTextView.setText(R.string.setup_pattern_lock_4_cell_at_least);
                setButtonsVisibility(View.INVISIBLE);
                mViewPatternView.clearPattern();
                mViewPatternView.setEnabled(true);
                break;
            case PatternLockStatus.SET_LOCK_FIRST_DRAW_FINISH:
            case PatternLockStatus.MODIFY_LOCK_FIRST_DRAW_FINISH:
                mSetupPatterLockTextView.setText(R.string.has_record_pattern_lock);
                mViewPatternView.setEnabled(false);
                mSetupPatterLockTextView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mViewPatternView.setEnabled(true);
                        setButtonsVisibility(View.VISIBLE);
                        mViewPatternView.clearPattern();
                        mConfirmPatternViewButton.setEnabled(false);
                        mSetupPatterLockTextView.setText(R.string.confirm_pattern_lock);
                    }
                }, 1500);

                break;
            case PatternLockStatus.UNLOCK:
            case PatternLockStatus.MODIFY_LOCK:
            case PatternLockStatus.CLEAR_LOCK:
                mSetupPatterLockTextView.setText(R.string.please_input_pattern_lock);
                setButtonsVisibility(View.INVISIBLE);
                mViewPatternView.setEnabled(true);
                break;
            case PatternLockStatus.MODIFY_LOCK_SECOND_DRAW_FINISH:
            case PatternLockStatus.SET_LOCK_SECOND_DRAW_FINISH:
                mSetupPatterLockTextView.setText(R.string.your_new_patter_lock);
                mViewPatternView.setEnabled(false);
                mConfirmPatternViewButton.setEnabled(true);
                break;
            case PatternLockStatus.CLEAR_LOCK_CONFIRM_LOCK_FINISH:
                SharedPreferences sharedPreferences
                        = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PatternLockStatus.KEY, "");
                editor.commit();
                resultCode = 0;
                finish();
                break;
            case PatternLockStatus.UNLOCK__CONFIRM_LOCK_FINISH:
                resultCode = 0;
                finish();
                break;
            default:
                Log.d(TAG, "switchLockStatus lockStatus " + lockStatus);
                finish();
                break;
        }

        return returnValue;
    }

    @Override
    public void finish() {
        setResult(resultCode);
        super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPatternStart() {
        Log.d(TAG, "onPatternStart ");
        mSetupPatterLockTextView.setText(R.string.finger_up_after_finish_input);
    }

    @Override
    public void onPatternCleared() {
        Log.d(TAG, "onPatternCleared ");

    }

    @Override
    public void onPatternCellAdded(List<PatternView.Cell> list) {
        Log.d(TAG, "onPatternCellAdded ");
    }

    @Override
    public void onPatternDetected(List<PatternView.Cell> list) {
        Log.d(TAG, "onPatternDetected ");

        if(list.size() < MINIMUM_CELL_COUNT){
            //输入图案元素小于最小值，需继续输入
            handleWrongStatus();

            return;
        }
        String string = PatternUtils.patternToSha1String(list);
        boolean isConfirmed = false;
        Log.d(TAG, "onPatternDetected string " + string);
        //已设置过解锁图案，则先需确认解锁图案
        if(!TextUtils.isEmpty(savedPatternLockString)){
            if(string.equals(savedPatternLockString)){
                //验证通过
                lockStatus += 1;
                savedPatternLockString = null;
                isConfirmed = true;
            }else{
                //绘制图案与已保存的不一致，需重新绘制
                handleWrongStatus();
                return;
            }
        }

        Log.d(TAG, "onPatternDetected isConfirmed " + isConfirmed);
        if(!isConfirmed){
            //若是第二次绘制解锁图案，则比较是否相同(firstPatternLockString不为空表示已绘制过一次)
            if(TextUtils.isEmpty(firstPatternLockString)){
                lockStatus += 1;
                firstPatternLockString = string;
            }else{
                if(string.equals(firstPatternLockString)){
                    lockStatus += 1;
                }else{
                    //两次绘制的图案不一致，需重新绘制
                    handleWrongStatus();
                    return;
                }
            }
        }

        switchLockStatus();
//        if(switchLockStatus() == 0){
//            finish();
//        }
    }

    void handleWrongStatus(){
        mViewPatternView.setDisplayMode(PatternView.DisplayMode.Wrong);
        mSetupPatterLockTextView.setText(R.string.please_input_again);
        postClearPatternRunnable();
    }

    void postClearPatternRunnable() {
        mViewPatternView.postDelayed(clearPatternRunnable, CLEAR_PATTERN_DELAY_MILLI);
    }

    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mViewPatternView.clearPattern();
        }
    };


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick ");
        switch (v.getId()){
            case R.id.redraw_pattern_view:
                onRedrawButtonClick();
                break;
            case R.id.confirm_pattern_view:
                onConfirmButtonClick();
                break;
            default:
                break;
        }
    }

    void onRedrawButtonClick(){
        switch (lockStatus & 0xff000000){
            case PatternLockStatus.MODIFY_LOCK:
                mViewPatternView.clearPattern();
                firstPatternLockString = null;
                lockStatus = PatternLockStatus.MODIFY_LOCK_CONFIRM_LOCK_FINISH;
                switchLockStatus();
                break;
            case PatternLockStatus.SET_LOCK:
                mViewPatternView.clearPattern();
                firstPatternLockString = null;
                lockStatus = PatternLockStatus.SET_LOCK;
                switchLockStatus();
                break;
        }
    }

    void onConfirmButtonClick() {
        SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PatternLockStatus.KEY, firstPatternLockString);
        editor.commit();
        resultCode = 0;
        finish();
    }
}
