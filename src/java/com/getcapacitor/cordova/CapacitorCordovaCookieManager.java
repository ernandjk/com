package com.getcapacitor.cordova;

import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.CookieManager;
import org.apache.cordova.ICordovaCookieManager;

class CapacitorCordovaCookieManager implements ICordovaCookieManager
{
    private final CookieManager cookieManager;
    protected final WebView webView;
    
    public CapacitorCordovaCookieManager(final WebView webView) {
        this.webView = webView;
        (this.cookieManager = CookieManager.getInstance()).setAcceptThirdPartyCookies(webView, true);
    }
    
    public void clearCookies() {
        this.cookieManager.removeAllCookies((ValueCallback)null);
    }
    
    public void flush() {
        this.cookieManager.flush();
    }
    
    public String getCookie(final String s) {
        return this.cookieManager.getCookie(s);
    }
    
    public void setCookie(final String s, final String s2) {
        this.cookieManager.setCookie(s, s2);
    }
    
    public void setCookiesEnabled(final boolean acceptCookie) {
        this.cookieManager.setAcceptCookie(acceptCookie);
    }
}
