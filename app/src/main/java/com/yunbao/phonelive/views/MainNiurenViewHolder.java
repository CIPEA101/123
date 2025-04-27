package com.yunbao.phonelive.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.AbsActivity;
import com.yunbao.phonelive.adapter.AdapterNiurenList;
import com.yunbao.phonelive.adapter.AdapterNiurenTop;
import com.yunbao.phonelive.adapter.HeaderFooterAdapter;
import com.yunbao.phonelive.bean.NiurenInfoBean;
import com.yunbao.phonelive.bean.NiurenList;
import com.yunbao.phonelive.bean.NiurenTop;
import com.yunbao.phonelive.bean.NiurenTopBean;
import com.yunbao.phonelive.custom.RefreshLayout;
import com.yunbao.phonelive.dialog.LiveGameTzInfoDialogFragment;
import com.yunbao.phonelive.event.GameTimeEvent;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.socket.SocketUtil;
import com.yunbao.phonelive.utils.CollectionUtils;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainNiurenViewHolder extends AbsMainViewHolder {
    private RefreshLayout mRefreshLay ;
    private RecyclerView mRv ;

    private RecyclerView mTopRv ;

    private List<NiurenTopBean> mTopList ;
    private AdapterNiurenTop mTopAdapter ;

    private List<NiurenList> mDateList ;
    private HeaderFooterAdapter<AdapterNiurenList> mAdapter ;

    private int mLastTimeValue = 0 ;

    private Dialog mLoadingDialog ;

    public MainNiurenViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_niuren_lay;
    }

    @Override
    public void init() {

        mLoadingDialog = DialogUitl.loadingDialog(mContext) ;

        mRefreshLay = (RefreshLayout) findViewById(R.id.view_main_niuren_refresh_lay);
        mRefreshLay.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNiurenInfo() ;
            }
            @Override
            public void onLoadMore() {
            }
        });
        mRefreshLay.setRefreshEnable(true);
        mRefreshLay.setLoadMoreEnable(false) ;

        mRv = (RecyclerView) findViewById(R.id.view_main_niuren_rv);

        mRefreshLay.setScorllView(mRv) ;

        mRv.setLayoutManager(new LinearLayoutManager(mContext));

        mDateList = new ArrayList<>() ;
        AdapterNiurenList adapter = new AdapterNiurenList(mContext, mDateList, new OnRvClickListener() {
            @Override
            public void onClick(int type, int position) {
                //有个header
                int realPosition = position - 1 ;

                if(1 == type){//跟投
                    if(realPosition >= 0 && realPosition < mDateList.size()){
                        NiurenList nrList = mDateList.get(realPosition) ;

                        LiveGameTzInfoDialogFragment tzFragment = new LiveGameTzInfoDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.LIVE_GAME_ID, nrList.getGame_id());
                        bundle.putSerializable(Constants.LIVE_GAME_NAME_TZ_LIST, (Serializable) nrList.getTypes()) ;
                        tzFragment.setArguments(bundle);
                        tzFragment.show(((AbsActivity) mContext).getSupportFragmentManager(), HttpConsts.LIVE_GAME_TZ_INFO_DIALOG_FRAGMENT);
                    }
                }
            }
        }) ;
        mAdapter = new HeaderFooterAdapter<>(adapter) ;

        View headerView = LayoutInflater.from(mContext).inflate(R.layout.header_niuren_list_lay,null) ;
        mAdapter.addHeader(headerView) ;
        mRv.setAdapter(mAdapter) ;

        mTopRv = headerView.findViewById(R.id.header_niuren_list_top_rv) ;
        mTopList = new ArrayList<>() ;

        mTopAdapter = new AdapterNiurenTop(mContext,mTopList) ;
        mTopRv.setLayoutManager(new GridLayoutManager(mContext,3));
        mTopRv.setAdapter(mTopAdapter) ;
    }

    /**
     * 填充内容
     * @param info info
     */
    private void updateInfo(NiurenInfoBean info){
        mLoadingDialog.dismiss() ;

        if(info != null){

            //三个榜首
            mTopList.clear() ;
            String userName1 = "朵朵来了" ;
            String userAvatar1 = "" ;
            String value1 = "100万" ;
            if(info.getFinancial_gas() != null){
                NiurenTop oneInfo = info.getFinancial_gas() ;
                if(!TextUtils.isEmpty(oneInfo.getUser_nickname())){
                    userName1 = oneInfo.getUser_nickname() ;
                    userAvatar1 = StringUtil.convertImageUrl(oneInfo.getAvatar()) ;
                }
                if(!TextUtils.isEmpty(oneInfo.getNum())){
                    value1 = StringUtil.convertNull(oneInfo.getNum()) ;
                }
            }
            mTopList.add(new NiurenTopBean(userName1,userAvatar1,R.color.color_nr_top_one
                    ,"财气主播",R.color.color_nr_top_one1,value1 + " 财气值")) ;

            //胜率榜
            String userName2 = "我杨树林" ;
            String userAvatar2 = "" ;
            String value2 = "50" ;

            if(info.getProbability() != null){
                NiurenTop topInfo = info.getProbability() ;
                if(!TextUtils.isEmpty(topInfo.getUser_nickname())){
                    userName2 = topInfo.getUser_nickname() ;
                    userAvatar2 = StringUtil.convertImageUrl(topInfo.getAvatar()) ;
                }
                if(!TextUtils.isEmpty(topInfo.getPercentage())){
                    value2 = StringUtil.convertNull(topInfo.getPercentage()) ;
                    if(StringUtil.convertFloat(value2) > 100F){
                        value2 = "100" ;
                    }
                }
            }

            mTopList.add(new NiurenTopBean(userName2,userAvatar2,R.color.color_nr_top_two
                    ,"神算子",R.color.color_nr_top_two1,value2 + "% 胜率")) ;

            //财富榜
            String userName3 = "哈哈打我呀" ;
            String userAvatar3 = "" ;
            String value3 = "1000万" ;

            if(info.getFortune() != null){
                NiurenTop topInfo = info.getFortune() ;
                if(!TextUtils.isEmpty(topInfo.getUser_nickname())){
                    userName3 = topInfo.getUser_nickname() ;
                    userAvatar3 = StringUtil.convertImageUrl(topInfo.getAvatar()) ;
                }

                if(!TextUtils.isEmpty(topInfo.getNum())){
                    value3 = StringUtil.convertNull(topInfo.getNum()) ;
                }
            }
            mTopList.add(new NiurenTopBean(userName3,userAvatar3,R.color.color_nr_top_three
                    ,"富豪榜",R.color.color_nr_top_three1,value3 + " 财富值")) ;
            mTopAdapter.notifyDataSetChanged() ;

            mDateList.clear();
            mDateList.addAll(info.getLists()) ;
            if(CollectionUtils.isNotEmpty(mDateList)){
                for(NiurenList list:mDateList){
                    list.setEndTime(60*1000+System.currentTimeMillis());
                    //开启socket  当前主播游戏时间传值  不需要开启socket
                    SocketUtil.startGameResultSocket(list.getGame_id());
                }
            }
            mAdapter.notifyDataSetChanged() ;
        }
    }

    @Override
    public void loadData() {
        if(mTopList.size() == 0){
            mLoadingDialog.show() ;
        }
        getTopInfo();
    }

    /**
     * 获取顶部三个数据
     */
    private void getTopInfo(){
        getNiurenInfo() ;
    }
    /**
     * 获取牛人信息
     */
    private void getNiurenInfo(){
        int lelTime = 0;
        if(mLastTimeValue > 0){
            lelTime = 30 - mLastTimeValue ;
        }
        if(lelTime < 0 ){
            lelTime = 0 ;
        }
        HttpUtil.niurenList(lelTime ,new CommonCallback<NiurenInfoBean>() {
            @Override
            public void callback(NiurenInfoBean bean) {
                mRefreshLay.completeRefresh() ;

                updateInfo(bean) ;
            }
        });
    }

    public void updateGameInfo(GameTimeEvent event){
        if(CollectionUtils.isEmpty(mDateList))return;
        //获取倒计时
        int time = event.time ;
        String mGameId=event.mGameId;
        for(int i=0;i<mDateList.size();i++){
            NiurenList bean=mDateList.get(i);
            if(mGameId.equals(bean.getGame_id())){
                bean.setEndTime((time+1)*1000+System.currentTimeMillis());
                mAdapter.notifyItemChanged(i+1);
            }
        }
    }
}
