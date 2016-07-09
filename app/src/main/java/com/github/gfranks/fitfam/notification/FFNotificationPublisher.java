package com.github.gfranks.fitfam.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FFNotificationPublisher extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        notificationManager.notify(intent.getIntExtra(NOTIFICATION_ID, 0), notification);
    }
}