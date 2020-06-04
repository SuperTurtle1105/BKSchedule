package com.baggarm.bkschedule.api;

import android.content.Context;
import android.util.Log;

import com.baggarm.bkschedule.api.firebase.FIRAPI;
import com.baggarm.bkschedule.api.rest.ScheduleAPI;
import com.baggarm.bkschedule.database.AppDatabase;
import com.baggarm.bkschedule.database.TodaySchedule;
import com.baggarm.bkschedule.helper.Extensions;
import com.baggarm.bkschedule.helper.SharedPreferencesManager;
import com.baggarm.bkschedule.model.LearningSchedule;
import com.baggarm.bkschedule.model.Semester;
import com.baggarm.bkschedule.model.Subject;
import com.baggarm.bkschedule.model.Test;
import com.baggarm.bkschedule.model.TestSchedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to get schedules in local database and used for all schedules api in application
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class LocalScheduleAPI {

    //create singleton
    private static LocalScheduleAPI ourInstance;

    private final String TAG = LocalScheduleAPI.class.getName();

    public boolean isShouldReloadLearningSchedules = true;

    public boolean isShouldReloadTestSchedules = true;

    public boolean isShouldReloadTodayTestSchedules = true;

    public boolean isShouldReloadTodayLearningSchedules = true;

    private ArrayList<LearningSchedule> learningSchedules = new ArrayList<LearningSchedule>();

    private ArrayList<TestSchedule> testSchedules = new ArrayList<TestSchedule>();

    private ArrayList<TodaySchedule> todayTestSchedules = new ArrayList<TodaySchedule>();

    private ArrayList<TodaySchedule> todayLearningSchedules = new ArrayList<TodaySchedule>();

    private Date selectedDate;

    private LocalScheduleAPI() {}

    public static LocalScheduleAPI getInstance() {
        if (ourInstance == null) {
            ourInstance = new LocalScheduleAPI();
        }
        return ourInstance;
    }

    public static List<TodaySchedule> getAll(Context context) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        return db.scheduleDao().getAll();
    }

    /**
     * Clear stored data when logout
     *
     * @since 1.0
     */
    public static void clearCache() {
        if (ourInstance != null) {
            ourInstance.isShouldReloadLearningSchedules = true;
            ourInstance.isShouldReloadTodayLearningSchedules = true;
            ourInstance.isShouldReloadTestSchedules = true;
            ourInstance.isShouldReloadTodayTestSchedules = true;
        }
    }

    /**
     * Get learning schedules from shared preference manager, if data is loaded in the last time and data was not change
     * It will get the value from the last time to return in order to improve performance
     *
     * @return Learning schedules list of user
     * @since 1.0
     */
    public ArrayList<LearningSchedule> getLearningSchedule() {

        if (!(ScheduleAPI.isNewLearningData || isShouldReloadLearningSchedules || learningSchedules.isEmpty())) {
            return learningSchedules;
        }

        //get all learning schedules in shared preferences
        String strLearningSchedule = SharedPreferencesManager.getLearningSchedule();
        try {
            JSONObject jsonObject = new JSONObject(strLearningSchedule);
            ArrayList<LearningSchedule> learningScheduleList = new ArrayList<>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                LearningSchedule schedule = new LearningSchedule(key, jsonObject.getJSONObject(key));
                learningScheduleList.add(schedule);
            }
            learningSchedules = learningScheduleList;
            isShouldReloadLearningSchedules = false;
            return learningSchedules;
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get test schedules from shared preference manager, if data is loaded in the last time and data was not change
     * It will get the value from the last time to return in order to improve performance
     *
     * @return Test schedules list of user
     * @since 1.0
     */
    public ArrayList<TestSchedule> getTestSchedule() {
        //return old data if needed
        if (!(ScheduleAPI.isNewTestData || isShouldReloadTestSchedules || testSchedules.isEmpty())) {
            return testSchedules;
        }

        String strTestSchedule = SharedPreferencesManager.getTestSchedule();
        try {
            JSONObject jsonObject = new JSONObject(strTestSchedule);
            ArrayList<TestSchedule> testScheduleList = new ArrayList<>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                TestSchedule testSchedule = new TestSchedule(key, jsonObject.getJSONObject(key));

                testScheduleList.add(testSchedule);
            }
            testSchedules = testScheduleList;
            isShouldReloadTestSchedules = false;
            return testSchedules;
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get schedules of user, combined from today schedules were saved in database, test schedules, learning schedules in a given date
     *
     * @param context
     * @param date    date to load schedule
     * @return schedules in date
     * @since 1.0
     */
    public ArrayList<TodaySchedule> getSchedulesInDay(Context context, Date date) {

        List<TodaySchedule> todayScheduleList;

        //get offline schedule
        //get learning schedule
        ArrayList<TodaySchedule> todayLearningScheduleList = getLearningSchedulesInDay(date);
        //get test schedule
        ArrayList<TodaySchedule> todayTestScheduleList = getTestSchedulesInDay(date);

        selectedDate = date;
        //get today schedule from sqlite database
        //create database
        AppDatabase db = AppDatabase.getAppDatabase(context);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);


        long start = Extensions.startOfDay(date);
        long end = Extensions.endOfDay(date);

        todayScheduleList = db.scheduleDao().findScheduleByTime(start, end);

        //check if can not get data
        //will nerver happen (db return empty array, not null array)
        if (todayScheduleList == null) {
            todayScheduleList = new ArrayList<>();
        }

        //merge today and learning/test schedule to today schedule
        if (todayLearningScheduleList != null)
            todayScheduleList.addAll(todayLearningScheduleList);
        if (todayTestScheduleList != null)
            todayScheduleList.addAll(todayTestScheduleList);

        //make array is unique
        //get timestamp current;
        long currentTimestamp = new Date().getTime();
        ArrayList<TodaySchedule> uniqueTodayScheduleList = new ArrayList<>();
        for (TodaySchedule schedule : todayScheduleList) {
            if (!uniqueTodayScheduleList.contains(schedule)) {
                schedule.setStateByTimestamp(currentTimestamp);
                uniqueTodayScheduleList.add(schedule);
            }
        }

        //sort list in ascending order
        Collections.sort(uniqueTodayScheduleList);
        return uniqueTodayScheduleList;
    }

    /**
     * Get test schedules in given date
     *
     * @param date
     * @return
     * @since 1.0
     */
    private ArrayList<TodaySchedule> getTestSchedulesInDay(Date date) {

        if ((selectedDate != null && selectedDate.compareTo(date) == 0 && !todayTestSchedules.isEmpty()) && (!ScheduleAPI.isNewTestData && !isShouldReloadTodayTestSchedules)) {
            return todayTestSchedules;
        }

        //get all test schedules in shared preferences
        String strTestSchedule = SharedPreferencesManager.getTestSchedule();
        if (strTestSchedule == null)
            return null;
        try {
            JSONObject jsonObject = new JSONObject(strTestSchedule);

            //current semester id
            ArrayList<TodaySchedule> todayScheduleList = new ArrayList<>();
            //get currensemester id from firebase
            String currentSemesterId = getCurrentSemesterId(FIRAPI.SEMESTERS.getSemesters(), date);

            TestSchedule testSchedule = new TestSchedule(currentSemesterId, jsonObject.getJSONObject(currentSemesterId));
            //convert date in BKUJson Format string (in json format that get from restful api)
            String strDateBKUJson = Extensions.convertDateToBKUJSONFormat(date);
            long startOfToday = Extensions.startOfDay(date) / 1000;
            for (Test test : testSchedule.testList) {
                if (test.ngaykt.equals(strDateBKUJson) || test.ngaythi.equals(strDateBKUJson)) {
                    long startOfTestTimestamp;
                    long endOfTestTimestamp;
                    boolean isEndCourseTest = false;
                    if (test.ngaykt == strDateBKUJson) {
                        //midterm tests
                        startOfTestTimestamp = startOfToday + Extensions.convertStartTestTimeToTimestamp(test.gio_kt);
                        endOfTestTimestamp = startOfTestTimestamp + 2 * 3600;
                    } else {
                        //final tests
                        isEndCourseTest = true;
                        startOfTestTimestamp = startOfToday + Extensions.convertStartTestTimeToTimestamp(test.gio_thi);
                        endOfTestTimestamp = startOfTestTimestamp + 2 * 3600;
                    }

                    //create todaySchedule from test
                    TodaySchedule todaySchedule = new TodaySchedule(test, isEndCourseTest, false, startOfTestTimestamp, startOfTestTimestamp, endOfTestTimestamp);
                    //add todaySchedule to todayScheduleList
                    todayScheduleList.add(todaySchedule);
                }
            }
            todayTestSchedules = todayScheduleList;
            isShouldReloadTodayTestSchedules = false;
            return todayTestSchedules;
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get learning schedules in given date
     *
     * @param date
     * @return
     * @since 1.0
     */
    private ArrayList<TodaySchedule> getLearningSchedulesInDay(Date date) {
        if ((selectedDate != null && selectedDate.compareTo(date) == 0 && !todayLearningSchedules.isEmpty()) && (!ScheduleAPI.isNewLearningData && !isShouldReloadTodayLearningSchedules)) {
            return todayLearningSchedules;
        }

        //get all learning schedules in shared preferences
        String strLearningSchedule = SharedPreferencesManager.getLearningSchedule();
        if (strLearningSchedule == null)
            return null;
        try {
            JSONObject jsonObject = new JSONObject(strLearningSchedule);
            ArrayList<Subject> subjectList = new ArrayList<>();
            //current semester id
            //get currensemester id from firebase
            String currentSemesterId = getCurrentSemesterId(FIRAPI.SEMESTERS.getSemesters(), date);
            LearningSchedule learningSchedule = new LearningSchedule(currentSemesterId, jsonObject.getJSONObject(currentSemesterId));

            String strDate = Extensions.convertDateToBKUJSONDay(date);
            String strWeekYear = Extensions.getWeekYearString(date);

            for (Subject subject : learningSchedule.subjectList) {
                String weekDay = subject.thu1;
                String weekLearning = subject.tuan_hoc;
                if (weekDay.equals(strDate) && weekLearning.contains(strWeekYear)) {
                    subjectList.add(subject);
                }
            }

            //get all subjects in today by week day
            ArrayList<TodaySchedule> todayScheduleList = new ArrayList<>();
            long startOfToday = Extensions.startOfDay(date) / 1000;
            for (Subject subject : subjectList) {
                //get start of lesson by timestamp
                long startOfLessonTimestamp = startOfToday + Extensions.convertStartLessonTimeToTimestamp(subject.tiet_bd1);
                long endOfLessonTimestamp = startOfToday + Extensions.convertEndLessonTimeToTimestamp(subject.tiet_kt1);

                TodaySchedule todaySchedule = new TodaySchedule(subject, false, startOfLessonTimestamp, startOfLessonTimestamp, endOfLessonTimestamp);
                todayScheduleList.add(todaySchedule);
            }

            todayLearningSchedules = todayScheduleList;
            isShouldReloadTodayLearningSchedules = false;
            return todayLearningSchedules;
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Insert schedules into database
     *
     * @param context
     * @param schedules
     * @return
     * @since 1.0
     */
    public long[] insertSchedules(Context context, TodaySchedule... schedules) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        return db.scheduleDao().insertSchedules(schedules);
    }

    /**
     * Insert schedule into database
     *
     * @param context
     * @param schedule
     * @return
     * @since 1.0
     */
    public long insertSchedule(Context context, TodaySchedule schedule) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        return db.scheduleDao().insertSchedule(schedule);
    }

    /**
     * Delete schedule from database
     *
     * @param context
     * @return
     * @since 1.0
     */
    public void deleteSchedule(Context context, TodaySchedule schedule) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        db.scheduleDao().delete(schedule);
    }

    /**
     * Delete schedule from database by id of schedule
     *
     * @param context
     * @return
     * @since 1.0
     */
    public void deleteScheduleById(Context context, int id) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        db.scheduleDao().deleteById(id);
    }

    /**
     * Get all schedules in database
     *
     * @param context
     * @return
     * @since 1.0
     */
    public List<TodaySchedule> selectAll(Context context) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        return db.scheduleDao().getAll();
    }

    /**
     * Update schedule into database
     *
     * @param context
     * @return
     * @since 1.0
     */
    public void updateSchedule(Context context, TodaySchedule... schedules) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        db.scheduleDao().updateSchedules(schedules);
    }

    /**
     * Update alert on state to database
     *
     * @param context
     * @return
     * @since 1.0
     */
    public void updateIsAlertOn(Context context, int id) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        db.scheduleDao().updateIsAlertOn(id);
    }

    /**
     * Get schedule by id of schedule
     *
     * @param context
     * @param ID
     * @return
     * @since 1.0
     */
    public TodaySchedule getScheduleByID(Context context, int ID) {
        AppDatabase db = AppDatabase.getAppDatabase(context);
        return db.scheduleDao().getScheduleByID(ID);
    }

    /**
     * Get semester id with a given date
     *
     * @param semesters
     * @param date
     * @return semesterID
     * @since 1.0
     */
    private String getCurrentSemesterId(List<Semester> semesters, Date date) {
        String id = "";

        long timestamp = date.getTime() / 1000;

        System.out.println(date);
        for (Semester semester : semesters) {
            if (semester.startTimestamp <= timestamp && timestamp <= semester.endTimestamp) {
                return semester.id;
            }
        }
        return id;
    }
}
