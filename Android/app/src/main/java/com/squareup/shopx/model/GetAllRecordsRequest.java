package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetAllRecordsRequest implements Serializable {
    @SerializedName("contact")
    String contact;

    public GetAllRecordsRequest(String contact) {
        this.contact = contact;
    }
}
