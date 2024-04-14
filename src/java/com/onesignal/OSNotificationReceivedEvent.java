package com.onesignal;

import org.json.JSONException;
import org.json.JSONObject;

public class OSNotificationReceivedEvent
{
    private static final long PROCESS_NOTIFICATION_TIMEOUT = 25000L;
    private final OSNotificationController controller;
    private boolean isComplete;
    private final OSNotification notification;
    private final OSTimeoutHandler timeoutHandler;
    private final Runnable timeoutRunnable;
    
    OSNotificationReceivedEvent(final OSNotificationController controller, final OSNotification notification) {
        this.isComplete = false;
        this.notification = notification;
        this.controller = controller;
        (this.timeoutHandler = OSTimeoutHandler.getTimeoutHandler()).startTimeout(25000L, this.timeoutRunnable = (Runnable)new Runnable(this) {
            final OSNotificationReceivedEvent this$0;
            
            public void run() {
                OneSignal.Log(OneSignal.LOG_LEVEL.DEBUG, "Running complete from OSNotificationReceivedEvent timeout runnable!");
                final OSNotificationReceivedEvent this$0 = this.this$0;
                this$0.complete(this$0.getNotification());
            }
        });
    }
    
    static boolean isRunningOnMainThread() {
        return OSUtils.isRunningOnMainThread();
    }
    
    private void processNotification(OSNotification copy) {
        final OSNotificationController controller = this.controller;
        final OSNotification copy2 = this.notification.copy();
        if (copy != null) {
            copy = copy.copy();
        }
        else {
            copy = null;
        }
        controller.processNotification(copy2, copy);
    }
    
    public void complete(final OSNotification osNotification) {
        synchronized (this) {
            this.timeoutHandler.destroyTimeout(this.timeoutRunnable);
            if (this.isComplete) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "OSNotificationReceivedEvent already completed");
                return;
            }
            this.isComplete = true;
            if (isRunningOnMainThread()) {
                new Thread((Runnable)new Runnable(this, osNotification) {
                    final OSNotificationReceivedEvent this$0;
                    final OSNotification val$notification;
                    
                    public void run() {
                        this.this$0.processNotification(this.val$notification);
                    }
                }, "OS_COMPLETE_NOTIFICATION").start();
                return;
            }
            this.processNotification(osNotification);
        }
    }
    
    public OSNotification getNotification() {
        return this.notification;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("notification", (Object)this.notification.toJSONObject());
            jsonObject.put("isComplete", this.isComplete);
        }
        catch (final JSONException ex) {
            ex.printStackTrace();
        }
        return jsonObject;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OSNotificationReceivedEvent{isComplete=");
        sb.append(this.isComplete);
        sb.append(", notification=");
        sb.append((Object)this.notification);
        sb.append('}');
        return sb.toString();
    }
}
