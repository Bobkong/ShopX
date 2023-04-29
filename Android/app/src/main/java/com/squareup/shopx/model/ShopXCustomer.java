package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShopXCustomer implements Serializable {
    @SerializedName("contact")
    String contact;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("ifNotify")
    int ifNotify;

    @SerializedName("password")
    String password;

    public ShopXCustomer(String contact, String nickname, int ifNotify, String password) {
        this.contact = contact;
        this.nickname = nickname;
        this.ifNotify = ifNotify;
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
