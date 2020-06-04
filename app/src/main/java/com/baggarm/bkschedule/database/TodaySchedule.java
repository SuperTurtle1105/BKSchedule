package com.baggarm.bkschedule.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.baggarm.bkschedule.model.Subject;
import com.baggarm.bkschedule.model.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is entity in DB "ScheduleDao", used to save task user created.
 * It contains id (primary key), name task, day, location, detail task,
 * time start, time end, alert time and isAlertOn.
 *
 * @author IMBAGGAARM
 * @version 2109.1406
 * @since 1.0
 */
@Entity(tableName = "schedule")
public class TodaySchedule implements Comparable<TodaySchedule> {

    @ColumnInfo(name = "start_time")
    public long startTime = 0;
    @ColumnInfo(name = "end_time")
    public long endTime = 0;
    @Ignore
    public TodayScheduleState state;
    String name = "";
    String day = "";
    String location = "";
    String detail = "";

    @ColumnInfo(name = "alert_time")
    long alertTime = 0;

    @ColumnInfo(name = "is_alert_on")
    boolean isAlertOn;
    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    public TodaySchedule() {
    }

    public TodaySchedule(Subject subject, boolean isAlertOn, long alertTime, long startTime, long endTime) {
        this.name = subject.ten_mh;
        this.startTime = startTime * 1000;
        this.endTime = endTime * 1000;
        this.isAlertOn = isAlertOn;
        this.alertTime = alertTime * 1000;
        this.detail = "Đi học " + name;
        this.location = subject.phong1;
        this.day = subject.thu1;
    }

    public TodaySchedule(Test test, boolean isEndCourseTest, boolean isAlertOn, long alertTime, long startTime, long endTime) {
        this.name = test.ten_mh;
        this.startTime = startTime * 1000;
        this.endTime = endTime * 1000;
        this.isAlertOn = isAlertOn;
        this.alertTime = alertTime * 1000;
        this.detail = "Đi thi " + name;

        if (isEndCourseTest) {
            this.location = test.phong_thi;
        } else {
            this.location = test.phong_ktra;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getLongStartTime() {
        return startTime;
    }

    public Long getLongEndTime() {
        return endTime;
    }

    public Long getLongAlertTime() {
        return alertTime;
    }

    public String getStartTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date(startTime));
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date(endTime));
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getAlertTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String a = formatter.format(new Date(alertTime));
        return a;
    }

    public void setAlertTime(long alertTime) {
        this.alertTime = alertTime;
    }

    public boolean isAlertOn() {
        return isAlertOn;
    }

    public void setAlertOn(boolean alertOn) {
        isAlertOn = alertOn;
    }

    /**
     * Check 2 schedules are equals if they have same name, startTime, endTime, location
     *
     * @param o
     * @return
     * @since 1.0
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodaySchedule todaySchedule = (TodaySchedule) o;
        boolean result = name.equals(todaySchedule.name) && startTime == todaySchedule.startTime && endTime == todaySchedule.endTime && location.equals(todaySchedule.location);
        return result;
    }

    /**
     * Compare 2 schedules, which one has smaller startTime is less than the other one,
     * and if startTime are equal, endTime will be compared to determine.
     *
     * @param schedule
     * @return
     * @since 1.0
     */
    @Override
    public int compareTo(TodaySchedule schedule) {
        long result = this.startTime - schedule.startTime;
        if (result == 0) {
            long result2 = this.endTime - schedule.endTime;
            return (int) result2;
        }
        return (int) result;
    }

    /**
     * Set state of a schedule by current timestamp
     *
     * @param timestamp current timestamp
     * @since 1.0
     */
    public void setStateByTimestamp(long timestamp) {
        if (timestamp > endTime) {
            state = TodayScheduleState.PAST;
            return;
        }

        if (timestamp < startTime) {
            state = TodayScheduleState.FUTURE;
            return;
        }
        state = TodayScheduleState.ONGOING;
    }

}
