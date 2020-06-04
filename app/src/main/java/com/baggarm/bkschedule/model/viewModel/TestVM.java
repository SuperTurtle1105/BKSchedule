package com.baggarm.bkschedule.model.viewModel;


import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * This model is used to store properties of Test: Name, Day, Room and Time.
 *
 * @author DN
 * @version 2.19.1406
 * @since 1.0
 */

public class TestVM implements Comparable<TestVM> {

    private String subjectTestName;
    private String subjectTestDay;
    private String subjectTestRoom;
    private String subjectTestTime;

    public TestVM() {
    }

    /**
     * A constructor to construct value for a new class
     *
     * @since 1.0
     */
    public TestVM(String subjectTestName, String subjectTestDay, String subjectTestRoom, String subjectTestTime) {
        this.subjectTestName = subjectTestName;
        this.subjectTestDay = subjectTestDay;
        this.subjectTestRoom = subjectTestRoom;
        this.subjectTestTime = subjectTestTime;
    }

    public String getSubjectTestName() {
        return subjectTestName;
    }

    public void setSubjectTestName(String subjectTestName) {
        this.subjectTestName = subjectTestName;
    }

    public String getSubjectTestDay() {
        return subjectTestDay;
    }

    public void setSubjectTestDay(String subjectTestDay) {
        this.subjectTestDay = subjectTestDay;
    }

    public String getSubjectTestRoom() {
        return subjectTestRoom;
    }

    public void setSubjectTestRoom(String subjectRoom) {
        this.subjectTestRoom = subjectRoom;
    }

    public String getSubjectTestTime() {
        return subjectTestTime;
    }

    public void setSubjectTestTime(String subjectTestTime) {
        this.subjectTestTime = subjectTestTime;
    }

    @Override
    public int compareTo(@NonNull TestVM testVM) {
        String[] times = subjectTestDay.split("/");
        //return smaller
        if (times.length < 2)
            return -1;
        String[] times1 = testVM.subjectTestDay.split("/");

        //return bigger
        if (times1.length < 2)
            return 1;

        if (times[1].compareTo(times1[1]) < 0)
            return -1;
        if (times[1].compareTo(times1[1]) > 0)
            return 1;

        return times[0].compareTo(times1[0]);
    }
}
