package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetLoyaltyInfoRequest implements Serializable {
    @SerializedName("contact")
    String contact;

    @SerializedName("access_token")
    String accessToken;

    public GetLoyaltyInfoRequest(String contact, String accessToken) {
        this.contact = contact;
        this.accessToken = accessToken;
    }
}
