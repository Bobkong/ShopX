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

public class BroadcastReceiverPage extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainFragment.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent p = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, "Notify")
                .setSmallIcon(R.drawable.marker_normal)
                .setContentTitle("ShopX Title")
                .setContentText("ShopX Description")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(p);

        NotificationManagerCompat n = NotificationManagerCompat.from(context);
        n.notify(200, b.build());
    }
}
