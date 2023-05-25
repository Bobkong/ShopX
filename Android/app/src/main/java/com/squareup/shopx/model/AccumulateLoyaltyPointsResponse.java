package com.squareup.shopx.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AccumulateLoyaltyPointsResponse implements Serializable {
    @SerializedName("events")
    ArrayList<Event> events;

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public static class Event implements Serializable {
        @SerializedName("accumulate_points")
        AccumulatePoints accumulatePoints;

        public AccumulatePoints getAccumulatePoints() {
            return accumulatePoints;
        }

        public void setAccumulatePoints(AccumulatePoints accumulatePoints) {
            this.accumulatePoints = accumulatePoints;
        }

        public static class AccumulatePoints implements Serializable {
            @SerializedName("points")
            Integer points;

            public Integer getPoints() {
                return points;
            }

            public void setPoints(Integer points) {
                this.points = points;
            }
        }
    }
}
