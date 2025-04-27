package com.yunbao.phonelive.activity;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.speech.utils.LogUtil;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.RegisterEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.CommonCallback;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SpUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 验证码
 */
public class ActivityLoginCode extends AbsActivity implements View.OnClickListener{
    private TextView mPhoneTipsTv ;
    private TextView mCodeOneTv ;
    private View mCodeOneDv ;
    private TextView mCodeTwoTv ;
    private View mCodeTwoDv ;
    private TextView mCodeThreeTv ;
    private View mCodeThreeDv ;
    private TextView mCodeFourTv ;
    private View mCodeFourDv ;
    private TextView mCodeFiveTv ;
    private View mCodeFiveDv ;
    private TextView mCodeSixTv ;
    private View mCodeSixDv ;

    private EditText mCodeEv ;

    private TextView mSubmitTv ;

    public static final int CODE_TYPE_LOGIN = 0 ;
    public static final int CODE_TYPE_REGISTER = 1 ;
    public static final int CODE_TYPE_SET_PASS = 2 ;
    private int mCodeType = CODE_TYPE_LOGIN ;
    private String mPhone;
    private String mInviteCode ;

    private ValueAnimator mTimerAnim ;
    private static final int WAIT_TIME = 60 ;
    private int mLastIndex = 0 ;

    private Dialog mLoadingDialog ;

    public static void launch(Context context,String phone,int type){
        Intent toIt = new Intent(context,ActivityLoginCode.class) ;
        toIt.putExtra("smsPhone",phone) ;
        toIt.putExtra("smsType",type) ;
        context.startActivity(toIt);
    }

    public static void launch(Context context,String phone,String inviteCode){
        Intent toIt = new Intent(context,ActivityLoginCode.class) ;
        toIt.putExtra("inviteCode",inviteCode) ;
        toIt.putExtra("smsPhone",phone) ;
        toIt.putExtra("smsType",CODE_TYPE_REGISTER) ;
        context.startActivity(toIt);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_code ;
    }

    @Override
    protected void main() {
        super.main();

        EventBus.getDefault().register(this);

        mLoadingDialog = DialogUitl.loadingDialog(mContext) ;

        mInviteCode = StringUtil.convertNull(getIntent().getStringExtra("inviteCode")) ;
        mPhone = StringUtil.convertNull(getIntent().getStringExtra("smsPhone")) ;
        mCodeType = getIntent().getIntExtra("smsType",mCodeType) ;

        mPhoneTipsTv = findViewById(R.id.act_login_code_mobile_tv) ;
        mCodeOneTv = findViewById(R.id.act_login_code_one_tv) ;
        mCodeOneDv = findViewById(R.id.act_login_code_one_dv) ;
        mCodeTwoTv = findViewById(R.id.act_login_code_two_tv) ;
        mCodeTwoDv = findViewById(R.id.act_login_code_two_dv) ;
        mCodeThreeTv = findViewById(R.id.act_login_code_three_tv) ;
        mCodeThreeDv = findViewById(R.id.act_login_code_three_dv) ;
        mCodeFourTv = findViewById(R.id.act_login_code_four_tv) ;
        mCodeFourDv = findViewById(R.id.act_login_code_four_dv) ;
        mCodeFiveTv = findViewById(R.id.act_login_code_five_tv) ;
        mCodeFiveDv = findViewById(R.id.act_login_code_five_dv) ;
        mCodeSixTv = findViewById(R.id.act_login_code_six_tv) ;
        mCodeSixDv = findViewById(R.id.act_login_code_six_dv) ;

        mCodeEv = findViewById(R.id.act_login_code_code_ev) ;
        mCodeEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 6){
                    mCodeEv.setText(charSequence.subSequence(0,6));
                }else{
                    updateCodeStyle() ;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSubmitTv = findViewById(R.id.act_login_code_submit_tv) ;
        mSubmitTv.setOnClickListener(this);

        mPhoneTipsTv.setText("已发到：" + mPhone) ;

        startTimer() ;
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;
        if(R.id.act_login_code_submit_tv == vId){//重新获取
            if(ClickUtil.canClick()){
                if(mLastIndex == 0){
                    getYzmCode() ;
                }
            }
        }
    }

