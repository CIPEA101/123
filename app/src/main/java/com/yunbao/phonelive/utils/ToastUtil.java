package com.yunbao.phonelive.utils;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2017/8/3.
 */

public class ToastUtil {

    private static Toast sToast;
    private static Toast sToastLong;
    private static long sLastTime;
    private static String sLastString;

    static {
        sToast = makeToast(false);
        sToastLong = makeToast(true);
    }

    private static Toast makeToast(boolean isLong) {
        Toast toast = new Toast(AppContext.sInstance);
        toast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(AppContext.sInstance).inflate(R.layout.view_toast, null);
        toast.setView(view);
        return toast;
    }


    public static void show(int res) {
        show(WordUtil.getString(res));
    }

    public static void show(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - sLastTime > 2000) {
            sLastTime = curTime;
            sLastString = s;
            sToast.setText(s);
            sToast.show();
        } else {
            if (!s.equals(sLastString)) {
                sLastTime = curTime;
                sLastString = s;
                sToast = makeToast(false);
                sToast.setText(s);
                sToast.show();
            }
        }
    }

    public static void showLong(String s){
        if (TextUtils.isEmpty(s)) {
            return;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - sLastTime > 2000) {
            sLastTime = curTime;
            sLastString = s;
            sToastLong.setText(s);
            sToastLong.show();
        } else {
            if (!s.equals(sLastString)) {
                sLastTime = curTime;
                sLastString = s;
                sToastLong = makeToast(true);
                sToastLong.setText(s);
                sToastLong.show();
            }
        }
    }

}
