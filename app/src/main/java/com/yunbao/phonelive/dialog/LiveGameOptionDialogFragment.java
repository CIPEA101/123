package com.yunbao.phonelive.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.speech.utils.LogUtil;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.activity.MyCoinActivity;
import com.yunbao.phonelive.adapter.LastAwardAdapter;
import com.yunbao.phonelive.adapter.OptionAdapter;
import com.yunbao.phonelive.adapter.OptionSingleAdapter;
import com.yunbao.phonelive.event.GameResultEvent;
import com.yunbao.phonelive.game.AwardBean;
import com.yunbao.phonelive.game.GameBean;
import com.yunbao.phonelive.game.OptionBean;
import com.yunbao.phonelive.game.TzChooseBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnFragmentDismissListener;
import com.yunbao.phonelive.socket.JWebSocketClient;
import com.yunbao.phonelive.socket.SocketGameReceiveBean;
import com.yunbao.phonelive.utils.GsonUtil;
import com.yunbao.phonelive.utils.SpUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 游戏下的游戏类型
 */
public class LiveGameOptionDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private ImageView gameOp;
    private TextView tvOp;
    private ImageView refresh;
    private TextView time;
    private RecyclerView kjRecy;
    private TextView tvFrist;
    private TextView tvSecond;
    private TextView tvThird;
    private RecyclerView xxRecy;
    private TextView balance;
    private ImageView ivCharge;
    private ImageView iskIv;
    private TextView iskTv;
    private LinearLayout iskLl;
    private ImageView tzIv;

    private String mGameType ;
    private String mGameNewPeroid ;

    private int gameUri;
    private String gameName;
    private boolean mIsLiveGame = false ;//是否是主播正在直播的游戏，如果是，就不要用timer更新界面，要用直播间的数据
