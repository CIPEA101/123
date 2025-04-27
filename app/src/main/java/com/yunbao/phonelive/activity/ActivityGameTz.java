package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.AdapterGameResult;
import com.yunbao.phonelive.adapter.AdapterGameTzChip;
import com.yunbao.phonelive.adapter.ViewPagerAdapter;
import com.yunbao.phonelive.bean.GameTzChip;
import com.yunbao.phonelive.dialog.LiveGameTzDialogFragment;
import com.yunbao.phonelive.event.GameResultEvent;
import com.yunbao.phonelive.event.GameTzEvent;
import com.yunbao.phonelive.game.AwardBean;
import com.yunbao.phonelive.game.OptionBean;
import com.yunbao.phonelive.game.TzChooseBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.keyboard.KeyboardHeightObserver;
import com.yunbao.phonelive.socket.JWebSocketClient;
import com.yunbao.phonelive.socket.SocketGameReceiveBean;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.GsonUtil;
import com.yunbao.phonelive.utils.RandomUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.SoftKeyBoardListener;
import com.yunbao.phonelive.utils.SpUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;
import com.yunbao.phonelive.views.ViewGameTzHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 投注
 */
public class ActivityGameTz extends AbsActivity implements View.OnClickListener, KeyboardHeightObserver {

    private MagicIndicator mIndicator ;
    private ViewPager mViewPager ;

    private TextView mLastPeriodTv ;
    private TextView mLatestPeriodTv ;
    private TextView mLatestPeriodTipsTv ;

    private RecyclerView mGameResultRv ;

    private TextView mTime1000Tv ;
    private TextView mTime100Tv ;
    private TextView mTime10Tv ;//剩余时间的十位秒
    private TextView mTime1Tv ;//剩余时间的各位秒

    private TextView mYueTv ;
    private TextView mCheckedCountTv ;
    private TextView mCheckedMoneyTv ;
    private EditText mChipCountEv ;//筹码

    private List<GameTzChip> mChipList = new ArrayList<>() ;

    private String mGameId ;
    private String mRequestTag;

    protected List<String> mTitleList ;

    private List<OptionBean> mTotalOptionList ;//总的
    private List<OptionBean> mOptionList1 ;
    private List<OptionBean> mOptionList2 ;
    private List<OptionBean> mOptionList3 ;
    private List<ViewGameTzHolder> mViewList ;

    private int mCheckCount = 0 ;

    //游戏结果
    private GameResultHandler mGameResultHandler;
    private JWebSocketClient mGameResultClient ;

    private String mDianshuResult ;
    private String mGameNewPeroid ;
    private String mGameTime ;
    private int timeSec = 0;

    private boolean mIsSoftShow = false ;//输入法是否显示了

    private boolean mIsFirstShow = true ;

    //更多
    private View mMoreLay ;
    private PopupWindow mMorePop ;

    private Dialog mGameRuleDialog ;

    //筹码
    private PopupWindow mChipPop ;
    private TextView mChipPopYueTv ;
    private TextView mChipPopCountTv ;
    private TextView mChipPopMoneyTv ;
    private EditText mChipPopCountEv ;
    private RecyclerView mChipPopRv ;

    private InputMethodManager imm;

    private boolean mIsPaused = false ;


    public static void launch(Context context,String gameId){
        Intent toIt = new Intent(context,ActivityGameTz.class) ;
        toIt.putExtra(Constants.LIVE_GAME_ID,gameId) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_tz ;
    }

