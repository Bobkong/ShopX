package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateLoyaltyRewardResponse implements Serializable {
    @SerializedName("reward")
    Reward reward;

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public static class Reward implements Serializable {
        @SerializedName("order_id")
        String orderId;

        @SerializedName("reward_tier_id")
        String rewardTierId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getRewardTierId() {
            return rewardTierId;
        }

        public void setRewardTierId(String rewardTierId) {
            this.rewardTierId = rewardTierId;
        }
    }
}
