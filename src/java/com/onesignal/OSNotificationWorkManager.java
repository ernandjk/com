package com.onesignal;

import androidx.work.ExistingWorkPolicy;
import androidx.work.Data$Builder;
import androidx.work.OneTimeWorkRequest$Builder;
import androidx.work.OneTimeWorkRequest;
import android.content.Context;
import java.util.Set;

class OSNotificationWorkManager
{
    private static final String ANDROID_NOTIF_ID_WORKER_DATA_PARAM = "android_notif_id";
    private static final String IS_RESTORING_WORKER_DATA_PARAM = "is_restoring";
    private static final String JSON_PAYLOAD_WORKER_DATA_PARAM = "json_payload";
    private static final String TIMESTAMP_WORKER_DATA_PARAM = "timestamp";
    private static Set<String> notificationIds;
    
    static {
        OSNotificationWorkManager.notificationIds = OSUtils.newConcurrentSet();
    }
    
    static boolean addNotificationIdProcessed(final String s) {
        if (OSUtils.isStringNotEmpty(s)) {
            if (OSNotificationWorkManager.notificationIds.contains((Object)s)) {
                final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
                final StringBuilder sb = new StringBuilder("OSNotificationWorkManager notification with notificationId: ");
                sb.append(s);
                sb.append(" already queued");
                OneSignal.Log(debug, sb.toString());
                return false;
            }
            OSNotificationWorkManager.notificationIds.add((Object)s);
        }
        return true;
    }
    
    static void beginEnqueueingWork(final Context context, final String s, final int n, final String s2, final long n2, final boolean b, final boolean b2) {
        final OneTimeWorkRequest oneTimeWorkRequest = (OneTimeWorkRequest)((OneTimeWorkRequest$Builder)new OneTimeWorkRequest$Builder((Class)OSNotificationWorkManager.OSNotificationWorkManager$NotificationWorker.class).setInputData(new Data$Builder().putInt("android_notif_id", n).putString("json_payload", s2).putLong("timestamp", n2).putBoolean("is_restoring", b).build())).build();
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OSNotificationWorkManager enqueueing notification work with notificationId: ");
        sb.append(s);
        sb.append(" and jsonPayload: ");
        sb.append(s2);
        OneSignal.Log(debug, sb.toString());
        OSWorkManagerHelper.getInstance(context).enqueueUniqueWork(s, ExistingWorkPolicy.KEEP, oneTimeWorkRequest);
    }
    
    static void removeNotificationIdProcessed(final String s) {
        if (OSUtils.isStringNotEmpty(s)) {
            OSNotificationWorkManager.notificationIds.remove((Object)s);
        }
    }
}
