package com.yunbao.phonelive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.VideoPlayActivity;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.bean.VideoBean;
import com.yunbao.phonelive.bean.VideoCommentBean;
import com.yunbao.phonelive.event.VideoCommentEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.TextRender;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/12/3.
 * 视频评论输入框
 */

public class VideoInputDialogFragment extends AbsDialogFragment implements View.OnClickListener, ChatFaceDialog.ActionListener {

    private InputMethodManager imm;
    private EditText mInput;
    private boolean mOpenFace;
    private int mOriginHeight;
    private int mFaceHeight;
    private CheckBox mCheckBox;
    private ChatFaceDialog mChatFaceDialog;
    private Handler mHandler;
    private VideoBean mVideoBean;
    private VideoCommentBean mVideoCommentBean;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_video_input;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        mOriginHeight = DpUtil.dp2px(48);
        params.height = mOriginHeight;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mHandler = new Handler();
        mInput = (EditText) mRootView.findViewById(R.id.input);
        mInput.setOnClickListener(this);
        mCheckBox = mRootView.findViewById(R.id.btn_face);
        mCheckBox.setOnClickListener(this);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendComment();
                    return true;
                }
                return false;
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            mOpenFace = bundle.getBoolean(Constants.VIDEO_FACE_OPEN, false);
            mFaceHeight = bundle.getInt(Constants.VIDEO_FACE_HEIGHT, 0);
            mVideoCommentBean = bundle.getParcelable(Constants.VIDEO_COMMENT_BEAN);
            if (mVideoCommentBean != null) {
                UserBean replyUserBean = mVideoCommentBean.getUserBean();//要回复的人
                if (replyUserBean != null) {
                    mInput.setHint(WordUtil.getString(R.string.video_comment_reply) + replyUserBean.getUserNiceName());
                }
            }
        }
        if (mOpenFace) {
            if (mCheckBox != null) {
                mCheckBox.setChecked(true);
            }
            if (mFaceHeight > 0) {
                changeHeight(mFaceHeight);
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFace();
                        }
                    }, 200);
                }
            }
        } else {
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSoftInput();
                    }
                }, 200);
            }
        }
    }


    public void setVideoBean(VideoBean videoBean) {
        mVideoBean = videoBean;
    }

    private void showSoftInput() {
        //软键盘弹出
        if (imm != null) {
            imm.showSoftInput(mInput, InputMethodManager.SHOW_FORCED);
        }
        if (mInput != null) {
            mInput.requestFocus();
        }
    }

    private void hideSoftInput() {
        if (imm != null) {
            imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        }
    }


    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpConsts.SET_COMMENT);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
        mChatFaceDialog = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_face:
                clickFace();
                break;
            case R.id.input:
                clickInput();
                break;
        }
    }

    private void clickInput() {
        hideFace();
        if (mCheckBox != null) {
            mCheckBox.setChecked(false);
        }
    }

    private void clickFace() {
        if (mCheckBox.isChecked()) {
            hideSoftInput();
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFace();
                    }
                }, 200);
            }
        } else {
            hideFace();
            showSoftInput();
        }
    }

    private void showFace() {
        if (mFaceHeight > 0) {
            changeHeight(mFaceHeight);
            View faceView = ((VideoPlayActivity) mContext).getFaceView();
            if (faceView != null) {
                mChatFaceDialog = new ChatFaceDialog(mRootView, faceView, false, VideoInputDialogFragment.this);
                mChatFaceDialog.show();
            }
        }
    }

    private void hideFace() {
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
    }

    /**
     * 改变高度
     */
    private void changeHeight(int deltaHeight) {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams params = window.getAttributes();
        params.height = mOriginHeight + deltaHeight;
        window.setAttributes(params);
    }

    @Override
    public void onFaceDialogDismiss() {
        changeHeight(0);
        mChatFaceDialog = null;
    }

    /**
     * 发表评论
     */
    public void sendComment() {
        if (mVideoBean == null || mInput == null || !canClick()) {
            return;
        }
        String content = mInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        String toUid = mVideoBean.getUid();
        String commentId = "0";
        String parentId = "0";
        if (mVideoCommentBean != null) {
            toUid = mVideoCommentBean.getUid();
            commentId = mVideoCommentBean.getCommentId();
            parentId = mVideoCommentBean.getId();
        }
        HttpUtil.setComment(toUid, mVideoBean.getId(), content, commentId, parentId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    if (mInput != null) {
                        mInput.setText("");
                    }
                    JSONObject obj = JSON.parseObject(info[0]);
                    String commentNum = obj.getString("comments");
                    if (mVideoBean != null) {
                        EventBus.getDefault().post(new VideoCommentEvent(mVideoBean.getId(), commentNum));
                    }
                    ToastUtil.show(msg);
                    dismiss();
                    ((VideoPlayActivity) mContext).hideCommentWindow();
                }
            }
        });
    }

    /**
     * 点击表情上面的删除按钮
     */
    public void onFaceDeleteClick() {
        if (mInput != null) {
            int selection = mInput.getSelectionStart();
            String text = mInput.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1, selection);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[", selection);
                    if (start >= 0) {
                        mInput.getText().delete(start, selection);
                    } else {
                        mInput.getText().delete(selection - 1, selection);
                    }
                } else {
                    mInput.getText().delete(selection - 1, selection);
                }
            }
        }
    }

    /**
     * 点击表情
     */
    public void onFaceClick(String str, int faceImageRes) {
        if (mInput != null) {
            Editable editable = mInput.getText();
            editable.insert(mInput.getSelectionStart(), TextRender.getFaceImageSpan(str, faceImageRes));
        }
    }

}
