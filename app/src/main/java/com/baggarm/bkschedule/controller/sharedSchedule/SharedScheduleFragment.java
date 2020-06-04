package com.baggarm.bkschedule.controller.sharedSchedule;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.baggarm.bkschedule.R;
import com.github.ybq.android.spinkit.style.Wave;

/**
 * This web view fragment shows shared schedule.
 *
 * @author nguyenBaoHuy
 * @version 2019.1306
 * @since 1.0
 */
public class SharedScheduleFragment extends Fragment {
    /**
     * Url address of web view.
     *
     * @since 1.0
     */
    private final String url = "https://docs.google.com/spreadsheets/d/1TQE7jMHUqrI5mZ8mly4t8L9kxwilJS2y-2Ua0VJzQVw/edit?usp=drive_open&ouid=0";
    /**
     * Web view shows shared schedule.
     *
     * @since 1.0
     */
    private WebView webView;
    /**
     * Fragment view.
     *
     * @since 1.0
     */
    private View view;
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
     * Create and return view associated with the Shared Schedule Fragment,
     * load Shared Schedule web and cache web.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hoc_chui, null);
        getActivity().setTitle("Lịch học chung");

        webView = view.findViewById(R.id.web_view);

        progressBar = view.findViewById(R.id.progress_bar_share_schedules);
        Wave wave = new Wave();
        progressBar.setIndeterminateDrawable(wave);
        wave.setColor(ContextCompat.getColor(getContext(), R.color.black));

        webView.setWebViewClient(new AppWebViewClients());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.loadUrl(url);
        return view;
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
