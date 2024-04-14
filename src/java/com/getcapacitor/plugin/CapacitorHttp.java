package com.getcapacitor.plugin;

import android.webkit.JavascriptInterface;
import java.util.Iterator;
import com.getcapacitor.plugin.util.CapacitorHttpUrlConnection;
import java.util.Map$Entry;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.plugin.util.HttpRequestHandler;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import com.getcapacitor.PluginCall;
import java.util.Map;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(permissions = { @Permission(alias = "HttpWrite", strings = { "android.permission.WRITE_EXTERNAL_STORAGE" }), @Permission(alias = "HttpRead", strings = { "android.permission.READ_EXTERNAL_STORAGE" }) })
public class CapacitorHttp extends Plugin
{
    private Map<Runnable, PluginCall> activeRequests;
    private final ExecutorService executor;
    
    public CapacitorHttp() {
        this.activeRequests = (Map<Runnable, PluginCall>)new HashMap();
        this.executor = Executors.newCachedThreadPool();
    }
    
    private void http(final PluginCall pluginCall, final String s) {
        final CapacitorHttp$$ExternalSyntheticLambda0 capacitorHttp$$ExternalSyntheticLambda0 = new CapacitorHttp$$ExternalSyntheticLambda0(this, pluginCall, s);
        this.activeRequests.put((Object)capacitorHttp$$ExternalSyntheticLambda0, (Object)pluginCall);
        this.executor.submit((Runnable)capacitorHttp$$ExternalSyntheticLambda0);
    }
    
    @PluginMethod
    public void delete(final PluginCall pluginCall) {
        this.http(pluginCall, "DELETE");
    }
    
    @PluginMethod
    public void get(final PluginCall pluginCall) {
        this.http(pluginCall, "GET");
    }
    
    protected void handleOnDestroy() {
        super.handleOnDestroy();
        final Iterator iterator = this.activeRequests.entrySet().iterator();
    Label_0095_Outer:
        while (true) {
            Label_0106: {
                if (!iterator.hasNext()) {
                    break Label_0106;
                }
                final Map$Entry map$Entry = (Map$Entry)iterator.next();
                final Runnable runnable = (Runnable)map$Entry.getKey();
                final PluginCall pluginCall = (PluginCall)map$Entry.getValue();
                while (true) {
                    if (!pluginCall.getData().has("activeCapacitorHttpUrlConnection")) {
                        break Label_0095;
                    }
                    try {
                        ((CapacitorHttpUrlConnection)pluginCall.getData().get("activeCapacitorHttpUrlConnection")).disconnect();
                        pluginCall.getData().remove("activeCapacitorHttpUrlConnection");
                        this.getBridge().releaseCall(pluginCall);
                        continue Label_0095_Outer;
                        this.activeRequests.clear();
                        this.executor.shutdownNow();
                    }
                    catch (final Exception ex) {
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    @JavascriptInterface
    public boolean isEnabled() {
        return this.getBridge().getConfig().getPluginConfiguration("CapacitorHttp").getBoolean("enabled", false);
    }
    
    public void load() {
        this.bridge.getWebView().addJavascriptInterface((Object)this, "CapacitorHttpAndroidInterface");
        super.load();
    }
    
    @PluginMethod
    public void patch(final PluginCall pluginCall) {
        this.http(pluginCall, "PATCH");
    }
    
    @PluginMethod
    public void post(final PluginCall pluginCall) {
        this.http(pluginCall, "POST");
    }
    
    @PluginMethod
    public void put(final PluginCall pluginCall) {
        this.http(pluginCall, "PUT");
    }
    
    @PluginMethod
    public void request(final PluginCall pluginCall) {
        this.http(pluginCall, null);
    }
}
