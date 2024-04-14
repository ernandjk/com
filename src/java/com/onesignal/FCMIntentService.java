package com.onesignal;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.app.IntentService;

public class FCMIntentService extends IntentService
{
    public FCMIntentService() {
        super("FCMIntentService");
        this.setIntentRedelivery(true);
    }
    
    protected void onHandleIntent(final Intent intent) {
        final Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        OneSignal.initWithContext((Context)this);
        NotificationBundleProcessor.processBundleFromReceiver((Context)this, extras, (NotificationBundleProcessor.ProcessBundleReceiverCallback)new FCMIntentService$1(this, intent));
    }
}
