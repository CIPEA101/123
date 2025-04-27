package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.AppContext;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.RegisterEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ScreenDimenUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.ValidatePhoneUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 登录
 */
public class ActivityLoginNew extends AbsActivity implements View.OnClickListener{
    private TextView mTitleTv ;
    private EditText mNumberEv ;

    private View mPassLay ;
    private EditText mPassEv ;
    private ImageView mPassIv ;
    private View mPassDv ;

    private TextView mChangeTv ;
    private TextView mSubmitTv ;

    private boolean mPassShow = false ;
    private boolean mLoginStylePass = false ;
    private boolean mInputOk = false ;

    private Dialog mLoginDialog ;
    private TextView mDialogTitleTv ;

    public static void forward() {
        Intent intent = new Intent(AppContext.sInstance, ActivityLoginNew.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        AppContext.sInstance.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_num;
    }

    @Override
    protected void main() {
        super.main();

        EventBus.getDefault().register(this);

        mTitleTv = findViewById(R.id.act_login_title_tv) ;
        View registerTv = findViewById(R.id.act_login_register_tv) ;
        mNumberEv = findViewById(R.id.act_login_num_ev) ;
        mPassLay = findViewById(R.id.act_login_pass_lay) ;
        mPassEv = findViewById(R.id.act_login_pass_ev) ;
        mPassIv = findViewById(R.id.act_login_pass_iv) ;
        mPassDv = findViewById(R.id.act_login_pass_dv) ;
        mChangeTv = findViewById(R.id.act_login_change_tv) ;
        mSubmitTv = findViewById(R.id.act_login_submit_tv) ;

        View helpTv = findViewById(R.id.act_login_help_tv) ;

        registerTv.setOnClickListener(this);
        mPassIv.setOnClickListener(this);
        mChangeTv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);
        helpTv.setOnClickListener(this);

        mNumberEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean inputOk = charSequence.length() > 10 ;
                if(mLoginStylePass && mPassEv.getText().length() <= 0){
                    inputOk = false ;
                }

                changeSubmitStyle(inputOk) ;
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPassEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean inputOk = charSequence.length() > 0 ;
                if(mLoginStylePass && mNumberEv.getText().length() <= 10){
                    inputOk = false ;
                }

                changeSubmitStyle(inputOk) ;
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;
        if(R.id.act_login_pass_iv == vId){//切换密码显示和隐藏
            changePassStyle() ;
        }else if(R.id.act_login_change_tv == vId){//切换登录类型
            changeLoginStyle() ;
        }else if(R.id.act_login_submit_tv == vId){//提交
            if(ClickUtil.canClick()){
                submitLogin() ;
            }
        }else if(R.id.act_login_help_tv == vId){//登录遇到困难
            if(ClickUtil.canClick()){
                WebViewPagerActivity.start(mContext,AppConfig.CUSTOMER_WEB_HERF,true);
//                WebViewActivity.launch(mContext,AppConfig.CUSTOMER_WEB_HERF) ;
            }
        }else if(R.id.act_login_register_tv == vId){//注册
            if(ClickUtil.canClick()){
                ActivityRegisterInviteCode.launch(mContext) ;
//                ActivityRegisterEdit.launch(mContext);
            }
        }
    }

    private void changeSubmitStyle(boolean inputOk){
        if(mInputOk == inputOk){
            return;
        }

        mInputOk = inputOk ;

        mSubmitTv.setBackground(getResources().getDrawable(mInputOk ? R.drawable.bg_login_btn_success : R.drawable.bg_login_btn_normal)) ;
        mSubmitTv.setTextColor(getResources().getColor(mInputOk ? R.color.white : R.color.textColor99)) ;
    }

