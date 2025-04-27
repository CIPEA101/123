package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.MainListAdapter;
import com.yunbao.phonelive.adapter.RefreshAdapter;
import com.yunbao.phonelive.bean.ListBean;
import com.yunbao.phonelive.custom.RefreshView;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.LifeCycleAdapter;
import com.yunbao.phonelive.utils.L;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/27.
 * 首页 排行 收益榜
 */

public class MainListProfitViewHolder extends AbsMainListViewHolder {

    public MainListProfitViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_list_page;
    }

    @Override
    public void init() {
        //super.init();
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_list);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<ListBean>() {
            @Override
            public RefreshAdapter<ListBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainListAdapter(mContext,MainListAdapter.TYPE_PROFIT);
                    mAdapter.setOnItemClickListener(MainListProfitViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.profitList(mType, p, callback);
            }

            @Override
            public List<ListBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), ListBean.class);
            }

            @Override
            public void onRefresh(List<ListBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {
                if (dataCount < 50) {
                    mRefreshView.setLoadMoreEnable(false);
                } else {
                    mRefreshView.setLoadMoreEnable(true);
                }
            }
        });
        mLifeCycleListener = new LifeCycleAdapter() {

            @Override
            public void onDestroy() {
                L.e("main----MainListProfitViewHolder-------LifeCycle---->onDestroy");
                HttpUtil.cancel(HttpConsts.PROFIT_LIST);
                HttpUtil.cancel(HttpConsts.SET_ATTENTION);
            }
        };
    }

    @Override
    public void loadData() {
        if(!isFirstLoadData()){
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

}
