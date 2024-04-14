package com.getcapacitor.community.nativemarket;

import com.getcapacitor.PluginMethod;
import android.content.Context;
import android.net.Uri;
import android.content.Intent;
import com.getcapacitor.PluginCall;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.Plugin;

@CapacitorPlugin(name = "NativeMarket")
public class NativeMarket extends Plugin
{
    @PluginMethod
    public void openCollection(final PluginCall pluginCall) {
        try {
            if (pluginCall.hasOption("name")) {
                final String string = pluginCall.getString("name");
                final Context context = this.getContext();
                final Intent intent = new Intent("android.intent.action.VIEW");
                final StringBuilder sb = new StringBuilder("https://play.google.com/store/apps/collection/");
                sb.append(string);
                intent.setData(Uri.parse(sb.toString()));
                intent.addFlags(268435456);
                context.startActivity(intent);
                pluginCall.resolve();
            }
            else {
                pluginCall.reject("name is missing");
            }
        }
        catch (final Exception ex) {
            pluginCall.error(ex.getLocalizedMessage());
        }
    }
    
    @PluginMethod
    public void openDevPage(final PluginCall pluginCall) {
        try {
            if (pluginCall.hasOption("devId")) {
                final String string = pluginCall.getString("devId");
                final Context context = this.getContext();
                final Intent intent = new Intent("android.intent.action.VIEW");
                final StringBuilder sb = new StringBuilder("https://play.google.com/store/apps/dev?id=");
                sb.append(string);
                intent.setData(Uri.parse(sb.toString()));
                intent.addFlags(268435456);
                context.startActivity(intent);
                pluginCall.resolve();
            }
            else {
                pluginCall.reject("devId is missing");
            }
        }
        catch (final Exception ex) {
            pluginCall.error(ex.getLocalizedMessage());
        }
    }
    
    @PluginMethod
    public void openEditorChoicePage(final PluginCall pluginCall) {
        try {
            if (pluginCall.hasOption("editorChoice")) {
                final String string = pluginCall.getString("editorChoice");
                final Context context = this.getContext();
                final Intent intent = new Intent("android.intent.action.VIEW");
                final StringBuilder sb = new StringBuilder("https://play.google.com/store/apps/topic?id=");
                sb.append(string);
                intent.setData(Uri.parse(sb.toString()));
                intent.addFlags(268435456);
                context.startActivity(intent);
                pluginCall.resolve();
            }
            else {
                pluginCall.reject("editorChoice is missing");
            }
        }
        catch (final Exception ex) {
            pluginCall.error(ex.getLocalizedMessage());
        }
    }
    
    @PluginMethod
    public void openStoreListing(final PluginCall pluginCall) {
        try {
            if (pluginCall.hasOption("appId")) {
                final String string = pluginCall.getString("appId");
                final Context applicationContext = this.bridge.getActivity().getApplicationContext();
                final StringBuilder sb = new StringBuilder("market://details?id=");
                sb.append(string);
                final Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(sb.toString()));
                intent.addFlags(268435456);
                applicationContext.startActivity(intent);
                pluginCall.resolve();
            }
            else {
                pluginCall.reject("appId is missing");
            }
        }
        catch (final Exception ex) {
            pluginCall.error(ex.getLocalizedMessage());
        }
    }
    
    @PluginMethod
    public void search(final PluginCall pluginCall) {
        try {
            if (pluginCall.hasOption("terms")) {
                final String string = pluginCall.getString("terms");
                final Context context = this.getContext();
                final StringBuilder sb = new StringBuilder("market://search?q=");
                sb.append(string);
                final Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(sb.toString()));
                intent.addFlags(268435456);
                context.startActivity(intent);
                pluginCall.resolve();
            }
            else {
                pluginCall.reject("terms is missing");
            }
        }
        catch (final Exception ex) {
            pluginCall.error(ex.getLocalizedMessage());
        }
    }
}
