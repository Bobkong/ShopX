package com.squareup.shopx.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.shopx.BaseApplication;


public class NetworkUtil {
    public static boolean isNetworkAvailable(){
        ConnectivityManager manager=(ConnectivityManager) BaseApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager==null)
            return false;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isConnected())
            return false;
        return true;
    }
}
