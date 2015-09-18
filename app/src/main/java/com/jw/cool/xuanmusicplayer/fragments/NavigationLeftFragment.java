package com.jw.cool.xuanmusicplayer.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnPlayListItemListener;
import com.jw.cool.xuanmusicplayer.adapter.PlayListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.events.PlaylistEvent;
import com.jw.cool.xuanmusicplayer.events.RetrieverPreparedEvent;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;
import com.jw.cool.xuanmusicplayer.events.SwitchFragmentEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class NavigationLeftFragment extends BaseFragment implements OnPlayListItemListener{
    private static final String TAG = "NavigationLeftFragment";
    private TextView mAllSongsTextView;
    private TextView mPlayListTextView;
    private RecyclerView mPlayListItemsRecyclerView;
    List<PlayList> playList;
    List<String> playListNames;
    PlayListAdapter playListAdapter;
    private boolean isRetrieverPrepared;
    private TextView mSettingsTextView;
    private ListView mPlayListListViewListView;
    //Android NestedScrolling 嵌套滑动详解
//    http://www.race604.com/android-nested-scrolling/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_left, container, false);
        mAllSongsTextView = (TextView) view.findViewById(R.id.nav_all_songs);
        mPlayListTextView = (TextView) view.findViewById(R.id.nav_play_list);
        mPlayListItemsRecyclerView = (RecyclerView) view.findViewById(R.id.nav_play_list_items);
        mSettingsTextView = (TextView) view.findViewById(R.id.nav_settings);
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
        mSettingsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SwitchFragmentEvent(SettingsFragment.class.getName(), null));
            }
        });
        initRecyclePlayList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart NavigationLeftFragment");
        refreshPlayList();
        refreshPlayListsItems();
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
//        mPlayListListViewListView.setAdapter(new ArrayAdapter<String>(getContext(),
//                android.R.layout.simple_list_item_1,
//                new String[]{"lfasjld", "sdfklj2", "fklskjfljk3"}));
        mPlayListItemsRecyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mPlayListItemsRecyclerView.setLayoutManager(layoutManager);
        mPlayListItemsRecyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        mPlayListItemsRecyclerView.setItemAnimator(new FadeInLeftAnimator());
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
        Log.d(TAG, "onPlayListItemClick " + pos);
        Bundle bundle = new Bundle();
        bundle.putLong(PlayListSongsFragment.PLAY_LIST_ID, playList.get(pos).getId());
        EventBus.getDefault().post(new SwitchFragmentEvent(PlayListSongsFragment.class.getName(), bundle));
    }

    @Override
    public void onPlayListDeleteButtonClick(View v, int pos) {
        long playlistId = playList.get(pos).getId();
        playListAdapter.notifyItemRemoved(pos);
        playList.remove(pos);
        playListNames.remove(pos);
        playListAdapter.notifyItemRangeChanged(pos, playListAdapter.getItemCount());

        Log.d(TAG, "onPlayListDeleteButtonClick pos " + pos);
        MusicRetriever.getInstance().deletePlaylist(playlistId);
    }

    @Override
    public void onPlayListCreateButtonClick(View v, int pos) {

    }

    public void onEvent(SearchEvent event){

    }

    public void onEvent(PlaylistEvent event){
        Log.d(TAG, "onEvent PlaylistEvent " + event.isAddPlaylist());
        if(event.isAddPlaylist()){
            refreshPlayList();
            refreshPlayListsItems();
            playListAdapter.notifyDataSetChanged();
        }
    }

    //    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop NavigationLeftFragment");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d(TAG, "onResume NavigationLeftFragment");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy NavigationLeftFragment");
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        Log.d(TAG, "onDetach NavigationLeftFragment");
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.d(TAG, "onDestroyView NavigationLeftFragment");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d(TAG, "onPause NavigationLeftFragment");
//    }
}
