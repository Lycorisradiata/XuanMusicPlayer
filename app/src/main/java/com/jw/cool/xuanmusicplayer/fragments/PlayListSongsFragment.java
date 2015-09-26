package com.jw.cool.xuanmusicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jw.cool.xuanmusicplayer.PlayActivity;
import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.adapter.SongListPlayListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.events.PopupWindowEvent;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class PlayListSongsFragment extends BaseFragment
        implements OnSongListItemClickListener{
    static final String TAG = "PlayListSongsFragment";
    public static final String PLAY_LIST_ID = "PLAY_LIST_ID";

    private static long playListId;
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

    void updateSongList(){
        songList = MusicRetriever.getInstance().getPlaylistItems(playListId);
        refreshItemsNameSongList();
//        adapterSongList.notifyDataSetChanged();
    }

    public void refreshSongList(){
        updateSongList();
        adapterSongList.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart playListId " + playListId);
        updateSongList();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run post PopupWindowEvent ");
                EventBus.getDefault().post(new PopupWindowEvent(true, 0));
            }
        }, 500);
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
        Intent intent = new Intent();
        intent.setAction(MusicService.ACTION_PLAY);
        MediaInfo item = songList.get(position);
        MusicRetriever.getInstance().setIsPlaylistMode(true);
        MusicRetriever.getInstance().setCurrentPos(item);
        Bundle bundle = new Bundle();
        bundle.putParcelable("MediaInfo", item);
        intent.putExtras(bundle);

        getActivity().startService(intent);
        getActivity().startActivity(new Intent(getActivity(), PlayActivity.class));
        Log.d(TAG, "onSongListItemClick ");
    }

    @Override
    public void onSongListItemLongClick(View view, int position) {

    }

    @Override
    public void onSongListDeleteButtonClick(View view, int position) {
        long audioId = songList.get(position).getId();
        adapterSongList.notifyItemRemoved(position);
        songList.remove(position);
        itemsNameSongList.remove(position);
        adapterSongList.notifyItemRangeChanged(position, adapterSongList.getItemCount());

        Log.d(TAG, "onSongListDeleteButtonClick position " + position);
        MusicRetriever.getInstance().deletePlayListSong(playListId, audioId);
    }

    public static long getPlayListId() {
        return playListId;
    }

    public static void setPlayListId(long playListId1) {
        playListId = playListId1;
    }

    public void onEvent(SearchEvent event){

    }
}
