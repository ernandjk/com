package com.getcapacitor;

import android.view.View;
import org.apache.cordova.CordovaInterface;
import com.getcapacitor.cordova.MockCordovaWebViewImpl;
import com.getcapacitor.android.R;
import org.apache.cordova.ConfigXmlParser;
import com.getcapacitor.util.PermissionHelper;
import android.app.Activity;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import org.json.JSONException;
import android.content.res.Configuration;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import java.util.Map$Entry;
import java.util.regex.Matcher;
import android.content.pm.PackageManager;
import java.util.regex.Pattern;
import android.os.Build$VERSION;
import java.net.SocketTimeoutException;
import com.getcapacitor.annotation.Permission;
import androidx.core.app.ActivityCompat;
import java.util.Arrays;
import java.util.Iterator;
import com.getcapacitor.plugin.CapacitorHttp;
import com.getcapacitor.plugin.CapacitorCookies;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.content.SharedPreferences$Editor;
import android.content.pm.PackageInfo;
import android.content.SharedPreferences;
import androidx.core.content.pm.PackageInfoCompat;
import com.getcapacitor.util.InternalUtils;
import android.webkit.WebSettings;
import java.net.URL;
import com.getcapacitor.util.WebColor;
import java.util.Collection;
import android.content.Context;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.cordova.PluginManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.os.Handler;
import java.util.LinkedList;
import org.apache.cordova.CordovaPreferences;
import java.util.Map;
import android.net.Uri;
import java.util.List;
import android.os.HandlerThread;
import androidx.fragment.app.Fragment;
import org.apache.cordova.CordovaWebView;
import com.getcapacitor.cordova.MockCordovaInterfaceImpl;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import com.getcapacitor.util.HostMask;
import java.util.Set;

public class Bridge
{
    private static final String BUNDLE_LAST_PLUGIN_CALL_METHOD_NAME_KEY = "capacitorLastActivityPluginMethod";
    private static final String BUNDLE_LAST_PLUGIN_ID_KEY = "capacitorLastActivityPluginId";
    private static final String BUNDLE_PLUGIN_CALL_BUNDLE_KEY = "capacitorLastPluginCallBundle";
    private static final String BUNDLE_PLUGIN_CALL_OPTIONS_SAVED_KEY = "capacitorLastPluginCallOptions";
    public static final String CAPACITOR_CONTENT_START = "/_capacitor_content_";
    public static final String CAPACITOR_FILE_START = "/_capacitor_file_";
    public static final String CAPACITOR_HTTPS_SCHEME = "https";
    public static final String CAPACITOR_HTTP_SCHEME = "http";
    public static final int DEFAULT_ANDROID_WEBVIEW_VERSION = 60;
    public static final int DEFAULT_HUAWEI_WEBVIEW_VERSION = 10;
    public static final String DEFAULT_WEB_ASSET_DIR = "public";
    private static final String LAST_BINARY_VERSION_CODE = "lastBinaryVersionCode";
    private static final String LAST_BINARY_VERSION_NAME = "lastBinaryVersionName";
    private static final String MINIMUM_ANDROID_WEBVIEW_ERROR = "System WebView is not supported";
    public static final int MINIMUM_ANDROID_WEBVIEW_VERSION = 55;
    public static final int MINIMUM_HUAWEI_WEBVIEW_VERSION = 10;
    private static final String PERMISSION_PREFS_NAME = "PluginPermStates";
    private static final String PREFS_NAME = "CapacitorSettings";
    private Set<String> allowedOriginRules;
    private App app;
    private HostMask appAllowNavigationMask;
    private String appUrl;
    private String appUrlConfig;
    private ArrayList<String> authorities;
    private CapConfig config;
    private final AppCompatActivity context;
    public final MockCordovaInterfaceImpl cordovaInterface;
    private CordovaWebView cordovaWebView;
    private final Fragment fragment;
    private final HandlerThread handlerThread;
    private final List<Class<? extends Plugin>> initialPlugins;
    private Uri intentUri;
    private WebViewLocalServer localServer;
    private String localUrl;
    private final MessageHandler msgHandler;
    private PluginCall pluginCallForLastActivity;
    private final List<Plugin> pluginInstances;
    private Map<String, PluginHandle> plugins;
    private CordovaPreferences preferences;
    private RouteProcessor routeProcessor;
    private Map<String, PluginCall> savedCalls;
    private Map<String, LinkedList<String>> savedPermissionCallIds;
    private ServerPath serverPath;
    private Handler taskHandler;
    private final WebView webView;
    private BridgeWebViewClient webViewClient;
    private List<WebViewListener> webViewListeners;
    
    @Deprecated
    public Bridge(final AppCompatActivity appCompatActivity, final WebView webView, final List<Class<? extends Plugin>> list, final MockCordovaInterfaceImpl mockCordovaInterfaceImpl, final PluginManager pluginManager, final CordovaPreferences cordovaPreferences, final CapConfig capConfig) {
        this(appCompatActivity, null, null, webView, list, (List<Plugin>)new ArrayList(), mockCordovaInterfaceImpl, pluginManager, cordovaPreferences, capConfig);
    }
    
