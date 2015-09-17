package com.jw.cool.xuanmusicplayer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnPlayListItemListener;
import com.jw.cool.xuanmusicplayer.adapter.OnSongListItemClickListener;
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

public class PlayListSongsFragment extends BaseFragment
        implements OnSongListItemClickListener{
    public final static String PLAY_LIST_ID = "PLAY_LIST_ID";
    private static final String TAG = "PlayListSongsFragment";
    private long playListId;
    RecyclerView recyclerSongList ;
    RecyclerView.Adapter adapterSongList;
    List<MediaInfo> songList;
    List<String> itemsNameSongList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_play_list, container, false);
        initRecycleSongList(view);
        return view;
    }

    void initRecycleSongList(View view){
        recyclerSongList = (RecyclerView) view.findViewById(R.id.song_list_play_list);
        recyclerSongList.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerSongList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerSongList.setItemAnimator(new FadeInLeftAnimator());
        recyclerSongList.setLayoutManager(layoutManager);
//        updateSongList(playListId);
        refreshItemsNameSongList();
        adapterSongList = new SongListPlayListAdapter(getContext(), itemsNameSongList, this);
//        ((RecyclerSwipeAdapter) adapterPlayList).setMode(Attributes.Mode.Single);
        recyclerSongList.setAdapter(adapterSongList);
    }

    void updateSongList(long id){
        songList = MusicRetriever.getInstance().getPlaylistItems(id);
        refreshItemsNameSongList();
//        adapterSongList.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateSongList(playListId);
    }

    void refreshItemsNameSongList(){
        if(itemsNameSongList == null){
            itemsNameSongList = new ArrayList<>();
        }else{
            itemsNameSongList.clear();
        }

        if(songList != null){
            for(MediaInfo item:songList){
                itemsNameSongList.add(item.getDisplayName());
            }
        }
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
        Log.d(TAG, "onSongListItemClick ");
    }

    @Override
    public void onSongListItemLongClick(View view, int position) {

    }

    @Override
    public void onSongListDeleteButtonClick(View view, int position) {

    }

    public long getPlayListId() {
        return playListId;
    }

    public void setPlayListId(long playListId) {
        this.playListId = playListId;
    }

    public void onEvent(SearchEvent event){

    }
}
