package com.lnt.wifidirecttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PeerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String URL = this.getIntent().getStringExtra("URL");
        setContentView(R.layout.activity_peer_view);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.setWebViewClient(new WebViewClient(){

                                   }


        );

        if(URL != null)
        myWebView.loadUrl(URL);
    }


}
