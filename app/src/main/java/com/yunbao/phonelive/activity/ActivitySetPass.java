package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.event.RegisterEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 找回密码
 */
public class ActivitySetPass extends AbsActivity implements View.OnClickListener{
    private EditText mPassEv ;
    private ImageView mPassIv ;
    private TextView mSubmitTv ;

    private boolean mPassShow = false ;
    private boolean mInputOk = false ;

    private String mPhone ;
    private String mCode ;

    public static void launch(Context context,String phone,String code) {
        Intent intent = new Intent(context, ActivitySetPass.class);
        intent.putExtra("loginPhone",phone) ;
        intent.putExtra("loginCode",code) ;
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_pass;
    }

    @Override
    protected void main() {
        super.main();

        mPhone = StringUtil.convertNull(getIntent().getStringExtra("loginPhone")) ;
        mCode = StringUtil.convertNull(getIntent().getStringExtra("loginCode")) ;

        mPassEv = findViewById(R.id.act_set_pass_ev) ;
        mPassIv = findViewById(R.id.act_set_pass_iv) ;
        mSubmitTv = findViewById(R.id.act_set_pass_submit_tv) ;

        mPassIv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);

        mPassEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean inputOk = charSequence.length() > 0 ;
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
        if(R.id.act_set_pass_iv == vId){//切换密码显示和隐藏
            changePassStyle() ;
        }else if(R.id.act_set_pass_submit_tv == vId){//提交
            if(ClickUtil.canClick()){
                submitChange() ;
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

    private void submitChange(){
        String pass = mPassEv.getText().toString().trim() ;
        if(TextUtils.isEmpty(pass)){
            ToastUtil.show("请输入密码");
            mPassEv.setText("");
            mPassEv.requestFocus() ;
            return;
        }

        toChangePass(pass) ;
    }

    private void toChangePass(String pass){
        HttpUtil.findPwd(mPhone, pass, pass, mCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(0 == code){
                    DialogUitl.showSimpleDialog(mContext, "设置密码","设置成功，请使用新密码登录",false,false
                            , new DialogUitl.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            EventBus.getDefault().post(new RegisterEvent(RegisterEvent.REG_TYPE_SETPASS)) ;

                            finish() ;
                        }
                    });
                }else{
                    if(TextUtils.isEmpty(msg)){
                        msg = "设置失败，请重试" ;
                    }

                    DialogUitl.showSimpleDialog(mContext, "设置密码",msg,true,false
                            , new DialogUitl.SimpleCallback() {
                                @Override
                                public void onConfirmClick(Dialog dialog, String content) {
                                }
                            });

                }
            }
            @Override
            public boolean showLoadingDialog() {
                return true;
            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext) ;
            }
        });
    }

}
