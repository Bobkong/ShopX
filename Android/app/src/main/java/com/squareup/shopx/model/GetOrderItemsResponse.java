package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetOrderItemsResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("items")
    ArrayList<GetMerchantDetailResponse.Item> items;

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

    public ArrayList<GetMerchantDetailResponse.Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<GetMerchantDetailResponse.Item> items) {
        this.items = items;
    }
}
