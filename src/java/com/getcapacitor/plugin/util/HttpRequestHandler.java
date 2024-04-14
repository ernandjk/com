package com.getcapacitor.plugin.util;

import org.json.JSONArray;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import com.getcapacitor.JSValue;
import java.util.Locale;
import com.getcapacitor.PluginCall;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import com.getcapacitor.JSArray;
import org.json.JSONObject;
import java.net.URL;
import com.getcapacitor.Bridge;
import java.util.Iterator;
import android.text.TextUtils;
import java.util.Map$Entry;
import org.json.JSONException;
import java.io.IOException;
import com.getcapacitor.JSObject;

public class HttpRequestHandler
{
    public static JSObject buildResponse(final CapacitorHttpUrlConnection capacitorHttpUrlConnection) throws IOException, JSONException {
        return buildResponse(capacitorHttpUrlConnection, ResponseType.DEFAULT);
    }
    
    public static JSObject buildResponse(final CapacitorHttpUrlConnection capacitorHttpUrlConnection, final ResponseType responseType) throws IOException, JSONException {
        final int responseCode = capacitorHttpUrlConnection.getResponseCode();
        final JSObject jsObject = new JSObject();
        jsObject.put("status", responseCode);
        jsObject.put("headers", buildResponseHeaders(capacitorHttpUrlConnection));
        jsObject.put("url", capacitorHttpUrlConnection.getURL());
        jsObject.put("data", readData((ICapacitorHttpUrlConnection)capacitorHttpUrlConnection, responseType));
        if (capacitorHttpUrlConnection.getErrorStream() != null) {
            jsObject.put("error", true);
        }
        return jsObject;
    }
    
    public static JSObject buildResponseHeaders(final CapacitorHttpUrlConnection capacitorHttpUrlConnection) {
        final JSObject jsObject = new JSObject();
        for (final Map$Entry map$Entry : capacitorHttpUrlConnection.getHeaderFields().entrySet()) {
            jsObject.put((String)map$Entry.getKey(), TextUtils.join((CharSequence)", ", (Iterable)map$Entry.getValue()));
        }
        return jsObject;
    }
    
