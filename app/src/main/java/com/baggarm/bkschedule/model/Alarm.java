package com.baggarm.bkschedule.model;

import com.baggarm.bkschedule.database.TodaySchedule;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * The activity for setting Alarm
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public class Alarm implements Serializable {
    /**
     * There are some attributes for setting alarm.
     * <ol>
     * <li>id: unique id for one alarm.</li>
     * <li>title: the value of this attribute is taken from class TodaySchedule's subject name.</li>
     * <li>detail, ticker: for display.</li>
     * </ol>
     *
     * @since 1.0
     */
    public int id;
    public String title;
    public String detail;
    public String ticker;

    /**
     * This method is to initial a object of class Alarm.
     *
     * @since 1.0
     */
    public Alarm() {
        id = 0;
        title = "";
        detail = "";
        ticker = "";
    }

    /**
     * This method is to initial a object of class Alarm.
     *
     * @since 1.0
     */
    public Alarm(int id, String title, String detail, String ticker) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.ticker = ticker;
    }

    /**
     * This method takes data from class TodaySchedule.
     * The attributes of this class receive data from class TodaySchedule.
     *
     * @param todaySchedule
     * @see TodaySchedule
     * #getTimeString(startTime, endTime)
     * @since 1.0
     */
    public Alarm(TodaySchedule todaySchedule) {

        String strTime = getTimeString(todaySchedule.startTime, todaySchedule.endTime);

        this.id = todaySchedule.getId();
        this.title = todaySchedule.getName();
        this.detail = "Thời gian: " + strTime;
        this.ticker = "Thời gian: " + strTime;

        String location = todaySchedule.getLocation();
        if (!location.isEmpty()) {
            this.detail += "\nĐịa điểm: " + location;
            this.ticker += ". Địa điểm: " + location;
        }

        String note = todaySchedule.getDetail();
        if (!note.isEmpty()) {
            this.detail += "\nGhi chú: " + note;
            this.ticker += ". Ghi chú: " + note + ".";
        }
    }

    /**
     * This method returns a string with the format: hour minute begins -> hour minute ends
     * This method is used for 2 attributes: detail, ticker
     *
     * @param start the time which begins an activity
     * @param end   the time which ends an activity
     * @see Calendar
     * @see GregorianCalendar
     * #setTimeInMillis(start)
     * #getStrTime(hour, minute)
     * @since 1.0
     */
    private String getTimeString(long start, long end) {
        String result = "";

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(start);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        result += getStrTime(hour, minute);
        result += " -> ";

        calendar.setTimeInMillis(end);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        result += getStrTime(hour, minute);

        return result;
    }

    /**
     * This method is to return a string with the format: 2 characters for hour in front and 2 characters for minute behind
     *
     * @since 1.0
     */
    private String getStrTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }
}
