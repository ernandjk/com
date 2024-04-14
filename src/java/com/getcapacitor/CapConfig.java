package com.getcapacitor;

import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Locale;
import com.getcapacitor.util.JSONUtils;
import android.content.Context;
import android.content.res.AssetManager;
import java.util.Map;
import org.json.JSONObject;

public class CapConfig
{
    private static final String LOG_BEHAVIOR_DEBUG = "debug";
    private static final String LOG_BEHAVIOR_NONE = "none";
    private static final String LOG_BEHAVIOR_PRODUCTION = "production";
    private boolean allowMixedContent;
    private String[] allowNavigation;
    private String androidScheme;
    private String appendedUserAgentString;
    private String backgroundColor;
    private boolean captureInput;
    private JSONObject configJSON;
    private String errorPath;
    private String hostname;
    private boolean html5mode;
    private boolean initialFocus;
    private boolean loggingEnabled;
    private int minHuaweiWebViewVersion;
    private int minWebViewVersion;
    private String overriddenUserAgentString;
    private Map<String, PluginConfig> pluginsConfiguration;
    private String serverUrl;
    private String startPath;
    private boolean useLegacyBridge;
    private boolean webContentsDebuggingEnabled;
    
    private CapConfig() {
        this.html5mode = true;
        this.hostname = "localhost";
        this.androidScheme = "http";
        this.allowMixedContent = false;
        this.captureInput = false;
        this.webContentsDebuggingEnabled = false;
        this.loggingEnabled = true;
        this.initialFocus = true;
        this.useLegacyBridge = false;
        this.minWebViewVersion = 60;
        this.minHuaweiWebViewVersion = 10;
        this.pluginsConfiguration = null;
        this.configJSON = new JSONObject();
    }
    
    @Deprecated
    public CapConfig(final AssetManager assetManager, final JSONObject configJSON) {
        this.html5mode = true;
        this.hostname = "localhost";
        this.androidScheme = "http";
        this.allowMixedContent = false;
        this.captureInput = false;
        this.webContentsDebuggingEnabled = false;
        this.loggingEnabled = true;
        this.initialFocus = true;
        this.useLegacyBridge = false;
        this.minWebViewVersion = 60;
        this.minHuaweiWebViewVersion = 10;
        this.pluginsConfiguration = null;
        this.configJSON = new JSONObject();
        if (configJSON != null) {
            this.configJSON = configJSON;
        }
        else {
            this.loadConfigFromAssets(assetManager, null);
        }
        this.deserializeConfig(null);
    }
    
    private CapConfig(final Builder builder) {
        this.html5mode = true;
        this.hostname = "localhost";
        this.androidScheme = "http";
        this.allowMixedContent = false;
        this.captureInput = false;
        this.webContentsDebuggingEnabled = false;
        this.loggingEnabled = true;
        this.initialFocus = true;
        this.useLegacyBridge = false;
        this.minWebViewVersion = 60;
        this.minHuaweiWebViewVersion = 10;
        this.pluginsConfiguration = null;
        this.configJSON = new JSONObject();
        this.html5mode = builder.html5mode;
        this.serverUrl = builder.serverUrl;
        this.hostname = builder.hostname;
        if (this.validateScheme(builder.androidScheme)) {
            this.androidScheme = builder.androidScheme;
        }
        this.allowNavigation = builder.allowNavigation;
        this.overriddenUserAgentString = builder.overriddenUserAgentString;
        this.appendedUserAgentString = builder.appendedUserAgentString;
        this.backgroundColor = builder.backgroundColor;
        this.allowMixedContent = builder.allowMixedContent;
        this.captureInput = builder.captureInput;
        this.webContentsDebuggingEnabled = builder.webContentsDebuggingEnabled;
        this.loggingEnabled = builder.loggingEnabled;
        this.initialFocus = builder.initialFocus;
        this.useLegacyBridge = builder.useLegacyBridge;
        this.minWebViewVersion = builder.minWebViewVersion;
        this.minHuaweiWebViewVersion = builder.minHuaweiWebViewVersion;
        this.errorPath = builder.errorPath;
        this.startPath = builder.startPath;
        this.pluginsConfiguration = builder.pluginsConfiguration;
    }
    
