package com.onesignal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

class JSONUtils
{
    static boolean compareJSONArrays(final JSONArray jsonArray, final JSONArray jsonArray2) {
        if (jsonArray == null && jsonArray2 == null) {
            return true;
        }
        if (jsonArray != null) {
            if (jsonArray2 != null) {
                if (jsonArray.length() != jsonArray2.length()) {
                    return false;
                }
                int i = 0;
                try {
                Label_0036:
                    while (i < jsonArray.length()) {
                        for (int j = 0; j < jsonArray2.length(); ++j) {
                            if (normalizeType(jsonArray.get(i)).equals(normalizeType(jsonArray2.get(j)))) {
                                ++i;
                                continue Label_0036;
                            }
                        }
                        return false;
                    }
                    return true;
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
    
    private static Object convertNestedJSONType(final Object o) throws JSONException {
        if (o instanceof JSONObject) {
            return jsonObjectToMapNonNull((JSONObject)o);
        }
        Object jsonArrayToListNonNull = o;
        if (o instanceof JSONArray) {
            jsonArrayToListNonNull = jsonArrayToListNonNull((JSONArray)o);
        }
        return jsonArrayToListNonNull;
    }
    
    static JSONObject generateJsonDiff(final JSONObject jsonObject, final JSONObject jsonObject2, final JSONObject jsonObject3, final Set<String> set) {
        if (jsonObject == null) {
            return null;
        }
        if (jsonObject2 == null) {
            return jsonObject3;
        }
        final Iterator keys = jsonObject2.keys();
        JSONObject jsonObject4;
        if (jsonObject3 != null) {
            jsonObject4 = jsonObject3;
        }
        else {
            jsonObject4 = new JSONObject();
        }
        while (keys.hasNext()) {
            try {
                final String s = (String)keys.next();
                final Object value = jsonObject2.get(s);
                if (jsonObject.has(s)) {
                    if (value instanceof JSONObject) {
                        final JSONObject jsonObject5 = jsonObject.getJSONObject(s);
                        JSONObject jsonObject6;
                        if (jsonObject3 != null && jsonObject3.has(s)) {
                            jsonObject6 = jsonObject3.getJSONObject(s);
                        }
                        else {
                            jsonObject6 = null;
                        }
                        final String string = generateJsonDiff(jsonObject5, (JSONObject)value, jsonObject6, set).toString();
                        if (string.equals((Object)"{}")) {
                            continue;
                        }
                        jsonObject4.put(s, (Object)new JSONObject(string));
                    }
                    else if (value instanceof JSONArray) {
                        handleJsonArray(s, (JSONArray)value, jsonObject.getJSONArray(s), jsonObject4);
                    }
                    else if (set != null && set.contains((Object)s)) {
                        jsonObject4.put(s, value);
                    }
                    else {
                        final Object value2 = jsonObject.get(s);
                        if (value.equals(value2)) {
                            continue;
                        }
                        if (value2 instanceof Number && value instanceof Number) {
                            if (((Number)value2).doubleValue() == ((Number)value).doubleValue()) {
                                continue;
                            }
                            jsonObject4.put(s, value);
                        }
                        else {
                            jsonObject4.put(s, value);
                        }
                    }
                }
                else if (value instanceof JSONObject) {
                    jsonObject4.put(s, (Object)new JSONObject(value.toString()));
                }
                else if (value instanceof JSONArray) {
                    handleJsonArray(s, (JSONArray)value, null, jsonObject4);
                }
                else {
                    jsonObject4.put(s, value);
                }
            }
            catch (final JSONException ex) {
                ex.printStackTrace();
            }
        }
        return jsonObject4;
    }
    
    static JSONObject getJSONObjectWithoutBlankValues(ImmutableJSONObject optJSONObject, String keys) {
        if (!optJSONObject.has(keys)) {
            return null;
        }
        final JSONObject jsonObject = new JSONObject();
        optJSONObject = (ImmutableJSONObject)optJSONObject.optJSONObject(keys);
        keys = (String)((JSONObject)optJSONObject).keys();
        while (((Iterator)keys).hasNext()) {
            final String s = (String)((Iterator)keys).next();
            try {
                final Object value = ((JSONObject)optJSONObject).get(s);
                if ("".equals(value)) {
                    continue;
                }
                jsonObject.put(s, value);
            }
            catch (final JSONException ex) {}
        }
        return jsonObject;
    }
    
    private static void handleJsonArray(final String s, final JSONArray jsonArray, final JSONArray jsonArray2, final JSONObject jsonObject) throws JSONException {
        if (!s.endsWith("_a") && !s.endsWith("_d")) {
            final String stringNE = toStringNE(jsonArray);
            final JSONArray jsonArray3 = new JSONArray();
            final JSONArray jsonArray4 = new JSONArray();
            String stringNE2;
            if (jsonArray2 == null) {
                stringNE2 = null;
            }
            else {
                stringNE2 = toStringNE(jsonArray2);
            }
            final int n = 0;
            for (int i = 0; i < jsonArray.length(); ++i) {
                final String s2 = (String)jsonArray.get(i);
                if (jsonArray2 == null || !stringNE2.contains((CharSequence)s2)) {
                    jsonArray3.put((Object)s2);
                }
            }
            if (jsonArray2 != null) {
                for (int j = n; j < jsonArray2.length(); ++j) {
                    final String string = jsonArray2.getString(j);
                    if (!stringNE.contains((CharSequence)string)) {
                        jsonArray4.put((Object)string);
                    }
                }
            }
            if (!jsonArray3.toString().equals((Object)"[]")) {
                final StringBuilder sb = new StringBuilder();
                sb.append(s);
                sb.append("_a");
                jsonObject.put(sb.toString(), (Object)jsonArray3);
            }
            if (!jsonArray4.toString().equals((Object)"[]")) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(s);
                sb2.append("_d");
                jsonObject.put(sb2.toString(), (Object)jsonArray4);
            }
            return;
        }
        jsonObject.put(s, (Object)jsonArray);
    }
    
    static List<Object> jsonArrayToList(final JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return null;
        }
        return jsonArrayToListNonNull(jsonArray);
    }
    
    private static List<Object> jsonArrayToListNonNull(final JSONArray jsonArray) throws JSONException {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < jsonArray.length(); ++i) {
            ((List)list).add(convertNestedJSONType(jsonArray.get(i)));
        }
        return (List<Object>)list;
    }
    
    static Map<String, Object> jsonObjectToMap(final JSONObject jsonObject) throws JSONException {
        if (jsonObject != null && jsonObject != JSONObject.NULL) {
            return jsonObjectToMapNonNull(jsonObject);
        }
        return null;
    }
    
    private static Map<String, Object> jsonObjectToMapNonNull(final JSONObject jsonObject) throws JSONException {
        final HashMap hashMap = new HashMap();
        final Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String s = (String)keys.next();
            ((Map)hashMap).put((Object)s, convertNestedJSONType(jsonObject.get(s)));
        }
        return (Map<String, Object>)hashMap;
    }
    
