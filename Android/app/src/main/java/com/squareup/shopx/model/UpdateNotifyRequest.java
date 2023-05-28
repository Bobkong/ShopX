package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateNotifyRequest implements Serializable {
    @SerializedName("contact")
    String phoneNumber;

    @SerializedName("ifNotify")
    int ifNotify;

    public UpdateNotifyRequest(String phoneNumber, int ifNotify) {
        this.phoneNumber = phoneNumber;
        this.ifNotify = ifNotify;
    }
}
