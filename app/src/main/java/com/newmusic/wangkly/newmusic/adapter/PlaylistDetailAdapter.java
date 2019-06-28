package com.newmusic.wangkly.newmusic.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.beans.OnlineSongItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<OnlineSongItem> mList;


    private final int ITEM_VIEWTYPE = 0;//普通列表项的view_type


    private final int HEADER_VIEWTYPE = 1;//header 项的 view_type;


    private View.OnClickListener itemClickListener;


    private String coverUrl;


    public PlaylistDetailAdapter(List<OnlineSongItem> mList,String cover,View.OnClickListener listener) {
        this.mList = mList;
        itemClickListener = listener;
        coverUrl = cover;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == ITEM_VIEWTYPE){
           View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.online_song_item,viewGroup,false);

            return  new ViewHolder(view);

        }else {

            View Header = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_detail_header,viewGroup,false);
            return  new HeaderViewHolder(Header);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if(viewHolder instanceof ViewHolder){
            final OnlineSongItem item = mList.get(i-1);

            ViewHolder holder = (ViewHolder) viewHolder;

            holder.song_name.setText(item.getName());
            holder.song_author.setText(item.getAuthorName()+"-"+item.getAlbumName());


        }else if(viewHolder instanceof  HeaderViewHolder){

            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            Picasso.get().load(coverUrl).into(holder.image);

        }


    }



    @Override
    public int getItemViewType(int position) {


        if(position == 0){

            return HEADER_VIEWTYPE;
        }else{
            return ITEM_VIEWTYPE;
        }



    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
    }



    public OnlineSongItem getItemAtPosition(int position){

        return mList.get(position);
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout song_item;

        private TextView song_name;

        private TextView song_author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            song_item = itemView.findViewById(R.id.song_item);

            song_name = itemView.findViewById(R.id.song_name);
            song_author = itemView.findViewById(R.id.song_author);

            itemView.setTag(this);

            itemView.setOnClickListener(itemClickListener);
        }
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder{

        LinearLayout detail_header;

        private ImageView image;


        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            detail_header = itemView.findViewById(R.id.detail_header);
            image = itemView.findViewById(R.id.header_bg);

        }
    }


    public void addItems(List<OnlineSongItem> list){

        mList.addAll(list);
    }


}
