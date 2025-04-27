package com.yunbao.phonelive.game;

import android.text.TextUtils;

import com.yunbao.phonelive.utils.StringUtil;

import java.math.BigDecimal;

public class TzListBean {
    private String id;
    private String totalcoin;
    private String is_ok;//是否中奖 1中奖 0没有中奖 3 未中奖
    private String game_id;
    private String datetime;
    private String ok ;//0 未开奖  1 已开奖
    private  int time;

    private String magnification;//倍率

    private long endTime;//自定义结束时间


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotalcoin() {
        return totalcoin;
    }

    public void setTotalcoin(String totalcoin) {
        this.totalcoin = totalcoin;
    }

    public String getIs_ok() {
        return is_ok;
    }

    public void setIs_ok(String is_ok) {
        this.is_ok = is_ok;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getGame_id() {
        return StringUtil.convertNull(game_id);
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getMagnification() {
        return magnification;
    }

    public void setMagnification(String magnification) {
        this.magnification = magnification;
    }

    //是否开奖了
    public boolean isTzKj(){
        return "1".equals(ok) ;
    }
    public boolean isZhongjiang(){
        return "1".equals(is_ok) ;
    }

    /**
     * 投注总金额
     * @return 投注金额 * 倍率
     */
    public String getTotalMoney(){
        if(!TextUtils.isEmpty(totalcoin) && !TextUtils.isEmpty(magnification)){
            BigDecimal moneyDec = new BigDecimal(totalcoin) ;
            BigDecimal roteDec = new BigDecimal(magnification) ;

            BigDecimal totalMoneyDec = moneyDec.multiply(roteDec).setScale(2,BigDecimal.ROUND_HALF_UP) ;

            return totalMoneyDec.stripTrailingZeros().toPlainString() ;
        }else{
            return null == totalcoin ? "" : totalcoin ;
        }
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
