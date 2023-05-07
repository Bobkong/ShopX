package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EnrollLoyaltyRequest implements Serializable {
    @SerializedName("contact")
    String contact;

    @SerializedName("access_token")
    String accessToken;

    @SerializedName("program_id")
    String programId;


    public EnrollLoyaltyRequest(String contact, String accessToken, String programId) {
        this.contact = contact;
        this.accessToken = accessToken;
        this.programId = programId;
    }
}
