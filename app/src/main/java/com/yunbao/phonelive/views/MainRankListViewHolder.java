package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.MainRankListAdapter;
import com.yunbao.phonelive.adapter.RefreshAdapter;
import com.yunbao.phonelive.bean.LevelBean;
import com.yunbao.phonelive.bean.RankList;
import com.yunbao.phonelive.custom.RefreshView;
import com.yunbao.phonelive.glide.ImgLoader;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.DataLoader;
import com.yunbao.phonelive.interfaces.LifeCycleAdapter;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainRankListViewHolder extends AbsMainViewHolder implements DataLoader, View.OnClickListener {
    public static final int RANK_TYPE_REWARD = 0 ;//明星榜
    public static final int RANK_TYPE_CONTRIBUTE = 1 ;//贡献榜
    public static final int RANK_TYPE_RICH = 2 ;//富豪榜
    private int mRankType = 0 ;

    public static final String DAY = "day";
    public static final String WEEK = "week";
    public static final String MONTH = "month";

    private TextView mNavDayTv ;
    private TextView mNavWeekTv ;
    private TextView mNavMonthTv ;

    private ImageView mTopHeadIv ;
    private TextView mTopNameTv ;
    private ImageView mTopLevelIv ;
    private TextView mTopVotesTv ;

    protected RefreshView mRefreshView;

    private String mType ;
    protected MainRankListAdapter mAdapter;

    private ImageView iv_ranking_bg;


    public MainRankListViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_rank_list;
    }

    @Override
    public void init() {
        mType = DAY ;

        mNavDayTv = (TextView) findViewById(R.id.view_main_rank_nav_day_tv);
        mNavWeekTv = (TextView) findViewById(R.id.view_main_rank_nav_week_tv);
        mNavMonthTv = (TextView) findViewById(R.id.view_main_rank_nav_month_tv);
        mNavMonthTv.setOnClickListener(this);
        mNavWeekTv.setOnClickListener(this);
        mNavDayTv.setOnClickListener(this);

        iv_ranking_bg = (ImageView) findViewById(R.id.iv_ranking_bg);

        mTopHeadIv = (ImageView) findViewById(R.id.view_main_rank_top_user_iv);
        mTopNameTv = (TextView) findViewById(R.id.view_main_rank_top_name_tv);
        mTopLevelIv = (ImageView) findViewById(R.id.view_main_rank_top_user_level_iv);
        mTopVotesTv = (TextView) findViewById(R.id.view_main_rank_top_votes_tv);

        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setLoadMoreEnable(false);//先禁用加载更多
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_list);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<RankList>() {
            @Override
            public RefreshAdapter<RankList> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainRankListAdapter(mContext,mRankType);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if(RANK_TYPE_CONTRIBUTE == mRankType){//贡献榜
                    HttpUtil.consumeList(mType, p, callback);
                }else if(RANK_TYPE_RICH == mRankType){//富豪榜
                    HttpUtil.rankRichList(mType, callback);//数据格式有问题
                }else{
                    HttpUtil.profitList(mType, p, callback);
                }
            }

            @Override
            public List<RankList> processData(String[] info) {
                try {
                    return JSON.parseArray(Arrays.toString(info), RankList.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ArrayList<>() ;
            }

            @Override
            public void onRefresh(List<RankList> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

                if(mAdapter != null){
                    int size = mAdapter.getList().size() ;
                    if(size > 0){
                        RankList topItem = mAdapter.getList().get(0) ;
                        initTopItemInfo(topItem) ;
                    }else{
                        initTopItemInfo(null) ;
                    }
                }

                if(RANK_TYPE_CONTRIBUTE == mRankType || RANK_TYPE_REWARD == mRankType){
                    if (dataCount < 50) {
                        mRefreshView.setLoadMoreEnable(false);
                    } else {
                        mRefreshView.setLoadMoreEnable(true);
                    }
                }
            }
        });
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                L.e("main----MainListProfitViewHolder-------LifeCycle---->onDestroy");
                HttpUtil.cancel(HttpConsts.CONSUME_LIST);
                HttpUtil.cancel(HttpConsts.PROFIT_LIST);
                HttpUtil.cancel(HttpConsts.RANK_RICH_LIST);
            }
        };
    }

    @Override
    public void loadData() {
//        if(!isFirstLoadData()){
//            return;
//        }
//        if (mRefreshView != null) {
//            mRefreshView.initData();
//        }
    }

    /**
     * 设置基本信息
     */
    public void setBaseInfo(int rankType){
        mRankType = rankType ;
        changeIcon();
    }

    /**
     * 更换图片背景
     */
    private void changeIcon(){
        switch (mRankType){
            case RANK_TYPE_REWARD://明星榜
                ImgLoader.display(R.mipmap.icon_ranking_one, iv_ranking_bg);
                break;
            case RANK_TYPE_CONTRIBUTE://贡献榜
                ImgLoader.display(R.mipmap.icon_ranking_two, iv_ranking_bg);
                break;
            case RANK_TYPE_RICH://富豪榜
                ImgLoader.display(R.mipmap.icon_ranking_three, iv_ranking_bg);
                break;
        }
    }
    public void updateDisplay(){
        if(null == mAdapter || mAdapter.getList().size() == 0){
            if (mRefreshView != null) {
                mRefreshView.initData();
            }
        }
    }

    /**
     * 填充榜首信息
     * @param bean info
     */
    private void initTopItemInfo(RankList bean){
        if(bean != null){
            ImgLoader.displayAvatar(bean.getAvatar(), mTopHeadIv);
            mTopNameTv.setText(bean.getUser_nicename());
            if(RANK_TYPE_RICH == mRankType) {//富豪榜
                mTopVotesTv.setText("游戏: *****" );
            }else {
                mTopVotesTv.setText("礼物:" + bean.getTotalcoin());
            }

            int level = bean.getLevel() ;
            if(level > 0){
                AppConfig appConfig = AppConfig.getInstance();
                LevelBean levelBean = appConfig.getLevel(level);
                if (levelBean != null) {
                    ImgLoader.display(levelBean.getThumb(), mTopLevelIv);
                }else{
                    mTopLevelIv.setVisibility(View.INVISIBLE);
                }
            }else{
                mTopLevelIv.setVisibility(View.INVISIBLE);
            }

        }else{
            mTopHeadIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_avatar_placeholder)) ;
            mTopNameTv.setText("虚位以待");
            mTopLevelIv.setVisibility(View.INVISIBLE) ;
            if(RANK_TYPE_RICH == mRankType) {//富豪榜
                mTopVotesTv.setText("游戏: *****" );
            }else {
                mTopVotesTv.setText("礼物:0");
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(!ClickUtil.canClick()){
            return;
        }

        int vId = view.getId() ;
        if(R.id.view_main_rank_nav_day_tv == vId){//日榜
            changeNavType(DAY) ;
        } else if(R.id.view_main_rank_nav_week_tv == vId){//周榜
            changeNavType(WEEK) ;
        } else if(R.id.view_main_rank_nav_month_tv == vId){//月榜
            changeNavType(MONTH) ;
        }
    }

    /**
     * 切换榜单类型
     */
    private void changeNavType(String type){
        if(mType.equals(type)){
            return;
        }

        if(DAY.equals(mType)){
            mNavDayTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_normal)) ;
            mNavDayTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_normal)) ;
        }else if(WEEK.equals(mType)){
            mNavWeekTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_normal)) ;
            mNavWeekTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_normal)) ;
        }else if(MONTH.equals(mType)){
            mNavMonthTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_normal)) ;
            mNavMonthTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_normal)) ;
        }

        if(DAY.equals(type)){
            mNavDayTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_checked)) ;
            mNavDayTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rank_left_checked)) ;
        }else if(WEEK.equals(type)){
            mNavWeekTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_checked)) ;
            mNavWeekTv.setBackground(mContext.getResources().getDrawable(R.drawable.drawable_rank_list_nav_mid)) ;
        }else if(MONTH.equals(type)){
            mNavMonthTv.setTextColor(mContext.getResources().getColor(R.color.color_rank_list_nav_checked)) ;
            mNavMonthTv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rank_right_checked)) ;
        }
        mType = type ;

        mRefreshView.initData() ;
    }

}
