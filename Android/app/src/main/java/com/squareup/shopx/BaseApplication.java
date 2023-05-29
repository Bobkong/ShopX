package com.squareup.shopx;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.shopx.consts.Configs;
import com.squareup.shopx.netservice.GooglePay.ChargeCall;
import com.squareup.shopx.netservice.GooglePay.GooglePayChargeClient;
import com.squareup.shopx.utils.PreferenceUtils;

import retrofit2.Retrofit;

public class BaseApplication extends Application {

    public static Context context;

    public static Context getContext(){
        return context;
    }

    /**
     * Initializes Sendbird UIKit
     */
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        PreferenceUtils.init(context);



    }

}
