package com.yunbao.phonelive.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.speech.utils.LogUtil;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.TzRecordBean;
import com.yunbao.phonelive.adapter.LiveTzRecordAdapter;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.utils.StringUtil;

import java.util.Arrays;
import java.util.List;

public class LiveGameTzRecordFragment extends AbsDialogFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;
    private TextView noData;
    private LiveTzRecordAdapter adapter;
    private String mGameType ;
    private int mCurPage = 1;
    private List<TzRecordBean> list;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_game_tz_record;
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
        params.height = dp2px(300);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = mRootView.findViewById(R.id.recycler);
        noData = mRootView.findViewById(R.id.tv_no_data);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        recyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallClipRotate);
        recyclerView.setLoadingListener(this);
        recyclerView.setFootViewText("加载中","没有更多了");

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        Bundle bundle = getArguments();
        if (bundle != null) {
            mGameType = bundle.getString(Constants.LIVE_GAME_TYPE);
        }

        if(TextUtils.isEmpty(mGameType)){
            mGameType = "1" ;
        }

        getData(mCurPage);
    }

    private void getData(final int page) {
        HttpUtil.getAwardRecord(mGameType, String.valueOf(page), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                LogUtil.e(LiveGameTzRecordFragment.class.getSimpleName(), Arrays.toString(info));

                if (code == 0) {
                    try {
                        list = JSON.parseArray(Arrays.toString(info), TzRecordBean.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (page == 1) {
                        if (list != null && list.size() > 0) {
                            adapter = new LiveTzRecordAdapter(mContext, getLayoutInflater(), list, StringUtil.convertInt(mGameType)) ;
                            recyclerView.setAdapter(adapter);
                        }else{
                            recyclerView.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (list != null && list.size() > 0) {
                            adapter.addList(list);
                        }
                    }

                    if(list != null && list.size() > 0){
                        mCurPage++;
                    }

                    recyclerView.refreshComplete();
                    recyclerView.loadMoreComplete();
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);

                if (page == 1) {
                    recyclerView.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
                recyclerView.refreshComplete();
                recyclerView.loadMoreComplete();
            }
        });
    }

    @Override
    public void onRefresh() {
        mCurPage = 1;

        if(list != null && list.size() > 0){
            list.clear();
            adapter.clear();
        }

        getData(mCurPage);
    }

    @Override
    public void onLoadMore() {
        getData(mCurPage);
    }

    private int dp2px(int dpVal) {
        return (int) (mContext.getResources().getDisplayMetrics().density * dpVal + 0.5f);
    }
}
