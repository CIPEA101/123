package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.ActivityExtension;
import com.yunbao.phonelive.adapter.ViewPagerAdapter;
import com.yunbao.phonelive.custom.MyViewPager;

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

public class MainHomeViewHolderNew extends AbsMainViewHolder {
    protected AbsMainViewHolder[] mViewHolders;
    protected MyViewPager mViewPager;

    protected List<String> mTitleList ;

    private View mTgzqIv ;

    public MainHomeViewHolderNew(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_home_new;
    }

    @Override
    public void init() {

        mTgzqIv = findViewById(R.id.view_main_home_tgzq_iv) ;
        mTgzqIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityExtension.launch(mContext) ;

            }
        });

        mViewPager = (MyViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(3);
        MagicIndicator mIndicator = (MagicIndicator) findViewById(R.id.view_main_home_magic_indicator);

        mViewHolders = new AbsMainChildTopViewHolder[2];
        mViewHolders[0] = new MainHomeLiveViewHolderNew(mContext, mViewPager);
        mViewHolders[1] = new MainHomeFollowViewHolder(mContext, mViewPager);

        List<View> list = new ArrayList<>();
        for (AbsMainViewHolder vh : mViewHolders) {
            list.add(vh.getContentView());
        }
        mViewPager.setAdapter(new ViewPagerAdapter(list));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mTgzqIv.setVisibility(0 == position ? View.VISIBLE : View.GONE) ;

                mViewHolders[position].loadData() ;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTitleList = new ArrayList<>(2) ;
        mTitleList.add("热门") ;
        mTitleList.add("关注") ;

        CommonNavigator mCommonNavigator = new CommonNavigator(mContext);
        mCommonNavigator.setSkimOver(true);
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
                        titleText.setTextColor(mContext.getResources().getColor(R.color.white));
                    }
                    @Override
                    public void onDeselected(int index, int totalCount) {
                        titleText.setTextColor(mContext.getResources().getColor(R.color.white));
                    }
                    @Override
                    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
                        float scale = 1.5f + (1.0f - 1.5f) * leavePercent ;
                        titleText.setScaleX(scale);
                        titleText.setScaleY(scale);
                    }
                    @Override
                    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
                        float scale = 1.0f + (1.5f - 1.0f) * enterPercent ;
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
                indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                indicator.setYOffset(UIUtil.dip2px(context, 3));
                indicator.setColors(mContext.getResources().getColor(R.color.white));
                return indicator;
            }
        });
        mIndicator.setNavigator(mCommonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }
}