    private Bridge(final AppCompatActivity context, final ServerPath serverPath, final Fragment fragment, final WebView webView, final List<Class<? extends Plugin>> initialPlugins, final List<Plugin> pluginInstances, final MockCordovaInterfaceImpl cordovaInterface, final PluginManager pluginManager, final CordovaPreferences preferences, CapConfig loadDefault) {
        this.allowedOriginRules = (Set<String>)new HashSet();
        this.authorities = (ArrayList<String>)new ArrayList();
        final HandlerThread handlerThread = new HandlerThread("CapacitorPlugins");
        this.handlerThread = handlerThread;
        this.taskHandler = null;
        this.plugins = (Map<String, PluginHandle>)new HashMap();
        this.savedCalls = (Map<String, PluginCall>)new HashMap();
        this.savedPermissionCallIds = (Map<String, LinkedList<String>>)new HashMap();
        this.webViewListeners = (List<WebViewListener>)new ArrayList();
        this.app = new App();
        this.serverPath = serverPath;
        this.context = context;
        this.fragment = fragment;
        this.webView = webView;
        this.webViewClient = new BridgeWebViewClient(this);
        this.initialPlugins = initialPlugins;
        this.pluginInstances = pluginInstances;
        this.cordovaInterface = cordovaInterface;
        this.preferences = preferences;
        handlerThread.start();
        this.taskHandler = new Handler(handlerThread.getLooper());
        if (loadDefault == null) {
            loadDefault = CapConfig.loadDefault((Context)this.getActivity());
        }
        Logger.init(this.config = loadDefault);
        this.initWebView();
        this.setAllowedOriginRules();
        this.msgHandler = new MessageHandler(this, webView, pluginManager);
        this.intentUri = context.getIntent().getData();
        this.registerAllPlugins();
        this.loadWebView();
    }
    
    private JSInjector getJSInjector() {
        try {
            final String globalJS = JSExport.getGlobalJS((Context)this.context, this.config.isLoggingEnabled(), this.isDevMode());
            final String bridgeJS = JSExport.getBridgeJS((Context)this.context);
            final String pluginJS = JSExport.getPluginJS((Collection<PluginHandle>)this.plugins.values());
            final String cordovaJS = JSExport.getCordovaJS((Context)this.context);
            final String cordovaPluginJS = JSExport.getCordovaPluginJS((Context)this.context);
            final String cordovaPluginsFileJS = JSExport.getCordovaPluginsFileJS((Context)this.context);
            final StringBuilder sb = new StringBuilder("window.WEBVIEW_SERVER_URL = '");
            sb.append(this.localUrl);
            sb.append("';");
            return new JSInjector(globalJS, bridgeJS, pluginJS, cordovaJS, cordovaPluginJS, cordovaPluginsFileJS, sb.toString());
        }
        catch (final Exception ex) {
            Logger.error("Unable to export Capacitor JS. App will not function!", (Throwable)ex);
            return null;
        }
    }
    
    private String getLegacyPluginName(final Class<? extends Plugin> clazz) {
        final NativePlugin nativePlugin = clazz.getAnnotation(NativePlugin.class);
        if (nativePlugin == null) {
            Logger.error("Plugin doesn't have the @CapacitorPlugin annotation. Please add it");
            return null;
        }
        return nativePlugin.name();
    }
    
    private void initWebView() {
        final WebSettings settings = this.webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (this.config.isMixedContentAllowed()) {
            settings.setMixedContentMode(0);
        }
        final String appendedUserAgentString = this.config.getAppendedUserAgentString();
        if (appendedUserAgentString != null) {
            final String userAgentString = settings.getUserAgentString();
            final StringBuilder sb = new StringBuilder();
            sb.append(userAgentString);
            sb.append(" ");
            sb.append(appendedUserAgentString);
            settings.setUserAgentString(sb.toString());
        }
        final String overriddenUserAgentString = this.config.getOverriddenUserAgentString();
        if (overriddenUserAgentString != null) {
            settings.setUserAgentString(overriddenUserAgentString);
        }
        final String backgroundColor = this.config.getBackgroundColor();
        if (backgroundColor != null) {
            try {
                this.webView.setBackgroundColor(WebColor.parseColor(backgroundColor));
            }
            catch (final IllegalArgumentException ex) {
                Logger.debug("WebView background color not applied");
            }
        }
        if (this.config.isInitialFocus()) {
            this.webView.requestFocusFromTouch();
        }
        WebView.setWebContentsDebuggingEnabled(this.config.isWebContentsDebuggingEnabled());
        this.appUrlConfig = this.getServerUrl();
        final String host = this.getHost();
        this.authorities.add((Object)host);
        final String scheme = this.getScheme();
        final StringBuilder sb2 = new StringBuilder();
        sb2.append(scheme);
        sb2.append("://");
        sb2.append(host);
        final String string = sb2.toString();
        this.localUrl = string;
        Label_0388: {
            if (this.appUrlConfig != null) {
                try {
                    this.authorities.add((Object)new URL(this.appUrlConfig).getAuthority());
                    final String appUrlConfig = this.appUrlConfig;
                    this.localUrl = appUrlConfig;
                    this.appUrl = appUrlConfig;
                    break Label_0388;
                }
                catch (final Exception ex2) {
                    final StringBuilder sb3 = new StringBuilder("Provided server url is invalid: ");
                    sb3.append(ex2.getMessage());
                    Logger.error(sb3.toString());
                    return;
                }
            }
            this.appUrl = string;
            if (!scheme.equals((Object)"http") && !scheme.equals((Object)"https")) {
                final StringBuilder sb4 = new StringBuilder();
                sb4.append(this.appUrl);
                sb4.append("/");
                this.appUrl = sb4.toString();
            }
        }
        final String startPath = this.config.getStartPath();
        if (startPath != null && !startPath.trim().isEmpty()) {
            final StringBuilder sb5 = new StringBuilder();
            sb5.append(this.appUrl);
            sb5.append(startPath);
            this.appUrl = sb5.toString();
        }
    }
    
