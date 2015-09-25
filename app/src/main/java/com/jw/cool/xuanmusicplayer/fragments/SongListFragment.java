package com.jw.cool.xuanmusicplayer.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnScrollChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.jw.cool.xuanmusicplayer.PlayActivity;
import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.adapter.DividerItemDecoration;
import com.jw.cool.xuanmusicplayer.adapter.OnSongListItemClickListener;
import com.jw.cool.xuanmusicplayer.adapter.SongListAdapter;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.coreservice.PlaylistItem;
import com.jw.cool.xuanmusicplayer.events.PlaylistEvent;
import com.jw.cool.xuanmusicplayer.events.PopupWindowEvent;
import com.jw.cool.xuanmusicplayer.events.RetrieverPreparedEvent;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;
import com.jw.cool.xuanmusicplayer.events.SlidingPaneLayoutEvent;
import com.jw.cool.xuanmusicplayer.popupWindows.PopWin;
import com.jw.cool.xuanmusicplayer.utils.HandlerFile;
import com.jw.cool.xuanmusicplayer.utils.HandlerScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

import static android.content.DialogInterface.OnClickListener;

public class SongListFragment extends BaseFragment
        implements View.OnClickListener, OnSongListItemClickListener{
    final String TAG = "SongListFragment";
    List<MediaInfo> itemList = new ArrayList<>();
    SongListAdapter adapter;
    Button selectNumber;
    boolean isNeedShowSelectBox;
    PopupWindow selectPopupWindow;
    PopupWindow operatePopupWindow;
    boolean[] selectedStatus;
    int selectedItemsCount;
    List<PlayList> playLists;
    List<String> itemsName;
    boolean isRetrieverPrepared;

    void setSelectNumber(){
        String sAgeFormat = getResources().getString(R.string.select_items_count);
        String sFinalAge = String.format(sAgeFormat, selectedItemsCount);
        selectNumber.setText(sFinalAge);
    }

    void showSelectPopupWindow(){
        if(selectPopupWindow == null){
            selectPopupWindow = PopWin.getSelectWindow(getContext(), this);
            selectNumber = (Button) selectPopupWindow.getContentView().findViewById(R.id.select_number);
        }
        selectPopupWindow.showAtLocation(recyclerView, Gravity.NO_GRAVITY, 0,
                HandlerScreen.getStatusBarHeight(getContext()));
    }

    void showOperatePopupWindow(){
        if(operatePopupWindow == null){
            operatePopupWindow = PopWin.getOperateWindow(getContext(), this);
        }
        operatePopupWindow.showAtLocation(recyclerView, Gravity.BOTTOM, 0, 0);
    }

    void selectAll(){
        Log.d(TAG, "selectAll ");
        int length = selectedStatus.length;
        for(int i = 0; i < length; i++){
            selectedStatus[i] = true;
        }
        adapter.notifyDataSetChanged();
        selectedItemsCount = length;
        setSelectNumber();
    }
    
    void selectOthers(){
        Log.d(TAG, "selectOthers ");
        int length = selectedStatus.length;
        for(int i = 0; i < length; i++){
            selectedStatus[i] = !selectedStatus[i];
        }
        adapter.notifyDataSetChanged();
        selectedItemsCount = length - selectedItemsCount;
        setSelectNumber();
    }

    /**是否需要通知刷新界面（即当前页面是否有数据更新），需要刷新则notify赋值true
     * */
    void dismissPopupWindows(Boolean notify){
        Log.d(TAG, "dismissPopupWindows ");
        isNeedShowSelectBox = false;
        adapter.setIsNeedShowSelectBox(isNeedShowSelectBox, null);
//        selectedStatus = null;
        if(null != operatePopupWindow && operatePopupWindow.isShowing()) {
            operatePopupWindow.dismiss();
        }

        if(null != selectPopupWindow && selectPopupWindow.isShowing()) {
            selectPopupWindow.dismiss();
        }

        if(notify){
            adapter.notifyDataSetChanged();
        }

    }

    public static SongListFragment newInstance(Context context,Bundle bundle) {
        SongListFragment newFragment = new SongListFragment();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    private RecyclerView recyclerView;
    FloatingActionButton toStartButton;
    FloatingActionButton toEndButton;
    LinearLayoutManager layoutManager;
    int diffY = 0;
    long showTime;
    //停止滑动后toStartButton， toEndButton持续显示的时间，超过将会自动隐藏
    final long displayDurationMillis = 2000;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView ");
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.song_list_recycle_view);
        recyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recyclerView.setItemAnimator(new FadeInLeftAnimator());
        itemList = MusicRetriever.getInstance().getItems();
        refreshItemsName();
        Log.d(TAG, "onCreateView itemsName " + itemsName + " " + itemsName.size());
        adapter = new SongListAdapter(getContext(), itemsName, this);
        recyclerView.setAdapter(adapter);
        toStartButton = (FloatingActionButton) view.findViewById(R.id.to_star);
        toStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //滑动recyclerView至开始元素位置
                Log.d(TAG, "onClick toStartButton ");
                toStartButton.hide();
                layoutManager.scrollToPosition(0);
            }
        });
        toStartButton.hide();

        toEndButton = (FloatingActionButton) view.findViewById(R.id.to_end);
        toEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //滑动recyclerView至结束元素位置
                Log.d(TAG, "onClick toEndButton ");
                toEndButton.hide();
                Log.d(TAG, "onClick recyclerView.Count()" + recyclerView.getChildCount());
                Log.d(TAG, "onClick layoutManager.Count()" + layoutManager.getChildCount());
                layoutManager.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
        toEndButton.hide();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0){
                    //隐藏PlayPopupWindow
                    EventBus.getDefault().post(new PopupWindowEvent(false, 0));

                    showFloatingActionButton(false, true);
                    showTime = System.currentTimeMillis();
                }else if(dy < 0){
                    showFloatingActionButton(true, false);
                    showTime = System.currentTimeMillis();
                }else{
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(toEndButton.isShown()){
                        toEndButton.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(System.currentTimeMillis() - showTime >= displayDurationMillis){
                                    toEndButton.hide();
                                    EventBus.getDefault().post(new PopupWindowEvent(true, 0));
                                }
                            }
                        }, displayDurationMillis);
                    }else if(toStartButton.isShown()){
                        toStartButton.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(System.currentTimeMillis() - showTime >= displayDurationMillis){
                                    toStartButton.hide();
                                }
                            }
                        }, displayDurationMillis);
                    }
                }
            }
        });


        return view;
    }

    void showFloatingActionButton(boolean toStart, boolean toEnd){
        if(toStart){
            toStartButton.show();
        }else{
            toStartButton.hide();
        }

        if(toEnd){
            toEndButton.show();
        }else{
            toEndButton.hide();
        }
    }

    public void onEvent(SearchEvent event) {
        Log.d(TAG, "onEvent SearchEvent " + event.searchText + " " + event.isNeedQuery);
        filterData(event.searchText);
    }

    @Override
    public boolean handleBackPressed() {
        super.handleBackPressed();
        Log.d(TAG, "handleBackPressed isNeedShowSelectBox " + isNeedShowSelectBox);
        if(isNeedShowSelectBox){
            dismissPopupWindows(true);
            return true;
        }

        return false;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr 需匹配的字符串
     */
    private void filterData(String filterStr){
//        Log.d(TAG, "filterData filterStr " + filterStr);
        itemList = MusicRetriever.getInstance().getItems();
        if(!TextUtils.isEmpty(filterStr)){
            List<MediaInfo> list = new ArrayList<>();
            for(MediaInfo item : itemList){
                String name = item.getDisplayName();
//                Log.d(TAG, "filterData name " + name);
                if(name.contains(filterStr)){
                    list.add(item);
                }
            }
            itemList = list;
        }
        refreshItemsName();
        Log.d(TAG, "filterData itemList " + itemList.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
        Log.d(TAG, "onStart test");
        itemList = MusicRetriever.getInstance().getItems();
        refreshItemsName();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "run post PopupWindowEvent ");
                EventBus.getDefault().post(new PopupWindowEvent(true, 0));
            }
        }, 1000);

