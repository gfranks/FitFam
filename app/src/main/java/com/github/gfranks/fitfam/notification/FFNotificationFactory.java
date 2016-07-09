package com.github.gfranks.fitfam.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.github.gfranks.fitfam.activity.FitFamActivity;
import com.github.gfranks.fitfam.application.FitFamApplication;
import com.github.gfranks.fitfam.data.model.FFUser;
import com.github.gfranks.fitfam.R;
import com.github.gfranks.fitfam.activity.UserProfileActivity;
import com.github.gfranks.fitfam.manager.AccountManager;
import com.github.gfranks.fitfam.util.Utils;
import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import org.joda.time.DateTime;

import java.util.Locale;

import javax.inject.Inject;

public class FFNotificationFactory extends DefaultNotificationFactory {

    @Inject
    AccountManager mAccountManager;

    public FFNotificationFactory(Context context) {
        super(context);
        FitFamApplication.get(context).inject(this);
    }

    @Nullable
    @Override
    public Notification createNotification(@NonNull PushMessage message, int notificationId) {
        if (!mAccountManager.isLoggedIn()) {
            return null;
        }

        if (message.getPushBundle().containsKey(FFUser.EXTRA)) {
            String userString = message.getPushBundle().getString(FFUser.EXTRA);
            try {
                FFUser user = Utils.getGson().fromJson(userString, FFUser.class);
                return getUserNotification(user);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return super.createNotification(message, notificationId);
    }

    public Notification getUserNotification(FFUser user) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra(FFUser.EXTRA, user);
        PendingIntent contentIntent = createPendingIntent(FitFamActivity.class, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setWhen(DateTime.now().getMillis())
                .setSmallIcon(getSmallIconId())
                .setColor(getColor())
                .setTicker(getContext().getString(R.string.notification_companion))
                .setContentTitle(getContext().getString(R.string.notification_companion))
                .setContentText(user.getFullName())
                .setContentIntent(contentIntent);

        return builder.build();
    }

    public void registerUserNotification(FFUser user) {
        scheduleNotification(getUserNotification(user), DateTime.now());
    }

    private PendingIntent createPendingIntent(Class<?> targetClass, Intent intent, int requestCode) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addParentStack(targetClass);
        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(requestCode,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Action createAction(int icon, String title, PendingIntent intent, Bundle extras) {
        return new NotificationCompat.Action.Builder(icon, title, intent)
                .addExtras(extras)
                .build();
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void scheduleNotification(Notification notification, DateTime dateTime) {
        Intent notificationIntent = new Intent(getContext(), FFNotificationPublisher.class);
        notificationIntent.putExtra(FFNotificationPublisher.NOTIFICATION_ID, (int) dateTime.getMillis());
        notificationIntent.putExtra(FFNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                (int) dateTime.getMillis(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime.toCalendar(Locale.getDefault()).getTimeInMillis(), pendingIntent);
    }
}
