package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LevelBean;
import com.yunbao.phonelive.bean.RankList;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.views.MainRankListViewHolder;

import java.util.List;

/**
 * Created by cxf on 2018/9/27.
 */

public class MainRankListAdapter extends RefreshAdapter<RankList> {

    private int mRankType ;

    public MainRankListAdapter(Context context, int rankType) {
        super(context);
        mRankType = rankType ;
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            int size = mList.size() ;
            if(size < 10){
                //第一个没在列表显示，所以需要去掉1
                size = 9 ;
            }
            return size;
        }
        return 0;
    }

    @Override
    public void clearData() {
        super.clearData();
    }

    @Override
    public void insertList(List<RankList> list) {
        if (mRecyclerView != null && mList != null && list != null && list.size() > 0) {
            int p = mList.size() + 1;

            mList.addAll(list);
            notifyItemRangeInserted(p, list.size());
            mRecyclerView.scrollBy(0, mLoadMoreHeight);
        }
    }

    @Override
    public void refreshData(List<RankList> list) {
        super.refreshData(list);
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RankViewHolder(mInflater.inflate(R.layout.item_main_rank_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        if (holder instanceof RankViewHolder) {
            //第一个数据没在列表中显示，所以数据是从第二个开始的，position+1
            int realItemPosi = position + 1 ;
            ((RankViewHolder) holder).setData(mList.size() > realItemPosi ? mList.get(realItemPosi) : null, realItemPosi);
        }
    }

    class RankViewHolder extends RecyclerView.ViewHolder {
        TextView mIndexTv;
        ImageView mAvatarIv;
        TextView mNameTv;
        ImageView mLevelIv;
        ImageView mVotesIv;
        TextView mVotesTv;

        public RankViewHolder(View itemView) {
            super(itemView);

            mIndexTv = itemView.findViewById(R.id.item_main_rank_no_tv);
            mAvatarIv = itemView.findViewById(R.id.item_main_rank_head_iv);
            mNameTv = itemView.findViewById(R.id.item_main_rank_name_tv);
            mLevelIv = itemView.findViewById(R.id.item_main_rank_level_iv);
            mVotesIv = itemView.findViewById(R.id.item_main_rank_gift_iv);
            mVotesTv = itemView.findViewById(R.id.item_main_rank_gift_tv);
        }

        void setData(RankList bean, int position) {
            mIndexTv.setText(String.valueOf(position + 1)) ;

            if(MainRankListViewHolder.RANK_TYPE_CONTRIBUTE == mRankType){
                mVotesIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_main_rank_list_heart));
            }else if(MainRankListViewHolder.RANK_TYPE_RICH == mRankType){
                mVotesIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_main_rank_list_gold));
            }else{
                mVotesIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_main_rank_list_gift));
            }

            if(bean != null){
                ImgLoader.displayAvatar(bean.getAvatar(), mAvatarIv);
                mNameTv.setText(bean.getUser_nicename());
                int level = bean.getLevel() ;
                if(level <= 0){
                    mLevelIv.setVisibility(View.INVISIBLE);
                }else{
                    mLevelIv.setVisibility(View.VISIBLE);

                    AppConfig appConfig = AppConfig.getInstance();
                    LevelBean levelBean = appConfig.getLevel(level);
                    if (levelBean != null) {
                        ImgLoader.display(levelBean.getThumb(), mLevelIv);
                    }
                }

                if(MainRankListViewHolder.RANK_TYPE_RICH == mRankType){
                    mVotesTv.setText("*****");
                }else{
                    mVotesTv.setText(bean.getTotalcoin());
                }
            }else{
                mAvatarIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_avatar_placeholder)) ;
                mNameTv.setText("虚位以待");
                mLevelIv.setVisibility(View.INVISIBLE);

                if(MainRankListViewHolder.RANK_TYPE_RICH == mRankType){
                    mVotesTv.setText("*****");
                }else{
                    mVotesTv.setText("0") ;
                }
            }
        }
    }
}
