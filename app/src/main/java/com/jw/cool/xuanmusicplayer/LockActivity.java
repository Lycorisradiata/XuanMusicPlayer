package com.jw.cool.xuanmusicplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.constant.PatternLockStatus;

import java.util.List;

import me.zhanghai.patternlock.PatternView;

public class LockActivity extends AppCompatActivity
    implements PatternView.OnPatternListener, View.OnClickListener{
    private static final String TAG = "LockActivity";
    private final int minimumCellsCount = 6;
    private TextView mSetupPatterLockTextView;
    private PatternView mViewPatternView;
    private Button mClearPatternViewButton;
    private Button mConfirmPatternViewButton;
//    private LinearLayout mClearAndConfirmLinearLayout;
    List<PatternView.Cell> patternViewCells;
    int lockStatus;
    int resultCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        mSetupPatterLockTextView = (TextView) findViewById(R.id.setup_patter_lock);
        mViewPatternView = (PatternView) findViewById(R.id.patter_view);
        mClearPatternViewButton = (Button) findViewById(R.id.clear_pattern_view);
        mConfirmPatternViewButton = (Button) findViewById(R.id.confirm_pattern_view);
//        mClearAndConfirmLinearLayout = (LinearLayout) findViewById(R.id.clear_and_confirm);

        mViewPatternView.setOnPatternListener(this);
        mClearPatternViewButton.setOnClickListener(this);
        mConfirmPatternViewButton.setOnClickListener(this);

        Intent intent = getIntent();
        lockStatus = intent.getIntExtra(PatternLockStatus.KEY, PatternLockStatus.SET_LOCK);
        switchLockStatus();
    }

    void setButtonsVisibility(int visibility){
        mClearPatternViewButton.setVisibility(visibility);
        mConfirmPatternViewButton.setVisibility(visibility);
    }

    void switchLockStatus(){
        switch (lockStatus){
            case PatternLockStatus.SET_LOCK:
            case PatternLockStatus.MODIFY_LOCK_FIRST_DRAW:
                mSetupPatterLockTextView.setText(R.string.setup_pattern_lock_4_cell_at_least);
                setButtonsVisibility(View.INVISIBLE);
                break;
            case PatternLockStatus.SET_LOCK_SECOND_DRAW:
            case PatternLockStatus.MODIFY_LOCK_SECOND_DRAW:
                mSetupPatterLockTextView.setText(R.string.confirm_pattern_lock);
                setButtonsVisibility(View.VISIBLE);
                mConfirmPatternViewButton.setEnabled(false);
                break;
            case PatternLockStatus.UNLOCK:
            case PatternLockStatus.MODIFY_LOCK:
            case PatternLockStatus.CLEAR_LOCK:
                mSetupPatterLockTextView.setText(R.string.please_input_pattern_lock);
                setButtonsVisibility(View.INVISIBLE);
                break;
            case PatternLockStatus.MODIFY_LOCK_SUCCESSFULLY:
            case PatternLockStatus.SET_LOCK_SUCCESSFULLY:
                    mSetupPatterLockTextView.setText(R.string.your_new_patter_lock);
                mConfirmPatternViewButton.setEnabled(true);
                break;
            default:
                break;
        }
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

        if(list.size() < minimumCellsCount){
            //输入图案元素小于最小值，需继续输入
            return;
        }

        switchLockStatus();
        patternViewCells = list;
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick ");
        switch (v.getId()){
            case R.id.clear_pattern_view:
                break;
            case R.id.confirm_pattern_view:
                break;
            default:
                break;
        }
    }

    void onClearButtonClick(){
        switch (lockStatus & 0xff000000){
            case PatternLockStatus.MODIFY_LOCK:
                lockStatus = PatternLockStatus.MODIFY_LOCK_FIRST_DRAW;
                break;
            case PatternLockStatus.SET_LOCK:
                lockStatus = PatternLockStatus.SET_LOCK;
                break;
        }

        switchLockStatus();
    }

    void onConfirmButtonClick() {
    }
}
