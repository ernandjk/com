package com.onesignal;

import android.os.Bundle;
import android.os.IBinder;
import android.content.Intent;
import android.content.Context;

public class FCMIntentJobService extends JobIntentService
{
    public static final String BUNDLE_EXTRA = "Bundle:Parcelable:Extras";
    private static final int JOB_ID = 123890;
    
    public static void enqueueWork(final Context context, final Intent intent) {
        enqueueWork(context, (Class)FCMIntentJobService.class, 123890, intent, false);
    }
    
    protected void onHandleWork(final Intent intent) {
        final Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        OneSignal.initWithContext((Context)this);
        NotificationBundleProcessor.processBundleFromReceiver((Context)this, extras, (NotificationBundleProcessor$ProcessBundleReceiverCallback)new NotificationBundleProcessor$ProcessBundleReceiverCallback(this) {
            final FCMIntentJobService this$0;
            
            public void onBundleProcessed(final NotificationBundleProcessor$ProcessedBundleResult notificationBundleProcessor$ProcessedBundleResult) {
            }
        });
    }
}
