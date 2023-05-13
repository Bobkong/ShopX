package com.squareup.shopx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.shopx.utils.PreferenceUtils;


public class SplashActivity extends AppCompatActivity {

    private static String TAG = "SplashActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            if (PreferenceUtils.getFirstUse()) {
                startActivity(new Intent(SplashActivity.this, GuideActivity.class));
            } if (PreferenceUtils.getUserPhone().length() == 0){
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }else {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }
            finish();
        }, 300);
    }
}