    private boolean isNewBinary() {
        final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("CapWebViewSettings", 0);
        final String string = sharedPreferences.getString("lastBinaryVersionCode", (String)null);
        final String string2 = sharedPreferences.getString("lastBinaryVersionName", (String)null);
        String string3;
        try {
            final PackageInfo packageInfo = InternalUtils.getPackageInfo(this.getContext().getPackageManager(), this.getContext().getPackageName());
            string3 = Integer.toString((int)PackageInfoCompat.getLongVersionCode(packageInfo));
            try {
                final String versionName = packageInfo.versionName;
            }
            catch (final Exception ex) {}
        }
        catch (final Exception ex) {
            string3 = "";
        }
        final Exception ex;
        Logger.error("Unable to get package info", (Throwable)ex);
        final String versionName = "";
        if (string3.equals((Object)string) && versionName.equals((Object)string2)) {
            return false;
        }
        final SharedPreferences$Editor edit = sharedPreferences.edit();
        edit.putString("lastBinaryVersionCode", string3);
        edit.putString("lastBinaryVersionName", versionName);
        edit.putString("serverBasePath", "");
        edit.apply();
        return true;
    }
    
    private void loadWebView() {
        (this.localServer = new WebViewLocalServer((Context)this.context, this, this.getJSInjector(), this.authorities, this.config.isHTML5Mode())).hostAssets("public");
        final StringBuilder sb = new StringBuilder("Loading app at ");
        sb.append(this.appUrl);
        Logger.debug(sb.toString());
        this.webView.setWebChromeClient((WebChromeClient)new BridgeWebChromeClient(this));
        this.webView.setWebViewClient((WebViewClient)this.webViewClient);
        if (!this.isDeployDisabled() && !this.isNewBinary()) {
            final String string = this.getContext().getSharedPreferences("CapWebViewSettings", 0).getString("serverBasePath", (String)null);
            if (string != null && !string.isEmpty() && new File(string).exists()) {
                this.setServerBasePath(string);
            }
        }
        if (!this.isMinimumWebViewInstalled()) {
            final String errorUrl = this.getErrorUrl();
            if (errorUrl != null) {
                this.webView.loadUrl(errorUrl);
                return;
            }
            Logger.error("System WebView is not supported");
        }
        final ServerPath serverPath = this.serverPath;
        if (serverPath != null) {
            if (serverPath.getType() == ServerPath.PathType.ASSET_PATH) {
                this.setServerAssetPath(this.serverPath.getPath());
            }
            else {
                this.setServerBasePath(this.serverPath.getPath());
            }
        }
        else {
            this.webView.loadUrl(this.appUrl);
        }
    }
    
    private void logInvalidPluginException(final Class<? extends Plugin> clazz) {
        final StringBuilder sb = new StringBuilder("NativePlugin ");
        sb.append(clazz.getName());
        sb.append(" is invalid. Ensure the @CapacitorPlugin annotation exists on the plugin class and the class extends Plugin");
        Logger.error(sb.toString());
    }
    
    private void logPluginLoadException(final Class<? extends Plugin> clazz, final Exception ex) {
        final StringBuilder sb = new StringBuilder("NativePlugin ");
        sb.append(clazz.getName());
        sb.append(" failed to load");
        Logger.error(sb.toString(), (Throwable)ex);
    }
    
    private String pluginId(final Class<? extends Plugin> clazz) {
        final String pluginName = this.pluginName(clazz);
        String simpleName = clazz.getSimpleName();
        if (pluginName == null) {
            return null;
        }
        if (!pluginName.equals((Object)"")) {
            simpleName = pluginName;
        }
        final StringBuilder sb = new StringBuilder("Registering plugin instance: ");
        sb.append(simpleName);
        Logger.debug(sb.toString());
        return simpleName;
    }
    
    private String pluginName(final Class<? extends Plugin> clazz) {
        final CapacitorPlugin capacitorPlugin = clazz.getAnnotation(CapacitorPlugin.class);
        String s;
        if (capacitorPlugin == null) {
            s = this.getLegacyPluginName(clazz);
        }
        else {
            s = capacitorPlugin.name();
        }
        return s;
    }
    
    private void registerAllPlugins() {
        this.registerPlugin((Class<? extends Plugin>)CapacitorCookies.class);
        this.registerPlugin((Class<? extends Plugin>)com.getcapacitor.plugin.WebView.class);
        this.registerPlugin((Class<? extends Plugin>)CapacitorHttp.class);
        final Iterator iterator = this.initialPlugins.iterator();
        while (iterator.hasNext()) {
            this.registerPlugin((Class<? extends Plugin>)iterator.next());
        }
        final Iterator iterator2 = this.pluginInstances.iterator();
        while (iterator2.hasNext()) {
            this.registerPluginInstance((Plugin)iterator2.next());
        }
    }
    
    private void setAllowedOriginRules() {
        final String[] allowNavigation = this.config.getAllowNavigation();
        final String host = this.getHost();
        final String scheme = this.getScheme();
        final Set<String> allowedOriginRules = this.allowedOriginRules;
        final StringBuilder sb = new StringBuilder();
        sb.append(scheme);
        sb.append("://");
        sb.append(host);
        allowedOriginRules.add((Object)sb.toString());
        if (this.getServerUrl() != null) {
            this.allowedOriginRules.add((Object)this.getServerUrl());
        }
        if (allowNavigation != null) {
            for (final String s : allowNavigation) {
                if (!s.startsWith("http")) {
                    final Set<String> allowedOriginRules2 = this.allowedOriginRules;
                    final StringBuilder sb2 = new StringBuilder("https://");
                    sb2.append(s);
                    allowedOriginRules2.add((Object)sb2.toString());
                }
                else {
                    this.allowedOriginRules.add((Object)s);
                }
            }
            this.authorities.addAll((Collection)Arrays.asList((Object[])allowNavigation));
        }
        this.appAllowNavigationMask = HostMask.Parser.parse(allowNavigation);
    }
    
    public void addWebViewListener(final WebViewListener webViewListener) {
        this.webViewListeners.add((Object)webViewListener);
    }
    
