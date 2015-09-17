package com.jw.cool.xuanmusicplayer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.adapter.OnPlayListItemListener;
import com.jw.cool.xuanmusicplayer.adapter.PlayListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.events.RetrieverPreparedEvent;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;
import com.jw.cool.xuanmusicplayer.events.SwitchFragmentEvent;
import com.jw.cool.xuanmusicplayer.fragments.BaseFragment;
import com.jw.cool.xuanmusicplayer.fragments.PlayListSongsFragment;
import com.jw.cool.xuanmusicplayer.fragments.SongListFragment;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class NavigationLeftFragment extends BaseFragment implements OnPlayListItemListener{
    private static final String TAG = "NavigationLeftFragment";
    private TextView mAllSongsTextView;
    private TextView mPlayListTextView;
    private RecyclerView mPlayListItemsRecyclerView;
    List<PlayList> playList;
    List<String> playListNames;
    PlayListAdapter playListAdapter;
    private boolean isRetrieverPrepared;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_left, container, false);
        mAllSongsTextView = (TextView) view.findViewById(R.id.nav_all_songs);
        mAllSongsTextView.setText(R.string.all_songs);
        mPlayListTextView = (TextView) view.findViewById(R.id.nav_play_list);
        mPlayListTextView.setText(R.string.play_list);
        mPlayListItemsRecyclerView = (RecyclerView) view.findViewById(R.id.nav_play_list_items);
        mAllSongsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SwitchFragmentEvent(SongListFragment.class.getName(), null));
            }
        });

        mPlayListTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayListItems();
            }
        });
        initRecyclePlayList();
        return view;
    }

    public void onEvent(RetrieverPreparedEvent event) {
        Log.d(TAG, "onEvent " + event);
        isRetrieverPrepared = true;
        refreshPlayList();
        refreshPlayListsItems();
        playListAdapter.notifyDataSetChanged();
    }

    void togglePlayListItems(){
        if(mPlayListItemsRecyclerView.getVisibility() == View.VISIBLE){
            mPlayListItemsRecyclerView.setVisibility(View.INVISIBLE);
        }else{
            mPlayListItemsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    void initRecyclePlayList(){
        mPlayListItemsRecyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mPlayListItemsRecyclerView.setLayoutManager(layoutManager);
        refreshPlayList();
        refreshPlayListsItems();
        Log.d(TAG, "initRecyclePlayList playListNames " + playListNames);
        playListAdapter = new PlayListAdapter(getContext(), playListNames, this);

        mPlayListItemsRecyclerView.setAdapter(playListAdapter);
    }

    void refreshPlayList(){
        playList = MusicRetriever.getInstance().getPlaylist();
    }

    void refreshPlayListsItems(){
        if(playListNames == null){
            playListNames = new ArrayList<>();
        }else{
            playListNames.clear();
        }

        for(PlayList item:playList){
            playListNames.add(item.getName());
        }
    }

    @Override
    public void onPlayListItemClick(View v, int pos) {
        Bundle bundle = new Bundle();
        bundle.putLong(PlayListSongsFragment.PLAY_LIST_ID, playList.get(pos).getId());
        EventBus.getDefault().post(new SwitchFragmentEvent(PlayListSongsFragment.class.getName(), bundle));
    }

    @Override
    public void onPlayListDeleteButtonClick(View v, int pos) {

    }

    @Override
    public void onPlayListCreateButtonClick(View v, int pos) {

    }

    public void onEvent(SearchEvent event){

    }
}
