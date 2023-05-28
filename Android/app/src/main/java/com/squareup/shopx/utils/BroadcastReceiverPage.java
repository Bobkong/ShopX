package com.squareup.shopx.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.squareup.shopx.R;
import com.squareup.shopx.activity.MainFragment;
import com.squareup.shopx.activity.MerchantDetailActivity;
import com.squareup.shopx.model.AllMerchantsResponse;

public class BroadcastReceiverPage extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        AllMerchantsResponse.ShopXMerchant merchant = (AllMerchantsResponse.ShopXMerchant) intent.getSerializableExtra("merchant");

        Intent i = new Intent(context, MerchantDetailActivity.class);
        i.putExtra("merchant", merchant);
        PendingIntent p = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, "Notify")
                .setSmallIcon(R.drawable.marker_normal)
                .setContentTitle("ShopX Offers!")
                .setContentText("Check out " + merchant.getBusinessName() + " to get your exclusive offers!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(p);

        NotificationManagerCompat n = NotificationManagerCompat.from(context);
        n.notify(200, b.build());
    }
}
