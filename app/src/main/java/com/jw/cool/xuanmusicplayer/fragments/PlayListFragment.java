package com.jw.cool.xuanmusicplayer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.jw.cool.xuanmusicplayer.SlidingPaneLayoutActivity;
import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnPlayListItemListener;
import com.jw.cool.xuanmusicplayer.adapter.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.adapter.PlayListAdapter;
import com.jw.cool.xuanmusicplayer.adapter.SongListAdapter;
import com.jw.cool.xuanmusicplayer.PlaylistActivity;
import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.adapter.SongListPlayListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class PlayListFragment extends BaseFragment
        implements OnSongListItemClickListener, OnPlayListItemListener {
    private static final String TAG = "PlayListFragment";
    RecyclerView recyclerPlayList, recyclerSongList ;
    RecyclerView.Adapter adapterPlayList, adapterSongList;
    List<PlayList> playLists;
    List<MediaInfo> songList;
    List<String> itemsNamePlayList, itemsNameSongList;
    boolean[] selectedStatus;
    int selectedItemsCount;
    SlidingPaneLayout spl = null;

    public static PlayListFragment newInstance(Context context,Bundle bundle) {
        PlayListFragment newFragment = new PlayListFragment();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_play_list, container, false);
        spl = (SlidingPaneLayout) view.findViewById(R.id.slidingpanellayout);
        initRecyclePlayList(view);
        initRecycleSongList(view);
        return view;
    }

    void initRecyclePlayList(View view){
        recyclerPlayList = (RecyclerView) view.findViewById(R.id.play_list);
        recyclerPlayList.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPlayList.setLayoutManager(layoutManager);
        playLists = MusicRetriever.getInstance().getPlaylist();
        refreshItemsNamePlayList();
        selectedStatus = new boolean[itemsNamePlayList.size()];
        adapterPlayList = new PlayListAdapter(getContext(), itemsNamePlayList, this);
        recyclerPlayList.setAdapter(adapterPlayList);
    }

    void initRecycleSongList(View view){
        recyclerSongList = (RecyclerView) view.findViewById(R.id.song_list_play_list);
        recyclerSongList.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerSongList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerSongList.setItemAnimator(new FadeInLeftAnimator());
        recyclerSongList.setLayoutManager(layoutManager);
        updateSongList(playLists.get(0).getId());
        refreshItemsNameSongList();
        adapterSongList = new SongListPlayListAdapter(getContext(), itemsNameSongList, this);
//        ((RecyclerSwipeAdapter) adapterPlayList).setMode(Attributes.Mode.Single);
        recyclerSongList.setAdapter(adapterSongList);
    }

    void updateSongList(long id){
        songList = MusicRetriever.getInstance().getPlaylistItems(id);
        refreshItemsNameSongList();
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
        Intent intent = new Intent(getContext(), PlaylistActivity.class);
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
//        Intent intent = new Intent(getContext(), SlidingPaneLayoutActivity.class);
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

    }

    @Override
    public void onSongListDeleteButtonClick(View view, int position) {

    }

    @Override
    public void onPlayListItemClick(View v, int pos) {

    }

    @Override
    public void onPlayListDeleteButtonClick(View v, int pos) {

    }
}
