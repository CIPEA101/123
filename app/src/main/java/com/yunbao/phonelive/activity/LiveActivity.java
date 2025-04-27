package com.yunbao.phonelive.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.GameTypeBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveBuyGuardMsgBean;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.bean.LiveDanMuBean;
import com.yunbao.phonelive.bean.LiveEnterRoomBean;
import com.yunbao.phonelive.bean.LiveGiftBean;
import com.yunbao.phonelive.bean.LiveGuardInfo;
import com.yunbao.phonelive.bean.LiveReceiveGiftBean;
import com.yunbao.phonelive.bean.LiveUserGiftBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.bean.UserShareCode;
import com.yunbao.phonelive.dialog.LiveChatListDialogFragment;
import com.yunbao.phonelive.dialog.LiveChatRoomDialogFragment;
import com.yunbao.phonelive.dialog.LiveGiftRankFragment;
import com.yunbao.phonelive.dialog.LiveGuardBuyDialogFragment;
import com.yunbao.phonelive.dialog.LiveGuardDialogFragment;
import com.yunbao.phonelive.dialog.LiveRedPackListDialogFragment;
import com.yunbao.phonelive.dialog.LiveRedPackSendDialogFragment;
import com.yunbao.phonelive.dialog.LiveShareDialogFragment;
import com.yunbao.phonelive.event.CoinChangeEvent;
import com.yunbao.phonelive.event.FollowEvent;
import com.yunbao.phonelive.event.GameTzEvent;
import com.yunbao.phonelive.game.GamePresenter;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.im.ImMessageUtil;
import com.yunbao.phonelive.im.ImUnReadCountEvent;
import com.yunbao.phonelive.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.phonelive.mob.MobCallback;
import com.yunbao.phonelive.mob.MobConst;
import com.yunbao.phonelive.mob.MobShareUtil;
import com.yunbao.phonelive.mob.ShareData;
import com.yunbao.phonelive.presenter.LiveLinkMicAnchorPresenter;
import com.yunbao.phonelive.presenter.LiveLinkMicPkPresenter;
import com.yunbao.phonelive.presenter.LiveLinkMicPresenter;
import com.yunbao.phonelive.socket.SocketChatUtil;
import com.yunbao.phonelive.socket.SocketClient;
import com.yunbao.phonelive.socket.SocketMessageListener;
import com.yunbao.phonelive.utils.BitmapUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.KeyBoardHeightUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.ProcessImageUtil;
import com.yunbao.phonelive.utils.ProcessResultUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.ViewUtils;
import com.yunbao.phonelive.utils.WordUtil;
import com.yunbao.phonelive.utils.ZXingUtils;
import com.yunbao.phonelive.views.AbsLiveViewHolder;
import com.yunbao.phonelive.views.AddLiveChatPop;
import com.yunbao.phonelive.views.LiveAddImpressViewHolder;
import com.yunbao.phonelive.views.LiveAdminListViewHolder;
import com.yunbao.phonelive.views.LiveContributeViewHolder;
import com.yunbao.phonelive.views.LiveEndViewHolder;
import com.yunbao.phonelive.views.LiveRoomViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 */

public abstract class LiveActivity extends AbsActivity implements SocketMessageListener, LiveShareDialogFragment.ActionListener, KeyBoardHeightChangeListener {

    protected ViewGroup mContainer;
    protected ViewGroup mPageContainer;
    protected LiveRoomViewHolder mLiveRoomViewHolder;
    protected AbsLiveViewHolder mLiveBottomViewHolder;
    protected LiveAddImpressViewHolder mLiveAddImpressViewHolder;
    protected LiveContributeViewHolder mLiveContributeViewHolder;
    protected LiveAdminListViewHolder mLiveAdminListViewHolder;
    protected LiveEndViewHolder mLiveEndViewHolder;
    protected LiveLinkMicPresenter mLiveLinkMicPresenter;//观众与主播连麦逻辑
    protected LiveLinkMicAnchorPresenter mLiveLinkMicAnchorPresenter;//主播与主播连麦逻辑
    protected LiveLinkMicPkPresenter mLiveLinkMicPkPresenter;//主播与主播PK逻辑
    protected GamePresenter mGamePresenter;
    protected SocketClient mSocketClient;
    protected LiveBean mLiveBean;
    protected boolean mIsAnchor;//是否是主播
    protected int mSocketUserType;//socket用户类型  30 普通用户  40 管理员  50 主播  60超管
    protected String mStream;
    protected String mLiveUid;
    protected String mLiveUserAvatar;//当前主播头像
    protected String mDanmuPrice;//弹幕价格
    protected String mCoinName;//钻石名称
    protected int mLiveType;//直播间的类型  普通 密码 门票 计时等
    protected int mLiveTypeVal;//收费价格,计时收费每次扣费的值
    protected String mShutTime;//禁言时间
    protected KeyBoardHeightUtil mKeyBoardHeightUtil;
    private MobShareUtil mMobShareUtil;
    private ProcessImageUtil mImageUtil;
    private boolean mFirstConnectSocket;//是否是第一次连接成功socket
    private boolean mGamePlaying;//是否在游戏中
    private boolean mChatRoomOpened;//判断私信聊天窗口是否打开
    private LiveChatRoomDialogFragment mLiveChatRoomDialogFragment;//私信聊天窗口
    protected LiveGuardInfo mLiveGuardInfo;
    private HashSet<DialogFragment> mDialogFragmentSet;

