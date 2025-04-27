package com.yunbao.phonelive.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cxf on 2018/7/19.
 */

public class DateFormatUtil {

    private static SimpleDateFormat sFormat;
    private static SimpleDateFormat sFormat2;
    private static SimpleDateFormat sFormat3;
    private static SimpleDateFormat sFormat4;

    static {
        sFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sFormat2 = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        sFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sFormat4 = new SimpleDateFormat("yyyyMMdd");
    }


    public static String getCurTimeString() {
        return sFormat.format(new Date());
    }

    public static String getVideoCurTimeString() {
        return sFormat2.format(new Date());
    }
    public static String dateTimeToString(String dateTime){
        try {
            Date date = new Date();
            date.setTime(Long.valueOf(dateTime) * 1000);
            return sFormat3.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateTime ;
    }
    public static String dateTimeToString(long dateTime){
        Date date = new Date();
        date.setTime(dateTime*1000);
        return sFormat3.format(date);
    }
    public static String dateTimeToYmd(long dateTime){
        Date date = new Date();
        date.setTime(dateTime*1000);
        return sFormat4.format(date);
    }
}
