package com.squareup.oauthexample;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Merchant implements Serializable {

    @SerializedName("merchantId")
    String merchantId;

    @SerializedName("accessToken")
    String accessToken;

    @SerializedName("businessName")
    String businessName;

    @SerializedName("logo")
    String logo;

    @SerializedName("address")
    Address address;

    @SerializedName("lat")
    Float lat;

    @SerializedName("lng")
    Float lng;

    @SerializedName("ifLoyalty")
    Boolean ifLoyalty;

    @SerializedName("discountProducts")
    String discountProducts;

    @SerializedName("discount")
    Discount discount;

    @SerializedName("locationId")
    String locationId;


    public Merchant(String merchantId, String accessToken, String businessName, String logo, Address location, Float lat, Float lng, String locationId) {
        this.merchantId = merchantId;
        this.accessToken = accessToken;
        this.businessName = businessName;
        if (logo == null) {
            this.logo = "";
        } else {
            this.logo = logo;
        }
        this.address = location;
        this.lat = lat;
        this.lng = lng;
        this.locationId = locationId;
    }

    public static class Address implements Serializable {
        @SerializedName("address_line_1")
        String addressLine1;

        @SerializedName("locality")
        String locality;

        @SerializedName("administrative_district_level_1")
        String administrativeDistrictLevel1;

        @SerializedName("postal_code")
        String postalCode;

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

        public Address(String addressLine1, String locality, String administrativeDistrictLevel1, String postalCode) {
            this.addressLine1 = addressLine1;
            this.locality = locality;
            this.administrativeDistrictLevel1 = administrativeDistrictLevel1;
            this.postalCode = postalCode;
        }
    }

    public static class Discount implements Serializable {
        @SerializedName("discountType")
        String discountType;

        @SerializedName("amount")
        float amount;

        @SerializedName("discountName")
        String discountName;

        public Discount(String discountType, float amount, String discountName) {
            this.discountType = discountType;
            this.amount = amount;
            this.discountName = discountName;
        }
    }

    public Boolean getIfLoyalty() {
        return ifLoyalty;
    }

    public void setIfLoyalty(Boolean ifLoyalty) {
        this.ifLoyalty = ifLoyalty;
    }

    public String getDiscountProducts() {
        return discountProducts;
    }

    public void setDiscountProducts(String discountProducts) {
        this.discountProducts = discountProducts;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }
}