    protected String mGameId = "" ;

    //游戏类型
    protected List<GameTypeBean> mGameList = new ArrayList<>() ;
    public List<GameTypeBean> getGameTypeList(){
        return mGameList ;
    }

    //分享海报
    private UserShareCode mShareCodeInfo ;
    private Dialog mShareDialog;//分享海报
    private View mCardLay ;
    private ImageView mCardShareIv ;
    private TextView mCardDescTv ;
    private TextView mCardShareTv ;
    private ImageView mCardAvatorIv;
    private TextView mCardLinkTv ;
    private int mCardWidHei ;

    public ProcessResultUtil mProcessResultUtil;

    @Override
    protected void main() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mProcessResultUtil = new ProcessImageUtil(this) ;

        mCoinName = AppConfig.getInstance().getCoinName();
        mIsAnchor = this instanceof LiveAnchorActivity;
        mPageContainer = findViewById(R.id.page_container);
        EventBus.getDefault().register(this);
        mImageUtil = new ProcessImageUtil(this);
        mDialogFragmentSet = new HashSet<>();
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    public ViewGroup getPageContainer() {
        return mPageContainer;
    }

    public ProcessImageUtil getProcessImageUtil() {
        return mImageUtil;
    }

    public void addDialogFragment(DialogFragment dialogFragment) {
        if (mDialogFragmentSet != null && dialogFragment != null) {
            mDialogFragmentSet.add(dialogFragment);
        }
    }

    public void removeDialogFragment(DialogFragment dialogFragment) {
        if (mDialogFragmentSet != null && dialogFragment != null) {
            mDialogFragmentSet.remove(dialogFragment);
        }
    }

    private void hideDialogs() {
        if (mDialogFragmentSet != null) {
            for (DialogFragment dialogFragment : mDialogFragmentSet) {
                if (dialogFragment != null) {
                    dialogFragment.dismissAllowingStateLoss();
                }
            }
        }
    }


    /**
     * 连接成功socket后调用
     */
    @Override
    public void onConnect(boolean successConn) {
        if (successConn) {
            if (!mFirstConnectSocket) {
                mFirstConnectSocket = true;
                if (mLiveType == Constants.LIVE_TYPE_PAY || mLiveType == Constants.LIVE_TYPE_TIME) {
                    SocketChatUtil.sendUpdateVotesMessage(mSocketClient, mLiveTypeVal, 1);
                }
                SocketChatUtil.getFakeFans(mSocketClient);
            }
        }
    }

    /**
     * 自己的socket断开
     */
    @Override
    public void onDisConnect() {

    }

