package com.onesignal;

import org.json.JSONObject;
import android.content.Context;

public class OSNotificationController
{
    private static final String EXTENSION_SERVICE_META_DATA_TAG_NAME = "com.onesignal.NotificationServiceExtension";
    static final String GOOGLE_SENT_TIME_KEY = "google.sent_time";
    static final String GOOGLE_TTL_KEY = "google.ttl";
    private boolean fromBackgroundLogic;
    private final OSNotificationGenerationJob notificationJob;
    private boolean restoring;
    
    OSNotificationController(final Context context, final OSNotification osNotification, final JSONObject jsonObject, final boolean restoring, final boolean fromBackgroundLogic, final Long n) {
        this.restoring = restoring;
        this.fromBackgroundLogic = fromBackgroundLogic;
        this.notificationJob = this.createNotificationJobFromCurrent(context, osNotification, jsonObject, n);
    }
    
    OSNotificationController(final OSNotificationGenerationJob notificationJob, final boolean restoring, final boolean fromBackgroundLogic) {
        this.restoring = restoring;
        this.fromBackgroundLogic = fromBackgroundLogic;
        this.notificationJob = notificationJob;
    }
    
    private OSNotificationGenerationJob createNotificationJobFromCurrent(final Context context, final OSNotification notification, final JSONObject jsonPayload, final Long shownTimeStamp) {
        final OSNotificationGenerationJob osNotificationGenerationJob = new OSNotificationGenerationJob(context);
        osNotificationGenerationJob.setJsonPayload(jsonPayload);
        osNotificationGenerationJob.setShownTimeStamp(shownTimeStamp);
        osNotificationGenerationJob.setRestoring(this.restoring);
        osNotificationGenerationJob.setNotification(notification);
        return osNotificationGenerationJob;
    }
    
    private void notDisplayNotificationLogic(final OSNotification notification) {
        this.notificationJob.setNotification(notification);
        if (this.restoring) {
            NotificationBundleProcessor.markNotificationAsDismissed(this.notificationJob);
        }
        else {
            this.notificationJob.setIsNotificationToDisplay(false);
            NotificationBundleProcessor.processNotification(this.notificationJob, true, false);
            OneSignal.handleNotificationReceived(this.notificationJob);
        }
    }
    
    static void setupNotificationServiceExtension(final Context context) {
        final String manifestMeta = OSUtils.getManifestMeta(context, "com.onesignal.NotificationServiceExtension");
        if (manifestMeta == null) {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "No class found, not setting up OSRemoteNotificationReceivedHandler");
            return;
        }
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("Found class: ");
        sb.append(manifestMeta);
        sb.append(", attempting to call constructor");
        OneSignal.onesignalLog(verbose, sb.toString());
        try {
            final Object instance = Class.forName(manifestMeta).newInstance();
            if (instance instanceof OneSignal.OSRemoteNotificationReceivedHandler && OneSignal.remoteNotificationReceivedHandler == null) {
                OneSignal.setRemoteNotificationReceivedHandler((OneSignal.OSRemoteNotificationReceivedHandler)instance);
            }
        }
        catch (final ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (final InstantiationException ex2) {
            ex2.printStackTrace();
        }
        catch (final IllegalAccessException ex3) {
            ex3.printStackTrace();
        }
    }
    
    public OSNotificationGenerationJob getNotificationJob() {
        return this.notificationJob;
    }
    
    public OSNotificationReceivedEvent getNotificationReceivedEvent() {
        return new OSNotificationReceivedEvent(this, this.notificationJob.getNotification());
    }
    
    public boolean isFromBackgroundLogic() {
        return this.fromBackgroundLogic;
    }
    
    public boolean isNotificationWithinTTL() {
        final boolean restoreTTLFilterActive = OneSignal.getRemoteParamController().isRestoreTTLFilterActive();
        boolean b = true;
        if (!restoreTTLFilterActive) {
            return true;
        }
        if (this.notificationJob.getNotification().getSentTime() + this.notificationJob.getNotification().getTtl() <= OneSignal.getTime().getCurrentTimeMillis() / 1000L) {
            b = false;
        }
        return b;
    }
    
    public boolean isRestoring() {
        return this.restoring;
    }
    
    void processNotification(final OSNotification osNotification, final OSNotification notification) {
        if (notification != null) {
            final boolean stringNotEmpty = OSUtils.isStringNotEmpty(notification.getBody());
            final boolean notificationWithinTTL = this.isNotificationWithinTTL();
            if (stringNotEmpty && notificationWithinTTL) {
                this.notificationJob.setNotification(notification);
                NotificationBundleProcessor.processJobForDisplay(this, this.fromBackgroundLogic);
            }
            else {
                this.notDisplayNotificationLogic(osNotification);
            }
            if (this.restoring) {
                OSUtils.sleep(100);
            }
        }
        else {
            this.notDisplayNotificationLogic(osNotification);
        }
    }
    
    public void setFromBackgroundLogic(final boolean fromBackgroundLogic) {
        this.fromBackgroundLogic = fromBackgroundLogic;
    }
    
    public void setRestoring(final boolean restoring) {
        this.restoring = restoring;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSNotificationController{notificationJob=");
        sb.append((Object)this.notificationJob);
        sb.append(", isRestoring=");
        sb.append(this.restoring);
        sb.append(", isBackgroundLogic=");
        sb.append(this.fromBackgroundLogic);
        sb.append('}');
        return sb.toString();
    }
}
