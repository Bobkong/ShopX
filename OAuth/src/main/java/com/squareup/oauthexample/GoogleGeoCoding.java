package com.squareup.oauthexample;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GoogleGeoCoding implements Serializable {
    @SerializedName("results")
    ArrayList<GoogleAddress> results;

    public ArrayList<GoogleAddress> getResults() {
        return results;
    }

    public void setResults(ArrayList<GoogleAddress> results) {
        this.results = results;
    }

    public static class GoogleAddress implements Serializable{
        @SerializedName("geometry")
        Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
    }

    public static class Geometry implements Serializable{
        @SerializedName("location")
        LatLng location;

        public LatLng getLocation() {
            return location;
        }

        public void setLocation(LatLng location) {
            this.location = location;
        }
    }

    public static class LatLng implements Serializable{
        @SerializedName("lat")
        Float lat;

        @SerializedName("lng")
        Float lng;

        public Float getLat() {
            return lat;
        }

        public void setLat(Float lat) {
            this.lat = lat;
        }

        public Float getLng() {
            return lng;
        }

        public void setLng(Float lng) {
            this.lng = lng;
        }
    }
}
