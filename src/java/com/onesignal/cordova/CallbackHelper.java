package com.onesignal.cordova;

import org.json.JSONObject;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult$Status;
import org.apache.cordova.CallbackContext;

public class CallbackHelper
{
    public static void callbackError(final CallbackContext callbackContext, final String s) {
        final PluginResult pluginResult = new PluginResult(PluginResult$Status.ERROR, s);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }
    
    public static void callbackError(final CallbackContext callbackContext, final JSONObject jsonObject) {
        JSONObject jsonObject2 = jsonObject;
        if (jsonObject == null) {
            jsonObject2 = new JSONObject();
        }
        final PluginResult pluginResult = new PluginResult(PluginResult$Status.ERROR, jsonObject2);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }
    
    public static void callbackSuccess(final CallbackContext callbackContext, final JSONObject jsonObject) {
        JSONObject jsonObject2 = jsonObject;
        if (jsonObject == null) {
            jsonObject2 = new JSONObject();
        }
        final PluginResult pluginResult = new PluginResult(PluginResult$Status.OK, jsonObject2);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }
    
    public static void callbackSuccessBoolean(final CallbackContext callbackContext, final boolean b) {
        final PluginResult pluginResult = new PluginResult(PluginResult$Status.OK, b);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
    }
}
