package com.capacitorjs.plugins.browser;

import android.content.ActivityNotFoundException;
import com.getcapacitor.util.WebColor;
import android.net.Uri;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(name = "Browser")
public class BrowserPlugin extends Plugin
{
    private Browser implementation;
    
    @PluginMethod
    public void close(final PluginCall pluginCall) {
        pluginCall.unimplemented();
    }
    
    protected void handleOnPause() {
        this.implementation.unbindService();
    }
    
    protected void handleOnResume() {
        if (!this.implementation.bindService()) {
            Logger.error(this.getLogTag(), "Error binding to custom tabs service", (Throwable)null);
        }
    }
    
    public void load() {
        (this.implementation = new Browser(this.getContext())).setBrowserEventListener((Browser$BrowserEventListener)new BrowserPlugin$$ExternalSyntheticLambda0(this));
    }
    
    void onBrowserEvent(final int n) {
        if (n != 1) {
            if (n == 2) {
                this.notifyListeners("browserFinished", (JSObject)null);
            }
        }
        else {
            this.notifyListeners("browserPageLoaded", (JSObject)null);
        }
    }
    
    @PluginMethod
    public void open(final PluginCall pluginCall) {
        final String string = pluginCall.getString("url");
        if (string == null) {
            pluginCall.reject("Must provide a URL to open");
            return;
        }
        if (string.isEmpty()) {
            pluginCall.reject("URL must not be empty");
            return;
        }
        try {
            final Uri parse = Uri.parse(string);
            final String string2 = pluginCall.getString("toolbarColor");
            Label_0072: {
                if (string2 != null) {
                    try {
                        final Integer value = WebColor.parseColor(string2);
                        break Label_0072;
                    }
                    catch (final IllegalArgumentException ex) {
                        Logger.error(this.getLogTag(), "Invalid color provided for toolbarColor. Using default", (Throwable)null);
                    }
                }
                final Integer value = null;
                try {
                    this.implementation.open(parse, value);
                    pluginCall.resolve();
                }
                catch (final ActivityNotFoundException ex2) {
                    Logger.error(this.getLogTag(), ex2.getLocalizedMessage(), (Throwable)null);
                    pluginCall.reject("Unable to display URL");
                }
            }
        }
        catch (final Exception ex3) {
            pluginCall.reject(ex3.getLocalizedMessage());
        }
    }
}
