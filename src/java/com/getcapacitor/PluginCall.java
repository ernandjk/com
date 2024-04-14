package com.getcapacitor;

import java.util.List;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.ArrayList;
import org.json.JSONArray;

public class PluginCall
{
    public static final String CALLBACK_ID_DANGLING = "-1";
    private final String callbackId;
    private final JSObject data;
    @Deprecated
    private boolean isReleased;
    private boolean keepAlive;
    private final String methodName;
    private final MessageHandler msgHandler;
    private final String pluginId;
    
    public PluginCall(final MessageHandler msgHandler, final String pluginId, final String callbackId, final String methodName, final JSObject data) {
        this.keepAlive = false;
        this.isReleased = false;
        this.msgHandler = msgHandler;
        this.pluginId = pluginId;
        this.callbackId = callbackId;
        this.methodName = methodName;
        this.data = data;
    }
    
    @Deprecated
    public void error(final String s) {
        this.reject(s);
    }
    
    @Deprecated
    public void error(final String s, final Exception ex) {
        this.reject(s, ex);
    }
    
    @Deprecated
    public void error(final String s, final String s2, final Exception ex) {
        this.reject(s, s2, ex);
    }
    
    public void errorCallback(final String s) {
        final PluginResult pluginResult = new PluginResult();
        try {
            pluginResult.put("message", s);
        }
        catch (final Exception ex) {
            Logger.error(Logger.tags("Plugin"), ex.toString(), null);
        }
        this.msgHandler.sendResponseMessage(this, null, pluginResult);
    }
    
    public JSArray getArray(final String s) {
        return this.getArray(s, null);
    }
    
