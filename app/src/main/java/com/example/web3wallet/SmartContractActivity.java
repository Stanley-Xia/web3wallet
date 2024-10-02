package com.example.web3wallet;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class SmartContractActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_contract);

        // 初始化 WebView
        webView = findViewById(R.id.webView);

        // 配置 WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 启用 JavaScript 支持
        webView.setWebViewClient(new WebViewClient()); // 保证页面在应用内加载

        // 加载 DappRadar 网站
        webView.loadUrl("https://dappradar.com/radar/resources");
    }

    // 处理返回按钮的行为，如果 WebView 能返回上一页面，则返回上一页面
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed(); // 否则执行默认的返回操作
        }
    }
}
