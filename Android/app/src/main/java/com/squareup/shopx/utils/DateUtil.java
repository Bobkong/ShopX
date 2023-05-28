package com.squareup.shopx.utils;

import java.util.Calendar;

public class DateUtil {
    public static String getOrderTime() {
        //获取当前时间
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        return translateMonth(month) + " " + date;
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
