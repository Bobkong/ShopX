package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    @SerializedName("contact")
    String phoneNumber;


    public LoginRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
