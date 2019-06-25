package com.newmusic.wangkly.newmusic.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.newmusic.wangkly.newmusic.R;
import com.newmusic.wangkly.newmusic.beans.PlaylistItem;

import java.util.List;

public class PlaylistDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private List<PlaylistItem> mList;


    private final int ITEM_VIEWTYPE = 0;//普通列表项的view_type


    private final int HEADER_VIEWTYPE = 1;//header 项的 view_type;





    public PlaylistDetailAdapter(List<PlaylistItem> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == ITEM_VIEWTYPE){
           View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item,viewGroup,false);

            return  new ViewHolder(view);

        }else {

            View Header = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.playlist_detail_header,viewGroup,false);
            return  new HeaderViewHolder(Header);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if(viewHolder instanceof ViewHolder){
            final PlaylistItem item = mList.get(i);

            ViewHolder holder = (ViewHolder) viewHolder;
            LinearLayout itemLine =  holder.item_line;

//            holder.image.setImageResource(R.drawable.ic_arrow);

            holder.audio_title.setText(item.getTitle());
            holder.audio_author.setText(item.getArtist());

//            itemLine.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("RecyclerListAdapter","recycler item click"+ item.getName());
//                }
//            });


        }else if(viewHolder instanceof  HeaderViewHolder){

            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

            holder.image.setImageResource(R.drawable.ic_arrow);

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



    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item_line;



        private TextView audio_title;

        private TextView audio_author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_line = itemView.findViewById(R.id.item_line);
//            image = itemView.findViewById(R.id.recycler_img);

            audio_title = itemView.findViewById(R.id.audio_title);
            audio_author = itemView.findViewById(R.id.audio_author);
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




}
