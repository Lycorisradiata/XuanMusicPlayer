package com.jw.cool.xuanmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.jw.cool.xuanmusicplayer.R;

import java.util.List;

/**
 * Created by Administrator on 15-9-11.
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private Context mContext;
    private List<String> list;
    SwipeLayout.SwipeListener listener;
    public PlayListAdapter(Context mContext, List<String> list,
                           SwipeLayout.SwipeListener listener) {
        this.mContext = mContext;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SwipeLayout view =
                (SwipeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(final PlayListAdapter.ViewHolder holder, int position) {
        final View view = holder.mView;
        TextView textView = (TextView)view.findViewById(R.id.swipe_item_top);
        textView.setText(list.get(position));
        Button button = (Button) view.findViewById(R.id.swipe_item_delete);
        button.setText("delete");
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        public final SwipeLayout mView;
        public int position;

        public ViewHolder(SwipeLayout view, SwipeLayout.SwipeListener listener) {
            super(view);
            mView = view;
            mView.setShowMode(SwipeLayout.ShowMode.LayDown);
            mView.addSwipeListener(listener);
        }
    }
}
