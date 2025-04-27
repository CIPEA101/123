package com.yunbao.phonelive.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.opensource.svgaplayer.SVGAImageView;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.activity.LiveAnchorActivity;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.activity.MyCoinActivity;
import com.yunbao.phonelive.adapter.LiveChatAdapter;
import com.yunbao.phonelive.adapter.LiveUserAdapter;
import com.yunbao.phonelive.bean.LevelBean;
import com.yunbao.phonelive.bean.LiveBuyGuardMsgBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveDanMuBean;
import com.yunbao.phonelive.bean.LiveEnterRoomBean;
import com.yunbao.phonelive.bean.LiveReceiveGiftBean;
import com.yunbao.phonelive.bean.LiveUserGiftBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.custom.TopGradual;
import com.yunbao.phonelive.dialog.LiveUserDialogFragment;
import com.yunbao.phonelive.event.GameTimeEvent;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.LifeCycleAdapter;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.presenter.LiveDanmuPresenter;
import com.yunbao.phonelive.presenter.LiveEnterRoomAnimPresenter;
import com.yunbao.phonelive.presenter.LiveGameResultPresenter;
import com.yunbao.phonelive.presenter.LiveGiftAnimPresenter2;
import com.yunbao.phonelive.presenter.LiveLightAnimPresenter;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.RandomUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by cxf on 2018/10/9.
 * 直播间公共逻辑
 */

public class LiveRoomViewHolder extends AbsViewHolder implements View.OnClickListener {
    public static int sOffsetY = 0;
    private ViewGroup mRoot;
    private View mCloseIv ;
    private ImageView mAvatar;
    private ImageView mLevelAnchor;
    private TextView mName;
    private TextView mID;
    private View mBtnFollow;
    private TextView mVotesName;//映票名称
    private TextView mVotes;
    private TextView mGuardNum;//守护人数
    private ImageView mGame;
    private TextView mUserCountTv ;//当前用户数量

    private RecyclerView mUserRecyclerView;
    private RecyclerView mChatRecyclerView;
    private LiveUserAdapter mLiveUserAdapter;
    private LiveChatAdapter mLiveChatAdapter;
    private View mBtnRedPack;
    private String mLiveUid;
    private String mStream;
    private LiveLightAnimPresenter mLightAnimPresenter;
    private LiveEnterRoomAnimPresenter mLiveEnterRoomAnimPresenter;
    private LiveDanmuPresenter mLiveDanmuPresenter;
    private LiveGiftAnimPresenter2 mLiveGiftAnimPresenter;
    private LiveRoomHandler mLiveRoomHandler;
    private HttpCallback mRefreshUserListCallback;
    private HttpCallback mTimeChargeCallback;
    protected int mUserListInterval;//用户列表刷新时间的间隔
    private GifImageView mGifImageView;
    private SVGAImageView mSVGAImageView;
    private TextView mLiveTimeTextView;//主播的直播时长
    private long mAnchorLiveTime;//主播直播时间

    //游戏类型
    private ImageView mGameTypeIv ;
    private TextView mGameTimeTv ;
    //游戏开奖结果
    private LiveGameResultPresenter mGameResultPresenter ;
    private int mGameTimeSeconds ;
    private String[] mGameResultArr ;
    private String mGamePeriod ;
    private String mGameType ;
    private String mGameNewPeriod ;

    private String mGameId ;

    private View.OnClickListener mCloseListener ;
    public void setOnCloseListener(View.OnClickListener listener){
        mCloseListener = listener ;
    }

    public LiveRoomViewHolder(Context context, ViewGroup parentView, GifImageView gifImageView, SVGAImageView svgaImageView) {
        super(context, parentView);
        mGifImageView = gifImageView;
        mSVGAImageView = svgaImageView;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_room;
    }

