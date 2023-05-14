package com.squareup.shopx;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.shopx.utils.PreferenceUtils;

public class BaseApplication extends Application {

    private static Context context;

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
