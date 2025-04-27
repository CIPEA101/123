package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.GameTzChip;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;

import java.util.List;

public class AdapterGameTzChip extends RecyclerView.Adapter<AdapterGameTzChip.AgtcHolder> {
    private Context mContext ;
    private List<GameTzChip> mDataList ;
    private OnRvClickListener mClickListener ;

    public AdapterGameTzChip(Context mContext, List<GameTzChip> mDataList, OnRvClickListener mClickListener) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public AgtcHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AgtcHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_game_tz_chip,parent,false),mClickListener) ;
    }

    @Override
    public void onBindViewHolder(@NonNull AgtcHolder holder, int position) {
        GameTzChip info = mDataList.get(position) ;
        holder.mNameTv.setBackground(mContext.getResources().getDrawable(info.getBgRes()));
    }

    @Override
    public int getItemCount() {
        return mDataList.size() ;
    }

    class AgtcHolder extends RecyclerView.ViewHolder{
        private TextView mNameTv ;

        public AgtcHolder(View itemView, final OnRvClickListener clickListener) {
            super(itemView);

            mNameTv = itemView.findViewById(R.id.adapter_game_tz_chip_tv) ;

            int size = getItemCount() ;
            int singleWidHei = DpUtil.dp2px(45) ;
            int dvWid = DpUtil.dp2px(10) ;
            int screenWidth = ScreenDimenUtil.getInstance().getScreenWdith() ;
            if((singleWidHei + dvWid) * size > screenWidth){
                singleWidHei = (screenWidth - dvWid * size) / size ;
                mNameTv.getLayoutParams().width = singleWidHei ;
                mNameTv.getLayoutParams().height = singleWidHei ;
            }

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