    public void callPluginMethod(String tags, final String s, final PluginCall pluginCall) {
        try {
            final PluginHandle plugin = this.getPlugin(tags);
            if (plugin == null) {
                final StringBuilder sb = new StringBuilder("unable to find plugin : ");
                sb.append(tags);
                Logger.error(sb.toString());
                final StringBuilder sb2 = new StringBuilder("unable to find plugin : ");
                sb2.append(tags);
                pluginCall.errorCallback(sb2.toString());
                return;
            }
            if (Logger.shouldLog()) {
                final StringBuilder sb3 = new StringBuilder("callback: ");
                sb3.append(pluginCall.getCallbackId());
                sb3.append(", pluginId: ");
                sb3.append(plugin.getId());
                sb3.append(", methodName: ");
                sb3.append(s);
                sb3.append(", methodData: ");
                sb3.append(pluginCall.getData().toString());
                Logger.verbose(sb3.toString());
            }
            this.taskHandler.post((Runnable)new Bridge$$ExternalSyntheticLambda4(this, plugin, s, pluginCall));
        }
        catch (final Exception ex) {
            tags = Logger.tags("callPluginMethod");
            final StringBuilder sb4 = new StringBuilder("error : ");
            sb4.append((Object)ex);
            Logger.error(tags, sb4.toString(), null);
            pluginCall.errorCallback(ex.toString());
        }
    }
    
    public void eval(final String s, final ValueCallback<String> valueCallback) {
        new Handler(this.context.getMainLooper()).post((Runnable)new Bridge$$ExternalSyntheticLambda5(this, s, valueCallback));
    }
    
    public void execute(final Runnable runnable) {
        this.taskHandler.post(runnable);
    }
    
    public void executeOnMainThread(final Runnable runnable) {
        new Handler(this.context.getMainLooper()).post(runnable);
    }
    
    public AppCompatActivity getActivity() {
        return this.context;
    }
    
    public Set<String> getAllowedOriginRules() {
        return this.allowedOriginRules;
    }
    
    public App getApp() {
        return this.app;
    }
    
    public HostMask getAppAllowNavigationMask() {
        return this.appAllowNavigationMask;
    }
    
    public String getAppUrl() {
        return this.appUrl;
    }
    
    public CapConfig getConfig() {
        return this.config;
    }
    
    public Context getContext() {
        return (Context)this.context;
    }
    
    public String getErrorUrl() {
        final String errorPath = this.config.getErrorPath();
        if (errorPath != null && !errorPath.trim().isEmpty()) {
            final String host = this.getHost();
            final String scheme = this.getScheme();
            final StringBuilder sb = new StringBuilder();
            sb.append(scheme);
            sb.append("://");
            sb.append(host);
            final String string = sb.toString();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(string);
            sb2.append("/");
            sb2.append(errorPath);
            return sb2.toString();
        }
        return null;
    }
    
    public Fragment getFragment() {
        return this.fragment;
    }
    
    public String getHost() {
        return this.config.getHostname();
    }
    
    public Uri getIntentUri() {
        return this.intentUri;
    }
    
    public WebViewLocalServer getLocalServer() {
        return this.localServer;
    }
    
    public String getLocalUrl() {
        return this.localUrl;
    }
    
    protected PluginCall getPermissionCall(String s) {
        final LinkedList list = (LinkedList)this.savedPermissionCallIds.get((Object)s);
        if (list != null) {
            s = (String)list.poll();
        }
        else {
            s = null;
        }
        return this.getSavedCall(s);
    }
    
    protected Map<String, PermissionState> getPermissionStates(final Plugin plugin) {
        final HashMap hashMap = new HashMap();
        for (final Permission permission : plugin.getPluginHandle().getPluginAnnotation().permissions()) {
            if (permission.strings().length != 0 && (permission.strings().length != 1 || !permission.strings()[0].isEmpty())) {
                for (final String s : permission.strings()) {
                    String alias;
                    if (permission.alias().isEmpty()) {
                        alias = s;
                    }
                    else {
                        alias = permission.alias();
                    }
                    PermissionState permissionState;
                    if (ActivityCompat.checkSelfPermission(this.getContext(), s) == 0) {
                        permissionState = PermissionState.GRANTED;
                    }
                    else {
                        final PermissionState prompt = PermissionState.PROMPT;
                        final String string = this.getContext().getSharedPreferences("PluginPermStates", 0).getString(s, (String)null);
                        if (string != null) {
                            permissionState = PermissionState.byState(string);
                        }
                        else {
                            permissionState = prompt;
                        }
                    }
                    final PermissionState permissionState2 = (PermissionState)((Map)hashMap).get((Object)alias);
                    if (permissionState2 == null || permissionState2 == PermissionState.GRANTED) {
                        ((Map)hashMap).put((Object)alias, (Object)permissionState);
                    }
                }
            }
            else {
                final String alias2 = permission.alias();
                if (!alias2.isEmpty() && ((Map)hashMap).get((Object)alias2) == null) {
                    ((Map)hashMap).put((Object)alias2, (Object)PermissionState.GRANTED);
                }
            }
        }
        return (Map<String, PermissionState>)hashMap;
    }
    
    public PluginHandle getPlugin(final String s) {
        return (PluginHandle)this.plugins.get((Object)s);
    }
    
    PluginCall getPluginCallForLastActivity() {
        final PluginCall pluginCallForLastActivity = this.pluginCallForLastActivity;
        this.pluginCallForLastActivity = null;
        return pluginCallForLastActivity;
    }
    
    @Deprecated
    public PluginHandle getPluginWithRequestCode(final int n) {
        for (final PluginHandle pluginHandle : this.plugins.values()) {
            final CapacitorPlugin pluginAnnotation = pluginHandle.getPluginAnnotation();
            int i = 0;
            final int n2 = 0;
            if (pluginAnnotation == null) {
                final NativePlugin legacyPluginAnnotation = pluginHandle.getLegacyPluginAnnotation();
                if (legacyPluginAnnotation == null) {
                    continue;
                }
                if (legacyPluginAnnotation.permissionRequestCode() == n) {
                    return pluginHandle;
                }
                final int[] requestCodes = legacyPluginAnnotation.requestCodes();
                for (int length = requestCodes.length, j = n2; j < length; ++j) {
                    if (requestCodes[j] == n) {
                        return pluginHandle;
                    }
                }
            }
            else {
                for (int[] requestCodes2 = pluginAnnotation.requestCodes(); i < requestCodes2.length; ++i) {
                    if (requestCodes2[i] == n) {
                        return pluginHandle;
                    }
                }
            }
        }
        return null;
    }
    
