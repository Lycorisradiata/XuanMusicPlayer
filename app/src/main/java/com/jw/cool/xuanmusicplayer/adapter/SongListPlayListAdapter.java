package com.jw.cool.xuanmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.jw.cool.xuanmusicplayer.R;

import java.util.List;

/**
 * Created by Administrator on 15-9-11.
 */
public class SongListPlayListAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context mContext;
    private List<String> list;
    OnSongListItemClickListener listener;
    public SongListPlayListAdapter(Context mContext, List<String> list,
                           OnSongListItemClickListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final View view = holder.mView;
        TextView textView = (TextView)view.findViewById(R.id.swipe_item_top);
        textView.setText(list.get(position));
        Button button = (Button) view.findViewById(R.id.swipe_item_delete);
        button.setText("delete");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSongListDeleteButtonClick(view, position);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSongListItemClick(v, position);
            }
        });
        SwipeLayout swipe_layout = (SwipeLayout) view.findViewById(R.id.swipe_layout);
        swipe_layout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout swipeLayout) {
                listener.onSongListItemLongClick(view, position);
            }

            @Override
            public void onOpen(SwipeLayout swipeLayout) {

            }

            @Override
            public void onStartClose(SwipeLayout swipeLayout) {

            }

            @Override
            public void onClose(SwipeLayout swipeLayout) {

            }

            @Override
            public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {

            }

            @Override
            public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {

            }
        });
        swipe_layout.setLeftSwipeEnabled(true);

        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
