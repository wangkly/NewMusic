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
import com.newmusic.wangkly.newmusic.beans.OnlinePlaylistItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OnlinePlayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<OnlinePlaylistItem> mList;


    private final int ITEM_VIEWTYPE = 0;//普通列表项的view_type


    private final int FOOTER_VIEWTYPE = 1;//footer 项的 view_type;


    private int loadState= 2;


    public final int LOADING = 1;

    public final int LOAD_COMPLETE = 2;


    public final int LOAD_END = 3;


    private View.OnClickListener onItemClickListener;



    public OnlinePlayListAdapter(List<OnlinePlaylistItem> mList,
                                 View.OnClickListener itemClickListener) {
        this.mList = mList;
        onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        if (viewType == ITEM_VIEWTYPE){
           View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.online_playlist_item,viewGroup,false);

            return  new ViewHolder(view);

        }else {

            View Footer = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_footer,viewGroup,false);
            return  new FooterViewHolder(Footer);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if(viewHolder instanceof ViewHolder){
            final OnlinePlaylistItem item = mList.get(i);

            ViewHolder holder = (ViewHolder) viewHolder;
            LinearLayout itemLine =  holder.online_line;

            // http://square.github.io/picasso/
            Picasso.get().load(item.getCoverImgUrl()).into(holder.image);
            holder.name.setText(item.getName());

//            itemLine.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//
//                }
//            });


        }else if(viewHolder instanceof  FooterViewHolder){

            FooterViewHolder holder = (FooterViewHolder) viewHolder;

            switch (loadState){

                case LOADING:
                    holder.progressBar.setVisibility(View.VISIBLE);

                    holder.fText.setText("正在加载。。。");

                    break;

                case LOAD_COMPLETE:
                    holder.progressBar.setVisibility(View.GONE);

                    holder.fText.setText("加载完成");

                    break;

                case LOAD_END:
                    holder.progressBar.setVisibility(View.GONE);
                    holder.fText.setText("已经到底了哦");

                    break;
            }

        }


    }



    @Override
    public int getItemViewType(int position) {

        if(position + 1 == getItemCount()){

            return FOOTER_VIEWTYPE;
        }else{
            return ITEM_VIEWTYPE;
        }
    }



    @Override
    public int getItemCount() {
        return mList.size()+1;
    }



    public OnlinePlaylistItem getItemAtPosition(int position){

        return mList.get(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout online_line;

        private ImageView image;

        private TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            online_line = itemView.findViewById(R.id.online_line);
            image = itemView.findViewById(R.id.item_cover);
            name = itemView.findViewById(R.id.item_name);
            itemView.setTag(this);
            //添加点击事件
            itemView.setOnClickListener(onItemClickListener);
        }

    }


    class FooterViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar progressBar;

        private TextView fText;


        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.recycler_footer_progress);

            fText = itemView.findViewById(R.id.recycler_footer_txt);
        }
    }



    public void  setLoadState(int loadState){
        this.loadState = loadState;

        notifyDataSetChanged();

    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layoutManager =  recyclerView.getLayoutManager();

        if(layoutManager instanceof GridLayoutManager){

            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;


            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int i) {

                    return getItemViewType(i) == FOOTER_VIEWTYPE ? gridLayoutManager.getSpanCount():1;

                }
            });

        }

    }
    
    
    
    public void addItems(List<OnlinePlaylistItem> items){

        mList.addAll(items);
//        notifyDataSetChanged();
    }


}
