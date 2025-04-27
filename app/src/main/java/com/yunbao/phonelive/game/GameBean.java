package com.yunbao.phonelive.game;

import android.view.View;

import com.yunbao.phonelive.R;

public class GameBean {
    private String name;
    private int imageId;
    private int page;
    private int value;
    private boolean checked;
    private View mView;
    private int customValue = 0 ;//自定义金额
    private boolean isCustom = false ;

    public GameBean(){
    }
    public GameBean(String name,int imageId){
        this.name = name;
        this.imageId = imageId;
    }
    public GameBean(boolean custom){
        this.isCustom = custom ;
        this.name = "自定义筹码" ;
        this.imageId = R.mipmap.icon_game_chip_hundred_empty ;
    }

    public GameBean(int value,int imageId,boolean checked){
        this.imageId = imageId;
        this.value = value;
        this.checked = checked;
    }

    public GameBean(String name,int imageId,boolean checked){
        this.name = name;
        this.imageId = imageId;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public View getView() {
        return mView;
    }

    public void setView(View mView) {
        this.mView = mView;
    }

    public int getCustomValue() {
        return customValue;
    }

    public void setCustomValue(int customValue) {
        this.customValue = customValue;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }
}
