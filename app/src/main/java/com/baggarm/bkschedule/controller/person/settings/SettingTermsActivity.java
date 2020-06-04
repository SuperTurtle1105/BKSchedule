package com.baggarm.bkschedule.controller.person.settings;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.baggarm.bkschedule.R;
import com.github.ybq.android.spinkit.style.Wave;

/**
 * This activity shows  application's Terms
 *
 * @author PXThanhLam
 * @version 2019.1306
 * @since 1.0
 */

public class SettingTermsActivity extends AppCompatActivity {

    /**
     * the string which contain the datapath's name to a html file
     * that contain the application's terms
     *
     * @since 1.0
     */
    private final String url = "https://bkuschedule.web.app/terms.html";
    /**
     * A WebView to show policy string
     *
     * @since 1.0
     */
    private WebView webView;
    /**
     * ProgressBare to show loading animation.
     *
     * @since 1.0
     */
    private ProgressBar progressBar;
    /**
     * Boolean Variable is used to check web loading have finished yet
     *
     * @since 1.0
     */
    private boolean loadingFinished = true;

    /**
     * Boolean variable is used to hide ProgressBar and show WebView if it is true.
     *
     * @since 1.0
     */
    private boolean redirect = false;

    /**
     * set title for toolbar to show,
     * load the content in the html file to the webview and show it
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_terms);
        setTitle("Điều khoản sử dụng");

        Toolbar toolbar = findViewById(R.id.mtoolbar_setting_terms);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progress_bar);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        wave.setColor(ContextCompat.getColor(this, R.color.appColor));

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new AppWebViewClients());
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);

        webView.loadUrl(url);
    }

    /**
     * Add ProgressBar in class WebViewClient
     *
     * @since 1.0
     */
    public class AppWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!loadingFinished) {
                redirect = true;
            }
            loadingFinished = false;
            view.loadUrl(url);
            return true;
        }

        /**
         * Show ProgressBar while loading web view
         *
         * @param view
         * @param url
         * @param favicon
         * @since 1.0
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loadingFinished = false;
            //SHOW LOADING IF IT ISNT ALREADY VISIBLE
            webView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Hide ProgressBar when finish loading web view
         *
         * @param view
         * @param url
         * @since 1.0
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            if (!redirect) {
                loadingFinished = true;
                //HIDE LOADING IT HAS FINISHED
                progressBar.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
            } else {
                redirect = false;
            }
            super.onPageFinished(view, url);
        }
    }
}

