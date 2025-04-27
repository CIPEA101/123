package com.yunbao.phonelive.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RankCurrentUserBean {
    public String id;
    public String user_nicename;
    public String avatar;
    public String avatar_thumb;
    public String sex;
    public String signature;
    public String consumption;
    public String votestotal;
    public String province;
    public String city;
    public String birthday;
    public String user_status;
    public String issuper;
    public String location;
    public String ishost;
    public String isLive;
    public String level;
    public String level_anchor;
    public Vip vip;
    public Liang liang;
    public String is_stay;
    public String totalcoin;

public static class Vip implements Parcelable {
    protected int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Vip() {

    }

    public Vip(Parcel in) {
        this.type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
    }

    public static final Creator<Vip> CREATOR = new Creator<Vip>() {
        @Override
        public Vip[] newArray(int size) {
            return new Vip[size];
        }

        @Override
        public Vip createFromParcel(Parcel in) {
            return new Vip(in);
        }
    };
}

    public static class Liang implements Parcelable {
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Liang() {

        }

        public Liang(Parcel in) {
            this.name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public static final Creator<Liang> CREATOR = new Creator<Liang>() {

            @Override
            public Liang createFromParcel(Parcel in) {
                return new Liang(in);
            }

            @Override
            public Liang[] newArray(int size) {
                return new Liang[size];
            }
        };

    }
}
