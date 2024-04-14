package com.onesignal;

import org.json.JSONException;
import android.os.Bundle;
import android.content.Context;
import org.json.JSONArray;
import android.app.Activity;
import org.json.JSONObject;
import android.content.Intent;

class NotificationPayloadProcessorHMS
{
    private static JSONObject covertHMSOpenIntentToJson(final Intent intent) {
        if (!OSNotificationFormatHelper.isOneSignalIntent(intent)) {
            return null;
        }
        final JSONObject bundleAsJSONObject = NotificationBundleProcessor.bundleAsJSONObject(intent.getExtras());
        reformatButtonClickAction(bundleAsJSONObject);
        return bundleAsJSONObject;
    }
    
    static void handleHMSNotificationOpenIntent(final Activity activity, final Intent intent) {
        OneSignal.initWithContext(activity.getApplicationContext());
        if (intent == null) {
            return;
        }
        final JSONObject covertHMSOpenIntentToJson = covertHMSOpenIntentToJson(intent);
        if (covertHMSOpenIntentToJson == null) {
            return;
        }
        handleProcessJsonOpenData(activity, covertHMSOpenIntentToJson);
    }
    
    private static void handleProcessJsonOpenData(final Activity activity, final JSONObject jsonObject) {
        if (OSInAppMessagePreviewHandler.notificationOpened(activity, jsonObject)) {
            return;
        }
        OneSignal.handleNotificationOpen(activity, new JSONArray().put((Object)jsonObject), OSNotificationFormatHelper.getOSNotificationIdFromJson(jsonObject));
    }
    
    public static void processDataMessageReceived(final Context context, final String s) {
        OneSignal.initWithContext(context);
        if (s == null) {
            return;
        }
        final Bundle jsonStringToBundle = OSUtils.jsonStringToBundle(s);
        if (jsonStringToBundle == null) {
            return;
        }
        NotificationBundleProcessor.processBundleFromReceiver(context, jsonStringToBundle, (NotificationBundleProcessor.ProcessBundleReceiverCallback)new NotificationPayloadProcessorHMS$1(context, jsonStringToBundle));
    }
    
    private static void reformatButtonClickAction(final JSONObject jsonObject) {
        try {
            final String s = (String)NotificationBundleProcessor.getCustomJSONObject(jsonObject).remove("actionId");
            if (s == null) {
                return;
            }
            jsonObject.put("actionId", (Object)s);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
    }
}
