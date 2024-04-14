package com.onesignal;

import android.database.Cursor;
import android.app.Activity;
import android.content.ContentValues;
import androidx.core.app.NotificationManagerCompat;
import android.content.Intent;
import android.os.Build$VERSION;
import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

class NotificationOpenedProcessor
{
    private static final String TAG = "com.onesignal.NotificationOpenedProcessor";
    
    private static void addChildNotifications(final JSONArray jsonArray, final String s, OneSignalDbHelper query) {
        query = (OneSignalDbHelper)query.query("notification", new String[] { "full_data" }, "group_id = ? AND dismissed = 0 AND opened = 0 AND is_summary = 0", new String[] { s }, (String)null, (String)null, (String)null);
        if (((Cursor)query).getCount() > 1) {
            ((Cursor)query).moveToFirst();
            do {
                try {
                    jsonArray.put((Object)new JSONObject(((Cursor)query).getString(((Cursor)query).getColumnIndex("full_data"))));
                }
                catch (final JSONException ex) {
                    final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                    final StringBuilder sb = new StringBuilder("Could not parse JSON of sub notification in group: ");
                    sb.append(s);
                    OneSignal.Log(error, sb.toString());
                }
            } while (((Cursor)query).moveToNext());
        }
        ((Cursor)query).close();
    }
    
    private static void clearStatusBarNotifications(final Context context, final OneSignalDbHelper oneSignalDbHelper, final String s) {
        if (s != null) {
            NotificationSummaryManager.clearNotificationOnSummaryClick(context, oneSignalDbHelper, s);
        }
        else if (Build$VERSION.SDK_INT >= 23 && OneSignalNotificationManager.getGrouplessNotifsCount(context) < 1) {
            OneSignalNotificationManager.getNotificationManager(context).cancel(OneSignalNotificationManager.getGrouplessSummaryId());
        }
    }
    
    private static void handleDismissFromActionButtonPress(final Context context, final Intent intent) {
        if (intent.getBooleanExtra("action_button", false)) {
            NotificationManagerCompat.from(context).cancel(intent.getIntExtra("androidNotificationId", 0));
            if (Build$VERSION.SDK_INT < 31) {
                context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
            }
        }
    }
    
    private static boolean isOneSignalIntent(final Intent intent) {
        return intent.hasExtra("onesignalData") || intent.hasExtra("summary") || intent.hasExtra("androidNotificationId");
    }
    
    private static void markNotificationsConsumed(final Context context, final Intent intent, final OneSignalDbHelper oneSignalDbHelper, final boolean b) {
        final String stringExtra = intent.getStringExtra("summary");
        String[] array = null;
        String[] array2 = null;
        String s2;
        if (stringExtra != null) {
            final boolean equals = stringExtra.equals((Object)OneSignalNotificationManager.getGrouplessSummaryKey());
            String s;
            if (equals) {
                s = "group_id IS NULL";
            }
            else {
                array2 = new String[] { stringExtra };
                s = "group_id = ?";
            }
            array = array2;
            s2 = s;
            if (!b) {
                array = array2;
                s2 = s;
                if (!OneSignal.getClearGroupSummaryClick()) {
                    final String value = String.valueOf((Object)OneSignalNotificationManager.getMostRecentNotifIdFromGroup(oneSignalDbHelper, stringExtra, equals));
                    s2 = s.concat(" AND android_notification_id = ?");
                    String[] array3;
                    if (equals) {
                        array3 = new String[] { value };
                    }
                    else {
                        array3 = new String[] { stringExtra, value };
                    }
                    array = array3;
                }
            }
        }
        else {
            final StringBuilder sb = new StringBuilder("android_notification_id = ");
            sb.append(intent.getIntExtra("androidNotificationId", 0));
            s2 = sb.toString();
        }
        clearStatusBarNotifications(context, oneSignalDbHelper, stringExtra);
        oneSignalDbHelper.update("notification", newContentValuesWithConsumed(intent), s2, array);
        BadgeCountUpdater.update((OneSignalDb)oneSignalDbHelper, context);
    }
    
