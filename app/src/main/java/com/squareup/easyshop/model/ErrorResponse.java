package com.squareup.easyshop.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ErrorResponse implements Serializable {
    @SerializedName("errors")
    ArrayList<Error> errors;

    public ArrayList<Error> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Error> errors) {
        this.errors = errors;
    }
}