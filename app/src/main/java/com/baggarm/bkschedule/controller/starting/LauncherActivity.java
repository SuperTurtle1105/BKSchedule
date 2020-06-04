package com.baggarm.bkschedule.controller.starting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.api.firebase.FIRAPI;
import com.baggarm.bkschedule.api.rest.ScheduleAPI;
import com.baggarm.bkschedule.api.rest.asyncTask.LoginAsyncTask;
import com.baggarm.bkschedule.api.rest.asyncTask.LoginCompletionHandler;
import com.baggarm.bkschedule.api.rest.asyncTask.ResultCode;
import com.baggarm.bkschedule.controller.MainActivity;
import com.baggarm.bkschedule.helper.NetworkHelper;
import com.baggarm.bkschedule.helper.SharedPreferencesManager;
import com.baggarm.bkschedule.model.Student;
import com.github.ybq.android.spinkit.style.Wave;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity for loading launch UI and auto-login
 * <p>
 * This activity is used to handle auto-login and can direct to MainActivity (if user already login) or LoginActivity (user didn't login before).
 *
 * @author IMBAGGAARM
 * @version 2019.1206
 * @since 1.0
 */
public class LauncherActivity extends AppCompatActivity {
    /**
     * Debug Tag for use logging debug output to LogCat
     *
     * @since 1.0
     */
    private final String TAG = LauncherActivity.class.getName();

    /**
     * Progress Bar is used to show the loading animation
     *
     * @since 1.0
     */
    private ProgressBar progressBar;

    /**
     * An override method
     * Binding views from layout, and create wave animation for progressBar. This method also calls to handleAutoLogin method to start the auto login process.
     *
     * @param savedInstanceState the old instance state, to reshow when comeback to this activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);


        //get progressBar
        progressBar = findViewById(R.id.launcher_progress_bar);
        //add wave ui for progressBar
        Wave wave = new Wave();
        wave.setColor(ContextCompat.getColor(this, R.color.appColor));
        progressBar.setIndeterminateDrawable(wave);

        handleAutoLogin();
    }

    /**
     * This method basically gets UserAccount saved in Shared Preferences and check whether UserAccount is null or not.
     * If UserAccount is null, then user has to login by themselves in LoginActivity.
     * Otherwise, the current User will be set username and password (if StrAccount could not be passed to JSON, an exception will be throw and user have to login manually).
     * After that, the TestSchedule, LearningSchedule and Semesters will be loaded from Shared Preferences.
     * If these data hadn't existed, user has to login by themselves in LoginActivity.
     * <p>
     * When these data exist, MainActivity gonna be showed, and this method also checks whether the internet connection is working or not.
     * If the internet connection is working, an async task will be started to get data from server in background thread, otherwise, handleLoginFailed
     * will be called with resultCode = INTERNET_CONNECTION_FAILED.
     * <p>
     * When completion handler is called, there are some cases can caused:
     * <ul>
     * <li> Login successfully, a method will check if the loaded data is different from old data or not, a toast gonna be showed if data was changed.
     * <li> Login failed because of server (resultCode = SERVER_FAILED), a toast gonna be showed for user to alert that by calling handleLoginFailed.
     * <li> Login failed because of account (resultCode = ACCOUNT_FAILED), a toast gonna be showed for user to alert that by calling handleLoginFailed.
     * </ul>
     *
     * @see SharedPreferencesManager
     * @see Student
     * @see NetworkHelper
     * @see LoginAsyncTask
     * @see LoginCompletionHandler
     * @see #startMainActivity()
     * @see #startLoginActivity()
     * @see #handleLoginFailed(ResultCode)
     * @see #alertIfDataChanged()
     * @since 1.0
     */
    private void handleAutoLogin() {
        //get account
        String strAccount = SharedPreferencesManager.getUserAccount();
        if (strAccount == null) {
            //because device doesn't have the account, it means there is no data in this device, user has to loginAndGetData
            startLoginActivity();
        } else {
            //get test, learning data
            try {
                //set username and password to current student
                Student currentStudent = Student.getCurrentUserAccount();
                currentStudent.setUsernameAndPasswordFromJson(new JSONObject(strAccount));

                String studentInfo = SharedPreferencesManager.getUserInfo();
                if (studentInfo != null) {
                    try {
                        JSONObject info = new JSONObject(studentInfo);
                        Student.getCurrentUserAccount().name = info.getString(SharedPreferencesManager.kUSER_INFO_NAME);
                        Student.getCurrentUserAccount().faculty = info.getString(SharedPreferencesManager.kUSER_INFO_FACULTY);
                        Student.getCurrentUserAccount().setAvtBase64Str(info.getString(SharedPreferencesManager.kUSER_INFO_AVT_BASE64STR));
                    } catch (JSONException e) {
                        //alert error
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                }

                //get data
                String strTestSchedule = SharedPreferencesManager.getTestSchedule();
                String strLearningSchedule = SharedPreferencesManager.getLearningSchedule();
                String strSemesters = SharedPreferencesManager.getSemesters();

                // log saved data
                Log.d(TAG, strTestSchedule);
                Log.d(TAG, strLearningSchedule);
                Log.d(TAG, strSemesters);

                // check if data are full
                if (strTestSchedule != null && strLearningSchedule != null && strSemesters != null) {
                    //get local semesters for use if needed
                    FIRAPI.SEMESTERS.setSemestersFromLocal(strSemesters);
                    //already have data
                    //start Main Activity
                    startMainActivity();
                    //auto login to update data in background thread
                    //check internet connection
                    if (NetworkHelper.isNetworkAvailable(this)) {
                        handleUpdateDataInBackgroundThread();
                    } else {
                        //make toast that internet connection is not working;
                        handleLoginFailed(ResultCode.INTERNET_CONNECTION_FAILED);
                    }
                } else {
                    //doesn't have data, login
                    startLoginActivity();
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getLocalizedMessage());
                e.printStackTrace();
                startLoginActivity();
            }
        }
    }

    /**
     * Update data in background thread, alert the result for users.
     *
     * @since 1.0
     */
    private void handleUpdateDataInBackgroundThread() {
        Student currentStudent = Student.getCurrentUserAccount();
        //auto loginAndGetData
        String username = currentStudent.username;
        String password = currentStudent.password;
        //
        LoginAsyncTask asyncTask = new LoginAsyncTask(username, password, new LoginCompletionHandler() {
            @Override
            public void handleLoginSucceed() {
                //update data and alert if data change
                alertIfDataChanged();
            }

            @Override
            public void handleLoginFailedServer() {
                //make toast that can not connect to server
                handleLoginFailed(ResultCode.SERVER_FAILED);
            }

            @Override
            public void handleLoginFailedAccount() {
                //alert user should re-login
                handleLoginFailed(ResultCode.ACCOUNT_FAILED);
            }
        });
        //execute task
        asyncTask.execute();
    }

    /**
     * Start LoginActivity, after that, destroy this activity immediately.
     *
     * @see LoginActivity
     * @since 1.0
     */
    private void startLoginActivity() {
        Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
        startActivity(intent);
        //destroy activity
        finish();
    }

    /**
     * Start MainActivity, after that, destroy this activity immediately.
     *
     * @see MainActivity
     * @since 1.0
     */
    private void startMainActivity() {
        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);
        //destroy activity
        finish();
    }

    /**
     * This method is used to covert resultCode to a message, and toast that message to user.
     *
     * @param resultCode an instance of ResultCode, assigned by result of login task.
     *                   There are some values of resultCode can be assigned to this method:
     *                   <ul>
     *                   <li>INTERNET_CONNECTION_FAILED</li>
     *                   <li>SERVER_FAILED</li>
     *                   <li>ACCOUNT FAILED</li>
     *                   </ul>
     * @see Toast
     * @see ResultCode
     * @see SharedPreferencesManager
     * @since 1.0
     */
    private void handleLoginFailed(ResultCode resultCode) {
        String message = "";
        switch (resultCode) {
            case INTERNET_CONNECTION_FAILED:
                message = "Không có kết nối mạng, đang sử dụng dữ liệu gần nhất.";
                break;
            case SERVER_FAILED:
                message = "Không thể kết nối tới server, đang sử dụng dữ liệu gần nhất.";
                break;
            case ACCOUNT_FAILED:
                message = "Không thể đăng nhập để cập nhật dữ liệu, đang sử dụng dữ liệu gần nhất.";
                break;
            default:
                break;
        }
        //alert error
        Toast.makeText(SharedPreferencesManager.APPLICATION_CONTEXT, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method simply shows a toast to user when data is loaded.
     * There are some types of message can be showed:
     * <ul>
     * <li>Test schedule and learning schedule are both changed</li>
     * <li>Test schedule is changed</li>
     * <li>Learning schedule is changed</li>
     * <li>Test and learning schedule are not changed</li>
     * </ul>
     *
     * @see ScheduleAPI
     * @see Toast
     * @see SharedPreferencesManager
     * @since 1.0
     */
    private void alertIfDataChanged() {
        //alert if data is new
        if (ScheduleAPI.isNewLearningData && ScheduleAPI.isNewTestData) {
            Toast.makeText(SharedPreferencesManager.APPLICATION_CONTEXT, "Thời khoá biểu và lịch thi có cập nhật mới.", Toast.LENGTH_SHORT).show();
        } else if (ScheduleAPI.isNewTestData) {
            Toast.makeText(SharedPreferencesManager.APPLICATION_CONTEXT, "Lịch thi có cập nhật mới.", Toast.LENGTH_SHORT).show();
        } else if (ScheduleAPI.isNewLearningData) {
            Toast.makeText(SharedPreferencesManager.APPLICATION_CONTEXT, "Thời khoá biểu có cập nhật mới.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SharedPreferencesManager.APPLICATION_CONTEXT, "Thời khoá biểu và lịch thi không có cập nhật mới.", Toast.LENGTH_SHORT).show();
        }
    }
}
