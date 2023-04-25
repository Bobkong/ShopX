package com.squareup.easyshop.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoyaltyAccount implements Serializable {
    @SerializedName("idempotency_key")
    String idempotencyKey;

    @SerializedName("loyalty_account")
    Account account;

    public static class Account implements Serializable {
        @SerializedName("program_id")
        String programId;

        @SerializedName("customer_id")
        String customerId;

    }
}


