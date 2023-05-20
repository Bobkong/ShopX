package com.squareup.shopx.model;

import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;

public class MerchantCart {

    private AllMerchantsResponse.ShopXMerchant merchant;
    private HashMap<GetMerchantDetailResponse.Item, Integer> cartItems = new HashMap<>();

    public AllMerchantsResponse.ShopXMerchant getMerchant() {
        return merchant;
    }

    public void setMerchant(AllMerchantsResponse.ShopXMerchant merchant) {
        this.merchant = merchant;
    }

    public HashMap<GetMerchantDetailResponse.Item, Integer> getCartItems() {
        return cartItems;
    }

    public MerchantCart(AllMerchantsResponse.ShopXMerchant merchant) {
        this.merchant = merchant;
    }

    public void updateItemCount(GetMerchantDetailResponse.Item item, int count) {
        if (!cartItems.containsKey(item)) {
            // no item in the cart, add
            cartItems.put(item, count);
        } else {
            // have the item in the cart, update
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cartItems.replace(item, count);
            }
        }

    }

    public float getPrice() {
        float price = 0F;
        for (GetMerchantDetailResponse.Item item : cartItems.keySet()) {
            price += item.getItemDiscountPrice(merchant) * cartItems.get(item);
        }
        return price;
    }

    public int getCountOfAnItem(GetMerchantDetailResponse.Item item) {
        for (GetMerchantDetailResponse.Item i : cartItems.keySet()) {
            if (item.equals(i)) {
                return cartItems.get(item);
            }
        }
        return 0;
    }

    public int getTotalItemCount() {
        int count = 0;
        for (GetMerchantDetailResponse.Item item : cartItems.keySet()) {
            count += cartItems.get(item);
        }
        return count;
    }
}
