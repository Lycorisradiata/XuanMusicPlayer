package com.jw.cool.xuanmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnPlayListItemListener;
import com.jw.cool.xuanmusicplayer.adapter.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.adapter.PlayListAdapter;
import com.jw.cool.xuanmusicplayer.adapter.SongListPlayListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

/**
 * Created by Administrator on 15-9-16.
 */
public class PlayListActivity2 extends AppCompatActivity
        implements OnSongListItemClickListener, OnPlayListItemListener {
    private static final String TAG = "PlayListActivity2";
    RecyclerView recyclerPlayList, recyclerSongList ;
    RecyclerView.Adapter adapterPlayList, adapterSongList;
    List<PlayList> playLists;
    List<MediaInfo> songList;
    List<String> itemsNamePlayList, itemsNameSongList;
    List<>
    boolean[] selectedStatus;
//    int selectedItemsCount;
    SlidingPaneLayout spl = null;
    CollapsingToolbarLayout collapsingToolbarLayout;
//    CoordinatorLayout rootLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist2);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("PlayList");
        spl = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout_2);
        initRecyclePlayList();
        initRecycleSongList();
        spl.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.d(TAG, "onPanelSlide "+spl.getX()+" "+" "+spl.getParallaxDistance()
//                +" " + spl.getHorizontalFadingEdgeLength() + " " + spl.getWidth()
//                + " " + spl.getPaddingLeft() + " " + spl.getTranslationX() + " " + slideOffset);
            }

            @Override
            public void onPanelOpened(View panel) {

            }

            @Override
            public void onPanelClosed(View panel) {

            }
        });
        spl.setParallaxDistance(200);

    }

    void initRecyclePlayList(){
//        RecyclerViewHeader header = RecyclerViewHeader.fromXml(context, R.layout.header);
        recyclerPlayList = (RecyclerView) findViewById(R.id.play_list_left);
//        recyclerPlayList.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerPlayList.setLayoutManager(layoutManager);
        playLists = MusicRetriever.getInstance().getPlaylist();
        refreshItemsNamePlayList();
        selectedStatus = new boolean[itemsNamePlayList.size()];
        adapterPlayList = new PlayListAdapter(this, itemsNamePlayList, this);
        recyclerPlayList.setAdapter(adapterPlayList);
    }

    void initRecycleSongList(){
        recyclerSongList = (RecyclerView) findViewById(R.id.song_list_right);
//        recyclerSongList.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerSongList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerSongList.setItemAnimator(new FadeInLeftAnimator());
        recyclerSongList.setLayoutManager(layoutManager);
        updateSongList(playLists.get(0).getId());
        refreshItemsNameSongList();
        adapterSongList = new SongListPlayListAdapter(this, itemsNameSongList, this);
//        ((RecyclerSwipeAdapter) adapterPlayList).setMode(Attributes.Mode.Single);
        recyclerSongList.setAdapter(adapterSongList);
    }

    void updateSongList(long id){
        songList = MusicRetriever.getInstance().getPlaylistItems(id);
        refreshItemsNameSongList();
        if(adapterSongList != null){
            adapterSongList.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateSongList(playLists.get(0).getId());
        spl.openPane();
    }

    void refreshItemsNamePlayList(){
        if(itemsNamePlayList == null){
            itemsNamePlayList = new ArrayList<>();
        }else{
            itemsNamePlayList.clear();
        }

        for(PlayList item:playLists){
            itemsNamePlayList.add(item.getName());
        }
    }

    void refreshItemsNameSongList(){
        if(itemsNameSongList == null){
            itemsNameSongList = new ArrayList<>();
        }else{
            itemsNameSongList.clear();
        }

        for(MediaInfo item:songList){
            itemsNameSongList.add(item.getDisplayName());
        }
    }

    public void onEvent(SearchEvent event) {
        Log.d(TAG, "onEvent SearchEvent " + event.searchText + " " + event.isNeedQuery);
//        filterData(event.searchText);
    }

    void toPlaylistActivity(int position){
        Intent intent = new Intent(this, PlaylistActivity.class);
        PlayList item = playLists.get(position);
        Bundle bundle = new Bundle();
        bundle.putLong("id", item.getId());
        bundle.putString("name", item.getName());
        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void onSongListItemClick(View view, int position) {
//        toPlaylistActivity(position);
//        textView.setText(String.valueOf(position));
//        Intent intent = new Intent(this, SlidingPaneLayoutActivity.class);
//        PlayList item = playLists.get(position);
//        Bundle bundle = new Bundle();
//        bundle.putLong("id", item.getId());
//        bundle.putString("name", item.getName());
//        intent.putExtras(bundle);
//
//        startActivity(intent);
        Log.d(TAG, "onSongListItemClick closePane");
        spl.closePane();
    }

    @Override
    public void onSongListItemLongClick(View view, int position) {
        Log.d(TAG, "onSongListItemLongClick closePane");
        if(spl.isOpen()){
            spl.closePane();
        }
    }

    @Override
    public void onSongListDeleteButtonClick(View view, int position) {
        Log.d(TAG, "onSongListDeleteButtonClick ");
    }

    @Override
    public void onPlayListItemClick(View v, int pos) {
        Log.d(TAG, "onPlayListItemClick ");
        updateSongList(playLists.get(pos).getId());
    }

    @Override
    public void onPlayListDeleteButtonClick(View v, int pos) {
        Log.d(TAG, "onPlayListDeleteButtonClick ");
    }
    
}
