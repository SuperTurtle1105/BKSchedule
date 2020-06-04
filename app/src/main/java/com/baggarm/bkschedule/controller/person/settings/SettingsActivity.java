package com.baggarm.bkschedule.controller.person.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.api.LocalScheduleAPI;
import com.baggarm.bkschedule.api.rest.asyncTask.LogoutAsyncTask;
import com.baggarm.bkschedule.api.rest.asyncTask.LogoutCompletionHandler;
import com.baggarm.bkschedule.controller.schedule.ScheduleFragment;
import com.baggarm.bkschedule.controller.starting.LoginActivity;
import com.baggarm.bkschedule.controller.today.TodayFragment;
import com.baggarm.bkschedule.helper.SharedPreferencesManager;
import com.baggarm.bkschedule.model.Student;

//import com.example.bkschedule.PersonSubClass.StudentDetailInfoActivity;

/**
 * This activity shows setting UI, handle logout task and also processing when user sending report email.
 *
 * @author PXThanhLam
 * @version 2019.1306
 * @since 1.0
 */
public class SettingsActivity extends AppCompatActivity {
    /**
     * Policy button, show applicatin's policy when user taps on it
     *
     * @since 1.0
     */
    private Button btnPolicy;
    /**
     * Terms button, show applicatin's terms when user taps on it
     *
     * @since 1.0
     */
    private Button btnTerms;
    /**
     * TeamInfo button, show applicatin's information  when user taps on it
     *
     * @since 1.0
     */
    private Button btnTeamInfo;
    /**
     * Report button,when user taps on it, it will so email for user to send report
     *
     * @since 1.0
     */
    private Button btnReport;
    /**
     * Logout button, start logout task when user taps on
     *
     * @since 1.0
     */
    private Button btnLogout;
    /**
     * Email TextView, it show user's email
     *
     * @since 1.0
     */
    private TextView txtEmail;
    /**
     * toolbar is a Toolbar which show the app title
     *
     * @since 1.0
     */
    private Toolbar toolbar;

    /**
     * set title for toolbar to show, set the email text to user's email
     * show app's Policy when user taps on Policy button
     * show the app's information when user taps on TeamInfo button
     * show app's Terms when user taps on Terms button
     * show the email UI for user to send report when user taps on Report button
     * handle logout task when user taps on Logout button
     *
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Cài đặt");
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.mtoolbar_setting);
        setSupportActionBar(toolbar);

        txtEmail = findViewById(R.id.txt_email);
        txtEmail.setText(Student.getCurrentUserAccount().email);

        btnPolicy = findViewById(R.id.policy_item);
        btnPolicy.setOnClickListener(v -> startPolicyActivity());

        btnTerms = findViewById(R.id.term_of_use);
        btnTerms.setOnClickListener(v -> startTermsActivity());

        btnTeamInfo = findViewById(R.id.team_info);
        btnTeamInfo.setOnClickListener(v -> startTeamInfoActivity());

        btnReport = findViewById(R.id.support_report_bug);
        btnReport.setOnClickListener(v -> sendReportBugEmail());

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> showConfirmLogoutDialog());
    }

    /**
     * start PolicyActivity
     */

    private void startPolicyActivity() {
        Intent intent = new Intent(SettingsActivity.this, SettingPolicyActivity.class);
        startActivity(intent);
    }

    /**
     * start TermsActivity
     */

    private void startTermsActivity() {
        Intent intent = new Intent(SettingsActivity.this, SettingTermsActivity.class);
        startActivity(intent);
    }

    /**
     * start TeamInfoActivity
     */

    private void startTeamInfoActivity() {
        Intent intent = new Intent(SettingsActivity.this, SettingTeamInfoActivity.class);
        startActivity(intent);
    }

    /**
     * It let the user to choose the application to send the report email,
     * then it show the UI for user to send report
     */

    private void sendReportBugEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.report_bug_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "THÔNG BÁO LỖI");
        startActivity(Intent.createChooser(intent, "Chọn ứng dụng mail để gửi..."));
    }

    /**
     * let the User confirm if he/she really want to logout by showing a dialog for he/she to choose,
     * after that, the dialog will be dismissed
     */

    private void showConfirmLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng xuất");
        builder.setMessage("Bạn chắc chắn muốn đăng xuất?");
        builder.setCancelable(true);
        builder.setPositiveButton("Huỷ", (dialog, which) -> dialog.dismiss());

        builder.setNegativeButton("Đăng xuất", (dialog, which) -> {
            dialog.dismiss();
            handleLogout();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * clear all the data of current user, include his/her schedule, information like name and faculity,....
     */

    private void clearCache() {
        SharedPreferencesManager.clearCache();
        LocalScheduleAPI.clearCache();
        TodayFragment.clearCache();
        ScheduleFragment.clearCache();
    }

    /**
     * handle the logout task, first, it create a LogoutAsyncTask.
     * If logout success, it will clear all the user information by calling
     * clearCache() method and also clear user account by calling
     * removeUserAccount() method from SharedPreferencesManager class,
     * than it start login Activity
     * If logout fail, it will show a notification to user
     */

    private void handleLogout() {
        //start activity indicator

        LogoutAsyncTask logoutAsyncTask = new LogoutAsyncTask(new LogoutCompletionHandler() {
            @Override
            public void onLogoutSucceed() {
                SharedPreferencesManager.removeUserAccount();
                clearCache();
                startLoginActivity();
            }

            @Override
            public void onLogoutFailed() {
                //end activity indicator
                Toast.makeText(SettingsActivity.this, "Đăng xuất thất bại, xin vui lòng thử lại sau.", Toast.LENGTH_SHORT);
            }
        });
        logoutAsyncTask.execute();
    }

    /**
     * Start LoginActivity
     */

    private void startLoginActivity() {
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //destroy this activity
        finish();
    }
}

