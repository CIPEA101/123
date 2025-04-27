package com.yunbao.phonelive.game;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class OptionBean implements Serializable {
    private String id;//
    private String name;
    private String ratio;
    private String type;
    private String parentName;
    private boolean checked;

    public OptionBean() {
    }
    public OptionBean(String id, String ratio, String type, String name, String parentName,boolean checked) {
        this.id = id;
        this.ratio = ratio;
        this.type = type;
        this.name = name;
        this.parentName = parentName;
        this.checked = checked;
    }

    @JSONField(name = "id")
    public String getId() {
        return id;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        this.id = id;
    }

    @JSONField(name = "name")
    public String getName() {
        return name;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        this.name = name;
    }

    @JSONField(name = "type")
    public String getType() {
        return type;
    }

    @JSONField(name = "type")
    public void setType(String type) {
        this.type = type;
    }

    @JSONField(name = "ratio")
    public String getRatio() {
        return ratio;
    }

    @JSONField(name = "ratio")
    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
