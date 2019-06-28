package com.newmusic.wangkly.newmusic.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.newmusic.wangkly.newmusic.activities.PlaylistDetailActivity;
import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.adapter.OnlinePlayListAdapter;
import com.newmusic.wangkly.newmusic.beans.OnlinePlaylistItem;
import com.newmusic.wangkly.newmusic.interfaces.QueryResultListener;
import com.newmusic.wangkly.newmusic.listener.RecyclerViewScrollListener;
import com.newmusic.wangkly.newmusic.tasks.OnlineListTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

            intent.putExtra("listId",listId);

            intent.putExtra("cover",cover);

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
        mAdapter = new OnlinePlayListAdapter(new ArrayList<OnlinePlaylistItem>(),clickListener);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view  = inflater.inflate(R.layout.online_play_list,container,false);

            mRecyclerView = view.findViewById(R.id.onlinePlaylist);

            refreshLayout = view.findViewById(R.id.refreshLayout);

            GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);

            mRecyclerView.setLayoutManager(layoutManager);

            mRecyclerView.setAdapter(mAdapter);

            OnlineListTask task = new OnlineListTask(listener);

            final StringBuilder sb = new StringBuilder("http://172.19.8.35:3000/top/playlist?limit=30&order=hot&offset=");

            task.execute(sb.toString()+"0");


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




                }
            });


            return view;
    }




    public void setTotal(int total){
        this.total = total;
    }


}