    private static Boolean isDomainExcludedFromSSL(final Bridge bridge, final URL url) {
        try {
            final Class<?> forName = Class.forName("io.ionic.sslpinning.SSLPinning");
            return (Boolean)forName.getDeclaredMethod("isDomainExcluded", Bridge.class, URL.class).invoke(forName.newInstance(), new Object[] { bridge, url });
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public static boolean isOneOf(final String s, final MimeType... array) {
        if (s != null) {
            for (int length = array.length, i = 0; i < length; ++i) {
                if (s.contains((CharSequence)array[i].getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static Object parseJSON(final String s) throws JSONException {
        new JSONObject();
        try {
            if ("null".equals((Object)s.trim())) {
                return JSONObject.NULL;
            }
            if ("true".equals((Object)s.trim())) {
                return true;
            }
            if ("false".equals((Object)s.trim())) {
                return false;
            }
            if (s.trim().length() <= 0) {
                return "";
            }
            if (s.trim().matches("^\".*\"$")) {
                return s.trim().substring(1, s.trim().length() - 1);
            }
            if (s.trim().matches("^-?\\d+$")) {
                return Integer.parseInt(s.trim());
            }
            if (s.trim().matches("^-?\\d+(\\.\\d+)?$")) {
                return Double.parseDouble(s.trim());
            }
            try {
                return new JSObject(s);
            }
            catch (final JSONException ex) {
                return new JSArray(s);
            }
        }
        catch (final JSONException ex2) {
            return s;
        }
    }
    
    public static Object readData(final ICapacitorHttpUrlConnection capacitorHttpUrlConnection, final ResponseType responseType) throws IOException, JSONException {
        final InputStream errorStream = capacitorHttpUrlConnection.getErrorStream();
        final String headerField = capacitorHttpUrlConnection.getHeaderField("Content-Type");
        if (errorStream != null) {
            if (isOneOf(headerField, MimeType.APPLICATION_JSON, MimeType.APPLICATION_VND_API_JSON)) {
                return parseJSON(readStreamAsString(errorStream));
            }
            return readStreamAsString(errorStream);
        }
        else {
            if (headerField != null && headerField.contains((CharSequence)MimeType.APPLICATION_JSON.getValue())) {
                return parseJSON(readStreamAsString(capacitorHttpUrlConnection.getInputStream()));
            }
            final InputStream inputStream = capacitorHttpUrlConnection.getInputStream();
            final int n = HttpRequestHandler$1.$SwitchMap$com$getcapacitor$plugin$util$HttpRequestHandler$ResponseType[responseType.ordinal()];
            if (n == 1 || n == 2) {
                return readStreamAsBase64(inputStream);
            }
            if (n != 3) {
                return readStreamAsString(inputStream);
            }
            return parseJSON(readStreamAsString(inputStream));
        }
    }
    
    public static String readStreamAsBase64(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final byte[] array = new byte[1024];
            while (true) {
                final int read = inputStream.read(array);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(array, 0, read);
            }
            final byte[] byteArray = byteArrayOutputStream.toByteArray();
            final String encodeToString = Base64.encodeToString(byteArray, 0, byteArray.length, 0);
            byteArrayOutputStream.close();
            return encodeToString;
        }
        finally {
            try {
                byteArrayOutputStream.close();
            }
            finally {
                final Throwable t;
                ((Throwable)inputStream).addSuppressed(t);
            }
        }
    }
    
    public static String readStreamAsString(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader((Reader)new InputStreamReader(inputStream));
        try {
            final StringBuilder sb = new StringBuilder();
            String line2;
            for (String line = bufferedReader.readLine(); line != null; line = line2) {
                sb.append(line);
                line2 = bufferedReader.readLine();
                if ((line = line2) != null) {
                    sb.append(System.getProperty("line.separator"));
                }
            }
            final String string = sb.toString();
            bufferedReader.close();
            return string;
        }
        finally {
            try {
                bufferedReader.close();
            }
            finally {
                final Throwable t;
                ((Throwable)inputStream).addSuppressed(t);
            }
        }
    }
    
    public static JSObject request(final PluginCall pluginCall, String method, final Bridge sslSocketFactory) throws IOException, URISyntaxException, JSONException {
        final String string = pluginCall.getString("url", "");
        final JSObject object = pluginCall.getObject("headers", new JSObject());
        final JSObject object2 = pluginCall.getObject("params", new JSObject());
        final Integer int1 = pluginCall.getInt("connectTimeout");
        final Integer int2 = pluginCall.getInt("readTimeout");
        final Boolean boolean1 = pluginCall.getBoolean("disableRedirects");
        final Boolean boolean2 = pluginCall.getBoolean("shouldEncodeUrlParams", true);
        final ResponseType parse = ResponseType.parse(pluginCall.getString("responseType"));
        final String string2 = pluginCall.getString("dataType");
        if (method == null) {
            method = pluginCall.getString("method", "GET");
        }
        method = method.toUpperCase(Locale.ROOT);
        final boolean b = method.equals((Object)"DELETE") || method.equals((Object)"PATCH") || method.equals((Object)"POST") || method.equals((Object)"PUT");
        final URL url = new URL(string);
        final CapacitorHttpUrlConnection build = new HttpURLConnectionBuilder().setUrl(url).setMethod(method).setHeaders(object).setUrlParams(object2, boolean2).setConnectTimeout(int1).setReadTimeout(int2).setDisableRedirects(boolean1).openConnection().build();
        if (sslSocketFactory != null && !isDomainExcludedFromSSL(sslSocketFactory, url)) {
            build.setSSLSocketFactory(sslSocketFactory);
        }
        if (b) {
            final JSValue jsValue = new JSValue(pluginCall, "data");
            if (jsValue.getValue() != null) {
                build.setDoOutput(true);
                build.setRequestBody(pluginCall, jsValue, string2);
            }
        }
        pluginCall.getData().put("activeCapacitorHttpUrlConnection", build);
        build.connect();
        final JSObject buildResponse = buildResponse(build, parse);
        build.disconnect();
        pluginCall.getData().remove("activeCapacitorHttpUrlConnection");
        return buildResponse;
    }
    
    public static class HttpURLConnectionBuilder
    {
        public Integer connectTimeout;
        public CapacitorHttpUrlConnection connection;
        public Boolean disableRedirects;
        public JSObject headers;
        public String method;
        public Integer readTimeout;
        public URL url;
        
        public CapacitorHttpUrlConnection build() {
            return this.connection;
        }
        
        public HttpURLConnectionBuilder openConnection() throws IOException {
            (this.connection = new CapacitorHttpUrlConnection((HttpURLConnection)this.url.openConnection())).setAllowUserInteraction(false);
            this.connection.setRequestMethod(this.method);
            final Integer connectTimeout = this.connectTimeout;
            if (connectTimeout != null) {
                this.connection.setConnectTimeout((int)connectTimeout);
            }
            final Integer readTimeout = this.readTimeout;
            if (readTimeout != null) {
                this.connection.setReadTimeout((int)readTimeout);
            }
            final Boolean disableRedirects = this.disableRedirects;
            if (disableRedirects != null) {
                this.connection.setDisableRedirects((boolean)disableRedirects);
            }
            this.connection.setRequestHeaders(this.headers);
            return this;
        }
        
        public HttpURLConnectionBuilder setConnectTimeout(final Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }
        
        public HttpURLConnectionBuilder setDisableRedirects(final Boolean disableRedirects) {
            this.disableRedirects = disableRedirects;
            return this;
        }
        
        public HttpURLConnectionBuilder setHeaders(final JSObject headers) {
            this.headers = headers;
            return this;
        }
        
        public HttpURLConnectionBuilder setMethod(final String method) {
            this.method = method;
            return this;
        }
        
        public HttpURLConnectionBuilder setReadTimeout(final Integer readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }
        
        public HttpURLConnectionBuilder setUrl(final URL url) {
            this.url = url;
            return this;
        }
        
        public HttpURLConnectionBuilder setUrlParams(final JSObject jsObject) throws MalformedURLException, URISyntaxException, JSONException {
            return this.setUrlParams(jsObject, true);
        }
        
        public HttpURLConnectionBuilder setUrlParams(final JSObject jsObject, final boolean b) throws URISyntaxException, MalformedURLException {
            final String query = this.url.getQuery();
            final String s = "";
            String s2 = query;
            if (query == null) {
                s2 = "";
            }
            final Iterator keys = jsObject.keys();
            if (!keys.hasNext()) {
                return this;
            }
            final StringBuilder sb = new StringBuilder(s2);
            while (keys.hasNext()) {
                final String s3 = (String)keys.next();
                try {
                    final StringBuilder sb2 = new StringBuilder();
                    final JSONArray jsonArray = jsObject.getJSONArray(s3);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        sb2.append(s3);
                        sb2.append("=");
                        sb2.append(jsonArray.getString(i));
                        if (i != jsonArray.length() - 1) {
                            sb2.append("&");
                        }
                    }
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append((CharSequence)sb2);
                }
                catch (final JSONException ex) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(s3);
                    sb.append("=");
                    sb.append(jsObject.getString(s3));
                }
            }
            final String string = sb.toString();
            final URI uri = this.url.toURI();
            if (b) {
                this.url = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), string, uri.getFragment()).toURL();
            }
            else {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(uri.getScheme());
                sb3.append("://");
                sb3.append(uri.getAuthority());
                sb3.append(uri.getPath());
                String string2;
                if (!string.equals((Object)"")) {
                    final StringBuilder sb4 = new StringBuilder("?");
                    sb4.append(string);
                    string2 = sb4.toString();
                }
                else {
                    string2 = "";
                }
                sb3.append(string2);
                String fragment = s;
                if (uri.getFragment() != null) {
                    fragment = uri.getFragment();
                }
                sb3.append(fragment);
                this.url = new URL(sb3.toString());
            }
            return this;
        }
    }
    
    @FunctionalInterface
    public interface ProgressEmitter
    {
        void emit(final Integer p0, final Integer p1);
    }
    
    public enum ResponseType
    {
        private static final ResponseType[] $VALUES;
        
        ARRAY_BUFFER("arraybuffer"), 
        BLOB("blob");
        
        static final ResponseType DEFAULT;
        
        DOCUMENT("document"), 
        JSON("json"), 
        TEXT("text");
        
        private final String name;
        
        private static /* synthetic */ ResponseType[] $values() {
            return new ResponseType[] { ResponseType.ARRAY_BUFFER, ResponseType.BLOB, ResponseType.DOCUMENT, ResponseType.JSON, ResponseType.TEXT };
        }
        
        static {
            $VALUES = $values();
            final ResponseType default1;
            DEFAULT = default1;
        }
        
        private ResponseType(final String name) {
            this.name = name;
        }
        
        public static ResponseType parse(final String s) {
            for (final ResponseType responseType : values()) {
                if (responseType.name.equalsIgnoreCase(s)) {
                    return responseType;
                }
            }
            return ResponseType.DEFAULT;
        }
    }
}
