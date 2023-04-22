package com.squareup.easyshop.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationResponse implements Serializable {
    @SerializedName("location")
    Location location;

    @SerializedName("errors")
    ArrayList<Error> errs;

    public static class Location implements Serializable {
        @SerializedName("address")
        Address address;

        @SerializedName("logo_url")
        String logoUrl;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getLogoUrl() {
            return logoUrl;
        }

        public void setLogoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
        }
    }

    public static class Address implements Serializable {
        @SerializedName("address_line_1")
        String addressLine1;

        @SerializedName("locality")
        String locality;

        @SerializedName("administrative_district_level_1")
        String administrativeDistrictLevel1;

        @SerializedName("postal_code")
        String postalCode;

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getLocality() {
            return locality;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public String getAdministrativeDistrictLevel1() {
            return administrativeDistrictLevel1;
        }

        public void setAdministrativeDistrictLevel1(String administrativeDistrictLevel1) {
            this.administrativeDistrictLevel1 = administrativeDistrictLevel1;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<Error> getErrs() {
        return errs;
    }

    public void setErrs(ArrayList<Error> errs) {
        this.errs = errs;
    }
}
