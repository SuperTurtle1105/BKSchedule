package com.baggarm.bkschedule.model.viewModel;

import com.baggarm.bkschedule.model.Subject;

/**
 * This model is used to store properties  of Subject: Name, Class, Code, Day, Room, Time, Weeks.
 *
 * @author DN
 * @version 2.19.1406
 * @since 1.0
 */
public class SubjectVM {
    public boolean isShowTime = false;
    private String subjectName;
    private String subjectClass;
    private String subjectCode;
    private String subjectDay;
    private String subjectRoom;
    private String subjectTime;
    private String subjectWeeks;

    public SubjectVM(Subject subject) {
        setSubjectName(subject.ten_mh);
        setSubjectWeeks(subject.tuan_hoc);
        setSubjectTime(subject.strTietHoc);
        setSubjectRoom(subject.strPhongHoc);
        setSubjectDay(subject.strThu);
        setSubjectCode(subject.ma_mh);
        setSubjectClass(subject.nhomto);
    }

    /**
     * A constructor to construct value for a new class
     *
     * @since 1.0
     */
    public SubjectVM(String subjectName, String subjectClass, String subjectCode, String subjectDay, String subjectRoom, String subjectTime, String subjectWeeks) {
        this.subjectName = subjectName;
        this.subjectClass = subjectClass;
        this.subjectCode = subjectCode;
        this.subjectDay = subjectDay;
        this.subjectRoom = subjectRoom;
        this.subjectTime = subjectTime;
        this.subjectWeeks = subjectWeeks;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectClass() {
        return subjectClass;
    }

    public void setSubjectClass(String subjectClass) {
        this.subjectClass = subjectClass;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectDay() {
        return subjectDay;
    }

    public void setSubjectDay(String subjectDay) {
        this.subjectDay = subjectDay;
    }

    public String getSubjectRoom() {
        return subjectRoom;
    }

    public void setSubjectRoom(String subjectRoom) {
        this.subjectRoom = subjectRoom;
    }

    public String getSubjectTime() {
        return subjectTime;
    }

    public void setSubjectTime(String subjectTime) {
        this.subjectTime = subjectTime;
    }

    public String getSubjectWeeks() {
        return subjectWeeks;
    }

    public void setSubjectWeeks(String subjectWeeks) {
        this.subjectWeeks = subjectWeeks;
    }

    @Override
    public String toString() {
        return "SubjectVM{" +
                "subjectName='" + subjectName + '\'' +
                '}';
    }
}
