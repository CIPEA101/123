package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.GameTypeBean;
import com.yunbao.phonelive.http.HttpConsts;

import java.util.ArrayList;
import java.util.List;

public class AdapterHomeGameList extends RecyclerView.Adapter<AdapterHomeGameList.AhglHolder> {
    private Context mContext ;
    private List<GameTypeBean> mDataList ;
    private OnGameOptClickListener mClickListener ;

    public AdapterHomeGameList(Context mContext,OnGameOptClickListener clickListener) {
        this.mContext = mContext;
        this.mClickListener = clickListener ;

        mDataList = new ArrayList<>() ;
        mDataList.add(new GameTypeBean("1", HttpConsts.getGameNameByType("1"))) ;
        mDataList.add(new GameTypeBean("2", HttpConsts.getGameNameByType("2"))) ;
        mDataList.add(new GameTypeBean("3", HttpConsts.getGameNameByType("3"))) ;
        mDataList.add(new GameTypeBean("4", HttpConsts.getGameNameByType("4"))) ;
        mDataList.add(new GameTypeBean("5", HttpConsts.getGameNameByType("5"))) ;
        mDataList.add(new GameTypeBean("6", HttpConsts.getGameNameByType("6"))) ;
        mDataList.add(new GameTypeBean("7", HttpConsts.getGameNameByType("7"))) ;
    }

    @NonNull
    @Override
    public AhglHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AhglHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_home_game_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AhglHolder holder, int position) {
        GameTypeBean info = mDataList.get(position) ;

        holder.mIv.setImageDrawable(mContext.getResources().getDrawable(HttpConsts.getGameIconByType(info.getId())));
        holder.mNameTv.setText(info.getName()) ;
    }

    @Override
    public int getItemCount() {
        return mDataList.size() ;
    }

    class AhglHolder extends RecyclerView.ViewHolder{
        private ImageView mIv ;
        private TextView mNameTv ;

        public AhglHolder(View itemView) {
            super(itemView);

            mIv = itemView.findViewById(R.id.adapter_home_game_iv) ;
            mNameTv = itemView.findViewById(R.id.adapter_home_game_tv) ;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onGameClick(mDataList.get(getAdapterPosition())) ;
                }
            });

        }
    }

    public interface OnGameOptClickListener{
        void onGameClick(GameTypeBean bean) ;
    }

}
