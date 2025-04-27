package com.yunbao.phonelive.game;

public class TzInfoBean {
    private String code;
    private String datetime;
    private String game_id;
    private String id;
    private String is_ok;
    private String magnification;
    private String odds;
    private String ok;
    private String totalcoin;
    private String type;
    private String uid;
    private String result;
    private String yfsc_id;
    private String yfks_id;
    private String yf115_id;
    private String yfssc_id;
    private String yflhc_id;
    private String yfklsf_id;
    private String yfxync_id;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIs_ok() {
        return is_ok;
    }

    public void setIs_ok(String is_ok) {
        this.is_ok = is_ok;
    }

    public String getMagnification() {
        return magnification;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setMagnification(String magnification) {
        this.magnification = magnification;
    }

    public String getOdds() {
        return odds;
    }

    public void setOdds(String odds) {
        this.odds = odds;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    public String getTotalcoin() {
        return totalcoin;
    }

    public void setTotalcoin(String totalcoin) {
        this.totalcoin = totalcoin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getYfsc_id() {
        return yfsc_id;
    }

    public void setYfsc_id(String yfsc_id) {
        this.yfsc_id = yfsc_id;
    }

    public String getYfks_id() {
        return yfks_id;
    }

    public void setYfks_id(String yfks_id) {
        this.yfks_id = yfks_id;
    }

    public String getYf115_id() {
        return yf115_id;
    }

    public void setYf115_id(String yf115_id) {
        this.yf115_id = yf115_id;
    }

    public String getYfssc_id() {
        return yfssc_id;
    }

    public void setYfssc_id(String yfssc_id) {
        this.yfssc_id = yfssc_id;
    }

    public String getYflhc_id() {
        return yflhc_id;
    }

    public void setYflhc_id(String yflhc_id) {
        this.yflhc_id = yflhc_id;
    }

    public String getYfklsf_id() {
        return yfklsf_id;
    }

    public void setYfklsf_id(String yfklsf_id) {
        this.yfklsf_id = yfklsf_id;
    }

    public String getYfxync_id() {
        return yfxync_id;
    }

    public void setYfxync_id(String yfxync_id) {
        this.yfxync_id = yfxync_id;
    }

    public String getPeriodId(){
        if("1".equals(game_id)){
            return yfks_id ;
        } else if("2".equals(game_id)){
            return yf115_id ;
        } else if("3".equals(game_id)){
            return yfsc_id ;
        } else if("4".equals(game_id)){
            return yfssc_id ;
        } else if("5".equals(game_id)){
            return yflhc_id ;
        } else if("6".equals(game_id)){
            return yfklsf_id ;
        } else if("7".equals(game_id)){
            return yfxync_id ;
        }else{
            return yfks_id ;
        }
    }
}
