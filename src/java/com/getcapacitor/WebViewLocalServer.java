package com.getcapacitor;

import java.util.HashMap;
import android.net.Uri$Builder;
import android.net.Uri;
import java.util.List;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import android.webkit.CookieManager;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map$Entry;
import java.util.Map;
import android.webkit.WebResourceResponse;
import android.webkit.WebResourceRequest;
import java.io.IOException;
import java.net.URLConnection;
import java.io.InputStream;
import java.util.Iterator;
import android.content.Context;
import java.util.ArrayList;

public class WebViewLocalServer
{
    private static final String capacitorContentStart = "/_capacitor_content_";
    private static final String capacitorFileStart = "/_capacitor_file_";
    private final ArrayList<String> authorities;
    private String basePath;
    private final Bridge bridge;
    private final boolean html5mode;
    private boolean isAsset;
    private final JSInjector jsInjector;
    private final AndroidProtocolHandler protocolHandler;
    private final UriMatcher uriMatcher;
    
    WebViewLocalServer(final Context context, final Bridge bridge, final JSInjector jsInjector, final ArrayList<String> authorities, final boolean html5mode) {
        this.uriMatcher = new UriMatcher(null);
        this.html5mode = html5mode;
        this.protocolHandler = new AndroidProtocolHandler(context.getApplicationContext());
        this.authorities = authorities;
        this.bridge = bridge;
        this.jsInjector = jsInjector;
    }
    
    private void createHostingDetails() {
        final String basePath = this.basePath;
        if (basePath.indexOf(42) == -1) {
            final WebViewLocalServer$1 webViewLocalServer$1 = new WebViewLocalServer$1(this, basePath);
            for (final String s : this.authorities) {
                this.registerUriForScheme("http", (PathHandler)webViewLocalServer$1, s);
                this.registerUriForScheme("https", (PathHandler)webViewLocalServer$1, s);
                final String scheme = this.bridge.getScheme();
                if (!scheme.equals((Object)"http") && !scheme.equals((Object)"https")) {
                    this.registerUriForScheme(scheme, (PathHandler)webViewLocalServer$1, s);
                }
            }
            return;
        }
        throw new IllegalArgumentException("assetPath cannot contain the '*' character.");
    }
    
    private String getMimeType(final String s, final InputStream inputStream) {
        String guessContentTypeFromStream = null;
        Label_0106: {
            try {
                final String guessContentTypeFromName = URLConnection.guessContentTypeFromName(s);
                if (guessContentTypeFromName != null) {
                    try {
                        if (s.endsWith(".js") && guessContentTypeFromName.equals((Object)"image/x-icon")) {
                            Logger.debug("We shouldn't be here");
                        }
                    }
                    catch (final Exception ex) {
                        guessContentTypeFromStream = guessContentTypeFromName;
                        break Label_0106;
                    }
                }
                if ((guessContentTypeFromStream = guessContentTypeFromName) != null) {
                    return guessContentTypeFromStream;
                }
                if (s.endsWith(".js") || s.endsWith(".mjs")) {
                    guessContentTypeFromStream = "application/javascript";
                    return guessContentTypeFromStream;
                }
                if (s.endsWith(".wasm")) {
                    guessContentTypeFromStream = "application/wasm";
                    return guessContentTypeFromStream;
                }
                guessContentTypeFromStream = URLConnection.guessContentTypeFromStream(inputStream);
                return guessContentTypeFromStream;
            }
            catch (final Exception ex) {
                guessContentTypeFromStream = null;
            }
        }
        final StringBuilder sb = new StringBuilder("Unable to get mime type");
        sb.append(s);
        final Exception ex;
        Logger.error(sb.toString(), (Throwable)ex);
        return guessContentTypeFromStream;
    }
    
    private int getStatusCode(final InputStream inputStream, int n) {
        try {
            if (inputStream.available() == -1) {
                n = 404;
            }
        }
        catch (final IOException ex) {
            n = 500;
        }
        return n;
    }
    