    RouteProcessor getRouteProcessor() {
        return this.routeProcessor;
    }
    
    public PluginCall getSavedCall(final String s) {
        if (s == null) {
            return null;
        }
        return (PluginCall)this.savedCalls.get((Object)s);
    }
    
    public String getScheme() {
        return this.config.getAndroidScheme();
    }
    
    public String getServerBasePath() {
        return this.localServer.getBasePath();
    }
    
    ServerPath getServerPath() {
        return this.serverPath;
    }
    
    public String getServerUrl() {
        return this.config.getServerUrl();
    }
    
    public WebView getWebView() {
        return this.webView;
    }
    
    public BridgeWebViewClient getWebViewClient() {
        return this.webViewClient;
    }
    
    List<WebViewListener> getWebViewListeners() {
        return this.webViewListeners;
    }
    
    public void handleAppUrlLoadError(final Exception ex) {
        if (ex instanceof SocketTimeoutException) {
            final StringBuilder sb = new StringBuilder("Unable to load app. Ensure the server is running at ");
            sb.append(this.appUrl);
            sb.append(", or modify the appUrl setting in capacitor.config.json (make sure to npx cap copy after to commit changes).");
            Logger.error(sb.toString(), (Throwable)ex);
        }
    }
    
    public boolean isDeployDisabled() {
        return this.preferences.getBoolean("DisableDeploy", false);
    }
    
    public boolean isDevMode() {
        return (this.getActivity().getApplicationInfo().flags & 0x2) != 0x0;
    }
    
    public boolean isMinimumWebViewInstalled() {
        final PackageManager packageManager = this.getContext().getPackageManager();
        final int sdk_INT = Build$VERSION.SDK_INT;
        final boolean b = true;
        final boolean b2 = true;
        boolean b3 = true;
        final boolean b4 = true;
        if (sdk_INT >= 26) {
            final PackageInfo m = Bridge$$ExternalSyntheticApiModelOutline0.m();
            final Matcher matcher = Pattern.compile("(\\d+)").matcher((CharSequence)m.versionName);
            if (!matcher.find()) {
                return false;
            }
            final int int1 = Integer.parseInt(matcher.group(0));
            if (m.packageName.equals((Object)"com.huawei.webview")) {
                return int1 >= this.config.getMinHuaweiWebViewVersion() && b4;
            }
            return int1 >= this.config.getMinWebViewVersion() && b;
        }
        else {
            String s = "com.google.android.webview";
            try {
                if (Build$VERSION.SDK_INT >= 24) {
                    s = "com.android.chrome";
                }
                b3 = (Integer.parseInt(InternalUtils.getPackageInfo(packageManager, s).versionName.split("\\.")[0]) >= this.config.getMinWebViewVersion() && b2);
                return b3;
            }
            catch (final Exception ex) {
                final StringBuilder sb = new StringBuilder("Unable to get package info for 'com.google.android.webview'");
                sb.append(ex.toString());
                Logger.warn(sb.toString());
                try {
                    if (Integer.parseInt(InternalUtils.getPackageInfo(packageManager, "com.android.webview").versionName.split("\\.")[0]) < this.config.getMinWebViewVersion()) {
                        b3 = false;
                    }
                    return b3;
                }
                catch (final Exception ex2) {
                    final StringBuilder sb2 = new StringBuilder("Unable to get package info for 'com.android.webview'");
                    sb2.append(ex2.toString());
                    Logger.warn(sb2.toString());
                    return false;
                }
            }
        }
    }
    
    public boolean launchIntent(final Uri uri) {
        final Iterator iterator = this.plugins.entrySet().iterator();
        while (iterator.hasNext()) {
            final Plugin instance = ((PluginHandle)((Map$Entry)iterator.next()).getValue()).getInstance();
            if (instance != null) {
                final Boolean shouldOverrideLoad = instance.shouldOverrideLoad(uri);
                if (shouldOverrideLoad != null) {
                    return shouldOverrideLoad;
                }
                continue;
            }
        }
        if (uri.getScheme().equals((Object)"data")) {
            return false;
        }
        final Uri parse = Uri.parse(this.appUrl);
        if ((parse.getHost().equals((Object)uri.getHost()) && uri.getScheme().equals((Object)parse.getScheme())) || this.appAllowNavigationMask.matches(uri.getHost())) {
            return false;
        }
        try {
            this.getContext().startActivity(new Intent("android.intent.action.VIEW", uri));
            return true;
        }
        catch (final ActivityNotFoundException ex) {
            return true;
        }
    }
    
    public void logToJs(final String s) {
        this.logToJs(s, "log");
    }
    
    public void logToJs(final String s, final String s2) {
        final StringBuilder sb = new StringBuilder("window.Capacitor.logJs(\"");
        sb.append(s);
        sb.append("\", \"");
        sb.append(s2);
        sb.append("\")");
        this.eval(sb.toString(), null);
    }
    
