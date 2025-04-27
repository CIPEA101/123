package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.RechargeChooseMoneyBean;
import com.yunbao.phonelive.interfaces.OnRvClickListener;

import java.util.List;

public class RechargeChooseMoneyAdapter extends RecyclerView.Adapter<RechargeChooseMoneyAdapter.Vh>  {
    private Context mContext;
    private List<RechargeChooseMoneyBean> mList;
    private OnRvClickListener mClickListener ;

    public RechargeChooseMoneyAdapter(Context context, List<RechargeChooseMoneyBean> list, OnRvClickListener clickListener) {
        this.mContext = context;
        this.mList = list;
        this.mClickListener = clickListener ;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.adapter_recharge_choose_money, parent, false),mClickListener);
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


        public Vh(View itemView, final OnRvClickListener clickListener) {
            super(itemView);

            mNum =  itemView.findViewById(R.id.tv_option_num);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        clickListener.onClick(0,getAdapterPosition()) ;
                    }
                }
            });
        }

        void setData(RechargeChooseMoneyBean bean) {
            mNum.setText(bean.money);
            mNum.setBackground(mContext.getResources().getDrawable(bean.isChoose ? R.drawable.bg_recharge_choose_money_choose : R.drawable.bg_recharge_choose_money));
            mNum.setTextColor(mContext.getResources().getColor(bean.isChoose?R.color.white:R.color.black));
        }
    }


}