//    private boolean mIsShowedInfo = false ;

    List<OptionBean> gameList1 = new ArrayList<>();
    List<OptionBean> gameList2 = new ArrayList<>();
    List<OptionBean> gameList3 = new ArrayList<>();
    OptionAdapter adapter;
    OptionSingleAdapter singleAdapter;
    private boolean isTrue = false;
    private int timeSec = 0;

    private String gameId;
    private String[] strArr ;
    private String mGameTime ;

    private GameResultHandler mGameResultHandler;
    private JWebSocketClient mGameResultClient ;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_game_options;
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

        gameOp = mRootView.findViewById(R.id.iv_game_options);
        tvOp = mRootView.findViewById(R.id.tv_game_options);
        refresh = mRootView.findViewById(R.id.iv_game_refresh);
        time = mRootView.findViewById(R.id.tv_game_time);
        kjRecy = mRootView.findViewById(R.id.recycler_kj);
        tvFrist = mRootView.findViewById(R.id.tv_frist);
        tvSecond = mRootView.findViewById(R.id.tv_second);
        tvThird = mRootView.findViewById(R.id.tv_third);
        xxRecy = mRootView.findViewById(R.id.recycler_xx);
        balance = mRootView.findViewById(R.id.tv_charge_balance);
        ivCharge = mRootView.findViewById(R.id.iv_charge);
        iskIv = mRootView.findViewById(R.id.iv_isk);
        iskTv = mRootView.findViewById(R.id.tv_isk);
        iskLl = mRootView.findViewById(R.id.ll_isk);
        tzIv = mRootView.findViewById(R.id.iv_tz);

        refresh.setOnClickListener(this);
        tvFrist.setOnClickListener(this);
        tvSecond.setOnClickListener(this);
        tvThird.setOnClickListener(this);
        ivCharge.setOnClickListener(this);
        iskLl.setOnClickListener(this);
        tzIv.setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mIsLiveGame = bundle.getBoolean(Constants.LIVE_GAME_IS_LIVE,false) ;
            gameUri = bundle.getInt(Constants.LIVE_GAME_IGAMEURL);
            gameName = bundle.getString(Constants.LIVE_GAME_NAME);
            gameOp.setBackgroundResource(gameUri);
            tvOp.setText(gameName);
        }

        if(mIsLiveGame){
            refresh.setVisibility(View.GONE);
        }

        LogUtil.e(LiveGameOptionDialogFragment.class.getSimpleName(),"init Bundle ,gameUri=" + gameUri) ;

        switch (gameUri) {
            case R.mipmap.icon_ks_game:
                tvFrist.setText("总和");
                tvSecond.setText("三军");
                tvThird.setText("短牌");

                mGameType = "1" ;
                getYfInfo(HttpConsts.GET_YFKS_INFO, HttpConsts.GAME_YFKS_INFO);
                break;
            case R.mipmap.icon_syxw_game:
                tvFrist.setText("总和");
                tvSecond.setText("第一球两面");
                tvThird.setText("全五中一");

                mGameType = "2" ;
                getYfInfo(HttpConsts.GET_YF115_INFO, HttpConsts.GAME_YF115_INFO);
                break;
            case R.mipmap.icon_syf_game:
                tvFrist.setText("冠军单码");
                tvSecond.setText("冠亚和");
                tvThird.setText("冠军两面");

                mGameType = "3" ;
                getYfInfo(HttpConsts.GET_YFSC_INFO, HttpConsts.GAME_YFSC_INFO);
                break;
            case R.mipmap.icon_ssc_game:
                tvFrist.setText("第一球两面");
                tvSecond.setText("第一球VS第五球");
                tvThird.setText("全5中1");

                mGameType = "4" ;
                getYfInfo(HttpConsts.GET_YFSSC_INFO, HttpConsts.GAME_YFSSC_INFO);
                break;
            case R.mipmap.icon_yfl_game:
                tvFrist.setText("特码两面");
                tvSecond.setText("特码生肖");
                tvThird.setText("特码色波");

                mGameType = "5" ;
                getYfInfo(HttpConsts.GET_YFLH_INFO, HttpConsts.GAME_YFLHC_INFO);
                break;
            case R.mipmap.icon_ten_game:
                tvFrist.setText("总和");
                tvSecond.setText("第一球两面");
                tvThird.setText("第八球两面");

                mGameType = "6" ;
                getYfInfo(HttpConsts.GET_YFKLSF_INFO, HttpConsts.GAME_YFKLSF_INFO);
                break;
            case R.mipmap.icon_xfnc_game:
                tvFrist.setText("总和");
                tvSecond.setText("第一球两面");
                tvThird.setText("第八球两面");

                mGameType = "7" ;
                getYfInfo(HttpConsts.GET_YFXYNC_INFO, HttpConsts.GAME_YFKLNC_INFO);
                break;
            default:
                tvFrist.setText("总和");
                tvSecond.setText("三军");
                tvThird.setText("短牌");

                mGameType = "1" ;
                getYfInfo(HttpConsts.GET_YFKS_INFO, HttpConsts.GAME_YFKS_INFO);
                break;
        }
        String str = SpUtil.getInstance().getStringValue("iskIv");
        if (str != null && !str.equals("")) {
            int imageUrl = Integer.parseInt(str);
            iskIv.setBackgroundResource(imageUrl == 0 ? R.mipmap.icon_game_chip_two : imageUrl);
            iskTv.setText(imageUrl == 0 ? "2" : SpUtil.getInstance().getStringSValue("iskTv"));

            boolean isCus = SpUtil.getInstance().getBooleanValue("iskCus") ;
            iskTv.setVisibility(isCus ? View.VISIBLE : View.GONE);
            iskIv.setVisibility(isCus ? View.GONE : View.VISIBLE) ;
        }else{
            iskIv.setBackgroundResource(R.mipmap.icon_game_chip_two);
            iskTv.setText("2");
        }
        loadNewData();
        connectGameResultSocket() ;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_game_refresh:
                LiveGameDialogFragment fragment = new LiveGameDialogFragment();
                List<Fragment> fragments = ((LiveActivity) mContext).getSupportFragmentManager().getFragments();
                for (Fragment fragment1 : fragments) {
                    ((LiveActivity) mContext).getSupportFragmentManager().beginTransaction().remove(fragment1).commit();
                }
                fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveGameDialogFragment");
                break;
            case R.id.tv_frist:
                setColumn(gameList1.size(), gameList1,1);
                tvFrist.setBackgroundColor(mContext.getResources().getColor(R.color.rgb8421F4));
                tvSecond.setBackgroundColor(mContext.getResources().getColor(R.color.rgb262628));
                tvThird.setBackgroundColor(mContext.getResources().getColor(R.color.rgb262628));
                break;
            case R.id.tv_second:
                setColumn(gameList2.size(), gameList2,2);
                tvFrist.setBackgroundColor(mContext.getResources().getColor(R.color.rgb262628));
                tvSecond.setBackgroundColor(mContext.getResources().getColor(R.color.rgb8421F4));
                tvThird.setBackgroundColor(mContext.getResources().getColor(R.color.rgb262628));
                break;
            case R.id.tv_third:
                setColumn(gameList3.size(), gameList3,3);
                tvFrist.setBackgroundColor(mContext.getResources().getColor(R.color.rgb262628));
                tvSecond.setBackgroundColor(mContext.getResources().getColor(R.color.rgb262628));
                tvThird.setBackgroundColor(mContext.getResources().getColor(R.color.rgb8421F4));
                break;
            case R.id.iv_charge:
                MyCoinActivity.forward(mContext);
                break;
            case R.id.ll_isk://筹码
                LiveGameChipDialogFragment chipFragment = new LiveGameChipDialogFragment();
                chipFragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), "LiveGameChipDialogFragment");
                chipFragment.setCallBack(new LiveGameChipDialogFragment.CallBack() {
                    @Override
                    public void onClick(GameBean bean) {
                        int value = bean.getValue() ;
                        if(bean.isCustom()){
                            value = bean.getCustomValue() ;
                        }

                        iskIv.setBackgroundResource(bean.getImageId());
                        iskTv.setText(String.valueOf(value));

                        iskTv.setVisibility(bean.isCustom() ? View.VISIBLE : View.GONE);
                        iskIv.setVisibility(bean.isCustom() ? View.GONE : View.VISIBLE) ;
                    }
                });
                break;
            case R.id.iv_tz:
                junpToTzGame();
                break;
        }
    }

    private void junpToTzGame(){
        if(timeSec <= HttpConsts.GAME_STOP_IN_TIME){
            ToastUtil.show("封盘中");
            return;
        }
        ArrayList<TzChooseBean> touzhuList = new ArrayList<>() ;
        if(!isTrue){
            touzhuList.addAll(adapter.getLists()) ;
        }else{
            if (singleAdapter != null) {
                touzhuList.addAll(singleAdapter.getLists()) ;
            }
        }

        if(touzhuList.size() == 0){
            ToastUtil.show("请选择投注内容");
            return;
        }

        LiveGameTzDialogFragment tzFragment = new LiveGameTzDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("saveInfo",touzhuList);
        bundle.putString(Constants.LIVE_GAME_ID,gameId);
        bundle.putString(Constants.LIVE_GAME_NAME_TZ,gameName);
        bundle.putInt(Constants.LIVE_GAME_TIMESEC,timeSec);

//        tzFragment.setOnFragmentDismissListener(new OnFragmentDismissListener() {
//            @Override
//            public void onFragmentDismiss() {
//                dismiss() ;
//            }
//        });

        tzFragment.setArguments(bundle);
        tzFragment.show(((LiveAudienceActivity) mContext).getSupportFragmentManager(), HttpConsts.LIVE_GAME_TZ_DIALOG_FRAGMENT);
    }

    private void getYfInfo(String url, String tag) {
        HttpUtil.gameYfInfo(url, tag, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                String arrStr = Arrays.toString(info);
                if (code == 0) {
                    List<OptionBean> list = JSON.parseArray(arrStr, OptionBean.class);
                    for (int i = 0; i < list.size(); i++) {
                        switch (list.get(i).getType()) {
                            case "1":
                                gameList1.add(list.get(i));
                                break;
                            case "2":
                                gameList2.add(list.get(i));
                                break;
                            case "3":
                                gameList3.add(list.get(i));
                                break;
                        }
                    }
                    setColumn(gameList1.size(), gameList1,1);
                }
            }
        });
    }

    private void setColumn(int size,final List<OptionBean> gameList,int type) {
        String strType = "";
        switch (type){
            case 1:
                strType = tvFrist.getText().toString();
                break;
            case 2:
                strType = tvSecond.getText().toString();
                break;
            case 3:
                strType = tvThird.getText().toString();
                break;
        }
        if (size <= 4) {
            isTrue = true;
            xxRecy.setLayoutManager(new GridLayoutManager(mContext, size, GridLayoutManager.VERTICAL, false));
            singleAdapter = new OptionSingleAdapter(mContext, getLayoutInflater(),gameName+"-"+strType,iskTv.getText().toString(), gameList);
            xxRecy.setAdapter(singleAdapter);
        } else {
            isTrue = false;
            xxRecy.setLayoutManager(new GridLayoutManager(mContext, (size+1)/2, GridLayoutManager.VERTICAL, false));
            adapter = new OptionAdapter(mContext, getLayoutInflater(),gameName+"-"+strType,iskTv.getText().toString(), gameList);
            xxRecy.setAdapter(adapter);
        }
    }

    public void initYfksLastAward(String[] strArr,String timeStr,String gameId) {
        Log.e(LiveGameOptionDialogFragment.class.getSimpleName(),"initYfksLastAward uri=" + gameUri) ;
        this.gameId = gameId;

        if(gameUri == R.mipmap.icon_ks_game) {

            Log.e(LiveGameOptionDialogFragment.class.getSimpleName(),"initYfksLastAward success time=" + timeStr) ;

            List<AwardBean> list = new ArrayList<>();
            int imgeUrl = 0;
            for (String str : strArr) {
                if (str.equals("1")) {
                    imgeUrl = R.mipmap.icon_game_dice_one;
                } else if (str.equals("2")) {
                    imgeUrl = R.mipmap.icon_game_dice_two;
                } else if (str.equals("3")) {
                    imgeUrl = R.mipmap.icon_game_dice_three;
                } else if (str.equals("4")) {
                    imgeUrl = R.mipmap.icon_game_dice_four;
                } else if (str.equals("5")) {
                    imgeUrl = R.mipmap.icon_game_dice_five;
                } else if (str.equals("6")) {
                    imgeUrl = R.mipmap.icon_game_dice_six;
                }
                list.add(new AwardBean(imgeUrl));
            }
            kjRecy.setHasFixedSize(true);
            kjRecy.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
            LastAwardAdapter adapter = new LastAwardAdapter(mContext, getLayoutInflater(), list, 1);
            kjRecy.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            timeSec = Integer.parseInt(timeStr);
            setTimer();
        }
    }

    public void initYf115LastAward(String[] strArr,String timeStr,String gameId) {
        Log.e(LiveGameOptionDialogFragment.class.getSimpleName(),"initYf115LastAward");
        this.gameId = gameId;
        if (gameUri == R.mipmap.icon_syxw_game || gameUri == R.mipmap.icon_ssc_game || gameUri == R.mipmap.icon_ten_game) {
            List<AwardBean> list = new ArrayList<>();
            for (String str : strArr) {
                list.add(new AwardBean(str));
            }
            kjRecy.setHasFixedSize(true);
            kjRecy.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
            LastAwardAdapter adapter = new LastAwardAdapter(mContext, getLayoutInflater(), list, 2);
            kjRecy.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            time.setText(timeStr);
            timeSec = Integer.parseInt(timeStr);
            setTimer();
        }
    }

    public void initYflhcLastAward(String[] strArr,String timeStr,String gameId) {
        Log.e(LiveGameOptionDialogFragment.class.getSimpleName(),"initYflhcLastAward");
        this.gameId = gameId;
        if(gameUri == R.mipmap.icon_yfl_game) {
            List<AwardBean> list = new ArrayList<>();
            for (int i = 0; i < strArr.length; i++) {
                if (i == strArr.length - 1) {
                    //加号
                    list.add(new AwardBean("",R.mipmap.ic_game_point_add_white));
//                    list.add(new AwardBean("+",R.drawable.bg_game_point_add));
                }
                String number = strArr[i] ;
                list.add(new AwardBean(number,AwardBean.getItemBackgroundByName(number))) ;
            }

            kjRecy.setHasFixedSize(true);
            kjRecy.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
            LastAwardAdapter adapter = new LastAwardAdapter(mContext, getLayoutInflater(), list, 3);
            kjRecy.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            time.setText(timeStr);
            timeSec = Integer.parseInt(timeStr);
            setTimer();
        }
    }

    public void initYfscLastAward(String[] strArr, String timeStr, String gameId) {
        Log.e(LiveGameOptionDialogFragment.class.getSimpleName(),"initYfscLastAward");
        this.gameId = gameId;
        if(gameUri == R.mipmap.icon_syf_game) {

            Log.e(LiveGameOptionDialogFragment.class.getSimpleName(),"initYfscLastAward is true");

            List<AwardBean> list = new ArrayList<>();
            for (String str : strArr){
                list.add(new AwardBean(str));
            }
            kjRecy.setHasFixedSize(true);
            kjRecy.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
            LastAwardAdapter adapter = new LastAwardAdapter(mContext, getLayoutInflater(), list, 4);
            kjRecy.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            time.setText(timeStr);
            timeSec = Integer.parseInt(timeStr);
            setTimer();
        }
    }

    public void initYfklncLastAward(String[] strArr,String timeStr,String gameId){
        Log.e(LiveGameOptionDialogFragment.class.getSimpleName(),"initYfklncLastAward");
        this.gameId = gameId;
        if(gameUri == R.mipmap.icon_xfnc_game) {
            List<AwardBean> list = new ArrayList<>();
            int imgeUrl = 0;
            for (String str : strArr) {
                if (str.equals("1")) {
                    imgeUrl = R.mipmap.cqxync01;
                } else if (str.equals("2")) {
                    imgeUrl = R.mipmap.cqxync02;
                } else if (str.equals("3")) {
                    imgeUrl = R.mipmap.cqxync03;
                } else if (str.equals("4")) {
                    imgeUrl = R.mipmap.cqxync04;
                } else if (str.equals("5")) {
                    imgeUrl = R.mipmap.cqxync05;
                } else if (str.equals("6")) {
                    imgeUrl = R.mipmap.cqxync06;
                } else if(str.equals("7")){
                    imgeUrl = R.mipmap.cqxync07;
                }else if (str.equals("8")) {
                    imgeUrl = R.mipmap.cqxync08;
                } else if (str.equals("9")) {
                    imgeUrl = R.mipmap.cqxync09;
                } else if (str.equals("10")) {
                    imgeUrl = R.mipmap.cqxync10;
                } else if (str.equals("11")) {
                    imgeUrl = R.mipmap.cqxync11;
                } else if (str.equals("12")) {
                    imgeUrl = R.mipmap.cqxync12;
                } else if(str.equals("13")){
                    imgeUrl = R.mipmap.cqxync13;
                }else if (str.equals("14")) {
                    imgeUrl = R.mipmap.cqxync14;
                } else if (str.equals("15")) {
                    imgeUrl = R.mipmap.cqxync15;
                } else if (str.equals("16")) {
                    imgeUrl = R.mipmap.cqxync16;
                } else if (str.equals("17")) {
                    imgeUrl = R.mipmap.cqxync17;
                } else if (str.equals("18")) {
                    imgeUrl = R.mipmap.cqxync18;
                } else if(str.equals("19")){
                    imgeUrl = R.mipmap.cqxync19;
                }else if (str.equals("20")){
                    imgeUrl = R.mipmap.cqxync20;
                }
                list.add(new AwardBean(imgeUrl));
            }
            kjRecy.setHasFixedSize(true);
            kjRecy.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
            LastAwardAdapter adapter = new LastAwardAdapter(mContext, getLayoutInflater(), list, 1);
            kjRecy.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            time.setText(timeStr);
            timeSec = Integer.parseInt(timeStr);
            setTimer();
        }
    }

    private void loadNewData() {
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    balance.setText(StringUtil.converMoney2Point(obj.getString("coin")));
                }
            }
        });
    }


    private void refreshGameTime(){
        EventBus.getDefault().post(new GameResultEvent(true,mGameType,mGameNewPeroid,timeSec)) ;

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

    private void setTimer() {
        refreshGameTime() ;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if(mGameResultHandler != null){
            mGameResultHandler.release() ;
        }

        if(mGameResultClient != null){
            if(mGameResultClient.isOpen()){
                mGameResultClient.close();
            }
            mGameResultClient = null ;
        }
    }


    /**
     * 填充内容
     */
    private void updateTouzhuWindowInfo(){
        LogUtil.e(LiveGameOptionDialogFragment.class.getSimpleName(),"updateTouzhuWindowInfo,gamePeroid=" + mGameNewPeroid + ",gameUri=" + gameUri) ;

        EventBus.getDefault().post(new GameResultEvent(false,mGameType,mGameNewPeroid, StringUtil.convertInt(mGameTime))) ;

        String gameUrl = HttpConsts.getGameResultUrlByType(mGameType) ;
        switch (gameUrl) {
            case HttpConsts.SOCKET_YFSC_URL:
                initYfscLastAward(strArr, mGameTime, mGameNewPeroid);
                break;
            case HttpConsts.SOCKET_YFKS_URL:
                initYfksLastAward(strArr, mGameTime, mGameNewPeroid);
                break;
            case HttpConsts.SOCKET_YFLHC_URL:
                initYflhcLastAward(strArr,mGameTime, mGameNewPeroid);
                break;
            case HttpConsts.SOCKET_YF115_URL:
                initYf115LastAward(strArr, mGameTime, mGameNewPeroid);
                break;
            case HttpConsts.SOCKET_YFSSC_URL:
                initYf115LastAward(strArr, mGameTime, mGameNewPeroid);
                break;
            case HttpConsts.SOCKET_YFKLSF_URL:
                initYf115LastAward(strArr, mGameTime, mGameNewPeroid);
                break;
            case HttpConsts.SOCKET_YFKLNC_URL:
                initYfklncLastAward(strArr, mGameTime, mGameNewPeroid);
                break;
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void updateGameInfo(GameResultEvent event){
//        if(mIsLiveGame){
//            boolean isOnlyTime = event.isOnlyTime() ;
//            if(!mIsShowedInfo || !isOnlyTime){
//                if(!mIsShowedInfo){
//                    mIsShowedInfo = true ;
//                }
//
//                updateTouzhuWindowInfo(event.getGameType(),event.getNewPeriod(),event.getGameResult(),event.getTime()) ;
//            }
//
//            if(isOnlyTime){
//                sendTime = Integer.parseInt(event.getTime()) ;
//                if (sendTime > 10) {
//                    time.setText("00分" + sendTime + "秒");
//                }else{
//                    time.setText("封盘中");
//                }
//            }
//        }
//    }


    /**
     * 连接 获取游戏结果 socket
     */
    private void connectGameResultSocket(){
        //启动socked
        mGameResultClient = new JWebSocketClient(mContext,URI.create(HttpConsts.getGameResultUrlByType(mGameType)),JWebSocketClient.GAME_SOCKET_TYPE_RESULT){
            @Override
            public void onMessage(String message) {
                Log.e("TestGameSocket",LiveGameOptionDialogFragment.class.getSimpleName() +"---onMessage-message=" + message) ;

                final SocketGameReceiveBean resultBean = GsonUtil.fromJson(message, SocketGameReceiveBean.class);

                if(resultBean != null){
                    String dianshu = resultBean.getDianshu_result();
                    if(dianshu != null && dianshu.contains(",")){
                        strArr = dianshu.split(",");
                    }
                    mGameNewPeroid = resultBean.getNewGamePeroid(mGameType) ;
                    mGameTime = resultBean.getTime() ;

                    mGameResultHandler.removeMessages(GameResultHandler.WHAT_GAME_TIME);
                    mGameResultHandler.sendEmptyMessage(GameResultHandler.WHAT_GAME_RESULT_DISPLAY) ;
                }
            }
        } ;

        Log.e("TestGameSocket",LiveGameOptionDialogFragment.class.getSimpleName() +"connectGameSocket") ;
        mGameResultClient.connect() ;
    }

    private static class GameResultHandler extends Handler {

        private LiveGameOptionDialogFragment mGameOptionFrag;
        private static final int WHAT_GAME_TIME = 10;//游戏倒计时
        private static final int WHAT_GAME_RESULT_DISPLAY = 11 ;//更新UI

        public GameResultHandler(LiveGameOptionDialogFragment fragment) {
            mGameOptionFrag = new WeakReference<>(fragment).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mGameOptionFrag != null) {
                switch (msg.what) {
                    case WHAT_GAME_TIME:
                        mGameOptionFrag.refreshGameTime() ;
                        break;
                    case WHAT_GAME_RESULT_DISPLAY:
                        mGameOptionFrag.updateTouzhuWindowInfo();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
        }
    }
}