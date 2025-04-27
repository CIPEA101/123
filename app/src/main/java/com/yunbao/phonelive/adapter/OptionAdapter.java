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
import android.widget.TextView;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.game.OptionBean;
import com.yunbao.phonelive.game.TzChooseBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.Vh>  {

    private List<OptionBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private String parentName;
    private String value;
    private ArrayList<TzChooseBean> tcbList = new ArrayList<>();

    public OptionAdapter(Context context, LayoutInflater inflater,String parentName,String value, List<OptionBean> list) {
        tcbList.clear();
        this.mContext = context;
        mInflater = inflater;
        this.parentName = parentName;
        this.value = value;
        mList = list;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }

    public void setList(List<OptionBean> mList) {
        tcbList.clear();
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_option, null, false));
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

        public Vh(View itemView) {
            super(itemView);
            mNum =  itemView.findViewById(R.id.tv_option_num);
            mName = itemView.findViewById(R.id.tv_option_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mNum.getCurrentTextColor() == mContext.getResources().getColor(R.color.white)){
                        mNum.setBackground(mContext.getResources().getDrawable(R.drawable.btn_circle_half_8_uncheck));
                        mNum.setTextColor(mContext.getResources().getColor(R.color.rgbDE5245));
                        if(tcbList != null && tcbList.size() > 0){
                            Iterator<TzChooseBean> tcbIter = tcbList.iterator();
                            while(tcbIter.hasNext()) {
                                TzChooseBean tcb = tcbIter.next();
                                if (tcb.getTzName().equals(parentName)&&tcb.getItem().getId().equals(mList.get(getAdapterPosition()).getId())){
                                    tcbIter.remove();
                                }
                            }
                        }
                    }else{
                        mNum.setBackground(mContext.getResources().getDrawable(R.drawable.btn_circle_half_8_checked));
                        mNum.setTextColor(mContext.getResources().getColor(R.color.white));
                        tcbList.add(new TzChooseBean(value,parentName, AppConfig.getInstance().getUid(),mList.get(getAdapterPosition())));
                    }
                }
            });
        }

        void setData(OptionBean bean) {
            mNum.setText(bean.getName());
            mName.setText(bean.getRatio());
        }
    }

    public ArrayList<TzChooseBean> getLists(){
        return tcbList;
    }
}
