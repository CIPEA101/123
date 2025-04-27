package com.yunbao.phonelive.game;

import android.os.Parcel;
import android.os.Parcelable;

public class TzChooseBean implements Parcelable {
    private String tzOption;//筹码
    private String tzName;// 例如：一分快三-短牌
    private String tzInject;//1倍1注
    private String tvDou;//加倍
    private String uid;    //用户ID
    private String type;   //1 大小单双 2三军 3短牌
    private String code;   //用户所选码
    private String totalcoin;//下注总金额
    private String odds;   //赔率
    private String magnification; //倍率
    private OptionBean item;

    public TzChooseBean(){}

    public TzChooseBean(String tzOption,String tzName,String uid,OptionBean item){
        this.tzOption = tzOption;
        this.tzName = tzName;
        this.uid = uid;
        this.item = item;
    }

    public String getTzOption() {
        return tzOption;
    }

    public void setTzOption(String tzOption) {
        this.tzOption = tzOption;
    }

    public String getTzName() {
        return tzName;
    }

    public void setTzName(String tzName) {
        this.tzName = tzName;
    }

    public String getTzInject() {
        return tzInject;
    }

    public void setTzInject(String tzInject) {
        this.tzInject = tzInject;
    }

    public String getTvDou() {
        return tvDou;
    }

    public String getMagnification() {
        return magnification;
    }

    public void setMagnification(String magnification) {
        this.magnification = magnification;
    }

    public void setTvDou(String tvDou) {
        this.tvDou = tvDou;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTotalcoin() {
        return totalcoin;
    }

    public void setTotalcoin(String totalcoin) {
        this.totalcoin = totalcoin;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public OptionBean getItem() {
        return item;
    }

    public void setItem(OptionBean item) {
        this.item = item;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tzOption);
        dest.writeString(this.tzName);
        dest.writeString(this.tzInject);
        dest.writeString(this.tvDou);
        dest.writeString(this.uid);
        dest.writeString(this.type);
        dest.writeString(this.code);
        dest.writeString(this.totalcoin);
        dest.writeString(this.odds);
        dest.writeSerializable(this.item);
    }

    protected TzChooseBean(Parcel in) {
        this.tzOption = in.readString();
        this.tzName = in.readString();
        this.tzInject = in.readString();
        this.tvDou = in.readString();
        this.uid = in.readString();
        this.type = in.readString();
        this.code = in.readString();
        this.totalcoin = in.readString();
        this.odds = in.readString();
        this.item = (OptionBean) in.readSerializable();
    }

    public static final Creator<TzChooseBean> CREATOR = new Creator<TzChooseBean>() {
        @Override
        public TzChooseBean createFromParcel(Parcel source) {
            return new TzChooseBean(source);
        }

        @Override
        public TzChooseBean[] newArray(int size) {
            return new TzChooseBean[size];
        }
    };
}
