package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Customer implements Serializable {
    @SerializedName("nickname")
    String nickName;

    @SerializedName("phone_number")
    String phoneNumber;

    @SerializedName("password")
    String password;

    @SerializedName("idempotency_key")
    String idempotencyKey;

    @SerializedName("id")
    String id;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer(String nickName, String phoneNumber, String password, String idempotencyKey) {
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.idempotencyKey = idempotencyKey;
    }
}
