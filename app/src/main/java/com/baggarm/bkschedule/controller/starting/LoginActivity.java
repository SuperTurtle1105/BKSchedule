package com.baggarm.bkschedule.controller.starting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.api.rest.asyncTask.LoginAsyncTask;
import com.baggarm.bkschedule.api.rest.asyncTask.LoginCompletionHandler;
import com.baggarm.bkschedule.api.rest.asyncTask.ResultCode;
import com.baggarm.bkschedule.controller.MainActivity;
import com.baggarm.bkschedule.controller.person.settings.SettingPolicyActivity;
import com.baggarm.bkschedule.controller.person.settings.SettingTermsActivity;
import com.baggarm.bkschedule.helper.NetworkHelper;
import com.baggarm.bkschedule.helper.SharedPreferencesManager;
import com.baggarm.bkschedule.model.Student;
import com.github.ybq.android.spinkit.style.Wave;

/**
 * This activity shows login UI, and handle login task.
 *
 * @author IMBAGGAARM
 * @version 2019.1206
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Debug Tag for use logging debug output to LogCat
     *
     * @since 1.0
     */
    private final String TAG = LoginActivity.class.getName();
    /**
     * Username EditText, user will input their MyBK username.
     *
     * @since 1.0
     */
    public EditText editTextUsername;
    /**
     * Login button, start login task when user taps on
     *
     * @since 1.0
     */
    private Button btnLogin;
    /**
     * Password EditText, user will input their MyBk password, inputType = password.
     *
     * @since 1.0
     */
    private EditText editTextPassword;

    /**
     * Progress Bar to show loading animation
     *
     * @since 1.0
     */
    private ProgressBar progressBar;

    /**
     * Privacy Policy TextView to show text that user already read and accepted the Terms Of Use and Privacy Policy when user clicks btnLogin
     * User can tap on "Terms" text and "Privacy Policy" text to read Terms or Privacy Policy.
     *
     * @see #customTextView(TextView) custom span text for this textView.
     * @since 1.0
     */
    private TextView txtPrivacyPolicy;

    /**
     * Binding views, custom Wave animation for progressBar,
     * add TextWatcher to editTextUsername and editTextPassword to check whether user input enough username and password to login or not.
     *
     * @param savedInstanceState
     * @see Wave
     * @see #checkReadyToLogin()
     * @since 1.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_Login);
        editTextUsername = findViewById(R.id.edit_txt_userName);
        editTextPassword = findViewById(R.id.edit_txt_password);
        txtPrivacyPolicy = findViewById(R.id.txt_privacy_policy);
        progressBar = findViewById(R.id.progress_bar);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        customTextView(txtPrivacyPolicy);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkReadyToLogin();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        //set listeners
        btnLogin.setOnClickListener(this);
        editTextPassword.addTextChangedListener(textWatcher);
        editTextUsername.addTextChangedListener(textWatcher);
    }

    /**
     * Check if user input both username and password, if it is true, btnLogin will be enabled,
     * otherwise, btnLogin will be disabled.
     *
     * @see #setBtnLoginEnabledState(boolean)
     * @since 1.0
     */
    private void checkReadyToLogin() {
        if (!editTextPassword.getText().toString().isEmpty() && !editTextUsername.getText().toString().isEmpty()) {
            //setEnabled btnLogin to true
            setBtnLoginEnabledState(true);
        } else {
            //setEnabled btnLogin to false
            setBtnLoginEnabledState(false);
        }
    }

    /**
     * Set state of btnLogin, if btnLogin is enabled,
     * background of btnLogin is a blue rounded corner rectangle,
     * otherwise, background of btnLogin is a gray one.
     *
     * @param isEnabled
     * @since 1.0
     */
    private void setBtnLoginEnabledState(boolean isEnabled) {
        if (isEnabled) {
            btnLogin.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.rounded_corner_blue));
        } else {
            btnLogin.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.rounded_corner_gray));
        }
        btnLogin.setEnabled(isEnabled);
    }

    /**
     * Set enabled state of views in this screen, if views are not enabled, user can not click any buttons or focus any text views.
     * The purpose of this method is to prevent user tap btnLogin multiple times or edit text views when login task is ongoing.
     *
     * @param isEnabled
     */
    private void setEnabledViews(boolean isEnabled) {
        btnLogin.setEnabled(isEnabled);
        editTextUsername.setEnabled(isEnabled);
        editTextPassword.setEnabled(isEnabled);
        btnLogin.setText(isEnabled ? "Đăng nhập" : "");
        progressBar.setVisibility(isEnabled ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * Handle onClick event of btnLogin.
     * The first thing this method does is calling method setEnabledViews to disable all views.
     * After that, it will check the internet connection, if network is available, a LoginAsyncTask will be execute.
     * If network is not available, alertLoginFailed will be called with resultCode = INTERNET_CONNECTION_FAILED.
     * <p>
     * This method also handles the result of login task, there are 3 cases can happen:
     * <ul>
     * <li>Login successfully, set new value of UserInfo and UserAccount to Shared Preferences</li>
     * <li>Login failed because of server, alertLoginFailed will be called with resultCode = SERVER_FAILED</li>
     * <li>Login failed because of account, alertLoginFailed will be called with resultCode = ACCOUNT_FAILED</li>
     * </ul>
     *
     * @param view a view that called this method
     * @see #setEnabledViews(boolean)
     * @see #alertLoginFailed(ResultCode)
     * @see LoginAsyncTask
     * @see ResultCode
     * @since 1.0
     */
    @Override
    public void onClick(View view) {
        if (!view.equals(btnLogin))
            return;

        //set enabled to false to prevent user tap ui when signing
        setEnabledViews(false);

        Student student = Student.getCurrentUserAccount();
        student.username = editTextUsername.getText().toString().toLowerCase();
        student.password = editTextPassword.getText().toString();

        if (NetworkHelper.isNetworkAvailable(this)) {
            //call loginAndGetData
            LoginAsyncTask asyncTask = new LoginAsyncTask(student.username, student.password, new LoginCompletionHandler() {
                @Override
                public void handleLoginSucceed() {
                    //MainActivity
                    SharedPreferencesManager.setUserInfo(student.name, student.faculty, student.avtBase64Str);
                    SharedPreferencesManager.setUserAccount(student.username, student.password);
                    startMainActivity();
                }

                @Override
                public void handleLoginFailedServer() {
                    alertLoginFailed(ResultCode.SERVER_FAILED);
                }

                @Override
                public void handleLoginFailedAccount() {
                    alertLoginFailed(ResultCode.ACCOUNT_FAILED);
                }
            });
            //execute task
            asyncTask.execute();
        } else {
            //make a toast
            alertLoginFailed(ResultCode.INTERNET_CONNECTION_FAILED);
        }
    }

    /**
     * Start MainActivity, after that, destroy this activity immediately.
     *
     * @see MainActivity
     * @since 1.0
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //destroy activity
        finish();
    }

    /**
     * This method is used to covert resultCode to a message, and toast that message to user.
     * After toast the message to user, setEnabledViews will be called to enable all views.
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
     * @see #setEnabledViews(boolean)
     * @since 1.0
     */
    private void alertLoginFailed(ResultCode resultCode) {
        String message = "";
        switch (resultCode) {
            case SERVER_FAILED:
                message = "Không thể kết nối tới server. Xin thử lại sau.";
                break;
            case ACCOUNT_FAILED:
                message = "Tài khoản hoặc mật khẩu không chính xác.";
                break;
            case INTERNET_CONNECTION_FAILED:
                message = "Kết nối Internet hiện tại không hoạt động. Xin thử lại sau";
                break;
            default:
                break;
        }
        Toast.makeText(SharedPreferencesManager.APPLICATION_CONTEXT, message, Toast.LENGTH_SHORT).show();
        setEnabledViews(true);
    }

    /**
     * This method is used to custom txtPrivacyPolicy, to make "Terms" and "Privacy Policy" text
     * can clickable, and calls startSettingTerms or startSettingPolicy.
     *
     * @param view an instance of TextView that will be custom.
     * @see #startSettingTerms()
     * @see #startSettingPolicy()
     * @since 1.0
     */
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "Với việc bấm vào nút Đăng nhập, bạn đã đọc và đồng ý với ");
        spanTxt.append("Điều khoản sử dụng");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startSettingTerms();
            }
        }, spanTxt.length() - "Điều khoản sử dụng".length(), spanTxt.length(), 0);
        spanTxt.append(" và ");
        spanTxt.append("Chính sách về Quyền riêng tư");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startSettingPolicy();
            }
        }, spanTxt.length() - "Chính sách về Quyền riêng tư".length(), spanTxt.length(), 0);
        spanTxt.append(" của chúng tôi.");
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    /**
     * This method will start SettingTermActivity
     *
     * @see SettingTermsActivity
     * @since 1.0
     */
    private void startSettingTerms() {
        Intent intent = new Intent(LoginActivity.this, SettingTermsActivity.class);
        startActivity(intent);
    }

    /**
     * This method will start SettingPolicyActivity
     *
     * @see SettingPolicyActivity
     * @since 1.0
     */
    private void startSettingPolicy() {
        Intent intent = new Intent(LoginActivity.this, SettingPolicyActivity.class);
        startActivity(intent);
    }

    /**
     * This method is used to log username and password that user input to Log Cat.
     */
    private void logInput() {
        Log.d(TAG, "btnLogin clicked");
        Log.d(TAG, "username: " + editTextUsername.getText());
        Log.d(TAG, "password: " + editTextPassword.getText());
    }
}