    private WebResourceResponse handleLocalRequest(final WebResourceRequest webResourceRequest, final PathHandler pathHandler) {
        final String path = webResourceRequest.getUrl().getPath();
        if (webResourceRequest.getRequestHeaders().get((Object)"Range") != null) {
            final WebViewLocalServer.WebViewLocalServer$LollipopLazyInputStream webViewLocalServer$LollipopLazyInputStream = new WebViewLocalServer.WebViewLocalServer$LollipopLazyInputStream(pathHandler, webResourceRequest);
            final String mimeType = this.getMimeType(path, (InputStream)webViewLocalServer$LollipopLazyInputStream);
            final Map<String, String> responseHeaders = pathHandler.getResponseHeaders();
            int n;
            try {
                final int available = ((InputStream)webViewLocalServer$LollipopLazyInputStream).available();
                final String[] split = ((String)webResourceRequest.getRequestHeaders().get((Object)"Range")).split("=")[1].split("-");
                final String s = split[0];
                int int1 = available - 1;
                if (split.length > 1) {
                    int1 = Integer.parseInt(split[1]);
                }
                responseHeaders.put((Object)"Accept-Ranges", (Object)"bytes");
                final StringBuilder sb = new StringBuilder("bytes ");
                sb.append(s);
                sb.append("-");
                sb.append(int1);
                sb.append("/");
                sb.append(available);
                responseHeaders.put((Object)"Content-Range", (Object)sb.toString());
                n = 206;
            }
            catch (final IOException ex) {
                n = 404;
            }
            return new WebResourceResponse(mimeType, pathHandler.getEncoding(), n, pathHandler.getReasonPhrase(), (Map)responseHeaders, (InputStream)webViewLocalServer$LollipopLazyInputStream);
        }
        if (!this.isLocalFile(webResourceRequest.getUrl())) {
            if (!this.isErrorUrl(webResourceRequest.getUrl())) {
                if (path.equals((Object)"/cordova.js")) {
                    return new WebResourceResponse("application/javascript", pathHandler.getEncoding(), pathHandler.getStatusCode(), pathHandler.getReasonPhrase(), (Map)pathHandler.getResponseHeaders(), (InputStream)null);
                }
                if (!path.equals((Object)"/")) {
                    if (webResourceRequest.getUrl().getLastPathSegment().contains((CharSequence)".") || !this.html5mode) {
                        if ("/favicon.ico".equalsIgnoreCase(path)) {
                            try {
                                return new WebResourceResponse("image/png", (String)null, (InputStream)null);
                            }
                            catch (final Exception ex2) {
                                Logger.error("favicon handling failed", (Throwable)ex2);
                            }
                        }
                        if (path.lastIndexOf(".") >= 0) {
                            final String substring = path.substring(path.lastIndexOf("."));
                            InputStream injectedStream;
                            final WebViewLocalServer.WebViewLocalServer$LollipopLazyInputStream webViewLocalServer$LollipopLazyInputStream2 = (WebViewLocalServer.WebViewLocalServer$LollipopLazyInputStream)(injectedStream = (InputStream)new WebViewLocalServer.WebViewLocalServer$LollipopLazyInputStream(pathHandler, webResourceRequest));
                            if (substring.equals((Object)".html")) {
                                injectedStream = this.jsInjector.getInjectedStream((InputStream)webViewLocalServer$LollipopLazyInputStream2);
                            }
                            return new WebResourceResponse(this.getMimeType(path, injectedStream), pathHandler.getEncoding(), this.getStatusCode(injectedStream, pathHandler.getStatusCode()), pathHandler.getReasonPhrase(), (Map)pathHandler.getResponseHeaders(), injectedStream);
                        }
                        return null;
                    }
                }
                try {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append(this.basePath);
                    sb2.append("/index.html");
                    String s2 = sb2.toString();
                    if (this.bridge.getRouteProcessor() != null) {
                        final ProcessedRoute process = this.bridge.getRouteProcessor().process(this.basePath, "/index.html");
                        s2 = process.getPath();
                        this.isAsset = process.isAsset();
                    }
                    InputStream inputStream;
                    if (this.isAsset) {
                        inputStream = this.protocolHandler.openAsset(s2);
                    }
                    else {
                        inputStream = this.protocolHandler.openFile(s2);
                    }
                    final InputStream injectedStream2 = this.jsInjector.getInjectedStream(inputStream);
                    return new WebResourceResponse("text/html", pathHandler.getEncoding(), this.getStatusCode(injectedStream2, pathHandler.getStatusCode()), pathHandler.getReasonPhrase(), (Map)pathHandler.getResponseHeaders(), injectedStream2);
                }
                catch (final IOException ex3) {
                    Logger.error("Unable to open index.html", (Throwable)ex3);
                    return null;
                }
            }
        }
        final WebViewLocalServer.WebViewLocalServer$LollipopLazyInputStream webViewLocalServer$LollipopLazyInputStream3 = new WebViewLocalServer.WebViewLocalServer$LollipopLazyInputStream(pathHandler, webResourceRequest);
        return new WebResourceResponse(this.getMimeType(webResourceRequest.getUrl().getPath(), (InputStream)webViewLocalServer$LollipopLazyInputStream3), pathHandler.getEncoding(), this.getStatusCode((InputStream)webViewLocalServer$LollipopLazyInputStream3, pathHandler.getStatusCode()), pathHandler.getReasonPhrase(), (Map)pathHandler.getResponseHeaders(), (InputStream)webViewLocalServer$LollipopLazyInputStream3);
    }
    
