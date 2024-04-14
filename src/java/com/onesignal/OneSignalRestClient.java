package com.onesignal;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.net.ConnectException;
import java.util.Scanner;
import android.net.TrafficStats;
import android.os.Build$VERSION;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONObject;

class OneSignalRestClient
{
    private static final String BASE_URL = "https://api.onesignal.com/";
    static final String CACHE_KEY_GET_TAGS = "CACHE_KEY_GET_TAGS";
    static final String CACHE_KEY_REMOTE_PARAMS = "CACHE_KEY_REMOTE_PARAMS";
    private static final int GET_TIMEOUT = 60000;
    private static final String OS_ACCEPT_HEADER = "application/vnd.onesignal.v1+json";
    private static final String OS_API_VERSION = "1";
    private static final int THREAD_ID = 10000;
    private static final int TIMEOUT = 120000;
    
    private static Thread callResponseHandlerOnFailure(final ResponseHandler responseHandler, final int n, final String s, final Throwable t) {
        if (responseHandler == null) {
            return null;
        }
        final Thread thread = new Thread((Runnable)new Runnable(responseHandler, n, s, t) {
            final ResponseHandler val$handler;
            final String val$response;
            final int val$statusCode;
            final Throwable val$throwable;
            
            public void run() {
                this.val$handler.onFailure(this.val$statusCode, this.val$response, this.val$throwable);
            }
        }, "OS_REST_FAILURE_CALLBACK");
        thread.start();
        return thread;
    }
    
    private static Thread callResponseHandlerOnSuccess(final ResponseHandler responseHandler, final String s) {
        if (responseHandler == null) {
            return null;
        }
        final Thread thread = new Thread((Runnable)new Runnable(responseHandler, s) {
            final ResponseHandler val$handler;
            final String val$response;
            
            public void run() {
                this.val$handler.onSuccess(this.val$response);
            }
        }, "OS_REST_SUCCESS_CALLBACK");
        thread.start();
        return thread;
    }
    
    public static void get(final String s, final ResponseHandler responseHandler, final String s2) {
        new Thread((Runnable)new Runnable(s, responseHandler, s2) {
            final String val$cacheKey;
            final ResponseHandler val$responseHandler;
            final String val$url;
            
            public void run() {
                makeRequest(this.val$url, null, null, this.val$responseHandler, 60000, this.val$cacheKey);
            }
        }, "OS_REST_ASYNC_GET").start();
    }
    
    public static void getSync(final String s, final ResponseHandler responseHandler, final String s2) {
        makeRequest(s, null, null, responseHandler, 60000, s2);
    }
    
    private static int getThreadTimeout(final int n) {
        return n + 5000;
    }
    
