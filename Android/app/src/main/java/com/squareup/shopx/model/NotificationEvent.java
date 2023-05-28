package com.squareup.shopx.model;

public class NotificationEvent {
    public AllMerchantsResponse.ShopXMerchant merchant;

    public NotificationEvent(AllMerchantsResponse.ShopXMerchant merchant) {
        this.merchant = merchant;
    }
}
