package com.yunbao.phonelive.bean;

public class NiurenTopBean {
    private String userName ;
    private String userHead ;

    private int bgColor;
    private String typeName ;
    private int valueColor;
    private String valueName ;


    public NiurenTopBean(String userName, String userHead, int bgColor, String typeName, int valueColor, String valueName) {
        this.userName = userName;
        this.userHead = userHead;
        this.bgColor = bgColor;
        this.typeName = typeName;
        this.valueColor = valueColor;
        this.valueName = valueName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserHead() {
        return userHead;
    }

    public int getBgColor() {
        return bgColor;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getValueColor() {
        return valueColor;
    }

    public String getValueName() {
        return valueName;
    }
}
