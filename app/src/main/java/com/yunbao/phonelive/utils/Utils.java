package com.yunbao.phonelive.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunbao.phonelive.R;


public class Utils {
    /**
     * 从context获取Activity
     * @param context
     * @return
     */
    public static Activity getActivityFromContext(Context context) {
        Activity activity = null;
        while (activity == null && context != null) {
            if (context instanceof Activity) {
                activity = (Activity) context;  // found it!
            } else {
                context = (context instanceof ContextWrapper) ?
                        ((ContextWrapper) context).getBaseContext() : // unwrap one level
                        null;                                         // done
            }
        }
        return activity;
    }
    /**
     * action sheet dialog
     *
     * @param context
     * @param view
     * @param gravity
     * @return
     * isanimations 是否显示动画
     * scale  屏幕显示比例
     */
    public static Dialog getActionSpSheet(Context context, View view, int gravity, boolean isanimations, int scale) {
        final Dialog dialog = new Dialog(context, R.style.action_sheet);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();

        int screen[] = getScreenSize(context);
        lp.width = screen[0];
        lp.height = screen[1]/scale;
        window.setGravity(gravity); // 此处可以设置dialog显示的位置
        if(isanimations){// 添加动画
            window.setWindowAnimations(R.style.action_sheet_animation);
        }
        return dialog;
    }
    public static int[] getScreenSize(Context context) {
        int[] screenSize = new int[2];
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenSize[0] = dm.widthPixels;
        screenSize[1] = dm.heightPixels;
        return screenSize;
    }
    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = ((Activity) context).getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ((Activity) context).getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
