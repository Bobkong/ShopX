package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CreateOrderRequest implements Serializable {
    @SerializedName("idempotency_key")
    String idempotencyKey;

    @SerializedName("order")
    Order order;

    public CreateOrderRequest(String idempotencyKey, Order order) {
        this.idempotencyKey = idempotencyKey;
        this.order = order;
    }

    public static class Order implements Serializable {
        @SerializedName("location_id")
        String locationId;

        @SerializedName("line_items")
        ArrayList<LineItem> lineItems;

        @SerializedName("pricing_options")
        PricingOptions pricingOptions;

        public Order(String locationId, ArrayList<LineItem> lineItems, PricingOptions pricingOptions) {
            this.locationId = locationId;
            this.lineItems = lineItems;
            this.pricingOptions = pricingOptions;
        }
    }

    public static class LineItem implements Serializable {
        @SerializedName("base_price_money")
        BasePriceMoney basePriceMoney;

        @SerializedName("quantity")
        String quantity;

        @SerializedName("catalog_object_id")
        String catalogObjectId;

        public LineItem(BasePriceMoney basePriceMoney, String quantity, String name, String catalogObjectId) {
            this.basePriceMoney = basePriceMoney;
            this.quantity = quantity;
            this.catalogObjectId = catalogObjectId;
        }
    }

    public static class PricingOptions implements Serializable {
        @SerializedName("auto_apply_discounts")
        boolean autoApplyDiscounts;
    }

    public static class BasePriceMoney implements Serializable {
        @SerializedName("amount")
        int amount;

        @SerializedName("currency")
        String currency;

        public BasePriceMoney(int amount) {
            this.amount = amount;
            this.currency = "USD";
        }
    }
}
