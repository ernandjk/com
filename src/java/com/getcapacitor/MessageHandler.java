package com.getcapacitor;

import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import androidx.webkit.WebViewCompat$WebMessageListener;
import java.util.Set;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import android.net.Uri;
import androidx.webkit.WebMessageCompat;
import android.webkit.WebView;
import androidx.webkit.JavaScriptReplyProxy;
import org.apache.cordova.PluginManager;

public class MessageHandler
{
    private Bridge bridge;
    private PluginManager cordovaPluginManager;
    private JavaScriptReplyProxy javaScriptReplyProxy;
    private WebView webView;
    
    public MessageHandler(final Bridge bridge, final WebView webView, final PluginManager cordovaPluginManager) {
        this.bridge = bridge;
        this.webView = webView;
        this.cordovaPluginManager = cordovaPluginManager;
        if (WebViewFeature.isFeatureSupported("WEB_MESSAGE_LISTENER") && !bridge.getConfig().isUsingLegacyBridge()) {
            final MessageHandler$$ExternalSyntheticLambda0 messageHandler$$ExternalSyntheticLambda0 = new MessageHandler$$ExternalSyntheticLambda0(this);
            try {
                WebViewCompat.addWebMessageListener(webView, "androidBridge", (Set)bridge.getAllowedOriginRules(), (WebViewCompat$WebMessageListener)messageHandler$$ExternalSyntheticLambda0);
            }
            catch (final Exception ex) {
                webView.addJavascriptInterface((Object)this, "androidBridge");
            }
        }
        else {
            webView.addJavascriptInterface((Object)this, "androidBridge");
        }
    }
    
    private void callCordovaPluginMethod(final String s, final String s2, final String s3, final String s4) {
        this.bridge.execute((Runnable)new MessageHandler$$ExternalSyntheticLambda2(this, s2, s3, s, s4));
    }
    
    private void callPluginMethod(final String s, final String s2, final String s3, final JSObject jsObject) {
        this.bridge.callPluginMethod(s2, s3, new PluginCall(this, s2, s, s3, jsObject));
    }
    
    private void legacySendResponseMessage(final PluginResult pluginResult) {
        final StringBuilder sb = new StringBuilder("window.Capacitor.fromNative(");
        sb.append(pluginResult.toString());
        sb.append(")");
        final String string = sb.toString();
        final WebView webView = this.webView;
        webView.post((Runnable)new MessageHandler$$ExternalSyntheticLambda1(webView, string));
    }
    
    @JavascriptInterface
    public void postMessage(String s) {
        try {
            final JSObject jsObject = new JSObject(s);
            final String string = jsObject.getString("type");
            final boolean b = string != null;
            final boolean b2 = b && string.equals((Object)"cordova");
            final boolean b3 = b && string.equals((Object)"js.error");
            final String string2 = jsObject.getString("callbackId");
            if (b2) {
                s = jsObject.getString("service");
                final String string3 = jsObject.getString("action");
                final String string4 = jsObject.getString("actionArgs");
                final String tags = Logger.tags("Plugin");
                final StringBuilder sb = new StringBuilder("To native (Cordova plugin): callbackId: ");
                sb.append(string2);
                sb.append(", service: ");
                sb.append(s);
                sb.append(", action: ");
                sb.append(string3);
                sb.append(", actionArgs: ");
                sb.append(string4);
                Logger.verbose(tags, sb.toString());
                this.callCordovaPluginMethod(string2, s, string3, string4);
            }
            else if (b3) {
                final StringBuilder sb2 = new StringBuilder("JavaScript Error: ");
                sb2.append(s);
                Logger.error(sb2.toString());
            }
            else {
                s = jsObject.getString("pluginId");
                final String string5 = jsObject.getString("methodName");
                final JSObject jsObject2 = jsObject.getJSObject("options", new JSObject());
                final String tags2 = Logger.tags("Plugin");
                final StringBuilder sb3 = new StringBuilder("To native (Capacitor plugin): callbackId: ");
                sb3.append(string2);
                sb3.append(", pluginId: ");
                sb3.append(s);
                sb3.append(", methodName: ");
                sb3.append(string5);
                Logger.verbose(tags2, sb3.toString());
                this.callPluginMethod(string2, s, string5, jsObject2);
            }
        }
        catch (final Exception ex) {
            Logger.error("Post message error:", (Throwable)ex);
        }
    }
    
    public void sendResponseMessage(final PluginCall pluginCall, final PluginResult pluginResult, final PluginResult pluginResult2) {
        Label_0266: {
            try {
                final PluginResult pluginResult3 = new PluginResult();
                pluginResult3.put("save", pluginCall.isKeptAlive());
                pluginResult3.put("callbackId", pluginCall.getCallbackId());
                pluginResult3.put("pluginId", pluginCall.getPluginId());
                pluginResult3.put("methodName", pluginCall.getMethodName());
                if (pluginResult2 != null) {
                    pluginResult3.put("success", false);
                    pluginResult3.put("error", pluginResult2);
                    final StringBuilder sb = new StringBuilder("Sending plugin error: ");
                    sb.append(pluginResult3.toString());
                    Logger.debug(sb.toString());
                }
                else {
                    pluginResult3.put("success", true);
                    if (pluginResult != null) {
                        pluginResult3.put("data", pluginResult);
                    }
                }
                if (pluginCall.getCallbackId().equals((Object)"-1") ^ true) {
                    if (this.bridge.getConfig().isUsingLegacyBridge()) {
                        this.legacySendResponseMessage(pluginResult3);
                    }
                    else {
                        if (WebViewFeature.isFeatureSupported("WEB_MESSAGE_LISTENER")) {
                            final JavaScriptReplyProxy javaScriptReplyProxy = this.javaScriptReplyProxy;
                            if (javaScriptReplyProxy != null) {
                                javaScriptReplyProxy.postMessage(pluginResult3.toString());
                                break Label_0266;
                            }
                        }
                        this.legacySendResponseMessage(pluginResult3);
                    }
                }
                else {
                    this.bridge.getApp().fireRestoredResult(pluginResult3);
                }
            }
            catch (final Exception ex) {
                final StringBuilder sb2 = new StringBuilder("sendResponseMessage: error: ");
                sb2.append((Object)ex);
                Logger.error(sb2.toString());
            }
        }
        if (!pluginCall.isKeptAlive()) {
            pluginCall.release(this.bridge);
        }
    }
}
