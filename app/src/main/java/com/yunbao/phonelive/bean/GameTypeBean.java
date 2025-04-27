package com.yunbao.phonelive.bean;

import java.io.Serializable;

public class GameTypeBean implements Serializable {
    private String id ;
    private String name ;
    private String status ;

    public GameTypeBean() {
    }

    public GameTypeBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isShow(){
        return "1".equals(status) ;
    }
}
