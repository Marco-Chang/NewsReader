package com.example.root.newsreader;

/**
 * Created by root on 2017/1/13.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);
        WebView webView = (WebView) findViewById(R.id.webView1);
        Intent i = getIntent();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(i.getStringExtra("url"));
    }
}
