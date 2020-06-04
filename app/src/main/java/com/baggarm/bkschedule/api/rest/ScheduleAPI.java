package com.baggarm.bkschedule.api.rest;

import android.util.Log;

import com.baggarm.bkschedule.api.LocalScheduleAPI;
import com.baggarm.bkschedule.api.rest.asyncTask.GetScheduleCompletionHandler;
import com.baggarm.bkschedule.helper.SharedPreferencesManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class is used to get schedule json from BK server
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class ScheduleAPI {
    //single ton
    private static final ScheduleAPI ourInstance = new ScheduleAPI();

    public static boolean isNewTestData = false;

    public static boolean isNewLearningData = false;

    private final String TAG = ScheduleAPI.class.getName();

    private boolean loadedLearningData = false;

    private boolean loadedTestData = false;

    private ScheduleAPI() {
    }

    public static ScheduleAPI getInstance() {
        return ourInstance;
    }

    /**
     * Get test and learning schedules with given http client
     *
     * @param client
     * @param completionHandler
     * @since 1.0
     */
    public void getSchedules(OkHttpClient client, GetScheduleCompletionHandler completionHandler) {

        getLearningSchedules(client, new GetScheduleCompletionHandler() {
            @Override
            public void onGetDataSucceed() {
                loadedLearningData = true;
                if (loadedLearningData && loadedTestData) {
                    completionHandler.onGetDataSucceed();
                }
            }

            @Override
            public void onGetDataFailed() {
                loadedLearningData = true;
                if (loadedLearningData && loadedTestData) {
                    completionHandler.onGetDataFailed();
                }
            }
        });

        getTestSchedules(client, new GetScheduleCompletionHandler() {
            @Override
            public void onGetDataSucceed() {
                loadedTestData = true;
                if (loadedLearningData && loadedTestData) {
                    completionHandler.onGetDataSucceed();
                }
            }

            @Override
            public void onGetDataFailed() {
                loadedTestData = true;
                if (loadedLearningData && loadedTestData) {
                    completionHandler.onGetDataFailed();
                }
            }
        });
    }

    /**
     * Get learning schedules from server
     *
     * @param client
     * @param completionHandler
     * @since 1.0
     */
    private void getLearningSchedules(OkHttpClient client, GetScheduleCompletionHandler completionHandler) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "_token=" + Auth.JWTToken);

        String learningScheduleURL = "http://www.aao.hcmut.edu.vn/stinfo/lichthi/ajax_lichhoc";
        Request request = new Request.Builder()
                .post(body)
                .url(learningScheduleURL)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            if (json != null) {
                String oldData = SharedPreferencesManager.getLearningSchedule();
                if (oldData != null && !oldData.isEmpty() && !json.equals(oldData)) {
                    //new data
                    //set data to local
                    SharedPreferencesManager.setLearningSchedule(json);
                    //set is_new_learning_data to true
                    setIsNewLearningData(true);

                    completionHandler.onGetDataSucceed();
                } else {
                    SharedPreferencesManager.setLearningSchedule(json);
                    completionHandler.onGetDataSucceed();
                }
            } else {
                completionHandler.onGetDataFailed();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            completionHandler.onGetDataFailed();
        }
    }

    /**
     * Get test schedules from server
     *
     * @param client
     * @param completionHandler
     * @since 1.0
     */
    private void getTestSchedules(OkHttpClient client, GetScheduleCompletionHandler completionHandler) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "_token=" + Auth.JWTToken);

        String testScheduleURL = "http://www.aao.hcmut.edu.vn/stinfo/lichthi/ajax_lichthi";
        Request request = new Request.Builder()
                .post(body)
                .url(testScheduleURL)
                .build();

        try {
            Response response = client.newCall(request).execute();

            String json = response.body().string();
            if (json != null) {
                String oldData = SharedPreferencesManager.getTestSchedule();
                if (oldData != null && !oldData.isEmpty() && !json.equals(oldData)) {
                    //new data
                    //set data to local
                    SharedPreferencesManager.setTestSchedule(json);
                    //set is_new_test_data to true
                    setIsNewTestData(true);
                    completionHandler.onGetDataSucceed();
                } else {
                    SharedPreferencesManager.setTestSchedule(json);
                    completionHandler.onGetDataSucceed();
                }
            } else {
                completionHandler.onGetDataFailed();
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            completionHandler.onGetDataFailed();
        }
    }

    /**
     * Update value if test data was updated by server
     *
     * @param isNew
     * @since 1.0
     */
    public void setIsNewTestData(boolean isNew) {
        isNewTestData = isNew;
        LocalScheduleAPI.getInstance().isShouldReloadTodayTestSchedules = isNew;
        LocalScheduleAPI.getInstance().isShouldReloadTestSchedules = isNew;
    }

    /**
     * Update value if learning data was updated by server
     *
     * @param isNew
     * @since 1.0
     */
    public void setIsNewLearningData(boolean isNew) {
        isNewLearningData = isNew;
        LocalScheduleAPI.getInstance().isShouldReloadTodayLearningSchedules = isNew;
        LocalScheduleAPI.getInstance().isShouldReloadLearningSchedules = isNew;
    }
}
