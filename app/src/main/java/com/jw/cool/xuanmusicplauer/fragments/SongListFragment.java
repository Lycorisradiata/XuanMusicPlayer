package com.jw.cool.xuanmusicplauer.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jw.cool.xuanmusicplauer.R;
import com.jw.cool.xuanmusicplauer.coreservice.MusicRetriever;
import com.jw.cool.xuanmusicplauer.coreservice.PrepareMusicRetrieverTask;
import com.jw.cool.xuanmusicplauer.coreservice.PrepareMusicRetrieverTask.MusicRetrieverPreparedListener;

import java.util.ArrayList;
import java.util.List;

public class SongListFragment extends android.support.v4.app.Fragment
        implements MusicRetrieverPreparedListener {
    final String TAG = "SongListFragment";
    List<MusicRetriever.Item> itemList = new ArrayList<MusicRetriever.Item>();
	Adapter adapter;
    MusicRetriever mRetriever;
    public static SongListFragment newInstance(Context context,Bundle bundle) {
        SongListFragment newFragment = new SongListFragment();
        newFragment.setArguments(bundle);
        return newFragment;

    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_activity_new, container, false);
        return recyclerView;
    }


    
    @Override
    public void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	
    	
    	recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SongListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        mRetriever = new MusicRetriever(getActivity().getContentResolver());
        (new PrepareMusicRetrieverTask(mRetriever,this)).execute();
//        如上述代码：
//
//    	Line1: 使RecyclerView保持固定的大小,这样会提高RecyclerView的性能。
//
//    	Line3: LinearLayoutManager，如果你需要显示的是横向滚动的列表或者竖直滚动的列表，则使用这个LayoutManager。显然，我们要实现的是ListView的效果，所以需要使用它。生成这个LinearLayoutManager之后可以设置他滚动的方向，默认竖直滚动，所以这里没有显式地设置。
//
//    	Line6: 初始化数据源。
//
//    	Line7～9: 跟ListView一样，需要设置RecyclerView的Adapter，但是这里的Adapter跟ListView使用的Adapter不一样，这里的Adapter需要继承RecyclerView.Adapter，需要实现3个方法：
//
//    	　　- onCreateViewHolder()
//
//    	　　- onBindViewHolder()
//
//    	　　- getItemCount()
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SongListAdapter(getActivity()));
    }

    @Override
    public void onMusicRetrieverPrepared() {
         itemList = mRetriever.getItems();
//        for (MusicRetriever.Item item:itemList){
//            Log.i(TAG, "ITEM " + item);
//        }
        adapter.notifyDataSetChanged();
    }


    public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> {

        private Context mContext;

        public SongListAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public SongListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_activity, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SongListAdapter.ViewHolder holder, int position) {
            final View view = holder.mView;
            TextView textView = (TextView)view.findViewById(R.id.textView);
            textView.setText(itemList.get(position).getDisplayName());
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;

            public ViewHolder(View view) {
                super(view);
                mView = view;

            }
        }
    }

}


/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/17/15.
 */
//public class PersonAdapter extends RecyclerView.Adapter {
//    public static interface OnRecyclerViewListener {
//        void onItemClick(int position);
//        boolean onItemLongClick(int position);
//    }
//
//    private OnRecyclerViewListener onRecyclerViewListener;
//
//    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
//        this.onRecyclerViewListener = onRecyclerViewListener;
//    }
//
//    private static final String TAG = PersonAdapter.class.getSimpleName();
//    private List<Person> list;
//
//    public PersonAdapter(List<Person> list) {
//        this.list = list;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        Logger.d(TAG, "onCreateViewHolder, i: " + i);
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_test_item_person, null);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        view.setLayoutParams(lp);
//        return new PersonViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
//        Logger.d(TAG, "onBindViewHolder, i: " + i + ", viewHolder: " + viewHolder);
//        PersonViewHolder holder = (PersonViewHolder) viewHolder;
//        holder.position = i;
//        Person person = list.get(i);
//        holder.nameTv.setText(person.getName());
//        holder.ageTv.setText(person.getAge() + "岁");
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener
//    {
//        public View rootView;
//        public TextView nameTv;
//        public TextView ageTv;
//        public int position;
//
//        public PersonViewHolder(View itemView) {
//            super(itemView);
//            nameTv = (TextView) itemView.findViewById(R.id.recycler_view_test_item_person_name_tv);
//            ageTv = (TextView) itemView.findViewById(R.id.recycler_view_test_item_person_age_tv);
//            rootView = itemView.findViewById(R.id.recycler_view_test_item_person_view);
//            rootView.setOnClickListener(this);
//            rootView.setOnLongClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (null != onRecyclerViewListener) {
//                onRecyclerViewListener.onItemClick(position);
//            }
//        }
//
//        @Override
//        public boolean onLongClick(View v) {
//            if(null != onRecyclerViewListener){
//                return onRecyclerViewListener.onItemLongClick(position);
//            }
//            return false;
//        }
//    }
//
//}

//@Override
//public View getView(int position, View convertView, ViewGroup parent) {
//    ViewHolder holder;
//    if(null == convertView){
//        holder = new ViewHolder();
//        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        convertView = mInflater.inflate(R.layout.item, null);
//        holder.btn = (Button) convertView.findViewById(R.id.btn);
//        holder.tv = (TextView) convertView.findViewById(R.id.tv);
//        holder.iv = (TextView) convertView.findViewById(R.id.iv);
//
//        convertView.setTag(holder);
//    }else{
//        holder = (ViewHolder) convertView.getTag();
//    }
//    final HashMap<String, Object> map = list.get(position);
//
//    holder.iv.setImageResource(Integer.valueOf(map.get("iv").toString()));
//    holder.tv.setText(map.get("tv").toString());
//
//    holder.btn.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Toast.makeText(context, map.get("btn").toString(), Toast.LENGTH_SHORT).show();
//        }
//    });
//
//    return convertView;
//}

//class ViewHolder{
//    Button btn;
//    ImageView iv;
//    TextView tv;
//
//}

