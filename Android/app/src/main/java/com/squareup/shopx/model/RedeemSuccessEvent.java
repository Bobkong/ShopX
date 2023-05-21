package com.squareup.shopx.model;

public class RedeemSuccessEvent {
    public String orderId;

    public RedeemSuccessEvent(String orderId) {
        this.orderId = orderId;
    }
}
