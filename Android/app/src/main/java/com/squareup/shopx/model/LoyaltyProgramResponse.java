package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LoyaltyProgramResponse implements Serializable {
    @SerializedName("program")
    Program program;

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public static class Program implements Serializable {
        @SerializedName("id")
        String id;

        @SerializedName("reward_tiers")
        ArrayList<RewardTier> rewardTiers;

        @SerializedName("accrual_rules")
        ArrayList<AccrualRule> accrualRules;

        public ArrayList<RewardTier> getRewardTiers() {
            return rewardTiers;
        }

        public void setRewardTiers(ArrayList<RewardTier> rewardTiers) {
            this.rewardTiers = rewardTiers;
        }

        public ArrayList<AccrualRule> getAccrualRules() {
            return accrualRules;
        }

        public void setAccrualRules(ArrayList<AccrualRule> accrualRules) {
            this.accrualRules = accrualRules;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class RewardTier implements Serializable {
        @SerializedName("id")
        String id;

        @SerializedName("points")
        int points;

        @SerializedName("name")
        String name;

        @SerializedName("definition")
        Definition definition;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Definition getDefinition() {
            return definition;
        }

        public void setDefinition(Definition definition) {
            this.definition = definition;
        }
    }

    public static class Definition implements Serializable {
        @SerializedName("scope")
        String scope;

        @SerializedName("discount_type")
        String discountType;

        @SerializedName("percentage_discount")
        String percentageDiscount;

        @SerializedName("max_discount_money")
        MaxDiscountMoney maxDiscountMoney;

        @SerializedName("catalog_object_ids")
        ArrayList<String> catalogObjectIds;

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getDiscountType() {
            return discountType;
        }

        public void setDiscountType(String discountType) {
            this.discountType = discountType;
        }

        public String getPercentageDiscount() {
            return percentageDiscount;
        }

        public void setPercentageDiscount(String percentageDiscount) {
            this.percentageDiscount = percentageDiscount;
        }

        public MaxDiscountMoney getMaxDiscountMoney() {
            return maxDiscountMoney;
        }

        public void setMaxDiscountMoney(MaxDiscountMoney maxDiscountMoney) {
            this.maxDiscountMoney = maxDiscountMoney;
        }

        public ArrayList<String> getCatalogObjectIds() {
            return catalogObjectIds;
        }

        public void setCatalogObjectIds(ArrayList<String> catalogObjectIds) {
            this.catalogObjectIds = catalogObjectIds;
        }
    }

    public static class MaxDiscountMoney implements Serializable {
        @SerializedName("amount")
        int amount;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    // how to earn points
    public static class AccrualRule implements Serializable {
        @SerializedName("accrual_type")
        String AccrualType;

        @SerializedName("points")
        int points;

        @SerializedName("spend_data")
        SpendData spendData;

        @SerializedName("visit_data")
        VisitData visitData;

        public String getAccrualType() {
            return AccrualType;
        }

        public void setAccrualType(String accrualType) {
            AccrualType = accrualType;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public SpendData getSpendData() {
            return spendData;
        }

        public void setSpendData(SpendData spendData) {
            this.spendData = spendData;
        }

        public VisitData getVisitData() {
            return visitData;
        }

        public void setVisitData(VisitData visitData) {
            this.visitData = visitData;
        }
    }

    public static class SpendData implements Serializable {
        @SerializedName("amount_money")
        AmountMoney amountMoney;

        public AmountMoney getAmountMoney() {
            return amountMoney;
        }

        public void setAmountMoney(AmountMoney amountMoney) {
            this.amountMoney = amountMoney;
        }
    }

    public static class AmountMoney implements Serializable {
        @SerializedName("amount")
        int amount;

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    public static class VisitData implements Serializable {
        @SerializedName("minimum_amount_money")
        MinimumAmountMoney minimumAmountMoney;

        public MinimumAmountMoney getMinimumAmountMoney() {
            return minimumAmountMoney;
        }

        public void setMinimumAmountMoney(MinimumAmountMoney minimumAmountMoney) {
            this.minimumAmountMoney = minimumAmountMoney;
        }
    }

    public static class MinimumAmountMoney implements Serializable {
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
