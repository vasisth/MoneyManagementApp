package com.example.bank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebsiteActivity extends AppCompatActivity {

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

    public class WebsiteActivity extends AppCompatActivity {
        private static final String TAG = "WebsiteActivity";

        private WebView webView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_website);

            webView = (WebView) findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("https://www.youtube.com");
        }

        @Override
        public void onBackPressed() {
            if (webView.canGoBack()) {
                webView.goBack();
            }else {
                super.onBackPressed();
            }
        }
    }




}