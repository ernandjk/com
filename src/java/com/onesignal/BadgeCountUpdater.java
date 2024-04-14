package com.onesignal;

import android.service.notification.StatusBarNotification;
import android.database.Cursor;
import com.onesignal.shortcutbadger.ShortcutBadgeException;
import com.onesignal.shortcutbadger.ShortcutBadger;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.Context;

class BadgeCountUpdater
{
    private static int badgesEnabled = -1;
    
    private static boolean areBadgeSettingsEnabled(final Context context) {
        final int badgesEnabled = BadgeCountUpdater.badgesEnabled;
        final boolean b = false;
        boolean b2 = false;
        if (badgesEnabled != -1) {
            if (badgesEnabled == 1) {
                b2 = true;
            }
            return b2;
        }
        try {
            final Bundle metaData = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData;
            if (metaData != null) {
                BadgeCountUpdater.badgesEnabled = ("DISABLE".equals((Object)metaData.getString("com.onesignal.BadgeCount")) ? 0 : 1);
            }
            else {
                BadgeCountUpdater.badgesEnabled = 1;
            }
        }
        catch (final PackageManager$NameNotFoundException ex) {
            BadgeCountUpdater.badgesEnabled = 0;
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Error reading meta-data tag 'com.onesignal.BadgeCount'. Disabling badge setting.", (Throwable)ex);
        }
        boolean b3 = b;
        if (BadgeCountUpdater.badgesEnabled == 1) {
            b3 = true;
        }
        return b3;
    }
    
    private static boolean areBadgesEnabled(final Context context) {
        return areBadgeSettingsEnabled(context) && OSUtils.areNotificationsEnabled(context);
    }
    
    static void update(final OneSignalDb oneSignalDb, final Context context) {
        if (!areBadgesEnabled(context)) {
            return;
        }
        if (Build$VERSION.SDK_INT >= 23) {
            updateStandard(context);
        }
        else {
            updateFallback(oneSignalDb, context);
        }
    }
    
    static void updateCount(final int n, final Context context) {
        if (!areBadgeSettingsEnabled(context)) {
            return;
        }
        try {
            ShortcutBadger.applyCountOrThrow(context, n);
        }
        catch (final ShortcutBadgeException ex) {}
    }
    
    private static void updateFallback(final OneSignalDb oneSignalDb, final Context context) {
        final Cursor query = oneSignalDb.query("notification", null, OneSignalDbHelper.recentUninteractedWithNotificationsWhere().toString(), null, null, null, null, NotificationLimitManager.MAX_NUMBER_OF_NOTIFICATIONS_STR);
        final int count = query.getCount();
        query.close();
        updateCount(count, context);
    }
    
    private static void updateStandard(final Context context) {
        final StatusBarNotification[] activeNotifications = OneSignalNotificationManager.getActiveNotifications(context);
        final int length = activeNotifications.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            if (!NotificationLimitManager.isGroupSummary(activeNotifications[i])) {
                ++n;
            }
            ++i;
        }
        updateCount(n, context);
    }
}
