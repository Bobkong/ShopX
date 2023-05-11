package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CreateLoyaltyRewardRequest implements Serializable {
    @SerializedName("idempotency_key")
    String idempotencyKey;

    @SerializedName("reward")
    Reward reward;

    public CreateLoyaltyRewardRequest(String idempotencyKey, Reward reward) {
        this.idempotencyKey = idempotencyKey;
        this.reward = reward;
    }

    public static class Reward implements Serializable {
        @SerializedName("loyalty_account_id")
        String loyaltyAccountId;

        @SerializedName("reward_tier_id")
        String rewardTierId;

        @SerializedName("order_id")
        String orderId;

        public Reward(String loyaltyAccountId, String rewardTierId, String orderId) {
            this.loyaltyAccountId = loyaltyAccountId;
            this.rewardTierId = rewardTierId;
            this.orderId = orderId;
        }
    }
}
