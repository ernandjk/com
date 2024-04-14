package com.getcapacitor;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

public class JSObject extends JSONObject
{
    public JSObject() {
    }
    
    public JSObject(final String s) throws JSONException {
        super(s);
    }
    
    public JSObject(final JSONObject jsonObject, final String[] array) throws JSONException {
        super(jsonObject, array);
    }
    
    public static JSObject fromJSONObject(final JSONObject jsonObject) throws JSONException {
        final Iterator keys = jsonObject.keys();
        final ArrayList list = new ArrayList();
        while (keys.hasNext()) {
            ((List)list).add((Object)keys.next());
        }
        return new JSObject(jsonObject, (String[])((List)list).toArray((Object[])new String[((List)list).size()]));
    }
    
    public Boolean getBool(final String s) {
        return this.getBoolean(s, null);
    }
    
    public Boolean getBoolean(final String s, final Boolean b) {
        try {
            return super.getBoolean(s);
        }
        catch (final JSONException ex) {
            return b;
        }
    }
    
    public Integer getInteger(final String s) {
        return this.getInteger(s, null);
    }
    
    public Integer getInteger(final String s, final Integer n) {
        try {
            return super.getInt(s);
        }
        catch (final JSONException ex) {
            return n;
        }
    }
    
    public JSObject getJSObject(final String s) {
        try {
            return this.getJSObject(s, null);
        }
        catch (final JSONException ex) {
            return null;
        }
    }
    
    public JSObject getJSObject(final String s, final JSObject jsObject) throws JSONException {
        try {
            final Object value = this.get(s);
            if (value instanceof JSONObject) {
                final Iterator keys = ((JSONObject)value).keys();
                final ArrayList list = new ArrayList();
                while (keys.hasNext()) {
                    ((List)list).add((Object)keys.next());
                }
                return new JSObject((JSONObject)value, (String[])((List)list).toArray((Object[])new String[((List)list).size()]));
            }
            return jsObject;
        }
        catch (final JSONException ex) {
            return jsObject;
        }
    }
    
    public String getString(final String s) {
        return this.getString(s, null);
    }
    
    public String getString(final String s, final String s2) {
        try {
            final String string = super.getString(s);
            if (!super.isNull(s)) {
                return string;
            }
            return s2;
        }
        catch (final JSONException ex) {
            return s2;
        }
    }
    
    public JSObject put(final String s, final double n) {
        try {
            super.put(s, n);
            return this;
        }
        catch (final JSONException ex) {
            return this;
        }
    }
    
    public JSObject put(final String s, final int n) {
        try {
            super.put(s, n);
            return this;
        }
        catch (final JSONException ex) {
            return this;
        }
    }
    
    public JSObject put(final String s, final long n) {
        try {
            super.put(s, n);
            return this;
        }
        catch (final JSONException ex) {
            return this;
        }
    }
    
    public JSObject put(final String s, final Object o) {
        try {
            super.put(s, o);
            return this;
        }
        catch (final JSONException ex) {
            return this;
        }
    }
    
    public JSObject put(final String s, final String s2) {
        try {
            super.put(s, (Object)s2);
            return this;
        }
        catch (final JSONException ex) {
            return this;
        }
    }
    
    public JSObject put(final String s, final boolean b) {
        try {
            super.put(s, b);
            return this;
        }
        catch (final JSONException ex) {
            return this;
        }
    }
    
    public JSObject putSafe(final String s, final Object o) throws JSONException {
        return (JSObject)super.put(s, o);
    }
}
