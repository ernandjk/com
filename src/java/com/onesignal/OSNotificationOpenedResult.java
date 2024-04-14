package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OSNotificationOpenedResult implements OneSignal$EntryStateListener
{
    private static final long PROCESS_NOTIFICATION_TIMEOUT = 5000L;
    private OSNotificationAction action;
    private boolean isComplete;
    private OSNotification notification;
    private final OSTimeoutHandler timeoutHandler;
    private final Runnable timeoutRunnable;
    
    public OSNotificationOpenedResult(final OSNotification notification, final OSNotificationAction action) {
        this.isComplete = false;
        this.notification = notification;
        this.action = action;
        (this.timeoutHandler = OSTimeoutHandler.getTimeoutHandler()).startTimeout(5000L, this.timeoutRunnable = (Runnable)new OSNotificationOpenedResult$1(this));
    }
    
    private void complete(final boolean b) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OSNotificationOpenedResult complete called with opened: ");
        sb.append(b);
        OneSignal.onesignalLog(debug, sb.toString());
        this.timeoutHandler.destroyTimeout(this.timeoutRunnable);
        if (this.isComplete) {
            OneSignal.onesignalLog(OneSignal$LOG_LEVEL.DEBUG, "OSNotificationOpenedResult already completed");
            return;
        }
        this.isComplete = true;
        if (b) {
            OneSignal.applicationOpenedByNotification(this.notification.getNotificationId());
        }
        OneSignal.removeEntryStateListener((OneSignal$EntryStateListener)this);
    }
    
    public OSNotificationAction getAction() {
        return this.action;
    }
    
    public OSNotification getNotification() {
        return this.notification;
    }
    
    public void onEntryStateChange(final OneSignal$AppEntryAction oneSignal$AppEntryAction) {
        final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("OSNotificationOpenedResult onEntryStateChange called with appEntryState: ");
        sb.append((Object)oneSignal$AppEntryAction);
        OneSignal.onesignalLog(debug, sb.toString());
        this.complete(OneSignal$AppEntryAction.APP_CLOSE.equals((Object)oneSignal$AppEntryAction));
    }
    
    @Deprecated
    public String stringify() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", (Object)this.action.toJSONObject());
            jsonObject.put("notification", (Object)this.notification.toJSONObject());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject.toString();
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", (Object)this.action.toJSONObject());
            jsonObject.put("notification", (Object)this.notification.toJSONObject());
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSNotificationOpenedResult{notification=");
        sb.append((Object)this.notification);
        sb.append(", action=");
        sb.append((Object)this.action);
        sb.append(", isComplete=");
        sb.append(this.isComplete);
        sb.append('}');
        return sb.toString();
    }
}
