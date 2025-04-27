package com.yunbao.phonelive.dialog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.AwardGameHistoryAdapter;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.event.GameTimeEvent;
import com.yunbao.phonelive.game.TzListBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.CollectionUtils;
import com.yunbao.phonelive.utils.ScreenDimenUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiveGameAwardHistoryFragment extends AbsDialogFragment implements View.OnClickListener, RefreshLayout.OnRefreshListener {

    private RelativeLayout rlAA;
    private TextView noData;
    private View mLoadingPb ;
    private TextView tvAA;
    private View vAA;
    private RelativeLayout rlNA;
    private TextView tvNA;
    private View vNA;
    private RelativeLayout rlLA;
    private TextView tvLA;
    private View vLA;
    private int page = 1;
    private int type = 2;
    private List<TzListBean> list = new ArrayList<>();

    private RefreshLayout mRefreshLay ;
    private RecyclerView recyclerView;
    private AwardGameHistoryAdapter adapter;

    private String mGameType ;
    private boolean currentGame;//随时加载当前主播游戏时间
    private  boolean isFirstLoad;//数据错乱时第一次加载数据

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_game_award_history;
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
        params.height = (int) (ScreenDimenUtil.getInstance().getScreenHeight() * 0.4F);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mGameType = bundle.getString(Constants.LIVE_GAME_TYPE);
        }

        if(TextUtils.isEmpty(mGameType)){
            mGameType = "1" ;
        }
        rlAA = mRootView.findViewById(R.id.rl_all_award);
        tvAA = mRootView.findViewById(R.id.tv_all_award);
        vAA = mRootView.findViewById(R.id.view_all_award);
        rlNA = mRootView.findViewById(R.id.rl_no_award);
        tvNA = mRootView.findViewById(R.id.tv_no_award);
        vNA = mRootView.findViewById(R.id.view_no_award);
        rlLA = mRootView.findViewById(R.id.rl_lottery);
        tvLA = mRootView.findViewById(R.id.tv_lottery);
        vLA = mRootView.findViewById(R.id.view_lottery);
        noData = mRootView.findViewById(R.id.tv_no_data);
        mLoadingPb = mRootView.findViewById(R.id.game_award_loading_pb);

        mRefreshLay = mRootView.findViewById(R.id.dialog_live_game_award_refresh_lay);
        recyclerView = mRootView.findViewById(R.id.dialog_live_game_award_rv);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //开始滑动

                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    //获取最后一个完全显示的ItemPosition
