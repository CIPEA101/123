package com.yunbao.phonelive.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.yunbao.phonelive.adapter.TzChooseAdapter;
import com.yunbao.phonelive.event.GameResultEvent;
import com.yunbao.phonelive.game.TzChooseBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnFragmentDismissListener;
import com.yunbao.phonelive.socket.JWebSocketClient;
import com.yunbao.phonelive.utils.SpUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URI;
import java.util.ArrayList;

/**
 * 确认投注
 */
public class LiveGameTzDialogFragment extends AbsDialogFragment implements View.OnClickListener {

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
    private TzChooseAdapter adapter;
    private int num = 0;
    private String gameId;
    private String bl="1";
    private String gameName;
    private ArrayList<TzChooseBean> list = new ArrayList<>();

    private boolean mIsShowedInfo = false ;
    private int sendTime ;

    private JWebSocketClient socketClient ;

    private OnFragmentDismissListener mFragDismissListener ;
    public void setOnFragmentDismissListener(OnFragmentDismissListener listener){
        this.mFragDismissListener = listener ;
    }

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

        EventBus.getDefault().register(this);

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
            list = bundle.getParcelableArrayList("saveInfo");

            recycler.setLayoutManager(new LinearLayoutManager(mContext,GridLayoutManager.VERTICAL, false));
            for(TzChooseBean item : list){
                item.setTvDou("1");
            }
            adapter = new TzChooseAdapter(mContext,getLayoutInflater(),list);
            recycler.setAdapter(adapter);

            gameName = bundle.getString(Constants.LIVE_GAME_NAME_TZ);
            gameId = bundle.getString(Constants.LIVE_GAME_ID);
            sendTime = bundle.getInt(Constants.LIVE_GAME_TIMESEC,0) ;

            updateTimeTvDisplay() ;

            adapter.setOnItemClick(new TzChooseAdapter.OnItemClick() {
                @Override
                public void onClickCancel(int postion) {
                    list.remove(postion);
                    adapter.notifyDataSetChanged();

                    injection.setText(String.valueOf(list.size()));
                    if(list.size() > 0){
                        int size = Integer.parseInt(list.get(0).getTvDou())*num*list.size();
                        totalInject.setText(String.valueOf(size));
                    }else {
                        totalInject.setText("0");
                    }
                }
            });

            String chap = SpUtil.getInstance().getStringValue("iskTv") ;
            if(TextUtils.isEmpty(chap)){
                SpUtil.getInstance().setStringValue("iskTv","1") ;
            }

