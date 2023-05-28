package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetOrderItemsRequest implements Serializable {
    @SerializedName("orderId")
    String orderId;

    @SerializedName("accessToken")
    String accessToken;

    public GetOrderItemsRequest(String orderId, String accessToken) {
        this.orderId = orderId;
        this.accessToken = accessToken;
    }
}
