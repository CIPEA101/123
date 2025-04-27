package com.yunbao.phonelive.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.LifeCycleAdapter;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveEndViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ImageView mAvatar1;
    private ImageView mAvatar2;
    private TextView mName;
    private TextView mDuration;//直播时长
    private TextView mVotes;//收获映票
    private TextView mWatchNum;//观看人数

    public LiveEndViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_end;
    }

    @Override
    public void init() {
        mAvatar1 = (ImageView) findViewById(R.id.avatar_1);
        mAvatar2 = (ImageView) findViewById(R.id.avatar_2);
        mName = (TextView) findViewById(R.id.name);
        mDuration = (TextView) findViewById(R.id.duration);
        mVotes = (TextView) findViewById(R.id.votes);
        mWatchNum = (TextView) findViewById(R.id.watch_num);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConsts.GET_LIVE_END_INFO);
            }
        };
        TextView votesName = (TextView) findViewById(R.id.votes_name);
        votesName.setText(WordUtil.getString(R.string.live_votes));
//        votesName.setText(WordUtil.getString(R.string.live_votes) + AppConfig.getInstance().getVotesName());
    }

    public void showData(LiveBean liveBean, String stream) {
        HttpUtil.getLiveEndInfo(stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mVotes.setText(StringUtil.toWan(obj.getLongValue("votes")));
                    mDuration.setText(obj.getString("length"));
                    mWatchNum.setText(StringUtil.toWan(obj.getLongValue("nums")));
                }
            }
        });
        if (liveBean != null) {
            mName.setText(liveBean.getUserNiceName());
            ImgLoader.displayBlur(liveBean.getAvatar(), mAvatar1);
            ImgLoader.displayAvatar(liveBean.getAvatar(), mAvatar2);
        }

    }

    @Override
    public void onClick(View v) {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).superBackPressed();
        } else if (mContext instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) mContext).exitLiveRoom();
        }
    }

}
