package com.yunbao.phonelive.socket;

import com.yunbao.phonelive.game.HostUidResultBean;

import java.util.List;

public class SocketCommonReceiveBean {
    private String dianshu_result;
    private List<HostUidResultBean> related_host_uid_result;
    private List<UserUidResultBean> related_user_uid_result;
    private String time;
    private String new_yf115_game_id;
    private String new_yfxync_game_id;
    private String new_yfks_game_id;
    private String new_yflhc_game_id;
    private String new_yfsc_game_id;
    private String new_yfssc_game_id;
    private String new_yfklsf_game_id;
    private String old_yf115_game_id;
    private String old_yfxync_game_id;
    private String old_yfks_game_id;
    private String old_yflhc_game_id;
    private String old_yfsc_game_id;
    private String old_yfssc_game_id;
    private String old_yfklsf_game_id;

    public String getNew_yfklsf_game_id() {
        return new_yfklsf_game_id;
    }

    public void setNew_yfklsf_game_id(String new_yfklsf_game_id) {
        this.new_yfklsf_game_id = new_yfklsf_game_id;
    }

    public String getNew_yfssc_game_id() {
        return new_yfssc_game_id;
    }

    public void setNew_yfssc_game_id(String new_yfssc_game_id) {
        this.new_yfssc_game_id = new_yfssc_game_id;
    }
    public String getNew_yfsc_game_id() {
        return new_yfsc_game_id;
    }

    public void setNew_yfsc_game_id(String new_yfsc_game_id) {
        this.new_yfsc_game_id = new_yfsc_game_id;
    }
    public String getNew_yflhc_game_id() {
        return new_yflhc_game_id;
    }

    public void setNew_yflhc_game_id(String new_yflhc_game_id) {
        this.new_yflhc_game_id = new_yflhc_game_id;
    }
    public String getNew_yfks_game_id() {
        return new_yfks_game_id;
    }

    public void setNew_yfks_game_id(String new_yfks_game_id) {
        this.new_yfks_game_id = new_yfks_game_id;
    }
    public String getNew_yfxync_game_id() {
        return new_yfxync_game_id;
    }

    public void setNew_yfxync_game_id(String new_yfxync_game_id) {
        this.new_yfxync_game_id = new_yfxync_game_id;
    }

    public String getNew_yf115_game_id() {
        return new_yf115_game_id;
    }

    public void setNew_yf115_game_id(String new_yf115_game_id) {
        this.new_yf115_game_id = new_yf115_game_id;
    }

    public String getDianshu_result() {
        return dianshu_result;
    }

    public void setDianshu_result(String dianshu_result) {
        this.dianshu_result = dianshu_result;
    }

    public List<HostUidResultBean> getRelated_host_uid_result() {
        return related_host_uid_result;
    }

    public void setRelated_host_uid_result(List<HostUidResultBean> related_host_uid_result) {
        this.related_host_uid_result = related_host_uid_result;
    }

    public List<UserUidResultBean> getRelated_user_uid_result() {
        return related_user_uid_result;
    }

    public void setRelated_user_uid_result(List<UserUidResultBean> related_user_uid_result) {
        this.related_user_uid_result = related_user_uid_result;
    }

    public String getOld_yf115_game_id() {
        return old_yf115_game_id;
    }

    public void setOld_yf115_game_id(String old_yf115_game_id) {
        this.old_yf115_game_id = old_yf115_game_id;
    }

    public String getOld_yfxync_game_id() {
        return old_yfxync_game_id;
    }

    public void setOld_yfxync_game_id(String old_yfxync_game_id) {
        this.old_yfxync_game_id = old_yfxync_game_id;
    }

    public String getOld_yfks_game_id() {
        return old_yfks_game_id;
    }

    public void setOld_yfks_game_id(String old_yfks_game_id) {
        this.old_yfks_game_id = old_yfks_game_id;
    }

    public String getOld_yflhc_game_id() {
        return old_yflhc_game_id;
    }

    public void setOld_yflhc_game_id(String old_yflhc_game_id) {
        this.old_yflhc_game_id = old_yflhc_game_id;
    }

    public String getOld_yfsc_game_id() {
        return old_yfsc_game_id;
    }

    public void setOld_yfsc_game_id(String old_yfsc_game_id) {
        this.old_yfsc_game_id = old_yfsc_game_id;
    }

    public String getOld_yfssc_game_id() {
        return old_yfssc_game_id;
    }

    public void setOld_yfssc_game_id(String old_yfssc_game_id) {
        this.old_yfssc_game_id = old_yfssc_game_id;
    }

    public String getOld_yfklsf_game_id() {
        return old_yfklsf_game_id;
    }

    public void setOld_yfklsf_game_id(String old_yfklsf_game_id) {
        this.old_yfklsf_game_id = old_yfklsf_game_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 根据游戏类型，获取相应的上期游戏期数
     * @param gameType 类型
     */
    public String getOldGamePeroid(String gameType){
        String oldPeroid ;
        if("1".equals(gameType)){
            oldPeroid = old_yfks_game_id ;
        }else if("2".equals(gameType)){
            oldPeroid = old_yf115_game_id ;
        }else if("3".equals(gameType)){
            oldPeroid = old_yfsc_game_id ;
        }else if("4".equals(gameType)){
            oldPeroid = old_yfssc_game_id ;
        }else if("5".equals(gameType)){
            oldPeroid = old_yflhc_game_id ;
        }else if("6".equals(gameType)){
            oldPeroid = old_yfklsf_game_id ;
        }else if("7".equals(gameType)){
            oldPeroid = old_yfxync_game_id ;
        }else{
            oldPeroid = old_yfks_game_id ;
        }

        return oldPeroid ;
    }
    /**
     * 根据游戏类型，获取相应的下期游戏期数
     * @param gameType 类型
     */
    public String getNewGamePeroid(String gameType){
        String newPeroid ;
        if("1".equals(gameType)){
            newPeroid = new_yfks_game_id ;
        }else if("2".equals(gameType)){
            newPeroid = new_yf115_game_id ;
        }else if("3".equals(gameType)){
            newPeroid = new_yfsc_game_id ;
        }else if("4".equals(gameType)){
            newPeroid = new_yfssc_game_id ;
        }else if("5".equals(gameType)){
            newPeroid = new_yflhc_game_id ;
        }else if("6".equals(gameType)){
            newPeroid = new_yfklsf_game_id ;
        }else if("7".equals(gameType)){
            newPeroid = new_yfxync_game_id ;
        }else{
            newPeroid = new_yfks_game_id ;
        }

        return newPeroid ;
    }
}
