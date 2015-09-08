package com.jw.cool.xuanmusicplayer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jw.cool.xuanmusicplayer.adapters.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.adapters.SongListAdapter;
import com.jw.cool.xuanmusicplayer.PlaylistActivity;
import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayListFragment extends BaseFragment implements OnSongListItemClickListener{
    private static final String TAG = "PlayListFragment";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<PlayList> playLists;
    List<String> itemsName;
    boolean[] selectedStatus;
    int selectedItemsCount;

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
        recyclerView = (RecyclerView) view.findViewById(R.id.song_list);
        recyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        playLists = MusicRetriever.getInstance().getPlaylist();
        refreshItemsName();
        selectedStatus = new boolean[itemsName.size()];
        adapter = new SongListAdapter(getContext(), itemsName, this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    void refreshItemsName(){
        if(itemsName == null){
            itemsName = new ArrayList<>();
        }else{
            itemsName.clear();
        }

        for(PlayList item:playLists){
            itemsName.add(item.getName());
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
        toPlaylistActivity(position);
    }

    @Override
    public void onSongListItemLongClick(View view, int position) {

    }


}
