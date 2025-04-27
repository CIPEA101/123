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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.game.GameBean;
import com.yunbao.phonelive.utils.SpUtil;

import java.util.List;

public class ChipOptionAdapter extends RecyclerView.Adapter<ChipOptionAdapter.Vh>  {

    private List<GameBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private View.OnClickListener mCustomItemListener ;

    public ChipOptionAdapter(Context context, LayoutInflater inflater, List<GameBean> list,View.OnClickListener listener) {
        this.mContext = context;
        mCustomItemListener = listener;
        mInflater = inflater;
        mList = list;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }

    public void setList(List<GameBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_chip_option, null, false),mCustomItemListener);
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
        LinearLayout llChipOption;
        ImageView ivChipUrl;
        TextView tvChipCrr;
        TextView tvChipCustom;

        public Vh(View itemView,final View.OnClickListener listener) {
            super(itemView);
            llChipOption =  itemView.findViewById(R.id.chip_option_ll);
            ivChipUrl = itemView.findViewById(R.id.chip_option_uri);
            tvChipCrr = itemView.findViewById(R.id.chip_option_current);
            tvChipCustom = itemView.findViewById(R.id.chip_option_custom_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() == mList.size() - 1){//最后一个表示自定义筹码
                        for (int i = 0; i < mList.size();i ++){
                            mList.get(i).setChecked(false);
                        }
                        mList.get(getAdapterPosition()).setChecked(true) ;
                        notifyDataSetChanged();

                        if(listener != null){
                            listener.onClick(view) ;
                        }
                    }else{
                        if (tvChipCrr.getVisibility() == View.INVISIBLE){
                            for (int i = 0; i < mList.size();i ++){
                                if (i == getAdapterPosition()){
                                    mList.get(i).setChecked(true);

                                    SpUtil.getInstance().setBooleanValue("iskCus",false);
                                    SpUtil.getInstance().setStringValue("iskTv",String.valueOf(mList.get(i).getValue()));
                                    SpUtil.getInstance().setStringValue("iskIv",String.valueOf(mList.get(i).getImageId()));
                                }else{
                                    mList.get(i).setChecked(false);
                                }
                            }
                        }
                        notifyDataSetChanged();
                    }
                }
            });
        }

        void setData(GameBean bean) {
            boolean isCustom = bean.isCustom() ;
            boolean isChecked = bean.isChecked() ;

            tvChipCrr.setVisibility(isCustom || isChecked ? View.VISIBLE : View.INVISIBLE) ;
            tvChipCustom.setVisibility(isCustom ? View.VISIBLE : View.GONE);
            ivChipUrl.setVisibility(isCustom ? View.GONE : View.VISIBLE);

            int customValue = bean.getCustomValue() ;
            tvChipCustom.setText(customValue > 0 ? String.valueOf(customValue) : "自定义") ;

            if(isCustom){
                if(bean.getCustomValue() > 0){//有自定义的值
                    tvChipCrr.setText("修改筹码");
                }else{
                    tvChipCrr.setText("设置筹码");
                }
            }else{
                tvChipCrr.setText("当前筹码");
            }

            ivChipUrl.setBackgroundResource(bean.getImageId());
            if (bean.isChecked()){
                llChipOption.setBackgroundColor(mContext.getResources().getColor(R.color.rgb130c2c));
            }else{
                llChipOption.setBackgroundColor(mContext.getResources().getColor(R.color.rgb4C4842));
            }
        }
    }
}