    private void deserializeConfig(final Context context) {
        final boolean loggingEnabled = context != null && (context.getApplicationInfo().flags & 0x2) != 0x0;
        this.html5mode = JSONUtils.getBoolean(this.configJSON, "server.html5mode", this.html5mode);
        this.serverUrl = JSONUtils.getString(this.configJSON, "server.url", null);
        this.hostname = JSONUtils.getString(this.configJSON, "server.hostname", this.hostname);
        this.errorPath = JSONUtils.getString(this.configJSON, "server.errorPath", null);
        final String string = JSONUtils.getString(this.configJSON, "server.androidScheme", this.androidScheme);
        if (this.validateScheme(string)) {
            this.androidScheme = string;
        }
        this.allowNavigation = JSONUtils.getArray(this.configJSON, "server.allowNavigation", null);
        final JSONObject configJSON = this.configJSON;
        this.overriddenUserAgentString = JSONUtils.getString(configJSON, "android.overrideUserAgent", JSONUtils.getString(configJSON, "overrideUserAgent", null));
        final JSONObject configJSON2 = this.configJSON;
        this.appendedUserAgentString = JSONUtils.getString(configJSON2, "android.appendUserAgent", JSONUtils.getString(configJSON2, "appendUserAgent", null));
        final JSONObject configJSON3 = this.configJSON;
        this.backgroundColor = JSONUtils.getString(configJSON3, "android.backgroundColor", JSONUtils.getString(configJSON3, "backgroundColor", null));
        final JSONObject configJSON4 = this.configJSON;
        this.allowMixedContent = JSONUtils.getBoolean(configJSON4, "android.allowMixedContent", JSONUtils.getBoolean(configJSON4, "allowMixedContent", this.allowMixedContent));
        this.minWebViewVersion = JSONUtils.getInt(this.configJSON, "android.minWebViewVersion", 60);
        this.minHuaweiWebViewVersion = JSONUtils.getInt(this.configJSON, "android.minHuaweiWebViewVersion", 10);
        this.captureInput = JSONUtils.getBoolean(this.configJSON, "android.captureInput", this.captureInput);
        this.useLegacyBridge = JSONUtils.getBoolean(this.configJSON, "android.useLegacyBridge", this.useLegacyBridge);
        this.webContentsDebuggingEnabled = JSONUtils.getBoolean(this.configJSON, "android.webContentsDebuggingEnabled", loggingEnabled);
        final JSONObject configJSON5 = this.configJSON;
        final String lowerCase = JSONUtils.getString(configJSON5, "android.loggingBehavior", JSONUtils.getString(configJSON5, "loggingBehavior", "debug")).toLowerCase(Locale.ROOT);
        lowerCase.hashCode();
        if (!lowerCase.equals((Object)"none")) {
            if (!lowerCase.equals((Object)"production")) {
                this.loggingEnabled = loggingEnabled;
            }
            else {
                this.loggingEnabled = true;
            }
        }
        else {
            this.loggingEnabled = false;
        }
        this.initialFocus = JSONUtils.getBoolean(this.configJSON, "android.initialFocus", this.initialFocus);
        this.pluginsConfiguration = deserializePluginsConfig(JSONUtils.getObject(this.configJSON, "plugins"));
    }
    
    private static Map<String, PluginConfig> deserializePluginsConfig(final JSONObject jsonObject) {
        final HashMap hashMap = new HashMap();
        if (jsonObject == null) {
            return (Map<String, PluginConfig>)hashMap;
        }
        final Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String s = (String)keys.next();
            try {
                ((Map)hashMap).put((Object)s, (Object)new PluginConfig(jsonObject.getJSONObject(s)));
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
        }
        return (Map<String, PluginConfig>)hashMap;
    }
    
    private void loadConfigFromAssets(final AssetManager assetManager, String fileFromAssets) {
        String string;
        if (fileFromAssets == null) {
            string = "";
        }
        else {
            string = fileFromAssets;
            if (fileFromAssets.charAt(fileFromAssets.length() - 1) != '/') {
                final StringBuilder sb = new StringBuilder();
                sb.append(fileFromAssets);
                sb.append("/");
                string = sb.toString();
            }
        }
        try {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(string);
            sb2.append("capacitor.config.json");
            fileFromAssets = FileUtils.readFileFromAssets(assetManager, sb2.toString());
            this.configJSON = new JSONObject(fileFromAssets);
        }
        catch (final JSONException ex) {
            Logger.error("Unable to parse capacitor.config.json. Make sure it's valid json", (Throwable)ex);
        }
        catch (final IOException ex2) {
            Logger.error("Unable to load capacitor.config.json. Run npx cap copy first", (Throwable)ex2);
        }
    }
    
