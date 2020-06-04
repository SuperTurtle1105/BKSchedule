package com.baggarm.bkschedule;

import android.app.Application;

import com.baggarm.bkschedule.helper.SharedPreferencesManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //set APPLICATION_CONTEXT to use for all methods need context
        SharedPreferencesManager.APPLICATION_CONTEXT = getApplicationContext();
    }

}