    @Override
    protected void main() {
        super.main();

        EventBus.getDefault().register(this);

        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        mGameId = getIntent().getStringExtra(Constants.LIVE_GAME_ID) ;

        TextView mTitleTv = findViewById(R.id.act_game_tz_title_tv) ;
        mTitleTv.setText(HttpConsts.getGameNameByType(mGameId)) ;

        mMoreLay = findViewById(R.id.act_game_tz_top_more_lay) ;
        mMoreLay.setOnClickListener(this);

        mIndicator = findViewById(R.id.act_game_tz_magic_indicator) ;
        mViewPager = findViewById(R.id.act_game_tz_vp) ;

        mGameResultRv = findViewById(R.id.act_game_tz_last_result_rv) ;

        View resetIv = findViewById(R.id.act_game_tz_reset_lay) ;
        View autoIv = findViewById(R.id.act_game_tz_auto_lay) ;
        View submitTv = findViewById(R.id.act_game_tz_submit_tv) ;
        submitTv.setOnClickListener(this);
        autoIv.setOnClickListener(this);
        resetIv.setOnClickListener(this);

        mLastPeriodTv = findViewById(R.id.act_game_tz_last_period_tv) ;
        mLatestPeriodTv = findViewById(R.id.act_game_tz_latest_period_tv) ;
        mLatestPeriodTipsTv = findViewById(R.id.act_game_tz_latest_period_tips_tv) ;
        mTime1000Tv = findViewById(R.id.act_game_tz_time_1000_tv) ;
        mTime100Tv = findViewById(R.id.act_game_tz_time_100_tv) ;
        mTime10Tv = findViewById(R.id.act_game_tz_time_10_tv) ;
        mTime1Tv = findViewById(R.id.act_game_tz_time_1_tv) ;

        mYueTv = findViewById(R.id.act_game_tz_yue_tv) ;
        mCheckedCountTv = findViewById(R.id.act_game_tz_check_count_tv) ;
        mCheckedMoneyTv = findViewById(R.id.act_game_tz_money_tv) ;
        mChipCountEv = findViewById(R.id.act_game_tz_chip_ev) ;
        mChipCountEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(StringUtil.convertInt(charSequence.toString()) > 10000){
                    ToastUtil.show("筹码不能大于10000") ;
                    mChipCountEv.setText("10000") ;
                    mChipCountEv.setSelection(5) ;
                }else{
                    updateCheckMoney() ;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        initChipPop() ;

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mIsSoftShow = true ;
                showChipView() ;
            }
            @Override
            public void keyBoardHide(int height) {
                mIsSoftShow = false ;
                showChipView() ;
            }
        });



        getYfInfo() ;
        getYueInfo() ;

        //开启结果sotcket
        mGameResultHandler = new GameResultHandler(this);
        connectGameResultSocket() ;
    }

    private void initChipPop(){
        mChipPop = new PopupWindow(mContext) ;
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_game_tz_chip_lay,null) ;
        mChipPop.setContentView(contentView);

        mChipPop.setWidth(ScreenDimenUtil.getInstance().getScreenWdith()) ;
        mChipPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mChipPop.setBackgroundDrawable(new BitmapDrawable()) ;
        mChipPop.setOutsideTouchable(false);
        mChipPop.setTouchable(true);
        mChipPop.setFocusable(true);
