package com.yunbao.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.baidu.speech.utils.LogUtil;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.TzRecordBean;
import com.yunbao.phonelive.adapter.LiveTzRecordAdapter;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.dialog.LiveGameTzRecordFragment;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.utils.StringUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 开奖历史
 */
public class GameRecordListActivity extends AbsActivity  implements RefreshLayout.OnRefreshListener {

    private String mGameId ;

    private RefreshLayout mRefreshLay ;
    private RecyclerView recyclerView;

    private int mCurPage = 1;
    private LiveTzRecordAdapter adapter;
    private List<TzRecordBean> list;

    public static void launch(Context context,String gameId){
        Intent toIt = new Intent(context,GameRecordListActivity.class) ;
        toIt.putExtra(Constants.LIVE_GAME_ID,gameId) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_record_list ;
    }

    @Override
    protected void main() {
        super.main();

        mGameId = getIntent().getStringExtra(Constants.LIVE_GAME_ID) ;

        mRefreshLay = findViewById(R.id.act_game_record_refresh_lay);
        recyclerView = findViewById(R.id.act_game_record_rv);

        mRefreshLay.setScorllView(recyclerView);
        mRefreshLay.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        setTitle(HttpConsts.getGameNameByType(mGameId) + "开奖记录");

        getData(mCurPage) ;
    }

    private void getData(final int page) {
        HttpUtil.getAwardRecord(mGameId, String.valueOf(page), new HttpCallback() {
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
                            adapter = new LiveTzRecordAdapter(mContext, getLayoutInflater(), list, StringUtil.convertInt(mGameId)) ;
                            recyclerView.setAdapter(adapter);
                        }
                    } else {
                        if (list != null && list.size() > 0) {
                            adapter.addList(list);
                        }
                    }

                    if(list != null && list.size() > 0){
                        mCurPage++;
                    }
                }

                mRefreshLay.completeRefresh();
                mRefreshLay.completeLoadMore();
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);

                mRefreshLay.completeRefresh();
                mRefreshLay.completeLoadMore();
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

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.GAME_LOG);
        super.onDestroy();
    }
}
