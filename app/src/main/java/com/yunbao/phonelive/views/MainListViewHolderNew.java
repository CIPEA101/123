package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.ViewPagerAdapter;
import com.yunbao.phonelive.interfaces.MainAppBarLayoutListener;

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
 * Created by cxf on 2018/9/22.
 * 排行
 */

public class MainListViewHolderNew extends AbsMainViewHolder {
    protected ViewPager mViewPager;

    private MagicIndicator mMagicIndicator;
    private CommonNavigator mCommonNavigator;

    private List<String> mTitleList ;

    private List<MainRankListViewHolder> mViewHolders ;

    public MainListViewHolderNew(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_list_new;
    }

    @Override
    public void init() {
        mViewPager = (ViewPager) findViewById(R.id.act_rank_list_vp);
        mMagicIndicator = (MagicIndicator) findViewById(R.id.act_rank_list_magic_indicator);

        MainRankListViewHolder starHolder = new MainRankListViewHolder(mContext,mViewPager) ;
        MainRankListViewHolder contributeHolder = new MainRankListViewHolder(mContext,mViewPager) ;
        MainRankListViewHolder richHolder = new MainRankListViewHolder(mContext,mViewPager) ;

        mViewHolders = new ArrayList<>(3) ;
        mViewHolders.add(starHolder) ;
        mViewHolders.add(contributeHolder) ;
        mViewHolders.add(richHolder) ;

        starHolder.setBaseInfo(MainRankListViewHolder.RANK_TYPE_REWARD) ;
        contributeHolder.setBaseInfo(MainRankListViewHolder.RANK_TYPE_CONTRIBUTE) ;
        richHolder.setBaseInfo(MainRankListViewHolder.RANK_TYPE_RICH) ;
        starHolder.updateDisplay();

        List<View> list = new ArrayList<>();
        for (AbsMainViewHolder vh : mViewHolders) {
            list.add(vh.getContentView());
        }
        mViewPager.setAdapter(new ViewPagerAdapter(list));
        mViewPager.setOffscreenPageLimit(4);
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
        mTitleList.add("明星榜") ;
        mTitleList.add("贡献榜") ;
        mTitleList.add("富豪榜") ;

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
                        titleText.setTextColor(mContext.getResources().getColor(R.color.fense));
                    }
                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(mContext.getResources().getColor(R.color.textColor));
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
                indicator.setColors(mContext.getResources().getColor(R.color.fense));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(mCommonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    @Override
    public void setAppBarLayoutListener(MainAppBarLayoutListener appBarLayoutListener) {

    }

    @Override
    public void loadData() {
        mViewHolders.get(mViewPager.getCurrentItem()).updateDisplay();
    }

}
