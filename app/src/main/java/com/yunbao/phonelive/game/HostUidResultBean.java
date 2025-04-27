package com.yunbao.phonelive.game;

import java.util.List;

public class HostUidResultBean {
    private List<List<Integer>> list_id;
    private List<List<String>> list_nickname;
    private List<Double> host_jiangjin;

    public List<List<Integer>> getList_id() {
        return list_id;
    }

    public void setList_id(List<List<Integer>> list_id) {
        this.list_id = list_id;
    }

    public List<List<String>> getList_nickname() {
        return list_nickname;
    }

    public void setList_nickname(List<List<String>> list_nickname) {
        this.list_nickname = list_nickname;
    }

    public List<Double> getHost_jiangjin() {
        return host_jiangjin;
    }

    public void setHost_jiangjin(List<Double> host_jiangjin) {
        this.host_jiangjin = host_jiangjin;
    }
}
