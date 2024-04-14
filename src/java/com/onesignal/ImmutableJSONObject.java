package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

class ImmutableJSONObject
{
    private final JSONObject jsonObject;
    
    public ImmutableJSONObject() {
        this.jsonObject = new JSONObject();
    }
    
    public ImmutableJSONObject(final JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
    
    public long getLong(final String s) throws JSONException {
        return this.jsonObject.getLong(s);
    }
    
    public boolean has(final String s) {
        return this.jsonObject.has(s);
    }
    
    public Object opt(final String s) {
        return this.jsonObject.opt(s);
    }
    
    public boolean optBoolean(final String s) {
        return this.jsonObject.optBoolean(s);
    }
    
    public boolean optBoolean(final String s, final boolean b) {
        return this.jsonObject.optBoolean(s, b);
    }
    
    public int optInt(final String s) {
        return this.jsonObject.optInt(s);
    }
    
    public int optInt(final String s, final int n) {
        return this.jsonObject.optInt(s, n);
    }
    
    public JSONObject optJSONObject(final String s) {
        return this.jsonObject.optJSONObject(s);
    }
    
    public long optLong(final String s) {
        return this.jsonObject.optLong(s);
    }
    
    public String optString(final String s) {
        return this.jsonObject.optString(s);
    }
    
    public String optString(final String s, final String s2) {
        return this.jsonObject.optString(s, s2);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ImmutableJSONObject{jsonObject=");
        sb.append((Object)this.jsonObject);
        sb.append('}');
        return sb.toString();
    }
}
