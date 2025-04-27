package com.yunbao.phonelive.socket;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.TzSocketFailBean;
import com.yunbao.phonelive.event.GameTzEvent;
import com.yunbao.phonelive.game.TzSocketRetBean;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

/**
 * 游戏投注相关socket
 */
public class JWebSocketClient extends WebSocketClient implements Serializable {
    private int mSocketType ;
    private boolean mAutoShowResult = true ;//是否自动显示中奖用户信息（目前设计的主播直播游戏每分钟都出结果，如果有用户中奖了，在消息界面显示出来）

    public static final int GAME_SOCKET_TYPE_RESULT = 1 ;//获取游戏结果，此时需要处理中奖用户显示
    public static final int GAME_SOCKET_TYPE_TOUZHU = 2 ;//游戏投注，此时需要处理
    public static final int GAME_SOCKET_TYPE_ROBOT= 3 ;//机器人投注信息，只需要匹配直播间id，并本地展示即可，不用发送公屏消息

    private String gameName;
    private String total;
    private Context mContext;

    private boolean mCanClose = false ;//是否自动关闭

    public JWebSocketClient(Context context, URI serverUri, int socketType) {
        super(serverUri);
        this.mContext = context ;
        this.mSocketType = socketType ;
    }

    public void setData(String gameName, String total) {
        this.gameName = gameName;
        this.total = total;
        mCanClose = true ;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        Log.e("JWebSocketClient", message);

        if(GAME_SOCKET_TYPE_TOUZHU == mSocketType){//投注
            if (gameName != null && !gameName.equals("")) {

                //投注额有限制，会返回 [{status:404,upper_limit:500}]，表示投注上限是500元
                //如果投注正常，返回正常数据。
                //如果余额不足，会提示  余额不足
                try {
                    Object nextObj = new JSONTokener(message).nextValue() ;
                    if(nextObj instanceof String){//是一个提示

                        EventBus.getDefault().post(new GameTzEvent(message)) ;

                    }else if(nextObj instanceof org.json.JSONArray){
                        if(message.contains("upper_limit")){

                            List<TzSocketFailBean> failBeans = JSON.parseArray(message,TzSocketFailBean.class) ;

                            if(failBeans != null && failBeans.size() >0 && "404".equals(failBeans.get(0).getStatus())){
                                TzSocketFailBean failBean = failBeans.get(0) ;
                                String toastStr = "" ;
                                if(!TextUtils.isEmpty(failBean.getUpper_limit())){
                                    toastStr = "单注投注额大于" + failBean.getUpper_limit() + "," ;
                                }
                                EventBus.getDefault().post(new GameTzEvent(toastStr + "投注失败")) ;
                            }
                        }
                    }else if(nextObj instanceof org.json.JSONObject){
                        TzSocketRetBean item = JSON.parseObject(message, TzSocketRetBean.class);

                        if(item != null){
                            if(AppConfig.getInstance().getUid().equals(item.getUser_id())){
                                EventBus.getDefault().post(new GameTzEvent("投注成功")) ;
                            }
                        }

                        StringBuilder stringBuffer = new StringBuilder();
                        List<String> ids = item.getId();
                        for (String id : ids) {
                            stringBuffer.append(id).append(",");
                        }
                        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                        ((LiveAudienceActivity) mContext).sendSystemTzMessage(item.getGame_id(),stringBuffer.toString()
                                ,AppConfig.getInstance().getUserBean().getUserNiceName()
//                        ,AppConfig.getInstance().getVotesName()
                                        + " 在" + gameName + "中,下注了" + total + "元。" );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(mCanClose){
                close();
            }
        }else if(GAME_SOCKET_TYPE_ROBOT == mSocketType){//机器人投注信息



        }else if(GAME_SOCKET_TYPE_RESULT == mSocketType){
            //2020年5月14日 中奖结果暂时设计为：主播端接收到中奖结果，然后发送一个系统消息（跟投注一样，只不过是主播发送消息，客户端接收）

            //数据会出现related_user_uid_result中奖用户信息返回 []  [[]] ，
            // 所以客户端需要处理，应该把中奖消息拼接好，然后自己推送到公屏上，不要发送消息。
//            if(!TextUtils.isEmpty(message)){
//                try {
//                    JSONObject object = JSONObject.parseObject(message) ;
//                    if(object != null){
//                        JSONArray userList = object.getJSONArray("related_user_uid_result") ;
//                        if(userList != null && userList.size() > 0){
//                            String firstItemString = JSONObject.toJSONString(userList.get(0)) ;
//                            LogUtil.e("JWebSocketClient","获取中奖结果,userList---firstItemString=" + firstItemString) ;
//
//                            String userUidListString = null ;
//
//                            Object nextObj = new JSONTokener(firstItemString).nextValue() ;
//                            if(nextObj instanceof org.json.JSONObject){
//
//                                userUidListString = firstItemString ;
//
//                                LogUtil.e("JWebSocketClient","获取中奖结果,result instanceof JSONObject") ;
//
//                            }else if(nextObj instanceof org.json.JSONArray){//没有中奖结果的时候，返回的是这种，暂不处理
//
//                                LogUtil.e("JWebSocketClient","获取中奖结果,result instanceof JSONArray") ;
//
//                            }
//
//                            if(userUidListString != null){
//                                SocketGameReceiveBean crb = JSON.parseObject(message, SocketGameReceiveBean.class);
//                                UserUidResultBean uurb = JSON.parseObject(userUidListString,UserUidResultBean.class) ;
//
//                                if (uurb != null && crb != null) {
//                                    //当主动连接socket的时候，会接收到一个结果，里面没有old_xxxx_game_id字段
//                                    // ，而推送的结果是有的，所以可以根据这个来区分是主动获取的还是系统推的
//                                    String gameName;
//                                    if (crb.getOld_yfks_game_id() != null) {
//                                        gameName = HttpConsts.GAME_NAME_YFKS;
//                                    } else if (crb.getOld_yf115_game_id() != null) {
//                                        gameName = HttpConsts.GAME_NAME_YF115;
//                                    } else if (crb.getOld_yfsc_game_id() != null) {
//                                        gameName = HttpConsts.GAME_NAME_YFSC;
//                                    } else if (crb.getOld_yfssc_game_id() != null) {
//                                        gameName = HttpConsts.GAME_NAME_YFSSC;
//                                    } else if (crb.getOld_yflhc_game_id() != null) {
//                                        gameName = HttpConsts.GAME_NAME_YFLHC;
//                                    } else if (crb.getOld_yfklsf_game_id() != null) {
//                                        gameName = HttpConsts.GAME_NAME_YFKLSF;
//                                    } else if (crb.getOld_yfxync_game_id() != null) {
//                                        gameName = HttpConsts.GAME_NAME_YFXYNC;
//                                    } else {
//                                        gameName = "";
//                                    }
//
//                                    List<String> strList = uurb.getList_nicename();
//
//                                    LogUtil.e("JWebSocketClient","获取中奖结果，gameName=" + gameName + ",strList=" + strList) ;
//
//                                    //中奖信息自己更新，不要发送系统消息
//                                    //拼接好相关中奖信息，发送给公屏

////                                    for (String str : strList) {
////                                        ((LiveAudienceActivity) mContext).sendSystemMessage(str + "#" + gameName, 1);
////                                    }
//                                }
//
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("JWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        Log.e("JWebSocketClient", "onError()=" + ex.toString());
    }

}