            num = Integer.parseInt(SpUtil.getInstance().getStringSValue("iskTv"));
            if (list != null && list.size() > 0) {
                injection.setText(String.valueOf(list.size()));
                totalInject.setText(String.valueOf(num * list.size()));
            }else{
                injection.setText("0");
                totalInject.setText("0");
            }
        }

        URI uri = URI.create(HttpConsts.SOCKET_GAME_PUSH_URL);
        socketClient = new JWebSocketClient(mContext,uri,JWebSocketClient.GAME_SOCKET_TYPE_TOUZHU);
        socketClient.connect();

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
                    totalInject.setText(String.valueOf(num * list.size()));
                    for(TzChooseBean item : list){
                        item.setTvDou("1");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "1";
                break;
            case R.id.btn_two_inject:
                setLight(1);
                if (list != null && list.size()> 0){
                    totalInject.setText(String.valueOf(num * list.size()*2));
                    for(TzChooseBean item : list){
                        item.setTvDou("2");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "2";
                break;
            case R.id.btn_five_inject:
                setLight(2);
                if (list != null && list.size()> 0){
                    totalInject.setText(String.valueOf(num * list.size()*5));
                    for(TzChooseBean item : list){
                        item.setTvDou("5");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "5";
                break;
            case R.id.btn_ten_inject:
                setLight(3);
                if (list != null && list.size()> 0){
                    totalInject.setText(String.valueOf(num * list.size()*10));
                    for(TzChooseBean item : list){
                        item.setTvDou("10");
                    }
                    adapter.notifyDataSetChanged();
                }
                bl = "10";
                break;
            case R.id.btn_twenty_inject:
                setLight(4);
                if (list != null && list.size()> 0){
                    totalInject.setText(String.valueOf(num * list.size()*20));
                    for(TzChooseBean item : list){
                        item.setTvDou("20");
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
                setBackgroundChecked(one);
                setBackgroundUnCheck(two);
                setBackgroundUnCheck(five);
                setBackgroundUnCheck(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 1:
                setBackgroundUnCheck(one);
                setBackgroundChecked(two);
                setBackgroundUnCheck(five);
                setBackgroundUnCheck(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 2:
                setBackgroundUnCheck(one);
                setBackgroundUnCheck(two);
                setBackgroundChecked(five);
                setBackgroundUnCheck(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 3:
                setBackgroundUnCheck(one);
                setBackgroundUnCheck(two);
                setBackgroundUnCheck(five);
                setBackgroundChecked(ten);
                setBackgroundUnCheck(twenty);
                break;
            case 4:
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

    private void getXzXync(){
        if(sendTime <= HttpConsts.GAME_STOP_IN_TIME){
            ToastUtil.show("封盘中");
            return;
        }

        if (list != null && list.size() > 0){
            StringBuffer strSend = new StringBuffer();
            for (int i = 0;i < list.size(); i++){
                String name = list.get(i).getItem().getName() ;

                if (gameName.equals(HttpConsts.GAME_NAME_YFKS)) {
                    //短牌需要把name里面的逗号隔开
                    if("3".equals(list.get(i).getItem().getType())){
                        if(name.length() > 1){
                            name = name.substring(0,1) ;
                        }
                    }
                    strSend.append("1");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YF115)){
                    strSend.append("2");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFSC)){
                    strSend.append("3");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFSSC)){
                    strSend.append("4");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFLHC)){
                    strSend.append("5");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFKLSF)){
                    strSend.append("6");
                }else if(gameName.equals(HttpConsts.GAME_NAME_YFXYNC)){
                    strSend.append("7");
                }

                strSend.append("#"+gameId
                        +"#"+ AppConfig.getInstance().getUid()
                        +"#"+list.get(i).getItem().getType()
                        +"#"+name
                        +"#"+num
                        +"#"+list.get(i).getItem().getRatio()
                        +"#"+bl+"@");
            }
            if (strSend.toString().contains("@")){
                strSend.deleteCharAt(strSend.length()-1);
            }

            LogUtil.e("TestTouzhu","param=" + strSend.toString()) ;

            getSocket(strSend.toString());
        }
    }
    private void getSocket(String str){
        if(socketClient.isOpen()){
            socketClient.setData(gameName,totalInject.getText().toString());
            socketClient.send(str);

            dismiss();

            if(mFragDismissListener != null){
                mFragDismissListener.onFragmentDismiss();
            }
        }
    }
    /**
     * 填充内容
     * @param gameType gameType
     * @param newGamePeriod 下一期游戏期数id，其实就是当前投注时的游戏期数id，但是属于当前结果的下一期
     */
    private void updateWindowInfo(String gameType,String newGamePeriod){
        gameId = newGamePeriod ;

        String gameName = HttpConsts.getGameNameByType(gameType) ;
        issue.setText(gameName+"距离"+ newGamePeriod+"期");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateGameInfo(GameResultEvent event){
        boolean isOnlyTime = event.isOnlyTime() ;
        if(!mIsShowedInfo || !isOnlyTime){
            if(!mIsShowedInfo){
                mIsShowedInfo = true ;
            }
            updateWindowInfo(event.getGameType(),event.getNewPeriod()) ;
        }

        if(isOnlyTime){
            sendTime = event.getTime() ;
            updateTimeTvDisplay() ;
        }
    }

    private void updateTimeTvDisplay(){
        if (sendTime > HttpConsts.GAME_STOP_IN_TIME) {
            int showTime = sendTime - HttpConsts.GAME_STOP_IN_TIME ;
            if(showTime < 10){
                time.setText("00分0" + showTime+ "秒") ;
            }else{
                time.setText("00分" + showTime );
            }

//            time.setText("00分" + sendTime + "秒");
        }else{
            time.setText("封盘中");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        EventBus.getDefault().unregister(this);
    }
}