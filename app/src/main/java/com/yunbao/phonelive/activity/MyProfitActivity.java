package com.yunbao.phonelive.activity;

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
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.HtmlConfig;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.IconUtil;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.SpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

/**
 * Created by cxf on 2018/10/20.
 */

public class MyProfitActivity extends AbsActivity implements View.OnClickListener {

    private TextView mAllName;//总映票数TextView
    private TextView mAll;//总映票数
    private TextView mCanName;//可提取映票数TextView
    private TextView mCan;//可提取映票数
    private TextView mGetName;//输入要提取的映票数
    private TextView mMoney;
    private TextView mTip;//温馨提示
    private EditText mEdit;
    private int mRate;
    private long mMaxCanMoney;//可提取映票数
    private View mChooseTip;
    private View mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccount;
    private String mAccountID;
    private String mVotesName;
    private View mBtnCash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_profit;
    }

    @Override
    protected void main() {
        mAllName = (TextView) findViewById(R.id.all_name);
        mAll = (TextView) findViewById(R.id.all);
        mCanName = (TextView) findViewById(R.id.can_name);
        mCan = (TextView) findViewById(R.id.can);
        mGetName = (TextView) findViewById(R.id.get_name);
        mTip = (TextView) findViewById(R.id.tip);
        mMoney = (TextView) findViewById(R.id.money);
        mEdit = findViewById(R.id.edit);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    long i = Long.parseLong(s.toString());
                    if (i > mMaxCanMoney) {
                        i = mMaxCanMoney;
                        s = String.valueOf(mMaxCanMoney);
                        mEdit.setText(s);
                        mEdit.setSelection(s.length());
                    }
                    if (mRate != 0) {
                        mMoney.setText("￥" + (i / mRate));
                    }
                    mBtnCash.setEnabled(true);
                } else {
                    mMoney.setText("￥");
                    mBtnCash.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mVotesName = AppConfig.getInstance().getVotesName();
        mAllName.setText("总" + mVotesName + "数");
        mCanName.setText("可提现" + mVotesName + "数");
        mGetName.setText("输入要提取的" + mVotesName + "数");
        mBtnCash = findViewById(R.id.btn_cash);
        mBtnCash.setOnClickListener(this);
        findViewById(R.id.btn_choose_account).setOnClickListener(this);
        findViewById(R.id.btn_cash_record).setOnClickListener(this);
        mChooseTip = findViewById(R.id.choose_tip);
        mAccountGroup = findViewById(R.id.account_group);
        mAccountIcon = findViewById(R.id.account_icon);
        mAccount = findViewById(R.id.account);
        loadData();
    }


    private void loadData() {
        HttpUtil.getProfit(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    try {
                        JSONObject obj = JSON.parseObject(info[0]);
                        mAll.setText(obj.getString("votestotal"));
                        mTip.setText(obj.getString("tips"));
                        String votes = obj.getString("votes");
                        mCan.setText(votes);
                        if (votes.contains(".")) {
                            votes = votes.substring(0, votes.indexOf('.'));
                        }
                        mMaxCanMoney = Long.parseLong(votes);
                        mRate = obj.getIntValue("cash_rate");
                    } catch (Exception e) {
                        L.e("提现接口错误------>" + e.getClass() + "------>" + e.getMessage());
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cash:
                cash();
                break;
            case R.id.btn_choose_account:
                chooseAccount();
                break;
            case R.id.btn_cash_record:
                cashRecord();
                break;
        }
    }

    /**
     * 提现记录
     */
    private void cashRecord() {
        String url = HtmlConfig.CASH_RECORD;
        WebViewPagerActivity.forward(mContext, url);
    }


    /**
     * 提现
     */
    private void cash() {
        String votes = mEdit.getText().toString().trim();
        if (TextUtils.isEmpty(votes)) {
            ToastUtil.show("输入要提现的" + mVotesName + "数");
            return;
        }
        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show(R.string.profit_choose_account);
            return;
        }
        HttpUtil.doCash(votes, mAccountID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
            }
        });
    }

    /**
     * 选择账户
     */
    private void chooseAccount() {
        Intent intent = new Intent(mContext, CashActivity.class);
        intent.putExtra(Constants.CASH_ACCOUNT_ID, mAccountID);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getAccount();
    }

    private void getAccount() {
        String[] values = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
        if (values != null && values.length == 3) {
            String accountId = values[0];
            String account = values[1];
            String type = values[2];
            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(type)) {
                if (mChooseTip.getVisibility() == View.VISIBLE) {
                    mChooseTip.setVisibility(View.INVISIBLE);
                }
                if (mAccountGroup.getVisibility() != View.VISIBLE) {
                    mAccountGroup.setVisibility(View.VISIBLE);
                }
                mAccountID = accountId;
                mAccountIcon.setImageResource(IconUtil.getCashTypeIcon(Integer.parseInt(type)));
                mAccount.setText(account);
            } else {
                if (mAccountGroup.getVisibility() == View.VISIBLE) {
                    mAccountGroup.setVisibility(View.INVISIBLE);
                }
                if (mChooseTip.getVisibility() != View.VISIBLE) {
                    mChooseTip.setVisibility(View.VISIBLE);
                }
                mAccountID = null;
            }
        } else {
            if (mAccountGroup.getVisibility() == View.VISIBLE) {
                mAccountGroup.setVisibility(View.INVISIBLE);
            }
            if (mChooseTip.getVisibility() != View.VISIBLE) {
                mChooseTip.setVisibility(View.VISIBLE);
            }
            mAccountID = null;
        }
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConsts.DO_CASH);
        HttpUtil.cancel(HttpConsts.GET_PROFIT);
        super.onDestroy();
    }
}