    public static Object normalizeType(final Object o) {
        final Class<?> class1 = o.getClass();
        if (class1.equals(Integer.class)) {
            return o;
        }
        Object value = o;
        if (class1.equals(Float.class)) {
            value = o;
        }
        return value;
    }
    
    static String toStringNE(final JSONArray jsonArray) {
        String s = "[";
        int i = 0;
        while (true) {
            try {
                while (i < jsonArray.length()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(s);
                    sb.append("\"");
                    sb.append(jsonArray.getString(i));
                    sb.append("\"");
                    final String string = sb.toString();
                    ++i;
                    s = string;
                }
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(s);
                sb2.append("]");
                return sb2.toString();
            }
            catch (final JSONException ex) {
                continue;
            }
            break;
        }
    }
    
    static String toUnescapedEUIDString(final JSONObject jsonObject) {
        String s2;
        final String s = s2 = jsonObject.toString();
        if (jsonObject.has("external_user_id")) {
            final Matcher matcher = Pattern.compile("(?<=\"external_user_id\":\").*?(?=\")").matcher((CharSequence)s);
            s2 = s;
            if (matcher.find()) {
                final String group = matcher.group(0);
                s2 = s;
                if (group != null) {
                    s2 = matcher.replaceAll(Matcher.quoteReplacement(group.replace((CharSequence)"\\/", (CharSequence)"/")));
                }
            }
        }
        return s2;
    }
}
