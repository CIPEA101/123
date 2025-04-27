package com.yunbao.phonelive.bean;

import com.yunbao.phonelive.game.TzInfoBean;
import com.yunbao.phonelive.http.HttpConsts;

import java.util.ArrayList;
import java.util.List;

public class NiurenList {
    private String uid ;
    private String user_nickname ;
    private String avatar ;
    private String total_totting ;
    private String win ;
    private String probability ;

    private String game_id ;
    private String game_name ;
    private String periods ;

    private String second ;

    private String type ;//玩法，去本地对比
    private List<TzInfoBean> types ;

    private long endTime;//自定义结束时间

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTotal_totting() {
        return total_totting;
    }

    public void setTotal_totting(String total_totting) {
        this.total_totting = total_totting;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getTottingWinRote(){
        return "场次/" + total_totting + "中" + win ;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getProbabilityWinRote(){
        return "胜率/" + probability + "%" ;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }


    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName(){
        return HttpConsts.getGameTypeNameById(game_id,type) ;
    }

    public List<TzInfoBean> getTypes() {
        return null == types ? new ArrayList<TzInfoBean>() : types ;
    }

    public void setTypes(List<TzInfoBean> types) {
        this.types = types;
    }
}
