package com.onesignal;

import kotlin.jvm.internal.Intrinsics;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import kotlin.Metadata;
import com.amazon.device.messaging.ADMMessageHandlerJobBase;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0014J\u001c\u0010\t\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014J\u001c\u0010\f\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\r\u001a\u0004\u0018\u00010\u000bH\u0014J\u001c\u0010\u000e\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u000f\u001a\u0004\u0018\u00010\u000bH\u0014¨\u0006\u0010" }, d2 = { "Lcom/onesignal/ADMMessageHandlerJob;", "Lcom/amazon/device/messaging/ADMMessageHandlerJobBase;", "()V", "onMessage", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "onRegistered", "newRegistrationId", "", "onRegistrationError", "error", "onUnregistered", "registrationId", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class ADMMessageHandlerJob extends ADMMessageHandlerJobBase
{
    protected void onMessage(final Context context, final Intent intent) {
        Bundle extras;
        if (intent != null) {
            extras = intent.getExtras();
        }
        else {
            extras = null;
        }
        NotificationBundleProcessor.processBundleFromReceiver(context, extras, (NotificationBundleProcessor.ProcessBundleReceiverCallback)new ADMMessageHandlerJob$onMessage$bundleReceiverCallback$1(extras, context));
    }
    
    protected void onRegistered(final Context context, final String s) {
        final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
        final StringBuilder sb = new StringBuilder("ADM registration ID: ");
        sb.append(s);
        OneSignal.Log(info, sb.toString());
        PushRegistratorADM.fireCallback(s);
    }
    
    protected void onRegistrationError(final Context context, final String s) {
        final OneSignal.LOG_LEVEL error = OneSignal.LOG_LEVEL.ERROR;
        final StringBuilder sb = new StringBuilder("ADM:onRegistrationError: ");
        sb.append(s);
        OneSignal.Log(error, sb.toString());
        if (Intrinsics.areEqual((Object)"INVALID_SENDER", (Object)s)) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "Please double check that you have a matching package name (NOTE: Case Sensitive), api_key.txt, and the apk was signed with the same Keystore and Alias.");
        }
        PushRegistratorADM.fireCallback((String)null);
    }
    
    protected void onUnregistered(final Context context, final String s) {
        final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
        final StringBuilder sb = new StringBuilder("ADM:onUnregistered: ");
        sb.append(s);
        OneSignal.Log(info, sb.toString());
    }
}