    /**
     * 收到聊天消息
     */
    @Override
    public void onChat(LiveChatBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertChat(bean);
        }
        if (bean.getType() == LiveChatBean.LIGHT) {
            onLight();
        }
    }

    /**
     * 收到飘心消息
     */
    @Override
    public void onLight() {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.playLightAnim();
        }
    }

    /**
     * 收到用户进房间消息
     */
    @Override
    public void onEnterRoom(LiveEnterRoomBean bean) {
        if (mLiveRoomViewHolder != null) {
            LiveUserGiftBean u = bean.getUserBean();
            mLiveRoomViewHolder.insertUser(u);
            mLiveRoomViewHolder.insertChat(bean.getLiveChatBean());
            mLiveRoomViewHolder.onEnterRoom(bean);
        }
    }

    /**
     * 收到用户离开房间消息
     */
    @Override
    public void onLeaveRoom(UserBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.removeUser(bean.getId());
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLeaveRoom(bean);
        }
    }

    /**
     * 收到礼物消息
     */
    @Override
    public void onSendGift(LiveReceiveGiftBean bean) {
        if (mLiveRoomViewHolder != null) {
            // mLiveRoomViewHolder.insertChat(bean.getLiveChatBean());
            mLiveRoomViewHolder.showGiftMessage(bean);
        }
    }

    /**
     * pk 时候收到礼物
     *
     * @param leftGift  左边的映票数
     * @param rightGift 右边的映票数
     */
    @Override
    public void onSendGiftPk(long leftGift, long rightGift) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onPkProgressChanged(leftGift, rightGift);
        }
    }

    /**
     * 收到弹幕消息
     */
    @Override
    public void onSendDanMu(LiveDanMuBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.showDanmu(bean);
        }
    }

    /**
     * 观众收到直播结束消息
     */
    @Override
    public void onLiveEnd() {
        hideDialogs();
    }

    /**
     * 超管关闭直播间
     */
    @Override
    public void onSuperCloseLive() {
        hideDialogs();
    }

    /**
     * 踢人
     */
    @Override
    public void onKick(String touid) {

    }

    /**
     * 禁言
     */
    @Override
    public void onShutUp(String touid, String content) {

    }

    /**
     * 设置或取消管理员
     */
    @Override
    public void onSetAdmin(String toUid, int isAdmin) {
        if (!TextUtils.isEmpty(toUid) && toUid.equals(AppConfig.getInstance().getUid())) {
            mSocketUserType = isAdmin == 1 ? Constants.SOCKET_USER_TYPE_ADMIN : Constants.SOCKET_USER_TYPE_NORMAL;
        }
    }

    /**
     * 主播切换计时收费或更改计时收费价格的时候执行
     */
    @Override
    public void onChangeTimeCharge(int typeVal) {

    }

    /**
     * 门票或计时收费更新主播映票数
     */
    @Override
    public void onUpdateVotes(String uid, String deltaVal, int first) {
        if (!AppConfig.getInstance().getUid().equals(uid) || first != 1) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.updateVotes(deltaVal);
            }
        }
    }

    /**
     * 添加僵尸粉
     */
    @Override
    public void addFakeFans(List<LiveUserGiftBean> list) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertUser(list);
        }
    }

    /**
     * 直播间  收到购买守护消息
     */
    @Override
    public void onBuyGuard(LiveBuyGuardMsgBean bean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.onGuardInfoChanged(bean);
            LiveChatBean chatBean = new LiveChatBean();
            chatBean.setContent(bean.getUserName() + WordUtil.getString(R.string.guard_buy_msg));
            chatBean.setType(LiveChatBean.SYSTEM);
            mLiveRoomViewHolder.insertChat(chatBean);
        }
    }

    /**
     * 直播间 收到红包消息
     */
    @Override
    public void onRedPack(LiveChatBean liveChatBean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.setRedPackBtnVisible(true);
            mLiveRoomViewHolder.insertChat(liveChatBean);
        }
    }

    @Override
    public void onTzFollow(LiveChatBean liveChatBean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertChat(liveChatBean);
        }
    }

    @Override
    public void onZjList(LiveChatBean liveChatBean) {
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.insertChat(liveChatBean);
        }
    }

    /**
     * 观众与主播连麦  主播收到观众的连麦申请
     */
    @Override
    public void onAudienceApplyLinkMic(UserBean u) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceApplyLinkMic(u);
        }
    }

    /**
     * 观众与主播连麦  观众收到主播同意连麦的socket
     */
    @Override
    public void onAnchorAcceptLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorAcceptLinkMic();
        }
    }

    /**
     * 观众与主播连麦  观众收到主播拒绝连麦的socket
     */
    @Override
    public void onAnchorRefuseLinkMic() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorRefuseLinkMic();
        }
    }

    /**
     * 观众与主播连麦  主播收到观众发过来的流地址
     */
    @Override
    public void onAudienceSendLinkMicUrl(String uid, String uname, String playUrl) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceSendLinkMicUrl(uid, uname, playUrl);
        }

    }

    /**
     * 观众与主播连麦  主播关闭观众的连麦
     */
    @Override
    public void onAnchorCloseLinkMic(String touid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorCloseLinkMic(touid, uname);
        }
    }

    /**
     * 观众与主播连麦  观众主动断开连麦
     */
    @Override
    public void onAudienceCloseLinkMic(String uid, String uname) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceCloseLinkMic(uid, uname);
        }
    }

    /**
     * 观众与主播连麦  主播连麦无响应
     */
    @Override
    public void onAnchorNotResponse() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorNotResponse();
        }
    }

    /**
     * 观众与主播连麦  主播正在忙
     */
    @Override
    public void onAnchorBusy() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAnchorBusy();
        }
    }

    /**
     * 主播与主播连麦  主播收到其他主播发过来的连麦申请的回调
     */
    @Override
    public void onLinkMicAnchorApply(UserBean u, String stream) {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  所有人收到对方主播的播流地址的回调
     *
     * @param playUrl 对方主播的播流地址
     * @param pkUid   对方主播的uid
     */
    @Override
    public void onLinkMicAnchorPlayUrl(String pkUid, String playUrl) {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorPlayUrl(pkUid, playUrl);
        }
    }

    /**
     * 主播与主播连麦  断开连麦的回调
     */
    @Override
    public void onLinkMicAnchorClose() {
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.onLinkMicAnchorClose();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
    }

    /**
     * 主播与主播连麦  对方主播拒绝连麦的回调
     */
    @Override
    public void onLinkMicAnchorRefuse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播无响应的回调
     */
    @Override
    public void onLinkMicAnchorNotResponse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播连麦  对方主播正在忙的回调
     */
    @Override
    public void onLinkMicAnchorBusy() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK  主播收到对方主播发过来的PK申请的回调
     *
     * @param u      对方主播的信息
     * @param stream 对方主播的stream
     */
    @Override
    public void onLinkMicPkApply(UserBean u, String stream) {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK 所有人收到PK开始的回调
     */
    @Override
    public void onLinkMicPkStart(String pkUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkStart(pkUid);
        }
    }

    /**
     * 主播与主播PK  所有人收到断开连麦pk的回调
     */
    @Override
    public void onLinkMicPkClose() {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkClose();
        }
    }

    /**
     * 主播与主播PK  对方主播拒绝pk的回调
     */
    @Override
    public void onLinkMicPkRefuse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   对方主播正在忙的回调
     */
    @Override
    public void onLinkMicPkBusy() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   对方主播无响应的回调
     */
    @Override
    public void onLinkMicPkNotResponse() {
        //主播直播间实现此逻辑
    }

    /**
     * 主播与主播PK   所有人收到PK结果的回调
     */
    @Override
    public void onLinkMicPkEnd(String winUid) {
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.onLinkMicPkEnd(winUid);
        }
    }


    /**
     * 连麦观众退出直播间
     */
    @Override
    public void onAudienceLinkMicExitRoom(String touid) {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.onAudienceLinkMicExitRoom(touid);
        }
    }

    @Override
    public void onGameZjh(JSONObject obj) {
        if (mGamePresenter != null) {
            mGamePresenter.onGameZjhSocket(obj);
        }
    }

    @Override
    public void onGameHd(JSONObject obj) {
        if (mGamePresenter != null) {
            mGamePresenter.onGameHdSocket(obj);
        }
    }

    @Override
    public void onGameZp(JSONObject obj) {
        if (mGamePresenter != null) {
            mGamePresenter.onGameZpSocket(obj);
        }
    }

    @Override
    public void onGameNz(JSONObject obj) {
        if (mGamePresenter != null) {
            mGamePresenter.onGameNzSocket(obj);
        }
    }

    @Override
    public void onGameEbb(JSONObject obj) {
        if (mGamePresenter != null) {
            mGamePresenter.onGameEbbSocket(obj);
        }
    }

    /**
     * 打开聊天输入框
     */
    public void openChatWindow() {
        //获取当前等级
        HttpUtil.getGrade(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    /**
                     * 	"level": "1",用户等级
                     */
                    JSONObject obj = JSON.parseObject(info[0]);
                    if(obj==null)return;
                    String level=obj.getString("is_speak");
                    if("1".equals(level)){
                        if (mLiveRoomViewHolder != null) {
                            mLiveRoomViewHolder.chatScrollToBottom();
                        }

                        AddLiveChatPop chatPop = new AddLiveChatPop(mContext,mDanmuPrice) ;
                        chatPop.showChatPop() ;
                    }else {
                        ToastUtil.show("需要达到vip"+obj.getString("speak_level")+"才能发言");
                    }

                }
            }
        });