    public JSArray getArray(final String s, final JSArray jsArray) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return jsArray;
        }
        if (!(opt instanceof JSONArray)) {
            return jsArray;
        }
        try {
            final JSONArray jsonArray = (JSONArray)opt;
            final ArrayList list = new ArrayList();
            for (int i = 0; i < jsonArray.length(); ++i) {
                ((List)list).add(jsonArray.get(i));
            }
            return new JSArray(((List)list).toArray());
        }
        catch (final JSONException ex) {
            return jsArray;
        }
    }
    
    public Boolean getBoolean(final String s) {
        return this.getBoolean(s, null);
    }
    
    public Boolean getBoolean(final String s, final Boolean b) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return b;
        }
        if (opt instanceof Boolean) {
            return (Boolean)opt;
        }
        return b;
    }
    
    public String getCallbackId() {
        return this.callbackId;
    }
    
    public JSObject getData() {
        return this.data;
    }
    
    public Double getDouble(final String s) {
        return this.getDouble(s, null);
    }
    
    public Double getDouble(final String s, final Double n) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return n;
        }
        if (opt instanceof Double) {
            return (Double)opt;
        }
        if (opt instanceof Float) {
            return (double)opt;
        }
        if (opt instanceof Integer) {
            return (double)opt;
        }
        return n;
    }
    
    public Float getFloat(final String s) {
        return this.getFloat(s, null);
    }
    
    public Float getFloat(final String s, final Float n) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return n;
        }
        if (opt instanceof Float) {
            return (Float)opt;
        }
        if (opt instanceof Double) {
            return ((Double)opt).floatValue();
        }
        if (opt instanceof Integer) {
            return (float)opt;
        }
        return n;
    }
    
    public Integer getInt(final String s) {
        return this.getInt(s, null);
    }
    
    public Integer getInt(final String s, final Integer n) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return n;
        }
        if (opt instanceof Integer) {
            return (Integer)opt;
        }
        return n;
    }
    
    public Long getLong(final String s) {
        return this.getLong(s, null);
    }
    
    public Long getLong(final String s, final Long n) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return n;
        }
        if (opt instanceof Long) {
            return (Long)opt;
        }
        return n;
    }
    
    public String getMethodName() {
        return this.methodName;
    }
    
    public JSObject getObject(final String s) {
        return this.getObject(s, null);
    }
    
    public JSObject getObject(final String s, final JSObject jsObject) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return jsObject;
        }
        if (!(opt instanceof JSONObject)) {
            return jsObject;
        }
        try {
            return JSObject.fromJSONObject((JSONObject)opt);
        }
        catch (final JSONException ex) {
            return jsObject;
        }
    }
    
    public String getPluginId() {
        return this.pluginId;
    }
    
    public String getString(final String s) {
        return this.getString(s, null);
    }
    
    public String getString(final String s, final String s2) {
        final Object opt = this.data.opt(s);
        if (opt == null) {
            return s2;
        }
        if (opt instanceof String) {
            return (String)opt;
        }
        return s2;
    }
    
    public boolean hasOption(final String s) {
        return this.data.has(s);
    }
    
    public boolean isKeptAlive() {
        return this.keepAlive;
    }
    
    @Deprecated
    public boolean isReleased() {
        return this.isReleased;
    }
    
    @Deprecated
    public boolean isSaved() {
        return this.isKeptAlive();
    }
    
    public void reject(final String s) {
        this.reject(s, null, null, null);
    }
    
    public void reject(final String s, final JSObject jsObject) {
        this.reject(s, null, null, jsObject);
    }
    
    public void reject(final String s, final Exception ex) {
        this.reject(s, null, ex, null);
    }
    
    public void reject(final String s, final Exception ex, final JSObject jsObject) {
        this.reject(s, null, ex, jsObject);
    }
    
    public void reject(final String s, final String s2) {
        this.reject(s, s2, null, null);
    }
    
    public void reject(final String s, final String s2, final JSObject jsObject) {
        this.reject(s, s2, null, jsObject);
    }
    
    public void reject(final String s, final String s2, final Exception ex) {
        this.reject(s, s2, ex, null);
    }
    
    public void reject(final String s, final String s2, final Exception ex, final JSObject jsObject) {
        final PluginResult pluginResult = new PluginResult();
        if (ex != null) {
            Logger.error(Logger.tags("Plugin"), s, (Throwable)ex);
        }
        try {
            pluginResult.put("message", s);
            pluginResult.put("code", s2);
            if (jsObject != null) {
                pluginResult.put("data", jsObject);
            }
        }
        catch (final Exception ex2) {
            Logger.error(Logger.tags("Plugin"), ex2.getMessage(), (Throwable)ex2);
        }
        this.msgHandler.sendResponseMessage(this, null, pluginResult);
    }
    
    public void release(final Bridge bridge) {
        this.keepAlive = false;
        bridge.releaseCall(this);
        this.isReleased = true;
    }
    
    public void resolve() {
        this.msgHandler.sendResponseMessage(this, null, null);
    }
    
    public void resolve(final JSObject jsObject) {
        this.msgHandler.sendResponseMessage(this, new PluginResult(jsObject), null);
    }
    
    @Deprecated
    public void save() {
        this.setKeepAlive(true);
    }
    
    public void setKeepAlive(final Boolean b) {
        this.keepAlive = b;
    }
    
    @Deprecated
    public void success() {
        this.resolve(new JSObject());
    }
    
    @Deprecated
    public void success(final JSObject jsObject) {
        this.msgHandler.sendResponseMessage(this, new PluginResult(jsObject), null);
    }
    
    public void successCallback(final PluginResult pluginResult) {
        if ("-1".equals((Object)this.callbackId)) {
            return;
        }
        this.msgHandler.sendResponseMessage(this, pluginResult, null);
    }
    
    public void unavailable() {
        this.unavailable("not available");
    }
    
    public void unavailable(final String s) {
        this.reject(s, "UNAVAILABLE", null, null);
    }
    
    public void unimplemented() {
        this.unimplemented("not implemented");
    }
    
    public void unimplemented(final String s) {
        this.reject(s, "UNIMPLEMENTED", null, null);
    }
    
    class PluginCallDataTypeException extends Exception
    {
        final PluginCall this$0;
        
        PluginCallDataTypeException(final PluginCall this$0, final String s) {
            this.this$0 = this$0;
            super(s);
        }
    }
}
