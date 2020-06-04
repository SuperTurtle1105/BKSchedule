/*
 * @startuml
 * */
package com.baggarm.bkschedule.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.controller.person.PersonFragment;
import com.baggarm.bkschedule.controller.sampleTest.SampleTestFragment;
import com.baggarm.bkschedule.controller.schedule.ScheduleFragment;
import com.baggarm.bkschedule.controller.sharedSchedule.SharedScheduleFragment;
import com.baggarm.bkschedule.controller.today.TodayFragment;
import com.baggarm.bkschedule.helper.SharedPreferencesManager;
import com.baggarm.bkschedule.model.Student;

/**
 * Main Activity of this application, contains bottom navigation view to navigate between fragments
 *
 * @author nguyenBaoHuy
 * @version 2019.1306
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Debug Tag for use logging debug output to Log Cat.
     *
     * @since 1.0
     */
    final static String TAG = MainActivity.class.getName();

    final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 24598;

    /**
     * A view that contains some items to navigate between fragments in this activity.
     *
     * @since 1.0
     */
    private BottomNavigationView bottomNav;

    /**
     * Current selected menu item of bottomNav, default = -1 (unselected).
     *
     * @see #bottomNav
     * @since 1.0
     */
    private int currentSelectedMenuItemId = -1;

    /**
     * A listener that listens for user tap to menu item.
     * This will check if user selected whether a different item from current item or not.
     * It it is, a new fragment will be create and show up.
     *
     * @since 1.0
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (currentSelectedMenuItemId == itemId)
                    return true;
                switch (itemId) {
                    case R.id.nav_schedule:
                        selectedFragment = new ScheduleFragment();
                        break;
                    case R.id.nav_hoc_chui:
                        selectedFragment = new SharedScheduleFragment();
                        break;
                    case R.id.nav_today:
                        selectedFragment = new TodayFragment();
                        break;
                    case R.id.nav_test:
                        selectedFragment = new SampleTestFragment();
                        break;
                    case R.id.nav_person:
                        selectedFragment = new PersonFragment();
                        break;
                }
                currentSelectedMenuItemId = item.getItemId();
                getSupportFragmentManager().beginTransaction().replace(R.id.Container,
                        selectedFragment).commit();
                return true;
            };

    /**
     * Binding views from layout, add listener for bottomNav and get UserInfo data that saved in Shared Preferences
     * and set it to CurrentUserAccount, if couldn't get data from Shared Preferences, an jSONException will be throw
     * and handles after that.
     *
     * @param savedInstanceState
     * @see SharedPreferencesManager
     * @see Student
     * @since 1.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        bottomNav.setSelectedItemId(R.id.nav_today);

        requestPermissions();
    }

    /**
     * Handle back button pressing event. If selected fragment is SampleTestFragment, it will check if
     * can go back or not. If can not go back, set select to TodayFragment.
     * If selected fragment is not SampleTestFragment, it will check if selected fragment is TodayFragment or not.
     * If not, set select to TodayFragment, otherwise, call super method.
     *
     * @see TodayFragment
     * @see SampleTestFragment
     * @since 1.0
     */
    @Override
    public void onBackPressed() {
        int seletedItemId = bottomNav.getSelectedItemId();
        if (seletedItemId == R.id.nav_test) {
            SampleTestFragment fragment = (SampleTestFragment)
                    getSupportFragmentManager().findFragmentById(R.id.Container);
            if (fragment.canGoBack()) {
                fragment.goBack();
            } else {
                bottomNav.setSelectedItemId(R.id.nav_today);
            }
        } else {
            if (R.id.nav_today != seletedItemId) {
                bottomNav.setSelectedItemId(R.id.nav_today);
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     * Request app permissions
     *
     * @since 1.1.6
     */
    public void requestPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    /**
     * Handle result of app permissions request
     *
     * @since 1.1.6
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
