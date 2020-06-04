package com.baggarm.bkschedule.api.rest.asyncTask;

/**
 * Logout completion handler is called when log out task is done
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @see LogoutAsyncTask
 * @since 1.0
 */
public interface LogoutCompletionHandler {
    void onLogoutSucceed();

    void onLogoutFailed();
}