    private static void makeRequest(final String s, final String s2, final JSONObject jsonObject, final ResponseHandler responseHandler, final int n, final String s3) {
        if (OSUtils.isRunningOnMainThread()) {
            final StringBuilder sb = new StringBuilder("Method: ");
            sb.append(s2);
            sb.append(" was called from the Main Thread!");
            throw new OSThrowable.OSMainThreadException(sb.toString());
        }
        if (s2 != null && OneSignal.shouldLogUserPrivacyConsentErrorMessageForMethodName(null)) {
            return;
        }
        final Thread[] array = { null };
        final Thread thread = new Thread((Runnable)new Runnable(array, s, s2, jsonObject, responseHandler, n, s3) {
            final String val$cacheKey;
            final Thread[] val$callbackThread;
            final JSONObject val$jsonBody;
            final String val$method;
            final ResponseHandler val$responseHandler;
            final int val$timeout;
            final String val$url;
            
            public void run() {
                this.val$callbackThread[0] = startHTTPConnection(this.val$url, this.val$method, this.val$jsonBody, this.val$responseHandler, this.val$timeout, this.val$cacheKey);
            }
        }, "OS_HTTPConnection");
        thread.start();
        try {
            thread.join((long)getThreadTimeout(n));
            if (thread.getState() != Thread$State.TERMINATED) {
                thread.interrupt();
            }
            final Thread thread2 = array[0];
            if (thread2 != null) {
                thread2.join();
            }
        }
        catch (final InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    private static HttpURLConnection newHttpURLConnection(final String s) throws IOException {
        final StringBuilder sb = new StringBuilder("https://api.onesignal.com/");
        sb.append(s);
        return (HttpURLConnection)new URL(sb.toString()).openConnection();
    }
    
    public static void post(final String s, final JSONObject jsonObject, final ResponseHandler responseHandler) {
        new Thread((Runnable)new Runnable(s, jsonObject, responseHandler) {
            final JSONObject val$jsonBody;
            final ResponseHandler val$responseHandler;
            final String val$url;
            
            public void run() {
                makeRequest(this.val$url, "POST", this.val$jsonBody, this.val$responseHandler, 120000, null);
            }
        }, "OS_REST_ASYNC_POST").start();
    }
    
    public static void postSync(final String s, final JSONObject jsonObject, final ResponseHandler responseHandler) {
        makeRequest(s, "POST", jsonObject, responseHandler, 120000, null);
    }
    
    public static void put(final String s, final JSONObject jsonObject, final ResponseHandler responseHandler) {
        new Thread((Runnable)new Runnable(s, jsonObject, responseHandler) {
            final JSONObject val$jsonBody;
            final ResponseHandler val$responseHandler;
            final String val$url;
            
            public void run() {
                makeRequest(this.val$url, "PUT", this.val$jsonBody, this.val$responseHandler, 120000, null);
            }
        }, "OS_REST_ASYNC_PUT").start();
    }
    
    public static void putSync(final String s, final JSONObject jsonObject, final ResponseHandler responseHandler) {
        makeRequest(s, "PUT", jsonObject, responseHandler, 120000, null);
    }
    
    private static Thread startHTTPConnection(String s, String requestMethod, final JSONObject jsonObject, final ResponseHandler responseHandler, int responseCode, final String s2) {
        if (Build$VERSION.SDK_INT >= 26) {
            TrafficStats.setThreadStatsTag(10000);
        }
        while (true) {
            while (true) {
                HttpURLConnection httpURLConnection;
                try {
                    final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
                    final StringBuilder sb = new StringBuilder("OneSignalRestClient: Making request to: https://api.onesignal.com/");
                    sb.append(s);
                    OneSignal.Log(debug, sb.toString());
                    httpURLConnection = newHttpURLConnection(s);
                    try {
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setConnectTimeout(responseCode);
                        httpURLConnection.setReadTimeout(responseCode);
                        final StringBuilder sb2 = new StringBuilder("onesignal/android/");
                        sb2.append(OneSignal.getSdkVersionRaw());
                        httpURLConnection.setRequestProperty("SDK-Version", sb2.toString());
                        httpURLConnection.setRequestProperty("Accept", "application/vnd.onesignal.v1+json");
                        if (jsonObject != null) {
                            httpURLConnection.setDoInput(true);
                        }
                        if (requestMethod != null) {
                            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                            httpURLConnection.setRequestMethod(requestMethod);
                            httpURLConnection.setDoOutput(true);
                        }
                        Label_0235: {
                            if (jsonObject == null) {
                                break Label_0235;
                            }
                            final String unescapedEUIDString = JSONUtils.toUnescapedEUIDString(jsonObject);
                            final OneSignal.LOG_LEVEL debug2 = OneSignal.LOG_LEVEL.DEBUG;
                            final StringBuilder sb3 = new StringBuilder("OneSignalRestClient: ");
                            sb3.append(requestMethod);
                            try {
                                sb3.append(" SEND JSON: ");
                                sb3.append(unescapedEUIDString);
                                OneSignal.Log(debug2, sb3.toString());
                                final byte[] bytes = unescapedEUIDString.getBytes("UTF-8");
                                httpURLConnection.setFixedLengthStreamingMode(bytes.length);
                                httpURLConnection.getOutputStream().write(bytes);
                                if (s2 != null) {
                                    final String prefs_ONESIGNAL = OneSignalPrefs.PREFS_ONESIGNAL;
                                    final StringBuilder sb4 = new StringBuilder("PREFS_OS_ETAG_PREFIX_");
                                    sb4.append(s2);
                                    final String string = OneSignalPrefs.getString(prefs_ONESIGNAL, sb4.toString(), null);
                                    if (string != null) {
                                        httpURLConnection.setRequestProperty("if-none-match", string);
                                        final OneSignal.LOG_LEVEL debug3 = OneSignal.LOG_LEVEL.DEBUG;
                                        final StringBuilder sb5 = new StringBuilder("OneSignalRestClient: Adding header if-none-match: ");
                                        sb5.append(string);
                                        OneSignal.Log(debug3, sb5.toString());
                                    }
                                }
                                responseCode = httpURLConnection.getResponseCode();
                                try {
                                    final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
                                    final StringBuilder sb6 = new StringBuilder("OneSignalRestClient: After con.getResponseCode to: https://api.onesignal.com/");
                                    sb6.append(s);
                                    OneSignal.Log(verbose, sb6.toString());
                                    Object o;
                                    if (responseCode != 200 && responseCode != 202) {
                                        if (responseCode != 304) {
                                            final OneSignal.LOG_LEVEL debug4 = OneSignal.LOG_LEVEL.DEBUG;
                                            final StringBuilder sb7 = new StringBuilder("OneSignalRestClient: Failed request to: https://api.onesignal.com/");
                                            sb7.append(s);
                                            OneSignal.Log(debug4, sb7.toString());
                                            InputStream inputStream;
                                            if ((inputStream = httpURLConnection.getErrorStream()) == null) {
                                                inputStream = httpURLConnection.getInputStream();
                                            }
                                            if (inputStream != null) {
                                                final Scanner scanner = new Scanner(inputStream, "UTF-8");
                                                if (scanner.useDelimiter("\\A").hasNext()) {
                                                    s = scanner.next();
                                                }
                                                else {
                                                    s = "";
                                                }
                                                scanner.close();
                                                final OneSignal.LOG_LEVEL warn = OneSignal.LOG_LEVEL.WARN;
                                                final StringBuilder sb8 = new StringBuilder("OneSignalRestClient: ");
                                                sb8.append(requestMethod);
                                                sb8.append(" RECEIVED JSON: ");
                                                sb8.append(s);
                                                OneSignal.Log(warn, sb8.toString());
                                            }
                                            else {
                                                final OneSignal.LOG_LEVEL warn2 = OneSignal.LOG_LEVEL.WARN;
                                                final StringBuilder sb9 = new StringBuilder("OneSignalRestClient: ");
                                                sb9.append(requestMethod);
                                                sb9.append(" HTTP Code: ");
                                                sb9.append(responseCode);
                                                sb9.append(" No response body!");
                                                OneSignal.Log(warn2, sb9.toString());
                                                s = null;
                                            }
                                            o = callResponseHandlerOnFailure(responseHandler, responseCode, s, null);
                                        }
                                        else {
                                            final String prefs_ONESIGNAL2 = OneSignalPrefs.PREFS_ONESIGNAL;
                                            final StringBuilder sb10 = new StringBuilder("PREFS_OS_HTTP_CACHE_PREFIX_");
                                            sb10.append(s2);
                                            final String string2 = OneSignalPrefs.getString(prefs_ONESIGNAL2, sb10.toString(), null);
                                            final OneSignal.LOG_LEVEL debug5 = OneSignal.LOG_LEVEL.DEBUG;
                                            final StringBuilder sb11 = new StringBuilder("OneSignalRestClient: ");
                                            if (requestMethod == null) {
                                                s = "GET";
                                            }
                                            else {
                                                s = requestMethod;
                                            }
                                            sb11.append(s);
                                            sb11.append(" - Using Cached response due to 304: ");
                                            sb11.append(string2);
                                            OneSignal.Log(debug5, sb11.toString());
                                            o = callResponseHandlerOnSuccess(responseHandler, string2);
                                        }
                                    }
                                    else {
                                        final OneSignal.LOG_LEVEL debug6 = OneSignal.LOG_LEVEL.DEBUG;
                                        final StringBuilder sb12 = new StringBuilder("OneSignalRestClient: Successfully finished request to: https://api.onesignal.com/");
                                        sb12.append(s);
                                        OneSignal.Log(debug6, sb12.toString());
                                        final Scanner scanner2 = new Scanner(httpURLConnection.getInputStream(), "UTF-8");
                                        if (scanner2.useDelimiter("\\A").hasNext()) {
                                            s = scanner2.next();
                                        }
                                        else {
                                            s = "";
                                        }
                                        scanner2.close();
                                        final OneSignal.LOG_LEVEL debug7 = OneSignal.LOG_LEVEL.DEBUG;
                                        final StringBuilder sb13 = new StringBuilder("OneSignalRestClient: ");
                                        String s3;
                                        if (requestMethod == null) {
                                            s3 = "GET";
                                        }
                                        else {
                                            s3 = requestMethod;
                                        }
                                        sb13.append(s3);
                                        sb13.append(" RECEIVED JSON: ");
                                        sb13.append(s);
                                        OneSignal.Log(debug7, sb13.toString());
                                        if (s2 != null) {
                                            final String headerField = httpURLConnection.getHeaderField("etag");
                                            if (headerField != null) {
                                                final OneSignal.LOG_LEVEL debug8 = OneSignal.LOG_LEVEL.DEBUG;
                                                final StringBuilder sb14 = new StringBuilder("OneSignalRestClient: Response has etag of ");
                                                sb14.append(headerField);
                                                sb14.append(" so caching the response.");
                                                OneSignal.Log(debug8, sb14.toString());
                                                final String prefs_ONESIGNAL3 = OneSignalPrefs.PREFS_ONESIGNAL;
                                                final StringBuilder sb15 = new StringBuilder("PREFS_OS_ETAG_PREFIX_");
                                                sb15.append(s2);
                                                OneSignalPrefs.saveString(prefs_ONESIGNAL3, sb15.toString(), headerField);
                                                final String prefs_ONESIGNAL4 = OneSignalPrefs.PREFS_ONESIGNAL;
                                                final StringBuilder sb16 = new StringBuilder("PREFS_OS_HTTP_CACHE_PREFIX_");
                                                sb16.append(s2);
                                                OneSignalPrefs.saveString(prefs_ONESIGNAL4, sb16.toString(), s);
                                            }
                                        }
                                        o = callResponseHandlerOnSuccess(responseHandler, s);
                                    }
                                    requestMethod = (String)o;
                                    if (httpURLConnection != null) {
                                        httpURLConnection.disconnect();
                                        requestMethod = (String)o;
                                        return (Thread)requestMethod;
                                    }
                                    return (Thread)requestMethod;
                                }
                                finally {}
                            }
                            finally {}
                        }
                    }
                    finally {}
                }
                finally {
                    httpURLConnection = null;
                }
                responseCode = -1;
                try {
                    final Throwable t;
                    if (!(t instanceof ConnectException) && !(t instanceof UnknownHostException)) {
                        final OneSignal.LOG_LEVEL warn3 = OneSignal.LOG_LEVEL.WARN;
                        final StringBuilder sb17 = new StringBuilder("OneSignalRestClient: ");
                        sb17.append(requestMethod);
                        sb17.append(" Error thrown from network stack. ");
                        OneSignal.Log(warn3, sb17.toString(), t);
                    }
                    else {
                        requestMethod = (String)OneSignal.LOG_LEVEL.INFO;
                        final StringBuilder sb18 = new StringBuilder("OneSignalRestClient: Could not send last request, device is offline. Throwable: ");
                        sb18.append(t.getClass().getName());
                        OneSignal.Log((OneSignal.LOG_LEVEL)requestMethod, sb18.toString());
                    }
                    requestMethod = (String)callResponseHandlerOnFailure(responseHandler, responseCode, null, t);
                    if (httpURLConnection != null) {
                        continue;
                    }
                    return (Thread)requestMethod;
                }
                finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
                break;
            }
        }
    }
    
    abstract static class ResponseHandler
    {
        void onFailure(final int n, final String s, final Throwable t) {
        }
        
        void onSuccess(final String s) {
        }
    }
}
