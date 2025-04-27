package com.yunbao.phonelive.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.ViewPagerAdapter;
import com.yunbao.phonelive.bean.BonusBean;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.custom.TabButtonGroup;
import com.yunbao.phonelive.dialog.MainStartDialogFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.ImMessageUtil;
import com.yunbao.phonelive.im.ImPushUtil;
import com.yunbao.phonelive.im.ImUnReadCountEvent;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.MainAppBarLayoutListener;
import com.yunbao.phonelive.interfaces.MainStartChooseCallback;
import com.yunbao.phonelive.presenter.CheckLivePresenter;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.LiveStorge;
import com.yunbao.phonelive.utils.LocationUtil;
import com.yunbao.phonelive.utils.ProcessResultUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.VersionUtil;
import com.yunbao.phonelive.utils.VideoStorge;
import com.yunbao.phonelive.utils.WordUtil;
import com.yunbao.phonelive.views.AbsMainViewHolder;
import com.yunbao.phonelive.views.BonusViewHolder;
import com.yunbao.phonelive.views.MainCoinViewHolder;
import com.yunbao.phonelive.views.MainHomeViewHolder;
import com.yunbao.phonelive.views.MainHomeViewHolderNew;
import com.yunbao.phonelive.views.MainListViewHolder;
import com.yunbao.phonelive.views.MainListViewHolderNew;
import com.yunbao.phonelive.views.MainMeViewHolder;
import com.yunbao.phonelive.views.MainNiurenViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AbsActivity {

    private ViewGroup mRootView;
    private TabButtonGroup mTabButtonGroup;
    private ViewPager mViewPager;
    private AbsMainViewHolder[] mViewHolders;
    private View mBottom;
    private int mDp70;
    private ProcessResultUtil mProcessResultUtil;
    private CheckLivePresenter mCheckLivePresenter;
    private boolean mLoad;
    private long mLastClickBackTime;//上次点击back键的时间

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void main() {
        boolean showInvite = getIntent().getBooleanExtra(Constants.SHOW_INVITE, false);
        mRootView = findViewById(R.id.rootView);
        mTabButtonGroup = findViewById(R.id.tab_group);
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(5);
        mViewHolders = new AbsMainViewHolder[5];
        mViewHolders[0] = new MainHomeViewHolderNew(mContext, mViewPager);
        mViewHolders[1] = new MainCoinViewHolder(mContext, mViewPager);
        mViewHolders[2] = new MainListViewHolderNew(mContext, mViewPager);
        mViewHolders[3] = new MainNiurenViewHolder(mContext, mViewPager);
        mViewHolders[4] = new MainMeViewHolder(mContext, mViewPager);
        List<View> list = new ArrayList<>();
        MainAppBarLayoutListener appBarLayoutListener = new MainAppBarLayoutListener() {
            @Override
            public void onOffsetChanged(float rate) {
                float curY = mBottom.getTranslationY();
                float targetY = rate * mDp70;
                if (curY != targetY) {
                    mBottom.setTranslationY(targetY);
                }
            }
        };
        for (AbsMainViewHolder vh : mViewHolders) {
            vh.setAppBarLayoutListener(appBarLayoutListener);
            addAllLifeCycleListener(vh.getLifeCycleListenerList());
            list.add(vh.getContentView());
        }
        mViewPager.setAdapter(new ViewPagerAdapter(list));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0, length = mViewHolders.length; i < length; i++) {
                    if (i == position) {
                        mViewHolders[i].setShowed(true);
                    } else {
                        mViewHolders[i].setShowed(false);
                    }
                }
                mViewHolders[position].loadData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTabButtonGroup.setViewPager(mViewPager);
        mTabButtonGroup.setAnimEnable(false) ;

        mDp70 = DpUtil.dp2px(70);
        mBottom = findViewById(R.id.bottom);
        mProcessResultUtil = new ProcessResultUtil(this);
        EventBus.getDefault().register(this);
        //checkVersion();
        if (showInvite) {
            showInvitationCode();
        }
        requestBonus();
        loginIM();
        AppConfig.getInstance().setLaunched(true);
    }

    public void mainClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_start:
                showStartDialog();
                break;
            case R.id.btn_search:
                SearchActivity.forward(mContext);
                break;
            case R.id.btn_msg:
                ChatActivity.forward(mContext);
                break;
        }
    }

    private void showStartDialog() {
        MainStartDialogFragment dialogFragment = new MainStartDialogFragment();
        dialogFragment.setMainStartChooseCallback(mMainStartChooseCallback);
        dialogFragment.show(getSupportFragmentManager(), "MainStartDialogFragment");

//        mProcessResultUtil.requestPermissions(new String[]{
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO
//        }, mStartLiveRunnable);

    }

    private MainStartChooseCallback mMainStartChooseCallback = new MainStartChooseCallback() {
        @Override
        public void onLiveClick() {
            mProcessResultUtil.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, mStartLiveRunnable);
        }

        @Override
        public void onVideoClick() {
            mProcessResultUtil.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            }, mStartVideoRunnable);
        }
    };

    private Runnable mStartLiveRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(mContext, LiveAnchorActivity.class));
        }
    };


    private Runnable mStartVideoRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(mContext, VideoRecordActivity.class));
        }
    };

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean configBean) {
                if (configBean != null) {
                    if (configBean.getMaintainSwitch() == 1) {//开启维护
                        DialogUitl.showSimpleTipDialog(mContext, WordUtil.getString(R.string.main_maintain_notice), configBean.getMaintainTips());
                    }else if (!VersionUtil.isLatest(configBean.getVersion())) {
                        VersionUtil.showDialog(mContext, configBean.getUpdateDes(), configBean.getDownloadApkUrl());
                    }
                }
            }
        });
    }

    /**
     * 填写邀请码
     */
    private void showInvitationCode() {
        DialogUitl.showSimpleInputDialog(mContext, WordUtil.getString(R.string.main_input_invatation_code), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(final Dialog dialog, final String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.main_input_invatation_code);
                    return;
                }
                HttpUtil.setDistribut(content, new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                            dialog.dismiss();
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    /**
     * 签到奖励
     */
    private void requestBonus() {
        HttpUtil.requestBonus(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    if (obj.getIntValue("bonus_switch") == 0) {
                        return;
                    }
                    int day = obj.getIntValue("bonus_day");
                    if (day <= 0) {
                        return;
                    }
                    List<BonusBean> list = JSON.parseArray(obj.getString("bonus_list"), BonusBean.class);
                    BonusViewHolder bonusViewHolder = new BonusViewHolder(mContext, mRootView);
                    bonusViewHolder.setData(list, day, obj.getString("count_day"));
                    bonusViewHolder.show();
                }
            }
        });
    }

    /**
     * 登录IM
     */
    private void loginIM() {
        String uid = AppConfig.getInstance().getUid();
        ImMessageUtil.getInstance().loginJMessage(uid);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mLoad) {
            mLoad = true;
//            MainHomeViewHolder mvh = (MainHomeViewHolder) mViewHolders[0];
//            if (ImPushUtil.getInstance().isClickNotification()) {//MainActivity是点击通知打开的
//                ImPushUtil.getInstance().setClickNotification(false);
//                int notificationType = ImPushUtil.getInstance().getNotificationType();
//                switch (notificationType) {
//                    case Constants.JPUSH_TYPE_LIVE:
//                        mvh.setCurrentPage(0);
//                        break;
//                    case Constants.JPUSH_TYPE_MESSAGE:
//                        mvh.setCurrentPage(1);
//                        ChatActivity.forward(mContext);
//                        break;
//                }
//            } else {
////                mvh.setCurrentPage(2);
//                mvh.setCurrentPage(0);
//            }
            getLocation();
        }
    }

    /**
     * 获取所在位置
     */
    private void getLocation() {
        mProcessResultUtil.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, new Runnable() {
            @Override
            public void run() {
                LocationUtil.getInstance().startLocation();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mTabButtonGroup != null) {
            mTabButtonGroup.cancelAnim();
        }
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConsts.GET_CONFIG);
        HttpUtil.cancel(HttpConsts.REQUEST_BONUS);
        HttpUtil.cancel(HttpConsts.GET_BONUS);
        HttpUtil.cancel(HttpConsts.SET_DISTRIBUT);
        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }
        LocationUtil.getInstance().stopLocation();
        if (mProcessResultUtil != null) {
            mProcessResultUtil.release();
        }
        AppConfig.getInstance().setGiftList(null);
        AppConfig.getInstance().setLaunched(false);
        LiveStorge.getInstance().clear();
        VideoStorge.getInstance().clear();
        super.onDestroy();
    }

    public static void forward(Context context) {
        forward(context, false);
    }

    public static void forward(Context context, boolean showInvite) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.SHOW_INVITE, showInvite);
        context.startActivity(intent);
    }

    /**
     * 观看直播
     */
    public void watchLive(LiveBean liveBean, String key, int position) {
        if(liveBean != null && AppConfig.getInstance().getUid().equals(liveBean.getUid())){//主播自己的直播间
            ToastUtil.show("不能进入自己的直播间") ;
            return;
        }

        if (mCheckLivePresenter == null) {
            mCheckLivePresenter = new CheckLivePresenter(mContext);
        }
        mCheckLivePresenter.watchLive(liveBean, key, position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount)) {
//            ((MainHomeViewHolder) mViewHolders[0]).setUnReadCount(unReadCount);
//            ((MainNearViewHolder) mViewHolders[1]).setUnReadCount(unReadCount);
        }
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        super.onBackPressed();
    }
}
