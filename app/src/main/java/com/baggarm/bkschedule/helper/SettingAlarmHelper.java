package com.baggarm.bkschedule.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.baggarm.bkschedule.database.TodaySchedule;
import com.baggarm.bkschedule.model.Alarm;
import com.baggarm.bkschedule.receiver.AlarmReceiver;
import com.google.gson.Gson;

import static android.content.Context.ALARM_SERVICE;

/**
 * This class was created in order to set task alarm and remove alarm in a simplest way
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class SettingAlarmHelper {

    private static SettingAlarmHelper ourInstance;
    private final String TAG = SettingAlarmHelper.class.getName();

    private SettingAlarmHelper() {
    }

    /**
     * Get singleton instance
     *
     * @return
     * @since 1.0
     */
    public static SettingAlarmHelper getInstance() {
        if (ourInstance == null)
            ourInstance = new SettingAlarmHelper();

        return ourInstance;
    }

    /**
     * Set alert of a task
     *
     * @param todaySchedule
     * @param context
     * @since 1.0
     */
    public void setAlert(TodaySchedule todaySchedule, Context context) {
        Alarm alarm = new Alarm(todaySchedule);
        String json = new Gson().toJson(alarm);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(json);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, todaySchedule.getId(), alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, todaySchedule.getLongAlertTime(), pendingIntent);
    }

    /**
     * Cancel alert of a task
     *
     * @param todaySchedule
     * @param context
     * @since 1.0
     */
    public void cancelAlert(TodaySchedule todaySchedule, Context context) {

        Alarm alarm = new Alarm(todaySchedule);
        String json = new Gson().toJson(alarm);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(json);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, todaySchedule.getId(), alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
