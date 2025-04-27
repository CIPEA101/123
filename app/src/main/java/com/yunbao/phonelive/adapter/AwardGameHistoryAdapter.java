package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.TzDetailActivity;
import com.yunbao.phonelive.game.TzListBean;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.utils.DateFormatUtil;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;

public class AwardGameHistoryAdapter extends RecyclerView.Adapter<AwardGameHistoryAdapter.Vh> {

    private List<TzListBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private int type;//2 全部 0未开奖 1中奖
    public AwardGameHistoryAdapter(Context context, LayoutInflater inflater, List<TzListBean> list,int type) {
        this.mContext = context;
        this.mInflater = inflater;
        this.mList = list;
        this.type = type;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }

    public void addList(List<TzListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }
    public List<TzListBean>getData(){
        return mList;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_game_award_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position),position);
    }

    @Override
    public int getItemCount() {
        if (mList == null || mList.size() == 0) {
            return 0;
        }
        return mList.size();
    }

    public void release() {
        if (mList != null) {
            mList.clear();
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTime;
        TextView mName;
        TextView mTotal;
        ImageView ivChange;
        ImageView ivEnter;
        TextView mTimeDes;
        CountdownView countdownView;
        View mTimeLay ;

        public Vh(View itemView) {
            super(itemView);
            mTime = itemView.findViewById(R.id.history_time);
            mName = itemView.findViewById(R.id.history_game);
            mTotal = itemView.findViewById(R.id.history_total);
            ivChange = itemView.findViewById(R.id.iv_change);
            ivEnter = itemView.findViewById(R.id.iv_enter);
            countdownView=itemView.findViewById(R.id.cv_countdownView);
            mTimeLay =itemView.findViewById(R.id.game_award_his_time_lay);
            mTimeDes =itemView.findViewById(R.id.tv_count_des);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("gameId", mList.get(getAdapterPosition()).getGame_id());
                    intent.putExtra("id", mList.get(getAdapterPosition()).getId());
                    intent.setClass(mContext, TzDetailActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

        void setData(final TzListBean bean,final int position ) {

            mTime.setText(DateFormatUtil.dateTimeToString(bean.getDatetime()));
            String str = "";
            switch (bean.getGame_id()) {
                case "1":
                    str = HttpConsts.GAME_NAME_YFKS;
                    break;
                case "2":
                    str = HttpConsts.GAME_NAME_YF115;
                    break;
                case "3":
                    str = HttpConsts.GAME_NAME_YFSC;
                    break;
                case "4":
                    str = HttpConsts.GAME_NAME_YFSSC;
                    break;
                case "5":
                    str = HttpConsts.GAME_NAME_YFLHC;
                    break;
                case "6":
                    str = HttpConsts.GAME_NAME_YFKLSF;
                    break;
                case "7":
                    str = HttpConsts.GAME_NAME_YFXYNC;
                    break;
            }
            mName.setText(str);
            mTotal.setText(bean.getTotalMoney());

            if(bean.isTzKj()){
                ivChange.setVisibility(View.VISIBLE);
                mTimeLay.setVisibility(View.GONE);

                if(bean.isZhongjiang()){
                    ivChange.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.yzj_ico));
                }else{
                    ivChange.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.wzj_ico));
                }
            }else{
                //未开奖
                ivChange.setVisibility(View.GONE);
                mTimeLay.setVisibility(View.VISIBLE);

                ivChange.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.wkj_ico));
                if(bean.getEndTime()<=0){
                    countdownView.setVisibility(View.GONE);
                    mTimeDes.setText("开奖中");
                }else {
                    //使用返回值
                    mTimeDes.setText("封盘：");
                    countdownView.setVisibility(View.VISIBLE);
                    refreshTime(bean.getEndTime()-System.currentTimeMillis(),bean,position);

                }

            }
        }
        public void refreshTime(long leftTime,final TzListBean bean,final int position) {
            if (leftTime > 0) {
                countdownView.start(leftTime);
            } else {
                countdownView.stop();
                countdownView.allShowZero();
                countdownView.setVisibility(View.GONE);
                mTimeDes.setText("开奖中");
            }
            countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                @Override
                public void onEnd(CountdownView cv) {
//                        ToastUtil.show("倒计时结束");
                    //刷新界面
                    bean.setTime(0);
                    bean.setEndTime(0);
                    notifyItemChanged(position);
                }
            });
        }
        public CountdownView getCvCountdownView() {
            return countdownView;
        }
    }
    /**
     * 以下两个接口代替 activity.onStart() 和 activity.onStop(), 控制 timer 的开关
     */
    @Override
    public void onViewAttachedToWindow(Vh holder) {
        int pos = holder.getAdapterPosition();
//            Log.d("MyViewHolder", String.format("mCvCountdownView %s is attachedToWindow", pos));

        TzListBean itemInfo = mList.get(pos);
        if(!itemInfo.isTzKj()){
            holder.refreshTime(itemInfo.getEndTime() - System.currentTimeMillis(),itemInfo,pos);
        }
    }

    @Override
    public void onViewDetachedFromWindow(Vh holder) {
//            int pos = holder.getAdapterPosition();
//            Log.d("MyViewHolder", String.format("mCvCountdownView %s is detachedFromWindow", pos));

        holder.getCvCountdownView().stop();
    }

    /**
     * 自动刷新  暂时为用到
     */
    public interface AdapterRefresh{
        void onAdapterRefresh();
    }
}

