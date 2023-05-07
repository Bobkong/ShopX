package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetAllLoyaltyRecordsRequest implements Serializable {
    @SerializedName("contact")
    String contact;

    public GetAllLoyaltyRecordsRequest(String contact) {
        this.contact = contact;
    }
}
