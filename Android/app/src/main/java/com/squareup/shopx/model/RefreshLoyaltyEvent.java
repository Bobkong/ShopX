package com.squareup.shopx.model;

public class RefreshLoyaltyEvent {
    public AllMerchantsResponse.ShopXMerchant merchant;
    public Integer accumulatePoints;
    public String loyaltyAccount;

    public RefreshLoyaltyEvent(AllMerchantsResponse.ShopXMerchant merchant, Integer accumulatePoints, String loyaltyAccount) {
        this.merchant = merchant;
        this.accumulatePoints = accumulatePoints;
        this.loyaltyAccount = loyaltyAccount;

    }
}
