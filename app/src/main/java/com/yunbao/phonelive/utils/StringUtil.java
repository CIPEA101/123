package com.yunbao.phonelive.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by cxf on 2018/9/28.
 */

public class StringUtil {
    private static DecimalFormat sDecimalFormat;
    private static DecimalFormat sDecimalFormat2;
    // private static Pattern sPattern;
    private static Pattern sIntPattern;


    static {
        sDecimalFormat = new DecimalFormat("#.#");
        sDecimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        sDecimalFormat2 = new DecimalFormat("#.##");
        sDecimalFormat2.setRoundingMode(RoundingMode.DOWN);
        //sPattern = Pattern.compile("[\u4e00-\u9fa5]");
        sIntPattern = Pattern.compile("^[-\\+]?[\\d]*$");
    }

    public static String format(double value) {
        return sDecimalFormat.format(value);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(num / 10000d) + "W";
    }


    /**
     * 把数字转化成多少万
     */
    public static String toWan2(String num) {
        if (StringUtil.convertFloat(num) < 10000F) {
            return String.valueOf(num);
        }
        return sDecimalFormat.format(StringUtil.convertFloat(num) / 10000d);
    }

    /**
     * 把数字转化成多少万
     */
    public static String toWan3(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        return sDecimalFormat2.format(num / 10000d) + "w";
    }

//    /**
//     * 判断字符串中是否包含中文
//     */
//    public static boolean isContainChinese(String str) {
//        Matcher m = sPattern.matcher(str);
//        if (m.find()) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断一个字符串是否是数字
     */
    public static boolean isInt(String str) {
        return sIntPattern.matcher(str).matches();
    }

    public static int convertInt(String str){
        try {
            return Integer.valueOf(str) ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0 ;
    }

    public static float convertFloat(String temp){
        if(TextUtils.isEmpty(temp)){
            return 0F ;
        }

        try {
            return Float.valueOf(temp) ;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0F ;
    }

    /**
     * 把一个long类型的总毫秒数转成时长
     */
    public static String getDurationText(long mms) {
        int hours = (int) (mms / (1000 * 60 * 60));
        int minutes = (int) ((mms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((mms % (1000 * 60)) / 1000);
        String s = "";
        if (hours > 0) {
            if (hours < 10) {
                s += "0" + hours + ":";
            } else {
                s += hours + ":";
            }
        }
        if (minutes > 0) {
            if (minutes < 10) {
                s += "0" + minutes + ":";
            } else {
                s += minutes + ":";
            }
        } else {
            s += "00" + ":";
        }
        if (seconds > 0) {
            if (seconds < 10) {
                s += "0" + seconds;
            } else {
                s += seconds;
            }
        } else {
            s += "00";
        }
        return s;
    }

    /**
     * 金额四舍五入保留2位小数
     * @param money money
     * @return money
     */
    public static String converMoney2Point(String money){
        if(TextUtils.isEmpty(money)){
            return "0" ;
        }

        BigDecimal moneyBid = new BigDecimal(money);
        return moneyBid.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static String convertNull(String temp){
        return TextUtils.isEmpty(temp) ? "" : temp ;
    }

    public static String convertImageUrl(String temp){
        if(TextUtils.isEmpty(temp)){
            return "" ;
        }

        if(!temp.startsWith("http")){
            return "http://" + temp ;
        }

        return temp ;
    }
    /**
     * 字符串不为空
     *
     * @param str
     * @return
     */
    public static boolean notEmpty(String str) {
        return (str != null) && (str.trim().length() > 0);
    }

    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean empty(String str) {
        return (str == null) || (str.trim().length() == 0);
    }
}
