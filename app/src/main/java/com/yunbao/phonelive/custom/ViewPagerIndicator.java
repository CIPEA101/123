package com.yunbao.phonelive.custom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.phonelive.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2017/4/19.
 * ViewPager指示器
 */

public class ViewPagerIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {
    private static final String TAG = "ViewPagerIndicator";
    private Context mContext;
    //所有的标题
    private String[] mTitles;
    //可见的item个数
    private int mVisibleItemCount;
    //宽高
    private int mWidth;
    private int mHeight;
    //字体大小
    private float mTextSize;
    //正常字体颜色
    private int mNormalColor;
    //高亮字体颜色
    private int mLightColor;
    private boolean mIsBold;
    private int mIndicatorColor;
    private int[] mNormalColorArgb;
    private int[] mLightColorArgb;
    //下面指示条的宽度
    private int mIndicatorWidth;
    //下面指示条的高度
    private int mIndicatorHeight;
    //指示条到底部的距离
    private int mIndicatorBottomMargin;
    //当前的第几个选项高亮
    private int mCurrentItem;
    //每次滚动偏移量
    private int mScrollX;
    //总偏移量
    private int mTotalScrollX;
    private Paint mPaint;
    private ViewPager mViewPager;

    private boolean mChangeColor;
    private boolean mChangeSize;
    private boolean isAdd;
    private Handler mHandler;
    private int[] mTrianglePositons;
    private boolean mDrawTriangle;
    private float mScale;
    private Paint mTrianglePaint;
    private List<TextView> mTitleList;

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTextSize = a.getDimension(R.styleable.ViewPagerIndicator_title_textSize, 0);
        mIndicatorWidth = (int) a.getDimension(R.styleable.ViewPagerIndicator_indicatorWidth, 0);
        mIndicatorHeight = (int) a.getDimension(R.styleable.ViewPagerIndicator_indicatorHeight, 0);
        mIndicatorBottomMargin = (int) a.getDimension(R.styleable.ViewPagerIndicator_indicatorBottomMargin, dp2px(2));
        mNormalColor = a.getColor(R.styleable.ViewPagerIndicator_normalColor, Color.BLACK);
        mLightColor = a.getColor(R.styleable.ViewPagerIndicator_lightColor, Color.BLACK);
        mIndicatorColor = a.getColor(R.styleable.ViewPagerIndicator_indicatorColor, Color.BLACK);
        mNormalColorArgb = getColorArgb(mNormalColor);
        mLightColorArgb = getColorArgb(mLightColor);
        mCurrentItem = a.getInteger(R.styleable.ViewPagerIndicator_currentItem, 0);
        mChangeColor = a.getBoolean(R.styleable.ViewPagerIndicator_change_color, true);
        mChangeSize = a.getBoolean(R.styleable.ViewPagerIndicator_change_size, true);
        mIsBold = a.getBoolean(R.styleable.ViewPagerIndicator_bold, false);
        mVisibleItemCount = a.getInteger(R.styleable.ViewPagerIndicator_visibleItemCount, 1);
        a.recycle();
        setOrientation(HORIZONTAL);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int position = msg.what;
                startAnim(position);
            }
        };
        initPaint();
        mScale = context.getResources().getDisplayMetrics().density;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mIndicatorColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mIndicatorHeight);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        if (!isAdd) {
            isAdd = true;
            addChild(widthMeasureSpec, heightMeasureSpec);
        }
    }


    private void addChild(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTitles == null || mVisibleItemCount == 0) {
            return;
        }
        mScrollX = mWidth / mVisibleItemCount;
        mTotalScrollX = mCurrentItem * mScrollX;
        mTitleList = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            final TextView textView = new TextView(mContext);
            mTitleList.add(textView);
            LayoutParams params = new LayoutParams(mScrollX, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(params);
            textView.setText(mTitles[i]);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            if (mIsBold) {
                textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            if (mCurrentItem == i) {
                textView.setTextColor(mNormalColor);
            } else {
                textView.setTextColor(mNormalColor);
            }
            //textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            final int finalI = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewPager != null) {
                        if (finalI == mViewPager.getCurrentItem()) {
                            return;
                        }
                        if (mListener != null) {
                            mListener.onTabClick(finalI);
                        }
                        mViewPager.setCurrentItem(finalI, true);
                    }
                }
            });
            addView(textView);
            textView.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 画出下面的横线
     *
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.translate(0, mHeight);
        //实际绘制的指示条的宽度
        int w = Math.min(mIndicatorWidth, mScrollX);
        //求出右上角和左下角的坐标
        int x1 = (mScrollX - w) / 2 + mTotalScrollX;
        int y1 = -mIndicatorHeight - mIndicatorBottomMargin;
        int x2 = x1 + w;
        int y2 = -mIndicatorBottomMargin;
        if (mDrawTriangle) {//画三角形
            canvas.drawLine(x1 + dp2px(8), -dp2px(10), x1 + w / 2, y2, mTrianglePaint);
            canvas.drawLine(x1 + w / 2, y2, x2 - dp2px(8), -dp2px(10), mTrianglePaint);
        } else {
            //画出矩形
            canvas.drawLine(x1, y1, x2, y1, mPaint);
        }
        canvas.restore();
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
    }

    public void setVisibleChildCount(int visibleChildCount) {
        mVisibleItemCount = visibleChildCount;
    }

    public void setTitleText(int position, String text) {
        if (mTitleList != null && position >= 0 && position < mTitleList.size()) {
            TextView textView = mTitleList.get(position);
            if (textView != null) {
                textView.setText(text);
            }
        }
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
    }

    private int[] getColorArgb(int color) {
        return new int[]{Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color)};
    }


    public void setChangeColor(boolean changeColor) {
        mChangeColor = changeColor;
    }

    public void setChangeSize(boolean changeSize) {
        mChangeSize = changeSize;
    }

    /**
     * 文字变颜色
     *
     * @param position
     * @param rate
     */
    private void textChangeColor(int position, float rate) {
        int a = (int) (mNormalColorArgb[0] * (1 - rate) + mLightColorArgb[0] * rate);
        int r = (int) (mNormalColorArgb[1] * (1 - rate) + mLightColorArgb[1] * rate);
        int g = (int) (mNormalColorArgb[2] * (1 - rate) + mLightColorArgb[2] * rate);
        int b = (int) (mNormalColorArgb[3] * (1 - rate) + mLightColorArgb[3] * rate);
        TextView textView = (TextView) getChildAt(position);
        if (textView != null) {
            textView.setTextColor(Color.argb(a, r, g, b));
        }
    }


    /**
     * 文字缩放
     *
     * @param position
     * @param rate
     */
    private void textChangeScale(int position, float rate) {
        TextView textView = (TextView) getChildAt(position);
        if (textView != null) {
            textView.setScaleX(rate);
            textView.setScaleY(rate);
        }
    }

    /**
     * 滚动的方法
     *
     * @param position
     * @param positionOffset
     */
    private void scroll(int position, float positionOffset) {
        mTotalScrollX = (int) ((position + positionOffset) * mScrollX);
        invalidate();

        if (mChangeColor) {
            changeColor(position, positionOffset);
        }
        if (mChangeSize) {
            changeScale(position, positionOffset);
        }
    }

    /**
     * 文字颜色变化
     */
    private void changeColor(int position, float positionOffset) {
        textChangeColor(position, 1 - positionOffset);
        textChangeColor(position + 1, positionOffset);
    }

    /**
     * 文字大小变化
     */
    private void changeScale(int position, float positionOffset) {
        textChangeScale(position, 1 + (1 - positionOffset) * 0.3f);
        textChangeScale(position + 1, 1 + positionOffset * 0.3f);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(mTitleList == null){
            return ;
        }

        for (int i =0 ; i< mTitleList.size(); i++){
            if (i == position){
                mTitleList.get(i).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }else{
                mTitleList.get(i).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }
        scroll(position, positionOffset);
        if (mListener != null) {
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    private ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (float) animation.getAnimatedValue();
            scrollTo((int) value, 0);
        }
    };

    private void startAnim(int position) {
        int target = (position - mVisibleItemCount + 2) * mScrollX;
        if (getScrollX() != target) {
            ValueAnimator a = ObjectAnimator.ofFloat(this, "scrollX", getScrollX(), target);
            a.addUpdateListener(listener);
            a.setDuration(250);
            a.start();
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mVisibleItemCount != mTitles.length && position >= mVisibleItemCount - 2 && position != mTitles.length - 1) {
            mHandler.sendEmptyMessageDelayed(position, 100);
        }
        if (mTrianglePositons != null) {
            mDrawTriangle = false;
            for (int p : mTrianglePositons) {
                if (p == position) {
                    mDrawTriangle = true;
                    break;
                }
            }
        }

        if (mListener != null) {
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 0) {
            fixTextColorChange();
        }
        if (mListener != null) {
            mListener.onPageScrollStateChanged(state);
        }
    }

    /**
     * 修复由于滚动造成的文字颜色不正常的问题
     */
    private void fixTextColorChange() {
        for (int i = 0; i < getChildCount(); i++) {
            if (i != mViewPager.getCurrentItem()) {
                TextView textView = (TextView) getChildAt(i);
                if (textView.getCurrentTextColor() != mNormalColor) {
                    textView.setTextColor(mNormalColor);
                }
            }
        }
    }

    private OnPageChangeListener mListener;

    public void setListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    public interface OnPageChangeListener {

        void onTabClick(int position);

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    public void setTrianglePositions(int[] positions) {
        mTrianglePositons = positions;
        if (mTrianglePositons != null) {
            mTrianglePaint = new Paint();
            mTrianglePaint.setAntiAlias(true);
            mTrianglePaint.setDither(true);
            mTrianglePaint.setColor(mLightColor);
            mTrianglePaint.setStrokeWidth(dp2px(2));
            mTrianglePaint.setStyle(Paint.Style.STROKE);
        }
    }

    //dp转成px
    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }

}
