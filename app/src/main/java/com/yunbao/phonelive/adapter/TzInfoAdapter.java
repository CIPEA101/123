package com.yunbao.phonelive.adapter;

import android.content.Context;
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
import com.yunbao.phonelive.game.TzInfoBean;
import com.yunbao.phonelive.http.HttpConsts;

import java.util.List;

public class TzInfoAdapter extends RecyclerView.Adapter<TzInfoAdapter.Vh> {

    private List<TzInfoBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private OnItemClick onItemClick;
    private String gameName;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public TzInfoAdapter(Context context, LayoutInflater inflater, List<TzInfoBean> list,String gameName) {
        this.mContext = context;
        this.mInflater = inflater;
        this.mList = list;
        this.gameName = gameName;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }

    public void setList(List<TzInfoBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_tz_choose, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, final int position, @NonNull List<Object> payloads) {
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

        TextView tzOption;
        ImageView delete;
        TextView tvName;
        TextView tvInject;
        TextView tvBs;
        TextView tzDou;

        public Vh(View itemView) {
            super(itemView);
            tzOption = itemView.findViewById(R.id.tv_tz_option);
            delete = itemView.findViewById(R.id.tv_cash_delete);
            tvName = itemView.findViewById(R.id.tv_tz_name);
            tvInject = itemView.findViewById(R.id.tv_tz_inject);
            tvBs = itemView.findViewById(R.id.tv_bs);
            tzDou = itemView.findViewById(R.id.tv_double);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onClickCancel(getAdapterPosition());
                }
            });
        }

        void setData(TzInfoBean bean) {
            tzOption.setText(bean.getCode());
            String strWf = "";

            switch (gameName) {
                case HttpConsts.GAME_NAME_YFKS:
                    switch (bean.getType()) {
                        case "1":
                            strWf = "总和";
                            break;
                        case "2":
                            strWf = "三军";
                            break;
                        case "3":
                            strWf = "短牌";
                            break;
                    }
                    break;
                case HttpConsts.GAME_NAME_YF115:
                    switch (bean.getType()) {
                        case "1":
                            strWf = "总和";
                            break;
                        case "2":
                            strWf = "第一球两面";
                            break;
                        case "3":
                            strWf = "全五中一";
                            break;
                    }
                    break;
                case HttpConsts.GAME_NAME_YFSC:
                    switch (bean.getType()) {
                        case "1":
                            strWf = "冠军单码";
                            break;
                        case "2":
                            strWf = "冠亚和";
                            break;
                        case "3":
                            strWf = "冠军两面";
                            break;
                    }
                    break;
                case HttpConsts.GAME_NAME_YFSSC:
                    switch (bean.getType()) {
                        case "1":
                            strWf = "第一球两面";
                            break;
                        case "2":
                            strWf = "第一球VS第五球";
                            break;
                        case "3":
                            strWf = "全5中1";
                            break;
                    }
                    break;
                case HttpConsts.GAME_NAME_YFLHC:
                    switch (bean.getType()) {
                        case "1":
                            strWf = "特码两面";
                            break;
                        case "2":
                            strWf = "特码生肖";
                            break;
                        case "3":
                            strWf = "特码色波";
                            break;
                    }
                    break;
                case HttpConsts.GAME_NAME_YFKLSF:
                case HttpConsts.GAME_NAME_YFXYNC:
                    switch (bean.getType()) {
                        case "1":
                            strWf = "总和";
                            break;
                        case "2":
                            strWf = "第一球两面";
                            break;
                        case "3":
                            strWf = "第八球两面";
                            break;
                    }
                    break;
            }

            tvName.setText(gameName+"-"+strWf);
            tvInject.setText("1");
            tvBs.setText(bean.getMagnification());
            tzDou.setText(bean.getTotalcoin() + "×" + bean.getMagnification());

        }
    }


    public interface OnItemClick {
        void onClickCancel(int postion);
    }

}

