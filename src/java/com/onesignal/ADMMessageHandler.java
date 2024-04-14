package com.onesignal;

import com.amazon.device.messaging.ADMMessageReceiver;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import com.amazon.device.messaging.ADMMessageHandlerBase;

public class ADMMessageHandler extends ADMMessageHandlerBase
{
    private static final int JOB_ID = 123891;
    
    public ADMMessageHandler() {
        super("ADMMessageHandler");
    }
    
    protected void onMessage(final Intent intent) {
        final Context applicationContext = this.getApplicationContext();
        final Bundle extras = intent.getExtras();
        NotificationBundleProcessor.processBundleFromReceiver(applicationContext, extras, (NotificationBundleProcessor.ProcessBundleReceiverCallback)new ADMMessageHandler$1(this, extras, applicationContext));
    }
    
    protected void onRegistered(final String s) {
        final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
        final StringBuilder sb = new StringBuilder("ADM registration ID: ");
        sb.append(s);
        OneSignal.Log(info, sb.toString());
        PushRegistratorADM.fireCallback(s);
    }
    
    protected void onRegistrationError(final String s) {
        final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
        final StringBuilder sb = new StringBuilder("ADM:onRegistrationError: ");
        sb.append(s);
        OneSignal.Log(error, sb.toString());
        if ("INVALID_SENDER".equals((Object)s)) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Please double check that you have a matching package name (NOTE: Case Sensitive), api_key.txt, and the apk was signed with the same Keystore and Alias.");
        }
        PushRegistratorADM.fireCallback((String)null);
    }
    
    protected void onUnregistered(final String s) {
        final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
        final StringBuilder sb = new StringBuilder("ADM:onUnregistered: ");
        sb.append(s);
        OneSignal.Log(info, sb.toString());
    }
    
    public static class Receiver extends ADMMessageReceiver
    {
        public Receiver() {
            super((Class)ADMMessageHandler.class);
            boolean b;
            try {
                Class.forName("com.amazon.device.messaging.ADMMessageHandlerJobBase");
                b = true;
            }
            catch (final ClassNotFoundException ex) {
                b = false;
            }
            if (b) {
                this.registerJobServiceClass((Class)ADMMessageHandlerJob.class, 123891);
            }
            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("ADM latest available: ");
            sb.append(b);
            OneSignal.Log(debug, sb.toString());
        }
    }
}
