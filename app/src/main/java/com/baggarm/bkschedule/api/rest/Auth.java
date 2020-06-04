package com.baggarm.bkschedule.api.rest;

import android.support.annotation.NonNull;
import android.util.Log;

import com.baggarm.bkschedule.api.firebase.FIRAPI;
import com.baggarm.bkschedule.api.firebase.FIRSemesters;
import com.baggarm.bkschedule.api.rest.asyncTask.GetScheduleCompletionHandler;
import com.baggarm.bkschedule.api.rest.asyncTask.LoginCompletionHandler;
import com.baggarm.bkschedule.api.rest.asyncTask.ResultCode;
import com.baggarm.bkschedule.helper.SharedPreferencesManager;
import com.baggarm.bkschedule.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * This class is used to handle login to BK Server, handle load needed data
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class Auth {
    //singleton
    private static final Auth ourInstance = new Auth();

    /**
     * Tag to debug
     *
     * @since 1.0
     */
    private static final String TAG = Auth.class.getName();

    /**
     * JsonWebToken was returned from server
     *
     * @since 1.0
     */
    static String JWTToken = "";

    /**
     * CASTGC cookie to authenticate with server
     *
     * @since 1.0
     */
    private static String CASTGC = "";

    /**
     * JSessionID cookie to authenticate with server
     *
     * @since 1.0
     */
    private static String JSessionID = "";

    /**
     * BKU login url
     *
     * @since 1.0
     */
    private final String loginURL = "https://sso.hcmut.edu.vn/cas/login";

    /**
     * BKU logout url
     *
     * @since 1.0
     */
    private final String logoutURL = "https://sso.hcmut.edu.vn/cas/logout";

    /**
     * BKU student info url
     *
     * @since 1.0
     */
    private final String stinfoURL = "http://www.aao.hcmut.edu.vn/stinfo/";

    private String username;

    private String password;

    private boolean isGetSemesterCompleted = false;

    private boolean isGetSchedulesCompleted = false;

    private Auth() {
    }

    public static Auth getInstance() {
        return ourInstance;
    }

    /**
     * Logout from current account
     *
     * @return whether logout successful or not
     * @since 1.0
     * @since 1.0
     */
    public boolean logout() {
        try {
            URL logoutEndpoint = new URL(logoutURL);
            OkHttpClient client = RESTAPIConfig.getInstance().client;

            Request request = new Request.Builder()
                    .url(logoutEndpoint)
                    .build();

            Response response = client.newCall(request).execute();
            int statusCode = response.code();
            response.close();
            if (statusCode == 200) {
                return true;
            }
            return false;
        } catch (Exception er) {
            //handle error
            Log.d(TAG, er.getLocalizedMessage());
            er.printStackTrace();
            return false;
        }
    }

    /**
     * Login to server, after login succeed, call load student info data, schedules, semesters data.
     * After get all data or failed at a step, completionHandler will be called.
     *
     * @param username
     * @param password
     * @param completionHandler
     * @since 1.0
     */
    public void loginAndGetData(String username, String password, LoginCompletionHandler completionHandler) {

        //assigns values
        this.username = username;
        this.password = password;

        //logout first
        boolean logoutResult = logout();
        if (!logoutResult) {
            completionHandler.handleLoginFailedServer();
            return;
        }
        //loginAndGetData
        try {

            OkHttpClient client = RESTAPIConfig.getInstance().client;

            Request request = new Request.Builder()
                    .url(loginURL)
                    .build();

            Response response = client.newCall(request).execute();

            String body = response.body().string();
            JSessionID = response.headers().get("Set-Cookie");
            String loginTicket = getLoginTicket(body);
            String execution = getExecution(body);

            if (loginTicket == "" || execution == "") {
                completionHandler.handleLoginFailedServer();
                return;
            }

            response.close();

            ResultCode resultCode = requestLogin(loginTicket, execution);
            switch (resultCode) {
                case SUCCEED:
                    FIRSemesters firSemesters = FIRAPI.SEMESTERS;
                    firSemesters.getSemesters(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //complete
                            isGetSemesterCompleted = true;
                            if (isGetSchedulesCompleted && isGetSemesterCompleted) {
                                completionHandler.handleLoginSucceed();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            //use old fire base database
                            isGetSemesterCompleted = true;
                            if (FIRAPI.SEMESTERS.getSemesters() != null) {
                                if (isGetSchedulesCompleted && isGetSemesterCompleted) {
                                    //complete
                                    completionHandler.handleLoginSucceed();
                                }
                            } else {
                                completionHandler.handleLoginFailedServer();
                            }
                        }
                    });

                    ScheduleAPI.getInstance().getSchedules(client, new GetScheduleCompletionHandler() {
                        @Override
                        public void onGetDataSucceed() {
                            isGetSchedulesCompleted = true;
                            //set username and password
                            Student.getCurrentUserAccount().setUsernameAndPassword(username, password);
                            //SharedPreferencesManager.
                            if (isGetSchedulesCompleted && isGetSemesterCompleted) {
                                completionHandler.handleLoginSucceed();
                            }
                        }

                        @Override
                        public void onGetDataFailed() {
                            isGetSchedulesCompleted = true;
                            if (isGetSchedulesCompleted && isGetSemesterCompleted) {
                                completionHandler.handleLoginFailedServer();
                            }
                        }
                    });
                    break;
                case ACCOUNT_FAILED:
                    completionHandler.handleLoginFailedAccount();
                    break;
                default:
                    //failed
                    completionHandler.handleLoginFailedServer();
            }
        } catch (Exception er) {
            //handle error
            Log.d(TAG, er.getLocalizedMessage());
            er.printStackTrace();
            completionHandler.handleLoginFailedServer();
        }
    }

    /**
     * Call request login with POST method to server
     *
     * @param lt  login ticket
     * @param exe execution
     * @return
     * @since 1.0
     */
    private ResultCode requestLogin(String lt, String exe) {

        try {

            OkHttpClient client = RESTAPIConfig.getInstance().client;

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "username=" + username + "&password=" + password + "&lt=" + lt + "&execution=" + exe + "&_eventId=submit");
            Request request;
            if (JSessionID != null) {
                request = new Request.Builder()
                        .url(loginURL)
                        .post(body)
                        .addHeader("cache-control", "no-cache")
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .addHeader("cookie", JSessionID)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(loginURL)
                        .post(body)
                        .addHeader("cache-control", "no-cache")
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .build();
            }

            Response response = client.newCall(request).execute();

            CASTGC = response.headers().get("Set-Cookie");
            response.close();
            if (CASTGC != null && !CASTGC.isEmpty()) {
                ResultCode resultCode = getTokenAndProfile(CASTGC);
                return resultCode;
            } else {
                return ResultCode.ACCOUNT_FAILED;
            }
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return ResultCode.SERVER_FAILED;
        }
    }

    /**
     * Get user info and Json web token
     *
     * @param CASTGC cookie to authenticate
     * @return
     * @since 1.0
     */
    private ResultCode getTokenAndProfile(String CASTGC) {
        try {

            OkHttpClient client = RESTAPIConfig.getInstance().client;

            Request request = new Request.Builder()
                    .url(stinfoURL)
                    .addHeader("cookie", CASTGC)
                    .build();

            Response response = client.newCall(request).execute();

            String body = response.body().string();

            response.close();

            JWTToken = getTokenFromCSS(body);
            getProfile(body);

            if (JWTToken != null && !JWTToken.isEmpty()) {
                return ResultCode.SUCCEED;
            } else {
                return ResultCode.SERVER_FAILED;
            }
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return ResultCode.SERVER_FAILED;
        }
    }

    /**
     * Get jwt token from html file
     *
     * @param content html file
     * @return json web token
     * @since 1.0
     */
    private String getTokenFromCSS(String content) {
        String start = "<meta name=\"_token\" content=\"";
        String end = "\" />";

        return regexGetStringBetween(content, start, end);
    }

    /**
     * Get login ticket from login html file
     *
     * @param content html file
     * @return login ticket
     * @since 1.0
     */
    private String getLoginTicket(String content) {
        String start = "<input type=\"hidden\" name=\"lt\" value=\"";
        String end = "\" />";

        return regexGetStringBetween(content, start, end);
    }

    /**
     * Get execution from login html file
     *
     * @param content
     * @return
     * @since 1.0
     */
    private String getExecution(String content) {
        String start = "<input type=\"hidden\" name=\"execution\" value=\"";
        String end = "\" />";

        return regexGetStringBetween(content, start, end);
    }

    /**
     * Get user info from html file
     *
     * @param result htlm file
     * @since 1.0
     */
    private void getProfile(String result) {
        try {
            Document docs = Jsoup.parse(result);

            Elements imgs = docs.getElementsByClass("top-avatar1").select("img");
            String avtBase64Str = "";
            for (Element element : imgs) {
                avtBase64Str = element.getElementsByAttribute("src").attr("src");
                break;
            }

            Elements es = docs.getElementsByClass("top-avatar2").select("p");
            String ten_khoa = "";
            for (Element element : es) {
                ten_khoa = element.text();
                break;
            }

            Elements tens = docs.getElementsByClass("top-avatar2").select("div");
            String ten = "";
            for (Element element : tens) {
                ten = element.text();
                ten = ten.replace(ten_khoa, "");
                break;
            }

            String studentName = ten;
            String studentFaculty = ten_khoa;
            String studentAvatarBase64Str = avtBase64Str;

            Student student = Student.getCurrentUserAccount();
            student.name = studentName;
            student.faculty = studentFaculty;
            student.setAvtBase64Str(studentAvatarBase64Str);

            SharedPreferencesManager.setUserInfo(studentName, studentFaculty, studentAvatarBase64Str);
        } catch (Exception e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get string between start and end string in a content with regex
     *
     * @param content
     * @param start
     * @param end
     * @return the string between
     * @since 1.0
     */
    private String regexGetStringBetween(String content, String start, String end) {
        String format = ".*" + start + "(.*)" + end + ".*";
        Pattern pattern = Pattern.compile(format);

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
