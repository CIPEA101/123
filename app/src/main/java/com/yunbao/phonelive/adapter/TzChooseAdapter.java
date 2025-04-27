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
import com.yunbao.phonelive.game.TzChooseBean;
import com.yunbao.phonelive.utils.SpUtil;

import java.util.List;

public class TzChooseAdapter extends RecyclerView.Adapter<TzChooseAdapter.Vh>  {

    private  List<TzChooseBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private OnItemClick onItemClick;

    public OnItemClick getOnItemClick() {
        return onItemClick;
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public TzChooseAdapter(Context context, LayoutInflater inflater, List<TzChooseBean> list) {
        this.mContext = context;
        mInflater = inflater;
        mList = list;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }

    public void setList(List<TzChooseBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_tz_choose, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) { }

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
            tzOption =  itemView.findViewById(R.id.tv_tz_option);
            delete = itemView.findViewById(R.id.tv_cash_delete);
            tvName =  itemView.findViewById(R.id.tv_tz_name);
            tvInject = itemView.findViewById(R.id.tv_tz_inject);
            tvBs = itemView.findViewById(R.id.tv_bs);
            tzDou =  itemView.findViewById(R.id.tv_double);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onClickCancel(getAdapterPosition());
                }
            });
        }

        void setData(TzChooseBean bean) {
            tzOption.setText(bean.getItem().getName());
            tvName.setText(bean.getTzName());
            tvInject.setText(bean.getTzInject());
            tvInject.setText("1");
            String str = SpUtil.getInstance().getStringSValue("iskTv");
            tvBs.setText(bean.getTvDou());
            if (!str.equals("")){
                tzDou.setText(str+"×"+bean.getTvDou());
            }
        }
    }


    public interface OnItemClick{
        void onClickCancel(int postion);
    }

}
