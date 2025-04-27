package com.yunbao.phonelive.socket;

import java.util.ArrayList;
import java.util.List;

public class UserUidResultBean {
    private List<Integer> list_id;
    private List<String> list_nicename;

    public List<Integer> getList_id() {
        return list_id;
    }

    public void setList_id(List<Integer> list_id) {
        this.list_id = list_id;
    }

    public void setList_nicename(List<String> list_nicename) {
        this.list_nicename = list_nicename;
    }

    public List<String> getList_nicename() {
        return null == list_nicename ? new ArrayList<String>() : list_nicename ;
    }
}
