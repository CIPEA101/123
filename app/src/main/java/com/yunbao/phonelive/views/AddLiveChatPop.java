package com.yunbao.phonelive.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveActivity;
import com.yunbao.phonelive.custom.MyRadioButton;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.WordUtil;

/**
 * 直播间聊天
 */
public class AddLiveChatPop extends PopupWindow  implements View.OnClickListener{
    private Context mContext ;

    private InputMethodManager imm;
    private EditText mInput;
    private CheckBox mCheckBox;
    private MyRadioButton mMyRadioButton;
    private String mHint1;
    private String mHint2;

    public AddLiveChatPop(Context context, String danmuPrice) {
        super(context);

        mContext = context ;

        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_live_chat_input,null) ;
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(DpUtil.dp2px(50)) ;

        setBackgroundDrawable(new BitmapDrawable()) ;
        setOutsideTouchable(true);
        setFocusable(true);
        setAnimationStyle(R.style.bottomToTopAnim);

        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        mInput =  contentView.findViewById(R.id.input);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mMyRadioButton.doChecked(false);
                } else {
                    mMyRadioButton.doChecked(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                //软键盘弹出
                imm.showSoftInput(mInput, InputMethodManager.SHOW_IMPLICIT);
                mInput.requestFocus();
            }
        }, 200);

        mCheckBox = contentView.findViewById(R.id.danmu);
        mMyRadioButton = contentView.findViewById(R.id.btn_send);
        mMyRadioButton.setOnClickListener(this);

        mHint1 = WordUtil.getString(R.string.live_open_alba) + danmuPrice + "/" + WordUtil.getString(R.string.live_tiao);
        mHint2 = WordUtil.getString(R.string.live_say_something);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    mInput.setHint(mHint1);
                } else {
                    mInput.setHint(mHint2);
                }
            }
        });

//        setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mInput.clearFocus() ;
//                mInput.setFocusable(false);
//                if(imm!=null){
//                    imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
//                }
//            }
//        });
    }

    public void showChatPop(){

        showAtLocation(((Activity)mContext).getWindow().getDecorView(), Gravity.BOTTOM,0,0) ;

    }

    @Override
    public void onClick(View view) {
        sendMessage();
    }

    private void sendMessage() {
        String content = mInput.getText().toString().trim();
        if (!TextUtils.isEmpty(content)) {
            if (mCheckBox.isChecked()) {
                ((LiveActivity) mContext).sendDanmuMessage(content);
            } else {
                ((LiveActivity) mContext).sendChatMessage(content);
            }
            mInput.setText("");

            if(imm!=null){
                imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void dismiss() {

        if(imm!=null){
            imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        }

        super.dismiss();
    }
}
