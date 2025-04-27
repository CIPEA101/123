package com.yunbao.phonelive.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.speech.utils.LogUtil;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.TzInfoAdapter;
import com.yunbao.phonelive.game.TzInfoBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.socket.JWebSocketClient;
import com.yunbao.phonelive.socket.SocketGameReceiveBean;
import com.yunbao.phonelive.utils.GsonUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 跟投
 */
public class LiveGameTzInfoDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private ImageView close;
    private TextView issue;
    private TextView time;
    private RecyclerView recycler;
    private Button one;
    private Button two;
    private Button five;
    private Button ten;
    private Button twenty;
    private TextView injection;
    private TextView totalInject;
    private TextView balance;
    private Button sure;
    private TzInfoAdapter adapter;
    private String gameId;
    private String bl="1";
    private String gameName;
    private String ids;
    private List<TzInfoBean> list = new ArrayList<>();
    private JWebSocketClient socketClient;
    private int totalCoin = 1;

    private GameResultHandler mGameResultHandler;
    private JWebSocketClient mGameResultClient ;

    private String mGameNewPeroid ;
    private int timeSec = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_game_tz;
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

        mGameResultHandler = new GameResultHandler(this);

        close = mRootView.findViewById(R.id.game_chip_close);
        issue = mRootView.findViewById(R.id.tv_game_issue);
        time = mRootView.findViewById(R.id.tv_game_issue_time);
        recycler = mRootView.findViewById(R.id.recycler);
        one = mRootView.findViewById(R.id.btn_one_inject);
        two = mRootView.findViewById(R.id.btn_two_inject);
        five = mRootView.findViewById(R.id.btn_five_inject);
        ten = mRootView.findViewById(R.id.btn_ten_inject);
        twenty = mRootView.findViewById(R.id.btn_twenty_inject);
        injection = mRootView.findViewById(R.id.tz_injection);
        totalInject = mRootView.findViewById(R.id.tv_total_inject);
        balance = mRootView.findViewById(R.id.tv_balance);
        sure = mRootView.findViewById(R.id.btn_tz_sure);

        close.setOnClickListener(this);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        five.setOnClickListener(this);
        ten.setOnClickListener(this);
        twenty.setOnClickListener(this);
        sure.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null){
            ids = bundle.getString(Constants.LIVE_GAME_NAME_TZ_IDS);
            gameId = bundle.getString(Constants.LIVE_GAME_ID);
        }

        URI uri = URI.create(HttpConsts.SOCKET_GAME_PUSH_URL);
        socketClient = new JWebSocketClient(mContext,uri,JWebSocketClient.GAME_SOCKET_TYPE_TOUZHU);
        socketClient.connect();

        getTzInfo();
        loadNewData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.game_chip_close:
                dismiss();
                break;
            case R.id.btn_one_inject:
                setLight(0);
                if (list != null && list.size()> 0){
                    for(TzInfoBean item : list){
                        item.setMagnification("1");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "1";
                break;
            case R.id.btn_two_inject:
                setLight(1);
                if (list != null && list.size()> 0){
                    for(TzInfoBean item : list){
                        item.setMagnification("2");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "2";
                break;
            case R.id.btn_five_inject:
                setLight(2);
                if (list != null && list.size()> 0){
                    for(TzInfoBean item : list){
                        item.setMagnification("5");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "5";
                break;
            case R.id.btn_ten_inject:
                setLight(3);
                if (list != null && list.size()> 0){
                    for(TzInfoBean item : list){
                        item.setMagnification("10");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "10";
                break;
            case R.id.btn_twenty_inject:
                setLight(4);
                if (list != null && list.size()> 0){
                    for(TzInfoBean item : list){
                        item.setMagnification("20");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "20";
                break;
            case R.id.btn_tz_sure:
                getXzXync();
                break;
        }
    }

    private void setLight(int num) {
        switch (num) {
            case 0:
                totalInject.setText(String.valueOf(totalCoin *1*list.size()));
                setBackgroundChecked(one);
                setBackgroundUnCheck(two);
                setBackgroundUnCheck(five);
                setBackgroundUnCheck(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 1:
                totalInject.setText(String.valueOf(totalCoin *2*list.size()));
                setBackgroundUnCheck(one);
                setBackgroundChecked(two);
                setBackgroundUnCheck(five);
                setBackgroundUnCheck(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 2:
                totalInject.setText(String.valueOf(totalCoin *5*list.size()));
                setBackgroundUnCheck(one);
                setBackgroundUnCheck(two);
                setBackgroundChecked(five);
                setBackgroundUnCheck(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 3:
                totalInject.setText(String.valueOf(totalCoin *10*list.size()));
                setBackgroundUnCheck(one);
                setBackgroundUnCheck(two);
                setBackgroundUnCheck(five);
                setBackgroundChecked(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 4:
                totalInject.setText(String.valueOf(totalCoin *20*list.size()));
                setBackgroundUnCheck(one);
                setBackgroundUnCheck(two);
                setBackgroundUnCheck(five);
                setBackgroundUnCheck(ten);
                setBackgroundChecked(twenty);
                break;
        }
    }

    private void setBackgroundUnCheck(TextView tv) {
        tv.setBackgroundResource(R.drawable.btn_circle_half_2_unchecked);
        tv.setTextColor(mContext.getResources().getColor(R.color.black));
    }

    private void setBackgroundChecked(TextView tv) {
        tv.setBackgroundResource(R.drawable.btn_circle_half_2_checked);
        tv.setTextColor(mContext.getResources().getColor(R.color.white));
    }

    private void loadNewData() {
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    balance.setText(StringUtil.converMoney2Point(obj.getString("coin"))+"元");
                }
            }
        });
    }

    private void getTzInfo(){
        HttpUtil.getTzInfoDetail(ids, gameId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<TzInfoBean> tzList = JSON.parseArray(Arrays.toString(info), TzInfoBean.class);

                if(tzList != null && tzList.size() > 0){
                    list.addAll(tzList) ;

                    TzInfoBean firstItem = tzList.get(0) ;
                    if(firstItem != null){
                        if(TextUtils.isEmpty(mGameNewPeroid)){
                            mGameNewPeroid = firstItem.getPeriodId() ;
                        }
                        bl = firstItem.getMagnification() ;//倍率
                        totalCoin= Integer.parseInt(firstItem.getTotalcoin());//筹码
                    }
                }

                injection.setText(String.valueOf(list.size()));

                if("1".equals(bl)){
                    setLight(0) ;
                } else if("2".equals(bl)){
                    setLight(1) ;
                } else if("5".equals(bl)){
                    setLight(2) ;
                } else if("10".equals(bl)){
                    setLight(3) ;
                } else if("20".equals(bl)){
                    setLight(4) ;
                }

                gameName = HttpConsts.getGameNameByType(gameId) ;

                recycler.setLayoutManager(new LinearLayoutManager(mContext, GridLayoutManager.VERTICAL, false));
                adapter = new TzInfoAdapter(mContext,getLayoutInflater(),list,gameName);
                recycler.setAdapter(adapter);

                adapter.setOnItemClick(new TzInfoAdapter.OnItemClick() {
                    @Override
                    public void onClickCancel(int postion) {
                        list.remove(postion);
                        adapter.notifyDataSetChanged();

                        injection.setText(String.valueOf(list.size()));

                        if(list.size() > 0){
                            int size = Integer.parseInt(bl) * totalCoin * list.size();
                            totalInject.setText(String.valueOf(size));
                        }else {
                            totalInject.setText("0");
                        }
                    }
                });

                connectGameResultSocket() ;
            }
        });
    }

    private void getXzXync(){
        if(timeSec <= HttpConsts.GAME_STOP_IN_TIME){
            ToastUtil.show("封盘中");
            return;
        }

        if (list != null && list.size() > 0){
            StringBuffer strSend = new StringBuffer();
            for (int i = 0;i < list.size(); i++){
                if (gameName.equals(HttpConsts.GAME_NAME_YFKS)) {
                    strSend.append("1"
                            +"#"+mGameNewPeroid
                            +"#"+ AppConfig.getInstance().getUid()
                            +"#"+list.get(i).getType()
                            +"#"+list.get(i).getCode()
                            +"#"+totalCoin
                            +"#"+list.get(i).getOdds()
                            +"#"+bl+"@");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YF115)){
                    strSend.append("2"
                            +"#"+mGameNewPeroid
                            +"#"+ AppConfig.getInstance().getUid()
                            +"#"+list.get(i).getType()
                            +"#"+list.get(i).getCode()
                            +"#"+totalCoin
                            +"#"+list.get(i).getOdds()
                            +"#"+bl+"@");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFSC)){
                    strSend.append("3"
                            +"#"+mGameNewPeroid
                            +"#"+ AppConfig.getInstance().getUid()
                            +"#"+list.get(i).getType()
                            +"#"+list.get(i).getCode()
                            +"#"+totalCoin
                            +"#"+list.get(i).getOdds()
                            +"#"+bl+"@");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFSSC)){
                    strSend.append("4"
                            +"#"+mGameNewPeroid
                            +"#"+ AppConfig.getInstance().getUid()
                            +"#"+list.get(i).getType()
                            +"#"+list.get(i).getCode()
                            +"#"+totalCoin
                            +"#"+list.get(i).getOdds()
                            +"#"+bl+"@");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFLHC)){
                    strSend.append("5"
                            +"#"+mGameNewPeroid
                            +"#"+ AppConfig.getInstance().getUid()
                            +"#"+list.get(i).getType()
                            +"#"+list.get(i).getCode()
                            +"#"+totalCoin
                            +"#"+list.get(i).getOdds()
                            +"#"+bl+"@");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFKLSF)){
                    strSend.append("6"
                            +"#"+mGameNewPeroid
                            +"#"+ AppConfig.getInstance().getUid()
                            +"#"+list.get(i).getType()
                            +"#"+list.get(i).getCode()
                            +"#"+totalCoin
                            +"#"+list.get(i).getOdds()
                            +"#"+bl+"@");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFXYNC)){
                    strSend.append("7"
                            +"#"+mGameNewPeroid
                            +"#"+ AppConfig.getInstance().getUid()
                            +"#"+list.get(i).getType()
                            +"#"+list.get(i).getCode()
                            +"#"+totalCoin
                            +"#"+list.get(i).getOdds()
                            +"#"+bl+"@");
                }
            }
            if (strSend.toString().contains("@")){
                strSend.deleteCharAt(strSend.length()-1);
            }

            LogUtil.e("TestTouzhu","跟投 ---param=" + strSend.toString()) ;

            getSocket(strSend.toString());
        }
    }
    private void getSocket(String str){
        if(socketClient.isOpen()){
            socketClient.setData(gameName,totalInject.getText().toString());
            socketClient.send(str);

            dismiss();
        }
    }


    /**
     * 连接 获取游戏结果 socket
     */
    private void connectGameResultSocket(){
        //启动socked
        mGameResultClient = new JWebSocketClient(mContext,URI.create(HttpConsts.getGameResultUrlByType(gameId))
                ,JWebSocketClient.GAME_SOCKET_TYPE_RESULT){
            @Override
            public void onMessage(String message) {
                Log.e("TestGameSocket",LiveGameOptionDialogFragment.class.getSimpleName() +"---onMessage-message=" + message) ;

                final SocketGameReceiveBean resultBean = GsonUtil.fromJson(message, SocketGameReceiveBean.class);

                if(resultBean != null){
                    mGameNewPeroid = resultBean.getNewGamePeroid(gameId) ;
                    timeSec = StringUtil.convertInt(resultBean.getTime()) ;

                    mGameResultHandler.removeMessages(GameResultHandler.WHAT_GAME_TIME);
                    mGameResultHandler.sendEmptyMessage(GameResultHandler.WHAT_GAME_RESULT_DISPLAY) ;
                }
            }
        } ;

        Log.e("TestGameSocket",LiveGameOptionDialogFragment.class.getSimpleName() +"connectGameSocket") ;
        mGameResultClient.connect() ;
    }

    private static class GameResultHandler extends Handler {

        private LiveGameTzInfoDialogFragment mGameFrag;
        private static final int WHAT_GAME_TIME = 10;//游戏倒计时
        private static final int WHAT_GAME_RESULT_DISPLAY = 11 ;//更新UI

        public GameResultHandler(LiveGameTzInfoDialogFragment fragment) {
            mGameFrag = new WeakReference<>(fragment).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mGameFrag != null) {
                switch (msg.what) {
                    case WHAT_GAME_TIME:
                        mGameFrag.refreshGameTime() ;
                        break;
                    case WHAT_GAME_RESULT_DISPLAY:
                        mGameFrag.updateTouzhuWindowInfo();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
        }
    }


    private void refreshGameTime(){
        mGameResultHandler.sendEmptyMessageDelayed(GameResultHandler.WHAT_GAME_TIME,1000) ;

        updateGameTimeTv() ;

        if(timeSec == 0){
            mGameResultHandler.removeMessages(GameResultHandler.WHAT_GAME_TIME);
        }
        timeSec-- ;
    }

    private void updateGameTimeTv(){
        if(timeSec <= HttpConsts.GAME_STOP_IN_TIME){
            time.setText("封盘中");
        }else{
            int showTime = timeSec - HttpConsts.GAME_STOP_IN_TIME ;
            if(showTime < 10){
                time.setText("00:0" + showTime) ;
            }else{
                time.setText("00:" + showTime) ;
            }

//            time.setText("00:" + timeSec) ;
        }
    }

    /**
     * 填充内容
     */
    private void updateTouzhuWindowInfo(){
        issue.setText(gameName+"距离"+ mGameNewPeroid +"期");

        refreshGameTime() ;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if(mGameResultClient != null){
            mGameResultClient.close();
            mGameResultClient = null ;
        }

        if(mGameResultHandler != null){
            mGameResultHandler.release();
        }

        super.onDismiss(dialog);
    }
}