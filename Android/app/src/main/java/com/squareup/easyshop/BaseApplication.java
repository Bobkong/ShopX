package com.squareup.easyshop;

import android.app.Application;
import android.content.Context;

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

    }


}
