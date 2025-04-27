package com.yunbao.phonelive.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ArticialBean;
import com.yunbao.phonelive.utils.IntentUtils;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.List;

/**
 * 人工充值
 */

public class RechargeArtificialAdapter extends RecyclerView.Adapter<RechargeArtificialAdapter.Vh>{
    private Context mContext;
    private List<ArticialBean> mList;

    public RechargeArtificialAdapter(Context context, List<ArticialBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(LayoutInflater.from(mContext).inflate(R.layout.item_recharge_artificial, parent, false));
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

        TextView tv_item_name;
        TextView tv_item_money;
        RelativeLayout rl_qq;
        RelativeLayout rl_weixin;


        public Vh(View itemView) {
            super(itemView);

            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_money = itemView.findViewById(R.id.tv_item_money);
            rl_qq = itemView.findViewById(R.id.rl_qq);
            rl_weixin = itemView.findViewById(R.id.rl_weixin);

        }

        void setData(final ArticialBean bean) {
            tv_item_name.setText(StringUtil.empty(bean.name)?"":bean.name);
            tv_item_money.setText(StringUtil.empty(bean.demo)?"":bean.demo);
            rl_qq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(TextUtils.isEmpty(bean.qq)){
                        ToastUtil.show("暂无QQ号，请选择其他方式或者联系客服") ;
                        return;
                    }

                    if(!IntentUtils.isQQClientAvailable(mContext)){
                        ToastUtil.show("请先安装QQ") ;
                        return;
                    }
                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("text",bean.qq );
                    cm.setPrimaryClip(clipData);
                    IntentUtils.intentToQQWithChat(mContext,bean.qq);
                }
            });
            rl_weixin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(TextUtils.isEmpty(bean.wechat)){
                        ToastUtil.show("暂无微信号，请选择其他方式或者联系客服") ;
                        return;
                    }

                    ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("text",bean.wechat );
                    cm.setPrimaryClip(clipData);

                    ToastUtil.show("微信号已复制，请打开微信搜索该微信号");

                    if(!IntentUtils.intentToWechatWithChat(mContext,bean.wechat)){
                        ToastUtil.show("请先安装微信") ;
                        return;
                    }
                }
            });
        }
    }

}
