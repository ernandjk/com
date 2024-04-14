package com.onesignal;

import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.JSONArray;
import android.os.Build$VERSION;
import org.json.JSONException;
import android.app.NotificationChannel;
import android.net.Uri;
import android.media.AudioAttributes;
import java.math.BigInteger;
import androidx.activity.ComponentDialog$$ExternalSyntheticApiModelOutline0;
import com.onesignal.language.LanguageContext;
import org.json.JSONObject;
import android.app.NotificationManager;
import android.content.Context;
import java.util.regex.Pattern;

class NotificationChannelManager
{
    private static final String CHANNEL_PREFIX = "OS_";
    private static final String DEFAULT_CHANNEL_ID = "fcm_fallback_notification_channel";
    private static final String RESTORE_CHANNEL_ID = "restored_OS_notifications";
    private static final Pattern hexPattern;
    
    static {
        hexPattern = Pattern.compile("^([A-Fa-f0-9]{8})$");
    }
    
    private static String createChannel(final Context context, final NotificationManager notificationManager, final JSONObject jsonObject) throws JSONException {
        final Object opt = jsonObject.opt("chnl");
        JSONObject jsonObject2;
        if (opt instanceof String) {
            jsonObject2 = new JSONObject((String)opt);
        }
        else {
            jsonObject2 = (JSONObject)opt;
        }
        String s = "fcm_fallback_notification_channel";
        final String optString = jsonObject2.optString("id", "fcm_fallback_notification_channel");
        if (!optString.equals((Object)"miscellaneous")) {
            s = optString;
        }
        JSONObject optJSONObject = null;
        Label_0125: {
            if (jsonObject2.has("langs")) {
                final JSONObject jsonObject3 = jsonObject2.getJSONObject("langs");
                final String language = LanguageContext.getInstance().getLanguage();
                if (jsonObject3.has(language)) {
                    optJSONObject = jsonObject3.optJSONObject(language);
                    break Label_0125;
                }
            }
            optJSONObject = jsonObject2;
        }
        final NotificationChannel m = ComponentDialog$$ExternalSyntheticApiModelOutline0.m(s, (CharSequence)optJSONObject.optString("nm", "Miscellaneous"), priorityToImportance(jsonObject.optInt("pri", 6)));
        OneSignal$$ExternalSyntheticApiModelOutline0.m(m, optJSONObject.optString("dscr", (String)null));
        if (jsonObject2.has("grp_id")) {
            final String optString2 = jsonObject2.optString("grp_id");
            OneSignal$$ExternalSyntheticApiModelOutline0.m(notificationManager, OneSignal$$ExternalSyntheticApiModelOutline0.m(optString2, (CharSequence)optJSONObject.optString("grp_nm")));
            OneSignal$$ExternalSyntheticApiModelOutline0.m$1(m, optString2);
        }
        if (jsonObject.has("ledc")) {
            String optString3;
            if (!NotificationChannelManager.hexPattern.matcher((CharSequence)(optString3 = jsonObject.optString("ledc"))).matches()) {
                OneSignal.Log(OneSignal.LOG_LEVEL.WARN, "OneSignal LED Color Settings: ARGB Hex value incorrect format (E.g: FF9900FF)");
                optString3 = "FFFFFFFF";
            }
            try {
                OneSignal$$ExternalSyntheticApiModelOutline0.m(m, new BigInteger(optString3, 16).intValue());
            }
            finally {
                final Throwable t;
                OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Couldn't convert ARGB Hex value to BigInteger:", t);
            }
        }
        final boolean b = true;
        OneSignal$$ExternalSyntheticApiModelOutline0.m$2(m, jsonObject.optInt("led", 1) == 1);
        if (jsonObject.has("vib_pt")) {
            final long[] vibrationPattern = OSUtils.parseVibrationPattern(jsonObject);
            if (vibrationPattern != null) {
                OneSignal$$ExternalSyntheticApiModelOutline0.m(m, vibrationPattern);
            }
        }
        OneSignal$$ExternalSyntheticApiModelOutline0.m$3(m, jsonObject.optInt("vib", 1) == 1);
        if (jsonObject.has("sound")) {
            final String optString4 = jsonObject.optString("sound", (String)null);
            final Uri soundUri = OSUtils.getSoundUri(context, optString4);
            if (soundUri != null) {
                OneSignal$$ExternalSyntheticApiModelOutline0.m(m, soundUri, (AudioAttributes)null);
            }
            else if ("null".equals((Object)optString4) || "nil".equals((Object)optString4)) {
                OneSignal$$ExternalSyntheticApiModelOutline0.m(m, (Uri)null, (AudioAttributes)null);
            }
        }
        OneSignal$$ExternalSyntheticApiModelOutline0.m$1(m, jsonObject.optInt("vis", 0));
        OneSignal$$ExternalSyntheticApiModelOutline0.m(m, jsonObject.optInt("bdg", 1) == 1);
        OneSignal$$ExternalSyntheticApiModelOutline0.m$1(m, jsonObject.optInt("bdnd", 0) == 1 && b);
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("Creating notification channel with channel:\n");
        sb.append(OneSignal$$ExternalSyntheticApiModelOutline0.m$1(m));
        OneSignal.onesignalLog(verbose, sb.toString());
        try {
            ComponentDialog$$ExternalSyntheticApiModelOutline0.m(notificationManager, m);
        }
        catch (final IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return s;
    }
    
    private static String createDefaultChannel(final NotificationManager notificationManager) {
        final NotificationChannel m = ComponentDialog$$ExternalSyntheticApiModelOutline0.m("fcm_fallback_notification_channel", (CharSequence)"Miscellaneous", 3);
        OneSignal$$ExternalSyntheticApiModelOutline0.m$2(m, true);
        OneSignal$$ExternalSyntheticApiModelOutline0.m$3(m, true);
        ComponentDialog$$ExternalSyntheticApiModelOutline0.m(notificationManager, m);
        return "fcm_fallback_notification_channel";
    }
    
    static String createNotificationChannel(final OSNotificationGenerationJob osNotificationGenerationJob) {
        if (Build$VERSION.SDK_INT < 26) {
            return "fcm_fallback_notification_channel";
        }
        final Context context = osNotificationGenerationJob.getContext();
        final JSONObject jsonPayload = osNotificationGenerationJob.getJsonPayload();
        final NotificationManager notificationManager = OneSignalNotificationManager.getNotificationManager(context);
        if (osNotificationGenerationJob.isRestoring()) {
            return createRestoreChannel(notificationManager);
        }
        if (jsonPayload.has("oth_chnl")) {
            final String optString = jsonPayload.optString("oth_chnl");
            if (ComponentDialog$$ExternalSyntheticApiModelOutline0.m(notificationManager, optString) != null) {
                return optString;
            }
        }
        if (!jsonPayload.has("chnl")) {
            return createDefaultChannel(notificationManager);
        }
        try {
            return createChannel(context, notificationManager, jsonPayload);
        }
        catch (final JSONException ex) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Could not create notification channel due to JSON payload error!", (Throwable)ex);
            return "fcm_fallback_notification_channel";
        }
    }
    
