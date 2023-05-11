package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EnrollLoyaltyResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("loyalty_account")
    String loyaltyAccount;

    public String getLoyaltyAccount() {
        return loyaltyAccount;
    }

    public void setLoyaltyAccount(String loyaltyAccount) {
        this.loyaltyAccount = loyaltyAccount;
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
}
