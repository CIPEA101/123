package com.yunbao.phonelive.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserShareCode;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.BitmapUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ProcessResultUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.ViewUtils;
import com.yunbao.phonelive.utils.ZXingUtils;

/**
 * 推广赚钱
 */
public class ActivityExtension extends AbsActivity implements View.OnClickListener{

    private TextView mShareCodeTv ;

    private Dialog mHelpDialog ;
    private Dialog mFriendDialog ;

    private String mInvCode = "" ;


    public ProcessResultUtil mProcessResultUtil;

    private Dialog mCardShareDialog;//铭牌
    private View mCardLay ;
    private TextView tv_invite_count1;
    private TextView tv_invite_count2;
    private TextView tv_invite_money1;
    private TextView tv_invite_money2;

    private TextView tv_invite_des1;
    private TextView tv_invite_des2;


    public static void launch(Context context){
        Intent toIt = new Intent(context,ActivityExtension.class) ;
        context.startActivity(toIt) ;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_extension;
    }

    @Override
    protected void main() {
        super.main();

        mProcessResultUtil = new ProcessResultUtil(this) ;
        tv_invite_des1 = findViewById(R.id.tv_invite_des1) ;
        tv_invite_des2 = findViewById(R.id.tv_invite_des2) ;
        mShareCodeTv = findViewById(R.id.act_extension_code_tv) ;
        tv_invite_count1 = findViewById(R.id.tv_invite_count1) ;
        tv_invite_count2 = findViewById(R.id.tv_invite_count2) ;
        tv_invite_money1 = findViewById(R.id.tv_invite_money1) ;
        tv_invite_money2 = findViewById(R.id.tv_invite_money2) ;
        View helpIv = findViewById(R.id.act_extension_help_iv) ;
        View copyIv = findViewById(R.id.act_extension_code_copy_tv) ;
        View moreIv = findViewById(R.id.iv_extension_more) ;
        moreIv.setOnClickListener(this);
        copyIv.setOnClickListener(this);
        helpIv.setOnClickListener(this);

//        ImageView ruleIv = findViewById(R.id.act_extension_rule_iv) ;
//        int ruleWid = ScreenDimenUtil.getInstance().getScreenWdith() - DpUtil.dp2px(44) ;
//        ruleIv.getLayoutParams().width = ruleWid ;
//        ruleIv.getLayoutParams().height = (int) (ruleWid * 1.73F);

        getUserShareCode(false) ;
        getCount() ;
        getRebate() ;
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;

        if(R.id.act_extension_help_iv == vId){
            showHelpTipsDialog() ;
        } else if(R.id.act_extension_code_copy_tv == vId){
            try {
                //获取剪贴板管理器：
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("Label", mInvCode) ;
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);

                ToastUtil.show("邀请码已复制");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void showHelpTipsDialog(){
        if(null == mHelpDialog){
            mHelpDialog = DialogUitl.createHelpDialog(mContext,"活动规则",getResources().getString(R.string.extension_help_tips)) ;
        }

        mHelpDialog.show() ;
    }


    private void getUserShareCode(final boolean showDialog){
        HttpUtil.getUserShareCode(AppConfig.getInstance().getUid()
                ,new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            try {
                                JSONObject obj = JSON.parseObject(info[0]);
                                UserShareCode bean = JSON.toJavaObject(obj, UserShareCode.class);
                                if(bean != null){
                                    mInvCode = bean.getCode() ;
                                    mShareCodeTv.setText("邀请码：" + mInvCode) ;

                                    if(showDialog){
                                        showUserCardShareDialog() ;
                                    }
                                }else{
                                    ToastUtil.show("没有相关信息");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

                                ToastUtil.show("没有相关信息");
                            }
                        }else{
                            ToastUtil.show("没有相关信息");
                        }
                    }
                    @Override
                    public boolean showLoadingDialog() {
                        return true;
                    }
                    @Override
                    public Dialog createLoadingDialog() {
                        return DialogUitl.loadingDialog(mContext);
                    }
                });
    }


