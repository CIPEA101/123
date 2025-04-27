package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class RechargeChooseMoneyBean implements Serializable {
    /**
     *  "name": "2000钻石",
     *         "coin": "2000",
     *         "coin_ios": "46000",
     *         "money": "2000.00",
     *         "money_ios": "0.00",
     *         "product_id": "coin_46000",
     *         "give": "1688"
     */
    public String name;
    public String coin;
    public String coin_ios;
    public String money;
    public String money_ios;
    public String product_id;
    public String give;

    public boolean isChoose;//自定义使用
}