//        showPlayPopupWindow();
    }

    void refreshItemsName(){
//        Log.d(TAG, "refreshItemsName isRetrieverPrepared" + isRetrieverPrepared);
        if(itemsName == null){
            itemsName = new ArrayList<>();
        }else{
//            Log.d(TAG, "refreshItemsName " + itemList.size());
            itemsName.clear();
        }

        if(MusicRetriever.getInstance().isRetrieverPrepared()){
            for(MediaInfo item:itemList){
                itemsName.add(item.getDisplayName());
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//        recyclerView.setAdapter(new SongListAdapter(getActivity()));

    }

    public void onEvent(RetrieverPreparedEvent event) {
        Log.d(TAG, "onEvent " + itemList.size());
        isRetrieverPrepared = true;
        refreshItemsName();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick v.id " + v.getId());
        switch (v.getId()){
            case R.id.select_all:
                selectAll();
                break;
            case R.id.select_others:
                selectOthers();
                break;
            case R.id.select_cancel:
                dismissPopupWindows(true);
                break;
            case R.id.add_to_playlist:
                showPlaylistDialog();
                break;
            case R.id.remove:
                remove();
                break;
            case R.id.more:
                more();
                break;
        }
    }

    void addToPlaylist(int pos){
        Log.d(TAG, "addToPlaylist ");
        if(pos == 0){
            showCreatePlaylistDialog();
        }else{
            MusicRetriever.getInstance().addToPlaylist(
                    getPlaylistItemsNeedAdd(), playLists.get(pos -1).getId());
        }
    }

    List<PlaylistItem>  getPlaylistItemsNeedAdd(){
        List<PlaylistItem> list = new ArrayList<>();
        for(int i = 0; i < selectedStatus.length; i++){
            if(selectedStatus[i]){
                MediaInfo info = itemList.get(i);
                PlaylistItem item = new PlaylistItem(-1, info.getId(), info.getId(), -1, "");
                list.add(item);
            }
        }
        return list;
    }


    void showCreatePlaylistDialog(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
           View layout = inflater.inflate(R.layout.dialog_create_playlist,
                   null);
        final EditText editText = (EditText) layout.findViewById(R.id.create_playlist);
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        long playlistId = MusicRetriever.getInstance().createPlaylist(editText.getText().toString());
                        if(playlistId >= 0){
                            MusicRetriever.getInstance().addToPlaylist(
                                    getPlaylistItemsNeedAdd(), playlistId);
                        }
                        dismissPopupWindows(true);
                        Log.d(TAG, "onClick create playlist");
                        EventBus.getDefault().post(new PlaylistEvent(true));
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
           new AlertDialog.Builder(getActivity()).setTitle(R.string.create_playlist).setView(layout)
             .setPositiveButton(R.string.ok, listener)
            .setNegativeButton(R.string.cancel, listener).show();
    }

    void showPlaylistDialog(){
        playLists = MusicRetriever.getInstance().getPlaylist();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.create_playlist);
        //    指定下拉列表的显示数据
        String[] arrayPlaylist = new String[playLists.size() + 1];
        arrayPlaylist[0] = getResources().getString(R.string.create_playlist);
        for(int i = 1; i < arrayPlaylist.length; i++){
            arrayPlaylist[i] = playLists.get(i - 1).getName();
        }

        //    设置一个下拉的列表选择项
        builder.setItems(arrayPlaylist, new OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                addToPlaylist(which);
            }
        });
        builder.show();
    }



    void remove(){
        dismissPopupWindows(false);
        Log.d(TAG, "remove itemList1 " + itemList.size());
        List<Uri> idList = new ArrayList<>();
        List<String> pathList = new ArrayList<>();
        int startPos = -1;
        for (int i = 0; i < selectedStatus.length; i++) {
            if(selectedStatus[i]){
                if(startPos == -1){
                    startPos = i;
                }
                idList.add(itemList.get(i).getURI());
                pathList.add(itemList.get(i).getPath());
                itemList.get(i).setDuration(-1);
                adapter.notifyItemRemoved(i);
            }
        }

        //刷新itemList
        Iterator<MediaInfo> iterator = itemList.iterator();
        MediaInfo temp = null;
        while (iterator.hasNext()) {
            temp = iterator.next();
            if (temp.getDuration() == -1) {
                iterator.remove();
            }
        }

        if(startPos != -1){
            refreshItemsName();
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        }
        Log.d(TAG, "remove itemList " + itemList.size());

        //删除数据库记录
        MusicRetriever.getInstance().removeSongList(idList);
        //删除文件
        boolean isDeleteSuccess = HandlerFile.delete(pathList);
        Log.d(TAG, "remove isDeleteSuccess " + isDeleteSuccess);
    }

    void more(){
        Log.d(TAG, "more ");
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
            MusicRetriever.getInstance().setIsPlaylistMode(false);
            MusicRetriever.getInstance().setCurrentPos(item);
            Bundle bundle = new Bundle();
            bundle.putParcelable("MediaInfo", item);
            intent.putExtras(bundle);

            getActivity().startService(intent);
            getActivity().startActivity(new Intent(getActivity(), PlayActivity.class));
        }
    }

    @Override
    public void onSongListItemLongClick(View view, int position) {
        Log.d(TAG, "onSongListItemLongClick " + isNeedShowSelectBox);
        if(!isNeedShowSelectBox){
            EventBus.getDefault().post(new SlidingPaneLayoutEvent(true));
            isNeedShowSelectBox = true;
            selectedStatus = new boolean[itemList.size()];
            selectedStatus[position] = true;
            selectedItemsCount = 1;
            adapter.setIsNeedShowSelectBox(true, selectedStatus);
            adapter.notifyDataSetChanged();
            showSelectPopupWindow();
            showOperatePopupWindow();
            setSelectNumber();
        }
    }

    @Override
    public void onSongListDeleteButtonClick(View view, int position) {

    }
}


