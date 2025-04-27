package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.event.RegisterEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.ValidatePhoneUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 注册
 */
public class ActivityRegisterNew extends AbsActivity implements View.OnClickListener{
    private EditText mNumberEv ;

    private TextView mSubmitTv ;

    private boolean mInputOk = false ;

    private String mInviteCode ;
    private int mRegisterType = ActivityLoginCode.CODE_TYPE_REGISTER ;

    public static void launch(Context context,String inviteCode) {
        Intent intent = new Intent(context, ActivityRegisterNew.class);
        intent.putExtra("inviteCode",inviteCode) ;
        intent.putExtra("registerType",ActivityLoginCode.CODE_TYPE_REGISTER) ;
        context.startActivity(intent);
    }

    public static void launch(Context context,int type){
        Intent intent = new Intent(context, ActivityRegisterNew.class);
        intent.putExtra("registerType",type) ;
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_new;
    }

    @Override
    protected void main() {
        super.main();

        EventBus.getDefault().register(this);

        mRegisterType = getIntent().getIntExtra("registerType",mRegisterType) ;
        mInviteCode = StringUtil.convertNull(getIntent().getStringExtra("inviteCode")) ;

        TextView mTitleTv = findViewById(R.id.act_register_title_tv) ;
        mTitleTv.setText(ActivityLoginCode.CODE_TYPE_SET_PASS == mRegisterType ? "找回密码" : "手机号注册");

        mNumberEv = findViewById(R.id.act_register_num_ev) ;
        mSubmitTv = findViewById(R.id.act_register_submit_tv) ;

        mSubmitTv.setOnClickListener(this);

        mNumberEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean inputOk = charSequence.length() == 11 ;
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
        if(R.id.act_register_submit_tv == vId){//提交
            if(mInputOk && ClickUtil.canClick()){
                submitRegister() ;
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

    private void submitRegister(){
        String number = mNumberEv.getText().toString().trim() ;
        if(!ValidatePhoneUtil.validateMobileNumber(number)){
            ToastUtil.show("请输入正确的手机号");
            mNumberEv.requestFocus() ;
            mNumberEv.setSelection(number.length());
            return;
        }

        getYzmCode() ;
    }


    private void getYzmCode(){
        final String number = mNumberEv.getText().toString().trim() ;
        HttpUtil.getSmsCode(number,ActivityLoginCode.CODE_TYPE_REGISTER == mRegisterType ? "1" : "0", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (!TextUtils.isEmpty(msg) && msg.contains("123456")) {
                        ToastUtil.show(msg);
                    }

                    //跳转到验证码界面
                    if(ActivityLoginCode.CODE_TYPE_REGISTER == mRegisterType){
                        ActivityLoginCode.launch(mContext,number,mInviteCode) ;
                    }else{
                        ActivityLoginCode.launch(mContext,number,mRegisterType) ;
                    }
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
                    ||RegisterEvent.REG_TYPE_REG == ev.getType()){
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

    }

}
