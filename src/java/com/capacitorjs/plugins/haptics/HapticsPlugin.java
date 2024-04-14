package com.capacitorjs.plugins.haptics;

import com.capacitorjs.plugins.haptics.arguments.HapticsNotificationType;
import com.getcapacitor.PluginMethod;
import com.capacitorjs.plugins.haptics.arguments.HapticsVibrationType;
import com.capacitorjs.plugins.haptics.arguments.HapticsImpactType;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(name = "Haptics")
public class HapticsPlugin extends Plugin
{
    private Haptics implementation;
    
    @PluginMethod
    public void impact(final PluginCall pluginCall) {
        this.implementation.performHaptics((HapticsVibrationType)HapticsImpactType.fromString(pluginCall.getString("style")));
        pluginCall.resolve();
    }
    
    public void load() {
        this.implementation = new Haptics(this.getContext());
    }
    
    @PluginMethod
    public void notification(final PluginCall pluginCall) {
        this.implementation.performHaptics((HapticsVibrationType)HapticsNotificationType.fromString(pluginCall.getString("type")));
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void selectionChanged(final PluginCall pluginCall) {
        this.implementation.selectionChanged();
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void selectionEnd(final PluginCall pluginCall) {
        this.implementation.selectionEnd();
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void selectionStart(final PluginCall pluginCall) {
        this.implementation.selectionStart();
        pluginCall.resolve();
    }
    
    @PluginMethod
    public void vibrate(final PluginCall pluginCall) {
        this.implementation.vibrate((int)pluginCall.getInt("duration", Integer.valueOf(300)));
        pluginCall.resolve();
    }
}
