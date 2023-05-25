package com.squareup.shopx.model;

public class RefreshLoyaltyEvent {
    public AllMerchantsResponse.ShopXMerchant merchant;
    public Integer accumulatePoints;

    public RefreshLoyaltyEvent(AllMerchantsResponse.ShopXMerchant merchant, Integer accumulatePoints) {
        this.merchant = merchant;
        this.accumulatePoints = accumulatePoints;
    }
}