    private void loadConfigFromFile(final String s) {
        String string;
        if (s == null) {
            string = "";
        }
        else {
            string = s;
            if (s.charAt(s.length() - 1) != '/') {
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append("/");
                string = sb.toString();
            }
        }
        try {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(string);
            sb2.append("capacitor.config.json");
            this.configJSON = new JSONObject(FileUtils.readFileFromDisk(new File(sb2.toString())));
        }
        catch (final IOException ex) {
            Logger.error("Unable to load capacitor.config.json.", (Throwable)ex);
        }
        catch (final JSONException ex2) {
            Logger.error("Unable to parse capacitor.config.json. Make sure it's valid json", (Throwable)ex2);
        }
    }
    
    public static CapConfig loadDefault(final Context context) {
        final CapConfig capConfig = new CapConfig();
        if (context == null) {
            Logger.error("Capacitor Config could not be created from file. Context must not be null.");
            return capConfig;
        }
        capConfig.loadConfigFromAssets(context.getAssets(), null);
        capConfig.deserializeConfig(context);
        return capConfig;
    }
    
    public static CapConfig loadFromAssets(final Context context, final String s) {
        final CapConfig capConfig = new CapConfig();
        if (context == null) {
            Logger.error("Capacitor Config could not be created from file. Context must not be null.");
            return capConfig;
        }
        capConfig.loadConfigFromAssets(context.getAssets(), s);
        capConfig.deserializeConfig(context);
        return capConfig;
    }
    
    public static CapConfig loadFromFile(final Context context, final String s) {
        final CapConfig capConfig = new CapConfig();
        if (context == null) {
            Logger.error("Capacitor Config could not be created from file. Context must not be null.");
            return capConfig;
        }
        capConfig.loadConfigFromFile(s);
        capConfig.deserializeConfig(context);
        return capConfig;
    }
    
    private boolean validateScheme(final String s) {
        if (Arrays.asList((Object[])new String[] { "file", "ftp", "ftps", "ws", "wss", "about", "blob", "data" }).contains((Object)s)) {
            final StringBuilder sb = new StringBuilder();
            sb.append(s);
            sb.append(" is not an allowed scheme.  Defaulting to http.");
            Logger.warn(sb.toString());
            return false;
        }
        return true;
    }
    
    public String[] getAllowNavigation() {
        return this.allowNavigation;
    }
    
    public String getAndroidScheme() {
        return this.androidScheme;
    }
    
    public String getAppendedUserAgentString() {
        return this.appendedUserAgentString;
    }
    
    @Deprecated
    public String[] getArray(final String s) {
        return JSONUtils.getArray(this.configJSON, s, null);
    }
    
    @Deprecated
    public String[] getArray(final String s, final String[] array) {
        return JSONUtils.getArray(this.configJSON, s, array);
    }
    
    public String getBackgroundColor() {
        return this.backgroundColor;
    }
    
    @Deprecated
    public boolean getBoolean(final String s, final boolean b) {
        return JSONUtils.getBoolean(this.configJSON, s, b);
    }
    
