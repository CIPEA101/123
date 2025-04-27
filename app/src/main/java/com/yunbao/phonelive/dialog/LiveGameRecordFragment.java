package com.yunbao.phonelive.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;


public class LiveGameRecordFragment extends AbsDialogFragment implements View.OnClickListener {

    private LinearLayout lotteryRecord;
    private LinearLayout tzRecord;
    private ImageView ivClose;

    private String mGameType ;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_game_record;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lotteryRecord = mRootView.findViewById(R.id.ll_game_lottery_record);
        tzRecord = mRootView.findViewById(R.id.ll_game_tz_record);
        ivClose = mRootView.findViewById(R.id.iv_live_game_record);

        lotteryRecord.setOnClickListener(this);
        tzRecord.setOnClickListener(this);
        ivClose.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mGameType = bundle.getString(Constants.LIVE_GAME_TYPE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_game_lottery_record://开奖记录
                dismiss();
                LiveGameTzRecordFragment tzfragment = new LiveGameTzRecordFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.LIVE_GAME_TYPE, TextUtils.isEmpty(mGameType) ? "1" : mGameType);
                tzfragment.setArguments(bundle);
                tzfragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), LiveGameTzRecordFragment.class.getSimpleName());
                break;
            case R.id.ll_game_tz_record://投注记录
                dismiss();
                LiveGameAwardHistoryFragment fragment = new LiveGameAwardHistoryFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString(Constants.LIVE_GAME_TYPE, TextUtils.isEmpty(mGameType) ? "1" : mGameType);
                fragment.setArguments(bundle2);
                fragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), LiveGameAwardHistoryFragment.class.getSimpleName());
                break;
            case R.id.iv_live_game_record:
                dismiss();
                break;
        }
    }

}