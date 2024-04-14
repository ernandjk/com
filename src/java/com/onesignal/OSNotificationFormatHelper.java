package com.onesignal;

import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;

class OSNotificationFormatHelper
{
    public static final String PAYLOAD_OS_NOTIFICATION_ID = "i";
    public static final String PAYLOAD_OS_ROOT_CUSTOM = "custom";
    
    private static String getOSNotificationIdFromBundle(final Bundle bundle) {
        if (bundle != null) {
            if (!bundle.isEmpty()) {
                final String string = bundle.getString("custom", (String)null);
                if (string != null) {
                    return getOSNotificationIdFromJsonString(string);
                }
                OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "Not a OneSignal formatted Bundle. No 'custom' field in the bundle.");
            }
        }
        return null;
    }
    
    static String getOSNotificationIdFromJson(final JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return getOSNotificationIdFromJsonString(jsonObject.optString("custom", (String)null));
    }
    
    private static String getOSNotificationIdFromJsonString(final String s) {
        try {
            final JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.has("i")) {
                return jsonObject.optString("i", (String)null);
            }
            OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "Not a OneSignal formatted JSON string. No 'i' field in custom.");
        }
        catch (final JSONException ex) {
            OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "Not a OneSignal formatted JSON String, error parsing string as JSON.");
        }
        return null;
    }
    
    static boolean isOneSignalBundle(final Bundle bundle) {
        return getOSNotificationIdFromBundle(bundle) != null;
    }
    
    static boolean isOneSignalIntent(final Intent intent) {
        return intent != null && isOneSignalBundle(intent.getExtras());
    }
}
