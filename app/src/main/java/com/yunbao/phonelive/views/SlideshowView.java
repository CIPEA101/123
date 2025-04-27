package com.yunbao.phonelive.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.baidu.speech.utils.LogUtil;
import com.bumptech.glide.Glide;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.WebViewPagerActivity;
import com.yunbao.phonelive.bean.RollPicBean;
import com.yunbao.phonelive.utils.DpUtil;

import java.util.ArrayList;
import java.util.List;

///轮播控件

public class SlideshowView extends FrameLayout {
    //自动轮播的时间间隔
    private final static int TIME_INTERVAL = 3 * 1000;

    //自动轮播启用开关
    private final static boolean isAutoPlay = true;

    //自定义轮播图的资源
    private String[] imageUrls;
    //跳转连接
    private String[] imageJumps;
    //放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;
    //放圆点的View的list
    private List<View> dotViewsList;

    private boolean mIsBotStyle = false ;

    private boolean mIsPaddMode = false ;//有间距的模式

    private ViewPager viewPager;
    //当前轮播页
    private int currentItem  = 0;

    private Context context;

    private static final int MSG_WHAT_BANNER = 10 ;
    //Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(MSG_WHAT_BANNER == msg.what){
                updateCurrentItem() ;
            }
        }

    };

    public SlideshowView(Context context) {
        this(context,null);
        // TODO Auto-generated constructor stub
    }
    public SlideshowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }
    public SlideshowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        initData();
        if(isAutoPlay){
            startPlay();
        }

    }
    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        if(handler != null) {
            handler.sendEmptyMessageDelayed(MSG_WHAT_BANNER, TIME_INTERVAL);
        }
    }
    /**
     * 停止轮播图切换
     */
    private void stopPlay(){
        if(handler != null) {
            handler.removeMessages(MSG_WHAT_BANNER);
        }
    }

    public void destory(){
        if(handler != null){
            handler.removeCallbacksAndMessages(null) ;
        }
    }

    private void updateCurrentItem(){
        if(imageViewsList.size() <= 1){
            return;
        }
        currentItem = currentItem + 1;

        viewPager.setCurrentItem(currentItem);
        handler.sendEmptyMessageDelayed(MSG_WHAT_BANNER,TIME_INTERVAL) ;
    }

    /**
     * 初始化相关Data
     */
    private void initData(){
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();

    }

    public void setBotStyle(boolean botStyle){
        mIsBotStyle = botStyle ;
    }

    public void setPaddMode(boolean isMode){
        mIsPaddMode = isMode ;
    }

    public void addDataToUI(List<RollPicBean> rollPics){
        addDataToUI(rollPics,false) ;
    }

    public void addDataToUI(List<RollPicBean> rollPics,boolean refresh){
        if(null == rollPics){
            return ;
        }
        if(refresh){
            stopPlay();
            imageViewsList.clear();
            dotViewsList.clear();
        }

        LogUtil.e("addDataToUI","length=" + rollPics.size()) ;

        imageUrls = new String[rollPics.size()];
        imageJumps = new String[rollPics.size()];

        for(int i = 0; i<rollPics.size(); i++){
            RollPicBean rollPicBean = rollPics.get(i) ;
            imageUrls[i] = rollPicBean.slide_pic;
            imageJumps[i] = rollPicBean.slide_url;
        }

        initUI(context);
    }
    /**
     * 初始化Views等UI
     */
    private void initUI(final Context context){
        if(imageUrls == null || imageUrls.length == 0)
            return;

        removeAllViews() ;

//        if(mIsBotStyle){
//            LayoutInflater.from(context).inflate(R.layout.layout_slideshow_v, this, true);
//        }else{
//            LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);
//        }
        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this, true);

        LinearLayout dotLayout = findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();

        // 热点个数与图片特殊相等
        for (int i = 0; i < imageUrls.length; i++) {
            ImageView view =  new ImageView(context);

            if(i == 0)//给一个默认图
                view.setImageDrawable(getResources().getDrawable(R.color.drawable_normal_dv)) ;

            view.setScaleType(ScaleType.FIT_XY);
            if(mIsPaddMode){
                view.setPadding(DpUtil.dp2px(10),0, DpUtil.dp2px(10),0) ;
            }

            imageViewsList.add(view);

            ImageView dotView =  new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 4;
            params.rightMargin = 4;
            params.width = dip2px(context, 6);
            params.height = dip2px(context, 6);

            if(0 == i){
                dotView.setBackgroundResource(R.drawable.bg_banner_dv_checked);
            }else {
                dotView.setBackgroundResource(R.drawable.bg_banner_dv_normal);
            }

            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }

        viewPager = findViewById(R.id.viewPager);
        viewPager.setFocusable(true);

        if(mIsPaddMode){
            viewPager.setClipChildren(false) ;
        }

        viewPager.setAdapter(new BannerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());

        if(isAutoPlay){
            startPlay();
        }
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 填充ViewPager的页面适配器
     *
     */
    private class BannerAdapter  extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView(imageViewsList.get(toRealPosition(position)));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            ImageView imageView = imageViewsList.get(toRealPosition(position));
            //点击跳转连接
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String linkUrl = imageJumps[toRealPosition(position)] ;
                    if(!TextUtils.isEmpty(linkUrl)){
                        WebViewPagerActivity.start(context,linkUrl,false) ;
                    }
                }
            });

            /*Glide
                    .with(context)
                    .load(imageUrls[toRealPosition(position)])
                    .centerCrop()
                    .placeholder(R.drawable.default_banner_pic)
                    .crossFade()
                    .into(imageView);*/

            Glide.with(context)
                    .load(imageUrls[toRealPosition(position)])
                    .fitCenter()
                    .placeholder(R.drawable.default_banner_pic)
                    .crossFade()
                    .into(imageView);

            ViewGroup viewGroup = ((ViewGroup)imageView.getParent());

            if(viewGroup != null){
                viewGroup.removeView(imageView);
            }

            container.addView(imageView);
            return imageView;
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(imageViewsList.size() <= 1 ){
                return 1;
            }
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public final void finishUpdate(ViewGroup container) {
            // 数量为1，不做position替换
            if (imageViewsList.size() <= 1) {
                return;
            }

            int position = viewPager.getCurrentItem();
            // ViewPager的更新即将完成，替换position，以达到无限循环的效果
            if (position == 0) {
                position = imageViewsList.size();
                viewPager.setCurrentItem(position, false);
            } else if (position == getCount() - 1) {
                position = imageViewsList.size() - 1;
                viewPager.setCurrentItem(position, false);
            }
        }
        public int toRealPosition(int position) {
            int realCount = imageViewsList.size();
            if (realCount == 0)
                return 0;

            int realPosition = position % realCount;
            if(realPosition < 0){
                realPosition += imageViewsList.size();
            }
            return realPosition;
        }

    }



    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     *
     */
    private class MyPageChangeListener implements OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub

            currentItem = pos;
            for(int i = 0; i < dotViewsList.size();i++){
                if(i == (pos%imageViewsList.size())){
                    dotViewsList.get(i).setBackgroundResource(R.drawable.bg_banner_dv_checked);
                }else {
                    dotViewsList.get(i).setBackgroundResource(R.drawable.bg_banner_dv_normal);
                }
            }
        }
    }

}
