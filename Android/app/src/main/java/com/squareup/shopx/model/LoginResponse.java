package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("customer")
    LoginCustomer customer;

    public LoginCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(LoginCustomer customer) {
        this.customer = customer;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class LoginCustomer implements Serializable {
        @SerializedName("nickname")
        String nickname;

        @SerializedName("ifNotify")
        int ifNotify;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getIfNotify() {
            return ifNotify;
        }

        public void setIfNotify(int ifNotify) {
            this.ifNotify = ifNotify;
        }
    }
}
