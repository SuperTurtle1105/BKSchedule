package com.baggarm.bkschedule.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This activity for getting the period time of the semester.
 * Class Semester contains id semester (unique for each semester),
 * time stamp start and time stamp end.
 *
 * @author Thuan
 * @version 2019.1406
 * @since 1.0
 */
public class Semester {

    private static final String TAG = Semester.class.getName();

    /**
     * There are three attributes:
     * <ul>
     * <li>id: one id for one semester.</li>
     * <li>startTimestamp: the beginning time of this semester.</li>
     * <li>endTimestamp: the ending time of this semester.</li>
     * </ul>
     */
    public String id;
    public long startTimestamp = 0;
    public long endTimestamp = 0;

    /**
     * This method is to get data (the period of the semester).
     * An exception will be throw if it appears errors.
     *
     * @param jsonObject
     * @param key
     * @since 1.0
     */
    Semester(String key, JSONObject jsonObject) {
        id = key;
        try {
            startTimestamp = jsonObject.getLong("startTimestamp");
            endTimestamp = jsonObject.getLong("endTimestamp");
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method returns a list of semesters.
     * The while loop will get all data about semesters to list of semesters.
     * #Semester(key, semesterJson)
     *
     * @param jsonObject
     * @since 1.0
     */

    public static List<Semester> semestersFrom(JSONObject jsonObject) {

        List<Semester> semesters = new ArrayList<Semester>();

        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject semesterJson = jsonObject.getJSONObject(key);
                Semester semester = new Semester(key, semesterJson);

                semesters.add(semester);
            } catch (JSONException e) {
                Log.d(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        return semesters;
    }

}
