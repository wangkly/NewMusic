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
import com.newmusic.wangkly.newmusic.view.RecyclerItem;

import java.util.List;

/**
 * 不包含footer 的RecyclerView list
 */
public class PlainRecyclerViewAdapter extends RecyclerView.Adapter<PlainRecyclerViewAdapter.ViewHolder> {



    private List<RecyclerItem> mList;


    public PlainRecyclerViewAdapter(List<RecyclerItem> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public PlainRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

           View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item,viewGroup,false);

            return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlainRecyclerViewAdapter.ViewHolder viewHolder, int i) {

            final RecyclerItem item = mList.get(i);

            ViewHolder holder = (ViewHolder) viewHolder;
            LinearLayout itemLine =  holder.item_line;

            holder.image.setImageResource(R.drawable.ic_arrow);
            holder.textView.setText(item.getName());

//            itemLine.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("RecyclerListAdapter","recycler item click"+ item.getName());
//                }
//            });

    }



    @Override
    public int getItemCount() {
        return mList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout item_line;

        private ImageView image;

        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_line = itemView.findViewById(R.id.item_line);
            image = itemView.findViewById(R.id.recycler_img);
            textView = itemView.findViewById(R.id.recycler_txt);
        }
    }



//    @Override
//    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//
//        RecyclerView.LayoutManager layoutManager =  recyclerView.getLayoutManager();
//
//        if(layoutManager instanceof GridLayoutManager){
//
//            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
//
//
//            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int i) {
//
//                    return getItemViewType(i) == FOOTER_VIEWTYPE ? gridLayoutManager.getSpanCount():1;
//
//                }
//            });
//
//        }
//
//    }

}
