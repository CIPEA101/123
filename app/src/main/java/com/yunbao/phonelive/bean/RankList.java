package com.yunbao.phonelive.bean;

import com.yunbao.phonelive.utils.StringUtil;

public class RankList {
    private String uid ;
    private String user_nicename ;
    private String user_nickname ;
    private String avatar ;
    private String level ;

    //贡献榜、礼物榜
    private String totalcoin ;

    //富豪榜
    private String coin ;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_nicename() {
        return null == user_nickname ? StringUtil.convertNull(user_nicename) : user_nickname;
    }

    public void setUser_nicename(String user_nickname) {
        this.user_nicename = user_nickname;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return StringUtil.convertNull(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getLevel() {
        return StringUtil.convertInt(level);
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTotalcoin() {
        return StringUtil.converMoney2Point(totalcoin);
    }

    public void setTotalcoin(String totalcoin) {
        this.totalcoin = totalcoin;
    }

    public String getCoin() {
        return StringUtil.converMoney2Point(coin);
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
