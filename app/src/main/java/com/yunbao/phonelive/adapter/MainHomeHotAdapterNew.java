package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.utils.IconUtil;

import java.util.List;

/**
 * Created by cxf on 2018/9/26.
 */

public class MainHomeHotAdapterNew extends RecyclerView.Adapter<MainHomeHotAdapterNew.Vh> {
    private Context mContext ;
    private List<LiveBean> mList ;
    private OnRvClickListener mClickListener ;

    public MainHomeHotAdapterNew(Context mContext, List<LiveBean> mList, OnRvClickListener clickListener) {
        this.mContext = mContext;
        this.mList = mList;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public MainHomeHotAdapterNew.Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.item_main_home_live_new, parent, false),mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MainHomeHotAdapterNew.Vh vh, int position) {
        vh.setData(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size() ;
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mCover;
        ImageView mAvatar;
        TextView mName;
        TextView mTitle;
        TextView mNum;
        ImageView mType;
        TextView mGameNameTv;

        public Vh(View itemView, final OnRvClickListener clickListener) {
            super(itemView);
            mCover = (ImageView) itemView.findViewById(R.id.cover);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mNum = (TextView) itemView.findViewById(R.id.num);
            mType = (ImageView) itemView.findViewById(R.id.type);
            mGameNameTv = itemView.findViewById(R.id.game_name_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onClick(0,getAdapterPosition()) ;
                    }
                }
            });
        }

        void setData(LiveBean bean, int position) {
            itemView.setTag(position);
            ImgLoader.display(bean.getThumb(), mCover);
            ImgLoader.display(bean.getAvatar(), mAvatar);
            mName.setText(bean.getUserNiceName());
            mGameNameTv.setText(bean.getGameName());

            if (TextUtils.isEmpty(bean.getTitle())) {
                if (mTitle.getVisibility() == View.VISIBLE) {
                    mTitle.setVisibility(View.GONE);
                }
            } else {
                if (mTitle.getVisibility() != View.VISIBLE) {
                    mTitle.setVisibility(View.VISIBLE);
                }
                mTitle.setText(bean.getTitle());
            }
            mNum.setText(bean.getNums());
            mType.setImageResource(IconUtil.getLiveTypeIcon(bean.getType()));
        }
    }

}
