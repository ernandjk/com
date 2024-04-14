package com.getcapacitor;

import org.json.JSONException;

public class JSValue
{
    private final Object value;
    
    public JSValue(final PluginCall pluginCall, final String s) {
        this.value = this.toValue(pluginCall, s);
    }
    
    private Object toValue(final PluginCall pluginCall, final String s) {
        final JSArray array = pluginCall.getArray(s, null);
        if (array != null) {
            return array;
        }
        final JSObject object = pluginCall.getObject(s, null);
        if (object != null) {
            return object;
        }
        final String string = pluginCall.getString(s, null);
        if (string != null) {
            return string;
        }
        return pluginCall.getData().opt(s);
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public JSArray toJSArray() throws JSONException {
        final Object value = this.value;
        if (value instanceof JSArray) {
            return (JSArray)value;
        }
        throw new JSONException("JSValue could not be coerced to JSArray.");
    }
    
    public JSObject toJSObject() throws JSONException {
        final Object value = this.value;
        if (value instanceof JSObject) {
            return (JSObject)value;
        }
        throw new JSONException("JSValue could not be coerced to JSObject.");
    }
    
    @Override
    public String toString() {
        return this.getValue().toString();
    }
}
