package com.jw.cool.xuanmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnPlayListItemListener;
import com.jw.cool.xuanmusicplayer.adapter.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.adapter.PlayListAdapter;
import com.jw.cool.xuanmusicplayer.adapter.SongListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.utils.HandlerScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by jw on 2015/9/7.
 */
public class PlaylistActivity extends AppCompatActivity
        implements OnPlayListItemListener, View.OnClickListener{
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

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CoordinatorLayout rootLayout;
    FloatingActionButton fabBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        HandlerScreen.setStatusAndNavigationBarTranslucent(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(rootLayout, "Hello. I am Snackbar!", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("Design Library");
        recyclerView = (RecyclerView) findViewById(R.id.playlist_recycleView);
        Bundle bundle = getIntent().getExtras();
        recyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        recyclerView.setLayoutManager(layoutManager);
        playlistId = bundle.getLong("id", -1);
        playlistName = bundle.getString("name", "");
        itemList = MusicRetriever.getInstance().getPlaylistItems(playlistId);
        refreshItemsName();
        adapter = new PlayListAdapter(this, itemsName, this);
//        ((RecyclerSwipeAdapter) adapter).setMode(Attributes.Mode.Single);
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

//    @Override
//    public void onItemClick(View v, int pos) {
//        Intent intent = new Intent();
//        intent.setAction(MusicService.ACTION_PLAY);
//        MediaInfo item = itemList.get(pos);
//        MusicRetriever.getInstance().setCurrentPos(item);
//        Bundle bundle = new Bundle();
//        bundle.putLong("id", item.getId());
//        bundle.putLong("duration", item.getDuration());
//        bundle.putString("title", item.getTitle());
//        bundle.putString("displayName", item.getDisplayName());
//        intent.putExtras(bundle);
//
//        startService(intent);
//        MusicRetriever.getInstance().setIsPlaylistMode(true);
//        MusicRetriever.getInstance().setCurrentPos(itemList.get(pos));
//        startActivity(new Intent(this, PlayActivity.class));
//    }

//    @Override
//    public void onDeleteButtonClick(View v, int pos) {
//        Toast.makeText(this, "delete item " + pos, Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onPlayListItemClick(View v, int pos) {

    }

    @Override
    public void onPlayListDeleteButtonClick(View v, int pos) {

    }

    @Override
    public void onPlayListCreateButtonClick(View v, int pos) {

    }
}
