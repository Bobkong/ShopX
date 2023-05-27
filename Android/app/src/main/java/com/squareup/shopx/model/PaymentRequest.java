package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentRequest implements Serializable {
    @SerializedName("idempotency_key")
    String idempotencyKey;

    @SerializedName("source_id")
    String sourceId;

    @SerializedName("amount_money")
    AmountMoney amountMoney;

    @SerializedName("order_id")
    String orderId;

    public PaymentRequest(String idempotencyKey, String sourceId, AmountMoney amountMoney, String orderId) {
        this.idempotencyKey = idempotencyKey;
        this.sourceId = sourceId;
        this.amountMoney = amountMoney;
        this.orderId = orderId;
    }

    public static class AmountMoney implements Serializable {
        @SerializedName("amount")
        int amount;

        @SerializedName("currency")
        String currency;

        public AmountMoney(int amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }
    }


}
