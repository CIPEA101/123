package com.yunbao.phonelive.dialog;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.speech.utils.LogUtil;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.activity.MyCoinActivity;
import com.yunbao.phonelive.adapter.LiveGiftCountAdapter;
import com.yunbao.phonelive.adapter.LiveGiftPagerAdapter;
import com.yunbao.phonelive.bean.LevelBean;
import com.yunbao.phonelive.bean.LiveGiftBean;
import com.yunbao.phonelive.bean.LiveGuardInfo;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.List;

/**
 * Created by cxf on 2018/10/12.
 * 送礼物的弹窗
 */

public class LiveGiftDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnItemClickListener<String>, LiveGiftPagerAdapter.ActionListener {

    private TextView mCoin;
    private RelativeLayout rl_recharge;
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private View mLoading;
    private View mArrow;
    private View mBtnSend;
    private View mBtnSendGroup;
    private View mBtnSendLian;
    private TextView mBtnChooseCount;
    private PopupWindow mGiftCountPopupWindow;//选择分组数量的popupWindow
    private Drawable mDrawable1;
    private Drawable mDrawable2;
    private LiveGiftPagerAdapter mLiveGiftPagerAdapter;
    private LiveGiftBean mLiveGiftBean;
    private static final String DEFAULT_COUNT = "1";
    private String mCount = DEFAULT_COUNT;
    private String mLiveUid;
    private String mStream;
    private Handler mHandler;
    private int mLianCountDownCount;//连送倒计时的数字
    private TextView mLianText;
    private static final int WHAT_LIAN = 100;
    private boolean mShowLianBtn;//是否显示了连送按钮
    private LiveGuardInfo mLiveGuardInfo;
    private ImageView iv_experience;//等级图片
    private ProgressBar pb_experience;//进度条
    private TextView tv_experience;//


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift;
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

