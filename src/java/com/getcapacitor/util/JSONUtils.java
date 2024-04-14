package com.getcapacitor.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils
{
    public static String[] getArray(final JSONObject jsonObject, final String s, final String[] array) {
        final String deepestKey = getDeepestKey(s);
        try {
            final JSONArray jsonArray = getDeepestObject(jsonObject, s).getJSONArray(deepestKey);
            if (jsonArray == null) {
                return array;
            }
            final int length = jsonArray.length();
            final String[] array2 = new String[length];
            for (int i = 0; i < length; ++i) {
                array2[i] = (String)jsonArray.get(i);
            }
            return array2;
        }
        catch (final JSONException ex) {
            return array;
        }
    }
    
    public static boolean getBoolean(final JSONObject jsonObject, final String s, final boolean b) {
        final String deepestKey = getDeepestKey(s);
        try {
            return getDeepestObject(jsonObject, s).getBoolean(deepestKey);
        }
        catch (final JSONException ex) {
            return b;
        }
    }
    
    private static String getDeepestKey(final String s) {
        final String[] split = s.split("\\.");
        if (split.length > 0) {
            return split[split.length - 1];
        }
        return null;
    }
    
    private static JSONObject getDeepestObject(JSONObject jsonObject, final String s) throws JSONException {
        final String[] split = s.split("\\.");
        for (int i = 0; i < split.length - 1; ++i) {
            jsonObject = jsonObject.getJSONObject(split[i]);
        }
        return jsonObject;
    }
    
    public static int getInt(final JSONObject jsonObject, final String s, final int n) {
        final String deepestKey = getDeepestKey(s);
        try {
            return getDeepestObject(jsonObject, s).getInt(deepestKey);
        }
        catch (final JSONException ex) {
            return n;
        }
    }
    
    public static JSONObject getObject(JSONObject jsonObject, final String s) {
        final String deepestKey = getDeepestKey(s);
        try {
            jsonObject = getDeepestObject(jsonObject, s).getJSONObject(deepestKey);
            return jsonObject;
        }
        catch (final JSONException ex) {
            return null;
        }
    }
    
    public static String getString(final JSONObject jsonObject, final String s, final String s2) {
        final String deepestKey = getDeepestKey(s);
        try {
            final String string = getDeepestObject(jsonObject, s).getString(deepestKey);
            if (string == null) {
                return s2;
            }
            return string;
        }
        catch (final JSONException ex) {
            return s2;
        }
    }
}
