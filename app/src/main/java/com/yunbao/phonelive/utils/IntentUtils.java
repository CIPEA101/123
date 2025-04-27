package com.yunbao.phonelive.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

public class IntentUtils {


    public static void intentToQQWithChat(Context context,String qq){
        final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq ;
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
    }

    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

//    public final static String WEIXIN_CHATTING_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.chatting.profile";//微信聊天

    public static boolean intentToWechatWithChat(Context context,String wxId){
//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.parse(wxId),WEIXIN_CHATTING_MIMETYPE);
//            context.startActivity(intent);
//            return true ;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false ;
//        }

        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
            return true ;
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            return false;
        }
    }


}
