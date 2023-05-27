package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetAllLoyaltyRecordsResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("loyalty_merchants")
    ArrayList<AllMerchantsResponse.ShopXMerchant> loyaltyMerchants;

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

    public ArrayList<AllMerchantsResponse.ShopXMerchant> getLoyaltyMerchants() {
        return loyaltyMerchants;
    }

    public void setLoyaltyMerchants(ArrayList<AllMerchantsResponse.ShopXMerchant> loyaltyMerchants) {
        this.loyaltyMerchants = loyaltyMerchants;
    }
}
