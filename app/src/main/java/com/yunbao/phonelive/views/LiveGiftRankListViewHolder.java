package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.AdapterLiveGiftRank;
import com.yunbao.phonelive.bean.RankList;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 直播间礼物贡献榜
 */
public class LiveGiftRankListViewHolder extends AbsViewHolder {
    public static final String RANK_TYPE_DAY = "day" ;
    public static final String RANK_TYPE_WEEK = "week" ;
    public static final String RANK_TYPE_MONTH = "month" ;
    private String mRankType ;
    private String mLiveUid ;//主播id

    private RecyclerView mRv ;
    private View mEmptyTv ;

    private List<RankList> mRankList ;
    private AdapterLiveGiftRank mAdapter ;

    private int mCurPage = 1 ;
    private boolean mHasMore = true ;
    private boolean mIsLoading = false ;

    public LiveGiftRankListViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_gift_rank_list ;
    }

    @Override
    public void init() {

        mRankList = new ArrayList<>() ;

        mEmptyTv = findViewById(R.id.view_live_gift_rank_empty_tv) ;
        mRv = (RecyclerView) findViewById(R.id.view_live_gift_rank_rv);
        mRv.setLayoutManager(new LinearLayoutManager(mContext));
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!mIsLoading && mHasMore && ViewUtils.isSlideBottom(mRv)){
                    getRankList() ;
                }

            }
        });
    }

    public void setBaseInfo(String liveUid,String rankType){
        mLiveUid = liveUid ;
        mRankType = rankType ;
    }

    public void updateDisplay(){
        if(mRankList.size() == 0){
           refreshList() ;
        }
    }

    private void refreshList(){
        mCurPage = 1 ;
        if(mAdapter != null){
            mRankList.clear();
            mAdapter.notifyDataSetChanged() ;
        }

        getRankList() ;
    }

    private void getRankList(){
        mIsLoading = true ;

        HttpUtil.consumeList(mRankType,mLiveUid, mCurPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<RankList> list = null ;

                try {
                    list = JSON.parseArray(Arrays.toString(info), RankList.class);
                    if(list != null){
                        mRankList.addAll(list) ;

                        if(null == mAdapter){
                            mAdapter = new AdapterLiveGiftRank(mContext, mRankList, new OnRvClickListener() {
                                @Override
                                public void onClick(int type, int position) {

                                }
                            }) ;
                            mRv.setAdapter(mAdapter) ;
                        }else{
                            mAdapter.notifyDataSetChanged() ;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(list != null){
                    if(list.size() > 0){
                        mCurPage ++ ;
                    }else{
                        mHasMore = false ;
                    }
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
        mIsLoading = false ;
        mEmptyTv.setVisibility(mRankList.size() == 0 ? View.VISIBLE : View.GONE) ;
    }

    public void relessView(){
        HttpUtil.cancel(HttpConsts.CONSUME_LIST + mRankType) ;
    }

}
