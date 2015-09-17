package com.jw.cool.xuanmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.R;

import java.util.List;

/**
 * Created by Administrator on 15-9-16.
 */
public class SubAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context mContext;
    private List<String> list;
    public SubAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final View view = holder.mView;
        TextView textView = (TextView)view.findViewById(R.id.sub_text_view);
        textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
