package com.jw.cool.xuanmusicplayer.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.PlayActivity;
import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.coreservice.MediaInfo;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplayer.coreservice.MusicService;
import com.jw.cool.xuanmusicplayer.coreservice.PlayList;
import com.jw.cool.xuanmusicplayer.coreservice.PlaylistItem;
import com.jw.cool.xuanmusicplayer.events.SearchEvent;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static android.content.DialogInterface.*;

public class SongListFragment extends BaseFragment
        implements View.OnClickListener{
    final String TAG = "SongListFragment";
    List<MediaInfo> itemList = new ArrayList<>();
	Adapter adapter;
//    MusicRetriever mRetriever;
    Button selectAll,  selectOthers, selectCancel, selectNumber;
    Button addToPlaylist, remove, more;
    boolean isNeedShowSelectBox;
    PopupWindow selectPopupWindow;
    PopupWindow operatePopupWindow;
    boolean[] selectedStatus;
    int selectedItemsCount;
    List<PlayList> playLists;

    void onItemClick(View view,int position){
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

            getActivity().startService(intent);
            getActivity().startActivity(new Intent(getActivity(), PlayActivity.class));
        }
    }

    void onItemLongClick(View view,int position){
//        Toast.makeText(getActivity(), "longClick " + position, Toast.LENGTH_LONG).show();
        if(!isNeedShowSelectBox){
            isNeedShowSelectBox = true;
            selectedStatus = new boolean[itemList.size()];
            selectedItemsCount = 0;
            adapter.notifyDataSetChanged();
            showSelectPopupWindow();
            showOperatePopupWindow();
            setSelectNumber();
        }
    }

    void setSelectNumber(){
        String sAgeFormat = getResources().getString(R.string.select_items_count);
        String sFinalAge = String.format(sAgeFormat, selectedItemsCount);
        selectNumber.setText(sFinalAge);
    }

    void showSelectPopupWindow(){
        if(selectPopupWindow == null){
            View layout =  LayoutInflater.from(getActivity()).inflate(R.layout.select_popup_window_song_list, null);
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
//            selectPopupWindow.setBackgroundDrawable(
//                    new ColorDrawable(getResources().getDrawable(R.color.select_popup_window_background));
        }

        selectPopupWindow.showAtLocation(recyclerView, Gravity.NO_GRAVITY, 0, getStatusBarHeight());
    }

    void showOperatePopupWindow(){
        if(operatePopupWindow == null){
            View layout =  LayoutInflater.from(getActivity()).inflate(R.layout.operate_popup_window_song_list, null);
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
//            operatePopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.song_list_background, null)));
        }

        operatePopupWindow.showAtLocation(recyclerView, Gravity.BOTTOM, 0, 0);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        Log.d(TAG, "getStatusBarHeight result " + result);
        return result;
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

    /**
     * */
    void dismissPopupWindows(){
        Log.d(TAG, "dismissPopupWindows ");
        isNeedShowSelectBox = false;
        selectedStatus = null;
        if(null != operatePopupWindow && operatePopupWindow.isShowing()) {
            operatePopupWindow.dismiss();
        }

        if(null != selectPopupWindow && selectPopupWindow.isShowing()) {
            selectPopupWindow.dismiss();
        }
        adapter.notifyDataSetChanged();
    }

    public static SongListFragment newInstance(Context context,Bundle bundle) {
        SongListFragment newFragment = new SongListFragment();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_song_list, container, false);
        return recyclerView;
    }



    public void onEvent(SearchEvent event) {
        Log.d(TAG, "onEvent SearchEvent " + event.searchText + " " + event.isNeedQuery);
        filterData(event.searchText);
    }

    @Override
    public boolean handleBackPressed() {
        super.handleBackPressed();
        if(isNeedShowSelectBox){
            dismissPopupWindows();
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
            List<MediaInfo> templist = new ArrayList<MediaInfo>();
            for(MediaInfo item : itemList){
                String name = item.getDisplayName();
//                Log.d(TAG, "filterData name " + name);
                if(name.indexOf(filterStr) != -1){
                    templist.add(item);
                }
            }
            itemList = templist;
        }
        Log.d(TAG, "filterData itemList " + itemList.size());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
        Log.d(TAG, "onStart test");
        recyclerView.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SongListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
//        mRetriever = new MusicRetriever(getActivity().getContentResolver());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SongListAdapter(getActivity()));
        itemList = MusicRetriever.getInstance().getItems();
        //        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v == selectAll){
            selectAll();
        }else if(v == selectOthers){
            selectOthers();
        }else if(v == selectCancel){
            dismissPopupWindows();
        }else if(v == addToPlaylist){
            showPlaylistDialog();
        }else if(v == remove){
            remove();
        } else if(v == more){
            more();
        }
    }

    void addToPlaylist(int pos){
        Log.d(TAG, "addToPlaylist ");
        if(pos == 0){
            showCreatePlaylistDialog();
        }else{
            MusicRetriever.getInstance().addToPlaylist(
                    getPlaylistItemsNeedAdd(), playLists.get(pos + 1).getId());
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
        Log.d(TAG, "remove ");  
    }

    void more(){
        Log.d(TAG, "more ");
    }

    public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
        private Context mContext;
        public SongListAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SongListAdapter.ViewHolder holder, int position) {
            final View view = holder.mView;
            TextView textView = (TextView)view.findViewById(R.id.text_view_song_list);
            textView.setText(itemList.get(position).getDisplayName());
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            checkBox.setClickable(false);
            if(isNeedShowSelectBox){
                checkBox.setVisibility(View.VISIBLE);
//                Log.d(TAG, "onBindViewHolder isChecked() " + checkBox.isChecked() + " " + position);
                checkBox.setChecked(selectedStatus[position]);
            }else{
                checkBox.setVisibility(View.INVISIBLE);
            }
            holder.position = position;
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener,View.OnLongClickListener {
            public final View mView;
            public int position;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mView.setOnClickListener(this);
                mView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                onItemClick(view, position);
            }

            @Override
            public boolean onLongClick(View view) {
                onItemLongClick(view, position);
                return true;
            }
        }
    }



}


