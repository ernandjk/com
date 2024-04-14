package com.getcapacitor.plugin;

import android.content.SharedPreferences$Editor;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin
public class WebView extends Plugin
{
    public static final String CAP_SERVER_PATH = "serverBasePath";
    public static final String WEBVIEW_PREFS_NAME = "CapWebViewSettings";
    
    @PluginMethod
    public void getServerBasePath(final PluginCall pluginCall) {
        final String serverBasePath = this.bridge.getServerBasePath();
        final JSObject jsObject = new JSObject();
        jsObject.put("path", serverBasePath);
        pluginCall.resolve(jsObject);
    }
    
    @PluginMethod
    public void persistServerBasePath(final PluginCall pluginCall) {
        final String serverBasePath = this.bridge.getServerBasePath();
        final SharedPreferences$Editor edit = this.getContext().getSharedPreferences("CapWebViewSettings", 0).edit();
        edit.putString("serverBasePath", serverBasePath);
        edit.apply();
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void setServerBasePath(final PluginCall pluginCall) {
        this.bridge.setServerBasePath(pluginCall.getString("path"));
        pluginCall.resolve();
    }
}
