package com.baggarm.bkschedule.controller.person.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.baggarm.bkschedule.R;

/**
 * This activity shows Information of this app
 *
 * @author PXThanhLam
 * @version 2019.1306
 * @since 1.0
 */
public class SettingTeamInfoActivity extends AppCompatActivity {

    /**
     * Contact button, which show the email's UI for user to contact with the author
     * when user taps on it
     */
    Button btnContact;

    /**
     * SendEmail button, which show the email's UI for user to contact with the author
     * when user taps on it
     */
    Button btnSendEmail;

    /**
     * Set title for toolbar to show,
     * show the email's UI for user to contact with the author
     * when user taps on btnContact or btnSendEmail
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_team_info);

        setTitle("Thông tin ứng dụng");
        Toolbar toolbar = findViewById(R.id.mtoolbar_setting_team_info);
        setSupportActionBar(toolbar);

        btnContact = findViewById(R.id.btn_contact);
        btnContact.setText(R.string.contact_email);
        btnContact.setOnClickListener(v -> {
            //open send email
            openSendEmail();
        });

        btnSendEmail = findViewById(R.id.btn_send_email);
        btnSendEmail.setOnClickListener(v -> {
            //open send email
            openSendEmail();
        });
    }

    /**
     * Let the user to choose the application to send the report email,
     * then it show the UI for user to send report
     */
    void openSendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.contact_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "LIÊN HỆ");
        startActivity(Intent.createChooser(intent, "Chọn ứng dụng mail để gửi..."));
    }
}