//        if (mLiveRoomViewHolder != null) {
//            mLiveRoomViewHolder.chatScrollToBottom();
//        }
//
//        AddLiveChatPop chatPop = new AddLiveChatPop(mContext,mDanmuPrice) ;
//        chatPop.showChatPop() ;
    }

    /**
     * 打开私信列表窗口
     */
    public void openChatListWindow() {
        LiveChatListDialogFragment fragment = new LiveChatListDialogFragment();
        if (!mIsAnchor) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.LIVE_UID, mLiveUid);
            fragment.setArguments(bundle);
        }
        fragment.show(getSupportFragmentManager(), "LiveChatListDialogFragment");
    }

    /**
     * 打开私信聊天窗口
     */
    public void openChatRoomWindow(UserBean userBean, boolean following) {
        if (mKeyBoardHeightUtil == null) {
            mKeyBoardHeightUtil = new KeyBoardHeightUtil(mContext, super.findViewById(android.R.id.content), this);
            mKeyBoardHeightUtil.start();
        }
        LiveChatRoomDialogFragment fragment = new LiveChatRoomDialogFragment();
        fragment.setImageUtil(mImageUtil);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.USER_BEAN, userBean);
        bundle.putBoolean(Constants.FOLLOW, following);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveChatRoomDialogFragment");
    }

    /**
     * 发 弹幕 消息
     */
    public void sendDanmuMessage(String content) {
        HttpUtil.sendDanmu(content, mLiveUid, mStream, mDanmuCallback);
    }

    private HttpCallback mDanmuCallback = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {
            if (code == 0 && info.length > 0) {
                JSONObject obj = JSON.parseObject(info[0]);
                UserBean u = AppConfig.getInstance().getUserBean();
                if (u != null) {
                    u.setLevel(obj.getIntValue("level"));
                    String coin = obj.getString("coin");
                    u.setCoin(coin);
                    onCoinChanged(coin);
                }
                SocketChatUtil.sendDanmuMessage(mSocketClient, obj.getString("barragetoken"));
            } else {
                ToastUtil.show(msg);
            }
        }
    };


    /**
     * 发 聊天 消息
     */
    public void sendChatMessage(String content) {
        int guardType = mLiveGuardInfo != null ? mLiveGuardInfo.getMyGuardType() : Constants.GUARD_TYPE_NONE;
        SocketChatUtil.sendChatMessage(mSocketClient, content, mIsAnchor, mSocketUserType, guardType);
    }

    /**
     * 发 系统 消息
     */
    public void sendSystemMessage(String content,int type) {
        SocketChatUtil.sendSystemMessage(mSocketClient, content,type);
    }

    public void sendSystemTzMessage(String gameId,String itemIds,String content){
        SocketChatUtil.sendSystemMessage(mSocketClient,gameId,itemIds, content,2) ;
    }

    /**
     * 发 送礼物 消息
     */
    public void sendGiftMessage(LiveGiftBean giftBean, String giftToken) {
        SocketChatUtil.sendGiftMessage(mSocketClient, giftBean.getType(), giftToken, mLiveUid);
    }

    /**
     * 主播或管理员踢人
     */
    public void kickUser(String toUid, String toName) {
        SocketChatUtil.sendKickMessage(mSocketClient, toUid, toName);
    }

    /**
     * 禁言
     */
    public void setShutUp(String toUid, String toName) {
        SocketChatUtil.sendShutUpMessage(mSocketClient, toUid, toName, mShutTime);
    }

    /**
     * 设置或取消管理员消息
     */
    public void sendSetAdminMessage(int action, String toUid, String toName) {
        SocketChatUtil.sendSetAdminMessage(mSocketClient, action, toUid, toName);
    }


    /**
     * 超管关闭直播间
     */
    public void superCloseRoom() {
        SocketChatUtil.superCloseRoom(mSocketClient);
    }

    /**
     * 更新主播映票数
     */
    public void sendUpdateVotesMessage(int deltaVal) {
        SocketChatUtil.sendUpdateVotesMessage(mSocketClient, deltaVal);
    }


    /**
     * 发送购买守护成功消息
     */
    public void sendBuyGuardMessage(String votes, int guardNum, int guardType) {
        SocketChatUtil.sendBuyGuardMessage(mSocketClient, votes, guardNum, guardType);
    }

    /**
     * 发送发红包成功消息
     */
    public void sendRedPackMessage() {
        SocketChatUtil.sendRedPackMessage(mSocketClient);
    }


    /**
     * 打开添加印象窗口
     */
    public void openAddImpressWindow(String toUid) {
        if (mLiveAddImpressViewHolder == null) {
            mLiveAddImpressViewHolder = new LiveAddImpressViewHolder(mContext, mPageContainer);
            addLifeCycleListener(mLiveAddImpressViewHolder.getLifeCycleListener());
        }
        mLiveAddImpressViewHolder.setToUid(toUid);
        mLiveAddImpressViewHolder.addToParent();
        mLiveAddImpressViewHolder.show();
    }

    /**
     * 直播间贡献榜窗口
     */
    public void openContributeWindow() {
        LiveGiftRankFragment fragment = new LiveGiftRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGiftRankFragment");
    }


    /**
     * 直播间管理员窗口
     */
    public void openAdminListWindow() {
        if (mLiveAdminListViewHolder == null) {
            mLiveAdminListViewHolder = new LiveAdminListViewHolder(mContext, mPageContainer, mLiveUid);
            addLifeCycleListener(mLiveAdminListViewHolder.getLifeCycleListener());
            mLiveAdminListViewHolder.addToParent();
        }
        mLiveAdminListViewHolder.show();
    }

    /**
     * 是否能够返回
     */
    protected boolean canBackPressed() {
        if (mLiveContributeViewHolder != null && mLiveContributeViewHolder.isShowed()) {
            mLiveContributeViewHolder.hide();
            return false;
        }
        if (mLiveAddImpressViewHolder != null && mLiveAddImpressViewHolder.isShowed()) {
            mLiveAddImpressViewHolder.hide();
            return false;
        }
        if (mLiveAdminListViewHolder != null && mLiveAdminListViewHolder.isShowed()) {
            mLiveAdminListViewHolder.hide();
            return false;
        }
        return true;
    }

    /**
     * 打开分享窗口
     */
    public void openShareWindow() {
//        LiveShareDialogFragment fragment = new LiveShareDialogFragment();
//        fragment.setActionListener(this);
//        fragment.show(getSupportFragmentManager(), "LiveShareDialogFragment");

        getUserShareCode() ;
    }


    /**
     * 铭牌
     */
    private void showUserCardShareDialog(){
        if(null == mShareDialog){
            mShareDialog = new Dialog(mContext,R.style.dialog) ;

            View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_user_card_share_live,null) ;
            mShareDialog.setContentView(contentView);

            int screenWidth = ScreenDimenUtil.getInstance().getScreenWdith() ;
            contentView.getLayoutParams().width = screenWidth;
            contentView.getLayoutParams().height = ScreenDimenUtil.getInstance().getScreenHeight();

            mCardLay = contentView.findViewById(R.id.dialog_live_share_content_lay) ;
            mCardAvatorIv = contentView.findViewById(R.id.dialog_live_share_avatar_iv) ;
            mCardShareIv = contentView.findViewById(R.id.dialog_live_share_code_iv) ;
            mCardShareTv = contentView.findViewById(R.id.dialog_live_share_code_tv) ;
            mCardDescTv = contentView.findViewById(R.id.dialog_live_share_desc_tv) ;
            mCardLinkTv = contentView.findViewById(R.id.dialog_live_share_url_tv) ;

            mCardWidHei = DpUtil.dp2px(60) ;

            View closeIv = contentView.findViewById(R.id.dialog_live_share_close_iv) ;
            View okIv = contentView.findViewById(R.id.dialog_live_share_save_tv) ;
            okIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProcessResultUtil.requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, new Runnable() {
                        @Override
                        public void run() {
                            saveUserShareCode() ;
                        }
                    });
                }
            });
            closeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mShareDialog.dismiss();
                }
            });
        }

        String link = mShareCodeInfo.getLink() ;
        if(TextUtils.isEmpty(link)){
            link = getResources().getString(R.string.app_download_url) ;
        }
        mCardLinkTv.setText(link) ;
        mCardShareIv.setImageBitmap(ZXingUtils.createQRImage(StringUtil.convertImageUrl(link),mCardWidHei,mCardWidHei
                ,BitmapUtil.getInstance().decodeBitmap(R.mipmap.ic_launcher))) ;
        ImgLoader.display(StringUtil.convertImageUrl(mLiveUserAvatar),mCardAvatorIv) ;

        String shareDesc = mShareCodeInfo.getSignature() ;
        String shareCode = mShareCodeInfo.getCode() ;
        mCardShareTv.setText("邀请码" + shareCode) ;
        if(!TextUtils.isEmpty(shareDesc)){
            mCardDescTv.setText(shareDesc) ;
        }

        mShareDialog.show() ;
    }

    /**
     * 保存海报
     */
    private void saveUserShareCode(){
        if(mCardLay != null){
            String path = BitmapUtil.getInstance().saveBitmap(ViewUtils.saveViewToBitmap(mCardLay)) ;
            if(!TextUtils.isEmpty(path)){
                ToastUtil.show("图片保存在" + path) ;
                // 最后通知图库更新
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ path)));
            }else{
                ToastUtil.show("保存失败") ;
            }
        }
    }
    private void getUserShareCode(){
        HttpUtil.getUserShareCode(AppConfig.getInstance().getUid()
                ,new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            try {
                                JSONObject obj = JSON.parseObject(info[0]);
                                UserShareCode bean = JSON.toJavaObject(obj, UserShareCode.class);
                                if(bean != null){
                                    mShareCodeInfo = bean ;
                                    showUserCardShareDialog();
                                }else{
                                    ToastUtil.show("没有相关信息");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                                ToastUtil.show("没有相关信息");
                            }
                        }else{
                            ToastUtil.show("没有相关信息");
                        }
                    }
                    @Override
                    public boolean showLoadingDialog() {
                        return true;
                    }
                    @Override
                    public Dialog createLoadingDialog() {
                        return DialogUitl.loadingDialog(mContext);
                    }
                });
    }


    /**
     * 分享点击事件回调
     */
    @Override
    public void onItemClick(String type) {
        if (Constants.LINK.equals(type)) {
            copyLink();
        } else {
            shareLive(type, null);
        }
    }

    /**
     * 复制直播间链接
     */
    private void copyLink() {
        if (TextUtils.isEmpty(mLiveUid)) {
            return;
        }
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        String link = configBean.getLiveWxShareUrl() + mLiveUid;
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", link);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(WordUtil.getString(R.string.copy_success));
    }


    /**
     * 分享直播间
     */
    public void shareLive(String type, MobCallback callback) {
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean == null) {
            return;
        }
        if (mMobShareUtil == null) {
            mMobShareUtil = new MobShareUtil();
        }
        ShareData data = new ShareData();
        data.setTitle(configBean.getLiveShareTitle());
        data.setDes(mLiveBean.getUserNiceName() + configBean.getLiveShareDes());
        data.setImgUrl(mLiveBean.getAvatarThumb());
        String webUrl = configBean.getDownloadApkUrl();
        if (MobConst.Type.WX.equals(type) || MobConst.Type.WX_PYQ.equals(type)) {
            webUrl = configBean.getLiveWxShareUrl() + mLiveUid;
        }
        data.setWebUrl(webUrl);
        mMobShareUtil.execute(type, data, callback);
    }

    /**
     * 监听关注变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(FollowEvent e) {
        if (!TextUtils.isEmpty(mLiveUid) && mLiveUid.equals(e.getToUid())) {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.setAttention(e.getIsAttention());
            }
        }
    }

    /**
     * 监听私信未读消息数变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImUnReadCountEvent(ImUnReadCountEvent e) {
        String unReadCount = e.getUnReadCount();
        if (!TextUtils.isEmpty(unReadCount) && mLiveBottomViewHolder != null) {
            mLiveBottomViewHolder.setUnReadCount(unReadCount);
        }
    }

    /**
     * 获取私信未读消息数量
     */
    protected String getImUnReadCount() {
        return ImMessageUtil.getInstance().getAllUnReadMsgCount();
    }

    /**
     * 监听钻石数量变化事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        onCoinChanged(e.getCoin());
        if (e.isChargeSuccess() && this instanceof LiveAudienceActivity) {
            ((LiveAudienceActivity) this).onChargeSuccess();
        }
    }
    /**
     * 投注信息相关
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTouzhuFailEvent(GameTzEvent e) {
        if (e != null && this instanceof LiveAudienceActivity) {
            String message = e.getMessage() ;
            if(!TextUtils.isEmpty(message)){
                ToastUtil.showLong(message);
//                Toast.makeText(mContext,message,Toast.LENGTH_LONG).show() ;
            }
        }
    }

    public void onCoinChanged(String coin) {
        if (mGamePresenter != null) {
            mGamePresenter.setLastCoin(coin);
        }
    }


    /**
     * 守护列表弹窗
     */
    public void openGuardListWindow() {
        LiveGuardDialogFragment fragment = new LiveGuardDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putBoolean(Constants.ANCHOR, mIsAnchor);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardDialogFragment");
    }

    /**
     * 打开购买守护的弹窗
     */
    public void openBuyGuardWindow() {
        if (TextUtils.isEmpty(mLiveUid) || TextUtils.isEmpty(mStream) || mLiveGuardInfo == null) {
            return;
        }
        LiveGuardBuyDialogFragment fragment = new LiveGuardBuyDialogFragment();
        fragment.setLiveGuardInfo(mLiveGuardInfo);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.COIN_NAME, mCoinName);
        bundle.putString(Constants.LIVE_UID, mLiveUid);
        bundle.putString(Constants.STREAM, mStream);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "LiveGuardBuyDialogFragment");
    }

    /**
     * 打开发红包的弹窗
     */
    public void openRedPackSendWindow() {
        LiveRedPackSendDialogFragment fragment = new LiveRedPackSendDialogFragment();
        fragment.setStream(mStream);
        //fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackSendDialogFragment");
    }

    /**
     * 打开发红包列表弹窗
     */
    public void openRedPackListWindow() {
        LiveRedPackListDialogFragment fragment = new LiveRedPackListDialogFragment();
        fragment.setStream(mStream);
        fragment.setCoinName(mCoinName);
        fragment.show(getSupportFragmentManager(), "LiveRedPackListDialogFragment");
    }


    /**
     * 键盘高度的变化
     */
    @Override
    public void onKeyBoardHeightChanged(int visibleHeight, int keyboardHeight) {
        if (mChatRoomOpened) {//判断私信聊天窗口是否打开
            if (mLiveChatRoomDialogFragment != null) {
                mLiveChatRoomDialogFragment.scrollToBottom();
            }
        } else {
            if (mLiveRoomViewHolder != null) {
                mLiveRoomViewHolder.onKeyBoardChanged(visibleHeight, keyboardHeight);
            }
        }
    }

    @Override
    public boolean isSoftInputShowed() {
        if (mKeyBoardHeightUtil != null) {
            return mKeyBoardHeightUtil.isSoftInputShowed();
        }
        return false;
    }

    public void setChatRoomOpened(LiveChatRoomDialogFragment chatRoomDialogFragment, boolean chatRoomOpened) {
        mChatRoomOpened = chatRoomOpened;
        mLiveChatRoomDialogFragment = chatRoomDialogFragment;
    }

    /**
     * 是否在游戏中
     */
    public boolean isGamePlaying() {
        return mGamePlaying;
    }

    public void setGamePlaying(boolean gamePlaying) {
        mGamePlaying = gamePlaying;
    }

    /**
     * 是否在连麦中
     */
    public boolean isLinkMic() {
        return mLiveLinkMicPresenter != null && mLiveLinkMicPresenter.isLinkMic();
    }

    /**
     * 主播是否在连麦中
     */
    public boolean isLinkMicAnchor() {
        return mLiveLinkMicAnchorPresenter != null && mLiveLinkMicAnchorPresenter.isLinkMic();
    }


    @Override
    protected void onPause() {
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.pause();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.resume();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.resume();
        }
    }

    protected void release() {
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConsts.SEND_DANMU);
        if (mKeyBoardHeightUtil != null) {
            mKeyBoardHeightUtil.release();
        }
        if (mLiveLinkMicPresenter != null) {
            mLiveLinkMicPresenter.release();
        }
        if (mLiveLinkMicAnchorPresenter != null) {
            mLiveLinkMicAnchorPresenter.release();
        }
        if (mLiveLinkMicPkPresenter != null) {
            mLiveLinkMicPkPresenter.release();
        }
        if (mLiveRoomViewHolder != null) {
            mLiveRoomViewHolder.release();
        }
        if (mLiveAddImpressViewHolder != null) {
            mLiveAddImpressViewHolder.release();
        }
        if (mLiveContributeViewHolder != null) {
            mLiveContributeViewHolder.release();
        }
        if (mMobShareUtil != null) {
            mMobShareUtil.release();
        }
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        if (mGamePresenter != null) {
            mGamePresenter.release();
        }
        mKeyBoardHeightUtil = null;
        mLiveLinkMicPresenter = null;
        mLiveLinkMicAnchorPresenter = null;
        mLiveLinkMicPkPresenter = null;
        mLiveRoomViewHolder = null;
        mLiveAddImpressViewHolder = null;
        mLiveContributeViewHolder = null;
        mMobShareUtil = null;
        mImageUtil = null;
        L.e("LiveActivity--------release------>");
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }


}