    private static ContentValues newContentValuesWithConsumed(final Intent intent) {
        final ContentValues contentValues = new ContentValues();
        final boolean booleanExtra = intent.getBooleanExtra("dismissed", false);
        final Integer value = 1;
        if (booleanExtra) {
            contentValues.put("dismissed", value);
        }
        else {
            contentValues.put("opened", value);
        }
        return contentValues;
    }
    
    static void processFromContext(final Context context, final Intent intent) {
        if (!isOneSignalIntent(intent)) {
            return;
        }
        if (context != null) {
            OneSignal.initWithContext(context.getApplicationContext());
        }
        handleDismissFromActionButtonPress(context, intent);
        processIntent(context, intent);
    }
    
    static void processIntent(final Context context, final Intent intent) {
        final OneSignalDbHelper instance = OneSignalDbHelper.getInstance(context);
        final String stringExtra = intent.getStringExtra("summary");
        final boolean booleanExtra = intent.getBooleanExtra("dismissed", false);
        OSNotificationIntentExtras processToOpenIntent;
        if (!booleanExtra) {
            if ((processToOpenIntent = processToOpenIntent(context, intent, instance, stringExtra)) == null) {
                return;
            }
        }
        else {
            processToOpenIntent = null;
        }
        markNotificationsConsumed(context, intent, instance, booleanExtra);
        if (stringExtra == null) {
            final String stringExtra2 = intent.getStringExtra("grp");
            if (stringExtra2 != null) {
                NotificationSummaryManager.updateSummaryNotificationAfterChildRemoved(context, (OneSignalDb)instance, stringExtra2, booleanExtra);
            }
        }
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("processIntent from context: ");
        sb.append((Object)context);
        sb.append(" and intent: ");
        sb.append((Object)intent);
        OneSignal.onesignalLog(debug, sb.toString());
        if (intent.getExtras() != null) {
            final OneSignal.LOG_LEVEL debug2 = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb2 = new StringBuilder("processIntent intent extras: ");
            sb2.append(intent.getExtras().toString());
            OneSignal.onesignalLog(debug2, sb2.toString());
        }
        if (!booleanExtra) {
            if (!(context instanceof Activity)) {
                final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                final StringBuilder sb3 = new StringBuilder("NotificationOpenedProcessor processIntent from an non Activity context: ");
                sb3.append((Object)context);
                OneSignal.onesignalLog(error, sb3.toString());
            }
            else {
                OneSignal.handleNotificationOpen((Activity)context, processToOpenIntent.getDataArray(), OSNotificationFormatHelper.getOSNotificationIdFromJson(processToOpenIntent.getJsonData()));
            }
        }
    }
    
    static OSNotificationIntentExtras processToOpenIntent(final Context context, final Intent intent, final OneSignalDbHelper oneSignalDbHelper, final String s) {
        final JSONArray jsonArray = null;
        JSONObject jsonObject2;
        try {
            final JSONObject jsonObject = new JSONObject(intent.getStringExtra("onesignalData"));
            try {
                if (!(context instanceof Activity)) {
                    final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
                    final StringBuilder sb = new StringBuilder("NotificationOpenedProcessor processIntent from an non Activity context: ");
                    sb.append((Object)context);
                    OneSignal.onesignalLog(error, sb.toString());
                }
                else if (OSInAppMessagePreviewHandler.notificationOpened((Activity)context, jsonObject)) {
                    return null;
                }
                jsonObject.put("androidNotificationId", intent.getIntExtra("androidNotificationId", 0));
                intent.putExtra("onesignalData", jsonObject.toString());
                final JSONArray jsonArray2 = NotificationBundleProcessor.newJsonArray(new JSONObject(intent.getStringExtra("onesignalData")));
            }
            catch (final JSONException ex) {
                jsonObject2 = jsonObject;
            }
        }
        catch (final JSONException ex) {
            jsonObject2 = null;
        }
        final JSONException ex;
        ex.printStackTrace();
        final JSONObject jsonObject = jsonObject2;
        final JSONArray jsonArray2 = jsonArray;
        if (s != null) {
            addChildNotifications(jsonArray2, s, oneSignalDbHelper);
        }
        return new OSNotificationIntentExtras(jsonArray2, jsonObject);
    }
}
