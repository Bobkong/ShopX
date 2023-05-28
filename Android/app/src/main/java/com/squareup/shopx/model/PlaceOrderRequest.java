package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaceOrderRequest implements Serializable {
    @SerializedName("contact")
    String phoneNumber;

    @SerializedName("accessToken")
    String accessToken;

    @SerializedName("orderId")
    String orderId;

    @SerializedName("timeStamp")
    String timeStamp;

    @SerializedName("finalPrice")
    int finalPrice;

    @SerializedName("originalPrice")
    int originalPrice;

    @SerializedName("discountValue")
    int discountValue;

    @SerializedName("loyaltyValue")
    int loyaltyValue;

    @SerializedName("itemCount")
    int itemCount;

    public PlaceOrderRequest(String phoneNumber, String accessToken, String orderId, String timeStamp, int finalPrice, int originalPrice, int discountValue, int loyaltyValue, int itemCount) {
        this.phoneNumber = phoneNumber;
        this.accessToken = accessToken;
        this.orderId = orderId;
        this.timeStamp = timeStamp;
        this.finalPrice = finalPrice;
        this.originalPrice = originalPrice;
        this.discountValue = discountValue;
        this.loyaltyValue = loyaltyValue;
        this.itemCount = itemCount;
    }
}
