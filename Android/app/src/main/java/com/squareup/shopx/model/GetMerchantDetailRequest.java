package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetMerchantDetailRequest implements Serializable {
    @SerializedName("access_token")
    String accessToken;

    @SerializedName("contact")
    String contact;

    public GetMerchantDetailRequest(String accessToken, String contact) {
        this.accessToken = accessToken;
        this.contact = contact;
    }
}
