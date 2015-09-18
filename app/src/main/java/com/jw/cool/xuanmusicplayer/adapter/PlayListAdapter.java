package com.jw.cool.xuanmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
//import android.widget.ArrayAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.ListView;
import android.widget.ListView;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.R;
import com.jw.cool.xuanmusicplayer.coreservice.MusicRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Administrator on 15-9-15.
 */
public class PlayListAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "PlayListAdapter";
    private Context mContext;
    private List<String> list;
    OnPlayListItemListener listener;
    public PlayListAdapter(Context mContext, List<String> list,
                           OnPlayListItemListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final View view = holder.mView;
        TextView textView = (TextView)view.findViewById(R.id.play_list_name);
        Log.d(TAG, "onBindViewHolder" + textView);
        Log.d(TAG, "onBindViewHolder list " + list + " " + list.size());
        textView.setText(list.get(position));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayListItemClick(v, position);
            }
        });


        Button delete = (Button) view.findViewById(R.id.delete_play_list);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayListDeleteButtonClick(view, position);
            }
        });

        Button create = (Button) view.findViewById(R.id.create_play_list);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayListCreateButtonClick(v, position);
            }
        });

        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
