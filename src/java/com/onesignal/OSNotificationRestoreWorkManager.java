package com.onesignal;

import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import java.util.ArrayList;
import android.os.Build$VERSION;
import android.database.Cursor;
import java.util.concurrent.TimeUnit;
import androidx.work.OneTimeWorkRequest$Builder;
import androidx.work.OneTimeWorkRequest;
import androidx.work.ExistingWorkPolicy;
import android.content.Context;

class OSNotificationRestoreWorkManager
{
    static final String[] COLUMNS_FOR_RESTORE;
    static final int DEFAULT_TTL_IF_NOT_IN_PAYLOAD = 259200;
    private static final int DELAY_BETWEEN_NOTIFICATION_RESTORES_MS = 200;
    private static final String NOTIFICATION_RESTORE_WORKER_IDENTIFIER;
    public static boolean restored;
    
    static {
        COLUMNS_FOR_RESTORE = new String[] { "notification_id", "android_notification_id", "full_data", "created_time" };
        NOTIFICATION_RESTORE_WORKER_IDENTIFIER = OSNotificationRestoreWorkManager.OSNotificationRestoreWorkManager$NotificationRestoreWorker.class.getCanonicalName();
    }
    
    public static void beginEnqueueingWork(final Context context, final boolean b) {
        int n;
        if (b) {
            n = 15;
        }
        else {
            n = 0;
        }
        OSWorkManagerHelper.getInstance(context).enqueueUniqueWork(OSNotificationRestoreWorkManager.NOTIFICATION_RESTORE_WORKER_IDENTIFIER, ExistingWorkPolicy.KEEP, (OneTimeWorkRequest)((OneTimeWorkRequest$Builder)new OneTimeWorkRequest$Builder((Class)OSNotificationRestoreWorkManager.OSNotificationRestoreWorkManager$NotificationRestoreWorker.class).setInitialDelay((long)n, TimeUnit.SECONDS)).build());
    }
    
    private static void queryAndRestoreNotificationsAndBadgeCount(final Context context, final OneSignalDbHelper oneSignalDbHelper, StringBuilder sb) {
        final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
        final StringBuilder sb2 = new StringBuilder("Querying DB for notifications to restore: ");
        sb2.append(sb.toString());
        OneSignal.Log(info, sb2.toString());
        Object query = null;
        Label_0116: {
            try {
                sb = (StringBuilder)(query = oneSignalDbHelper.query("notification", OSNotificationRestoreWorkManager.COLUMNS_FOR_RESTORE, sb.toString(), (String[])null, (String)null, (String)null, "_id DESC", NotificationLimitManager.MAX_NUMBER_OF_NOTIFICATIONS_STR));
                showNotificationsFromCursor(context, (Cursor)sb, 200);
                query = sb;
                BadgeCountUpdater.update((OneSignalDb)oneSignalDbHelper, context);
                if (sb != null && !((Cursor)sb).isClosed()) {
                    break Label_0116;
                }
            }
            finally {
                try {
                    final Throwable t;
                    OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error restoring notification records! ", t);
                    if (query != null && !((Cursor)query).isClosed()) {
                        sb = (StringBuilder)query;
                        ((Cursor)sb).close();
                    }
                }
                finally {
                    if (query != null && !((Cursor)query).isClosed()) {
                        ((Cursor)query).close();
                    }
                }
            }
        }
    }
    
    static void showNotificationsFromCursor(final Context context, final Cursor cursor, final int n) {
        if (!cursor.moveToFirst()) {
            return;
        }
        do {
            OSNotificationWorkManager.beginEnqueueingWork(context, cursor.getString(cursor.getColumnIndex("notification_id")), cursor.getInt(cursor.getColumnIndex("android_notification_id")), cursor.getString(cursor.getColumnIndex("full_data")), cursor.getLong(cursor.getColumnIndex("created_time")), true, false);
            if (n > 0) {
                OSUtils.sleep(n);
            }
        } while (cursor.moveToNext());
    }
    
    private static void skipVisibleNotifications(final Context context, final StringBuilder sb) {
        if (Build$VERSION.SDK_INT < 23) {
            return;
        }
        final StatusBarNotification[] activeNotifications = OneSignalNotificationManager.getActiveNotifications(context);
        if (activeNotifications.length == 0) {
            return;
        }
        final ArrayList list = new ArrayList();
        for (int length = activeNotifications.length, i = 0; i < length; ++i) {
            list.add((Object)activeNotifications[i].getId());
        }
        sb.append(" AND android_notification_id NOT IN (");
        sb.append(TextUtils.join((CharSequence)",", (Iterable)list));
        sb.append(")");
    }
}
