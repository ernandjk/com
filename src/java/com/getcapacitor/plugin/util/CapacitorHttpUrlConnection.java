package com.getcapacitor.plugin.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import com.getcapacitor.Bridge;
import java.net.ProtocolException;
import com.getcapacitor.JSObject;
import com.getcapacitor.JSValue;
import com.getcapacitor.PluginCall;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONException;
import java.io.IOException;
import java.util.Iterator;
import org.json.JSONObject;
import java.io.DataOutputStream;
import com.getcapacitor.JSArray;
import android.text.TextUtils;
import java.util.Locale;
import com.getcapacitor.Bridge$$ExternalSyntheticApiModelOutline0;
import android.os.Build$VERSION;
import java.net.HttpURLConnection;

public class CapacitorHttpUrlConnection implements ICapacitorHttpUrlConnection
{
    private final HttpURLConnection connection;
    
    public CapacitorHttpUrlConnection(final HttpURLConnection connection) {
        this.connection = connection;
        this.setDefaultRequestProperties();
    }
    
    private String buildDefaultAcceptLanguageProperty() {
        Locale locale;
        if (Build$VERSION.SDK_INT >= 24) {
            locale = Bridge$$ExternalSyntheticApiModelOutline0.m(Bridge$$ExternalSyntheticApiModelOutline0.m(), 0);
        }
        else {
            locale = Locale.getDefault();
        }
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        String s;
        if (!TextUtils.isEmpty((CharSequence)language)) {
            if (!TextUtils.isEmpty((CharSequence)country)) {
                s = String.format("%s-%s,%s;q=0.5", new Object[] { language, country, language });
            }
            else {
                s = String.format("%s;q=0.5", new Object[] { language });
            }
        }
        else {
            s = "";
        }
        return s;
    }
    
    private void setDefaultRequestProperties() {
        final String buildDefaultAcceptLanguageProperty = this.buildDefaultAcceptLanguageProperty();
        if (!TextUtils.isEmpty((CharSequence)buildDefaultAcceptLanguageProperty)) {
            this.connection.setRequestProperty("Accept-Language", buildDefaultAcceptLanguageProperty);
        }
    }
    
    private void writeFormDataRequestBody(String o, final JSArray jsArray) throws IOException, JSONException {
        final DataOutputStream dataOutputStream = new DataOutputStream(this.connection.getOutputStream());
        try {
            o = ((String)o).split(";")[1].split("=")[1];
            for (final Object next : jsArray.toList()) {
                if (next instanceof JSONObject) {
                    final JSONObject jsonObject = (JSONObject)next;
                    final String string = jsonObject.getString("type");
                    final String string2 = jsonObject.getString("key");
                    final String string3 = jsonObject.getString("value");
                    if (string.equals((Object)"string")) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("--");
                        sb.append((String)o);
                        sb.append("\r\n");
                        dataOutputStream.writeBytes(sb.toString());
                        final StringBuilder sb2 = new StringBuilder();
                        sb2.append("Content-Disposition: form-data; name=\"");
                        sb2.append(string2);
                        sb2.append("\"");
                        sb2.append("\r\n");
                        sb2.append("\r\n");
                        dataOutputStream.writeBytes(sb2.toString());
                        dataOutputStream.writeBytes(string3);
                        dataOutputStream.writeBytes("\r\n");
                    }
                    else {
                        if (!string.equals((Object)"base64File")) {
                            continue;
                        }
                        final String string4 = jsonObject.getString("fileName");
                        final String string5 = jsonObject.getString("contentType");
                        final StringBuilder sb3 = new StringBuilder();
                        sb3.append("--");
                        sb3.append((String)o);
                        sb3.append("\r\n");
                        dataOutputStream.writeBytes(sb3.toString());
                        final StringBuilder sb4 = new StringBuilder();
                        sb4.append("Content-Disposition: form-data; name=\"");
                        sb4.append(string2);
                        sb4.append("\"; filename=\"");
                        sb4.append(string4);
                        sb4.append("\"");
                        sb4.append("\r\n");
                        dataOutputStream.writeBytes(sb4.toString());
                        final StringBuilder sb5 = new StringBuilder();
                        sb5.append("Content-Type: ");
                        sb5.append(string5);
                        sb5.append("\r\n");
                        dataOutputStream.writeBytes(sb5.toString());
                        final StringBuilder sb6 = new StringBuilder();
                        sb6.append("Content-Transfer-Encoding: binary");
                        sb6.append("\r\n");
                        dataOutputStream.writeBytes(sb6.toString());
                        dataOutputStream.writeBytes("\r\n");
                        if (Build$VERSION.SDK_INT >= 26) {
                            dataOutputStream.write(Bridge$$ExternalSyntheticApiModelOutline0.m(Bridge$$ExternalSyntheticApiModelOutline0.m(), string3));
                        }
                        dataOutputStream.writeBytes("\r\n");
                    }
                }
            }
            final StringBuilder sb7 = new StringBuilder();
            sb7.append("--");
            sb7.append((String)o);
            sb7.append("--");
            sb7.append("\r\n");
            dataOutputStream.writeBytes(sb7.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
        }
        finally {
            try {
                dataOutputStream.close();
            }
            finally {
                final Throwable t;
                ((Throwable)o).addSuppressed(t);
            }
        }
    }
    
