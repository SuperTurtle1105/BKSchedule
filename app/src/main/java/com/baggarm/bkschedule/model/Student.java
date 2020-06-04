package com.baggarm.bkschedule.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class contains username, password of account myBK of student,
 * avatar, Faculty name and avatar of student.
 *
 * @author PXThanhLam
 * @version 2019.1406
 * @since 1.0
 */
public class Student {
    private static final String TAG = Student.class.getName();

    private static Student INSTANCE;
    public String username = "";
    public String password = "";
    public String avtBase64Str = "";
    public Bitmap avtBitmap;
    public String email = "";
    public String name = "";
    public String faculty = "";

    private Student() {
    }

    public static Student getCurrentUserAccount() {
        if (INSTANCE == null) {
            INSTANCE = new Student();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public void setUsernameAndPasswordFromJson(JSONObject json) {
        try {
            username = json.getString("username");
            password = json.getString("password");
            email = username + "@hcmut.edu.vn";
        } catch (JSONException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public String getAvtBase64Str() {
        return avtBase64Str;
    }

    public void setAvtBase64Str(String str) {
        avtBase64Str = str;
        final String pureBase64Encoded = avtBase64Str.substring(avtBase64Str.indexOf(",") + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        avtBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public void setUsernameAndPassword(String username, String password) {
        this.username = username;
        this.password = password;
        email = username + "@hcmut.edu.vn";
    }
}
