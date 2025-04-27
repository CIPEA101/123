package com.yunbao.phonelive.event;

public class RegisterEvent {
    private int type ;
    public static final int REG_TYPE_REG = 0 ;
    public static final int REG_TYPE_SETPASS = 1 ;
    public static final int REG_TYPE_LOGIN = 2 ;

    public int getType() {
        return type;
    }

    public RegisterEvent(int type) {
        this.type = type;
    }
}
