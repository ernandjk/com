package com.capacitorjs.plugins.keyboard;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginMethod;
import android.os.Handler;
import android.os.Looper;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(name = "Keyboard")
public class KeyboardPlugin extends Plugin
{
    private Keyboard implementation;
    
    @PluginMethod
    public void getResizeMode(final PluginCall pluginCall) {
        pluginCall.unimplemented();
    }
    
    protected void handleOnDestroy() {
        this.implementation.setKeyboardEventListener((Keyboard$KeyboardEventListener)null);
    }
    
    @PluginMethod
    public void hide(final PluginCall pluginCall) {
        this.execute((Runnable)new KeyboardPlugin$$ExternalSyntheticLambda0(this, pluginCall));
    }
    
    public void load() {
        this.execute((Runnable)new KeyboardPlugin$$ExternalSyntheticLambda3(this));
    }
    
    void onKeyboardEvent(final String s, final int n) {
        final JSObject jsObject = new JSObject();
        s.hashCode();
        final int hashCode = s.hashCode();
        int n2 = -1;
        switch (hashCode) {
            case -33765642: {
                if (!s.equals((Object)"keyboardWillShow")) {
                    break;
                }
                n2 = 3;
                break;
            }
            case -34092741: {
                if (!s.equals((Object)"keyboardWillHide")) {
                    break;
                }
                n2 = 2;
                break;
            }
            case -661733835: {
                if (!s.equals((Object)"keyboardDidShow")) {
                    break;
                }
                n2 = 1;
                break;
            }
            case -662060934: {
                if (!s.equals((Object)"keyboardDidHide")) {
                    break;
                }
                n2 = 0;
                break;
            }
        }
        switch (n2) {
            case 1:
            case 3: {
                final StringBuilder sb = new StringBuilder("{ 'keyboardHeight': ");
                sb.append(n);
                sb.append(" }");
                this.bridge.triggerWindowJSEvent(s, sb.toString());
                jsObject.put("keyboardHeight", n);
                this.notifyListeners(s, jsObject);
                break;
            }
            case 0:
            case 2: {
                this.bridge.triggerWindowJSEvent(s);
                this.notifyListeners(s, jsObject);
                break;
            }
        }
    }
    
    @PluginMethod
    public void setAccessoryBarVisible(final PluginCall pluginCall) {
        pluginCall.unimplemented();
    }
    
    @PluginMethod
    public void setResizeMode(final PluginCall pluginCall) {
        pluginCall.unimplemented();
    }
    
    @PluginMethod
    public void setScroll(final PluginCall pluginCall) {
        pluginCall.unimplemented();
    }
    
    @PluginMethod
    public void setStyle(final PluginCall pluginCall) {
        pluginCall.unimplemented();
    }
    
    @PluginMethod
    public void show(final PluginCall pluginCall) {
        this.execute((Runnable)new KeyboardPlugin$$ExternalSyntheticLambda4(this, pluginCall));
    }
}