    private WebResourceResponse handleProxyRequest(final WebResourceRequest webResourceRequest, final PathHandler pathHandler) {
        final String method = webResourceRequest.getMethod();
        if (method.equals((Object)"GET")) {
            try {
                final String string = webResourceRequest.getUrl().toString();
                final Map requestHeaders = webResourceRequest.getRequestHeaders();
                while (true) {
                    for (final Map$Entry map$Entry : requestHeaders.entrySet()) {
                        if (((String)map$Entry.getKey()).equalsIgnoreCase("Accept") && ((String)map$Entry.getValue()).toLowerCase().contains((CharSequence)"text/html")) {
                            final boolean b = true;
                            if (b) {
                                final HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(string).openConnection();
                                for (final Map$Entry map$Entry2 : requestHeaders.entrySet()) {
                                    httpURLConnection.setRequestProperty((String)map$Entry2.getKey(), (String)map$Entry2.getValue());
                                }
                                final String cookie = CookieManager.getInstance().getCookie(string);
                                if (cookie != null) {
                                    httpURLConnection.setRequestProperty("Cookie", cookie);
                                }
                                httpURLConnection.setRequestMethod(method);
                                httpURLConnection.setReadTimeout(30000);
                                httpURLConnection.setConnectTimeout(30000);
                                if (webResourceRequest.getUrl().getUserInfo() != null) {
                                    final String encodeToString = Base64.encodeToString(webResourceRequest.getUrl().getUserInfo().getBytes(StandardCharsets.UTF_8), 2);
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append("Basic ");
                                    sb.append(encodeToString);
                                    httpURLConnection.setRequestProperty("Authorization", sb.toString());
                                }
                                final List list = (List)httpURLConnection.getHeaderFields().get((Object)"Set-Cookie");
                                if (list != null) {
                                    final Iterator iterator3 = list.iterator();
                                    while (iterator3.hasNext()) {
                                        CookieManager.getInstance().setCookie(string, (String)iterator3.next());
                                    }
                                }
                                return new WebResourceResponse("text/html", pathHandler.getEncoding(), pathHandler.getStatusCode(), pathHandler.getReasonPhrase(), (Map)pathHandler.getResponseHeaders(), this.jsInjector.getInjectedStream(httpURLConnection.getInputStream()));
                            }
                            return null;
                        }
                    }
                    final boolean b = false;
                    continue;
                }
            }
            catch (final Exception ex) {
                this.bridge.handleAppUrlLoadError(ex);
            }
        }
        return null;
    }
    
    private boolean isAllowedUrl(final Uri uri) {
        return this.bridge.getServerUrl() != null || this.bridge.getAppAllowNavigationMask().matches(uri.getHost());
    }
    
    private boolean isErrorUrl(final Uri uri) {
        return uri.toString().equals((Object)this.bridge.getErrorUrl());
    }
    
    private boolean isLocalFile(final Uri uri) {
        final String path = uri.getPath();
        return path.startsWith("/_capacitor_content_") || path.startsWith("/_capacitor_file_");
    }
    
    private boolean isMainUrl(final Uri uri) {
        return this.bridge.getServerUrl() == null && uri.getHost().equalsIgnoreCase(this.bridge.getHost());
    }
    
    private static Uri parseAndVerifyUrl(final String s) {
        if (s == null) {
            return null;
        }
        final Uri parse = Uri.parse(s);
        if (parse == null) {
            final StringBuilder sb = new StringBuilder("Malformed URL: ");
            sb.append(s);
            Logger.error(sb.toString());
            return null;
        }
        final String path = parse.getPath();
        if (path != null && !path.isEmpty()) {
            return parse;
        }
        final StringBuilder sb2 = new StringBuilder("URL does not have a path: ");
        sb2.append(s);
        Logger.error(sb2.toString());
        return null;
    }
    
    private void registerUriForScheme(final String s, final PathHandler pathHandler, final String s2) {
        final Uri$Builder uri$Builder = new Uri$Builder();
        uri$Builder.scheme(s);
        uri$Builder.authority(s2);
        uri$Builder.path("");
        final Uri build = uri$Builder.build();
        this.register(Uri.withAppendedPath(build, "/"), pathHandler);
        this.register(Uri.withAppendedPath(build, "**"), pathHandler);
    }
    
