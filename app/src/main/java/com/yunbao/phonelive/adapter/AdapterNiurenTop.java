package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.NiurenTopBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.utils.ScreenDimenUtil;

import java.util.List;

public class AdapterNiurenTop extends RecyclerView.Adapter<AdapterNiurenTop.AntHolder> {
    private Context mContext ;
    private List<NiurenTopBean> mDateList ;

    public AdapterNiurenTop(Context mContext, List<NiurenTopBean> mDateList) {
        this.mContext = mContext;
        this.mDateList = mDateList;
    }

    @NonNull
    @Override
    public AntHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AntHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_niuren_header_top,parent,false)) ;
    }

    @Override
    public void onBindViewHolder(@NonNull AntHolder holder, int position) {
        NiurenTopBean info = mDateList.get(position) ;

        holder.mMainLay.setBackgroundColor(mContext.getResources().getColor(info.getBgColor())) ;
        holder.mValueLay.setBackgroundColor(mContext.getResources().getColor(info.getValueColor()));

        holder.mTypeNameTv.setText(info.getTypeName());
        holder.mUserNameTv.setText(info.getUserName());
        holder.mValueTv.setText(info.getValueName()) ;

        String avatar = info.getUserHead() ;
        if(TextUtils.isEmpty(avatar)){
            int defResId ;
            if(0 == position){
                defResId = R.mipmap.ic_nr_top_def_1 ;
            }else if(1 == position){
                defResId = R.mipmap.ic_nr_top_def_2 ;
            }else{
                defResId = R.mipmap.ic_nr_top_def_3 ;
            }
            holder.mUserIv.setImageDrawable(mContext.getResources().getDrawable(defResId));
        }else{
            ImgLoader.displayAvatar(info.getUserHead(),holder.mUserIv) ;
        }

    }

    @Override
    public int getItemCount() {
        return mDateList.size() ;
    }

    static class AntHolder extends RecyclerView.ViewHolder{
        private View mMainLay ;
        private ImageView mUserIv ;
        private TextView mTypeNameTv ;
        private TextView mUserNameTv ;
        private View mValueLay ;
        private TextView mValueTv ;

        public AntHolder(View itemView) {
            super(itemView);

            mMainLay = itemView.findViewById(R.id.adapter_niuren_header_top_main_lay) ;
            mUserIv = itemView.findViewById(R.id.adapter_niuren_header_top_iv) ;
            mTypeNameTv = itemView.findViewById(R.id.adapter_niuren_header_top_type_tv) ;
            mUserNameTv = itemView.findViewById(R.id.adapter_niuren_header_name_tv) ;
            mValueLay = itemView.findViewById(R.id.adapter_niuren_header_value_lay) ;
            mValueTv = itemView.findViewById(R.id.adapter_niuren_header_value_tv) ;

            int widHei = (int) (ScreenDimenUtil.getInstance().getScreenWdith() / 5F);
            mUserIv.getLayoutParams().width = widHei ;
            mUserIv.getLayoutParams().height = widHei ;



        }
    }
}
