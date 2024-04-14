package com.capacitorjs.plugins.app;

import androidx.lifecycle.LifecycleOwner;
import androidx.activity.OnBackPressedCallback;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import androidx.core.content.pm.PackageInfoCompat;
import com.getcapacitor.util.InternalUtils;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PluginCall;
import com.getcapacitor.App$AppRestoredListener;
import com.getcapacitor.App$AppStatusChangeListener;
import com.getcapacitor.Logger;
import com.getcapacitor.JSObject;
import com.getcapacitor.Bridge;
import com.getcapacitor.PluginResult;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(name = "App")
public class AppPlugin extends Plugin
{
    private static final String EVENT_BACK_BUTTON = "backButton";
    private static final String EVENT_PAUSE = "pause";
    private static final String EVENT_RESTORED_RESULT = "appRestoredResult";
    private static final String EVENT_RESUME = "resume";
    private static final String EVENT_STATE_CHANGE = "appStateChange";
    private static final String EVENT_URL_OPEN = "appUrlOpen";
    private boolean hasPausedEver;
    
    public AppPlugin() {
        this.hasPausedEver = false;
    }
    
    static /* synthetic */ boolean access$000(final AppPlugin appPlugin, final String s) {
        return appPlugin.hasListeners(s);
    }
    
    static /* synthetic */ void access$400(final AppPlugin appPlugin, final String s, final JSObject jsObject, final boolean b) {
        appPlugin.notifyListeners(s, jsObject, b);
    }
    
    private void unsetAppListeners() {
        this.bridge.getApp().setStatusChangeListener((App$AppStatusChangeListener)null);
        this.bridge.getApp().setAppRestoredListener((App$AppRestoredListener)null);
    }
    
    @PluginMethod
    public void exitApp(final PluginCall pluginCall) {
        this.unsetAppListeners();
        pluginCall.resolve();
        this.getBridge().getActivity().finish();
    }
    
    @PluginMethod
    public void getInfo(final PluginCall pluginCall) {
        final JSObject jsObject = new JSObject();
        try {
            final PackageInfo packageInfo = InternalUtils.getPackageInfo(this.getContext().getPackageManager(), this.getContext().getPackageName());
            final ApplicationInfo applicationInfo = this.getContext().getApplicationInfo();
            final int labelRes = applicationInfo.labelRes;
            String s;
            if (labelRes == 0) {
                s = applicationInfo.nonLocalizedLabel.toString();
            }
            else {
                s = this.getContext().getString(labelRes);
            }
            jsObject.put("name", s);
            jsObject.put("id", packageInfo.packageName);
            jsObject.put("build", Integer.toString((int)PackageInfoCompat.getLongVersionCode(packageInfo)));
            jsObject.put("version", packageInfo.versionName);
            pluginCall.resolve(jsObject);
        }
        catch (final Exception ex) {
            pluginCall.reject("Unable to get App Info");
        }
    }
    
    @PluginMethod
    public void getLaunchUrl(final PluginCall pluginCall) {
        final Uri intentUri = this.bridge.getIntentUri();
        if (intentUri != null) {
            final JSObject jsObject = new JSObject();
            jsObject.put("url", intentUri.toString());
            pluginCall.resolve(jsObject);
        }
        else {
            pluginCall.resolve();
        }
    }
    
    @PluginMethod
    public void getState(final PluginCall pluginCall) {
        final JSObject jsObject = new JSObject();
        jsObject.put("isActive", this.bridge.getApp().isActive());
        pluginCall.resolve(jsObject);
    }
    
    protected void handleOnDestroy() {
        this.unsetAppListeners();
    }
    
    protected void handleOnNewIntent(final Intent intent) {
        super.handleOnNewIntent(intent);
        final String action = intent.getAction();
        final Uri data = intent.getData();
        if ("android.intent.action.VIEW".equals((Object)action)) {
            if (data != null) {
                final JSObject jsObject = new JSObject();
                jsObject.put("url", data.toString());
                this.notifyListeners("appUrlOpen", jsObject, true);
            }
        }
    }
    
    protected void handleOnPause() {
        super.handleOnPause();
        this.hasPausedEver = true;
        this.notifyListeners("pause", (JSObject)null);
    }
    
    protected void handleOnResume() {
        super.handleOnResume();
        if (this.hasPausedEver) {
            this.notifyListeners("resume", (JSObject)null);
        }
    }
    
    public void load() {
        this.bridge.getApp().setStatusChangeListener((App$AppStatusChangeListener)new AppPlugin$$ExternalSyntheticLambda0(this));
        this.bridge.getApp().setAppRestoredListener((App$AppRestoredListener)new AppPlugin$$ExternalSyntheticLambda1(this));
        this.getActivity().getOnBackPressedDispatcher().addCallback((LifecycleOwner)this.getActivity(), (OnBackPressedCallback)new OnBackPressedCallback(this, true) {
            final AppPlugin this$0;
            
            public void handleOnBackPressed() {
                if (!AppPlugin.access$000(this.this$0, "backButton")) {
                    if (this.this$0.bridge.getWebView().canGoBack()) {
                        this.this$0.bridge.getWebView().goBack();
                    }
                }
                else {
                    final JSObject jsObject = new JSObject();
                    jsObject.put("canGoBack", this.this$0.bridge.getWebView().canGoBack());
                    AppPlugin.access$400(this.this$0, "backButton", jsObject, true);
                    this.this$0.bridge.triggerJSEvent("backbutton", "document");
                }
            }
        });
    }
    
    @PluginMethod
    public void minimizeApp(final PluginCall pluginCall) {
        final Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.setFlags(268435456);
        this.getActivity().startActivity(intent);
        pluginCall.resolve();
    }
}
