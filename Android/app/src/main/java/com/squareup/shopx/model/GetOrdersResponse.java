package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetOrdersResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("orders")
    ArrayList<PlaceOrderRequest> allOrders;

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

    public ArrayList<PlaceOrderRequest> getAllOrders() {
        return allOrders;
    }

    public void setAllOrders(ArrayList<PlaceOrderRequest> allOrders) {
        this.allOrders = allOrders;
    }
}
