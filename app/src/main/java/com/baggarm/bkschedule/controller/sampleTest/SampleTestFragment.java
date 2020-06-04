package com.baggarm.bkschedule.controller.sampleTest;

import android.Manifest;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baggarm.bkschedule.R;
import com.github.ybq.android.spinkit.style.Wave;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * This web view fragment shows Sample Test.
 *
 * @author nguyenBaoHuy
 * @version 2019.1306
 * @since 1.0
 */
public class SampleTestFragment extends Fragment {
    //TAG

    /**
     * Regex used to parse content-disposition headers
     */
    private static final Pattern CONTENT_DISPOSITION_PATTERN =
            Pattern.compile("(.*)filename(.*)=\"(.*)\"(.*)",
                    Pattern.CASE_INSENSITIVE);
    /**
     * Debug Tag for use debug output to LogCat
     *
     * @since 1.0
     */
    private final String TAG = SampleTestFragment.class.getName();
    /**
     * Url address of web view.
     *
     * @since 1.0
     */
    private final String url = "https://sites.google.com/site/thuvientailieubachkhoa/";
    /**
     * Boolean Variable is used to check web loading have finished yet
     *
     * @since 1.0
     */
    boolean isLoadingFinished = false;

    //UI
    /**
     * Boolean variable is used to hide ProgressBar and show WebView if it is true.
     *
     * @since 1.0
     */
    boolean redirect = false;
    /**
     * Web view shows Sample Test.
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
     * Create and return view associated with the Shared Schedule Fragment,
     * load Shared Schedule web and download file.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test, null);
        getActivity().setTitle("Đề thi mẫu");

        webView = view.findViewById(R.id.web_view);
        webView.setWebViewClient(new AppWebViewClients());
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        progressBar = view.findViewById(R.id.progress_bar_test);
        Wave wave = new Wave();
        wave.setColor(ContextCompat.getColor(getContext(), R.color.black));
        progressBar.setIndeterminateDrawable(wave);

        //webview start load page
        webView.loadUrl(url);

        //set download listener if user press download in webview
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {

            if (ContextCompat.checkSelfPermission(this.getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this.getActivity(), "Bạn chưa cấp quyền lưu trữ trên thiết bị cho ứng dụng. Hãy cấp quyền cho ứng dụng trong cài đặt.", Toast.LENGTH_LONG).show();
                return;
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            final String filename = guessFileName(contentDisposition);
            Log.d(TAG, filename);
            //if could not guess the filename, alert error.
            if (filename == null) {
                Toast.makeText(getActivity(), "Không thể tải file",
                        Toast.LENGTH_LONG).show();
                return;
            }
            //start download file
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
            DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
            dm.enqueue(request);
            //toast for user that file is being downloaded
            Toast.makeText(getActivity(), "Đang tải file", Toast.LENGTH_LONG).show(); //To notify the Client that the file is being downloaded
        });

        return view;
    }

    /**
     * Cache web view
     *
     * @since 1.0
     */
    @Override
    public void onStart() {
        super.onStart();
        enableHTML5AppCache();
    }

    /**
     * Save web view sate
     *
     * @param outState
     * @since 1.0
     */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    /**
     * Restore web view state
     *
     * @param savedInstanceState
     * @since 1.0
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    /**
     * Check web view can go back
     *
     * @return boolean
     */
    public boolean canGoBack() {
        return webView.canGoBack();
    }

    /**
     * Go back
     *
     * @since 1.0
     */
    public void goBack() {
        webView.goBack();
    }

    /**
     * Enable html cache
     *
     * @since 1.0
     */
    private void enableHTML5AppCache() {
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCachePath("/data/data/" + getActivity().getPackageName() + "/cache");
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    //attachment;filename="b_i t_p d_i.rar";filename*=UTF-8''b%C3%A0i%20t%E1%BA%ADp%20d%C3%A0i.rar
    private String guessFileName(String contentDisposition) {
        try {
            //".*" + start + "(.*)" + end + ".*"
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (m.find()) {
                System.out.println(m.group(3));
                return m.group(3);
            }
        } catch (IllegalStateException ex) {
            Log.d(TAG, ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Add ProgressBar in class WebViewClient
     *
     * @since 1.0
     */
    public class AppWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isLoadingFinished) {
                redirect = true;
            }

            isLoadingFinished = false;
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
            isLoadingFinished = false;
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
                isLoadingFinished = true;
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
