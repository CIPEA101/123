package com.yunbao.phonelive.views;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yunbao.phonelive.AppConfig;
import com.yunbao.phonelive.R;

/**
 * Created by cxf on 2018/9/22.
 * 首页视频
 */

public class MainHomeActivityViewHolder extends AbsMainChildTopViewHolder {

    public MainHomeActivityViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_base_webview;
    }

    @Override
    public void init() {
        super.init();
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webView.loadUrl("");
    }
}
