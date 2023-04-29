package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VerifyPhoneRequest implements Serializable {
    @SerializedName("contact")
    String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public VerifyPhoneRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
