package com.onesignal.cordova;

import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult$Status;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OSSMSSubscriptionStateChanges;
import org.json.JSONException;
import com.onesignal.OSPermissionStateChanges;
import com.onesignal.OneSignal;
import com.onesignal.OSEmailSubscriptionStateChanges;
import org.json.JSONObject;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSMSSubscriptionObserver;
import com.onesignal.OSPermissionObserver;
import org.apache.cordova.CallbackContext;
import com.onesignal.OSEmailSubscriptionObserver;

public class OneSignalObserverController
{
    private static OSEmailSubscriptionObserver emailSubscriptionObserver;
    private static CallbackContext jsEmailSubscriptionObserverCallBack;
    private static CallbackContext jsPermissionObserverCallBack;
    private static CallbackContext jsSMSSubscriptionObserverCallBack;
    private static CallbackContext jsSubscriptionObserverCallBack;
    private static OSPermissionObserver permissionObserver;
    private static OSSMSSubscriptionObserver smsSubscriptionObserver;
    private static OSSubscriptionObserver subscriptionObserver;
    
    public static boolean addEmailSubscriptionObserver(final CallbackContext jsEmailSubscriptionObserverCallBack) {
        OneSignalObserverController.jsEmailSubscriptionObserverCallBack = jsEmailSubscriptionObserverCallBack;
        if (OneSignalObserverController.emailSubscriptionObserver == null) {
            OneSignal.addEmailSubscriptionObserver(OneSignalObserverController.emailSubscriptionObserver = (OSEmailSubscriptionObserver)new OSEmailSubscriptionObserver() {
                public void onOSEmailSubscriptionChanged(final OSEmailSubscriptionStateChanges osEmailSubscriptionStateChanges) {
                    callbackSuccess(OneSignalObserverController.jsEmailSubscriptionObserverCallBack, renameStateChangesKey(osEmailSubscriptionStateChanges.getFrom().toJSONObject(), osEmailSubscriptionStateChanges.getTo().toJSONObject(), "isSubscribed", "isEmailSubscribed"));
                }
            });
        }
        return true;
    }
    
    public static boolean addPermissionObserver(final CallbackContext jsPermissionObserverCallBack) {
        OneSignalObserverController.jsPermissionObserverCallBack = jsPermissionObserverCallBack;
        if (OneSignalObserverController.permissionObserver == null) {
            OneSignal.addPermissionObserver(OneSignalObserverController.permissionObserver = (OSPermissionObserver)new OSPermissionObserver() {
                public void onOSPermissionChanged(OSPermissionStateChanges osPermissionStateChanges) {
                    final JSONObject jsonObject = osPermissionStateChanges.getFrom().toJSONObject();
                    final JSONObject jsonObject2 = osPermissionStateChanges.getTo().toJSONObject();
                    osPermissionStateChanges = (OSPermissionStateChanges)new JSONObject();
                    try {
                        final boolean boolean1 = jsonObject.getBoolean("areNotificationsEnabled");
                        final int n = 2;
                        int n2;
                        if (boolean1) {
                            n2 = 2;
                        }
                        else {
                            n2 = 1;
                        }
                        jsonObject.put("status", n2);
                        jsonObject.remove("areNotificationsEnabled");
                        int n3;
                        if (jsonObject2.getBoolean("areNotificationsEnabled")) {
                            n3 = n;
                        }
                        else {
                            n3 = 1;
                        }
                        jsonObject2.put("status", n3);
                        jsonObject2.remove("areNotificationsEnabled");
                        ((JSONObject)osPermissionStateChanges).put("from", (Object)jsonObject);
                        ((JSONObject)osPermissionStateChanges).put("to", (Object)jsonObject2);
                    }
                    catch (final JSONException ex) {
                        ex.printStackTrace();
                    }
                    callbackSuccess(OneSignalObserverController.jsPermissionObserverCallBack, (JSONObject)osPermissionStateChanges);
                }
            });
        }
        return true;
    }
    
    public static boolean addSMSSubscriptionObserver(final CallbackContext jsSMSSubscriptionObserverCallBack) {
        OneSignalObserverController.jsSMSSubscriptionObserverCallBack = jsSMSSubscriptionObserverCallBack;
        if (OneSignalObserverController.smsSubscriptionObserver == null) {
            OneSignal.addSMSSubscriptionObserver(OneSignalObserverController.smsSubscriptionObserver = (OSSMSSubscriptionObserver)new OSSMSSubscriptionObserver() {
                public void onSMSSubscriptionChanged(final OSSMSSubscriptionStateChanges ossmsSubscriptionStateChanges) {
                    callbackSuccess(OneSignalObserverController.jsSMSSubscriptionObserverCallBack, renameStateChangesKey(ossmsSubscriptionStateChanges.getFrom().toJSONObject(), ossmsSubscriptionStateChanges.getTo().toJSONObject(), "isSubscribed", "isSMSSubscribed"));
                }
            });
        }
        return true;
    }
    
    public static boolean addSubscriptionObserver(final CallbackContext jsSubscriptionObserverCallBack) {
        OneSignalObserverController.jsSubscriptionObserverCallBack = jsSubscriptionObserverCallBack;
        if (OneSignalObserverController.subscriptionObserver == null) {
            OneSignal.addSubscriptionObserver(OneSignalObserverController.subscriptionObserver = (OSSubscriptionObserver)new OSSubscriptionObserver() {
                public void onOSSubscriptionChanged(final OSSubscriptionStateChanges osSubscriptionStateChanges) {
                    callbackSuccess(OneSignalObserverController.jsSubscriptionObserverCallBack, osSubscriptionStateChanges.toJSONObject());
                }
            });
        }
        return true;
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
    
    private static JSONObject renameStateChangesKey(final JSONObject jsonObject, final JSONObject jsonObject2, final String s, final String s2) {
        final JSONObject jsonObject3 = new JSONObject();
        try {
            jsonObject.put(s2, jsonObject.getBoolean(s));
            jsonObject.remove(s);
            jsonObject2.put(s2, jsonObject2.getBoolean(s));
            jsonObject2.remove(s);
            jsonObject3.put("from", (Object)jsonObject);
            jsonObject3.put("to", (Object)jsonObject2);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject3;
    }
}
