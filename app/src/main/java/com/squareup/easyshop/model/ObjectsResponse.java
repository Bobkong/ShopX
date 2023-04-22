package com.squareup.easyshop.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjectsResponse implements Serializable {
    @SerializedName("objects")
    ArrayList<Object> objects;

    @SerializedName("errors")
    ArrayList<Error> errs;

    public static class Object implements Serializable {
        @SerializedName("item_data")
        ItemData itemData;

        @SerializedName("image_data")
        ImageData imageData;

        public ItemData getItemData() {
            return itemData;
        }

        public ImageData getImageData() {
            return imageData;
        }

        public void setImageData(ImageData imageData) {
            this.imageData = imageData;
        }

        public void setItemData(ItemData itemData) {
            this.itemData = itemData;
        }
    }

    public static class ImageData implements Serializable {
        @SerializedName("url")
        String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class ItemData implements Serializable {
        @SerializedName("name")
        String name;

        @SerializedName("description")
        String description;

        @SerializedName("variations")
        ArrayList<Variation> variations;

        @SerializedName("image_ids")
        ArrayList<String> imageIds;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ArrayList<Variation> getVariations() {
            return variations;
        }

        public void setVariations(ArrayList<Variation> variations) {
            this.variations = variations;
        }

        public ArrayList<String> getImageIds() {
            return imageIds;
        }

        public void setImageIds(ArrayList<String> imageIds) {
            this.imageIds = imageIds;
        }
    }

    public static class Variation implements Serializable {
        @SerializedName("item_variation_data")
        ItemVariationData itemVariationData;

        public ItemVariationData getItemVariationData() {
            return itemVariationData;
        }

        public void setItemVariationData(ItemVariationData itemVariationData) {
            this.itemVariationData = itemVariationData;
        }
    }

    public static class ItemVariationData implements Serializable {
        @SerializedName("price_money")
        PriceMoney priceMoney;

        public PriceMoney getPriceMoney() {
            return priceMoney;
        }

        public void setPriceMoney(PriceMoney priceMoney) {
            this.priceMoney = priceMoney;
        }
    }

    public static class PriceMoney implements Serializable {
        @SerializedName("amount")
        int amount;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object> objects) {
        this.objects = objects;
    }

    public ArrayList<Error> getErrs() {
        return errs;
    }

    public void setErrs(ArrayList<Error> errs) {
        this.errs = errs;
    }
}
