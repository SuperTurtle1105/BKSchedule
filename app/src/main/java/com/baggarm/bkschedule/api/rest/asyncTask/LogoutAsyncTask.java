package com.baggarm.bkschedule.api.rest.asyncTask;

import android.os.AsyncTask;

import com.baggarm.bkschedule.api.rest.Auth;

/**
 * This task is used to logout
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @see LogoutCompletionHandler
 * @since 1.0
 */
public class LogoutAsyncTask extends AsyncTask<Void, ResultCode, Void> {
    LogoutCompletionHandler completionHandler;
    private ResultCode resultCode;

    public LogoutAsyncTask(LogoutCompletionHandler completionHandler) {
        this.completionHandler = completionHandler;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        boolean result = Auth.getInstance().logout();
        if (result)
            resultCode = ResultCode.SUCCEED;
        else
            resultCode = ResultCode.SERVER_FAILED;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (resultCode.equals(ResultCode.SUCCEED)) {
            // true
            completionHandler.onLogoutSucceed();
        } else {
            //false
            completionHandler.onLogoutFailed();
        }
    }
}
