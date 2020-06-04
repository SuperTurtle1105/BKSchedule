package com.baggarm.bkschedule.model;

/**
 * The activity for getting time and break time for LessonTime in function TKB.
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public class LessonTime {
    public String time;
    public String breakTime;

    public LessonTime(String time, String breakTime) {
        this.time = time;
        this.breakTime = breakTime;
    }
}
