package com.yunbao.phonelive.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.event.RegisterEvent;
import com.yunbao.phonelive.utils.ClickUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 注册--邀请码
 */
public class ActivityRegisterInviteCode extends AbsActivity implements View.OnClickListener{
    private EditText mCodeEv;

    private TextView mSubmitTv ;

    private boolean mInputOk = false ;

    public static void launch(Context context) {
        Intent intent = new Intent(context, ActivityRegisterInviteCode.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_invite_code;
    }

    @Override
    protected void main() {
        super.main();

        EventBus.getDefault().register(this);

        View skipTv = findViewById(R.id.act_register_invite_code_skip_tv) ;
        mCodeEv = findViewById(R.id.act_register_invite_code_ev) ;
        mSubmitTv = findViewById(R.id.act_register_invite_code_submit_tv) ;

        mSubmitTv.setOnClickListener(this);
        skipTv.setOnClickListener(this);

        mCodeEv.addTextChangedListener(new TextWatcher() {
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
        if(R.id.act_register_invite_code_submit_tv == vId){//提交
            if(ClickUtil.canClick() && mInputOk){
                submitRegister() ;
            }
        }else if(R.id.act_register_invite_code_skip_tv == vId){//跳过
            if(ClickUtil.canClick()){
                ActivityRegisterNew.launch(mContext,"") ;
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
        String number = mCodeEv.getText().toString().trim() ;
        ActivityRegisterNew.launch(mContext,number) ;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterEvent(RegisterEvent ev){
        if(ev != null){
            if(RegisterEvent.REG_TYPE_REG == ev.getType()){
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
