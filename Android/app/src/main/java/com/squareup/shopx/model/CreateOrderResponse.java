package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CreateOrderResponse implements Serializable {
    @SerializedName("order")
    OrderResponse order;

    public OrderResponse getOrder() {
        return order;
    }

    public void setOrder(OrderResponse order) {
        this.order = order;
    }

    public static class OrderResponse implements Serializable {
        @SerializedName("id")
        String id;

        @SerializedName("total_money")
        OrderAmount totalMoney;

        @SerializedName("total_discount_money")
        OrderAmount discountAmount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public OrderAmount getTotalMoney() {
            return totalMoney;
        }

        public void setTotalMoney(OrderAmount totalMoney) {
            this.totalMoney = totalMoney;
        }

        public OrderAmount getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(OrderAmount discountAmount) {
            this.discountAmount = discountAmount;
        }
    }

    public static class OrderAmount implements Serializable {
        @SerializedName("amount")
        int amount;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }



}