    boolean onActivityResult(final int n, final int n2, final Intent intent) {
        final PluginHandle pluginWithRequestCode = this.getPluginWithRequestCode(n);
        if (pluginWithRequestCode != null && pluginWithRequestCode.getInstance() != null) {
            if (pluginWithRequestCode.getInstance().getSavedCall() == null && this.pluginCallForLastActivity != null) {
                pluginWithRequestCode.getInstance().saveCall(this.pluginCallForLastActivity);
            }
            pluginWithRequestCode.getInstance().handleOnActivityResult(n, n2, intent);
            this.pluginCallForLastActivity = null;
            return true;
        }
        final StringBuilder sb = new StringBuilder("Unable to find a Capacitor plugin to handle requestCode, trying Cordova plugins ");
        sb.append(n);
        Logger.debug(sb.toString());
        return this.cordovaInterface.onActivityResult(n, n2, intent);
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnConfigurationChanged(configuration);
        }
    }
    
    public void onDestroy() {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnDestroy();
        }
        this.handlerThread.quitSafely();
        final CordovaWebView cordovaWebView = this.cordovaWebView;
        if (cordovaWebView != null) {
            cordovaWebView.handleDestroy();
        }
    }
    
    public void onDetachedFromWindow() {
        this.webView.removeAllViews();
        this.webView.destroy();
    }
    
    public void onNewIntent(final Intent intent) {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnNewIntent(intent);
        }
        final CordovaWebView cordovaWebView = this.cordovaWebView;
        if (cordovaWebView != null) {
            cordovaWebView.onNewIntent(intent);
        }
    }
    
    public void onPause() {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnPause();
        }
        if (this.cordovaWebView != null) {
            this.cordovaWebView.handlePause(this.shouldKeepRunning() || this.cordovaInterface.getActivityResultCallback() != null);
        }
    }
    
    boolean onRequestPermissionsResult(final int n, final String[] array, final int[] array2) {
        final PluginHandle pluginWithRequestCode = this.getPluginWithRequestCode(n);
        final boolean b = false;
        if (pluginWithRequestCode == null) {
            final StringBuilder sb = new StringBuilder("Unable to find a Capacitor plugin to handle permission requestCode, trying Cordova plugins ");
            sb.append(n);
            Logger.debug(sb.toString());
            boolean handlePermissionResult;
            try {
                handlePermissionResult = this.cordovaInterface.handlePermissionResult(n, array, array2);
            }
            catch (final JSONException ex) {
                final StringBuilder sb2 = new StringBuilder("Error on Cordova plugin permissions request ");
                sb2.append(ex.getMessage());
                Logger.debug(sb2.toString());
                handlePermissionResult = b;
            }
            return handlePermissionResult;
        }
        if (pluginWithRequestCode.getPluginAnnotation() == null) {
            pluginWithRequestCode.getInstance().handleRequestPermissionsResult(n, array, array2);
            return true;
        }
        return false;
    }
    
    public void onRestart() {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnRestart();
        }
    }
    
    public void onResume() {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnResume();
        }
        final CordovaWebView cordovaWebView = this.cordovaWebView;
        if (cordovaWebView != null) {
            cordovaWebView.handleResume(this.shouldKeepRunning());
        }
    }
    
    public void onStart() {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnStart();
        }
        final CordovaWebView cordovaWebView = this.cordovaWebView;
        if (cordovaWebView != null) {
            cordovaWebView.handleStart();
        }
    }
    
    public void onStop() {
        final Iterator iterator = this.plugins.values().iterator();
        while (iterator.hasNext()) {
            ((PluginHandle)iterator.next()).getInstance().handleOnStop();
        }
        final CordovaWebView cordovaWebView = this.cordovaWebView;
        if (cordovaWebView != null) {
            cordovaWebView.handleStop();
        }
    }
    
    public <I, O> ActivityResultLauncher<I> registerForActivityResult(final ActivityResultContract<I, O> activityResultContract, final ActivityResultCallback<O> activityResultCallback) {
        final Fragment fragment = this.fragment;
        if (fragment != null) {
            return (ActivityResultLauncher<I>)fragment.registerForActivityResult((ActivityResultContract)activityResultContract, (ActivityResultCallback)activityResultCallback);
        }
        return (ActivityResultLauncher<I>)this.context.registerForActivityResult((ActivityResultContract)activityResultContract, (ActivityResultCallback)activityResultCallback);
    }
    
    public void registerPlugin(final Class<? extends Plugin> clazz) {
        final String pluginId = this.pluginId(clazz);
        if (pluginId == null) {
            return;
        }
        try {
            this.plugins.put((Object)pluginId, (Object)new PluginHandle(this, clazz));
        }
        catch (final PluginLoadException ex) {
            this.logPluginLoadException(clazz, ex);
        }
        catch (final InvalidPluginException ex2) {
            this.logInvalidPluginException(clazz);
        }
    }
    
    public void registerPluginInstance(final Plugin plugin) {
        final Class<? extends Plugin> class1 = plugin.getClass();
        final String pluginId = this.pluginId(class1);
        if (pluginId == null) {
            return;
        }
        try {
            this.plugins.put((Object)pluginId, (Object)new PluginHandle(this, plugin));
        }
        catch (final InvalidPluginException ex) {
            this.logInvalidPluginException(class1);
        }
    }
    
    public void registerPluginInstances(final Plugin[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            this.registerPluginInstance(array[i]);
        }
    }
    
    public void registerPlugins(final Class<? extends Plugin>[] array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            this.registerPlugin(array[i]);
        }
    }
    
    public void releaseCall(final PluginCall pluginCall) {
        this.releaseCall(pluginCall.getCallbackId());
    }
    
    public void releaseCall(final String s) {
        this.savedCalls.remove((Object)s);
    }
    
    public void reload() {
        this.webView.post((Runnable)new Bridge$$ExternalSyntheticLambda2(this));
    }
    
    public void removeWebViewListener(final WebViewListener webViewListener) {
        this.webViewListeners.remove((Object)webViewListener);
    }
    
    public void reset() {
        this.savedCalls = (Map<String, PluginCall>)new HashMap();
    }
    
    public void restoreInstanceState(Bundle bundle) {
        final String string = bundle.getString("capacitorLastActivityPluginId");
        final String string2 = bundle.getString("capacitorLastActivityPluginMethod");
        final String string3 = bundle.getString("capacitorLastPluginCallOptions");
        if (string != null) {
            if (string3 != null) {
                try {
                    this.pluginCallForLastActivity = new PluginCall(this.msgHandler, string, "-1", string2, new JSObject(string3));
                }
                catch (final JSONException ex) {
                    Logger.error("Unable to restore plugin call, unable to parse persisted JSON object", (Throwable)ex);
                }
            }
            bundle = bundle.getBundle("capacitorLastPluginCallBundle");
            final PluginHandle plugin = this.getPlugin(string);
            if (bundle != null && plugin != null) {
                plugin.getInstance().restoreState(bundle);
            }
            else {
                Logger.error("Unable to restore last plugin call");
            }
        }
    }
    
    public void saveCall(final PluginCall pluginCall) {
        this.savedCalls.put((Object)pluginCall.getCallbackId(), (Object)pluginCall);
    }
    
    public void saveInstanceState(final Bundle bundle) {
        Logger.debug("Saving instance state!");
        final PluginCall pluginCallForLastActivity = this.pluginCallForLastActivity;
        if (pluginCallForLastActivity != null) {
            final PluginHandle plugin = this.getPlugin(pluginCallForLastActivity.getPluginId());
            if (plugin != null) {
                final Bundle saveInstanceState = plugin.getInstance().saveInstanceState();
                if (saveInstanceState != null) {
                    bundle.putString("capacitorLastActivityPluginId", pluginCallForLastActivity.getPluginId());
                    bundle.putString("capacitorLastActivityPluginMethod", pluginCallForLastActivity.getMethodName());
                    bundle.putString("capacitorLastPluginCallOptions", pluginCallForLastActivity.getData().toString());
                    bundle.putBundle("capacitorLastPluginCallBundle", saveInstanceState);
                }
                else {
                    final StringBuilder sb = new StringBuilder("Couldn't save last ");
                    sb.append(pluginCallForLastActivity.getPluginId());
                    sb.append("'s Plugin ");
                    sb.append(pluginCallForLastActivity.getMethodName());
                    sb.append(" call");
                    Logger.error(sb.toString());
                }
            }
        }
    }
    
    protected void savePermissionCall(final PluginCall pluginCall) {
        if (pluginCall != null) {
            if (!this.savedPermissionCallIds.containsKey((Object)pluginCall.getPluginId())) {
                this.savedPermissionCallIds.put((Object)pluginCall.getPluginId(), (Object)new LinkedList());
            }
            ((LinkedList)this.savedPermissionCallIds.get((Object)pluginCall.getPluginId())).add((Object)pluginCall.getCallbackId());
            this.saveCall(pluginCall);
        }
    }
    
    protected void setCordovaWebView(final CordovaWebView cordovaWebView) {
        this.cordovaWebView = cordovaWebView;
    }
    
    void setPluginCallForLastActivity(final PluginCall pluginCallForLastActivity) {
        this.pluginCallForLastActivity = pluginCallForLastActivity;
    }
    
    void setRouteProcessor(final RouteProcessor routeProcessor) {
        this.routeProcessor = routeProcessor;
    }
    
    public void setServerAssetPath(final String s) {
        this.localServer.hostAssets(s);
        this.webView.post((Runnable)new Bridge$$ExternalSyntheticLambda7(this));
    }
    
    public void setServerBasePath(final String s) {
        this.localServer.hostFiles(s);
        this.webView.post((Runnable)new Bridge$$ExternalSyntheticLambda3(this));
    }
    
    public void setWebViewClient(final BridgeWebViewClient bridgeWebViewClient) {
        this.webViewClient = bridgeWebViewClient;
        this.webView.setWebViewClient((WebViewClient)bridgeWebViewClient);
    }
    
    void setWebViewListeners(final List<WebViewListener> webViewListeners) {
        this.webViewListeners = webViewListeners;
    }
    
    public boolean shouldKeepRunning() {
        return this.preferences.getBoolean("KeepRunning", true);
    }
    
    @Deprecated
    public void startActivityForPluginWithResult(final PluginCall pluginCallForLastActivity, final Intent intent, final int n) {
        Logger.debug("Starting activity for result");
        this.pluginCallForLastActivity = pluginCallForLastActivity;
        this.getActivity().startActivityForResult(intent, n);
    }
    
    public void triggerDocumentJSEvent(final String s) {
        this.triggerJSEvent(s, "document");
    }
    
    public void triggerDocumentJSEvent(final String s, final String s2) {
        this.triggerJSEvent(s, "document", s2);
    }
    
    public void triggerJSEvent(final String s, final String s2) {
        final StringBuilder sb = new StringBuilder("window.Capacitor.triggerEvent(\"");
        sb.append(s);
        sb.append("\", \"");
        sb.append(s2);
        sb.append("\")");
        this.eval(sb.toString(), (ValueCallback<String>)new Bridge$$ExternalSyntheticLambda6());
    }
    
    public void triggerJSEvent(final String s, final String s2, final String s3) {
        final StringBuilder sb = new StringBuilder("window.Capacitor.triggerEvent(\"");
        sb.append(s);
        sb.append("\", \"");
        sb.append(s2);
        sb.append("\", ");
        sb.append(s3);
        sb.append(")");
        this.eval(sb.toString(), (ValueCallback<String>)new Bridge$$ExternalSyntheticLambda1());
    }
    
    public void triggerWindowJSEvent(final String s) {
        this.triggerJSEvent(s, "window");
    }
    
    public void triggerWindowJSEvent(final String s, final String s2) {
        this.triggerJSEvent(s, "window", s2);
    }
    
    protected boolean validatePermissions(final Plugin plugin, final PluginCall pluginCall, final Map<String, Boolean> map) {
        final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("PluginPermStates", 0);
        for (final Map$Entry map$Entry : map.entrySet()) {
            final String s = (String)map$Entry.getKey();
            if (map$Entry.getValue()) {
                if (sharedPreferences.getString(s, (String)null) == null) {
                    continue;
                }
                final SharedPreferences$Editor edit = sharedPreferences.edit();
                edit.remove(s);
                edit.apply();
            }
            else {
                final SharedPreferences$Editor edit2 = sharedPreferences.edit();
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)this.getActivity(), s)) {
                    edit2.putString(s, PermissionState.PROMPT_WITH_RATIONALE.toString());
                }
                else {
                    edit2.putString(s, PermissionState.DENIED.toString());
                }
                edit2.apply();
            }
        }
        final String[] array = (String[])map.keySet().toArray((Object[])new String[0]);
        if (!PermissionHelper.hasDefinedPermissions(this.getContext(), array)) {
            final StringBuilder sb = new StringBuilder("Missing the following permissions in AndroidManifest.xml:\n");
            for (final String s2 : PermissionHelper.getUndefinedPermissions(this.getContext(), array)) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(s2);
                sb2.append("\n");
                sb.append(sb2.toString());
            }
            pluginCall.reject(sb.toString());
            return false;
        }
        return true;
    }
    
    public static class Builder
    {
        private AppCompatActivity activity;
        private CapConfig config;
        private Fragment fragment;
        private Bundle instanceState;
        private List<Plugin> pluginInstances;
        private List<Class<? extends Plugin>> plugins;
        private RouteProcessor routeProcessor;
        private ServerPath serverPath;
        private final List<WebViewListener> webViewListeners;
        
        public Builder(final AppCompatActivity activity) {
            this.instanceState = null;
            this.config = null;
            this.plugins = (List<Class<? extends Plugin>>)new ArrayList();
            this.pluginInstances = (List<Plugin>)new ArrayList();
            this.webViewListeners = (List<WebViewListener>)new ArrayList();
            this.activity = activity;
        }
        
        public Builder(final Fragment fragment) {
            this.instanceState = null;
            this.config = null;
            this.plugins = (List<Class<? extends Plugin>>)new ArrayList();
            this.pluginInstances = (List<Plugin>)new ArrayList();
            this.webViewListeners = (List<WebViewListener>)new ArrayList();
            this.activity = (AppCompatActivity)fragment.getActivity();
            this.fragment = fragment;
        }
        
        public Builder addPlugin(final Class<? extends Plugin> clazz) {
            this.plugins.add((Object)clazz);
            return this;
        }
        
        public Builder addPluginInstance(final Plugin plugin) {
            this.pluginInstances.add((Object)plugin);
            return this;
        }
        
        public Builder addPluginInstances(final List<Plugin> list) {
            this.pluginInstances.addAll((Collection)list);
            return this;
        }
        
        public Builder addPlugins(final List<Class<? extends Plugin>> list) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.addPlugin((Class<? extends Plugin>)iterator.next());
            }
            return this;
        }
        
        public Builder addWebViewListener(final WebViewListener webViewListener) {
            this.webViewListeners.add((Object)webViewListener);
            return this;
        }
        
        public Builder addWebViewListeners(final List<WebViewListener> list) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                this.addWebViewListener((WebViewListener)iterator.next());
            }
            return this;
        }
        
        public Bridge create() {
            final ConfigXmlParser configXmlParser = new ConfigXmlParser();
            configXmlParser.parse(this.activity.getApplicationContext());
            final CordovaPreferences preferences = configXmlParser.getPreferences();
            preferences.setPreferencesBundle(this.activity.getIntent().getExtras());
            final ArrayList pluginEntries = configXmlParser.getPluginEntries();
            final MockCordovaInterfaceImpl mockCordovaInterfaceImpl = new MockCordovaInterfaceImpl(this.activity);
            final Bundle instanceState = this.instanceState;
            if (instanceState != null) {
                mockCordovaInterfaceImpl.restoreInstanceState(instanceState);
            }
            final Fragment fragment = this.fragment;
            View view;
            if (fragment != null) {
                view = fragment.getView().findViewById(R.id.webview);
            }
            else {
                view = this.activity.findViewById(R.id.webview);
            }
            final WebView webView = (WebView)view;
            final MockCordovaWebViewImpl cordovaWebView = new MockCordovaWebViewImpl(this.activity.getApplicationContext());
            cordovaWebView.init((CordovaInterface)mockCordovaInterfaceImpl, (List)pluginEntries, preferences, webView);
            final PluginManager pluginManager = cordovaWebView.getPluginManager();
            mockCordovaInterfaceImpl.onCordovaInit(pluginManager);
            final Bridge bridge = new Bridge(this.activity, this.serverPath, this.fragment, webView, this.plugins, this.pluginInstances, mockCordovaInterfaceImpl, pluginManager, preferences, this.config, null);
            if (webView instanceof CapacitorWebView) {
                ((CapacitorWebView)webView).setBridge(bridge);
            }
            bridge.setCordovaWebView((CordovaWebView)cordovaWebView);
            bridge.setWebViewListeners(this.webViewListeners);
            bridge.setRouteProcessor(this.routeProcessor);
            final Bundle instanceState2 = this.instanceState;
            if (instanceState2 != null) {
                bridge.restoreInstanceState(instanceState2);
            }
            return bridge;
        }
        
        public Builder setConfig(final CapConfig config) {
            this.config = config;
            return this;
        }
        
        public Builder setInstanceState(final Bundle instanceState) {
            this.instanceState = instanceState;
            return this;
        }
        
        public Builder setPlugins(final List<Class<? extends Plugin>> plugins) {
            this.plugins = plugins;
            return this;
        }
        
        public Builder setRouteProcessor(final RouteProcessor routeProcessor) {
            this.routeProcessor = routeProcessor;
            return this;
        }
        
        public Builder setServerPath(final ServerPath serverPath) {
            this.serverPath = serverPath;
            return this;
        }
    }
}
