package com.squareup.shopx.model;

import android.view.View;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GetMerchantDetailResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("items")
    ArrayList<Item> items;

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class Item implements Serializable {
        @SerializedName("itemName")
        String itemName;

        @SerializedName("itemDescription")
        String itemDescription;

        @SerializedName("itemId")
        String itemId;

        @SerializedName("itemPrice")
        int itemPrice;

        @SerializedName("itemImage")
        String itemImage;

        @SerializedName("ARLink")
        String ARLink;

        @SerializedName("pricingType")
        String pricingType;

        @SerializedName("itemVariationId")
        String itemVariationId;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemDescription() {
            return itemDescription;
        }

        public void setItemDescription(String itemDescription) {
            this.itemDescription = itemDescription;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public int getItemPrice() {
            return itemPrice;
        }

        public void setItemPrice(int itemPrice) {
            this.itemPrice = itemPrice;
        }

        public String getItemImage() {
            return itemImage;
        }

        public void setItemImage(String itemImage) {
            this.itemImage = itemImage;
        }

        public String getARLink() {
            return ARLink;
        }

        public void setARLink(String ARLink) {
            this.ARLink = ARLink;
        }

        public String getPricingType() {
            return pricingType;
        }

        public void setPricingType(String pricingType) {
            this.pricingType = pricingType;
        }

        public String getItemVariationId() {
            return itemVariationId;
        }

        public void setItemVariationId(String itemVariationId) {
            this.itemVariationId = itemVariationId;
        }

        public float getItemDiscountPrice(AllMerchantsResponse.ShopXMerchant merchantInfo) {
            List<String> discountItems = Arrays.asList(merchantInfo.getDiscountProducts().split("\\|"));

            if (merchantInfo.getDiscountType() == null || merchantInfo.getDiscountType().isEmpty() || !discountItems.contains(getItemId())) {
                return getItemPrice();
            } else {
                if (merchantInfo.getDiscountType().equals("FIXED_PERCENTAGE")) {
                    return getItemPrice() * (100 - merchantInfo.getDiscountAmount()) / 100F;
                } else if (merchantInfo.getDiscountType().equals("FIXED_AMOUNT")) {
                   return getItemPrice() - merchantInfo.getDiscountAmount();
                }
            }
            return 0F;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item item = (Item) o;
            return itemId.equals(item.itemId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemId);
        }
    }

}
