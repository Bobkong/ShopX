package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class BatchRetrieveRequest implements Serializable {
    @SerializedName("object_ids")
    ArrayList<String> objectIds;

    public BatchRetrieveRequest(ArrayList<String> objectIds) {
        this.objectIds = objectIds;
    }
}
