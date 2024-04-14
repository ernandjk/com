package com.getcapacitor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;

public class PluginResult
{
    private final JSObject json;
    
    public PluginResult() {
        this(new JSObject());
    }
    
    public PluginResult(final JSObject json) {
        this.json = json;
    }
    
    public JSObject getWrappedResult() {
        final JSObject jsObject = new JSObject();
        jsObject.put("pluginId", this.json.getString("pluginId"));
        jsObject.put("methodName", this.json.getString("methodName"));
        jsObject.put("success", this.json.getBoolean("success", false));
        jsObject.put("data", this.json.getJSObject("data"));
        jsObject.put("error", this.json.getJSObject("error"));
        return jsObject;
    }
    
    PluginResult jsonPut(final String s, final Object o) {
        try {
            this.json.put(s, o);
        }
        catch (final Exception ex) {
            Logger.error(Logger.tags("Plugin"), "", (Throwable)ex);
        }
        return this;
    }
    
    public PluginResult put(final String s, final double n) {
        return this.jsonPut(s, n);
    }
    
    public PluginResult put(final String s, final int n) {
        return this.jsonPut(s, n);
    }
    
    public PluginResult put(final String s, final long n) {
        return this.jsonPut(s, n);
    }
    
    public PluginResult put(final String s, final PluginResult pluginResult) {
        return this.jsonPut(s, pluginResult.json);
    }
    
    public PluginResult put(final String s, final Object o) {
        return this.jsonPut(s, o);
    }
    
    public PluginResult put(final String s, final Date date) {
        final TimeZone timeZone = TimeZone.getTimeZone("UTC");
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        ((DateFormat)simpleDateFormat).setTimeZone(timeZone);
        return this.jsonPut(s, ((DateFormat)simpleDateFormat).format(date));
    }
    
    public PluginResult put(final String s, final boolean b) {
        return this.jsonPut(s, b);
    }
    
    @Override
    public String toString() {
        return this.json.toString();
    }
}
