package com.yunbao.phonelive.bean;

import com.yunbao.phonelive.utils.StringUtil;

public class UserShareCode {
    private String avatar ;
    private String signature ;
    private String code ;
    private String link ;

    public String getAvatar() {
        return StringUtil.convertImageUrl(avatar);
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSignature() {
        return StringUtil.convertNull(signature);
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getCode() {
        return StringUtil.convertNull(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLink() {
        return StringUtil.convertNull(link);
    }

    public void setLink(String link) {
        this.link = link;
    }
}
