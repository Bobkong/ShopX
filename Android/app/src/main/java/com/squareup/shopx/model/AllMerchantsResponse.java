package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class AllMerchantsResponse implements Serializable {
    @SerializedName("code")
    int code;

    @SerializedName("msg")
    String msg;

    @SerializedName("merchants")
    ArrayList<ShopXMerchant> merchants;

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

    public ArrayList<ShopXMerchant> getMerchants() {
        return merchants;
    }

    public void setMerchants(ArrayList<ShopXMerchant> merchants) {
        this.merchants = merchants;
    }

    public static class ShopXMerchant implements Serializable {
        @SerializedName("merchantId")
        String merchantId;

        @SerializedName("accessToken")
        String accessToken;

        @SerializedName("logoUrl")
        String logoUrl;

        @SerializedName("addressLine1")
        String addressLine1;

        @SerializedName("locality")
        String locality;

        @SerializedName("administrativeDistrictLevel1")
        String administrativeDistrictLevel1;

        @SerializedName("postalCode")
        String postalCode;

        @SerializedName("lat")
        float lat;

        @SerializedName("lng")
        float lng;

        @SerializedName("ifLoyalty")
        int ifLoyalty;

        @SerializedName("discountProducts")
        String discountProducts;

        @SerializedName("discountType")
        String discountType;

        @SerializedName("discountAmount")
        float discountAmount;

        @SerializedName("businessName")
        String businessName;

        @SerializedName("arEnable")
        int arEnable;

        @SerializedName("locationId")
        String locationId;

        public String getMerchantId() {
            return merchantId;
        }

        public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getAdministrativeDistrictLevel1() {
            return administrativeDistrictLevel1;
        }

        public void setAdministrativeDistrictLevel1(String administrativeDistrictLevel1) {
            this.administrativeDistrictLevel1 = administrativeDistrictLevel1;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }

        public float getLng() {
            return lng;
        }

        public void setLng(float lng) {
            this.lng = lng;
        }

        public int getIfLoyalty() {
            return ifLoyalty;
        }

        public void setIfLoyalty(int ifLoyalty) {
            this.ifLoyalty = ifLoyalty;
        }

        public String getDiscountProducts() {
            return discountProducts;
        }

        public void setDiscountProducts(String discountProducts) {
            this.discountProducts = discountProducts;
        }

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public float getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(float discountAmount) {
            this.discountAmount = discountAmount;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public int getArEnable() {
            return arEnable;
        }

        public void setArEnable(int arEnable) {
            this.arEnable = arEnable;
        }

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ShopXMerchant that = (ShopXMerchant) o;
            return accessToken.equals(that.accessToken);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accessToken);
        }
    }
}
