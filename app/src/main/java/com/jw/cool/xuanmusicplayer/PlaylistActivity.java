package com.jw.cool.xuanmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.jw.cool.xuanmusicplayer.adapters.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.adapters.SongListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.utils.HandlerScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jw on 2015/9/7.
 */
public class PlaylistActivity extends Activity implements OnSongListItemClickListener, View.OnClickListener{
    private static final String TAG = "PlaylistActivity";
    RecyclerView recyclerView;
    List<MediaInfo> itemList;
    RecyclerView.Adapter adapter;
    Button selectAll,  selectOthers, selectCancel, selectNumber;
    Button addToPlaylist, remove, more;
    boolean isNeedShowSelectBox;
    PopupWindow selectPopupWindow;
    PopupWindow operatePopupWindow;
    boolean[] selectedStatus;
    int selectedItemsCount;
    String playlistName;
    long playlistId;
    List<String> itemsName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        HandlerScreen.setStatusAndNavigationBarTranslucent(this);
        recyclerView = (RecyclerView) findViewById(R.id.playlist_recycleView);
        Bundle bundle = getIntent().getExtras();
        recyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        playlistId = bundle.getLong("id", -1);
        playlistName = bundle.getString("name", "");
        itemList = MusicRetriever.getInstance().getPlaylistItems(playlistId);
        refreshItemsName();
        adapter = new SongListAdapter(this, itemsName, this);
        recyclerView.setAdapter(adapter);
    }

    void refreshItemsName(){
        if(itemsName == null){
            itemsName = new ArrayList<>();
        }else{
            itemsName.clear();
        }

        for(MediaInfo item:itemList){
            itemsName.add(item.getDisplayName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void setSelectNumber(){
        String sAgeFormat = getResources().getString(R.string.select_items_count);
        String sFinalAge = String.format(sAgeFormat, selectedItemsCount);
        selectNumber.setText(sFinalAge);
    }

    @Override
    public void onSongListItemClick(View view, int position) {
        if(isNeedShowSelectBox){
            selectedStatus[position] = !selectedStatus[position];
            if(selectedStatus[position]){
                selectedItemsCount++;
            }else{
                selectedItemsCount--;
            }
            setSelectNumber();
            Log.d(TAG, "onItemClick position " + position + " " + selectedStatus[position]);
            adapter.notifyItemChanged(position);
        }else{
            Intent intent = new Intent();
            intent.setAction(MusicService.ACTION_PLAY);
            MediaInfo item = itemList.get(position);
            MusicRetriever.getInstance().setCurrentPos(item);
            Bundle bundle = new Bundle();
            bundle.putLong("id", item.getId());
            bundle.putLong("duration", item.getDuration());
            bundle.putString("title", item.getTitle());
            bundle.putString("displayName", item.getDisplayName());
            intent.putExtras(bundle);

            startService(intent);
            MusicRetriever.getInstance().setIsPlaylistMode(true);
            MusicRetriever.getInstance().setCurrentPos(itemList.get(position));
            startActivity(new Intent(this, PlayActivity.class));
        }
    }

    @Override
    public void onSongListItemLongClick(View view, int position) {
        if(!isNeedShowSelectBox){
            isNeedShowSelectBox = true;
            selectedStatus = new boolean[itemList.size()];
            selectedStatus[position] = true;
            selectedItemsCount = 1;
            adapter.notifyDataSetChanged();
            showSelectPopupWindow();
            showOperatePopupWindow();
            setSelectNumber();
        }
    }

    void showSelectPopupWindow(){
        if(selectPopupWindow == null){
            View layout =  LayoutInflater.from(this).inflate(R.layout.select_popup_window_song_list, null);
            selectAll = (Button) layout.findViewById(R.id.select_all);
            selectAll.setOnClickListener(this);
            selectOthers = (Button) layout.findViewById(R.id.select_others);
            selectOthers.setOnClickListener(this);
            selectCancel = (Button) layout.findViewById(R.id.select_cancel);
            selectCancel.setOnClickListener(this);
            selectNumber = (Button) layout.findViewById(R.id.select_number);
            Log.d(TAG, "showPopupWindow layout " + layout);
            selectPopupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            selectPopupWindow.setTouchable(true);
            selectPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch ");
                    return false;
                }
            });
        }

        selectPopupWindow.showAtLocation(recyclerView, Gravity.NO_GRAVITY, 0,
                                            HandlerScreen.getStatusBarHeight(this));
    }

    void showOperatePopupWindow(){
        if(operatePopupWindow == null){
            View layout =  LayoutInflater.from(this).inflate(R.layout.operate_popup_window_song_list, null);
            addToPlaylist = (Button) layout.findViewById(R.id.add_to_playlist);
            addToPlaylist.setOnClickListener(this);
            remove = (Button) layout.findViewById(R.id.remove);
            remove.setOnClickListener(this);
            more = (Button) layout.findViewById(R.id.more);
            more.setOnClickListener(this);
            Log.d(TAG, "showPopupWindow layout " + layout);
            operatePopupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            operatePopupWindow.setTouchable(true);
            operatePopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch ");
                    return false;
                }
            });
        }

        operatePopupWindow.showAtLocation(recyclerView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(View v) {

    }
}
