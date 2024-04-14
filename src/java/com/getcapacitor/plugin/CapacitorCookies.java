package com.getcapacitor.plugin;

import java.net.CookieHandler;
import java.net.CookieStore;
import java.net.CookiePolicy;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import java.net.HttpCookie;
import com.getcapacitor.PluginMethod;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin
public class CapacitorCookies extends Plugin
{
    CapacitorCookieManager cookieManager;
    
    private boolean isAllowingInsecureCookies() {
        return this.getBridge().getConfig().getPluginConfiguration("CapacitorCookies").getBoolean("androidCustomSchemeAllowInsecureAccess", false);
    }
    
    @PluginMethod
    public void clearAllCookies(final PluginCall pluginCall) {
        this.cookieManager.removeAllCookies();
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void clearCookies(final PluginCall pluginCall) {
        final String string = pluginCall.getString("url");
        for (final HttpCookie httpCookie : this.cookieManager.getCookies(string)) {
            final CapacitorCookieManager cookieManager = this.cookieManager;
            final StringBuilder sb = new StringBuilder();
            sb.append(httpCookie.getName());
            sb.append("=; Expires=Wed, 31 Dec 2000 23:59:59 GMT");
            cookieManager.setCookie(string, sb.toString());
        }
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void deleteCookie(final PluginCall pluginCall) {
        final String string = pluginCall.getString("key");
        if (string == null) {
            pluginCall.reject("Must provide key");
        }
        final String string2 = pluginCall.getString("url");
        final CapacitorCookieManager cookieManager = this.cookieManager;
        final StringBuilder sb = new StringBuilder();
        sb.append(string);
        sb.append("=; Expires=Wed, 31 Dec 2000 23:59:59 GMT");
        cookieManager.setCookie(string2, sb.toString());
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void getCookies(final PluginCall pluginCall) {
        if (this.isAllowingInsecureCookies()) {
            final String string = pluginCall.getString("url");
            final JSObject jsObject = new JSObject();
            for (final HttpCookie httpCookie : this.cookieManager.getCookies(string)) {
                jsObject.put(httpCookie.getName(), httpCookie.getValue());
            }
            pluginCall.resolve(jsObject);
        }
        else {
            this.bridge.eval("document.cookie", (ValueCallback)new CapacitorCookies$$ExternalSyntheticLambda0(pluginCall));
        }
    }
    
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        this.cookieManager.removeSessionCookies();
    }
    
    @JavascriptInterface
    public boolean isEnabled() {
        return this.getBridge().getConfig().getPluginConfiguration("CapacitorCookies").getBoolean("enabled", false);
    }
    
    public void load() {
        this.bridge.getWebView().addJavascriptInterface((Object)this, "CapacitorCookiesAndroidInterface");
        (this.cookieManager = new CapacitorCookieManager((CookieStore)null, CookiePolicy.ACCEPT_ALL, this.bridge)).removeSessionCookies();
        CookieHandler.setDefault((CookieHandler)this.cookieManager);
        super.load();
    }
    
    @PluginMethod
    public void setCookie(final PluginCall pluginCall) {
        final String string = pluginCall.getString("key");
        if (string == null) {
            pluginCall.reject("Must provide key");
        }
        final String string2 = pluginCall.getString("value");
        if (string2 == null) {
            pluginCall.reject("Must provide value");
        }
        this.cookieManager.setCookie(pluginCall.getString("url"), string, string2, pluginCall.getString("expires", ""), pluginCall.getString("path", "/"));
        pluginCall.resolve();
    }
    
    @JavascriptInterface
    public void setCookie(final String s, final String s2) {
        this.cookieManager.setCookie(s, s2);
    }
}
