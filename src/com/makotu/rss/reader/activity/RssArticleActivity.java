package com.makotu.rss.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RssArticleActivity extends RssBaseActivity {

    public static final String INTENT_PUTEXTRA_KEY = "url";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //RSS‹LŽ–ˆê——‰æ–Ê‚©‚ç‚ÌIntent‚ðŽæ“¾
        Intent intent = getIntent();
        String url = intent.getStringExtra(INTENT_PUTEXTRA_KEY);

        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        setContentView(webView);
    }
}
