package com.baggarm.bkschedule.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The activity for getting data through calling superclass function (for function TKB) from server.
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public class LearningSchedule extends BaseBKUSchedule {

    private static final String TAG = LearningSchedule.class.getName();

    //list of subjects in this semester schedule
    public ArrayList<Subject> subjectList = new ArrayList<Subject>();

    /**
     * This method gets data from jsonObject.
     * It inherits from BaseBKUSchedule. After calling constructor from this superclass to get data (label data: semester, date of update,...), it gets data for subjects, times,...
     * <p>
     * BKU server has two types of schedule in json: Array and Object.
     * This method tries to get data from JSONArray. If it fails, the methods will try to get data from Object. If both of them fail, an exception will be throw.
     * </p>
     *
     * @param id
     * @param jsonObject
     * @see JSONArray
     * @see JSONObject
     * @since 1.0
     */
    public LearningSchedule(String id, JSONObject jsonObject) {
        //gets data from jsonObject.
        super(id, jsonObject);

        //there are two types of schedule in json
        JSONArray arrSchedule;
        JSONObject mapSchedule;

        try {
            arrSchedule = jsonObject.getJSONArray("lichhoc");

            for (int i = 0; i < arrSchedule.length(); i++) {
                JSONObject data = arrSchedule.getJSONObject(i);
                Subject subject = new Subject(data);
                subjectList.add(subject);
            }
        } catch (JSONException e) {
            try {
                mapSchedule = jsonObject.getJSONObject("lichhoc");
                Iterator<String> keys = mapSchedule.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject data = mapSchedule.getJSONObject(key);
                    Subject subject = new Subject(data);

                    subjectList.add(subject);
                }
            } catch (Exception er) {
                //handle error
                Log.d(TAG, er.getLocalizedMessage());
                er.printStackTrace();
            }
        }
    }
}

