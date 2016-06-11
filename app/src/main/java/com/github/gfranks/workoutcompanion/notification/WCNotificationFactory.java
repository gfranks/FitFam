package com.github.gfranks.workoutcompanion.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.github.gfranks.workoutcompanion.R;
import com.github.gfranks.workoutcompanion.activity.UserProfileActivity;
import com.github.gfranks.workoutcompanion.activity.WorkoutCompanionActivity;
import com.github.gfranks.workoutcompanion.application.WorkoutCompanionApplication;
import com.github.gfranks.workoutcompanion.data.model.WCUser;
import com.github.gfranks.workoutcompanion.manager.AccountManager;
import com.github.gfranks.workoutcompanion.util.Utils;
import com.urbanairship.push.PushMessage;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import org.joda.time.DateTime;

import java.util.Locale;

import javax.inject.Inject;

public class WCNotificationFactory extends DefaultNotificationFactory {

    @Inject
    AccountManager mAccountManager;

    public WCNotificationFactory(Context context) {
        super(context);
        WorkoutCompanionApplication.get(context).inject(this);
    }

    @Nullable
    @Override
    public Notification createNotification(@NonNull PushMessage message, int notificationId) {
        if (!mAccountManager.isLoggedIn()) {
            return null;
        }

        if (message.getPushBundle().containsKey(WCUser.EXTRA)) {
            String userString = message.getPushBundle().getString(WCUser.EXTRA);
            try {
                WCUser user = Utils.getGson().fromJson(userString, WCUser.class);
                return getUserNotification(user);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return super.createNotification(message, notificationId);
    }

    public Notification getUserNotification(WCUser user) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra(WCUser.EXTRA, user);
        PendingIntent contentIntent = createPendingIntent(WorkoutCompanionActivity.class, intent, 0);

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

    public void registerUserNotification(WCUser user) {
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
        Intent notificationIntent = new Intent(getContext(), WCNotificationPublisher.class);
        notificationIntent.putExtra(WCNotificationPublisher.NOTIFICATION_ID, (int) dateTime.getMillis());
        notificationIntent.putExtra(WCNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                (int) dateTime.getMillis(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime.toCalendar(Locale.getDefault()).getTimeInMillis(), pendingIntent);
    }
}
