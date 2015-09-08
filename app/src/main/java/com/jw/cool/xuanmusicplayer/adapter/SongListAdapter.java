package com.jw.cool.xuanmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.R;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {
    private Context mContext;
    private List<String> list;
    private boolean isNeedShowSelectBox;
    private boolean[] selectedStatus;
    OnSongListItemClickListener listener;
    public SongListAdapter(Context mContext, List<String> list,
                           OnSongListItemClickListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener = listener;
    }

    public void setIsNeedShowSelectBox(boolean isNeedShowSelectBox, boolean[] selectedStatus) {
        this.isNeedShowSelectBox = isNeedShowSelectBox;
        this.selectedStatus = selectedStatus;
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
            textView.setText(list.get(position));
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
            return list.size();
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
                listener.onSongListItemClick(view, position);
            }

            @Override
            public boolean onLongClick(View view) {
                listener.onSongListItemLongClick(view, position);
                return true;
            }
        }
    }