    /**
     * 铭牌
     */
    private void showUserCardShareDialog(){
        if(null == mCardShareDialog){
            mCardShareDialog = new Dialog(mContext,R.style.dialog) ;

            View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_user_card_share,null) ;
            mCardShareDialog.setContentView(contentView);

            int screenWidth = ScreenDimenUtil.getInstance().getScreenWdith() ;
            contentView.getLayoutParams().width = screenWidth;
            contentView.getLayoutParams().height = ScreenDimenUtil.getInstance().getScreenHeight();

            int contentWidth = (int) (screenWidth * 0.7F);

            mCardLay = contentView.findViewById(R.id.dialog_user_card_share_content_lay) ;
            View topIv = contentView.findViewById(R.id.dialog_user_card_share_top_iv) ;
            ImageView mCardShareIv = contentView.findViewById(R.id.dialog_user_card_share_code_iv) ;
            TextView mCardShareTv = contentView.findViewById(R.id.dialog_user_card_share_code_tv) ;

            mCardLay.getLayoutParams().width = contentWidth ;
            topIv.getLayoutParams().height = (int) (contentWidth * 0.3F);

            int mCardWidHei = (int) (screenWidth * 0.3F);
            mCardShareIv.getLayoutParams().width = mCardWidHei;
            mCardShareIv.getLayoutParams().height = mCardWidHei;

            mCardShareIv.setImageBitmap(ZXingUtils.createQRImage(mInvCode,mCardWidHei,mCardWidHei,null)) ;
            mCardShareTv.setText("邀请码 " + mInvCode) ;

            View closeIv = contentView.findViewById(R.id.dialog_user_card_share_close_iv) ;
            View okIv = contentView.findViewById(R.id.dialog_user_card_share_save_tv) ;
            okIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mContext instanceof MainActivity){
                       mProcessResultUtil.requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, new Runnable() {
                            @Override
                            public void run() {
                                saveUserShareCode() ;
                            }
                        });
                    }
                }
            });
            closeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCardShareDialog.dismiss();
                }
            });
        }

        mCardShareDialog.show() ;
    }


    /**
     * 保存海报
     */
    private void saveUserShareCode(){
        if(mCardLay != null){
            String path = BitmapUtil.getInstance().saveBitmap(ViewUtils.saveViewToBitmap(mCardLay)) ;
            if(!TextUtils.isEmpty(path)){
                ToastUtil.show("图片保存在" + path) ;
                // 最后通知图库更新
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ path)));
            }else{
                ToastUtil.show("保存失败") ;
            }
        }
    }

    /**
     * 赋值链接
     * @param view
     */
    public void gotoCopy(View view){
        if(StringUtil.empty(mInvCode))return;
        try {
            String str=getString(R.string.copy)+mInvCode;
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", str) ;
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);

            ToastUtil.show("链接已复制");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoSave(View view){
        if(TextUtils.isEmpty(mInvCode)){
            getUserShareCode(true) ;
        }else{
            showUserCardShareDialog();
        }
    }

    /**
     * 有效好友规则
     * @param view
     */
    public void gotoFriendRule(View view){
        if(null == mFriendDialog){
            mFriendDialog = DialogUitl.createHelpDialog(mContext,"有效好友规则",getResources().getString(R.string.friend_rule)) ;
        }

        mFriendDialog.show() ;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHelpDialog!=null){
            if(mHelpDialog.isShowing()){
                mHelpDialog.dismiss();
            }
            mHelpDialog=null;
        }
        if(mFriendDialog!=null){
            if(mFriendDialog.isShowing()){
                mFriendDialog.dismiss();
            }
            mFriendDialog=null;
        }
    }
    private void getCount(){
        HttpUtil.getMoney(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            try {
                                /**
                                 * "nums": "0",
                                 * 		"is_effective_user_num": 0,
                                 * 		"yesday_coins": null,
                                 * 		"coins": "70.00000"
                                 * 	： "nums": "已邀请好友数",
                                 *         "is_effective_user_num": 有效人数,
                                 *         "yesday_coins": 昨日收益,
                                 *         "coins": "总收益"
                                 */
                                JSONObject obj = JSON.parseObject(info[0]);
                                tv_invite_count1.setText(obj.getString("nums"));
                                tv_invite_count2.setText(obj.getString("is_effective_user_num"));
                                tv_invite_money1.setText(obj.getString("yesday_coins"));
                                tv_invite_money2.setText(obj.getString("coins"));
                            } catch (Exception e) {
                                e.printStackTrace();

                                ToastUtil.show("没有相关信息");
                            }
                        }else{
                            ToastUtil.show("没有相关信息");
                        }
                    }
                });
    }
    private void getRebate(){
        HttpUtil.getRebate(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        /**
                         * 	"code": 0,
                         * 		"msg": "",
                         * 		"info": ["5"]
                         */
                        String str1=String.format(getResources().getString(R.string.invite_money),info[0]);
                        String str2=String.format(getResources().getString(R.string.relus),info[0]);
                        tv_invite_des1.setText(str1);
                        tv_invite_des2.setText(str2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
