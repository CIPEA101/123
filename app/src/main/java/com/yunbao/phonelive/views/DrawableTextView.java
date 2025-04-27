package com.yunbao.phonelive.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class DrawableTextView extends TextView {

    public DrawableRightClickListener drawableRightClickListener;

    public DrawableTextView(Context context) {
        super(context);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public DrawableRightClickListener getDrawableRightClick() {
        return drawableRightClickListener;
    }

    public void setDrawableRightClick(DrawableRightClickListener drawableRightClickListener) {
        this.drawableRightClickListener = drawableRightClickListener;
    }

    //为了方便,直接写了一个内部类的接口
    public interface DrawableRightClickListener{
        void onDrawableRightClickListener(View view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (drawableRightClickListener != null) {
                    // getCompoundDrawables获取是一个数组，数组0,1,2,3,对应着左，上，右，下 这4个位置的图片，如果没有就为null
                    Drawable rightDrawable = getCompoundDrawables()[2];
                    //判断的依据是获取点击区域相对于屏幕的x值比我(获取TextView的最右边界减去边界宽度)大就可以判断点击在Drawable上
                    if (rightDrawable != null && event.getRawX() >= (getRight() - rightDrawable.getBounds().width())) {
                        drawableRightClickListener.onDrawableRightClickListener(this);
                    }
                    //此处不能设置成false,否则drawable不会触发点击事件,如果设置,TextView会处理事件
                    return false;
                }

                break;

        }
        return super.onTouchEvent(event);
    }
}
