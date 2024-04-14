package com.onesignal;

import java.util.SortedMap;
import android.database.Cursor;
import java.util.Iterator;
import android.service.notification.StatusBarNotification;
import java.util.Map$Entry;
import java.util.TreeMap;
import android.os.Build$VERSION;
import android.content.Context;

class NotificationLimitManager
{
    private static final int MAX_NUMBER_OF_NOTIFICATIONS_INT = 49;
    static final String MAX_NUMBER_OF_NOTIFICATIONS_STR;
    
    static {
        MAX_NUMBER_OF_NOTIFICATIONS_STR = Integer.toString(49);
    }
    
    static void clearOldestOverLimit(final Context context, final int n) {
        try {
            if (Build$VERSION.SDK_INT >= 23) {
                clearOldestOverLimitStandard(context, n);
            }
        }
        finally {
            clearOldestOverLimitFallback(context, n);
        }
    }
    
    static void clearOldestOverLimitFallback(Context query, int n) {
        final OneSignalDbHelper instance = OneSignalDbHelper.getInstance(query);
        final Context context = query = null;
        try {
            final String string = OneSignalDbHelper.recentUninteractedWithNotificationsWhere().toString();
            query = context;
            query = context;
            final StringBuilder sb = new StringBuilder();
            query = context;
            sb.append(getMaxNumberOfNotificationsString());
            query = context;
            sb.append(n);
            query = context;
            final String string2 = sb.toString();
            query = context;
            final Object o = query = (Context)instance.query("notification", new String[] { "android_notification_id" }, string, (String[])null, (String)null, (String)null, "_id", string2);
            final int count = ((Cursor)o).getCount();
            query = (Context)o;
            if ((n += count - getMaxNumberOfNotificationsInt()) < 1) {
                if (o != null && !((Cursor)o).isClosed()) {
                    ((Cursor)o).close();
                }
                return;
            }
            do {
                query = (Context)o;
                if (!((Cursor)o).moveToNext()) {
                    break;
                }
                query = (Context)o;
                OneSignal.removeNotification(((Cursor)o).getInt(((Cursor)o).getColumnIndex("android_notification_id")));
            } while (--n > 0);
            if (o != null && !((Cursor)o).isClosed()) {
                query = (Context)o;
            }
        }
        finally {
            try {
                final Throwable t;
                OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error clearing oldest notifications over limit! ", t);
            }
            finally {
                if (query != null && !((Cursor)query).isClosed()) {
                    ((Cursor)query).close();
                }
            }
        }
    }
    
    static void clearOldestOverLimitStandard(final Context context, int i) throws Throwable {
        final StatusBarNotification[] activeNotifications = OneSignalNotificationManager.getActiveNotifications(context);
        final int n = activeNotifications.length - getMaxNumberOfNotificationsInt() + i;
        if (n < 1) {
            return;
        }
        final TreeMap treeMap = new TreeMap();
        int length;
        StatusBarNotification statusBarNotification;
        for (length = activeNotifications.length, i = 0; i < length; ++i) {
            statusBarNotification = activeNotifications[i];
            if (!isGroupSummary(statusBarNotification)) {
                ((SortedMap)treeMap).put((Object)statusBarNotification.getNotification().when, (Object)statusBarNotification.getId());
            }
        }
        final Iterator iterator = ((SortedMap)treeMap).entrySet().iterator();
        i = n;
        while (iterator.hasNext()) {
            OneSignal.removeNotification((int)((Map$Entry)iterator.next()).getValue());
            if (--i <= 0) {
                break;
            }
        }
    }
    
    private static int getMaxNumberOfNotificationsInt() {
        return 49;
    }
    
    private static String getMaxNumberOfNotificationsString() {
        return NotificationLimitManager.MAX_NUMBER_OF_NOTIFICATIONS_STR;
    }
    
    static boolean isGroupSummary(final StatusBarNotification statusBarNotification) {
        return (statusBarNotification.getNotification().flags & 0x200) != 0x0;
    }
}
