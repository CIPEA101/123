package com.yunbao.phonelive.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ViewUtils {


    public static boolean isSlideBottom(RecyclerView rv){
        if(null == rv){
            return false ;
        }

        return !rv.canScrollVertically(1) ;//是否能向上滚动，false表示已经滚动到底部
    }


    public static Bitmap saveViewToBitmap(View v){
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

}
