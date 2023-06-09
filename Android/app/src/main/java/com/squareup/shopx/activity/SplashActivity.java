package com.squareup.shopx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.codelabs.arlocalizer.helpers.GeoPermissionsHelper;
import com.squareup.shopx.utils.PreferenceUtils;
import com.squareup.shopx.utils.Transparent;


public class SplashActivity extends AppCompatActivity {

    private static String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Transparent.transparentNavBar(this);
        Transparent.transparentStatusBar(this, true);

        new Handler().postDelayed(() -> {
            if (PreferenceUtils.getFirstUse()) {
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            } else if (PreferenceUtils.getUserPhone().length() == 0){
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
            finish();
        }, 300);
    }
}
