package com.capacitorjs.plugins.statusbar;

import com.getcapacitor.PluginMethod;
import com.getcapacitor.JSObject;
import com.getcapacitor.util.WebColor;
import java.util.Locale;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(name = "StatusBar")
public class StatusBarPlugin extends Plugin
{
    private StatusBar implementation;
    
    @PluginMethod
    public void getInfo(final PluginCall pluginCall) {
        final StatusBarInfo info = this.implementation.getInfo();
        final JSObject jsObject = new JSObject();
        jsObject.put("visible", info.isVisible());
        jsObject.put("style", info.getStyle());
        jsObject.put("color", info.getColor());
        jsObject.put("overlays", info.isOverlays());
        pluginCall.resolve(jsObject);
    }
    
    @PluginMethod
    public void hide(final PluginCall pluginCall) {
        this.getBridge().executeOnMainThread((Runnable)new StatusBarPlugin$$ExternalSyntheticLambda1(this, pluginCall));
    }
    
    public void load() {
        this.implementation = new StatusBar(this.getActivity());
    }
    
    @PluginMethod
    public void setBackgroundColor(final PluginCall pluginCall) {
        final String string = pluginCall.getString("color");
        if (string == null) {
            pluginCall.reject("Color must be provided");
            return;
        }
        this.getBridge().executeOnMainThread((Runnable)new StatusBarPlugin$$ExternalSyntheticLambda0(this, string, pluginCall));
    }
    
    @PluginMethod
    public void setOverlaysWebView(final PluginCall pluginCall) {
        this.getBridge().executeOnMainThread((Runnable)new StatusBarPlugin$$ExternalSyntheticLambda3(this, pluginCall.getBoolean("overlay", Boolean.valueOf(true)), pluginCall));
    }
    
    @PluginMethod
    public void setStyle(final PluginCall pluginCall) {
        final String string = pluginCall.getString("style");
        if (string == null) {
            pluginCall.reject("Style must be provided");
            return;
        }
        this.getBridge().executeOnMainThread((Runnable)new StatusBarPlugin$$ExternalSyntheticLambda4(this, string, pluginCall));
    }
    
    @PluginMethod
    public void show(final PluginCall pluginCall) {
        this.getBridge().executeOnMainThread((Runnable)new StatusBarPlugin$$ExternalSyntheticLambda2(this, pluginCall));
    }
}
