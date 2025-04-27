package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.game.OptionBean;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;

import java.util.List;

public class GameTzOptionAdapter extends RecyclerView.Adapter<GameTzOptionAdapter.Vh>  {
    private Context mContext;
    private List<OptionBean> mList;
    private OnRvClickListener mClickListener ;

    public GameTzOptionAdapter(Context context, List<OptionBean> list, OnRvClickListener clickListener) {
        this.mContext = context;
        this.mList = list;
        this.mClickListener = clickListener ;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.adapter_game_tz_option, parent, false),mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) { }

    @Override
    public void onBindViewHolder(@NonNull Vh vh,final int position, @NonNull List<Object> payloads) {
        vh.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void release() {
        if (mList != null) {
            mList.clear();
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mNum;
        TextView mName;

        public Vh(View itemView, final OnRvClickListener clickListener) {
            super(itemView);

            mNum =  itemView.findViewById(R.id.tv_option_num);
            mName = itemView.findViewById(R.id.tv_option_name);

            //spanSize = 6 ，实际计算按8个算
            int screenWidth = ScreenDimenUtil.getInstance().getScreenWdith() ;
            int dvWid = DpUtil.dp2px(16) ;
            int singleWidth = (screenWidth - dvWid * 8) / 8 ;
            mNum.getLayoutParams().width = singleWidth ;
            mNum.getLayoutParams().height = singleWidth ;

//            int screenWidth = ScreenDimenUtil.getInstance().getScreenWdith() ;
//            int singleWidth = DpUtil.dp2px(60) ;
//            int size = getItemCount() ;
//            if(size > 4){
//                int dvWid = DpUtil.dp2px(16) ;
//                int singleSize = (size + 1) / 2 ;
//
//                if((singleWidth + dvWid) * singleSize > screenWidth){
//                    singleWidth = (screenWidth - dvWid * singleSize) / singleSize ;
//                    mNum.getLayoutParams().width = singleWidth ;
//                    mNum.getLayoutParams().height = singleWidth ;
//                }
//            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onClick(0,getAdapterPosition()) ;
                    }
                }
            });
        }

        void setData(OptionBean bean) {
            mNum.setText(bean.getName());
            mName.setText(bean.getRatio());

            boolean isChecked = bean.isChecked() ;
            mNum.setBackground(mContext.getResources().getDrawable(isChecked ? R.drawable.bg_game_tz_option_checked : R.drawable.bg_game_tz_option_normal));
            mNum.setTextColor(mContext.getResources().getColor(isChecked ? R.color.white : R.color.rgbDE5245));
        }
    }


}