    private void writeRequestBody(final String s) throws IOException {
        final DataOutputStream dataOutputStream = new DataOutputStream(this.connection.getOutputStream());
        try {
            dataOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
            dataOutputStream.flush();
            dataOutputStream.close();
        }
        finally {
            try {
                dataOutputStream.close();
            }
            finally {
                final Throwable t;
                ((Throwable)s).addSuppressed(t);
            }
        }
    }
    
    public void connect() throws IOException {
        this.connection.connect();
    }
    
    public void disconnect() {
        this.connection.disconnect();
    }
    
    public InputStream getErrorStream() {
        return this.connection.getErrorStream();
    }
    
    public String getHeaderField(final String s) {
        return this.connection.getHeaderField(s);
    }
    
    public Map<String, List<String>> getHeaderFields() {
        return (Map<String, List<String>>)this.connection.getHeaderFields();
    }
    
    public HttpURLConnection getHttpConnection() {
        return this.connection;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.connection.getInputStream();
    }
    
    public int getResponseCode() throws IOException {
        return this.connection.getResponseCode();
    }
    
    public URL getURL() {
        return this.connection.getURL();
    }
    
    public void setAllowUserInteraction(final boolean allowUserInteraction) {
        this.connection.setAllowUserInteraction(allowUserInteraction);
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        if (connectTimeout >= 0) {
            this.connection.setConnectTimeout(connectTimeout);
            return;
        }
        throw new IllegalArgumentException("timeout can not be negative");
    }
    
    public void setDisableRedirects(final boolean b) {
        this.connection.setInstanceFollowRedirects(b ^ true);
    }
    
    public void setDoOutput(final boolean doOutput) {
        this.connection.setDoOutput(doOutput);
    }
    
    public void setReadTimeout(final int readTimeout) {
        if (readTimeout >= 0) {
            this.connection.setReadTimeout(readTimeout);
            return;
        }
        throw new IllegalArgumentException("timeout can not be negative");
    }
    
    public void setRequestBody(final PluginCall pluginCall, final JSValue jsValue) throws JSONException, IOException {
        this.setRequestBody(pluginCall, jsValue, null);
    }
    
    public void setRequestBody(PluginCall pluginCall, final JSValue jsValue, String s) throws JSONException, IOException {
        final String requestProperty = this.connection.getRequestProperty("Content-Type");
        if (requestProperty != null) {
            if (!requestProperty.isEmpty()) {
                if (requestProperty.contains((CharSequence)"application/json")) {
                    JSArray array = null;
                    final String s2 = "";
                    if (jsValue != null) {
                        s = jsValue.toString();
                    }
                    else {
                        array = pluginCall.getArray("data", (JSArray)null);
                        s = "";
                    }
                    if (array != null) {
                        s = array.toString();
                    }
                    else if (jsValue == null) {
                        s = pluginCall.getString("data");
                    }
                    String s3 = s2;
                    if (s != null) {
                        s3 = s;
                    }
                    this.writeRequestBody(s3);
                }
                else {
                    if (s != null && s.equals((Object)"file")) {
                        pluginCall = (PluginCall)new DataOutputStream(this.connection.getOutputStream());
                        try {
                            if (Build$VERSION.SDK_INT >= 26) {
                                ((DataOutputStream)pluginCall).write(Bridge$$ExternalSyntheticApiModelOutline0.m(Bridge$$ExternalSyntheticApiModelOutline0.m(), jsValue.toString()));
                            }
                            ((DataOutputStream)pluginCall).flush();
                            ((DataOutputStream)pluginCall).close();
                            return;
                        }
                        finally {
                            try {
                                ((DataOutputStream)pluginCall).close();
                            }
                            finally {
                                final Throwable t;
                                ((Throwable)jsValue).addSuppressed(t);
                            }
                        }
                    }
                    if (s != null && s.equals((Object)"formData")) {
                        this.writeFormDataRequestBody(requestProperty, jsValue.toJSArray());
                    }
                    else {
                        this.writeRequestBody(jsValue.toString());
                    }
                }
            }
        }
    }
    
    public void setRequestHeaders(final JSObject jsObject) {
        final Iterator keys = jsObject.keys();
        while (keys.hasNext()) {
            final String s = (String)keys.next();
            this.connection.setRequestProperty(s, jsObject.getString(s));
        }
    }
    
    public void setRequestMethod(final String requestMethod) throws ProtocolException {
        this.connection.setRequestMethod(requestMethod);
    }
    
    public void setSSLSocketFactory(final Bridge bridge) {
        try {
            final Class<?> forName = Class.forName("io.ionic.sslpinning.SSLPinning");
            final SSLSocketFactory sslSocketFactory = (SSLSocketFactory)forName.getDeclaredMethod("getSSLSocketFactory", Bridge.class).invoke(forName.newInstance(), new Object[] { bridge });
            if (sslSocketFactory != null) {
                ((HttpsURLConnection)this.connection).setSSLSocketFactory(sslSocketFactory);
            }
        }
        catch (final Exception ex) {}
    }
}
