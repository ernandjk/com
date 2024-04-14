package com.onesignal.cordova;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult$Status;
import org.apache.cordova.CallbackContext;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Map;
import com.onesignal.OneSignal;
import java.util.HashMap;
import org.json.JSONArray;

public class OneSignalInAppMessagingController
{
    public static boolean addTriggers(final JSONArray jsonArray) {
        try {
            final JSONObject jsonObject = jsonArray.getJSONObject(0);
            final HashMap hashMap = new HashMap();
            final Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                final String s = (String)keys.next();
                ((Map)hashMap).put((Object)s, jsonObject.get(s));
            }
            OneSignal.addTriggers((Map)hashMap);
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    private static void callbackSuccess(final CallbackContext callbackContext, final JSONObject jsonObject) {
        JSONObject jsonObject2 = jsonObject;
        if (jsonObject == null) {
            jsonObject2 = new JSONObject();
        }
        final PluginResult pluginResult = new PluginResult(PluginResult$Status.OK, jsonObject2);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }
    
    public static boolean getTriggerValueForKey(final CallbackContext callbackContext, final JSONArray jsonArray) {
        try {
            final Object triggerValueForKey = OneSignal.getTriggerValueForKey(jsonArray.getString(0));
            if (triggerValueForKey == null) {
                callbackSuccess(callbackContext, new JSONObject());
            }
            else {
                final StringBuilder sb = new StringBuilder("{value:");
                sb.append(triggerValueForKey.toString());
                sb.append("}");
                callbackSuccess(callbackContext, new JSONObject(sb.toString()));
            }
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean isInAppMessagingPaused(final CallbackContext callbackContext) {
        CallbackHelper.callbackSuccessBoolean(callbackContext, OneSignal.isInAppMessagingPaused());
        return true;
    }
    
    public static boolean pauseInAppMessages(final JSONArray jsonArray) {
        try {
            OneSignal.pauseInAppMessages(jsonArray.getBoolean(0));
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean removeTriggersForKeys(JSONArray jsonArray) {
        try {
            jsonArray = jsonArray.getJSONArray(0);
            final ArrayList list = new ArrayList();
            for (int i = 0; i < jsonArray.length(); ++i) {
                ((List)list).add((Object)jsonArray.getString(i));
            }
            OneSignal.removeTriggersForKeys((Collection)list);
            return true;
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
