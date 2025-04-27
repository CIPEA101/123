package com.yunbao.phonelive.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/10/9.
 */

public abstract class AbsLiveViewHolder extends AbsViewHolder implements View.OnClickListener {

    private TextView mRedPoint;

    public AbsLiveViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void init() {
        findViewById(R.id.live_chat_tv).setOnClickListener(this);
        findViewById(R.id.btn_chat).setOnClickListener(this);
        findViewById(R.id.btn_msg).setOnClickListener(this);
        mRedPoint = (TextView) findViewById(R.id.red_point);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_msg) {
            ((LiveActivity) mContext).openChatListWindow();
        } else if (id == R.id.live_chat_tv || id == R.id.btn_chat) {
            ((LiveActivity) mContext).openChatWindow();
        }
    }

    public void setUnReadCount(String unReadCount) {
        if (mRedPoint != null) {
            if ("0".equals(unReadCount)) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
            }
            mRedPoint.setText(unReadCount);
        }
    }


}
