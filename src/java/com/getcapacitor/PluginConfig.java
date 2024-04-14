package com.getcapacitor;

import com.getcapacitor.util.JSONUtils;
import org.json.JSONObject;

public class PluginConfig
{
    private final JSONObject config;
    
    PluginConfig(final JSONObject config) {
        this.config = config;
    }
    
    public String[] getArray(final String s) {
        return this.getArray(s, null);
    }
    
    public String[] getArray(final String s, final String[] array) {
        return JSONUtils.getArray(this.config, s, array);
    }
    
    public boolean getBoolean(final String s, final boolean b) {
        return JSONUtils.getBoolean(this.config, s, b);
    }
    
    public JSONObject getConfigJSON() {
        return this.config;
    }
    
    public int getInt(final String s, final int n) {
        return JSONUtils.getInt(this.config, s, n);
    }
    
    public JSONObject getObject(final String s) {
        return JSONUtils.getObject(this.config, s);
    }
    
    public String getString(final String s) {
        return this.getString(s, null);
    }
    
    public String getString(final String s, final String s2) {
        return JSONUtils.getString(this.config, s, s2);
    }
    
    public boolean isEmpty() {
        return this.config.length() == 0;
    }
}
