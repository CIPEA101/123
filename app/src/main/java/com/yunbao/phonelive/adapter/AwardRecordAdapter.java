package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.game.AwardBean;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;

import java.util.List;

public class AwardRecordAdapter extends RecyclerView.Adapter<AwardRecordAdapter.Vh>  {

    private List<AwardBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private int type;
    private int mCornorSize ;

    public AwardRecordAdapter(Context context, LayoutInflater inflater, List<AwardBean> list, int type) {
        this.mContext = context;
        this.mInflater = inflater;
        this.mList = list;
        this.type = type;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);

        //暂定最多10个
        mCornorSize = (int) ((ScreenDimenUtil.getInstance().getScreenWdith() - DpUtil.dp2px(33) * 10) / 10F);
        if(mCornorSize > DpUtil.dp2px(30)){
            mCornorSize = DpUtil.dp2px(30) ;
        }
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_award_record, parent, false));
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
            mName =  itemView.findViewById(R.id.last_ward_num);
        }

        void setData(final AwardBean bean, Object payload) {
            if (payload == null) {
                if (type == 1) {
                    mName.setBackgroundResource(bean.getImageUrl());
                }else if(type == 2){
                    mName.setText(bean.getName());
                }else if (type == 3){
                    mName.setText(bean.getName());
                    mName.setBackgroundResource(bean.getImageUrl());

//                    switch (getAdapterPosition()){
//                        case 0:
//                            mName.setText(bean.getName());
//                            mName.setBackgroundResource(R.color.rgb1278F7);
//                            break;
//                        case 1:
//                            mName.setText(bean.getName());
//                            mName.setBackgroundResource(R.color.rgbE997C7);
//                            break;
//                        case 2:
//                            mName.setText(bean.getName());
//                            mName.setBackgroundResource(R.color.rgb1779EF);
//                            break;
//                        case 3:
//                            mName.setText(bean.getName());
//                            mName.setBackgroundResource(R.color.rgbD692B0);
//                            break;
//                        case 4:
//                            mName.setText(bean.getName());
//                            mName.setBackgroundResource(R.color.rgb61BC9F);
//                            break;
//                        case 5:
//                        case 7:
//                            mName.setText(bean.getName());
//                            mName.setBackgroundResource(R.color.rgbE49DC7);
//                            break;
//                        case 6:
//                            mName.setBackgroundResource(bean.getImageUrl());
//                            break;
//                    }
                }else if(type == 4){
                    mName.setText(bean.getName());

                    GradientDrawable bgDr = new GradientDrawable() ;
                    bgDr.setShape(GradientDrawable.OVAL);
                    bgDr.setCornerRadius(mCornorSize);
                    bgDr.setColor(mContext.getResources().getColor(bean.getNumberBgColor())) ;
                    mName.setBackground(bgDr) ;
//                    switch (getAdapterPosition()){
//                        case 0:
//                            mName.setBackgroundResource(R.color.rgbD692B0);
//                            break;
//                        case 1:
//                            mName.setBackgroundResource(R.color.rgbC5DB38);
//                            break;
//                        case 2:
//                            mName.setBackgroundResource(R.color.rgb0A61EE);
//                            break;
//                        case 3:
//                            mName.setBackgroundResource(R.color.rgb2B2B43);
//                            break;
//                        case 4:
//                            mName.setBackgroundResource(R.color.rgb027113);
//                            break;
//                        case 5:
//                            mName.setBackgroundResource(R.color.rgb00BEFC);
//                            break;
//                        case 6:
//                            mName.setBackgroundResource(R.color.rgb1513FC);
//                            break;
//                        case 7:
//                            mName.setBackgroundResource(R.color.rgb9BABE7);
//                            break;
//                        case 8:
//                            mName.setBackgroundResource(R.color.rgbD91C18);
//                            break;
//                        case 9:
//                            mName.setBackgroundResource(R.color.rgb5A1011);
//                            break;
//                    }
                }
            }
        }
    }
}