    private static String createRestoreChannel(final NotificationManager notificationManager) {
        ComponentDialog$$ExternalSyntheticApiModelOutline0.m(notificationManager, ComponentDialog$$ExternalSyntheticApiModelOutline0.m("restored_OS_notifications", (CharSequence)"Restored", 2));
        return "restored_OS_notifications";
    }
    
    private static int priorityToImportance(final int n) {
        if (n > 9) {
            return 5;
        }
        if (n > 7) {
            return 4;
        }
        if (n > 5) {
            return 3;
        }
        if (n > 3) {
            return 2;
        }
        if (n > 1) {
            return 1;
        }
        return 0;
    }
    
    static void processChannelList(Context m, final JSONArray jsonArray) {
        if (Build$VERSION.SDK_INT < 26) {
            return;
        }
        if (jsonArray != null) {
            if (jsonArray.length() != 0) {
                final NotificationManager notificationManager = OneSignalNotificationManager.getNotificationManager(m);
                final HashSet set = new HashSet();
                for (int length = jsonArray.length(), i = 0; i < length; ++i) {
                    try {
                        ((Set)set).add((Object)createChannel(m, notificationManager, jsonArray.getJSONObject(i)));
                    }
                    catch (final JSONException ex) {
                        OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Could not create notification channel due to JSON payload error!", (Throwable)ex);
                    }
                }
                if (((Set)set).isEmpty()) {
                    return;
                }
                m = (Context)new ArrayList();
                try {
                    m = (Context)OneSignal$$ExternalSyntheticApiModelOutline0.m(notificationManager);
                }
                catch (final NullPointerException ex2) {
                    final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                    final StringBuilder sb = new StringBuilder("Error when trying to delete notification channel: ");
                    sb.append(ex2.getMessage());
                    OneSignal.onesignalLog(error, sb.toString());
                }
                final Iterator iterator = ((List)m).iterator();
                while (iterator.hasNext()) {
                    final String j = OneSignal$$ExternalSyntheticApiModelOutline0.m(ComponentDialog$$ExternalSyntheticApiModelOutline0.m(iterator.next()));
                    if (j.startsWith("OS_") && !((Set)set).contains((Object)j)) {
                        OneSignal$$ExternalSyntheticApiModelOutline0.m(notificationManager, j);
                    }
                }
            }
        }
    }
}
