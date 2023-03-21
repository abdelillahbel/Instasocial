/**
 * Company : Bsetec
 * Product: Instasocial
 * Email : support@bsetec.com
 * Copyright © 2018 BSEtec. All rights reserved.
 **/

package com.androidapp.instasocial.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidapp.instasocial.R;
import com.androidapp.instasocial.utils.NetworkReceiver;
import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * This class is used to view Web content
 */
public class WebActivity extends AppCompatActivity {

    /**
     * Member variables declarations/initializations
     */
    public static final String TAG = "WebActivity";
    public static final String ARG_URL = "url";
    public static final String ARG_TITLE = "title";
    String url;
    String title;

    Views views;

    public static Intent getArgIntent(Activity activity, String url,String title) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_TITLE,title);
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        url = i != null && i.hasExtra(ARG_URL) ? i.getStringExtra(ARG_URL) : "url-not-found";
        title = i != null && i.hasExtra(ARG_TITLE) ? i.getStringExtra(ARG_TITLE) : "";

        Log.e(TAG, "url: " + url+" title: "+title);
        setContentView(R.layout.activity_web_view);
        views = new Views();
        views.init();
    }


    //UI view handling logics
    public class Views {
        final WebView webView;
        final ImageView icBack;
        final ProgressWheel progress;
        final TextView txtNoData;
        final TextView txtTitle;

        public Views() {
            webView=findViewById(R.id.webView);
            progress=findViewById(R.id.progress);
            txtNoData=findViewById(R.id.txtNoData);
            icBack = findViewById(R.id.icBack);
            txtTitle=findViewById(R.id.txtTitle);
        }
        public void setNoDataVisible(boolean isVisible){
            txtNoData.setVisibility(isVisible?View.VISIBLE:View.GONE);
        }
        public void setLoaderVisible(boolean isVisible){
            progress.setVisibility(isVisible?View.VISIBLE:View.GONE);
        }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void init(){
            setLoaderVisible(false);
            setNoDataVisible(false);
            txtTitle.setText(title);
            icBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });


            /**
             * Detect whether online connection is enabled
             */

            if (NetworkReceiver.isOnline(WebActivity.this)) {
                setNoDataVisible(false);
                //webview_make_donation.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new ChromeNavigation());
                webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                webView.getSettings().setAppCacheEnabled(false);
                webView.getSettings().setDomStorageEnabled(true);

                CookieSyncManager.createInstance(WebActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                cookieManager.setAcceptCookie(false);

                webView.loadUrl(url);
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                        return super.onJsAlert(view, url, message, result);
                    }
                });


            } else {
                setNoDataVisible(true);
            }
        }

    }

    /**
     * Web client handler class
     */

    private class ChromeNavigation extends WebViewClient {
        public static final int STATE_IDLE = 0;
        public static final int STATE_LOADING = 1;
        private int state = STATE_IDLE;

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            views.setLoaderVisible(false);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (state == STATE_IDLE) {
                state = STATE_LOADING;
            }
            if (views.progress.getVisibility()==View.GONE){
                views.progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            state = STATE_IDLE;
            views.setLoaderVisible(false);
        }
    }
}
