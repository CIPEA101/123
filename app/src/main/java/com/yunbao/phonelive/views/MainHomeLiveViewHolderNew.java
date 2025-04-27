package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ActivityGameTz;
import com.yunbao.phonelive.adapter.AdapterHomeGameList;
import com.yunbao.phonelive.adapter.MainHomeHotAdapterNew;
import com.yunbao.phonelive.adapter.MainHomeLiveClassAdapter;
import com.yunbao.phonelive.bean.ConfigBean;
import com.yunbao.phonelive.bean.GameTypeBean;
import com.yunbao.phonelive.bean.LiveBean;
import com.yunbao.phonelive.bean.LiveClassBean;
import com.yunbao.phonelive.bean.RollPicBean;
import com.yunbao.phonelive.custom.ItemDecoration;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.WordUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页 直播
 */

public class MainHomeLiveViewHolderNew extends AbsMainChildTopViewHolder implements OnItemClickListener<LiveBean> {
    private RefreshLayout mRefreshLay ;

    private NestedScrollView mSv ;
    private RecyclerView mRv ;

    private RecyclerView mClassRecyclerView;
    private MainHomeLiveClassAdapter mHomeLiveClassAdapter;

    private List<LiveBean> mDateList ;
    private MainHomeHotAdapterNew mAdapter ;
    private int mCurPage = 1 ;

    private SlideshowView mSlideshowView;

    public MainHomeLiveViewHolderNew(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_live_new;
    }

    @Override
    public void init() {
        super.init();
        mClassRecyclerView = (RecyclerView) findViewById(R.id.classRecyclerView);
        mClassRecyclerView.setHasFixedSize(true);
        mClassRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        mClassRecyclerView.setVisibility(View.GONE);

        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean != null) {
            List<LiveClassBean> list = configBean.getLiveClass();
            if (list == null || list.size() == 0) {
                mClassRecyclerView.setVisibility(View.GONE);
            } else {
                List<LiveClassBean> targetList = new ArrayList<>();
                if (list.size() <= 6) {
                    targetList.addAll(list);
                } else {
                    targetList.addAll(list.subList(0, 5));
                    LiveClassBean bean = new LiveClassBean();
                    bean.setAll(true);
                    bean.setName(WordUtil.getString(R.string.all));
                    targetList.add(bean);
                }
                mHomeLiveClassAdapter = new MainHomeLiveClassAdapter(mContext, targetList, false);
                mClassRecyclerView.setAdapter(mHomeLiveClassAdapter);
            }
        } else {
            mClassRecyclerView.setVisibility(View.GONE);
        }

        RecyclerView gameRv = (RecyclerView) findViewById(R.id.view_main_home_game_rv);
        gameRv.setFocusable(false);
        gameRv.setNestedScrollingEnabled(false);

        AdapterHomeGameList gameListAdapter = new AdapterHomeGameList(mContext, new AdapterHomeGameList.OnGameOptClickListener() {
            @Override
            public void onGameClick(GameTypeBean bean) {
                ActivityGameTz.launch(mContext,String.valueOf(bean.getId())) ;
            }
        }) ;
        gameRv.setLayoutManager(new GridLayoutManager(mContext,4));
        gameRv.setAdapter(gameListAdapter) ;

        mRefreshLay = (RefreshLayout) findViewById(R.id.live_main_home_live_refresh_lay);
        mSv = (NestedScrollView) findViewById(R.id.live_main_home_live_sv);
        mRv = (RecyclerView) findViewById(R.id.live_main_home_live_rv);

        mRefreshLay.setScorllView(mSv);
        mRefreshLay.setLoadMoreEnable(true) ;
        mRefreshLay.setRefreshEnable(true) ;

        mRefreshLay.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList() ;
            }
            @Override
            public void onLoadMore() {
                getHotList() ;
            }
        });

        mRv.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false)) ;
        mRv.setNestedScrollingEnabled(false);
        mRv.setFocusable(false);

        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 6, 6);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRv.addItemDecoration(decoration);

        mSlideshowView = (SlideshowView) findViewById(R.id.slideshowView);

        int screenWidth = ScreenDimenUtil.getInstance().getScreenWdith() ;
        //43 / 18
        int sdHeight = (int) (screenWidth * 18F / 43F);

        mSlideshowView.getLayoutParams().width = screenWidth ;
        mSlideshowView.getLayoutParams().height = sdHeight ;

        mDateList = new ArrayList<>() ;

        refreshList() ;
    }


    public void setLiveClassItemClickListener(OnItemClickListener<LiveClassBean> liveClassItemClickListener) {
        if (mHomeLiveClassAdapter != null) {
            mHomeLiveClassAdapter.setOnItemClickListener(liveClassItemClickListener);
        }
    }

    @Override
    public void onItemClick(LiveBean bean, int position) {
        watchLive(bean, Constants.LIVE_HOME, position);
    }

    @Override
    public void loadData() {
        if(mDateList != null && mDateList.size() == 0){
            refreshList() ;
        }
    }


    private void refreshList(){
        mCurPage = 1 ;
        getHotList() ;
    }

    private void getHotList(){
        HttpUtil.getHot(mCurPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                try {
                    List<RollPicBean> images = JSON.parseArray(JSON.parseObject(info[0]).getString("slide"), RollPicBean.class);
                    if (images.size() > 0) {
                        mSlideshowView.addDataToUI(images,true) ;
                    }

                    List<LiveBean> list = JSON.parseArray(JSON.parseObject(info[0]).getString("list"), LiveBean.class);

                    if(1 == mCurPage){
                        mDateList.clear();
                    }
                    mDateList.addAll(list) ;

                    if(mAdapter == null){
                        mAdapter = new MainHomeHotAdapterNew(mContext, mDateList, new OnRvClickListener() {
                            @Override
                            public void onClick(int type, int position) {
                                if(0 == type && position >= 0 && position < mDateList.size()){
                                    watchLive(mDateList.get(position), Constants.LIVE_HOME, position);
                                }
                            }
                        }) ;
                        mRv.setAdapter(mAdapter) ;
                    }else{
                        mAdapter.notifyDataSetChanged() ;
                    }

                    if(list.size() > 0){
                        mCurPage ++ ;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                finishRequest() ;
            }
            @Override
            public void onError() {
                super.onError();

                finishRequest() ;
            }
        });
    }

    private void finishRequest(){
        mRefreshLay.completeLoadMore() ;
        mRefreshLay.completeRefresh() ;
    }

}
