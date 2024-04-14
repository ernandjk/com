package com.onesignal;

import android.text.TextUtils;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import android.content.Context;

class PushRegistratorHMS implements PushRegistrator
{
    static final String HMS_CLIENT_APP_ID = "client/app_id";
    private static final int NEW_TOKEN_TIMEOUT_MS = 30000;
    private static boolean callbackSuccessful;
    private static PushRegistrator$RegisteredHandler registeredHandler;
    
    private static void doTimeOutWait() {
        try {
            Thread.sleep(30000L);
        }
        catch (final InterruptedException ex) {}
    }
    
    static void fireCallback(final String s) {
        final PushRegistrator$RegisteredHandler registeredHandler = PushRegistratorHMS.registeredHandler;
        if (registeredHandler == null) {
            return;
        }
        PushRegistratorHMS.callbackSuccessful = true;
        registeredHandler.complete(s, 1);
    }
    
    private void getHMSTokenTask(final Context context, final PushRegistrator$RegisteredHandler pushRegistrator$RegisteredHandler) throws ApiException {
        synchronized (this) {
            if (!OSUtils.hasAllHMSLibrariesForPushKit()) {
                pushRegistrator$RegisteredHandler.complete((String)null, -28);
                return;
            }
            final String token = HmsInstanceId.getInstance(context).getToken(AGConnectServicesConfig.fromContext(context).getString("client/app_id"), "HCM");
            if (!TextUtils.isEmpty((CharSequence)token)) {
                final OneSignal$LOG_LEVEL info = OneSignal$LOG_LEVEL.INFO;
                final StringBuilder sb = new StringBuilder("Device registered for HMS, push token = ");
                sb.append(token);
                OneSignal.Log(info, sb.toString());
                pushRegistrator$RegisteredHandler.complete(token, 1);
            }
            else {
                this.waitForOnNewPushTokenEvent(pushRegistrator$RegisteredHandler);
            }
        }
    }
    
    private void waitForOnNewPushTokenEvent(final PushRegistrator$RegisteredHandler pushRegistrator$RegisteredHandler) {
        doTimeOutWait();
        if (!PushRegistratorHMS.callbackSuccessful) {
            OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, "HmsMessageServiceOneSignal.onNewToken timed out.");
            pushRegistrator$RegisteredHandler.complete((String)null, -25);
        }
    }
    
    public void registerForPush(final Context context, final String s, final PushRegistrator$RegisteredHandler registeredHandler) {
        PushRegistratorHMS.registeredHandler = registeredHandler;
        new Thread((Runnable)new PushRegistratorHMS$1(this, context, registeredHandler), "OS_HMS_GET_TOKEN").start();
    }
}
