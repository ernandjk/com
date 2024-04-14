package com.getcapacitor.plugin;

import android.webkit.ValueCallback;
import java.util.Iterator;
import java.util.Objects;
import java.util.ArrayList;
import com.getcapacitor.Logger;
import java.net.HttpCookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.net.CookiePolicy;
import java.net.CookieStore;
import com.getcapacitor.Bridge;
import java.net.CookieManager;

public class CapacitorCookieManager extends CookieManager
{
    private final String TAG;
    private final String localUrl;
    private final String serverUrl;
    private final android.webkit.CookieManager webkitCookieManager;
    
    public CapacitorCookieManager(final Bridge bridge) {
        this(null, null, bridge);
    }
    
    public CapacitorCookieManager(final CookieStore cookieStore, final CookiePolicy cookiePolicy, final Bridge bridge) {
        super(cookieStore, cookiePolicy);
        this.TAG = "CapacitorCookies";
        this.webkitCookieManager = android.webkit.CookieManager.getInstance();
        this.localUrl = bridge.getLocalUrl();
        this.serverUrl = bridge.getServerUrl();
    }
    
    private String getDomainFromCookieString(String trim) throws URISyntaxException {
        final String[] split = trim.toLowerCase(Locale.ROOT).split("domain=");
        if (split.length <= 1) {
            trim = null;
        }
        else {
            trim = split[1].split(";")[0].trim();
        }
        return this.getSanitizedDomain(trim);
    }
    
    public void flush() {
        this.webkitCookieManager.flush();
    }
    
    public Map<String, List<String>> get(final URI uri, final Map<String, List<String>> map) {
        if (uri != null && map != null) {
            final String string = uri.toString();
            final HashMap hashMap = new HashMap();
            final String cookieString = this.getCookieString(string);
            if (cookieString != null) {
                ((Map)hashMap).put((Object)"Cookie", (Object)Collections.singletonList((Object)cookieString));
            }
            return (Map<String, List<String>>)hashMap;
        }
        throw new IllegalArgumentException("Argument is null");
    }
    
    public HttpCookie getCookie(final String s, final String s2) {
        for (final HttpCookie httpCookie : this.getCookies(s)) {
            if (httpCookie.getName().equals((Object)s2)) {
                return httpCookie;
            }
        }
        return null;
    }
    
    public CookieStore getCookieStore() {
        throw new UnsupportedOperationException();
    }
    
    public String getCookieString(String s) {
        try {
            s = this.getSanitizedDomain(s);
            final StringBuilder sb = new StringBuilder("Getting cookies at: '");
            sb.append(s);
            sb.append("'");
            Logger.info("CapacitorCookies", sb.toString());
            s = this.webkitCookieManager.getCookie(s);
            return s;
        }
        catch (final Exception ex) {
            Logger.error("CapacitorCookies", "Failed to get cookies at the given URL.", (Throwable)ex);
            return null;
        }
    }
    
    public HttpCookie[] getCookies(String cookieString) {
        try {
            final ArrayList list = new ArrayList();
            cookieString = this.getCookieString(cookieString);
            if (cookieString != null) {
                final String[] split = cookieString.split(";");
                for (int length = split.length, i = 0; i < length; ++i) {
                    final HttpCookie httpCookie = (HttpCookie)HttpCookie.parse(split[i]).get(0);
                    httpCookie.setValue(httpCookie.getValue());
                    list.add((Object)httpCookie);
                }
            }
            return (HttpCookie[])list.toArray((Object[])new HttpCookie[list.size()]);
        }
        catch (final Exception ex) {
            return new HttpCookie[0];
        }
    }
    
    public String getSanitizedDomain(final String s) throws URISyntaxException {
        String serverUrl = null;
        Label_0018: {
            if (s != null) {
                serverUrl = s;
                if (!s.isEmpty()) {
                    break Label_0018;
                }
            }
            serverUrl = this.serverUrl;
            try {
                new URI(serverUrl);
                return serverUrl;
            }
            catch (final Exception ex) {
                final String localUrl;
                serverUrl = (localUrl = this.localUrl);
                final URI uri = new URI(localUrl);
            }
        }
        try {
            final String localUrl = serverUrl;
            final URI uri = new URI(localUrl);
            return serverUrl;
        }
        catch (final Exception ex2) {
            Logger.error("CapacitorCookies", "Failed to get sanitized URL.", (Throwable)ex2);
            throw ex2;
        }
    }
    
    public void put(final URI uri, final Map<String, List<String>> map) {
        if (uri != null) {
            if (map != null) {
                for (final String s : map.keySet()) {
                    if (s != null) {
                        if (!s.equalsIgnoreCase("Set-Cookie2") && !s.equalsIgnoreCase("Set-Cookie")) {
                            continue;
                        }
                        for (final String s2 : (List)Objects.requireNonNull((Object)map.get((Object)s))) {
                            try {
                                this.setCookie(uri.toString(), s2);
                                this.setCookie(this.getDomainFromCookieString(s2), s2);
                            }
                            catch (final Exception ex) {}
                        }
                    }
                }
            }
        }
    }
    
    public void removeAllCookies() {
        this.webkitCookieManager.removeAllCookies((ValueCallback)null);
        this.flush();
    }
    
    public void removeSessionCookies() {
        this.webkitCookieManager.removeSessionCookies((ValueCallback)null);
    }
    
    public void setCookie(final String s, final String s2) {
        try {
            final String sanitizedDomain = this.getSanitizedDomain(s);
            final StringBuilder sb = new StringBuilder("Setting cookie '");
            sb.append(s2);
            sb.append("' at: '");
            sb.append(sanitizedDomain);
            sb.append("'");
            Logger.info("CapacitorCookies", sb.toString());
            this.webkitCookieManager.setCookie(sanitizedDomain, s2);
            this.flush();
        }
        catch (final Exception ex) {
            Logger.error("CapacitorCookies", "Failed to set cookie.", (Throwable)ex);
        }
    }
    
    public void setCookie(final String s, final String s2, final String s3) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s2);
        sb.append("=");
        sb.append(s3);
        this.setCookie(s, sb.toString());
    }
    
    public void setCookie(final String s, final String s2, final String s3, final String s4, final String s5) {
        final StringBuilder sb = new StringBuilder();
        sb.append(s2);
        sb.append("=");
        sb.append(s3);
        sb.append("; expires=");
        sb.append(s4);
        sb.append("; path=");
        sb.append(s5);
        this.setCookie(s, sb.toString());
    }
}
