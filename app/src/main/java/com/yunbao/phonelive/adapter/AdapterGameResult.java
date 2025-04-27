package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.game.AwardBean;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;

import java.util.List;

public class AdapterGameResult extends RecyclerView.Adapter<AdapterGameResult.Vh>  {
    private Context mContext;
    private List<AwardBean> mList;
    private int type;
    private int mParentWidth ;
    private int mCornorSize ;
    private boolean mNeedAutoFit ;

    public AdapterGameResult(Context context, List<AwardBean> list, int type){
        this(context,list,type,ScreenDimenUtil.getInstance().getScreenWdith() - DpUtil.dp2px(45)) ;
    }

    public AdapterGameResult(Context context, List<AwardBean> list, int type, int parentWidth){
        this.mContext = context;
        this.mList = list;
        this.type = type;
        mParentWidth = parentWidth ;
        mCornorSize = DpUtil.dp2px(30) ;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.adapter_game_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) { }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), payload);
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

        TextView mName;

        public Vh(View itemView) {
            super(itemView);
            mName =  itemView.findViewById(R.id.adapter_game_result_tv);

            if(mParentWidth > 0){
                int itemSize = mList.size() ;
                int totalWidth = itemSize * (mCornorSize + DpUtil.dp2px(3)) ;
                if(totalWidth > mParentWidth){//总宽度超过父布局了，需要调整宽度
                    mCornorSize = (mParentWidth - (DpUtil.dp2px(3) * itemSize)) / itemSize ;
                    mNeedAutoFit = true ;
                }
            }

            if(mNeedAutoFit){
                mName.getLayoutParams().width = mCornorSize ;
                mName.getLayoutParams().height = mCornorSize ;
            }
        }

        void setData(final AwardBean bean, Object payload) {
            if (payload == null) {
                if (type == 1) {
                    mName.setBackgroundResource(bean.getImageUrl());
                }else if(type == 2){
                    mName.setText(bean.getName());
                }else if (type == 3){//六合彩
                    mName.setText(bean.getName());
                    mName.setBackgroundResource(bean.getImageUrl());
                }else if(type == 4){
                    String value = bean.getName() ;
                    mName.setText(value);

                    GradientDrawable bgDr = new GradientDrawable() ;
                    bgDr.setShape(GradientDrawable.OVAL);
                    bgDr.setCornerRadius(mCornorSize);
                    bgDr.setColor(mContext.getResources().getColor(bean.getNumberBgColor())) ;
                    mName.setBackground(bgDr) ;
                }
            }
        }
    }
}
