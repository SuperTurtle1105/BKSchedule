package com.baggarm.bkschedule.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class was created in order to save and get data from App shared preferences
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class SharedPreferencesManager {

    private static final String TAG = SharedPreferencesManager.class.getName();

    /**
     * Name of student
     *
     * @see #USER_INFO
     * @see #kUSER_INFO_FACULTY
     * @see #kUSER_INFO_AVT_BASE64STR
     * @see #getUserInfo()
     * @since 1.0
     */
    public static final String kUSER_INFO_NAME = "USER_INFO_NAME";

    /**
     * Faculty of student
     *
     * @see #USER_INFO
     * @see #kUSER_INFO_NAME
     * @see #kUSER_INFO_AVT_BASE64STR
     * @see #getUserInfo()
     * @since 1.0
     */
    public static final String kUSER_INFO_FACULTY = "kUSER_INFO_FACULTY";

    /**
     * A bases 64 string of student's avatar
     *
     * @see #USER_INFO
     * @see #kUSER_INFO_FACULTY
     * @see #kUSER_INFO_NAME
     * @see #getUserInfo()
     * @since 1.0
     */
    public static final String kUSER_INFO_AVT_BASE64STR = "kUSER_INFO_AVT_BASE64STR";

    /**
     * Used to create a private context to save data
     *
     * @see #getSharedPreferences()
     * @since 1.0
     */
    private static final String APP_SETTINGS = "APP_SETTINGS";

    /**
     * Stores name, faculty, avatar of student
     *
     * @see #kUSER_INFO_NAME
     * @see #kUSER_INFO_AVT_BASE64STR
     * @see #kUSER_INFO_FACULTY
     * @see #getUserInfo()
     * @since 1.0
     */
    private static final String USER_INFO = "USER_INFO";

    /**
     * Store test schedules json that was get from server
     *
     * @see #LEARNING_SCHEDULE
     * @see #getTestSchedule()
     * @since 1.0
     */
    private static final String TEST_SCHEDULE = "TEST_SCHEDULE";

    /**
     * Store test schedules json that was get from server
     *
     * @see #TEST_SCHEDULE
     * @see #getLearningSchedule() ()
     * @since 1.0
     */
    private static final String LEARNING_SCHEDULE = "LEARNING_SCHEDULE";

    /**
     * Store username and password of user
     *
     * @see #getUserAccount()
     * @since 1.0
     */
    private static final String USER_ACCOUNT = "USER_ACCOUNT";

    /**
     * Store semester json was get from firebase
     *
     * @see #getSemesters()
     * @since 1.0
     */
    private static final String SEMESTERS = "SEMESTERS";

    /**
     * Application context to use for all app
     *
     * @since 1.0
     */
    public static Context APPLICATION_CONTEXT;

    /**
     * Semester json that was got from storage
     *
     * @since 1.0
     */
    private static String semesters = "";

    /**
     * Test schedules json that was got from storage
     *
     * @since 1.0
     */
    private static String testSchedules = "";

    /**
     * Learning schedules json that was got from storage
     *
     * @since 1.0
     */
    private static String learningSchedules = "";

    /**
     * User info json that was got from storage
     *
     * @since 1.0
     */
    private static String userInfo = "";

    private SharedPreferencesManager() {
    }

    /**
     * Get shared preferences with mode private with key is APP_SETTINGS, if it has not created, it will be created a new instance to return
     *
     * @return
     * @since 1.0
     */
    private static SharedPreferences getSharedPreferences() {
        return APPLICATION_CONTEXT.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getUserInfo() {
        if (userInfo != null && !userInfo.isEmpty()) {
            return userInfo;
        }
        userInfo = getSharedPreferences().getString(USER_INFO, null);
        return userInfo;
    }

    public static void setUserInfo(String name, String faculty, String avtBase64Str) {
        String newValue = "";
        if (name != null && faculty != null) {
            newValue = "{\"" + kUSER_INFO_NAME + "\": \"" + name + "\", \"" + kUSER_INFO_FACULTY + "\": \"" + faculty + "\", \"" + kUSER_INFO_AVT_BASE64STR + "\": \"" + avtBase64Str + "\"}";
        }
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(USER_INFO, newValue);
        editor.commit();

        userInfo = newValue;
    }

    public static String getTestSchedule() {
        if (testSchedules != null && !testSchedules.isEmpty()) {
            return testSchedules;
        }
        testSchedules = getSharedPreferences().getString(TEST_SCHEDULE, null);
        return testSchedules;
    }

    public static void setTestSchedule(String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(TEST_SCHEDULE, newValue);
        editor.commit();

        testSchedules = newValue;
    }

    public static String getLearningSchedule() {
        if (learningSchedules != null && !learningSchedules.isEmpty()) {
            return learningSchedules;
        }
        learningSchedules = getSharedPreferences().getString(LEARNING_SCHEDULE, null);
        return learningSchedules;
    }

    public static void setLearningSchedule(String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(LEARNING_SCHEDULE, newValue);
        editor.commit();

        learningSchedules = newValue;
    }

    public static String getUserAccount() {
        String json = getSharedPreferences().getString(USER_ACCOUNT, null);
        if (json != null) {
            Log.d(TAG, json);
        } else {
            Log.d(TAG, "null result");
        }

        return json;
    }

    public static void setUserAccount(String username, String password) {
        String newValue = "";
        if (username != null && password != null) {
            newValue = "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}";
        }
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(USER_ACCOUNT, newValue);
        editor.commit();
    }

    public static void removeUserAccount() {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(USER_ACCOUNT);
        editor.commit();
    }

    public static String getSemesters() {
        if (!semesters.isEmpty()) {
            return semesters;
        }
        semesters = getSharedPreferences().getString(SEMESTERS, null);
        return semesters;
    }

    public static void setSemesters(String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(SEMESTERS, newValue);
        editor.commit();
        semesters = newValue;
    }

    /**
     * Clear data when logout
     *
     * @since 1.0
     */
    public static void clearCache() {
        semesters = "";
        learningSchedules = "";
        testSchedules = "";
        userInfo = "";
    }
}

