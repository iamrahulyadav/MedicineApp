package com.hvantage.medicineapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hvantage.medicineapp.R;

public class WebActivity extends AppCompatActivity {

    private WebView browser;
    private String URL = "https://wapking.fun/";
    private ProgressDialog progressBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        if (isConnectingToInternet(this)) {
            browser = (WebView) findViewById(R.id.webview);
            browser.setWebViewClient(new MyBrowser());
            browser.getSettings().setLoadsImagesAutomatically(true);
            browser.getSettings().setJavaScriptEnabled(true);
            browser.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            browser.loadUrl(URL);
        } else {
            Toast.makeText(this, "No internet connection, connect to internet", Toast.LENGTH_LONG).show();
            finish();
            // ErrorDialog.setDialog(WebActivity.this, getResources().getString(R.string.no_internet_text), errorListener);
        }
    }


    View.OnClickListener errorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Clieckeddddd", Toast.LENGTH_SHORT).show();
        }
    };

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.show();

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressBar.isShowing())
                progressBar.dismiss();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (progressBar.isShowing())
                progressBar.dismiss();
        }

    }

    @Override
    public void onBackPressed() {
        if (browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
