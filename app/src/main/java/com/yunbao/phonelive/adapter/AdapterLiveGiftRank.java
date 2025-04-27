package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LevelBean;
import com.yunbao.phonelive.bean.RankList;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnRvClickListener;

import java.util.List;

public class AdapterLiveGiftRank extends RecyclerView.Adapter<AdapterLiveGiftRank.AlgrHolder> {
    private Context mContext ;
    private List<RankList> mDataList ;
    private OnRvClickListener mClickListener ;

    public AdapterLiveGiftRank(Context mContext, List<RankList> mDataList, OnRvClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public AlgrHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlgrHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_live_gift_rank_list,parent,false),mClickListener) ;
    }

    @Override
    public void onBindViewHolder(@NonNull AlgrHolder holder, int position) {
        RankList info = mDataList.get(position) ;

        ImgLoader.displayAvatar(info.getAvatar(), holder.mUserIv);
        holder.mUserTv.setText(info.getUser_nicename());
        holder.mMoneyTv.setText(info.getTotalcoin());

        int level = info.getLevel() ;
        if(level <= 0){
            holder.mUserLevelIv.setVisibility(View.GONE);
        }else{
            holder.mUserLevelIv.setVisibility(View.VISIBLE);

            AppConfig appConfig = AppConfig.getInstance();
            LevelBean levelBean = appConfig.getLevel(level);
            if (levelBean != null) {
                ImgLoader.display(levelBean.getThumb(), holder.mUserLevelIv);
            }
        }

        int index = position + 1 ;
        if(index > 3){
            holder.mIndexIv.setVisibility(View.GONE) ;
            holder.mIndexTv.setText(String.valueOf(index)) ;
            holder.mIndexTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_transparent));
        }else{
            holder.mIndexTv.setText("");
            holder.mIndexIv.setVisibility(View.VISIBLE) ;
            if(1 == index){
                holder.mIndexIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.photos1));
            }else if(2 == index){
                holder.mIndexIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.photos2));
            }else{
                holder.mIndexIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.photos3));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size() ;
    }

    static class AlgrHolder extends RecyclerView.ViewHolder{
        private TextView mIndexTv ;
        private ImageView mIndexIv ;
        private ImageView mUserIv ;
        private TextView mUserTv ;
        private ImageView mUserLevelIv ;
        private TextView mMoneyTv ;

        public AlgrHolder(View itemView, final OnRvClickListener clickListener) {
            super(itemView);

            mIndexTv = itemView.findViewById(R.id.adapter_live_gift_rank_index_tv) ;
            mIndexIv = itemView.findViewById(R.id.adapter_live_gift_rank_index_iv) ;
            mUserIv = itemView.findViewById(R.id.adapter_live_gift_rank_user_iv) ;
            mUserTv = itemView.findViewById(R.id.adapter_live_gift_rank_user_tv) ;
            mUserLevelIv = itemView.findViewById(R.id.adapter_live_gift_rank_level_tv) ;
            mMoneyTv = itemView.findViewById(R.id.adapter_live_gift_rank_money_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onClick(0,getAdapterPosition()) ;
                    }
                }
            });

        }
    }
}
