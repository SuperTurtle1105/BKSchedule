package com.baggarm.bkschedule.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class was created in order to detect the internet connection of device
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class NetworkHelper {

    /**
     * Check if network is available
     *
     * @param activity
     * @return
     * @since 1.0
     */
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
