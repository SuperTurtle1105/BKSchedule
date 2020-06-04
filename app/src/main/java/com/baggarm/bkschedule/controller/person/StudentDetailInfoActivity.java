package com.baggarm.bkschedule.controller.person;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.baggarm.bkschedule.model.Student;
import com.github.ybq.android.spinkit.style.Wave;

public class StudentDetailInfoActivity extends AppCompatActivity {

    /**
     * Web view shows infomation of Student.
     *
     * @since 1.0
     */
    WebView webView;

    /**
     * ProgressBar to shows animation when page is loading.
     *
     * @since 1.0
     */
    ProgressBar progressBar;

    /**
     * Title of item
     *
     * @since 1.0
     */
    TextView txtTitle;

    private int numLoad = 0;

    /**
     * Load web view which item user want to see.
     *
     * @param savedInstanceState
     * @since 1.0
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail_info);

        txtTitle = findViewById(R.id.txt_title);
        webView = findViewById(R.id.Webview);
        progressBar = findViewById(R.id.xd_progressbar);
        Wave wave = new Wave();

        wave.setColor(ContextCompat.getColor(this, R.color.black));
        progressBar.setIndeterminateDrawable(wave);

        final int index = PersonFragment.index;
        switch (index) {
            case 0: {
                txtTitle.setText("Điểm trung bình");
                break;
            }
            case 1: {
                txtTitle.setText("Tiến trình học tập");
                break;
            }
            case 2: {
                txtTitle.setText("Số tín chỉ");
                break;
            }
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (!(networkInfo != null && networkInfo.isConnected())) {
            Toast.makeText(StudentDetailInfoActivity.this, "Unconnected", Toast.LENGTH_SHORT).show();
            return;
        }

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false; // then it is not handled by default action
            }
        });

        final String ids = PersonFragment.ids;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 " + "(KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        webView.getSettings().setUserAgentString(userAgent);

        webView.setVisibility(View.INVISIBLE);
        webView.loadUrl("http://www.aao.hcmut.edu.vn/stinfo/logout");
        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView v, String url) {
                        numLoad += 1;
                        setLoadAnimation(true);

                        webView.getSettings().setUserAgentString(userAgent);
                        webView.loadUrl("javascript:(function() { " +
                                "d=document.getElementById('username');d.value='" + Student.getCurrentUserAccount().username + "' ;" +
                                "})()");
                        webView.loadUrl("javascript:(function() { " +
                                "dp=document.getElementById('password');dp.value='" + Student.getCurrentUserAccount().password + "'; " +
                                "})()");
                        webView.loadUrl("javascript:(function() { " +
                                "document.getElementsByClassName('btn-submit')[0].click(); " +
                                "})()");
                        if (numLoad == 2) {
                            webView.loadUrl("javascript:(function() { " +
                                    "document.getElementsByClassName('header-wrapper')[0].style.background='#f8f8f8'; " +
                                    "})()");
                            webView.loadUrl("javascript:(function() { " +
                                    "document.getElementsByClassName('row')[0].style.display='none'; " +
                                    "})()");
                            webView.loadUrl("javascript:(function() { " +
                                    "document.getElementsByClassName('sidebar isClose')[0].style.display='none'; " +
                                    "})()");
                            webView.loadUrl("javascript:(function() { " +
                                    "document.getElementsByClassName('footer-wp')[0].style.background='#f8f8f8'; " +
                                    "})()");
                            webView.loadUrl("javascript:(function() { " +
                                    "document.getElementsByClassName('breadcrumb-wp')[0].style.display='none'; " +
                                    "})()");
                            webView.loadUrl("javascript:(function() { " +
                                    "document.getElementsByClassName('jqx-tabs-headerWrapper jqx-tabs-header jqx-widget-header jqx-rc-t')[0].style.display='none'; " +
                                    "})()");
                            webView.loadUrl("javascript:(function() { " +
                                    "document.getElementsByClassName('row home-content-padding')[1].style.display='none'; " +
                                    "})()");
                            String s1 = "javascript:(function() { document.getElementById('";
                            webView.loadUrl(s1 + ids + "' ).click(); })()");
                            numLoad = 0;

                            setLoadAnimation(false);
                        }
                    }
                }
        );

    }

    /**
     * Show load animation when web haven't loaded successfully
     * and hide when it finished.
     *
     * @param isStart
     * @since 1.0
     */
    void setLoadAnimation(Boolean isStart) {
        if (isStart) {
            progressBar.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
            return;
        }
        progressBar.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.VISIBLE);
    }

}

