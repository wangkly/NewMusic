package com.newmusic.wangkly.newmusic.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.newmusic.wangkly.newmusic.MainActivity;
import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.activities.PlaylistDetailActivity;
import com.newmusic.wangkly.newmusic.adapter.OnlinePlayListAdapter;
import com.newmusic.wangkly.newmusic.beans.OnlinePlaylistItem;
import com.newmusic.wangkly.newmusic.constant.Constant;
import com.newmusic.wangkly.newmusic.interfaces.QueryResultListener;
import com.newmusic.wangkly.newmusic.listener.RecyclerViewScrollListener;
import com.newmusic.wangkly.newmusic.tasks.OnlineListTask;
import com.newmusic.wangkly.newmusic.utils.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OnlinePlayListFragment extends Fragment {


    private int total = 0;

    private int currentPage = 0;

    private SwipeRefreshLayout refreshLayout;

    private RecyclerView mRecyclerView;

    private OnlinePlayListAdapter mAdapter;


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();

            int position = viewHolder.getAdapterPosition();


            OnlinePlaylistItem item = mAdapter.getItemAtPosition(position);

            System.out.println(item);

            long listId = item.getId();

            String cover = item.getCoverImgUrl();


            Intent intent = new Intent(getContext(), PlaylistDetailActivity.class);

            Bundle bundle = new Bundle();

            bundle.putBinder("myBinder",((MainActivity)getActivity()).getMyBinder());

            bundle.putLong("listId",listId);
            bundle.putString("cover",cover);

            intent.putExtras(bundle);

            startActivity(intent);

//                viewHolder.getItemId();
//            View itemView = viewHolder.itemView;

        }
    };


    private QueryResultListener listener = new QueryResultListener() {
        @Override
        public void onSuccess(String result) {


            try {
                JSONObject json = new JSONObject(result);

                int total = json.getInt("total");
                boolean more = json.getBoolean("more");

                List<OnlinePlaylistItem> itemList = new ArrayList<>();

                JSONArray playlist = json.getJSONArray("playlists");

                for (int i =0 ;i < playlist.length(); i++){

                    JSONObject obj = playlist.getJSONObject(i);

                    OnlinePlaylistItem item = new OnlinePlaylistItem();
                    item.setId(obj.getLong("id"));
                    item.setName(obj.getString("name"));
                    item.setCoverImgUrl(obj.getString("coverImgUrl"));
                    item.setDescription(obj.getString("description"));
                    item.setSubscribedCount(obj.getInt("subscribedCount"));
//                    item.setTags(obj.getJSONArray("tags"));

                    itemList.add(item);

                }

                //通知adapter
                mAdapter.addItems(itemList);
                mAdapter.setLoadState(mAdapter.LOAD_COMPLETE);

                setTotal(total);


                SQLiteDatabase db = new DBHelper(getContext()).getWritableDatabase();

               Iterator<OnlinePlaylistItem> it = itemList.iterator();

               while (it.hasNext()){

                   OnlinePlaylistItem item = it.next();

                   ContentValues value = new ContentValues();

                   value.put("id",item.getId());
                   value.put("name",item.getName());
                   value.put("coverImgUrl",item.getCoverImgUrl());
                   value.put("description",item.getDescription());
                   value.put("subscribedCount",item.getSubscribedCount());

                    db.insert("online_list",null,value);
               }



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }



        @Override
        public void onFailure(String result) {


        }

    };




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view  = inflater.inflate(R.layout.online_play_list,container,false);

            mRecyclerView = view.findViewById(R.id.onlinePlaylist);

            refreshLayout = view.findViewById(R.id.refreshLayout);

            GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);

            mRecyclerView.setLayoutManager(layoutManager);


            //查询本地是否有缓存歌单，如果没有在去网络查询
            List<OnlinePlaylistItem> list = queryPlayListLocalCache();

            mAdapter = new OnlinePlayListAdapter(list,clickListener);


            mRecyclerView.setAdapter(mAdapter);
            final StringBuilder sb = new StringBuilder(Constant.HOST_URL);

            sb.append("/top/playlist?limit=30&order=hot&offset=");

            if(list.size() == 0){
                OnlineListTask task = new OnlineListTask(listener);

                task.execute(sb.toString()+"0");
            }




            mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
                @Override
                public void loadMore() {
                    mAdapter.setLoadState(mAdapter.LOADING);

                    OnlineListTask task = new OnlineListTask(listener);
                    currentPage++;

                    task.execute(sb.toString()+String.valueOf(currentPage* 30));


                }
            });


            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    clearPlaylistCache(); //清空本地缓存

                    OnlineListTask task = new OnlineListTask(listener);

                    task.execute(sb.toString()+"0");

                    currentPage = 0;
                   refreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(),"刷新成功",Toast.LENGTH_SHORT).show();
                }
            });


            return view;
    }



    public List<OnlinePlaylistItem> queryPlayListLocalCache(){

        List<OnlinePlaylistItem> list = new ArrayList<>();

        DBHelper helper = DBHelper.getInstance(getContext());

        Cursor cursor =helper.getWritableDatabase().query("online_list",null,null,null,null,null,null);

        if(null != cursor && cursor.moveToFirst()){

            while (cursor.moveToNext()){

                OnlinePlaylistItem item = new OnlinePlaylistItem();
                item.setId(cursor.getLong(cursor.getColumnIndex("id")));
                item.setName(cursor.getString(cursor.getColumnIndex("name")));
                item.setCoverImgUrl(cursor.getString(cursor.getColumnIndex("coverImgUrl")));
                item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                item.setSubscribedCount(cursor.getInt(cursor.getColumnIndex("subscribedCount")));

                list.add(item);
            }


        }


        return list;
    }


    public void clearPlaylistCache(){

        DBHelper helper = DBHelper.getInstance(getContext());

        helper.getWritableDatabase().delete("online_list",null,null);

    }


    public void setTotal(int total){
        this.total = total;
    }


}