    private void startTimer(){
        if(mTimerAnim != null){
            if(mTimerAnim.isStarted()){
                mTimerAnim.cancel();
            }
            mTimerAnim = null ;
        }

        mTimerAnim = ValueAnimator.ofInt(WAIT_TIME,0) ;
        mTimerAnim.setDuration(WAIT_TIME*1000) ;
        mTimerAnim.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                return v;
            }
        });
        mTimerAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if(mLastIndex != value){
                    mLastIndex = value ;

                    updateSubmitStyle() ;
                }
            }
        });
        mTimerAnim.start() ;
    }

    private void updateSubmitStyle(){
        if(mLastIndex == 0){
            mSubmitTv.setBackground(getResources().getDrawable(R.drawable.bg_login_btn_success));
            mSubmitTv.setTextColor(getResources().getColor(R.color.white));
            mSubmitTv.setText("重新获取");
        }else{
            mSubmitTv.setBackground(getResources().getDrawable(R.drawable.bg_login_btn_normal));
            mSubmitTv.setTextColor(getResources().getColor(R.color.textColor99));

            if(mLastIndex < 10){
                mSubmitTv.setText("重新获取（0" + mLastIndex + "）");
            }else{
                mSubmitTv.setText("重新获取（" + mLastIndex + "）");
            }
        }
    }

    private void updateCodeStyle(){
        String code = mCodeEv.getText().toString() ;
        int codeLength = code.length() ;
        if(codeLength > 6){
            codeLength = 6 ;
            code = code.substring(0,6) ;
        }

        mCodeOneTv.setText("") ;
        mCodeOneDv.setBackground(getResources().getDrawable(R.color.color_btn_red)) ;
        mCodeTwoTv.setText("") ;
        mCodeTwoDv.setBackground(getResources().getDrawable(R.color.color_normal_dv)) ;
        mCodeThreeTv.setText("") ;
        mCodeThreeDv.setBackground(getResources().getDrawable(R.color.color_normal_dv)) ;
        mCodeFourTv.setText("") ;
        mCodeFourDv.setBackground(getResources().getDrawable(R.color.color_normal_dv)) ;
        mCodeFiveTv.setText("") ;
        mCodeFiveDv.setBackground(getResources().getDrawable(R.color.color_normal_dv)) ;
        mCodeSixTv.setText("") ;
        mCodeSixDv.setBackground(getResources().getDrawable(R.color.color_normal_dv)) ;

        if(codeLength > 0){
            mCodeOneTv.setText(code.substring(0,1)) ;
            mCodeOneDv.setBackground(getResources().getDrawable(R.color.color_btn_red)) ;
            mCodeTwoDv.setBackground(getResources().getDrawable(R.color.color_btn_red)) ;
        }
        if(codeLength > 1){
            mCodeTwoTv.setText(code.substring(1,2)) ;
            mCodeThreeDv.setBackground(getResources().getDrawable(R.color.color_btn_red)) ;
        }
        if(codeLength > 2){
            mCodeThreeTv.setText(code.substring(2,3)) ;
            mCodeFourDv.setBackground(getResources().getDrawable(R.color.color_btn_red)) ;
        }
        if(codeLength > 3){
            mCodeFourTv.setText(code.substring(3,4)) ;
            mCodeFiveDv.setBackground(getResources().getDrawable(R.color.color_btn_red)) ;
        }
        if(codeLength > 4){
            mCodeFiveTv.setText(code.substring(4,5)) ;
            mCodeSixDv.setBackground(getResources().getDrawable(R.color.color_btn_red)) ;
        }
        if(codeLength > 5){
            mCodeSixTv.setText(code.substring(5,6)) ;

            if(CODE_TYPE_LOGIN == mCodeType){
                //验证码登录
                loginByCode() ;
            }else {
                checkCode() ;
            }
        }
    }

    /**
     * 注册
     */
    private void registerByCode(){
        String code = mCodeEv.getText().toString() ;
        HttpUtil.registerByCode(mPhone, code,mInviteCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                mLoadingDialog.dismiss();

                if(0 == code){
                    LogUtil.e("info=" + info[0]) ;

                    JSONObject obj = JSON.parseObject(info[0]);
                    String uid = obj.getString("uid");
                    String token = obj.getString("token");
                    AppConfig.getInstance().setLoginInfo(uid, token, true);

                    UserBean bean = JSON.toJavaObject(obj, UserBean.class);
                    AppConfig.getInstance().setUserBean(bean);
                    SpUtil.getInstance().setStringValue(SpUtil.USER_INFO, info[0]);

                    ActivityRegisterEdit.launch(mContext) ;
                }else{
                    if(TextUtils.isEmpty(msg)){
                        msg = "注册失败，请重试" ;
                    }
                    ToastUtil.show(msg) ;
                }
            }

            @Override
            public void onError() {
                super.onError();

                mLoadingDialog.dismiss();
            }
        });
    }

    /**
     * 登录
     */
    private void loginByCode(){
        String code = mCodeEv.getText().toString() ;
        HttpUtil.loginBySmsCode(mPhone, code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext);
            }
            @Override
            public boolean showLoadingDialog() {
                return true ;
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

            EventBus.getDefault().post(new RegisterEvent(RegisterEvent.REG_TYPE_LOGIN)) ;

            getBaseUserInfo();
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                mLoadingDialog.dismiss();

                MainActivity.forward(mContext, false);
                finish();
            }
        });
    }

    /**
     * 验证 验证码
     */
    private void checkCode(){
        mLoadingDialog.show() ;

        String code = mCodeEv.getText().toString() ;
        HttpUtil.checkSmsCode(mPhone, code, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(0 == code){
                    finishCodeInput() ;
                }else{
                    mLoadingDialog.dismiss();

                    if(TextUtils.isEmpty(msg)){
                        msg = "验证码有误" ;
                    }
                    ToastUtil.show(msg) ;
                }
            }

            @Override
            public void onError() {
                super.onError();

                mLoadingDialog.dismiss();
            }
        });
    }

    private void finishCodeInput(){
        String code = mCodeEv.getText().toString() ;

        if(CODE_TYPE_REGISTER == mCodeType){
            registerByCode() ;
        }else if(CODE_TYPE_SET_PASS == mCodeType){
            mLoadingDialog.dismiss();
            ActivitySetPass.launch(mContext,mPhone,code) ;
        }
    }


    private void getYzmCode(){
        HttpUtil.getSmsCode(mPhone,CODE_TYPE_REGISTER == mCodeType ? "1" : "0", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                        ToastUtil.show(msg);
                    }

                    startTimer();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterEvent(RegisterEvent ev){
        if(ev != null){
            if(RegisterEvent.REG_TYPE_SETPASS == ev.getType()
                    || RegisterEvent.REG_TYPE_REG == ev.getType()){
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        if(mTimerAnim != null){
            if(mTimerAnim.isStarted()){
                mTimerAnim.cancel();
            }
            mTimerAnim = null ;
        }
    }
}
