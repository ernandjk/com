package com.onesignal;

import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;

public class NotificationOpenedActivityHMS extends Activity
{
    private void processIntent() {
        this.processOpen(this.getIntent());
        this.finish();
    }
    
    private void processOpen(final Intent intent) {
        NotificationPayloadProcessorHMS.handleHMSNotificationOpenIntent(this, intent);
    }
    
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.processIntent();
    }
    
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.processIntent();
    }
}