    private void changePassStyle(){
        mPassShow = !mPassShow ;

        mPassEv.setTransformationMethod(mPassShow ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        mPassIv.setImageDrawable(getResources().getDrawable(mPassShow ? R.mipmap.login_pass_show : R.mipmap.login_pass_hidden)) ;
        mPassEv.setSelection(mPassEv.getText().length()) ;
    }

    private void changeLoginStyle(){
        mLoginStylePass = !mLoginStylePass ;

        mPassLay.setVisibility(mLoginStylePass ? View.VISIBLE : View.GONE);
        mPassDv.setVisibility(mLoginStylePass ? View.VISIBLE : View.GONE);
        mTitleTv.setText(mLoginStylePass ? "账号登录" : "手机号登录") ;
        mSubmitTv.setText(mLoginStylePass ? "登录" : "获取验证码") ;
        mChangeTv.setText(mLoginStylePass ? "验证码登录" : "密码登录");

        boolean inputOk = mNumberEv.getText().length() > 0 ;
        if(mLoginStylePass && mPassEv.getText().length() <= 0){
            inputOk = false ;
        }
        changeSubmitStyle(inputOk) ;
    }

    private void submitLogin(){
        String number = mNumberEv.getText().toString().trim() ;
        if(!ValidatePhoneUtil.validateMobileNumber(number)){
            ToastUtil.show("请输入正确的手机号");
            mNumberEv.requestFocus() ;
            mNumberEv.setSelection(number.length());
            return;
        }

        if(mLoginStylePass){
            String pass = mPassEv.getText().toString().trim() ;
            if(TextUtils.isEmpty(pass)){
                ToastUtil.show("请输入密码");
                mPassEv.setText("");
                mPassEv.requestFocus() ;
                return;
            }

            toLogin() ;
        }else{
            getYzmCode() ;
        }
    }


    private void getYzmCode(){
        final String number = mNumberEv.getText().toString().trim() ;
        HttpUtil.getNormalSmsCode(number, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                        ToastUtil.show(msg);
                    }

                    //跳转到验证码界面
                    ActivityLoginCode.launch(mContext,number,ActivityLoginCode.CODE_TYPE_LOGIN) ;
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext) ;
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }
        });
    }

    private void toLogin(){
        String number = mNumberEv.getText().toString().trim() ;
        String pass = mPassEv.getText().toString().trim() ;
        HttpUtil.login(number, pass, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext) ;
            }
            @Override
            public boolean showLoadingDialog() {
                return true;
            }
        });
    }

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0 && info.length > 0) {
            JSONObject obj = JSON.parseObject(info[0]);
            String uid = obj.getString("id");
            String token = obj.getString("token");
            AppConfig.getInstance().setLoginInfo(uid, token, true);

            getBaseUserInfo();
        } else {
            if(TextUtils.isEmpty(msg)){
                msg = "登录失败，请重试" ;
            }
            showFindPassDialog(msg) ;
        }
    }

    private void showFindPassDialog(String msg){
        if(null == mLoginDialog){
            mLoginDialog = new Dialog(mContext,R.style.dialog) ;
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_login_result,null) ;
            mLoginDialog.setContentView(view);

            view.getLayoutParams().width = (int) (ScreenDimenUtil.getInstance().getScreenWdith() * 0.7F);

            mDialogTitleTv = view.findViewById(R.id.dialog_login_result_title_tv) ;
            View cancelTv = view.findViewById(R.id.dialog_login_result_cancel_tv) ;
            View findTv = view.findViewById(R.id.dialog_login_result_find_tv) ;
            cancelTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLoginDialog.dismiss();
                }
            });
            findTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLoginDialog.dismiss();

                    ActivityRegisterNew.launch(mContext,ActivityLoginCode.CODE_TYPE_SET_PASS) ;
                }
            });
        }

        mDialogTitleTv.setText(msg) ;
        mLoginDialog.show() ;
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                MainActivity.forward(mContext, false);
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterEvent(RegisterEvent ev){
        if(ev != null){
            if(RegisterEvent.REG_TYPE_REG == ev.getType()){
                MainActivity.forward(mContext, false);
                finish();
            }else if(RegisterEvent.REG_TYPE_LOGIN == ev.getType()){
                finish() ;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

    }
}
