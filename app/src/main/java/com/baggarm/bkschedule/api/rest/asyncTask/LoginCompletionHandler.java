package com.baggarm.bkschedule.api.rest.asyncTask;

/**
 * This interface was created in order to use in login async task, call when task is done
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @see LoginAsyncTask
 * @since 1.0
 */
public interface LoginCompletionHandler {

    void handleLoginSucceed();

    void handleLoginFailedServer();

    void handleLoginFailedAccount();
}