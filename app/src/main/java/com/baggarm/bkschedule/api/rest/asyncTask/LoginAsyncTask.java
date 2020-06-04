package com.baggarm.bkschedule.api.rest.asyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.baggarm.bkschedule.api.rest.Auth;

/**
 * An async task to login to BK Server
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @see LoginCompletionHandler
 * @since 1.0
 */
public class LoginAsyncTask extends AsyncTask<Void, ResultCode, Void> {

    private final String TAG = LoginAsyncTask.class.getName();

    LoginCompletionHandler completionHandler;

    private String username;

    private String password;

    private ResultCode resultCode;

    public LoginAsyncTask(String username, String password, LoginCompletionHandler completionHandler) {
        this.username = username;
        this.password = password;
        this.completionHandler = completionHandler;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Auth.getInstance().loginAndGetData(username, password, new LoginCompletionHandler() {
            @Override
            public void handleLoginSucceed() {
                resultCode = ResultCode.SUCCEED;
            }

            @Override
            public void handleLoginFailedServer() {
                resultCode = ResultCode.SERVER_FAILED;
            }

            @Override
            public void handleLoginFailedAccount() {
                resultCode = ResultCode.ACCOUNT_FAILED;
            }
        });
        return null;
    }

    /**
     * Call completion handler when task is done
     *
     * @param aVoid
     * @since 1.0
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (resultCode == null) {
            Log.d(TAG, "unexpected resultCode = null, couldn't get the result");
            completionHandler.handleLoginFailedServer();
            return;
        }
        switch (resultCode) {
            case SUCCEED:
                completionHandler.handleLoginSucceed();
                break;
            case ACCOUNT_FAILED:
                completionHandler.handleLoginFailedAccount();
                break;
            case SERVER_FAILED:
                completionHandler.handleLoginFailedServer();
                break;
            default:
                Log.d(TAG, "unexpected result, couldn't get the result");
        }
    }
}
