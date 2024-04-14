package com.onesignal;

import android.app.NotificationManager;
import android.database.Cursor;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import java.util.Iterator;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import android.app.NotificationChannel;
import androidx.activity.ComponentDialog$$ExternalSyntheticApiModelOutline0;
import android.os.Build$VERSION;
import androidx.core.app.NotificationManagerCompat;
import android.content.Context;

public class OneSignalNotificationManager
{
    private static final int GROUPLESS_SUMMARY_ID = -718463522;
    private static final String GROUPLESS_SUMMARY_KEY = "os_group_undefined";
    
    public static boolean areNotificationsEnabled(final Context context, final String s) {
        final boolean notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled();
        boolean b = false;
        if (!notificationsEnabled) {
            return false;
        }
        if (Build$VERSION.SDK_INT >= 26) {
            final NotificationChannel m = ComponentDialog$$ExternalSyntheticApiModelOutline0.m(getNotificationManager(context), s);
            if (m == null || ComponentDialog$$ExternalSyntheticApiModelOutline0.m(m) != 0) {
                b = true;
            }
            return b;
        }
        return true;
    }
    
    static void assignGrouplessNotifications(final Context context, final ArrayList<StatusBarNotification> list) {
        for (final StatusBarNotification statusBarNotification : list) {
            NotificationManagerCompat.from(context).notify(statusBarNotification.getId(), ComponentDialog$$ExternalSyntheticApiModelOutline0.m(context, statusBarNotification.getNotification()).setGroup("os_group_undefined").setOnlyAlertOnce(true).build());
        }
    }
    
    static ArrayList<StatusBarNotification> getActiveGrouplessNotifications(final Context context) {
        final ArrayList list = new ArrayList();
        for (final StatusBarNotification statusBarNotification : getActiveNotifications(context)) {
            final Notification notification = statusBarNotification.getNotification();
            final boolean groupSummary = NotificationLimitManager.isGroupSummary(statusBarNotification);
            final boolean b = notification.getGroup() == null || notification.getGroup().equals((Object)getGrouplessSummaryKey());
            if (!groupSummary && b) {
                list.add((Object)statusBarNotification);
            }
        }
        return (ArrayList<StatusBarNotification>)list;
    }
    
    static StatusBarNotification[] getActiveNotifications(final Context context) {
        final StatusBarNotification[] array = new StatusBarNotification[0];
        try {
            return ComponentDialog$$ExternalSyntheticApiModelOutline0.m(getNotificationManager(context));
        }
        finally {
            return array;
        }
    }
    
    static Integer getGrouplessNotifsCount(final Context context) {
        final StatusBarNotification[] activeNotifications = getActiveNotifications(context);
        final int length = activeNotifications.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            final StatusBarNotification statusBarNotification = activeNotifications[i];
            int n2 = n;
            if (!NotificationCompat.isGroupSummary(statusBarNotification.getNotification())) {
                n2 = n;
                if ("os_group_undefined".equals((Object)statusBarNotification.getNotification().getGroup())) {
                    n2 = n + 1;
                }
            }
            ++i;
            n = n2;
        }
        return n;
    }
    
    static int getGrouplessSummaryId() {
        return -718463522;
    }
    
    static String getGrouplessSummaryKey() {
        return "os_group_undefined";
    }
    
    static Integer getMostRecentNotifIdFromGroup(final OneSignalDbHelper oneSignalDbHelper, final String s, final boolean b) {
        String s2;
        if (b) {
            s2 = "group_id IS NULL";
        }
        else {
            s2 = "group_id = ?";
        }
        final String concat = s2.concat(" AND dismissed = 0 AND opened = 0 AND is_summary = 0");
        String[] array;
        if (b) {
            array = null;
        }
        else {
            array = new String[] { s };
        }
        final Cursor query = oneSignalDbHelper.query("notification", (String[])null, concat, array, (String)null, (String)null, "created_time DESC", "1");
        if (!query.moveToFirst()) {
            query.close();
            return null;
        }
        final int int1 = query.getInt(query.getColumnIndex("android_notification_id"));
        query.close();
        return int1;
    }
    
    static NotificationManager getNotificationManager(final Context context) {
        return (NotificationManager)context.getSystemService("notification");
    }
}
