package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MerchantResponse implements Serializable {
    @SerializedName("merchant")
    Merchant merchant;

    @SerializedName("errors")
    ArrayList<Error> errs;

    public static class Merchant  implements Serializable {
        @SerializedName("id")
        String id;

        @SerializedName("business_name")
        String businessName;

        @SerializedName("main_location_id")
        String mainLocationId;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public String getMainLocationId() {
            return mainLocationId;
        }

        public void setMainLocationId(String mainLocationId) {
            this.mainLocationId = mainLocationId;
        }
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public ArrayList<Error> getErrs() {
        return errs;
    }

    public void setErrs(ArrayList<Error> errs) {
        this.errs = errs;
    }
}
