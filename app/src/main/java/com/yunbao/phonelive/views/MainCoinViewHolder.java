package com.yunbao.phonelive.views;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.MyCoinActivity;
import com.yunbao.phonelive.activity.WebViewServiceActivity;
import com.yunbao.phonelive.adapter.RechargeArtificialAdapter;
import com.yunbao.phonelive.adapter.RechargeChooseMoneyAdapter;
import com.yunbao.phonelive.bean.ArticialBean;
import com.yunbao.phonelive.bean.CoinBean;
import com.yunbao.phonelive.bean.RechargeBankBean;
import com.yunbao.phonelive.bean.RechargeChooseMoneyBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.http.JsonBean1;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.interfaces.OnRvClickListener;
import com.yunbao.phonelive.utils.CollectionUtils;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.L;
import com.yunbao.phonelive.utils.StringUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MainCoinViewHolder extends AbsMainViewHolder implements OnItemClickListener<CoinBean>, OnRvClickListener {

    public MainCoinViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    private View mTop;
    private TextView mBalance;
    private String mBalanceValue;
    private RecyclerView mRecyclerView;//人工
    private RechargeArtificialAdapter mAdapter;//人工
    private String mCoinName;
    private CoinBean mCheckedCoinBean;
    private String mAliPartner;// 支付宝商户ID
    private String mAliSellerId; // 支付宝商户收款账号
    private String mAliPrivateKey; // 支付宝商户私钥，pkcs8格式
    private String mWxAppID;//微信AppID
    private boolean mFirstLoad = true;
    private SparseArray<String> mSparseArray;
    RelativeLayout rl_recharge_unionpay, rl_recharge_alipay, rl_recharge_weixin, rl_recharge_artificial;
    LinearLayout ll_unionpay, ll_artificial,ll_alipay;
    private TextView tv_recharge_money_des;
    private List<View> viewList ;
    private View mBtnCash,mBtnCashAlipay;
    private EditText mEdit;

    private List<RechargeChooseMoneyBean> moneyBeanList = new ArrayList<>();
    private RecyclerView xxRecy;
    private RechargeChooseMoneyAdapter mChooseAdapter;
    private int currentPosition;//
    private Dialog moneyDialog;

    //公告
    private TextView mWarningTv;
    private int mWarningTransX;
    //银行卡
    private TextView tv_recharge_unionpay_number;
    private TextView tv_recharge_unionpay_name;
    private TextView tv_recharge_unionpay_bank_name;
    private TextView tv_recharge_unionpay_bank_des;
    private ImageView iv_copy_number;
    private ImageView iv_copy_name;
    private ImageView iv_copy_bank_name;
    private ImageView iv_artificial_copy_number;
    private RechargeBankBean bean;
    private EditText act_add_bank_owner_ev;
    private EditText act_add_bank_net_ev;
    //人工
    private TextView tv_recharge_artificial_number;
    private List<ArticialBean> rechargeBankBeans = new ArrayList<>();//人工数据
    private String user_login;
    //
    private int typePay;//0银行卡 1支付宝 2微信

    @Override
    protected int getLayoutId() {
        return R.layout.holder_coin;
    }

    @Override
    public void init() {
        mWarningTv = (TextView) findViewById(R.id.view_live_room_warning_tv);
        mWarningTv.setSelected(true);

        moneyDialog = DialogUitl.loadingDialog(mContext);
        mSparseArray = new SparseArray<>();
        mCoinName = AppConfig.getInstance().getCoinName();
        xxRecy = (RecyclerView) findViewById(R.id.rcv_choose_money);
        xxRecy.setLayoutManager(new GridLayoutManager(mContext, 4));

        tv_recharge_money_des = (TextView) findViewById(R.id.tv_recharge_money_des);
        ll_unionpay = (LinearLayout) findViewById(R.id.ll_unionpay);
        ll_artificial = (LinearLayout) findViewById(R.id.ll_artificial);
        ll_alipay = (LinearLayout) findViewById(R.id.ll_alipay);
        rl_recharge_artificial = (RelativeLayout) findViewById(R.id.rl_recharge_artificial);
        rl_recharge_unionpay = (RelativeLayout) findViewById(R.id.rl_recharge_unionpay);
        rl_recharge_alipay = (RelativeLayout) findViewById(R.id.rl_recharge_alipay);
        rl_recharge_weixin = (RelativeLayout) findViewById(R.id.rl_recharge_weixin);
        mBtnCash = findViewById(R.id.btn_cash);
        mBtnCashAlipay = findViewById(R.id.btn_cash_alipay);
        mEdit = (EditText) findViewById(R.id.edit);
        viewList=new ArrayList<>();
        viewList.add(rl_recharge_artificial);
        viewList.add(rl_recharge_unionpay);
        viewList.add(rl_recharge_alipay);
        viewList.add(rl_recharge_weixin);
        mTop = findViewById(R.id.top);
        mBalance = (TextView) findViewById(R.id.balance);
        mRecyclerView = (RecyclerView) findViewById(R.id.rcv_choose_type);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        initBank();
        setOnClick();
        loadChooseData();
        loadArtificialData();
    }

    @Override
    public void loadData() {
        super.loadData();

    }

    private void initBank() {
        tv_recharge_unionpay_number = (TextView) findViewById(R.id.tv_recharge_unionpay_number);
        tv_recharge_unionpay_name = (TextView) findViewById(R.id.tv_recharge_unionpay_name);
        tv_recharge_unionpay_bank_name = (TextView) findViewById(R.id.tv_recharge_unionpay_bank_name);
        tv_recharge_unionpay_bank_des = (TextView) findViewById(R.id.tv_recharge_unionpay_bank_des);
        iv_copy_number = (ImageView) findViewById(R.id.iv_copy_number);
        iv_copy_name = (ImageView) findViewById(R.id.iv_copy_name);
        iv_copy_bank_name = (ImageView) findViewById(R.id.iv_copy_bank_name);
        iv_artificial_copy_number = (ImageView) findViewById(R.id.iv_artificial_copy_number);
        act_add_bank_owner_ev = (EditText) findViewById(R.id.act_add_bank_owner_ev);
        act_add_bank_net_ev = (EditText) findViewById(R.id.act_add_bank_net_ev);
        //人工
        tv_recharge_artificial_number = (TextView) findViewById(R.id.tv_recharge_artificial_number);
    }

    private void loadArtificialData() {
        HttpUtil.loadArtificialData(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject json = JSON.parseObject(info[0]);
                    JSONObject object = json.getJSONObject("user_login");
                    if (object != null) {
                        user_login = object.getString("user_login");
                        tv_recharge_artificial_number.setText("您的充值账号：" + user_login);
                    }
                    List<ArticialBean> list = JSON.parseArray(json.getString("list"), ArticialBean.class);
                    rechargeBankBeans=new ArrayList<>();
                    rechargeBankBeans.clear();
                    if (CollectionUtils.isEmpty(list)) return;
                    rechargeBankBeans.addAll(list);
                    mAdapter = new RechargeArtificialAdapter(mContext, rechargeBankBeans);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
        });
    }

    private void loadChooseData() {
        HttpUtil.getChooseBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject json = JSON.parseObject(info[0]);
                    List<RechargeChooseMoneyBean> list = JSON.parseArray(json.getString("coins"), RechargeChooseMoneyBean.class);
                    if (CollectionUtils.isEmpty(list)) return;
                    moneyBeanList=new ArrayList<>();
                    moneyBeanList.addAll(list);
                    mChooseAdapter = new RechargeChooseMoneyAdapter(mContext, moneyBeanList, MainCoinViewHolder.this);
                    xxRecy.setAdapter(mChooseAdapter);
                    //赋值银行卡
                    List<RechargeBankBean> beanList = JSON.parseArray(json.getString("card"), RechargeBankBean.class);
                    if (CollectionUtils.isNotEmpty(beanList)) {
                        bean = beanList.get(0);
                        if (bean == null) return;
                        tv_recharge_unionpay_number.setText("银行卡号：" + bean.card_no);
                        tv_recharge_unionpay_name.setText("收款姓名：" + bean.realname);
                        tv_recharge_unionpay_bank_name.setText("开户银行：" + bean.bank_name);
                        tv_recharge_unionpay_bank_des.setText("开户网点：" + bean.outlets);
                    }
                }
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        HttpUtil.cancel(HttpConsts.GET_BALANCE);
//        HttpUtil.cancel(HttpConsts.GET_ALI_ORDER);
//        HttpUtil.cancel(HttpConsts.GET_WX_ORDER);
//        if (mWarningTv != null) {
//            mWarningTv.clearAnimation();
//        }
//        super.onDestroy();
//    }

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

    }


    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyCoinActivity.class));
    }

    private void setOnClick() {
        rl_recharge_artificial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_artificial.setVisibility(View.VISIBLE);
                ll_unionpay.setVisibility(View.GONE);
                ll_alipay.setVisibility(View.GONE);
                tv_recharge_money_des.setVisibility(View.GONE);
                xxRecy.setVisibility(View.GONE);
                rl_recharge_artificial.setBackgroundResource(R.mipmap.icon_recharge_bg);
                rl_recharge_unionpay.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_alipay.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_weixin.setBackgroundResource(R.drawable.bg_recharge_no);
            }
        });
        rl_recharge_unionpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_alipay.setVisibility(View.GONE);
                ll_artificial.setVisibility(View.GONE);
                ll_unionpay.setVisibility(View.VISIBLE);
                tv_recharge_money_des.setVisibility(View.VISIBLE);
                xxRecy.setVisibility(View.VISIBLE);
                rl_recharge_unionpay.setBackgroundResource(R.mipmap.icon_recharge_bg);
                rl_recharge_artificial.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_alipay.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_weixin.setBackgroundResource(R.drawable.bg_recharge_no);
                typePay=0;
            }
        });
        rl_recharge_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.show(R.string.pay_not_support) ;

                ll_artificial.setVisibility(View.GONE);
                ll_unionpay.setVisibility(View.GONE);
                ll_alipay.setVisibility(View.VISIBLE);
                tv_recharge_money_des.setVisibility(View.VISIBLE);
                xxRecy.setVisibility(View.VISIBLE);
                rl_recharge_unionpay.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_artificial.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_alipay.setBackgroundResource(R.mipmap.icon_recharge_bg);
                rl_recharge_weixin.setBackgroundResource(R.drawable.bg_recharge_no);
                typePay=1;
            }
        });
        rl_recharge_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.show(R.string.pay_not_support) ;

                ll_artificial.setVisibility(View.GONE);
                ll_unionpay.setVisibility(View.GONE);
                ll_alipay.setVisibility(View.VISIBLE);
                tv_recharge_money_des.setVisibility(View.VISIBLE);
                xxRecy.setVisibility(View.VISIBLE);
                rl_recharge_unionpay.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_artificial.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_alipay.setBackgroundResource(R.drawable.bg_recharge_no);
                rl_recharge_weixin.setBackgroundResource(R.mipmap.icon_recharge_bg);
                typePay=2;
            }
        });
        mBtnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //处理点击事件
                String moeny = mEdit.getText().toString().trim();
                if (StringUtil.notEmpty(moeny)) {
                    double money = Double.parseDouble(moeny) * 100;
                    submit(money);
                }else {
                    ToastUtil.show("请输入正确金额");
                }
            }
        });
        mBtnCashAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //处理点击事件
                String moeny = mEdit.getText().toString().trim();
                if (StringUtil.notEmpty(moeny)) {
                    double money = Double.parseDouble(moeny) * 100;
                    submit(money);
                }else {
                    ToastUtil.show("请输入正确金额");
                }
            }
        });
        mEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    for (RechargeChooseMoneyBean bean : moneyBeanList) {
                        bean.isChoose = false;
                    }
                    if (mChooseAdapter != null) {
                        mChooseAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
//        mEdit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 0) {
//                    long i = Long.parseLong(s.toString());
//
//                    mBtnCash.setEnabled(true);
//                } else {
//                    mBtnCash.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        iv_copy_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean == null) return;
                goCopy( bean.card_no);
            }
        });
        iv_copy_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean == null) return;
                goCopy( bean.realname);
            }
        });
        iv_copy_bank_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean == null) return;
                goCopy( bean.bank_name);
            }
        });
        iv_artificial_copy_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.empty(user_login)) return;
                goCopy(user_login);
            }
        });
    }

    /**
     * 判断选择了那个金额
     *
     * @param type
     * @param position
     */
    @Override
    public void onClick(int type, int position) {
        currentPosition = position;
        for (int i = 0; i < moneyBeanList.size(); i++) {
            if (position == i) {
                moneyBeanList.get(i).isChoose = true;
            } else {
                moneyBeanList.get(i).isChoose = false;
            }
        }
        mChooseAdapter.notifyDataSetChanged();
        //处理界面
        mEdit.setText(moneyBeanList.get(position).money);
        //让文本框失去焦点
        if (mEdit != null) {
            //判断是否有键盘
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEdit.getWindowToken(), 0);
            mEdit.clearFocus();
        }
    }

    private void submit(double money) {

        switch (typePay){
            case 0:
                gotoUniopay();
                break;
            case 1:
                ToastUtil.show(R.string.pay_not_support) ;

                //支付宝
//                moneyDialog.show();
//                gotoAlipay(money);
                break;
            case 2:
                ToastUtil.show(R.string.pay_not_support) ;

                //微信
//                moneyDialog.show();
//                gotoWeixin(money);
                break;
        }

    }
    private void gotoUniopay(){
        String amount = mEdit.getText().toString().trim();
        String card_no = bean.card_no;
        String leav_msg = act_add_bank_net_ev.getText().toString().trim();
        String remitter = act_add_bank_owner_ev.getText().toString().trim();
        if(StringUtil.empty(remitter)){
            ToastUtil.show("请输入您的汇款姓名");
            return;
        }
        if(StringUtil.empty(amount)){
            ToastUtil.show("请输入充值金额");
            return;
        }
        if(amount.startsWith("0")||amount.startsWith(".")){
            ToastUtil.show("请输入正确充值金额");
            return;
        }
        moneyDialog.show();
        HttpUtil.addPayOrder(amount, card_no, leav_msg, remitter, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(0==code){
                    ToastUtil.show("提交成功");
                }
                if (moneyDialog != null) {
                    moneyDialog.dismiss();
                }
            }

            @Override
            public void onError() {
                super.onError();
                if (moneyDialog != null) {
                    moneyDialog.dismiss();
                }
            }
        });
    }
    private void gotoAlipay(final double money){
//                    HttpUtil.money2(money,new AbsCallback<JsonBean1>(){
//
//                @Override
//                public JsonBean1 convertResponse(okhttp3.Response response) throws Throwable {
//                    return JSON.parseObject(response.body().string(), JsonBean1.class);
//                }
//
//                @Override
//                public void onSuccess(Response<JsonBean1> response) {
//                            if(moneyDialog!=null){
//                                moneyDialog.dismiss();
//                            }
//                    JsonBean1 bean = response.body();
//                    if (bean != null) {
//                        if (200 == bean.getRet()) {
//                            String data = bean.getData();
//                            if (data != null) {
////                                JSONObject obj=JSON.parseObject(bean.getData());
////                                String string=obj.getString("data");
////                                ToastUtil.show(string);
//                                WebViewServiceActivity.forward(mContext, data);
////                                TestMoneyActivity.start(mContext,string);
//                            } else {
//                                L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
//                            }
//                        } else {
//                            L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
//                        }
//
//                    } else {
//                        L.e("服务器返回值异常--->bean = null");
//                    }
//
//                }
//                        @Override
//                        public void onError(Response<JsonBean1> response) {
//                            super.onError(response);
//                            if(moneyDialog!=null){
//                                moneyDialog.dismiss();
//                            }
//                        }
//            });
        HttpUtil.money(money,new AbsCallback<JsonBean1>(){
            //
            @Override
            public JsonBean1 convertResponse(okhttp3.Response response) throws Throwable {
                return JSON.parseObject(response.body().string(), JsonBean1.class);
            }

            @Override
            public void onSuccess(Response<JsonBean1> response) {
                if(moneyDialog!=null){
                    moneyDialog.dismiss();
                }
                JsonBean1 bean = response.body();
                if (bean != null) {
                    if (200 == bean.getRet()) {
                        String data = bean.getData();
                        if (data != null) {
//                                JSONObject obj=JSON.parseObject(bean.getData());
//                                String string=obj.getString("data");
//                                ToastUtil.show(string);
                            WebViewServiceActivity.forward(mContext, data);
//                                TestMoneyActivity.start(mContext,string);
                        } else {
                            L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                        }
                    } else {
                        L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                    }

                } else {
                    L.e("服务器返回值异常--->bean = null");
                }
            }

            @Override
            public void onError(Response<JsonBean1> response) {
                super.onError(response);
                if(moneyDialog!=null){
                    moneyDialog.dismiss();
                }
            }
        });
    }
    private void gotoWeixin(final double money){
        HttpUtil.money2(money,new AbsCallback<JsonBean1>(){
            //
            @Override
            public JsonBean1 convertResponse(okhttp3.Response response) throws Throwable {
                return JSON.parseObject(response.body().string(), JsonBean1.class);
            }

            @Override
            public void onSuccess(Response<JsonBean1> response) {
                if(moneyDialog!=null){
                    moneyDialog.dismiss();
                }
                JsonBean1 bean = response.body();
                if (bean != null) {
                    if (200 == bean.getRet()) {
                        String data = bean.getData();
                        if (data != null) {
//                                JSONObject obj=JSON.parseObject(bean.getData());
//                                String string=obj.getString("data");
//                                ToastUtil.show(string);
//                                WebViewServiceActivity.forward(mContext, data);
//                            TestMoneyActivity.start(mContext,"https://order.duolabao.com/active/c?state=182020070418130551417%7C10011015937630765379998%7C300.00%7C%7CAPI");
                        } else {
                            L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                        }
                    } else {
                        L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                    }

                } else {
                    L.e("服务器返回值异常--->bean = null");
                }
            }

            @Override
            public void onError(Response<JsonBean1> response) {
                super.onError(response);
                if(moneyDialog!=null){
                    moneyDialog.dismiss();
                }
            }
        });
    }

    private void goCopy(String content){
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);;
        cm.setPrimaryClip(clipData);
        ToastUtil.show(mContext.getString(R.string.copy_success));

    }
}
