package com.squareup.shopx.model;

import java.util.ArrayList;

public class MerchantCart {

    private AllMerchantsResponse.ShopXMerchant merchant;
    private ArrayList<GetMerchantDetailResponse.Item> cartItems = new ArrayList<>();

    public AllMerchantsResponse.ShopXMerchant getMerchant() {
        return merchant;
    }

    public void setMerchant(AllMerchantsResponse.ShopXMerchant merchant) {
        this.merchant = merchant;
    }

    public ArrayList<GetMerchantDetailResponse.Item> getCartItems() {
        return cartItems;
    }

    public void setCartItems(ArrayList<GetMerchantDetailResponse.Item> cartItems) {
        this.cartItems = cartItems;
    }

    public MerchantCart(AllMerchantsResponse.ShopXMerchant merchant) {
        this.merchant = merchant;
    }

    public void addToCart(GetMerchantDetailResponse.Item item) {
        if (cartItems.contains(item)) {
            return;
        }
        cartItems.add(item);
    }

    public void deleteFromCart(GetMerchantDetailResponse.Item item) {
        if (!cartItems.contains(item)) {
            return;
        }
        cartItems.remove(item);
    }

    public float getPrice() {
        float price = 0F;
        for (GetMerchantDetailResponse.Item item : cartItems) {
            price += item.getItemDiscountPrice(merchant);
        }
        return price;
    }
}
