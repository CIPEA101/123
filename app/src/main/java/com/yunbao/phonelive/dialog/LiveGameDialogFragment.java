package com.yunbao.phonelive.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.adapter.GamePagerAdapter;
import com.yunbao.phonelive.bean.GameTypeBean;
import com.yunbao.phonelive.game.GameBean;
import com.yunbao.phonelive.http.HttpConsts;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏弹窗，选择游戏类型
 *
 * 2020年5月15日 改为从接口获取游戏类型，并根据状态控制显示和隐藏
 */
public class LiveGameDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    ViewPager mViewPager;
    private List<GameBean> mList = new ArrayList<>();

    @Override
    protected int getLayoutId() { return R.layout.dialog_live_games; }

    @Override
    protected int getDialogStyle() { return R.style.dialog2; }

    @Override
    protected boolean canCancel() { return true; }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager = mRootView.findViewById(R.id.recycler);

        initData();
        mViewPager.setOffscreenPageLimit(3);

        GamePagerAdapter adapter = new GamePagerAdapter(mContext,mList);
        mViewPager.setAdapter(adapter);
    }

    private void initData(){
        List<GameTypeBean> gameTypeList = ((LiveActivity)mContext).getGameTypeList() ;
        if(gameTypeList != null && gameTypeList.size() > 0){
            for(GameTypeBean typeBean : gameTypeList){
                String gameTypeId = typeBean.getId() ;
                if(!TextUtils.isEmpty(gameTypeId) && typeBean.isShow()){
                    mList.add(new GameBean(typeBean.getName(),HttpConsts.getGameIconByType(gameTypeId))) ;
                }
            }
        }

//        mList.add(new GameBean(HttpConsts.GAME_NAME_YFKS,R.mipmap.icon_ks_game));
//        mList.add(new GameBean(HttpConsts.GAME_NAME_YF115,R.mipmap.icon_syxw_game));
//        mList.add(new GameBean(HttpConsts.GAME_NAME_YFSC,R.mipmap.icon_syf_game));
//        mList.add(new GameBean(HttpConsts.GAME_NAME_YFSSC,R.mipmap.icon_ssc_game));
//        mList.add(new GameBean(HttpConsts.GAME_NAME_YFLHC,R.mipmap.icon_yfl_game));
//        mList.add(new GameBean(HttpConsts.GAME_NAME_YFKLSF,R.mipmap.icon_ten_game));
//        mList.add(new GameBean(HttpConsts.GAME_NAME_YFXYNC,R.mipmap.icon_xfnc_game));
        Log.e(LiveGameDialogFragment.class.getSimpleName(),mList.toArray().toString());
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        Log.e("FragmentLifeCi","onDismiss") ;

    }
}
