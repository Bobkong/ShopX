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
    Long timeStamp;

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

    @SerializedName("timeString")
    String timeString;

    public PlaceOrderRequest(String phoneNumber, String accessToken, String orderId, Long timeStamp, int finalPrice, int originalPrice, int discountValue, int loyaltyValue, int itemCount, String timeString) {
        this.phoneNumber = phoneNumber;
        this.accessToken = accessToken;
        this.orderId = orderId;
        this.timeStamp = timeStamp;
        this.finalPrice = finalPrice;
        this.originalPrice = originalPrice;
        this.discountValue = discountValue;
        this.loyaltyValue = loyaltyValue;
        this.itemCount = itemCount;
        this.timeString = timeString;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(int finalPrice) {
        this.finalPrice = finalPrice;
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(int discountValue) {
        this.discountValue = discountValue;
    }

    public int getLoyaltyValue() {
        return loyaltyValue;
    }

    public void setLoyaltyValue(int loyaltyValue) {
        this.loyaltyValue = loyaltyValue;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }
}
