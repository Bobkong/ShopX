package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetLoyaltyInfoResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("loyalty_info")
    LoyaltyProgramResponse loyaltyInfo;

    @SerializedName("is_enrolled")
    int isEnrolled;

    @SerializedName("points")
    int points;

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

    public LoyaltyProgramResponse getLoyaltyInfo() {
        return loyaltyInfo;
    }

    public void setLoyaltyInfo(LoyaltyProgramResponse loyaltyInfo) {
        this.loyaltyInfo = loyaltyInfo;
    }

    public int getIsEnrolled() {
        return isEnrolled;
    }

    public void setIsEnrolled(int isEnrolled) {
        this.isEnrolled = isEnrolled;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
