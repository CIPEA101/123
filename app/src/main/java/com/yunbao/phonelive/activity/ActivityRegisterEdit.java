package com.yunbao.phonelive.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.RegisterEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 编辑资料
 */
public class ActivityRegisterEdit extends AbsActivity implements View.OnClickListener{
    private EditText mNickNameEv ;
    private ImageView mSexManIv ;
    private TextView mSexManTv ;
    private ImageView mSexWomenIv ;
    private TextView mSexWomenTv ;
    private TextView mSubmitTv ;

    private int mSexType = 0 ;

    private boolean mInputOk = false ;

    public static void launch(Context context){
        Intent toIt = new Intent(context, ActivityRegisterEdit.class) ;
        context.startActivity(toIt) ;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register_edit;
    }

    @Override
    protected void main() {
        super.main();

        mNickNameEv = findViewById(R.id.act_register_edit_nickname_ev) ;
        View refreshIv = findViewById(R.id.act_register_edit_nickname_iv) ;

        View sexManLay = findViewById(R.id.act_register_edit_sex_man_lay) ;
        mSexManIv = findViewById(R.id.act_register_edit_sex_man_iv) ;
        mSexManTv = findViewById(R.id.act_register_edit_sex_man_tv) ;
        View sexWomenLay = findViewById(R.id.act_register_edit_sex_women_lay) ;
        mSexWomenIv = findViewById(R.id.act_register_edit_sex_women_iv) ;
        mSexWomenTv = findViewById(R.id.act_register_edit_sex_women_tv) ;

        mSubmitTv = findViewById(R.id.act_register_edit_submit_tv) ;

        sexManLay.setOnClickListener(this);
        sexWomenLay.setOnClickListener(this);
        refreshIv.setOnClickListener(this);
        mSubmitTv.setOnClickListener(this);

        mNickNameEv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeInputType() ;
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getRandomNickName() ;
    }

    @Override
    public void onClick(View view) {
        int vId = view.getId() ;
        if(R.id.act_register_edit_nickname_iv == vId){
            if(ClickUtil.canClick()){
                getRandomNickName() ;
            }
        }else if(R.id.act_register_edit_sex_man_lay == vId){
            changeSexStyle(1) ;
        }else if(R.id.act_register_edit_sex_women_lay == vId){
            changeSexStyle(2) ;
        }else if(R.id.act_register_edit_submit_tv == vId){
            updateUserInfo();
        }

    }

    private void changeInputType(){
        boolean allOk = mNickNameEv.getText().length() > 0 && mSexType > 0 ;
        if(mInputOk == allOk){
            return;
        }

        mInputOk = allOk ;
        mSubmitTv.setBackground(getResources().getDrawable(mInputOk ? R.drawable.bg_login_btn_success : R.drawable.bg_login_btn_normal)) ;
        mSubmitTv.setTextColor(getResources().getColor(mInputOk ? R.color.white : R.color.textColor99)) ;
    }

    private void changeSexStyle(int sexType){
        if(mSexType == sexType){
            return;
        }

        mSexType = sexType ;
        boolean isMan = 1 == mSexType ;

        mSexManIv.setImageDrawable(getResources().getDrawable(isMan ? R.mipmap.login_sex1 : R.mipmap.login_sex2)) ;
        mSexManTv.setTextColor(getResources().getColor(isMan ? R.color.color_btn_red : R.color.textColor99)) ;
        mSexWomenIv.setImageDrawable(getResources().getDrawable(!isMan ? R.mipmap.login_sex3 : R.mipmap.login_sex4)) ;
        mSexWomenTv.setTextColor(getResources().getColor(!isMan ? R.color.color_btn_red : R.color.textColor99)) ;

        changeInputType() ;
    }


    private void getRandomNickName(){
        HttpUtil.cancel(HttpConsts.GET_USER_RANDOM_NAME) ;
        HttpUtil.getRandomNickName(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(code == 0){
                    if(info != null && info.length > 0){
                        String nickName = info[0] ;
                        mNickNameEv.setText(nickName) ;
                        mNickNameEv.setSelection(nickName.length()) ;
                    }
                }
            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(mContext) ;
            }
            @Override
            public boolean showLoadingDialog() {
                return true ;
            }
        });
    }

    private void updateUserInfo(){
        final String nickName = mNickNameEv.getText().toString().trim() ;
        if(TextUtils.isEmpty(nickName)){
            ToastUtil.show("请输入昵称");
            return;
        }
        if(mSexType == 0){
            ToastUtil.show("请选择性别");
            return;
        }
        String sex = mSexType == 1 ? "1" : "2" ;

        HttpUtil.updateFields("{\"user_nicename\":\"" + nickName + "\",\"sex\":\"" + sex +  "\"}", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("注册成功");
                    UserBean u = AppConfig.getInstance().getUserBean();
                    if (u != null) {
                        u.setUserNiceName(nickName);
                    }
                    EventBus.getDefault().post(new RegisterEvent(RegisterEvent.REG_TYPE_REG)) ;
                    finish() ;
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

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new RegisterEvent(RegisterEvent.REG_TYPE_REG)) ;
        super.onBackPressed();
    }
}
