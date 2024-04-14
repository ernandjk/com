package com.onesignal;

import org.json.JSONObject;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import java.lang.ref.WeakReference;
import android.database.Cursor;

class OSNotificationDataController extends OSBackgroundManager
{
    private static final long NOTIFICATION_CACHE_DATA_LIFETIME = 604800L;
    private static final String OS_NOTIFICATIONS_THREAD = "OS_NOTIFICATIONS_THREAD";
    private final OneSignalDbHelper dbHelper;
    private final OSLogger logger;
    
    public OSNotificationDataController(final OneSignalDbHelper dbHelper, final OSLogger logger) {
        this.dbHelper = dbHelper;
        this.logger = logger;
    }
    
    private void cleanNotificationCache() {
        this.runRunnableOnThread((Runnable)new BackgroundRunnable(this) {
            final OSNotificationDataController this$0;
            
            public void run() {
                super.run();
                this.this$0.dbHelper.delete("notification", "created_time < ?", new String[] { String.valueOf(OneSignal.getTime().getCurrentTimeMillis() / 1000L - 604800L) });
            }
        }, "OS_NOTIFICATIONS_THREAD");
    }
    
    private void isDuplicateNotification(final String s, final OSNotificationDataController$InvalidOrDuplicateNotificationCallback osNotificationDataController$InvalidOrDuplicateNotificationCallback) {
        if (s == null || "".equals((Object)s)) {
            osNotificationDataController$InvalidOrDuplicateNotificationCallback.onResult(false);
            return;
        }
        if (!OSNotificationWorkManager.addNotificationIdProcessed(s)) {
            this.logger.debug("Notification notValidOrDuplicated with id duplicated");
            osNotificationDataController$InvalidOrDuplicateNotificationCallback.onResult(true);
            return;
        }
        this.runRunnableOnThread((Runnable)new BackgroundRunnable(this, s, osNotificationDataController$InvalidOrDuplicateNotificationCallback) {
            final OSNotificationDataController this$0;
            final OSNotificationDataController$InvalidOrDuplicateNotificationCallback val$callback;
            final String val$id;
            
            public void run() {
                super.run();
                final Cursor query = this.this$0.dbHelper.query("notification", new String[] { "notification_id" }, "notification_id = ?", new String[] { this.val$id }, null, null, null);
                final boolean moveToFirst = query.moveToFirst();
                query.close();
                boolean b;
                if (moveToFirst) {
                    final OSLogger access$100 = this.this$0.logger;
                    final StringBuilder sb = new StringBuilder("Notification notValidOrDuplicated with id duplicated, duplicate FCM message received, skip processing of ");
                    sb.append(this.val$id);
                    access$100.debug(sb.toString());
                    b = true;
                }
                else {
                    b = false;
                }
                this.val$callback.onResult(b);
            }
        }, "OS_NOTIFICATIONS_THREAD");
    }
    
    void cleanOldCachedData() {
        this.cleanNotificationCache();
    }
    
    void clearOneSignalNotifications(final WeakReference<Context> weakReference) {
        this.runRunnableOnThread((Runnable)new BackgroundRunnable(this, weakReference) {
            final OSNotificationDataController this$0;
            final WeakReference val$weakReference;
            
            public void run() {
                super.run();
                final Context context = (Context)this.val$weakReference.get();
                if (context == null) {
                    return;
                }
                final NotificationManager notificationManager = OneSignalNotificationManager.getNotificationManager(context);
                final Cursor query = this.this$0.dbHelper.query("notification", new String[] { "android_notification_id" }, "dismissed = 0 AND opened = 0", null, null, null, null);
                if (query.moveToFirst()) {
                    do {
                        notificationManager.cancel(query.getInt(query.getColumnIndex("android_notification_id")));
                    } while (query.moveToNext());
                }
                final ContentValues contentValues = new ContentValues();
                contentValues.put("dismissed", Integer.valueOf(1));
                this.this$0.dbHelper.update("notification", contentValues, "opened = 0", null);
                BadgeCountUpdater.updateCount(0, context);
                query.close();
            }
        }, "OS_NOTIFICATIONS_THREAD");
    }
    
    void notValidOrDuplicated(final JSONObject jsonObject, final OSNotificationDataController$InvalidOrDuplicateNotificationCallback osNotificationDataController$InvalidOrDuplicateNotificationCallback) {
        final String osNotificationIdFromJson = OSNotificationFormatHelper.getOSNotificationIdFromJson(jsonObject);
        if (osNotificationIdFromJson == null) {
            this.logger.debug("Notification notValidOrDuplicated with id null");
            osNotificationDataController$InvalidOrDuplicateNotificationCallback.onResult(true);
            return;
        }
        this.isDuplicateNotification(osNotificationIdFromJson, osNotificationDataController$InvalidOrDuplicateNotificationCallback);
    }
    
    void removeGroupedNotifications(final String s, final WeakReference<Context> weakReference) {
        this.runRunnableOnThread((Runnable)new BackgroundRunnable(this, weakReference, s) {
            final OSNotificationDataController this$0;
            final String val$group;
            final WeakReference val$weakReference;
            
            public void run() {
                super.run();
                final Context context = (Context)this.val$weakReference.get();
                if (context == null) {
                    return;
                }
                final NotificationManager notificationManager = OneSignalNotificationManager.getNotificationManager(context);
                final String[] array = { this.val$group };
                final Cursor query = this.this$0.dbHelper.query("notification", new String[] { "android_notification_id" }, "group_id = ? AND dismissed = 0 AND opened = 0", array, null, null, null);
                while (query.moveToNext()) {
                    final int int1 = query.getInt(query.getColumnIndex("android_notification_id"));
                    if (int1 != -1) {
                        notificationManager.cancel(int1);
                    }
                }
                query.close();
                final ContentValues contentValues = new ContentValues();
                contentValues.put("dismissed", Integer.valueOf(1));
                this.this$0.dbHelper.update("notification", contentValues, "group_id = ? AND opened = 0 AND dismissed = 0", array);
                BadgeCountUpdater.update((OneSignalDb)this.this$0.dbHelper, context);
            }
        }, "OS_NOTIFICATIONS_THREAD");
    }
    
    void removeNotification(final int n, final WeakReference<Context> weakReference) {
        this.runRunnableOnThread((Runnable)new BackgroundRunnable(this, weakReference, n) {
            final OSNotificationDataController this$0;
            final int val$id;
            final WeakReference val$weakReference;
            
            public void run() {
                super.run();
                final Context context = (Context)this.val$weakReference.get();
                if (context == null) {
                    return;
                }
                final StringBuilder sb = new StringBuilder("android_notification_id = ");
                sb.append(this.val$id);
                sb.append(" AND opened = 0 AND dismissed = 0");
                final String string = sb.toString();
                final ContentValues contentValues = new ContentValues();
                contentValues.put("dismissed", Integer.valueOf(1));
                if (this.this$0.dbHelper.update("notification", contentValues, string, null) > 0) {
                    NotificationSummaryManager.updatePossibleDependentSummaryOnDismiss(context, (OneSignalDb)this.this$0.dbHelper, this.val$id);
                }
                BadgeCountUpdater.update((OneSignalDb)this.this$0.dbHelper, context);
                OneSignalNotificationManager.getNotificationManager(context).cancel(this.val$id);
            }
        }, "OS_NOTIFICATIONS_THREAD");
    }
}
