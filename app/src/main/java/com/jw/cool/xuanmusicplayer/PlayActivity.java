package com.jw.cool.xuanmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.events.CompletionEvent;
import com.jw.cool.xuanmusicplayer.events.ProcessEvent;
import com.jw.cool.xuanmusicplayer.utils.HandlerTime;

import de.greenrobot.event.EventBus;

public class PlayActivity extends Activity implements View.OnClickListener{
    final String TAG = "PlayActivity";
    TextView song, singer, album, currentTime, totalTime;
    SeekBar seekBar;
    Button previous, playOrPause, next;
    boolean isNeedRefreshMediaInfo;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        song = (TextView) findViewById(R.id.song);
        singer = (TextView) findViewById(R.id.singer);
        album = (TextView) findViewById(R.id.album);
        currentTime = (TextView) findViewById(R.id.current_time);
        totalTime = (TextView) findViewById(R.id.total_time);
        previous = (Button) findViewById(R.id.previous);
        previous.setOnClickListener(this);
        playOrPause = (Button) findViewById(R.id.play_or_pause);
        playOrPause.setOnClickListener(this);
        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.image);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d(TAG, "onProgressChanged i " + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch seekBar " + seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch seekBar " + seekBar.getProgress() + " " + seekBar.getMax());
                String action = MusicService.ACTION_SEEK;
                int totalMilliseconds = HandlerTime.totalSeconds(totalTime.getText().toString())*1000;
                int seekPos = totalMilliseconds * seekBar.getProgress() / seekBar.getMax();
                sendAction(action, seekPos);
            }
        });
        refresh(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        isNeedRefreshMediaInfo = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(ProcessEvent event) {
        Log.d(TAG, "onEventMainThread isNeedRefreshMediaInfo " + isNeedRefreshMediaInfo);
        if(isNeedRefreshMediaInfo){
            refresh(MusicRetriever.getCurrentItem());
        }
        currentTime.setText(HandlerTime.seconds2HHMMSS(event.currentPos / 1000));
        seekBar.setProgress(event.currentPos * 100 / event.totalMilliSeconds);
    }
    public void onEvent(CompletionEvent event) {
        Log.d(TAG, "onEventThread event.isCompleted " + event.isCompleted);
        if(event.isCompleted){
            isNeedRefreshMediaInfo = true;
        }
    }


    void refresh(MusicRetriever.Item item){
        Log.d(TAG, "refresh item " + isNeedRefreshMediaInfo);
        if(item != null){
            song.setText(item.getDisplayName());
            singer.setText(item.getArtist());
            album.setText(item.getAlbum());
            totalTime.setText(HandlerTime.seconds2HHMMSS(item.getDuration() / 1000));
            isNeedRefreshMediaInfo = false;
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick view" + view);
        Log.d(TAG, "onClick playOrPause" + playOrPause);
        String action = null;

        if(view == previous){
            action = MusicService.ACTION_PREVIOUS;
        }else if (view == playOrPause){
            action = MusicService.ACTION_TOGGLE_PLAYBACK;
        }else if (view == next){
            action = MusicService.ACTION_SKIP;
        }

        sendAction(action, -1);
    }

    void sendAction(String action, int seekPos){
        if(action != null && action.length() > 0){
            Intent intent = new Intent();
            intent.setAction(action);
            intent.setPackage(getPackageName());//这里你需要设置你应用的包名
            if(seekPos >= 0){
                intent.putExtra("seekPos", seekPos);
            }
            startService(intent);
            isNeedRefreshMediaInfo = true;
            Log.d(TAG, "onClick intent " + intent.getAction());
        }
    }

//    intent.setAction(MusicService.ACTION_PLAY);
//    MusicRetriever.Item item = itemList.get(position);
//    Bundle bundle = new Bundle();
////        Item item = new Item(bundle.getLong("id"),
////                strEmpty ,
////                bundle.getString("title"),
////                strEmpty,
////                bundle.getLong("duration"),
////                bundle.getString("displayName")
//    bundle.putLong("id", item.getId());
//    bundle.putLong("duration", item.getDuration());
//    bundle.putString("title", item.getTitle());
//    bundle.putString("displayName", item.getDisplayName());
//    intent.putExtras(bundle);
//    getActivity().startService(intent);
}
