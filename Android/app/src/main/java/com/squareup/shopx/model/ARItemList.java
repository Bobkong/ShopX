package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ARItemList implements Serializable {
    @SerializedName("arItems")
    public ArrayList<GetMerchantDetailResponse.Item> arItems = new ArrayList<>();
}
