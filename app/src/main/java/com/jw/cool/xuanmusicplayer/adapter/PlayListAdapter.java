package com.jw.cool.xuanmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private Context mContext;
    private List<SPNavigationItem> list;
    OnPlayListItemListener listener;
    public PlayListAdapter(Context mContext, List<SPNavigationItem> list,
                           OnPlayListItemListener listener) {
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
        TextView textView;
        if(position == list.size() - 1){
            textView = (TextView)view.findViewById(R.id.swipe_item_top_sub);
        }else{
             textView = (TextView)view.findViewById(R.id.swipe_item_top);
        }
        textView.setText(list.get(position).getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayListItemClick(v, position);
            }
        });
        textView.setVisibility(View.VISIBLE);


        Button button = (Button) view.findViewById(R.id.swipe_item_delete);
        button.setText("delete");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayListDeleteButtonClick(view, position);
            }
        });


        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
