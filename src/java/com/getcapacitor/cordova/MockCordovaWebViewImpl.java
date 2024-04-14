package com.getcapacitor.cordova;

import java.util.Map;
import android.webkit.WebChromeClient$CustomViewCallback;
import org.apache.cordova.PluginResult;
import android.content.Intent;
import org.apache.cordova.NativeToJsMessageQueue$BridgeMode;
import java.util.Collection;
import org.apache.cordova.PluginEntry;
import java.util.List;
import android.view.View;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.ICordovaCookieManager;
import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.PluginManager;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.CordovaInterface;
import android.content.Context;
import org.apache.cordova.CordovaWebView;

public class MockCordovaWebViewImpl implements CordovaWebView
{
    private Context context;
    private CapacitorCordovaCookieManager cookieManager;
    private CordovaInterface cordova;
    private boolean hasPausedEver;
    private NativeToJsMessageQueue nativeToJsMessageQueue;
    private PluginManager pluginManager;
    private CordovaPreferences preferences;
    private CordovaResourceApi resourceApi;
    private WebView webView;
    
    public MockCordovaWebViewImpl(final Context context) {
        this.context = context;
    }
    
    public boolean backHistory() {
        return false;
    }
    
    public boolean canGoBack() {
        return false;
    }
    
    public void clearCache() {
    }
    
    @Deprecated
    public void clearCache(final boolean b) {
    }
    
    public void clearHistory() {
    }
    
    public void eval(final String s, final ValueCallback<String> valueCallback) {
        new Handler(this.context.getMainLooper()).post((Runnable)new MockCordovaWebViewImpl$$ExternalSyntheticLambda0(this, s, (ValueCallback)valueCallback));
    }
    
    public Context getContext() {
        return this.webView.getContext();
    }
    
    public ICordovaCookieManager getCookieManager() {
        return (ICordovaCookieManager)this.cookieManager;
    }
    
    public CordovaWebViewEngine getEngine() {
        return null;
    }
    
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }
    
    public CordovaPreferences getPreferences() {
        return this.preferences;
    }
    
    public CordovaResourceApi getResourceApi() {
        return this.resourceApi;
    }
    
    public String getUrl() {
        return this.webView.getUrl();
    }
    
    public View getView() {
        return (View)this.webView;
    }
    
    public void handleDestroy() {
        if (!this.isInitialized()) {
            return;
        }
        this.pluginManager.onDestroy();
    }
    
    public void handlePause(final boolean b) {
        if (!this.isInitialized()) {
            return;
        }
        this.hasPausedEver = true;
        this.pluginManager.onPause(b);
        this.triggerDocumentEvent("pause");
        if (!b) {
            this.setPaused(true);
        }
    }
    
    public void handleResume(final boolean b) {
        if (!this.isInitialized()) {
            return;
        }
        this.setPaused(false);
        this.pluginManager.onResume(b);
        if (this.hasPausedEver) {
            this.triggerDocumentEvent("resume");
        }
    }
    
    public void handleStart() {
        if (!this.isInitialized()) {
            return;
        }
        this.pluginManager.onStart();
    }
    
    public void handleStop() {
        if (!this.isInitialized()) {
            return;
        }
        this.pluginManager.onStop();
    }
    
    @Deprecated
    public void hideCustomView() {
    }
    
    public void init(final CordovaInterface cordova, final List<PluginEntry> list, final CordovaPreferences preferences) {
        this.cordova = cordova;
        this.preferences = preferences;
        this.pluginManager = new PluginManager((CordovaWebView)this, this.cordova, (Collection)list);
        this.resourceApi = new CordovaResourceApi(this.context, this.pluginManager);
        this.pluginManager.init();
    }
    
    public void init(final CordovaInterface cordova, final List<PluginEntry> list, final CordovaPreferences preferences, final WebView webView) {
        this.cordova = cordova;
        this.webView = webView;
        this.preferences = preferences;
        this.pluginManager = new PluginManager((CordovaWebView)this, this.cordova, (Collection)list);
        this.resourceApi = new CordovaResourceApi(this.context, this.pluginManager);
        (this.nativeToJsMessageQueue = new NativeToJsMessageQueue()).addBridgeMode((NativeToJsMessageQueue$BridgeMode)new CapacitorEvalBridgeMode(webView, this.cordova));
        this.nativeToJsMessageQueue.setBridgeMode(0);
        this.cookieManager = new CapacitorCordovaCookieManager(webView);
        this.pluginManager.init();
    }
    
    public boolean isButtonPlumbedToJs(final int n) {
        return false;
    }
    
    @Deprecated
    public boolean isCustomViewShowing() {
        return false;
    }
    
    public boolean isInitialized() {
        return this.cordova != null;
    }
    
    public void loadUrl(final String s) {
        this.loadUrlIntoView(s, true);
    }
    
    public void loadUrlIntoView(final String s, final boolean b) {
        if (!s.equals((Object)"about:blank") && !s.startsWith("javascript:")) {
            return;
        }
        this.webView.loadUrl(s);
    }
    
    public void onNewIntent(final Intent intent) {
        final PluginManager pluginManager = this.pluginManager;
        if (pluginManager != null) {
            pluginManager.onNewIntent(intent);
        }
    }
    
    public Object postMessage(final String s, final Object o) {
        return this.pluginManager.postMessage(s, o);
    }
    
    @Deprecated
    public void sendJavascript(final String s) {
        this.nativeToJsMessageQueue.addJavaScript(s);
    }
    
    public void sendPluginResult(final PluginResult pluginResult, final String s) {
        this.nativeToJsMessageQueue.addPluginResult(pluginResult, s);
    }
    
    public void setButtonPlumbedToJs(final int n, final boolean b) {
    }
    
    public void setPaused(final boolean b) {
        if (b) {
            this.webView.onPause();
            this.webView.pauseTimers();
        }
        else {
            this.webView.onResume();
            this.webView.resumeTimers();
        }
    }
    
    @Deprecated
    public void showCustomView(final View view, final WebChromeClient$CustomViewCallback webChromeClient$CustomViewCallback) {
    }
    
    public void showWebPage(final String s, final boolean b, final boolean b2, final Map<String, Object> map) {
    }
    
    public void stopLoading() {
    }
    
    public void triggerDocumentEvent(final String s) {
        final StringBuilder sb = new StringBuilder("window.Capacitor.triggerEvent('");
        sb.append(s);
        sb.append("', 'document');");
        this.eval(sb.toString(), (ValueCallback<String>)new MockCordovaWebViewImpl$$ExternalSyntheticLambda1());
    }
    
    public static class CapacitorEvalBridgeMode extends NativeToJsMessageQueue$BridgeMode
    {
        private final CordovaInterface cordova;
        private final WebView webView;
        
        public CapacitorEvalBridgeMode(final WebView webView, final CordovaInterface cordova) {
            this.webView = webView;
            this.cordova = cordova;
        }
        
        public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue nativeToJsMessageQueue) {
            this.cordova.getActivity().runOnUiThread((Runnable)new MockCordovaWebViewImpl$CapacitorEvalBridgeMode$$ExternalSyntheticLambda0(this, nativeToJsMessageQueue));
        }
    }
}
