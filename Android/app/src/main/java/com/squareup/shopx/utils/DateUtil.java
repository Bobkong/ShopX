package com.squareup.shopx.utils;

import android.os.Build;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static String getOrderTime() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        return translateMonth(month) + " " + day;
    }

    public static String translateMonth(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return "June";
        }


    }
}
