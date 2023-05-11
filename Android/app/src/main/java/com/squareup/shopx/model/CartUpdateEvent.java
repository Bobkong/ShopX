package com.squareup.shopx.model;

public class CartUpdateEvent {
    public String accessToken;

    public CartUpdateEvent(String accessToken) {
        this.accessToken = accessToken;
    }
}
