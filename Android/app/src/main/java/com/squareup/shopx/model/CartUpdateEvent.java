package com.squareup.shopx.model;

public class CartUpdateEvent {
    public AllMerchantsResponse.ShopXMerchant merchant;

    public CartUpdateEvent(AllMerchantsResponse.ShopXMerchant merchant) {
        this.merchant = merchant;
    }
}
