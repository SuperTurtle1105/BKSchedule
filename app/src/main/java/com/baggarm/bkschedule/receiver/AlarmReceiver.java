package com.baggarm.bkschedule.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.controller.starting.LauncherActivity;
import com.baggarm.bkschedule.model.Alarm;
import com.google.gson.Gson;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;


/**
 * This class was created to push notification
 *
 * @author IMBAGGAARM
 * @since 1.0
 * @version 2019.1706
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "com.example.bkschedule.channelId";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Alarm alarm = new Alarm();
        if (action != null && !action.isEmpty()) {
            alarm = new Gson().fromJson(action, Alarm.class);
        }

        Intent notificationIntent;
        notificationIntent = new Intent(context, LauncherActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LauncherActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);

        Notification notification = builder
                .setContentTitle(alarm.title.toUpperCase())
                .setTicker(alarm.ticker)
                .setContentText(alarm.ticker)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new Notification.BigTextStyle().bigText(alarm.detail))
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "BKSchedule",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(Integer.valueOf(alarm.id), notification);

    }
}
