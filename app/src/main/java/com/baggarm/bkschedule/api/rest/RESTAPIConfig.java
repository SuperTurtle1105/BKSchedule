package com.baggarm.bkschedule.api.rest;

import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;

/**
 * Configuration for RestAPI, configure cookie manager, also delete cookies
 *
 * @author IMBAGGAARM
 * @version 2019.1706
 * @since 1.0
 */
public class RESTAPIConfig {
    //singleton
    private static final RESTAPIConfig ourInstance = new RESTAPIConfig();
    public OkHttpClient client;
    private CookieManager cookieManager;

    private RESTAPIConfig() {

        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar((cookieManager)))
                .build();

    }

    public static RESTAPIConfig getInstance() {
        return ourInstance;
    }

    public void deleteCookie() {
        cookieManager.getCookieStore().removeAll();
    }
}