    @Override
    public void init() {
        mRoot = (ViewGroup) findViewById(R.id.root);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mLevelAnchor = (ImageView) findViewById(R.id.level_anchor);
        mName = (TextView) findViewById(R.id.name);
        mID = (TextView) findViewById(R.id.id_val);
        mBtnFollow = findViewById(R.id.btn_follow);
        mVotesName = (TextView) findViewById(R.id.votes_name);
        mVotes = (TextView) findViewById(R.id.votes);
        mGuardNum = (TextView) findViewById(R.id.guard_num);
        mGame = (ImageView) findViewById(R.id.btn_red_game);

        mGameTypeIv = (ImageView) findViewById(R.id.view_live_game_iv);
        mGameTimeTv = (TextView) findViewById(R.id.view_live_game_tv);
        mGameTypeIv.setOnClickListener(this);

        RelativeLayout gameResultView = (RelativeLayout) findViewById(R.id.view_live_room_game_result_lay);
        mGameResultPresenter = new LiveGameResultPresenter(mContext,gameResultView) ;

        //用户头像列表
        mUserCountTv = (TextView) findViewById(R.id.user_count_tv);
        mUserRecyclerView = (RecyclerView) findViewById(R.id.user_recyclerView);
        mUserRecyclerView.setHasFixedSize(true);
        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLiveUserAdapter = new LiveUserAdapter(mContext);
        mLiveUserAdapter.setOnItemClickListener(new OnItemClickListener<UserBean>() {
            @Override
            public void onItemClick(UserBean bean, int position) {
                showUserDialog(bean.getId());
            }
        });
        mUserRecyclerView.setAdapter(mLiveUserAdapter);
        //聊天栏
        mChatRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mChatRecyclerView.addItemDecoration(new TopGradual());
        mLiveChatAdapter = new LiveChatAdapter(mContext);
        mLiveChatAdapter.setOnItemClickListener(new OnItemClickListener<LiveChatBean>() {
            @Override
            public void onItemClick(LiveChatBean bean, int position) {
                showUserDialog(bean.getId());
            }
        });
        mChatRecyclerView.setAdapter(mLiveChatAdapter);
        mVotesName.setText(AppConfig.getInstance().getVotesName());
        mBtnFollow.setOnClickListener(this);
        mAvatar.setOnClickListener(this);

        mCloseIv = findViewById(R.id.view_live_room_close_iv) ;
        mCloseIv.setOnClickListener(this);

        findViewById(R.id.btn_votes).setOnClickListener(this);
        findViewById(R.id.btn_guard).setOnClickListener(this);
        mBtnRedPack = findViewById(R.id.btn_red_pack);
        mBtnRedPack.setOnClickListener(this);
        if (mContext instanceof LiveAudienceActivity) {
            mRoot.setOnClickListener(this);
        } else {
            mLiveTimeTextView = (TextView) findViewById(R.id.live_time);
            mLiveTimeTextView.setVisibility(View.VISIBLE);
        }

        mGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContext instanceof LiveAudienceActivity){
                    ((LiveAudienceActivity)mContext).openGameWindow();
                }
            }
        });

        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConsts.GET_USER_LIST);
                HttpUtil.cancel(HttpConsts.TIME_CHARGE);
                HttpUtil.cancel(HttpConsts.SET_ATTENTION);
                L.e("LiveRoomViewHolder-------->onDestroy");
            }
        };
        mLightAnimPresenter = new LiveLightAnimPresenter(mContext, mParentView);
        mLiveEnterRoomAnimPresenter = new LiveEnterRoomAnimPresenter(mContext, mContentView);
        mLiveRoomHandler = new LiveRoomHandler(this);
        mRefreshUserListCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                try {
                    if (code == 0 && info.length > 0) {
                        if (mLiveUserAdapter != null) {
                            JSONObject obj = JSON.parseObject(info[0]);

                            int count = StringUtil.convertInt(obj.getString("nums")) ;
                            count += RandomUtil.nextInt(10) ;
                            mUserCountTv.setText(String.valueOf(count)) ;

                            List<LiveUserGiftBean> list = JSON.parseArray(obj.getString("userlist"), LiveUserGiftBean.class);
                            mLiveUserAdapter.refreshList(list);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mTimeChargeCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (mContext instanceof LiveAudienceActivity) {
                    final LiveAudienceActivity liveAudienceActivity = (LiveAudienceActivity) mContext;
                    if (code == 0) {
                        liveAudienceActivity.roomChargeUpdateVotes();
                    } else {
                        if (mLiveRoomHandler != null) {
                            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_TIME_CHARGE);
                        }
                        liveAudienceActivity.pausePlay();
                        if (code == 1008) {//余额不足
                            liveAudienceActivity.setCoinNotEnough(true);
                            DialogUitl.showSimpleDialog(mContext, WordUtil.getString(R.string.live_coin_not_enough), false,
                                    new DialogUitl.SimpleCallback2() {
                                        @Override
                                        public void onConfirmClick(Dialog dialog, String content) {
                                            MyCoinActivity.forward(mContext);
                                        }

                                        @Override
                                        public void onCancelClick() {
                                            liveAudienceActivity.exitLiveRoom();
                                        }
                                    });
                        }
                    }
                }
            }
        };
    }

    /**
     * 显示主播头像
     */
    public void setAvatar(String url) {
        if (mAvatar != null) {
            ImgLoader.displayAvatar(url, mAvatar);
        }
    }

    /**
     * 显示主播等级
     */
    public void setAnchorLevel(int anchorLevel) {
        if (mLevelAnchor != null) {
            LevelBean levelBean = AppConfig.getInstance().getAnchorLevel(anchorLevel);
            if (levelBean != null) {
                ImgLoader.display(levelBean.getThumbIcon(), mLevelAnchor);
            }
        }
    }

    /**
     * 显示用户名
     */
    public void setName(String name) {
        if (mName != null) {
            mName.setText(name);
        }
    }

    /**
     * 显示房间号
     */
    public void setRoomNum(String roomNum) {
        if (mID != null) {
            mID.setText(roomNum);
        }
    }

    /**
     * 显示是否关注
     */
    public void setAttention(int attention) {
        if (mBtnFollow != null) {
            if (attention == 0) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示刷新直播间用户列表
     */
    public void setUserList(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.refreshList(list);
        }
    }
    public void setGameId(String gameId){
        mGameId = gameId ;
    }
    /**
     * 显示主播映票数
     */
    public void setVotes(String votes) {
        if (mVotes != null) {
            mVotes.setText(votes);
        }
    }

    /**
     * 显示主播守护人数
     */
    public void setGuardNum(int guardNum) {
        if (mGuardNum != null) {
            if (guardNum > 0) {
                mGuardNum.setText(guardNum + WordUtil.getString(R.string.ren));
            } else {
                mGuardNum.setText(R.string.main_list_no_data);
            }
        }
    }

    public void setLiveInfo(String liveUid, String stream, int userListInterval) {
        mLiveUid = liveUid;
        mStream = stream;
        mUserListInterval = userListInterval;
    }

    public void setGameInfo(String gameType,int seconds,String[] result,String period,String newPeriod){
        mGameTimeSeconds = seconds ;
        mGameResultArr = result ;
        mGamePeriod = period ;
        mGameType = gameType ;
        mGameNewPeriod = newPeriod ;

        int imageId ;
        if("1".equals(gameType)){
            imageId = R.mipmap.icon_ks_game ;
        }else if("2".equals(gameType)){
            imageId = R.mipmap.icon_syxw_game ;
        }else if("3".equals(gameType)){
            imageId = R.mipmap.icon_syf_game ;
        }else if("4".equals(gameType)){
            imageId = R.mipmap.icon_ssc_game ;
        }else if("5".equals(gameType)){
            imageId = R.mipmap.icon_yfl_game ;
        }else if("6".equals(gameType)){
            imageId = R.mipmap.icon_ten_game ;
        }else if("7".equals(gameType)){
            imageId = R.mipmap.icon_xfnc_game ;
        }else{
            imageId = R.mipmap.icon_ks_game ;
        }

        mGameTypeIv.setBackgroundResource(imageId) ;
        updateGameTimeTv() ;

        if(mGameTimeSeconds == 60){//新结果
            mGameResultPresenter.showGameResult(gameType,result,period) ;
        }

        //要设计为 10s封盘，所以准备从50s开始倒计时

        mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_GAME_TIME);
        mLiveRoomHandler.sendEmptyMessage(LiveRoomHandler.WHAT_GAME_TIME) ;
    }

    public void refreshGameTime(){
        mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_GAME_TIME,1000) ;

        updateGameTimeTv() ;

        if(mGameTimeSeconds == 0){
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_GAME_TIME);
        }
        mGameTimeSeconds-- ;
    }

    private void updateGameTimeTv(){
        if(mGameTimeSeconds <= HttpConsts.GAME_STOP_IN_TIME){
            mGameTimeTv.setText("封盘中");
        }else{
            int showTime = mGameTimeSeconds - HttpConsts.GAME_STOP_IN_TIME ;
            if(showTime < 10){
                mGameTimeTv.setText("00:0" + showTime) ;
            }else{
                mGameTimeTv.setText("00:" + showTime) ;
            }
            //内部使用当前主播游戏倒计时
            GameTimeEvent event=new GameTimeEvent();
            event.mGameId=mGameId;
            event.time=showTime;
            EventBus.getDefault().post(event);
//            mGameTimeTv.setText("00:" + mGameTimeSeconds) ;
        }
    }

    /**
     * 守护信息发生变化
     */
    public void onGuardInfoChanged(LiveBuyGuardMsgBean bean) {
        setGuardNum(bean.getGuardNum());
        setVotes(bean.getVotes());
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.onGuardChanged(bean.getUid(), bean.getGuardType());
        }
    }

    /**
     * 设置红包按钮是否可见
     */
    public void setRedPackBtnVisible(boolean visible) {
//        if (mBtnRedPack != null) {
//            if (visible) {
//                if (mBtnRedPack.getVisibility() != View.VISIBLE) {
//                    mBtnRedPack.setVisibility(View.VISIBLE);
//                }
//            } else {
//                if (mBtnRedPack.getVisibility() == View.VISIBLE) {
//                    mBtnRedPack.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.avatar:
                showAnchorUserDialog();
                break;
            case R.id.btn_follow:
                follow();
                break;
            case R.id.btn_votes:
                openContributeWindow();
                break;
            case R.id.btn_guard:
                ((LiveActivity) mContext).openGuardListWindow();
                break;
            case R.id.root:
                light();
                break;
            case R.id.btn_red_pack:
                ((LiveActivity) mContext).openRedPackListWindow();
                break;
            case R.id.view_live_game_iv:
               if(mContext instanceof LiveAudienceActivity){
                   ((LiveAudienceActivity)mContext).openTouzhuWindow() ;
               }
                break;
            case R.id.view_live_room_close_iv:

                if(mCloseListener != null){
                    mCloseListener.onClick(mCloseIv);
                }

                break;
        }
    }

    /**
     * 关注主播
     */
    private void follow() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        HttpUtil.setAttention(Constants.FOLLOW_FROM_LIVE, mLiveUid, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                if (isAttention == 1) {
                    ((LiveActivity) mContext).sendSystemMessage(
                            AppConfig.getInstance().getUserBean().getUserNiceName() + WordUtil.getString(R.string.live_follow_anchor),0);
                }
            }
        });
    }

    /**
     * 用户进入房间，用户列表添加该用户
     */
    public void insertUser(LiveUserGiftBean bean) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertItem(bean);

            int count = StringUtil.convertInt(mUserCountTv.getText().toString()) ;
            count += RandomUtil.nextInt(10) ;
            mUserCountTv.setText(String.valueOf(count)) ;
        }
    }

    /**
     * 用户进入房间，添加僵尸粉
     */
    public void insertUser(List<LiveUserGiftBean> list) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.insertList(list);

            int count = StringUtil.convertInt(mUserCountTv.getText().toString()) ;
            count += RandomUtil.nextInt(10) ;
            mUserCountTv.setText(String.valueOf(count)) ;
        }
    }

    /**
     * 用户离开房间，用户列表删除该用户
     */
    public void removeUser(String uid) {
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.removeItem(uid);

            int userCount = StringUtil.convertInt(mUserCountTv.getText().toString()) ;
            int count = mLiveUserAdapter.getItemCount() ;
            int showCount = userCount ;

            if(userCount - count > 10){
                showCount -= RandomUtil.nextInt(10) ;
            }

            mUserCountTv.setText(String.valueOf(showCount)) ;
        }
    }

    /**
     * 刷新用户列表
     */
    private void refreshUserList() {
        if (!TextUtils.isEmpty(mLiveUid) && mRefreshUserListCallback != null && mLiveUserAdapter != null) {
            HttpUtil.cancel(HttpConsts.GET_USER_LIST);
            HttpUtil.getUserList(mLiveUid, mStream, mRefreshUserListCallback);

            if (mLiveRoomHandler != null) {
                mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_REFRESH_USER_LIST, mUserListInterval > 0 ? mUserListInterval : 60000);
            }
        }
    }

    /**
     * 开始刷新用户列表
     */
    public void startRefreshUserList() {
        refreshUserList() ;
    }

    /**
     * 请求计时收费的扣费接口
     */
    private void requestTimeCharge() {
        if (!TextUtils.isEmpty(mLiveUid) && mTimeChargeCallback != null) {
            HttpUtil.cancel(HttpConsts.TIME_CHARGE);
            HttpUtil.timeCharge(mLiveUid, mStream, mTimeChargeCallback);
            startRequestTimeCharge();
        }
    }

    /**
     * 开始请求计时收费的扣费接口
     */
    public void startRequestTimeCharge() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_TIME_CHARGE, 60*10*1000);
        }
    }


    /**
     * 添加聊天消息到聊天栏
     */
    public void insertChat(LiveChatBean bean) {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.insertItem(bean);
        }
    }

    /**
     * 播放飘心动画
     */
    public void playLightAnim() {
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.play();
        }
    }

    /**
     * 点亮
     */
    private void light() {
        ((LiveAudienceActivity) mContext).light();
    }


    /**
     * 键盘高度变化
     */
    public void onKeyBoardChanged(int visibleHeight, int keyBoardHeight) {
        if (mRoot != null) {
            if (keyBoardHeight == 0) {
                mRoot.setTranslationY(0);
                return;
            }
            if (sOffsetY == 0) {
                mRoot.setTranslationY(-keyBoardHeight);
                return;
            }
            if (sOffsetY > 0 && sOffsetY < keyBoardHeight) {
                mRoot.setTranslationY(sOffsetY - keyBoardHeight);
            }
        }
    }

    /**
     * 聊天栏滚到最底部
     */
    public void chatScrollToBottom() {
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.scrollToBottom();
        }
    }

    /**
     * 用户进入房间 金光一闪,坐骑动画
     */
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (bean == null) {
            return;
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.enterRoom(bean);
        }
    }

    /**
     * 显示弹幕
     */
    public void showDanmu(LiveDanMuBean bean) {
        if (mVotes != null) {
            mVotes.setText(bean.getVotes());
        }
        if (mLiveDanmuPresenter == null) {
            mLiveDanmuPresenter = new LiveDanmuPresenter(mContext, mParentView);
        }
        mLiveDanmuPresenter.showDanmu(bean);
    }

    /**
     * 显示主播的个人资料弹窗
     */
    private void showAnchorUserDialog() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        showUserDialog(mLiveUid);
    }

    /**
     * 显示个人资料弹窗
     */
    private void showUserDialog(String toUid) {
        if (!TextUtils.isEmpty(mLiveUid) && !TextUtils.isEmpty(toUid)) {
            LiveUserDialogFragment fragment = new LiveUserDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.LIVE_UID, mLiveUid);
            bundle.putString(Constants.TO_UID, toUid);
            fragment.setArguments(bundle);
            fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveUserDialogFragment");
        }
    }

    /**
     * 直播间贡献榜窗口
     */
    private void openContributeWindow() {
        ((LiveActivity) mContext).openContributeWindow();
    }


    /**
     * 显示礼物动画
     */
    public void showGiftMessage(LiveReceiveGiftBean bean) {
        mVotes.setText(bean.getVotes());
        if (mLiveGiftAnimPresenter == null) {
            mLiveGiftAnimPresenter = new LiveGiftAnimPresenter2(mContext, mContentView, mGifImageView, mSVGAImageView);
        }
        mLiveGiftAnimPresenter.showGiftAnim(bean);
    }

    /**
     * 增加主播映票数
     *
     * @param deltaVal 增加的映票数量
     */
    public void updateVotes(String deltaVal) {
        if (mVotes == null) {
            return;
        }
        String votesVal = mVotes.getText().toString().trim();
        if (TextUtils.isEmpty(votesVal)) {
            return;
        }
        try {
            double votes = Double.parseDouble(votesVal);
            double addVotes = Double.parseDouble(deltaVal);
            votes += addVotes;
            mVotes.setText(StringUtil.format(votes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewGroup getInnerContainer() {
        return (ViewGroup) findViewById(R.id.inner_container);
    }


    /**
     * 主播显示直播时间
     */
    private void showAnchorLiveTime() {
        if (mLiveTimeTextView != null) {
            mAnchorLiveTime += 1000;
            mLiveTimeTextView.setText(StringUtil.getDurationText(mAnchorLiveTime));
            startAnchorLiveTime();
        }
    }

    public void startAnchorLiveTime() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_LIVE_TIME, 1000);
        }
    }

    /**
     * 主播切后台，50秒后关闭直播
     */
    public void anchorPause() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.sendEmptyMessageDelayed(LiveRoomHandler.WHAT_ANCHOR_PAUSE, 50000);
        }
    }

    /**
     * 主播切后台后又回到前台
     */
    public void anchorResume() {
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeMessages(LiveRoomHandler.WHAT_ANCHOR_PAUSE);
        }
    }

    /**
     * 主播结束直播
     */
    private void anchorEndLive() {
        if (mContext instanceof LiveAnchorActivity) {
            ((LiveAnchorActivity) mContext).endLive();
        }
    }

    public void release() {
        HttpUtil.cancel(HttpConsts.GET_USER_LIST);
        HttpUtil.cancel(HttpConsts.TIME_CHARGE);
        HttpUtil.cancel(HttpConsts.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.release();
        }
        mLiveRoomHandler = null;
        if (mLightAnimPresenter != null) {
            mLightAnimPresenter.release();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.release();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.release();
        }
        mRefreshUserListCallback = null;
        mTimeChargeCallback = null;


    }

    public void clearData() {
        HttpUtil.cancel(HttpConsts.GET_USER_LIST);
        HttpUtil.cancel(HttpConsts.TIME_CHARGE);
        HttpUtil.cancel(HttpConsts.SET_ATTENTION);
        if (mLiveRoomHandler != null) {
            mLiveRoomHandler.removeCallbacksAndMessages(null);
        }
        if (mAvatar != null) {
            mAvatar.setImageDrawable(null);
        }
        if (mLevelAnchor != null) {
            mLevelAnchor.setImageDrawable(null);
        }
        if (mName != null) {
            mName.setText("");
        }
        if (mID != null) {
            mID.setText("");
        }
        if (mVotes != null) {
            mVotes.setText("");
        }
        if (mGuardNum != null) {
            mGuardNum.setText("");
        }
        if (mLiveUserAdapter != null) {
            mLiveUserAdapter.clear();
        }
        if (mLiveChatAdapter != null) {
            mLiveChatAdapter.clear();
        }
        if (mLiveEnterRoomAnimPresenter != null) {
            mLiveEnterRoomAnimPresenter.cancelAnim();
            mLiveEnterRoomAnimPresenter.resetAnimView();
        }
        if (mLiveDanmuPresenter != null) {
            mLiveDanmuPresenter.release();
            mLiveDanmuPresenter.reset();
        }
        if (mLiveGiftAnimPresenter != null) {
            mLiveGiftAnimPresenter.cancelAllAnim();
        }
    }


    private static class LiveRoomHandler extends Handler {

        private LiveRoomViewHolder mLiveRoomViewHolder;
        private static final int WHAT_REFRESH_USER_LIST = 1;
        private static final int WHAT_TIME_CHARGE = 2;//计时收费房间定时请求接口扣费
        private static final int WHAT_ANCHOR_LIVE_TIME = 3;//直播间主播计时
        private static final int WHAT_ANCHOR_PAUSE = 4;//主播切后台
        private static final int WHAT_GAME_TIME = 10;//游戏倒计时

        public LiveRoomHandler(LiveRoomViewHolder liveRoomViewHolder) {
            mLiveRoomViewHolder = new WeakReference<>(liveRoomViewHolder).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mLiveRoomViewHolder != null) {
                switch (msg.what) {
                    case WHAT_REFRESH_USER_LIST:
                        mLiveRoomViewHolder.refreshUserList();
                        break;
                    case WHAT_TIME_CHARGE:
                        mLiveRoomViewHolder.requestTimeCharge();
                        break;
                    case WHAT_ANCHOR_LIVE_TIME:
                        mLiveRoomViewHolder.showAnchorLiveTime();
                        break;
                    case WHAT_ANCHOR_PAUSE:
                        mLiveRoomViewHolder.anchorEndLive();
                        break;
                    case WHAT_GAME_TIME:
                        mLiveRoomViewHolder.refreshGameTime() ;
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
            mLiveRoomViewHolder = null;
        }
    }
}
