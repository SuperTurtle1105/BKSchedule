package com.baggarm.bkschedule.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The activity for getting data through calling superclass function (for function TKB) from server.
 * It consists of a list of the test.
 *
 * @author Thuan
 * @version 2019.1206
 * @since 1.0
 */
public class TestSchedule extends BaseBKUSchedule {

    private static final String TAG = TestSchedule.class.getName();

    //list of test schedules in this semester
    public ArrayList<Test> testList = new ArrayList<Test>();

    /**
     * This method gets data from jsonObject.
     * It inherits from BaseBKUSchedule. After calling constructor from this superclass to get data.
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
    public TestSchedule(String id, JSONObject jsonObject) {
        super(id, jsonObject);

        //there are two types of schedule in json
        JSONArray arrSchedule;
        JSONObject mapSchedule;

        try {
            arrSchedule = jsonObject.getJSONArray("lichthi");

            for (int i = 0; i < arrSchedule.length(); i++) {
                JSONObject data = arrSchedule.getJSONObject(i);
                Test test = new Test(data);

                testList.add(test);
            }
        } catch (JSONException e) {
            try {
                mapSchedule = jsonObject.getJSONObject("lichthi");

                Iterator<String> keys = mapSchedule.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JSONObject data = mapSchedule.getJSONObject(key);
                    Test test = new Test(data);

                    testList.add(test);
                }

            } catch (Exception er) {
                //handle error
                Log.d(TAG, er.getLocalizedMessage());
                er.printStackTrace();
            }
        }
    }
}