    public String getErrorPath() {
        return this.errorPath;
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    @Deprecated
    public int getInt(final String s, final int n) {
        return JSONUtils.getInt(this.configJSON, s, n);
    }
    
    public int getMinHuaweiWebViewVersion() {
        final int minHuaweiWebViewVersion = this.minHuaweiWebViewVersion;
        if (minHuaweiWebViewVersion < 10) {
            Logger.warn("Specified minimum Huawei webview version is too low, defaulting to 10");
            return 10;
        }
        return minHuaweiWebViewVersion;
    }
    
    public int getMinWebViewVersion() {
        final int minWebViewVersion = this.minWebViewVersion;
        if (minWebViewVersion < 55) {
            Logger.warn("Specified minimum webview version is too low, defaulting to 55");
            return 55;
        }
        return minWebViewVersion;
    }
    
    @Deprecated
    public JSONObject getObject(final String s) {
        try {
            return this.configJSON.getJSONObject(s);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public String getOverriddenUserAgentString() {
        return this.overriddenUserAgentString;
    }
    
    public PluginConfig getPluginConfiguration(final String s) {
        PluginConfig pluginConfig;
        if ((pluginConfig = (PluginConfig)this.pluginsConfiguration.get((Object)s)) == null) {
            pluginConfig = new PluginConfig(new JSONObject());
        }
        return pluginConfig;
    }
    
    public String getServerUrl() {
        return this.serverUrl;
    }
    
    public String getStartPath() {
        return this.startPath;
    }
    
    @Deprecated
    public String getString(final String s) {
        return JSONUtils.getString(this.configJSON, s, null);
    }
    
    @Deprecated
    public String getString(final String s, final String s2) {
        return JSONUtils.getString(this.configJSON, s, s2);
    }
    
    public boolean isHTML5Mode() {
        return this.html5mode;
    }
    
    public boolean isInitialFocus() {
        return this.initialFocus;
    }
    
    public boolean isInputCaptured() {
        return this.captureInput;
    }
    
    public boolean isLoggingEnabled() {
        return this.loggingEnabled;
    }
    
    public boolean isMixedContentAllowed() {
        return this.allowMixedContent;
    }
    
    public boolean isUsingLegacyBridge() {
        return this.useLegacyBridge;
    }
    
    public boolean isWebContentsDebuggingEnabled() {
        return this.webContentsDebuggingEnabled;
    }
    
    public static class Builder
    {
        private boolean allowMixedContent;
        private String[] allowNavigation;
        private String androidScheme;
        private String appendedUserAgentString;
        private String backgroundColor;
        private boolean captureInput;
        private Context context;
        private String errorPath;
        private String hostname;
        private boolean html5mode;
        private boolean initialFocus;
        private boolean loggingEnabled;
        private int minHuaweiWebViewVersion;
        private int minWebViewVersion;
        private String overriddenUserAgentString;
        private Map<String, PluginConfig> pluginsConfiguration;
        private String serverUrl;
        private String startPath;
        private boolean useLegacyBridge;
        private Boolean webContentsDebuggingEnabled;
        
        public Builder(final Context context) {
            this.html5mode = true;
            this.hostname = "localhost";
            this.androidScheme = "http";
            this.allowMixedContent = false;
            this.captureInput = false;
            this.webContentsDebuggingEnabled = null;
            this.loggingEnabled = true;
            this.initialFocus = false;
            this.useLegacyBridge = false;
            this.minWebViewVersion = 60;
            this.minHuaweiWebViewVersion = 10;
            this.startPath = null;
            this.pluginsConfiguration = (Map<String, PluginConfig>)new HashMap();
            this.context = context;
        }
        
        public CapConfig create() {
            if (this.webContentsDebuggingEnabled == null) {
                this.webContentsDebuggingEnabled = ((this.context.getApplicationInfo().flags & 0x2) != 0x0);
            }
            return new CapConfig(this, null);
        }
        
        public Builder setAllowMixedContent(final boolean allowMixedContent) {
            this.allowMixedContent = allowMixedContent;
            return this;
        }
        
        public Builder setAllowNavigation(final String[] allowNavigation) {
            this.allowNavigation = allowNavigation;
            return this;
        }
        
        public Builder setAndroidScheme(final String androidScheme) {
            this.androidScheme = androidScheme;
            return this;
        }
        
        public Builder setAppendedUserAgentString(final String appendedUserAgentString) {
            this.appendedUserAgentString = appendedUserAgentString;
            return this;
        }
        
        public Builder setBackgroundColor(final String backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }
        
        public Builder setCaptureInput(final boolean captureInput) {
            this.captureInput = captureInput;
            return this;
        }
        
        public Builder setErrorPath(final String errorPath) {
            this.errorPath = errorPath;
            return this;
        }
        
        public Builder setHTML5mode(final boolean html5mode) {
            this.html5mode = html5mode;
            return this;
        }
        
        public Builder setHostname(final String hostname) {
            this.hostname = hostname;
            return this;
        }
        
        public Builder setInitialFocus(final boolean initialFocus) {
            this.initialFocus = initialFocus;
            return this;
        }
        
        public Builder setLoggingEnabled(final boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
            return this;
        }
        
        public Builder setOverriddenUserAgentString(final String overriddenUserAgentString) {
            this.overriddenUserAgentString = overriddenUserAgentString;
            return this;
        }
        
        public Builder setPluginsConfiguration(final JSONObject jsonObject) {
            this.pluginsConfiguration = deserializePluginsConfig(jsonObject);
            return this;
        }
        
        public Builder setServerUrl(final String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }
        
        public Builder setStartPath(final String startPath) {
            this.startPath = startPath;
            return this;
        }
        
        public Builder setUseLegacyBridge(final boolean useLegacyBridge) {
            this.useLegacyBridge = useLegacyBridge;
            return this;
        }
        
        public Builder setWebContentsDebuggingEnabled(final boolean b) {
            this.webContentsDebuggingEnabled = b;
            return this;
        }
    }
}
