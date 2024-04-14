package com.onesignal;

import android.os.Bundle;
import android.content.Context;
import com.huawei.hms.push.RemoteMessage;
import com.huawei.hms.push.HmsMessageService;

public class HmsMessageServiceOneSignal extends HmsMessageService
{
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        OneSignalHmsEventBridge.onMessageReceived((Context)this, remoteMessage);
    }
    
    @Deprecated
    public void onNewToken(final String s) {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("HmsMessageServiceOneSignal onNewToken refresh token:");
        sb.append(s);
        OneSignal.onesignalLog(debug, sb.toString());
        OneSignalHmsEventBridge.onNewToken((Context)this, s);
    }
    
    public void onNewToken(final String s, final Bundle bundle) {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("HmsMessageServiceOneSignal onNewToken refresh token:");
        sb.append(s);
        OneSignal.onesignalLog(debug, sb.toString());
        OneSignalHmsEventBridge.onNewToken((Context)this, s, bundle);
    }
}