//                    int firstVisibleItem = manager.findFirstVisibleItemPosition();
//                    int totalItemCount = manager.getItemCount();
//
//                    // 判断是否滚动到底部，并且是向右滚动
//                    if (firstVisibleItem != 0 ) {
//                        //加载更多功能的代码
//                        currentGame=false;
//                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentGame=false;
            }
        });
        mRefreshLay.setScorllView(recyclerView);
        mRefreshLay.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new AwardGameHistoryAdapter(mContext, getLayoutInflater(), list,type);
        recyclerView.setAdapter(adapter);

        rlAA.setOnClickListener(this);
        rlNA.setOnClickListener(this);
        rlLA.setOnClickListener(this);

        getList(page, type);
    }

    @Override
    public void onClick(View view) {
        if(!ClickUtil.canClick()){
            return;
        }

        switch (view.getId()) {
            case R.id.rl_all_award:
                tvAA.setTextColor(mContext.getResources().getColor(R.color.black));
                tvNA.setTextColor(mContext.getResources().getColor(R.color.textColor2));
                tvLA.setTextColor(mContext.getResources().getColor(R.color.textColor2));
                vAA.setVisibility(View.VISIBLE);
                vNA.setVisibility(View.GONE);
                vLA.setVisibility(View.GONE);
                type = 2;
                onRefresh();
                break;
            case R.id.rl_no_award:
                tvAA.setTextColor(mContext.getResources().getColor(R.color.textColor2));
                tvNA.setTextColor(mContext.getResources().getColor(R.color.black));
                tvLA.setTextColor(mContext.getResources().getColor(R.color.textColor2));
                vAA.setVisibility(View.GONE);
                vNA.setVisibility(View.VISIBLE);
                vLA.setVisibility(View.GONE);
                type = 0;
                onRefresh();
                break;
            case R.id.rl_lottery:
                tvAA.setTextColor(mContext.getResources().getColor(R.color.textColor2));
                tvNA.setTextColor(mContext.getResources().getColor(R.color.textColor2));
                tvLA.setTextColor(mContext.getResources().getColor(R.color.black));
                vAA.setVisibility(View.GONE);
                vNA.setVisibility(View.GONE);
                vLA.setVisibility(View.VISIBLE);
                type = 1;
                onRefresh();
                break;
        }
    }

    @Override
    public void onRefresh() {
        mRefreshLay.setLoadMoreEnable(false) ;
        page = 1;
        if (list != null && list.size() > 0) {
            list.clear();
            adapter.release();
            adapter.notifyDataSetChanged() ;
        }
        getList(page, type);
    }

    @Override
    public void onLoadMore() {
        page++;
        getList(page, type);
    }

    private void getList(final int page, int ok) {
        if(adapter == null || adapter.getItemCount() == 0){
            mLoadingPb.setVisibility(View.VISIBLE) ;
        }
        noData.setVisibility(View.GONE) ;
        currentGame=false;
        HttpUtil.cancel(HttpConsts.USER_LOG);

        HttpUtil.getTzRecord(String.valueOf(page), String.valueOf(ok), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<TzListBean> tzListBeans=new ArrayList<>();
                    try {
                        tzListBeans = JSON.parseArray(Arrays.toString(info), TzListBean.class);
                        for(TzListBean bean:tzListBeans){
                            //判断未开奖的
                            if(!bean.isTzKj()){
                                //设置结束时间
                                bean.setEndTime(bean.getTime()*1000+System.currentTimeMillis());
                                bean.setTime(10);//此处赋值 因为服务器没有返回 所以赋值  具体操作可看演示版
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (page == 1) {
                        if (tzListBeans != null && tzListBeans.size() > 0) {
                            list.clear();
                            if(CollectionUtils.isNotEmpty(tzListBeans)){
                                list.addAll(tzListBeans);
                            }
                            adapter.notifyDataSetChanged();
                        }else{
                            mRefreshLay.setLoadMoreEnable(false) ;
                        }
                    } else {
                        if (tzListBeans != null && tzListBeans.size() > 0) {
                            adapter.addList(tzListBeans);
                        }else{
                            mRefreshLay.setLoadMoreEnable(false) ;
                        }
                    }
                    for(TzListBean bean:adapter.getData()){
                        //判断未开奖的
                        if(!bean.isTzKj()&&bean.getTime()>0&&!mGameType.equals(bean.getGame_id())){
                            //开启socket  当前主播游戏时间传值  不需要开启socket
                            SocketUtil.startGameResultSocket(bean.getGame_id());
                        }
                    }
                }
                isFirstLoad=false;//释放掉状态锁
                finishRequest() ;
            }
            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);

                finishRequest() ;
            }
        });
    }

    private void finishRequest(){
        mLoadingPb.setVisibility(View.GONE) ;
        noData.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE) ;

        mRefreshLay.completeLoadMore();
        mRefreshLay.completeRefresh();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateGameInfo(GameTimeEvent event){
        if(CollectionUtils.isEmpty(adapter.getData()))return;
        //获取倒计时
        int time = event.time ;
        String mGameId=event.mGameId;
//        ToastUtil.show(String.valueOf("时间="+time+"  游戏id="+mGameId));
        //判断当前主播游戏等于通知游戏
        if(event.mGameId.equals(mGameType)){
            //判断滑动
            if(!currentGame){
                for(int i=0;i<adapter.getData().size();i++){
                    TzListBean bean=adapter.getData().get(i);
                    if(mGameId.equals(bean.getGame_id())&&!bean.isTzKj()){
                        if(bean.getTime()>0){//防止误差过大  误差大用服务器返回 不用socket返回数据
                            currentGame=true;//加个开关 随时匹配主播房间游戏倒计时
                            bean.setTime(time);
                            bean.setEndTime((time+1)*1000+System.currentTimeMillis());
                            adapter.notifyItemChanged(i,false);
                        }else {
                            currentGame=true;//加个开关 随时匹配主播房间游戏倒计时
                            //从新拉去服务器数据
                            if(!isFirstLoad){
                                isFirstLoad=true;//保证只加载一次数据
                                page=1;
                                getList(page, type);
                            }

                        }
                    }
                }
            }

        }else {
            for(int i=0;i<adapter.getData().size();i++){
                TzListBean bean=adapter.getData().get(i);
                if(mGameId.equals(bean.getGame_id())&&!bean.isTzKj()){
//                    if(bean.getTime()>0){//防止误差过大  误差大用服务器返回 不用socket返回数据
                        bean.setTime(time);
                        bean.setEndTime((time+1)*1000+System.currentTimeMillis());
                        adapter.notifyItemChanged(i);
//                    }
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConsts.USER_LOG);
    }
}
