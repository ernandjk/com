package com.getcapacitor;

import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.graphics.Bitmap;
import java.util.Iterator;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BridgeWebViewClient extends WebViewClient
{
    private Bridge bridge;
    
    public BridgeWebViewClient(final Bridge bridge) {
        this.bridge = bridge;
    }
    
    public void onPageFinished(final WebView webView, final String s) {
        super.onPageFinished(webView, s);
        if (this.bridge.getWebViewListeners() != null && webView.getProgress() == 100) {
            final Iterator iterator = this.bridge.getWebViewListeners().iterator();
            while (iterator.hasNext()) {
                ((WebViewListener)iterator.next()).onPageLoaded(webView);
            }
        }
    }
    
    public void onPageStarted(final WebView webView, final String s, final Bitmap bitmap) {
        super.onPageStarted(webView, s, bitmap);
        this.bridge.reset();
        if (this.bridge.getWebViewListeners() != null) {
            final Iterator iterator = this.bridge.getWebViewListeners().iterator();
            while (iterator.hasNext()) {
                ((WebViewListener)iterator.next()).onPageStarted(webView);
            }
        }
    }
    
    public void onReceivedError(final WebView webView, final WebResourceRequest webResourceRequest, final WebResourceError webResourceError) {
        super.onReceivedError(webView, webResourceRequest, webResourceError);
        if (this.bridge.getWebViewListeners() != null) {
            final Iterator iterator = this.bridge.getWebViewListeners().iterator();
            while (iterator.hasNext()) {
                ((WebViewListener)iterator.next()).onReceivedError(webView);
            }
        }
        final String errorUrl = this.bridge.getErrorUrl();
        if (errorUrl != null && webResourceRequest.isForMainFrame()) {
            webView.loadUrl(errorUrl);
        }
    }
    
    public void onReceivedHttpError(final WebView webView, final WebResourceRequest webResourceRequest, final WebResourceResponse webResourceResponse) {
        super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
        if (this.bridge.getWebViewListeners() != null) {
            final Iterator iterator = this.bridge.getWebViewListeners().iterator();
            while (iterator.hasNext()) {
                ((WebViewListener)iterator.next()).onReceivedHttpError(webView);
            }
        }
        final String errorUrl = this.bridge.getErrorUrl();
        if (errorUrl != null && webResourceRequest.isForMainFrame()) {
            webView.loadUrl(errorUrl);
        }
    }
    
    public WebResourceResponse shouldInterceptRequest(final WebView webView, final WebResourceRequest webResourceRequest) {
        return this.bridge.getLocalServer().shouldInterceptRequest(webResourceRequest);
    }
    
    public boolean shouldOverrideUrlLoading(final WebView webView, final WebResourceRequest webResourceRequest) {
        return this.bridge.launchIntent(webResourceRequest.getUrl());
    }
    
    @Deprecated
    public boolean shouldOverrideUrlLoading(final WebView webView, final String s) {
        return this.bridge.launchIntent(Uri.parse(s));
    }
}
