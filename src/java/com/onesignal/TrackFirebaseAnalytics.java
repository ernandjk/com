package com.onesignal;

import android.os.Bundle;
import java.lang.reflect.Method;
import android.content.Context;
import java.util.concurrent.atomic.AtomicLong;

class TrackFirebaseAnalytics
{
    private static final String EVENT_NOTIFICATION_INFLUENCE_OPEN = "os_notification_influence_open";
    private static final String EVENT_NOTIFICATION_OPENED = "os_notification_opened";
    private static final String EVENT_NOTIFICATION_RECEIVED = "os_notification_received";
    private static Class<?> FirebaseAnalyticsClass;
    private static AtomicLong lastOpenedTime;
    private static OSNotification lastReceivedNotification;
    private static AtomicLong lastReceivedTime;
    private Context appContext;
    private Object mFirebaseAnalyticsInstance;
    
    TrackFirebaseAnalytics(final Context appContext) {
        this.appContext = appContext;
    }
    
    static boolean CanTrack() {
        try {
            TrackFirebaseAnalytics.FirebaseAnalyticsClass = Class.forName("com.google.firebase.analytics.FirebaseAnalytics");
            return true;
        }
        catch (final ClassNotFoundException ex) {
            return false;
        }
    }
    
    private String getCampaignNameFromNotification(final OSNotification osNotification) {
        if (!osNotification.getTemplateName().isEmpty() && !osNotification.getTemplateId().isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(osNotification.getTemplateName());
            sb.append(" - ");
            sb.append(osNotification.getTemplateId());
            return sb.toString();
        }
        if (osNotification.getTitle() != null) {
            return osNotification.getTitle().substring(0, Math.min(10, osNotification.getTitle().length()));
        }
        return "";
    }
    
    private Object getFirebaseAnalyticsInstance(final Context context) {
        if (this.mFirebaseAnalyticsInstance == null) {
            final Method instanceMethod = getInstanceMethod(TrackFirebaseAnalytics.FirebaseAnalyticsClass);
            try {
                this.mFirebaseAnalyticsInstance = instanceMethod.invoke((Object)null, new Object[] { context });
            }
            finally {
                final Throwable t;
                t.printStackTrace();
                return null;
            }
        }
        return this.mFirebaseAnalyticsInstance;
    }
    
    private static Method getInstanceMethod(final Class clazz) {
        try {
            return clazz.getMethod("getInstance", Context.class);
        }
        catch (final NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static Method getTrackMethod(final Class clazz) {
        try {
            return clazz.getMethod("logEvent", String.class, Bundle.class);
        }
        catch (final NoSuchMethodException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    void trackInfluenceOpenEvent() {
        if (TrackFirebaseAnalytics.lastReceivedTime != null) {
            if (TrackFirebaseAnalytics.lastReceivedNotification != null) {
                final long currentTimeMillis = OneSignal.getTime().getCurrentTimeMillis();
                if (currentTimeMillis - TrackFirebaseAnalytics.lastReceivedTime.get() > 120000L) {
                    return;
                }
                final AtomicLong lastOpenedTime = TrackFirebaseAnalytics.lastOpenedTime;
                if (lastOpenedTime != null && currentTimeMillis - lastOpenedTime.get() < 30000L) {
                    return;
                }
                try {
                    final Object firebaseAnalyticsInstance = this.getFirebaseAnalyticsInstance(this.appContext);
                    final Method trackMethod = getTrackMethod(TrackFirebaseAnalytics.FirebaseAnalyticsClass);
                    final Bundle bundle = new Bundle();
                    bundle.putString("source", "OneSignal");
                    bundle.putString("medium", "notification");
                    bundle.putString("notification_id", TrackFirebaseAnalytics.lastReceivedNotification.getNotificationId());
                    bundle.putString("campaign", this.getCampaignNameFromNotification(TrackFirebaseAnalytics.lastReceivedNotification));
                    trackMethod.invoke(firebaseAnalyticsInstance, new Object[] { "os_notification_influence_open", bundle });
                }
                finally {
                    final Throwable t;
                    t.printStackTrace();
                }
            }
        }
    }
    
    void trackOpenedEvent(final OSNotificationOpenedResult osNotificationOpenedResult) {
        if (TrackFirebaseAnalytics.lastOpenedTime == null) {
            TrackFirebaseAnalytics.lastOpenedTime = new AtomicLong();
        }
        TrackFirebaseAnalytics.lastOpenedTime.set(OneSignal.getTime().getCurrentTimeMillis());
        try {
            final Object firebaseAnalyticsInstance = this.getFirebaseAnalyticsInstance(this.appContext);
            final Method trackMethod = getTrackMethod(TrackFirebaseAnalytics.FirebaseAnalyticsClass);
            final Bundle bundle = new Bundle();
            bundle.putString("source", "OneSignal");
            bundle.putString("medium", "notification");
            bundle.putString("notification_id", osNotificationOpenedResult.getNotification().getNotificationId());
            bundle.putString("campaign", this.getCampaignNameFromNotification(osNotificationOpenedResult.getNotification()));
            trackMethod.invoke(firebaseAnalyticsInstance, new Object[] { "os_notification_opened", bundle });
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
    }
    
    void trackReceivedEvent(final OSNotificationOpenedResult osNotificationOpenedResult) {
        try {
            final Object firebaseAnalyticsInstance = this.getFirebaseAnalyticsInstance(this.appContext);
            final Method trackMethod = getTrackMethod(TrackFirebaseAnalytics.FirebaseAnalyticsClass);
            final Bundle bundle = new Bundle();
            bundle.putString("source", "OneSignal");
            bundle.putString("medium", "notification");
            bundle.putString("notification_id", osNotificationOpenedResult.getNotification().getNotificationId());
            bundle.putString("campaign", this.getCampaignNameFromNotification(osNotificationOpenedResult.getNotification()));
            trackMethod.invoke(firebaseAnalyticsInstance, new Object[] { "os_notification_received", bundle });
            if (TrackFirebaseAnalytics.lastReceivedTime == null) {
                TrackFirebaseAnalytics.lastReceivedTime = new AtomicLong();
            }
            TrackFirebaseAnalytics.lastReceivedTime.set(OneSignal.getTime().getCurrentTimeMillis());
            TrackFirebaseAnalytics.lastReceivedNotification = osNotificationOpenedResult.getNotification();
        }
        finally {
            final Throwable t;
            t.printStackTrace();
        }
    }
}
