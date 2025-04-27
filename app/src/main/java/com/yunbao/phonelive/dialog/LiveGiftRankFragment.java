package com.yunbao.phonelive.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.ViewPagerAdapter;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.views.AbsViewHolder;
import com.yunbao.phonelive.views.LiveGiftRankListViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.CommonPagerTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 礼物贡献榜
 */
public class LiveGiftRankFragment extends AbsDialogFragment {
    protected ViewPager mViewPager;

    private MagicIndicator mMagicIndicator;
    private CommonNavigator mCommonNavigator;

    private List<String> mTitleList ;
    private List<LiveGiftRankListViewHolder> mViewHolders ;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_gift_rank ;
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
        params.height = (int) (ScreenDimenUtil.getInstance().getScreenHeight() * 0.7F);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String liveUid = null ;
        Bundle bundle = getArguments() ;
        if(bundle != null){
            liveUid = bundle.getString(Constants.LIVE_UID) ;
        }
        if(TextUtils.isEmpty(liveUid)){
            liveUid = "0" ;
        }

        mMagicIndicator = mRootView.findViewById(R.id.dialog_live_gift_rank_indicator);
        mViewPager = mRootView.findViewById(R.id.dialog_live_gift_rank_vp);

        LiveGiftRankListViewHolder dayHolder = new LiveGiftRankListViewHolder(mContext,mViewPager) ;
        LiveGiftRankListViewHolder weekHolder = new LiveGiftRankListViewHolder(mContext,mViewPager) ;
        LiveGiftRankListViewHolder monthHolder = new LiveGiftRankListViewHolder(mContext,mViewPager) ;

        mViewHolders = new ArrayList<>(3) ;
        mViewHolders.add(dayHolder) ;
        mViewHolders.add(weekHolder) ;
        mViewHolders.add(monthHolder) ;

        dayHolder.setBaseInfo(liveUid,LiveGiftRankListViewHolder.RANK_TYPE_DAY) ;
        weekHolder.setBaseInfo(liveUid,LiveGiftRankListViewHolder.RANK_TYPE_WEEK) ;
        monthHolder.setBaseInfo(liveUid,LiveGiftRankListViewHolder.RANK_TYPE_MONTH) ;
        dayHolder.updateDisplay();

        List<View> list = new ArrayList<>();
        for (AbsViewHolder vh : mViewHolders) {
            list.add(vh.getContentView());
        }
        mViewPager.setAdapter(new ViewPagerAdapter(list));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mViewHolders.get(position).updateDisplay() ;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTitleList = new ArrayList<>() ;
        mTitleList.add("日榜") ;
        mTitleList.add("周榜") ;
        mTitleList.add("月榜") ;

        mCommonNavigator = new CommonNavigator(mContext);
        mCommonNavigator.setSkimOver(true);
        mCommonNavigator.setAdjustMode(true) ;
        mCommonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                CommonPagerTitleView commonPagerTitleView = new CommonPagerTitleView(mContext);
                commonPagerTitleView.setContentView(R.layout.simple_pager_title_layout);

                final TextView titleText = commonPagerTitleView.findViewById(R.id.title_text);
                titleText.setText(mTitleList.get(index));

                commonPagerTitleView.setOnPagerTitleChangeListener(new CommonPagerTitleView.OnPagerTitleChangeListener() {
                    @Override
                    public void onSelected(int index, int totalCount) {
                        titleText.setTextColor(mContext.getResources().getColor(R.color.shenfen));
                    }
                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(mContext.getResources().getColor(R.color.white));
                    }
                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        float scale = 1.2f + (1.0f - 1.2f) * leavePercent ;
                        titleText.setScaleX(scale);
                        titleText.setScaleY(scale);
                    }
                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        float scale = 1.0f + (1.2f - 1.0f) * enterPercent ;
                        titleText.setScaleX(scale);
                        titleText.setScaleY(scale);
                    }
                });

                commonPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });

                return commonPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setYOffset(UIUtil.dip2px(context, 3));
                indicator.setLineWidth(UIUtil.dip2px(context,20)) ;
                indicator.setColors(mContext.getResources().getColor(R.color.red));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(mCommonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    @Override
    public void onDestroy() {
        for(LiveGiftRankListViewHolder holder : mViewHolders){
            holder.relessView() ;
        }
        super.onDestroy();
    }
}