//            mChipPop.setAnimationStyle(R.style.bottomToTopAnim);//不要动画

        mChipPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if(imm!=null){
                    imm.hideSoftInputFromWindow(mChipPopCountEv.getWindowToken(), 1);
                }

                mChipPopRv.setVisibility(View.GONE) ;
            }
        });

        View emptyLay = contentView.findViewById(R.id.pop_game_tz_chip_empty_lay) ;
        emptyLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        View resetIv = contentView.findViewById(R.id.pop_game_tz_reset_lay) ;
        View autoIv = contentView.findViewById(R.id.pop_game_tz_auto_lay) ;
        View submitTv = contentView.findViewById(R.id.pop_game_tz_submit_tv) ;
        submitTv.setOnClickListener(this);
        autoIv.setOnClickListener(this);
        resetIv.setOnClickListener(this);

        mChipPopYueTv = contentView.findViewById(R.id.pop_game_tz_yue_tv) ;
        mChipPopCountTv = contentView.findViewById(R.id.pop_game_tz_check_count_tv) ;
        mChipPopMoneyTv = contentView.findViewById(R.id.pop_game_tz_money_tv) ;
        mChipPopCountEv = contentView.findViewById(R.id.pop_game_tz_chip_ev) ;
        mChipPopCountEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(StringUtil.convertInt(charSequence.toString()) > 10000){
                    mChipPopCountEv.setText("10000") ;
                    mChipPopCountEv.setSelection(5) ;
                }

                mChipCountEv.setText(charSequence) ;
                mChipCountEv.setSelection(charSequence.length());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mChipPopRv = contentView.findViewById(R.id.pop_game_tz_chip_rv) ;
        mChipPopRv.setNestedScrollingEnabled(false);
        mChipPopRv.setFocusable(false);

        mChipList.addAll(GameTzChip.getDefaultChipList()) ;

        AdapterGameTzChip chipAdapter = new AdapterGameTzChip(mContext, mChipList, new OnRvClickListener() {
            @Override
            public void onClick(int type, int position) {
                if(0 == type){
                    if(position >= 0 && position < mChipList.size()){
                        String value = mChipList.get(position).getChipValue() ;
                        mChipPopCountEv.setText(value) ;
                        mChipPopCountEv.setSelection(value.length()) ;
                    }
                }
            }
        }) ;
        mChipPopRv.setLayoutManager(new GridLayoutManager(mContext,mChipList.size())) ;
        mChipPopRv.setAdapter(chipAdapter) ;
    }

    /**
     * 显示筹码
     */
    private void showChipPop(){
        mChipPopRv.setVisibility(View.VISIBLE) ;
        mChipPop.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM,0,0) ;
    }

    /**
     * 填充内容
     */
    private void updateDisplay( List<OptionBean> list){
        if(null == list || list.size() == 0){
            return;
        }

        mTotalOptionList = new ArrayList<>() ;
        mTotalOptionList.addAll(list) ;

        mOptionList1 = new ArrayList<>() ;
        mOptionList2 = new ArrayList<>() ;
        mOptionList3 = new ArrayList<>() ;
        for (int i = 0; i < list.size(); i++) {
            switch (list.get(i).getType()) {
                case "1":
                    mOptionList1.add(list.get(i));
                    break;
                case "2":
                    mOptionList2.add(list.get(i));
                    break;
                case "3":
                    mOptionList3.add(list.get(i));
                    break;
            }
        }

        //填充viewPager
        ViewGameTzHolder firstHolder = new ViewGameTzHolder(mContext,mViewPager) ;
        ViewGameTzHolder secondHolder = new ViewGameTzHolder(mContext,mViewPager) ;
        ViewGameTzHolder thirdHolder = new ViewGameTzHolder(mContext,mViewPager) ;

        mViewList = new ArrayList<>() ;
        mViewList.add(firstHolder) ;
        mViewList.add(secondHolder) ;
        mViewList.add(thirdHolder) ;

        String itemName1 = HttpConsts.getGameTypeNameById(mGameId,"1") ;
        String itemName2 = HttpConsts.getGameTypeNameById(mGameId,"2") ;
        String itemName3 = HttpConsts.getGameTypeNameById(mGameId,"3") ;


        //暂定：当选择一种玩法的时候，选中了，别的玩法就会取消
        firstHolder.initBaseinfo(mOptionList1, new OnRvClickListener() {
            @Override
            public void onClick(int type, int position) {
                if(0 == type){
                    if(position >= 0 && position < mOptionList1.size()){
                        OptionBean info = mOptionList1.get(position) ;
                        info.setChecked(!info.isChecked());
                        mViewList.get(0).notifyAdapterInfo() ;

                        for(OptionBean info2 : mOptionList2){
                            info2.setChecked(false) ;
                        }
                        mViewList.get(1).notifyAdapterInfo() ;
                        for(OptionBean info3 : mOptionList3){
                            info3.setChecked(false) ;
                        }
                        mViewList.get(2).notifyAdapterInfo() ;

                        updateBottomInfo() ;
                    }
                }
            }
        }) ;
        secondHolder.initBaseinfo(mOptionList2, new OnRvClickListener() {
            @Override
            public void onClick(int type, int position) {
                if(0 == type){
                    if(position >= 0 && position < mOptionList2.size()){
                        OptionBean info = mOptionList2.get(position) ;
                        info.setChecked(!info.isChecked());
                        mViewList.get(1).notifyAdapterInfo() ;

                        for(OptionBean info1 : mOptionList1){
                            info1.setChecked(false) ;
                        }
                        mViewList.get(0).notifyAdapterInfo() ;
                        for(OptionBean info3 : mOptionList3){
                            info3.setChecked(false) ;
                        }
                        mViewList.get(2).notifyAdapterInfo() ;

                        updateBottomInfo() ;
                    }
                }
            }
        }) ;
        thirdHolder.initBaseinfo(mOptionList3, new OnRvClickListener() {
            @Override
            public void onClick(int type, int position) {
                if(0 == type){
                    if(position >= 0 && position < mOptionList3.size()){
                        OptionBean info = mOptionList3.get(position) ;
                        info.setChecked(!info.isChecked());
                        mViewList.get(2).notifyAdapterInfo() ;

                        for(OptionBean info1 : mOptionList1){
                            info1.setChecked(false) ;
                        }
                        mViewList.get(0).notifyAdapterInfo() ;
                        for(OptionBean info2 : mOptionList2){
                            info2.setChecked(false) ;
                        }
                        mViewList.get(1).notifyAdapterInfo() ;

                        updateBottomInfo() ;
                    }
                }
            }
        }) ;

        List<View> holderList = new ArrayList<>();
        holderList.add(firstHolder.getContentView()) ;
        holderList.add(secondHolder.getContentView()) ;
        holderList.add(thirdHolder.getContentView()) ;

        mViewPager.setOffscreenPageLimit(3) ;
        mViewPager.setAdapter(new ViewPagerAdapter(holderList));

        mTitleList = new ArrayList<>(3) ;
        mTitleList.add(itemName1) ;
        mTitleList.add(itemName2) ;
        mTitleList.add(itemName3) ;

        CommonNavigator mCommonNavigator = new CommonNavigator(mContext);
        mCommonNavigator.setSkimOver(true);
        mCommonNavigator.setAdjustMode(true) ;
        mCommonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(mContext);
                commonPagerTitleView.setContentView(R.layout.simple_pager_title_layout);

                final TextView titleText = commonPagerTitleView.findViewById(R.id.title_text);
                titleText.setText(mTitleList.get(index));

                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(mContext.getResources().getColor(R.color.textColor0));
                    }
                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(mContext.getResources().getColor(R.color.textColor2));
                    }
                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

                    }
                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });

                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                indicator.setYOffset(UIUtil.dip2px(context, 3));
                indicator.setColors(mContext.getResources().getColor(R.color.red));
                return indicator;
            }
        });
        mIndicator.setNavigator(mCommonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }

    /**
     * 根据选中的选项和筹码，计算金额
     */
    private void updateBottomInfo(){
        mCheckCount = 0 ;
        for(OptionBean info : mTotalOptionList){
            if(info.isChecked()){
                mCheckCount = mCheckCount + 1 ;
            }
        }

        mCheckedCountTv.setText(String.valueOf(mCheckCount)) ;
        if(mChipPopCountTv != null){
            mChipPopCountTv.setText(String.valueOf(mCheckCount)) ;
        }

        updateCheckMoney() ;
    }

    private void updateCheckMoney(){
        //筹码--默认筹码为2
        int chip = StringUtil.convertInt(mChipCountEv.getText().toString().trim()) ;
        if(chip <= 0){
            chip = 2 ;
        }
        String totalCount = String.valueOf(chip * mCheckCount)+".00" ;
        mCheckedMoneyTv.setText(totalCount) ;

        if(mChipPopMoneyTv != null){
            mChipPopMoneyTv.setText(totalCount) ;
        }
    }

    /**
     * 显示、隐藏筹码布局
     */
    public void showChipView(){
        if(mIsSoftShow){
            showChipPop() ;
        }else{
            if(mChipPop != null){
                mChipPop.dismiss() ;
            }
        }
    }

    /**
     * 获取余额
     */
    private void getYueInfo(){
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mYueTv.setText("账户余额：" + StringUtil.converMoney2Point(obj.getString("coin"))+"元");
                    mChipPopYueTv.setText("账户余额：" + StringUtil.converMoney2Point(obj.getString("coin"))+"元");
                }
            }
        });
    }

    /**
     * 获取游戏信息
     */
    private void getYfInfo() {
        String url ;

        if("1".equals(mGameId)){
            url = HttpConsts.GET_YFKS_INFO;
            mRequestTag = HttpConsts.GAME_YFKS_INFO ;
        } else if("2".equals(mGameId)){
            url = HttpConsts.GET_YF115_INFO;
            mRequestTag = HttpConsts.GAME_YF115_INFO ;
        } else if("3".equals(mGameId)){
            url = HttpConsts.GET_YFSC_INFO;
            mRequestTag = HttpConsts.GAME_YFSC_INFO ;
        } else if("4".equals(mGameId)){
            url = HttpConsts.GET_YFSSC_INFO;
            mRequestTag = HttpConsts.GAME_YFSSC_INFO ;
        } else if("5".equals(mGameId)){
            url = HttpConsts.GET_YFLH_INFO;
            mRequestTag = HttpConsts.GAME_YFLHC_INFO ;
        } else if("6".equals(mGameId)){
            url = HttpConsts.GET_YFKLSF_INFO;
            mRequestTag = HttpConsts.GAME_YFKLSF_INFO ;
        } else if("7".equals(mGameId)){
            url = HttpConsts.GET_YFXYNC_INFO;
            mRequestTag = HttpConsts.GAME_YFKLNC_INFO ;
        } else{
            url = HttpConsts.GET_YFKS_INFO;
            mRequestTag = HttpConsts.GAME_YFKS_INFO ;
        }

        HttpUtil.gameYfInfo(url, mRequestTag, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                try {
                    String arrStr = Arrays.toString(info);
                    if (code == 0) {
                        List<OptionBean> list = JSON.parseArray(arrStr, OptionBean.class);
                        updateDisplay(list) ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 连接 获取游戏结果 socket
     */
    private void connectGameResultSocket(){
        //启动socked
        mGameResultClient = new JWebSocketClient(mContext, URI.create(HttpConsts.getGameResultUrlByType(mGameId))
                ,JWebSocketClient.GAME_SOCKET_TYPE_RESULT){
            @Override
            public void onMessage(String message) {
                final SocketGameReceiveBean resultBean = GsonUtil.fromJson(message, SocketGameReceiveBean.class);

                if(resultBean != null){
                    mDianshuResult = resultBean.getDianshu_result() ;
                    mGameNewPeroid = resultBean.getNewGamePeroid(mGameId) ;
                    mGameTime = resultBean.getTime() ;
                    timeSec = StringUtil.convertInt(resultBean.getTime()) ;

                    mGameResultHandler.removeMessages(GameResultHandler.WHAT_GAME_TIME);
                    mGameResultHandler.sendEmptyMessage(GameResultHandler.WHAT_GAME_RESULT_DISPLAY) ;
                }
            }
        } ;

        mGameResultClient.connect() ;
    }

    @Override
    public void onClick(View view) {
        if(!ClickUtil.canClick()){
            return;
        }

        int vId = view.getId() ;
        if(R.id.act_game_tz_auto_lay == vId
                || R.id.pop_game_tz_auto_lay == vId){//机选

            autoOptionCheck() ;

        }else if(R.id.act_game_tz_reset_lay == vId
                || R.id.pop_game_tz_reset_lay == vId){//重置

            resetOptionsCheck() ;

        }else if(R.id.act_game_tz_submit_tv == vId
                || R.id.pop_game_tz_submit_tv == vId){

            submitCheck() ;

        } else if(R.id.act_game_tz_top_more_lay == vId){//更多

            showMorePop() ;

        }

    }

    /**
     * 更多
     */
    private void showMorePop(){
        if(mMorePop == null){
            mMorePop = new PopupWindow(mContext) ;

            View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_game_play_more_lay,null) ;
            mMorePop.setContentView(contentView);

            mMorePop.setWidth(DpUtil.dp2px(100)) ;
            mMorePop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            mMorePop.setBackgroundDrawable(new BitmapDrawable()) ;
            mMorePop.setOutsideTouchable(true);
            mMorePop.setFocusable(true);
//            mMorePop.setAnimationStyle(R.style.bottomToTopAnim);

            mMorePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1.0F) ;
                }
            });

            View rechargeLay = contentView.findViewById(R.id.pop_game_more_recharge_tv) ;
            View withdrawLay = contentView.findViewById(R.id.pop_game_more_withdraw_tv) ;
            View tzLay = contentView.findViewById(R.id.pop_game_more_tz_tv) ;
            View kjLay = contentView.findViewById(R.id.pop_game_more_kj_tv) ;
            View ruleLay = contentView.findViewById(R.id.pop_game_more_rule_tv) ;
            rechargeLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMorePop.dismiss() ;
                    MyCoinActivity.forward(mContext);
                }
            });
            withdrawLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMorePop.dismiss() ;
                    mContext.startActivity(new Intent(mContext, MyProfitActivity.class));
                }
            });
            tzLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMorePop.dismiss() ;
                    ActivityGameTzRecord.launch(mContext) ;
                }
            });
            kjLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMorePop.dismiss() ;
                    GameRecordListActivity.launch(mContext,mGameId);
                }
            });
            ruleLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMorePop.dismiss() ;

                    showGameRuleDialog() ;
                }
            });
        }

        backgroundAlpha(0.8F) ;
        mMorePop.showAsDropDown(mMoreLay,0,-DpUtil.dp2px(10),Gravity.RIGHT) ;
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void showGameRuleDialog(){
        if(null == mGameRuleDialog){
            mGameRuleDialog = DialogUitl.createHelpDialog(mContext,""
                    ,mContext.getResources().getString(HttpConsts.getGameRuleByType(mGameId))) ;
        }

        mGameRuleDialog.show() ;
    }

    /**
     * 提交选择
     */
    private void submitCheck(){
        if(timeSec <= HttpConsts.GAME_STOP_IN_TIME){
            ToastUtil.show(WordUtil.getString(R.string.game_tz_fail_tips));
            return;
        }
        int curItem = mViewPager.getCurrentItem() ;

        List<OptionBean> optionBeans = new ArrayList<>() ;
        if(0 == curItem){
            optionBeans.addAll(mOptionList1) ;
        }else if(1 == curItem){
            optionBeans.addAll(mOptionList2) ;
        }else if(2 == curItem){
            optionBeans.addAll(mOptionList3) ;
        }

        ArrayList<TzChooseBean> touzhuList = new ArrayList<>() ;

        String chipValue = mChipCountEv.getText().toString().trim() ;
        int chipValueInt = StringUtil.convertInt(chipValue) ;
        if(chipValueInt <= 0){
            chipValue = "2" ;
        }

        String gameName = HttpConsts.getGameNameByType(mGameId) ;
        String tzName = gameName + "-" + HttpConsts.getGameTypeNameById(mGameId,String.valueOf(curItem + 1)) ;

        for(OptionBean info : optionBeans){
            if(info.isChecked()){
                touzhuList.add(new TzChooseBean(chipValue, tzName, AppConfig.getInstance().getUid(), info)) ;
            }
        }

        if(touzhuList.size() == 0){
            ToastUtil.show(getResources().getString(R.string.game_tz_no_item_tips));
            return;
        }

        SpUtil.getInstance().setBooleanValue("iskCus",true);
        SpUtil.getInstance().setStringValue("iskTv",chipValue);
        SpUtil.getInstance().setStringValue("iskIv",String.valueOf(R.mipmap.icon_game_chip_hundred_empty));

        LiveGameTzDialogFragment tzFragment = new LiveGameTzDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("saveInfo",touzhuList);
        bundle.putString(Constants.LIVE_GAME_ID,mGameNewPeroid);
        bundle.putString(Constants.LIVE_GAME_NAME_TZ,gameName);
        bundle.putInt(Constants.LIVE_GAME_TIMESEC,timeSec);

        tzFragment.setArguments(bundle);
        tzFragment.show(getSupportFragmentManager(), HttpConsts.LIVE_GAME_TZ_DIALOG_FRAGMENT);
    }

    /**
     * 震动一下
     */
    private void vibrateSecond(){
        Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator != null){
            vibrator.vibrate(1000);
        }
    }

    /**
     * 机选
     */
    private void autoOptionCheck(){
        vibrateSecond() ;

        int curItem = mViewPager.getCurrentItem() ;
        int checkIndex = -1 ;

        if(0 == curItem){

            //重置别的
            for(OptionBean info2 : mOptionList2){
                info2.setChecked(false) ;
            }
            mViewList.get(1).notifyAdapterInfo() ;
            for(OptionBean info3 : mOptionList3){
                info3.setChecked(false) ;
            }
            mViewList.get(2).notifyAdapterInfo() ;



            for(OptionBean info : mOptionList1){
                info.setChecked(false);
            }
            checkIndex = RandomUtil.nextInt(mOptionList1.size()) ;
            if(checkIndex < 0 || checkIndex >= mOptionList1.size()){
                checkIndex = 0 ;
            }

            mOptionList1.get(checkIndex).setChecked(true);


        }else if(1 == curItem){
            //重置别的
            for(OptionBean info1 : mOptionList1){
                info1.setChecked(false) ;
            }
            mViewList.get(0).notifyAdapterInfo() ;
            for(OptionBean info3 : mOptionList3){
                info3.setChecked(false) ;
            }
            mViewList.get(2).notifyAdapterInfo() ;


            for(OptionBean info : mOptionList2){
                info.setChecked(false);
            }
            checkIndex = RandomUtil.nextInt(mOptionList2.size()) ;
            if(checkIndex < 0 || checkIndex >= mOptionList2.size()){
                checkIndex = 0 ;
            }

            mOptionList2.get(checkIndex).setChecked(true);

        }else if(2 == curItem){
            //重置别的
            for(OptionBean info1 : mOptionList1){
                info1.setChecked(false) ;
            }
            mViewList.get(0).notifyAdapterInfo() ;
            for(OptionBean info2 : mOptionList2){
                info2.setChecked(false) ;
            }
            mViewList.get(1).notifyAdapterInfo() ;

            for(OptionBean info : mOptionList3){
                info.setChecked(false);
            }
            checkIndex = RandomUtil.nextInt(mOptionList3.size()) ;
            if(checkIndex < 0 || checkIndex >= mOptionList3.size()){
                checkIndex = 0 ;
            }

            mOptionList3.get(checkIndex).setChecked(true);

        }

        if(checkIndex >= 0){
            mViewList.get(curItem).notifyAdapterInfo();
            updateBottomInfo() ;
        }
    }

    /**
     * 重置
     */
    private void resetOptionsCheck(){
        for(OptionBean info : mTotalOptionList){
            info.setChecked(false);
        }
        for(ViewGameTzHolder holder : mViewList){
            holder.notifyAdapterInfo() ;
        }

        updateBottomInfo();
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {

//        if(height > 0){
//            mKeyboardHeight = height ;
//        }
//
//        Log.e("onKeyboardHeightChanged","mKeyboardHeight=" + mKeyboardHeight) ;
    }


    private static class GameResultHandler extends Handler {
        private ActivityGameTz mGameTzAct;
        private static final int WHAT_GAME_TIME = 10;//游戏倒计时
        private static final int WHAT_GAME_RESULT_DISPLAY = 11 ;//更新UI
        private static final int WHAT_GAME_CHIP_VIEW = 12 ;//显示筹码

        public GameResultHandler(ActivityGameTz activity) {
            mGameTzAct = new WeakReference<>(activity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mGameTzAct != null) {
                switch (msg.what) {
                    case WHAT_GAME_TIME:
                        mGameTzAct.refreshGameTime() ;
                        break;
                    case WHAT_GAME_RESULT_DISPLAY:
                        mGameTzAct.updateTouzhuWindowInfo();
                        break;
                    case WHAT_GAME_CHIP_VIEW:
                        mGameTzAct.showChipView();
                        break;
                }
            }
        }

        public void release() {
            removeCallbacksAndMessages(null);
        }
    }


    private void refreshGameTime(){
        EventBus.getDefault().post(new GameResultEvent(true,mGameId,mGameNewPeroid,timeSec)) ;

        mGameResultHandler.sendEmptyMessageDelayed(GameResultHandler.WHAT_GAME_TIME,1000) ;

        updateGameTimeTv() ;

        if(timeSec == 0){
            mGameResultHandler.removeMessages(GameResultHandler.WHAT_GAME_TIME);
        }
        timeSec-- ;
    }

    /**
     * 填充内容
     */
    private void updateTouzhuWindowInfo(){
//        String date = ImDateUtil.dateToString(new Date()) ;
//        String showedPeriod = date + mGameNewPeroid ;
        String showedPeriod = mGameNewPeroid ;
        //数据已更新，当前期数 showedPeriod

        EventBus.getDefault().post(new GameResultEvent(false,mGameId,mGameNewPeroid, StringUtil.convertInt(mGameTime))) ;

        if(!mIsFirstShow){
            showUpdateToast(showedPeriod) ;
        }else{
            mIsFirstShow = false ;
        }

        mLastPeriodTv.setText(String.valueOf(StringUtil.convertInt(mGameNewPeroid) - 1)) ;
//        mLastPeriodTv.setText(date + (StringUtil.convertInt(mGameNewPeroid) - 1)) ;
        mLatestPeriodTv.setText(showedPeriod) ;
        refreshGameTime() ;

        //更新点数
        updateGameResult() ;
    }

    private void showUpdateToast(String msg){
        if(!mIsPaused){
            View view = LayoutInflater.from(mContext).inflate(R.layout.toast_game_period_update,null) ;
            TextView contentTv = view.findViewById(R.id.toast_game_period_tv) ;
            contentTv.setText("期数已更新，当前期数" + msg);

            Toast toast = new Toast(mContext) ;
            toast.setGravity(Gravity.CENTER,0,0);
            toast.setDuration(Toast.LENGTH_SHORT) ;
            toast.setView(view) ;
            toast.show() ;
        }
    }

    /**
     * 填充结果
     */
    private void updateGameResult(){
        String[] result = mDianshuResult.split(",") ;

        int type = 1 ;
        List<AwardBean> list = new ArrayList<>();

        if("1".equals(mGameId)){//快三
            type = 1 ;
            int imgeUrl = 0;
            for (String str : result) {
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
        }else if("2".equals(mGameId) || "4".equals(mGameId) || "6".equals(mGameId)){//11选5  时时彩  快乐十分
            type = 2 ;

            for (String str : result) {
                list.add(new AwardBean(str));
            }
        }else if("3".equals(mGameId)){//赛车
            type = 4 ;

            for (String str : result){
                list.add(new AwardBean(str));
            }
        }else if("5".equals(mGameId)){//六合彩
            type = 3 ;

            for (int i = 0; i < result.length; i++) {
                if (i == result.length - 1) {
                    list.add(new AwardBean("",R.mipmap.ic_game_point_add_black));
                }

                String number = result[i] ;
                list.add(new AwardBean(number,AwardBean.getItemBackgroundByName(number))) ;
            }
        }else if("7".equals(mGameId)){//时时彩
            type = 1 ;

            int imgeUrl = 0;
            for (String str : result) {
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
        }

        int parentWidth = (int)(ScreenDimenUtil.getInstance().getScreenWdith() * 5f / 8f) - DpUtil.dp2px(25) ;//多留了空间

        mGameResultRv.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
        AdapterGameResult adapter = new AdapterGameResult(mContext, list, type,parentWidth);
        mGameResultRv.setAdapter(adapter);
    }

    private void updateGameTimeTv(){
        if(timeSec <= HttpConsts.GAME_STOP_IN_TIME){
            mLatestPeriodTipsTv.setText("期 封盘中") ;

            mTime1000Tv.setText("-") ;
            mTime100Tv.setText("-") ;
            mTime10Tv.setText("-") ;
            mTime1Tv.setText("-") ;
        }else{
            mLatestPeriodTipsTv.setText("期 封盘") ;
            mTime1000Tv.setText("0") ;
            mTime100Tv.setText("0") ;

            int showTime = timeSec - HttpConsts.GAME_STOP_IN_TIME ;
            String showTimeStr = String.valueOf(showTime) ;

            if(showTime < 10){
                mTime10Tv.setText("0") ;
                mTime1Tv.setText(showTimeStr) ;
            }else{
                if(showTimeStr.length() > 1){
                    mTime10Tv.setText(showTimeStr.substring(0,1)) ;
                    mTime1Tv.setText(showTimeStr.substring(1,2)) ;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mIsPaused = false ;
    }

    @Override
    protected void onPause() {
        super.onPause();

        mIsPaused = true ;
    }

    /**
     * 投注信息相关
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTouzhuResultEvent(GameTzEvent e) {
        if (e != null) {
            String message = e.getMessage() ;
            if(!TextUtils.isEmpty(message)){
                ToastUtil.showLong(message);
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        if(mGameResultClient != null && mGameResultClient.isOpen()){
            mGameResultClient.close();
            mGameResultClient = null ;
        }

        if(mGameResultHandler != null){
            mGameResultHandler.release();
        }

        HttpUtil.cancel(mRequestTag) ;
        HttpUtil.cancel(HttpConsts.GET_BALANCE) ;

        super.onDestroy();
    }
}
