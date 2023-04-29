package com.squareup.shopx;

import android.app.Application;
import android.content.Context;

import com.squareup.shopx.Util.PreferenceUtils;

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
