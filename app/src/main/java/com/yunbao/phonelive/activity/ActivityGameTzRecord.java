package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
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
import com.yunbao.phonelive.utils.DialogUitl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 游戏投注历史
 * 跟 LiveGameAwardHistoryFragment 一样
 */
public class ActivityGameTzRecord extends AbsActivity implements View.OnClickListener, RefreshLayout.OnRefreshListener {
    private RelativeLayout rlAA;
    private TextView noData;
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

    public static void launch(Context context){
        Intent toIt = new Intent(context,ActivityGameTzRecord.class) ;
        context.startActivity(toIt) ;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_tz_record;
    }

    @Override
    protected void main() {
        super.main();

        setTitle("投注记录");
        EventBus.getDefault().register(this);

        rlAA = findViewById(R.id.rl_all_award);
        tvAA = findViewById(R.id.tv_all_award);
        vAA = findViewById(R.id.view_all_award);
        rlNA = findViewById(R.id.rl_no_award);
        tvNA = findViewById(R.id.tv_no_award);
        vNA = findViewById(R.id.view_no_award);
        rlLA = findViewById(R.id.rl_lottery);
        tvLA = findViewById(R.id.tv_lottery);
        vLA = findViewById(R.id.view_lottery);
        recyclerView = findViewById(R.id.recycler);
        noData = findViewById(R.id.tv_no_data);


        mRefreshLay = findViewById(R.id.act_game_tz_record_refresh_lay);
        recyclerView = findViewById(R.id.act_game_tz_record_rv);

        mRefreshLay.setScorllView(recyclerView);
        mRefreshLay.setOnRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

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
        noData.setVisibility(View.GONE);

        HttpUtil.getTzRecord(String.valueOf(page), String.valueOf(ok), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    try {
                        list = JSON.parseArray(Arrays.toString(info), TzListBean.class);
                        for(TzListBean bean:list){
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
                        if (list != null && list.size() > 0) {
                            adapter = new AwardGameHistoryAdapter(mContext, getLayoutInflater(), list,type);
                            recyclerView.setAdapter(adapter);

                            noData.setVisibility(View.GONE);
                        }else{
                            noData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (list != null && list.size() > 0) {
                            adapter.addList(list);
                        }
                    }
                    for(TzListBean bean:list){
                        //判断未开奖的
                        if(!bean.isTzKj()&&bean.getTime()>0){
                            //开启socket
                            SocketUtil.startGameResultSocket(bean.getGame_id());
                        }
                    }
                }

                mRefreshLay.completeLoadMore();
                mRefreshLay.completeRefresh();
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);

                if(page == 1){
                    noData.setVisibility(View.VISIBLE);

                    if(adapter != null){
                        adapter.release() ;
                        adapter.notifyDataSetChanged() ;
                    }
                }

                mRefreshLay.completeLoadMore();
                mRefreshLay.completeRefresh();
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext) ;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        HttpUtil.cancel(HttpConsts.USER_LOG) ;

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateGameInfo(GameTimeEvent event){
        //获取倒计时
        int time = event.time ;
        String mGameId=event.mGameId;
//        ToastUtil.show(String.valueOf("时间="+time+"  游戏id="+mGameId));
        for(int i=0;i<list.size();i++){
            TzListBean bean=list.get(i);
            if(mGameId.equals(bean.getGame_id())&&!bean.isTzKj()){
//                if(bean.getTime()>0){//防止误差过大  误差大用服务器返回 不用socket返回数据
                    bean.setTime(time);
                    bean.setEndTime((time+1)*1000+System.currentTimeMillis());
                    adapter.notifyItemChanged(i);
//                }
            }
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

}