    public void setLiveGuardInfo(LiveGuardInfo liveGuardInfo) {
        mLiveGuardInfo = liveGuardInfo;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_experience = mRootView.findViewById(R.id.tv_experience);
        pb_experience = mRootView.findViewById(R.id.pb_experience);
        iv_experience = mRootView.findViewById(R.id.iv_experience);
        mCoin = mRootView.findViewById(R.id.coin);
        rl_recharge = mRootView.findViewById(R.id.rl_recharge);
        mLoading = mRootView.findViewById(R.id.loading);
        mArrow = mRootView.findViewById(R.id.arrow);
        mBtnSend = mRootView.findViewById(R.id.btn_send);
        mBtnSendGroup = mRootView.findViewById(R.id.btn_send_group);
        mBtnSendLian = mRootView.findViewById(R.id.btn_send_lian);
        mBtnChooseCount =  mRootView.findViewById(R.id.btn_choose);
        mLianText =  mRootView.findViewById(R.id.lian_text);
        mDrawable1 = ContextCompat.getDrawable(mContext, R.drawable.bg_live_gift_send);
        mDrawable2 = ContextCompat.getDrawable(mContext, R.drawable.bg_live_gift_send_2);
        mViewPager = mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mRadioGroup != null) {
                    ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup = mRootView.findViewById(R.id.radio_group);
        mBtnSend.setOnClickListener(this);
        mBtnSendLian.setOnClickListener(this);
        mBtnChooseCount.setOnClickListener(this);
        mCoin.setOnClickListener(this);
        rl_recharge.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mLianCountDownCount--;
                if (mLianCountDownCount == 0) {
                    hideLianBtn();
                } else {
                    if (mLianText != null) {
                        mLianText.setText(mLianCountDownCount + "s");
                        if (mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
                        }
                    }
                }
            }
        };
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLiveUid = bundle.getString(Constants.LIVE_UID);
            mStream = bundle.getString(Constants.LIVE_STREAM);
        }
        loadData();
        //获取等级
        getLevel();
    }

    /**
     * 等级池
     */
    private void getLevel(){
        HttpUtil.getGrade(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    /**
                     * 	"level": "1",用户等级
                     * 			"consumption": "17",用户经验值
                     * 			"last_level_up": "0",下限
                     * 			"level_up": "100",当前等级上限值
                     * 			"levelname": "一级",等级名称
                     * 			"next_level": "2",
                     * 			"next_levelname": "二级"
                     */
                    JSONObject obj = JSON.parseObject(info[0]);
                    if(obj==null)return;
                    String level=obj.getString("level");
                    if(StringUtil.notEmpty(level)){
                        //加载等级图片
                        LevelBean levelBean = AppConfig.getInstance().getLevel(Integer.parseInt(level));
                        if (levelBean != null) {
                            ImgLoader.display(levelBean.getThumb(), iv_experience);
                        }
                    }
                    double consumption=Double.parseDouble(obj.getString("consumption"));
                    double last_level_up=Double.parseDouble(obj.getString("last_level_up"));
                    double level_up=Double.parseDouble(obj.getString("level_up"));
                    double current=consumption-last_level_up;
                    double all=level_up-last_level_up;
                    int progress= (int) ((current/all)*100);
                    pb_experience.setProgress(progress);
                    tv_experience.setText("需要"+(level_up-consumption)+"升级");
                }
            }
        });
    }
    private void loadData() {
        List<LiveGiftBean> giftList = AppConfig.getInstance().getGiftList();
        if (giftList == null) {
            HttpUtil.getGiftList(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        List<LiveGiftBean> list = JSON.parseArray(obj.getString("giftlist"), LiveGiftBean.class);
                        AppConfig.getInstance().setGiftList(list);
                        showGiftList(list);
                        mCoin.setText(obj.getString("coin"));
                    }
                }

                @Override
                public void onFinish() {
                    if (mLoading != null) {
                        mLoading.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } else {
            for (LiveGiftBean bean : giftList) {
                bean.setChecked(false);
            }
            mLoading.setVisibility(View.INVISIBLE);
            showGiftList(giftList);
            HttpUtil.getCoin(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                        mCoin.setText(JSONObject.parseObject(info[0]).getString("coin"));
                    }
                }
            });
        }
    }

    private void showGiftList(List<LiveGiftBean> list) {
        mLiveGiftPagerAdapter = new LiveGiftPagerAdapter(mContext, list);
        mLiveGiftPagerAdapter.setActionListener(this);
        mViewPager.setAdapter(mLiveGiftPagerAdapter);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = mLiveGiftPagerAdapter.getCount(); i < size; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_gift_indicator, mRadioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            mRadioGroup.addView(radioButton);
        }
    }
    @Override
    public void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
        HttpUtil.cancel(HttpConsts.GET_GIFT_LIST);
        HttpUtil.cancel(HttpConsts.GET_COIN);
        HttpUtil.cancel(HttpConsts.SEND_GIFT);
        if (mLiveGiftPagerAdapter != null) {
            mLiveGiftPagerAdapter.release();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
            case R.id.btn_send_lian:
                //一秒一次
                if(!ClickUtil.canClick()){
                    return;
                }
                    sendGift();
                break;
            case R.id.btn_choose:
                showGiftCount();
                break;
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.rl_recharge:
            case R.id.coin:
                forwardMyCoin();
                break;
        }
    }

    /**
     * 跳转到我的钻石
     */
    private void forwardMyCoin() {
        dismiss();
        MyCoinActivity.forward(mContext);
    }

    /**
     * 显示分组数量
     */
    private void showGiftCount() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.view_gift_count, null);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        LiveGiftCountAdapter adapter = new LiveGiftCountAdapter(mContext);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mGiftCountPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mGiftCountPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mGiftCountPopupWindow.setOutsideTouchable(true);
        mGiftCountPopupWindow.showAtLocation(mBtnChooseCount, Gravity.BOTTOM | Gravity.RIGHT, DpUtil.dp2px(70), DpUtil.dp2px(40));
    }

    /**
     * 隐藏分组数量
     */
    private void hideGiftCount() {
        if (mGiftCountPopupWindow != null) {
            mGiftCountPopupWindow.dismiss();
        }
    }


    @Override
    public void onItemClick(String bean, int position) {
        mCount = bean;
        mBtnChooseCount.setText(bean);
        hideGiftCount();
    }

    @Override
    public void onItemChecked(LiveGiftBean bean) {
        mLiveGiftBean = bean;
        hideLianBtn();
        mBtnSend.setEnabled(true);
        if (!DEFAULT_COUNT.equals(mCount)) {
            mCount = DEFAULT_COUNT;
            mBtnChooseCount.setText(DEFAULT_COUNT);
        }
        if (bean.getType() == LiveGiftBean.TYPE_DELUXE) {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() == View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.INVISIBLE);
                mArrow.setVisibility(View.INVISIBLE);
                mBtnSend.setBackground(mDrawable2);
            }
        } else {
            if (mBtnChooseCount != null && mBtnChooseCount.getVisibility() != View.VISIBLE) {
                mBtnChooseCount.setVisibility(View.VISIBLE);
                mArrow.setVisibility(View.VISIBLE);
                mBtnSend.setBackground(mDrawable1);
            }
        }
    }

    /**
     * 赠送礼物
     */
    public void sendGift() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGiftBean == null) {
            return;
        }
        if (mLiveGuardInfo != null) {
            if (mLiveGiftBean.getMark() == LiveGiftBean.MARK_GUARD && mLiveGuardInfo.getMyGuardType() != Constants.GUARD_TYPE_YEAR) {
                ToastUtil.show(R.string.guard_gift_tip);
                return;
            }
        }
        SendGiftCallback callback = new SendGiftCallback(mLiveGiftBean);
        HttpUtil.sendGift(mLiveUid, mStream, mLiveGiftBean.getId(), mCount, callback);
    }

    /**
     * 隐藏连送按钮
     */
    private void hideLianBtn() {
        mShowLianBtn = false;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() == View.VISIBLE) {
            mBtnSendLian.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() != View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示连送按钮
     */
    private void showLianBtn() {
        if (mLianText != null) {
            mLianText.setText("5s");
        }
        mLianCountDownCount = 5;
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_LIAN);
            mHandler.sendEmptyMessageDelayed(WHAT_LIAN, 1000);
        }
        if (mShowLianBtn) {
            return;
        }
        mShowLianBtn = true;
        if (mBtnSendGroup != null && mBtnSendGroup.getVisibility() == View.VISIBLE) {
            mBtnSendGroup.setVisibility(View.INVISIBLE);
        }
        if (mBtnSendLian != null && mBtnSendLian.getVisibility() != View.VISIBLE) {
            mBtnSendLian.setVisibility(View.VISIBLE);
        }
    }


    private class SendGiftCallback extends HttpCallback {

        private LiveGiftBean mGiftBean;

        public SendGiftCallback(LiveGiftBean giftBean) {
            mGiftBean = giftBean;
        }

        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0) {
                if (info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    UserBean u = AppConfig.getInstance().getUserBean();
                    if (u != null) {
                        u.setLevel(obj.getIntValue("level"));
                        u.setCoin(coin);
                    }
                    if (mCoin != null) {
                        mCoin.setText(coin);
                    }
                    if (mContext != null && mGiftBean != null) {
                        ((LiveActivity) mContext).onCoinChanged(coin);
                        ((LiveActivity) mContext).sendGiftMessage(mGiftBean, obj.getString("gifttoken"));
                    }
                    if (mLiveGiftBean.getType() == LiveGiftBean.TYPE_NORMAL) {
                        showLianBtn();
                    }
                    //查询经验等级
                    getLevel();
                }
            } else {
                hideLianBtn();
                LogUtil.e("我来了============="+"info=="+info+"    msg=="+msg);
                ToastUtil.show(msg);
            }
        }
    }


}
