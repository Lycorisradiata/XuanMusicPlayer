package com.jw.cool.xuanmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jw.cool.xuanmusicplayer.R;

import java.util.List;

public class NavigationMainAdapter extends RecyclerView.Adapter<NavigationMainAdapter.ViewHolder>{
 
    private List<Actor> actors;
 
    private Context mContext;
    private NavigationItemListener listener;
 
    public NavigationMainAdapter(Context context, List<Actor> actors, NavigationItemListener listener)
    {
        this.mContext = context;
        this.actors = actors;
        this.listener = listener;
    }
 
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i )
    {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Actor p = actors.get(position);
        holder.mTextView.setText(p.name);

        holder.mImageView.setImageDrawable(mContext.getResources().getDrawable(p.getImageResourceId(mContext)));
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "onClick " + position);
            }
        });

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "onClick position " + position);
                listener.navigationItemClick(holder, position);
            }
        });
    }
 
    @Override
    public int getItemCount()
    {
        // 返回数据总数
        return actors == null ? 0 : actors.size();
    }
 
    // 重写的自定义ViewHolder
    public static class ViewHolder
        extends RecyclerView.ViewHolder
    {
        public TextView mTextView;
 
        public ImageView mImageView;

        public RelativeLayout layout;
        public ViewHolder( View v )
        {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.name);
            mImageView = (ImageView) v.findViewById(R.id.pic);
            layout = (RelativeLayout) v.findViewById(R.id.card_view_relative_layout);
        }
    }
}