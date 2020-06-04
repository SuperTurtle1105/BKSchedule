package com.baggarm.bkschedule.model.viewModel;

import com.baggarm.bkschedule.controller.today.TodayFragment;
import com.baggarm.bkschedule.database.TodaySchedule;
import com.baggarm.bkschedule.model.TaskState;

import java.io.Serializable;

/**
 * Task Block contains name schedule, location, detail,
 * time start, time end, time alert, variable to set alarm
 * and unique block id
 *
 * @author nguyenBaoHuy
 * @since 1.0
 */
public class TaskVM implements Serializable {
    private String name;
    private String location;
    private String detail;
    private String timeStart;
    private String timeEnd;
    private String timeAlert;
    private boolean isAlert = false;
    private TaskState state;
    private int ID;    // Duy nhat doi voi moi block task

    private long timestampStart;
    private long timestampEnd;
    private long timestampAlert;

    private int schedulesArrayIndex = 0;

    public TaskVM(String headerTitle) {
        name = headerTitle;
    }

    public TaskVM(TodaySchedule schedule, int recycleIndex) {
        this.schedulesArrayIndex = recycleIndex;

        name = schedule.getName();
        this.location = schedule.getLocation();
        detail = schedule.getDetail();

        this.timeStart = schedule.getStartTime();
        this.timeEnd = schedule.getEndTime();
        this.timeAlert = schedule.getAlertTime();
        this.isAlert = schedule.isAlertOn();
        this.ID = schedule.getId();

        switch (schedule.state) {
            case PAST:
                this.state = TaskState.PAST;
                break;
            case ONGOING:
                this.state = TaskState.ONGOING;
                break;
            case FUTURE:
                this.state = TaskState.FUTURE;
                break;
        }

        this.timestampStart = schedule.getLongStartTime();
        this.timestampEnd = schedule.getLongEndTime();
        this.timestampAlert = schedule.getLongAlertTime();
    }

    public void updateID(int ID) {
        this.ID = ID;
    }

    public TodaySchedule getTodaySchedule() {
        TodaySchedule todaySchedule = TodayFragment.todayScheduleArrayList.get(schedulesArrayIndex);
        return todaySchedule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getAlert() {
        return timeAlert;
    }

    public void setAlert(String alert) {
        this.timeAlert = alert;
    }

    public boolean getIsAlert() {
        return isAlert;
    }

    public void setIsAlert(boolean newValue) {
        isAlert = newValue;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }

    ;

    public TaskState getState() {
        return state;
    }

    public long getTimestampStart() {
        return timestampStart;
    }

    public long getTimestampEnd() {
        return timestampEnd;
    }

    public long getTimestampAlert() {
        return timestampAlert;
    }

}
