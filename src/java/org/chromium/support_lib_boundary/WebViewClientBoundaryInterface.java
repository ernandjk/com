package org.chromium.support_lib_boundary;

import android.app.PendingIntent;
import android.webkit.WebResourceResponse;
import java.lang.reflect.InvocationHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

public interface WebViewClientBoundaryInterface extends FeatureFlagHolderBoundaryInterface
{
    void onPageCommitVisible(final WebView p0, final String p1);
    
    void onReceivedError(final WebView p0, final WebResourceRequest p1, final InvocationHandler p2);
    
    void onReceivedHttpError(final WebView p0, final WebResourceRequest p1, final WebResourceResponse p2);
    
    void onSafeBrowsingHit(final WebView p0, final WebResourceRequest p1, final int p2, final InvocationHandler p3);
    
    boolean onWebAuthnIntent(final WebView p0, final PendingIntent p1, final InvocationHandler p2);
    
    boolean shouldOverrideUrlLoading(final WebView p0, final WebResourceRequest p1);
}
