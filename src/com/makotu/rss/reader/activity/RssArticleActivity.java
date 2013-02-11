package com.makotu.rss.reader.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
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
        
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        WebViewClient client = new WebViewClient() {
        public void onPageFinished(final WebView view, final String url) {
            if (loading.isShowing()) {
                loading.dismiss();
            }
        };

        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            loading.show();
            };
        };

        WebChromeClient chrome = new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                loading.setProgress(progress);
            }
        };

        WebView webView = new WebView(this);
        webView.setWebViewClient(client);
        webView.setWebChromeClient(chrome);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        setContentView(webView);
    }
}
