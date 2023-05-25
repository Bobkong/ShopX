package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AccumulateLoyaltyPointsRequest implements Serializable {
    @SerializedName("idempotency_key")
    String idempotencyKey;

    @SerializedName("location_id")
    String locationId;

    @SerializedName("accumulate_points")
    AccumulatePoints accumulatePoints;

    public AccumulateLoyaltyPointsRequest(String idempotencyKey, String locationId, AccumulatePoints accumulatePoints) {
        this.idempotencyKey = idempotencyKey;
        this.locationId = locationId;
        this.accumulatePoints = accumulatePoints;
    }

    public static class AccumulatePoints implements Serializable {
        @SerializedName("order_id")
        String orderId;

        @SerializedName("points")
        Integer points;

        public AccumulatePoints(String orderId) {
            this.orderId = orderId;
        }

        public AccumulatePoints(Integer points) {
            this.points = points;
        }
    }
}
