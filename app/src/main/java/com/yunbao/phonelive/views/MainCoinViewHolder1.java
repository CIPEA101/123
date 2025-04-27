package com.yunbao.phonelive.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MyCoinActivity;
import com.yunbao.phonelive.adapter.CoinAdapter;
import com.yunbao.phonelive.bean.CoinBean;
import com.yunbao.phonelive.bean.UserBean;
import com.yunbao.phonelive.event.CoinChangeEvent;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.pay.PayCallback;
import com.yunbao.phonelive.pay.ali.AliPayBuilder;
import com.yunbao.phonelive.pay.wx.WxPayBuilder;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;
import com.yunbao.phonelive.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.List;

public class MainCoinViewHolder1 extends AbsMainViewHolder implements OnItemClickListener<CoinBean> {

    public MainCoinViewHolder1(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    private View mTop;
    private TextView mBalance;
    private String mBalanceValue;
    private RecyclerView mRecyclerView;
    private CoinAdapter mAdapter;
    private String mCoinName;
    private CoinBean mCheckedCoinBean;
    private String mAliPartner;// 支付宝商户ID
    private String mAliSellerId; // 支付宝商户收款账号
    private String mAliPrivateKey; // 支付宝商户私钥，pkcs8格式
    private String mWxAppID;//微信AppID
    private boolean mFirstLoad = true;
    private SparseArray<String> mSparseArray;

    @Override
    protected int getLayoutId() {
        return R.layout.holder_coin1;
    }

    @Override
    public void init() {
        mSparseArray = new SparseArray<>();
        mCoinName = AppConfig.getInstance().getCoinName();
        mTop = findViewById(R.id.top);
        mBalance = (TextView) findViewById(R.id.balance);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CoinAdapter(mContext, DpUtil.dp2px(150), mCoinName);
        mAdapter.setContactView(mTop);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void loadData() {
        super.loadData();
        if (mFirstLoad) {
            mFirstLoad = false;
            loadNewData();
        } else {
            checkPayResult();
        }
    }

    private void loadNewData() {
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = StringUtil.converMoney2Point(obj.getString("coin"));
                    mBalanceValue = coin ;
                    mBalance.setText(coin);
                    List<CoinBean> list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (mAdapter != null) {
                        mAdapter.setList(list);
                    }
                    mAliPartner = obj.getString("aliapp_partner");
                    mAliSellerId = obj.getString("aliapp_seller_id");
                    mAliPrivateKey = obj.getString("aliapp_key_android");
                    mWxAppID = obj.getString("wx_appid");
                    boolean aliPayEnable = obj.getIntValue("aliapp_switch") == 1;//支付宝是否开启
                    boolean wxPayEnable = obj.getIntValue("wx_switch") == 1;//微信支付是否开启
                    if (aliPayEnable) {
                        mSparseArray.put(Constants.PAY_TYPE_ALI, WordUtil.getString(R.string.coin_pay_ali));
                    }
                    if (wxPayEnable) {
                        mSparseArray.put(Constants.PAY_TYPE_WX, WordUtil.getString(R.string.coin_pay_wx));
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(CoinBean bean, int position) {
        mCheckedCoinBean = bean;
        if (mSparseArray == null || mSparseArray.size() == 0) {
            ToastUtil.show(Constants.PAY_ALL_NOT_ENABLE);
            return;
        }
        DialogUitl.showStringArrayDialog(mContext, mSparseArray, mArrayDialogCallback);
    }

    private DialogUitl.StringArrayDialogCallback mArrayDialogCallback = new DialogUitl.StringArrayDialogCallback() {
        @Override
        public void onItemClick(String text, int tag) {
            switch (tag) {
                case Constants.PAY_TYPE_ALI://支付宝支付
                    aliPay();
                    break;
                case Constants.PAY_TYPE_WX://微信支付
                    wxPay();
                    break;
            }
        }
    };

    private void aliPay() {
        if (!AppConfig.isAppExist(Constants.PACKAGE_NAME_ALI)) {
            ToastUtil.show(R.string.coin_ali_not_install);
            return;
        }
        if (TextUtils.isEmpty(mAliPartner) || TextUtils.isEmpty(mAliSellerId) || TextUtils.isEmpty(mAliPrivateKey)) {
            ToastUtil.show(Constants.PAY_ALI_NOT_ENABLE);
            return;
        }
        AliPayBuilder builder = new AliPayBuilder((Activity) mContext, mAliPartner, mAliSellerId, mAliPrivateKey);
        builder.setCoinName(mCoinName);
        builder.setCoinBean(mCheckedCoinBean);
        builder.setPayCallback(mPayCallback);
        builder.pay();
    }

    private void wxPay() {
        if (!AppConfig.isAppExist(Constants.PACKAGE_NAME_WX)) {
            ToastUtil.show(R.string.coin_wx_not_install);
            return;
        }
        if (TextUtils.isEmpty(mWxAppID)) {
            ToastUtil.show(Constants.PAY_WX_NOT_ENABLE);
            return;
        }
        WxPayBuilder builder = new WxPayBuilder(mContext, mWxAppID);
        builder.setCoinBean(mCheckedCoinBean);
        builder.setPayCallback(mPayCallback);
        builder.pay();
    }

    PayCallback mPayCallback = new PayCallback() {
        @Override
        public void onSuccess() {
            // checkPayResult();
        }

        @Override
        public void onFailed() {
            //ToastUtil.show(R.string.coin_charge_failed);
        }
    };

    /**
     * 检查支付结果
     */
    private void checkPayResult() {
        HttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = StringUtil.converMoney2Point(obj.getString("coin"));
                    mBalance.setText(coin);

                    BigDecimal oldBalance = new BigDecimal(mBalanceValue) ;
                    BigDecimal newBalance = new BigDecimal(coin) ;

                    if(oldBalance.compareTo(newBalance) > 0){//值大
                        mBalanceValue = coin ;
                        ToastUtil.show(R.string.coin_charge_success);
                        UserBean u = AppConfig.getInstance().getUserBean();
                        if (u != null) {
                            u.setCoin(coin);
                        }
                        EventBus.getDefault().post(new CoinChangeEvent(coin, true));
                    }
                }
            }
        });
    }

    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyCoinActivity.class));
    }
}
