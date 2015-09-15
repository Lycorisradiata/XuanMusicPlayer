package com.jw.cool.xuanmusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 15-9-15.
 */
public class SlidingPaneLayoutActivity extends Activity {
    private static final String TAG = "SlidingPaneActivity";
    SlidingPaneLayout spl;
    TextView rightTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sliding_panel_layout);
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(TAG, "onPanelSlide " + panel + " slideOffset " + slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {
                Log.d(TAG, "onPanelOpened " + panel);
            }

            @Override
            public void onPanelClosed(View panel) {
                Log.d(TAG, "onPanelClosed " + panel);
            }
        });

        rightTextView = (TextView) findViewById(R.id.sliding_pane_textview);
        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick v ");
                if(spl.isOpen()){
                    spl.closePane();
                }
            }
        });

    }
}