    public String getBasePath() {
        return this.basePath;
    }
    
    public void hostAssets(final String basePath) {
        this.isAsset = true;
        this.basePath = basePath;
        this.createHostingDetails();
    }
    
    public void hostFiles(final String basePath) {
        this.isAsset = false;
        this.basePath = basePath;
        this.createHostingDetails();
    }
    
    void register(final Uri uri, final PathHandler pathHandler) {
        final UriMatcher uriMatcher = this.uriMatcher;
        synchronized (uriMatcher) {
            this.uriMatcher.addURI(uri.getScheme(), uri.getAuthority(), uri.getPath(), pathHandler);
        }
    }
    
    public WebResourceResponse shouldInterceptRequest(final WebResourceRequest webResourceRequest) {
        final Uri url = webResourceRequest.getUrl();
        final UriMatcher uriMatcher = this.uriMatcher;
        synchronized (uriMatcher) {
            final PathHandler pathHandler = (PathHandler)this.uriMatcher.match(webResourceRequest.getUrl());
            monitorexit(uriMatcher);
            if (pathHandler == null) {
                return null;
            }
            if (!this.isLocalFile(url) && !this.isMainUrl(url) && this.isAllowedUrl(url) && !this.isErrorUrl(url)) {
                return this.handleProxyRequest(webResourceRequest, pathHandler);
            }
            final StringBuilder sb = new StringBuilder("Handling local request: ");
            sb.append(webResourceRequest.getUrl().toString());
            Logger.debug(sb.toString());
            return this.handleLocalRequest(webResourceRequest, pathHandler);
        }
    }
    
    private abstract static class LazyInputStream extends InputStream
    {
        protected final PathHandler handler;
        private InputStream is;
        
        public LazyInputStream(final PathHandler handler) {
            this.is = null;
            this.handler = handler;
        }
        
        private InputStream getInputStream() {
            if (this.is == null) {
                this.is = this.handle();
            }
            return this.is;
        }
        
        public int available() throws IOException {
            final InputStream inputStream = this.getInputStream();
            int available;
            if (inputStream != null) {
                available = inputStream.available();
            }
            else {
                available = -1;
            }
            return available;
        }
        
        protected abstract InputStream handle();
        
        public int read() throws IOException {
            final InputStream inputStream = this.getInputStream();
            int read;
            if (inputStream != null) {
                read = inputStream.read();
            }
            else {
                read = -1;
            }
            return read;
        }
        
        public int read(final byte[] array) throws IOException {
            final InputStream inputStream = this.getInputStream();
            int read;
            if (inputStream != null) {
                read = inputStream.read(array);
            }
            else {
                read = -1;
            }
            return read;
        }
        
        public int read(final byte[] array, int read, final int n) throws IOException {
            final InputStream inputStream = this.getInputStream();
            if (inputStream != null) {
                read = inputStream.read(array, read, n);
            }
            else {
                read = -1;
            }
            return read;
        }
        
        public long skip(long skip) throws IOException {
            final InputStream inputStream = this.getInputStream();
            if (inputStream != null) {
                skip = inputStream.skip(skip);
            }
            else {
                skip = 0L;
            }
            return skip;
        }
    }
    
    public abstract static class PathHandler
    {
        private String charset;
        private String encoding;
        protected String mimeType;
        private String reasonPhrase;
        private Map<String, String> responseHeaders;
        private int statusCode;
        
        public PathHandler() {
            this(null, null, 200, "OK", null);
        }
        
        public PathHandler(final String encoding, final String charset, final int statusCode, final String reasonPhrase, final Map<String, String> map) {
            this.encoding = encoding;
            this.charset = charset;
            this.statusCode = statusCode;
            this.reasonPhrase = reasonPhrase;
            Object responseHeaders = map;
            if (map == null) {
                responseHeaders = new HashMap();
            }
            ((Map)responseHeaders).put((Object)"Cache-Control", (Object)"no-cache");
            this.responseHeaders = (Map<String, String>)responseHeaders;
        }
        
        public String getCharset() {
            return this.charset;
        }
        
        public String getEncoding() {
            return this.encoding;
        }
        
        public String getReasonPhrase() {
            return this.reasonPhrase;
        }
        
        public Map<String, String> getResponseHeaders() {
            return this.responseHeaders;
        }
        
        public int getStatusCode() {
            return this.statusCode;
        }
        
        public abstract InputStream handle(final Uri p0);
        
        public InputStream handle(final WebResourceRequest webResourceRequest) {
            return this.handle(webResourceRequest.getUrl());
        }
    }
}
