package com.baggarm.bkschedule.api.rest.asyncTask;

/**
 * This interface is used for get schedule api
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public interface GetScheduleCompletionHandler {
    void onGetDataSucceed();

    void onGetDataFailed();
}
