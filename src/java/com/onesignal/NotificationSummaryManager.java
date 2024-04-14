package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.database.Cursor;
import android.app.NotificationManager;
import android.content.Context;

class NotificationSummaryManager
{
    static void clearNotificationOnSummaryClick(final Context context, final OneSignalDbHelper oneSignalDbHelper, final String s) {
        final Integer summaryNotificationId = getSummaryNotificationId((OneSignalDb)oneSignalDbHelper, s);
        final boolean equals = s.equals((Object)OneSignalNotificationManager.getGrouplessSummaryKey());
        final NotificationManager notificationManager = OneSignalNotificationManager.getNotificationManager(context);
        final Integer mostRecentNotifIdFromGroup = OneSignalNotificationManager.getMostRecentNotifIdFromGroup(oneSignalDbHelper, s, equals);
        if (mostRecentNotifIdFromGroup != null) {
            if (OneSignal.getClearGroupSummaryClick()) {
                Integer value = summaryNotificationId;
                if (equals) {
                    value = OneSignalNotificationManager.getGrouplessSummaryId();
                }
                if (value != null) {
                    notificationManager.cancel((int)value);
                }
            }
            else {
                OneSignal.removeNotification(mostRecentNotifIdFromGroup);
            }
        }
    }
    
    static Integer getSummaryNotificationId(final OneSignalDb oneSignalDb, final String s) {
        Object value = null;
        Integer n = null;
        try {
            final Cursor query = oneSignalDb.query("notification", new String[] { "android_notification_id" }, "group_id = ? AND dismissed = 0 AND opened = 0 AND is_summary = 1", new String[] { s }, null, null, null);
            try {
                if (!query.moveToFirst()) {
                    query.close();
                    if (query != null && !query.isClosed()) {
                        query.close();
                    }
                    return null;
                }
                value = query.getInt(query.getColumnIndex("android_notification_id"));
                query.close();
                if (query == null || query.isClosed()) {
                    return n;
                }
                query.close();
            }
            finally {
                value = query;
            }
        }
        finally {
            n = null;
        }
        try {
            final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
            final StringBuilder sb = new StringBuilder("Error getting android notification id for summary notification group: ");
            sb.append(s);
            final Throwable t;
            OneSignal.Log(error, sb.toString(), t);
            if (value != null && !((Cursor)value).isClosed()) {
                ((Cursor)value).close();
            }
            return n;
        }
        finally {
            if (value != null && !((Cursor)value).isClosed()) {
                ((Cursor)value).close();
            }
        }
    }
    
    private static Cursor internalUpdateSummaryNotificationAfterChildRemoved(final Context context, final OneSignalDb oneSignalDb, final String s, final boolean b) {
        final Cursor query = oneSignalDb.query("notification", new String[] { "android_notification_id", "created_time", "full_data" }, "group_id = ? AND dismissed = 0 AND opened = 0 AND is_summary = 0", new String[] { s }, null, null, "_id DESC");
        final int count = query.getCount();
        if (count == 0 && !s.equals((Object)OneSignalNotificationManager.getGrouplessSummaryKey())) {
            query.close();
            final Integer summaryNotificationId = getSummaryNotificationId(oneSignalDb, s);
            if (summaryNotificationId == null) {
                return query;
            }
            OneSignalNotificationManager.getNotificationManager(context).cancel((int)summaryNotificationId);
            final ContentValues contentValues = new ContentValues();
            String s2;
            if (b) {
                s2 = "dismissed";
            }
            else {
                s2 = "opened";
            }
            contentValues.put(s2, Integer.valueOf(1));
            final StringBuilder sb = new StringBuilder("android_notification_id = ");
            sb.append((Object)summaryNotificationId);
            oneSignalDb.update("notification", contentValues, sb.toString(), null);
            return query;
        }
        else {
            if (count != 1) {
                try {
                    query.moveToFirst();
                    final long long1 = query.getLong(query.getColumnIndex("created_time"));
                    final String string = query.getString(query.getColumnIndex("full_data"));
                    query.close();
                    if (getSummaryNotificationId(oneSignalDb, s) == null) {
                        return query;
                    }
                    final OSNotificationGenerationJob osNotificationGenerationJob = new OSNotificationGenerationJob(context);
                    osNotificationGenerationJob.setRestoring(true);
                    osNotificationGenerationJob.setShownTimeStamp(long1);
                    osNotificationGenerationJob.setJsonPayload(new JSONObject(string));
                    GenerateNotification.updateSummaryNotification(osNotificationGenerationJob);
                }
                catch (final JSONException ex) {
                    ex.printStackTrace();
                }
                return query;
            }
            query.close();
            if (getSummaryNotificationId(oneSignalDb, s) == null) {
                return query;
            }
            restoreSummary(context, s);
            return query;
        }
    }
    
    private static void restoreSummary(final Context context, String s) {
        final OneSignalDbHelper instance = OneSignalDbHelper.getInstance(context);
        Cursor query = null;
        Label_0079: {
            try {
                s = (String)(query = instance.query("notification", OSNotificationRestoreWorkManager.COLUMNS_FOR_RESTORE, "group_id = ? AND dismissed = 0 AND opened = 0 AND is_summary = 0", new String[] { s }, (String)null, (String)null, (String)null));
                OSNotificationRestoreWorkManager.showNotificationsFromCursor(context, (Cursor)s, 0);
                if (s != null && !((Cursor)s).isClosed()) {
                    break Label_0079;
                }
            }
            finally {
                try {
                    final Throwable t;
                    OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error restoring notification records! ", t);
                    if (query != null && !query.isClosed()) {
                        s = (String)query;
                        ((Cursor)s).close();
                    }
                }
                finally {
                    if (query != null && !query.isClosed()) {
                        query.close();
                    }
                }
            }
        }
    }
    
    static void updatePossibleDependentSummaryOnDismiss(final Context context, final OneSignalDb oneSignalDb, final int n) {
        final StringBuilder sb = new StringBuilder("android_notification_id = ");
        sb.append(n);
        final Cursor query = oneSignalDb.query("notification", new String[] { "group_id" }, sb.toString(), null, null, null, null);
        if (query.moveToFirst()) {
            final String string = query.getString(query.getColumnIndex("group_id"));
            query.close();
            if (string != null) {
                updateSummaryNotificationAfterChildRemoved(context, oneSignalDb, string, true);
            }
        }
        else {
            query.close();
        }
    }
    
    static void updateSummaryNotificationAfterChildRemoved(final Context context, final OneSignalDb oneSignalDb, final String s, final boolean b) {
        try {
            final Cursor internalUpdateSummaryNotificationAfterChildRemoved = internalUpdateSummaryNotificationAfterChildRemoved(context, oneSignalDb, s, b);
            if (internalUpdateSummaryNotificationAfterChildRemoved != null && !internalUpdateSummaryNotificationAfterChildRemoved.isClosed()) {
                internalUpdateSummaryNotificationAfterChildRemoved.close();
            }
        }
        finally {
            final Throwable t;
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error running updateSummaryNotificationAfterChildRemoved!", t);
        }
    }
}
