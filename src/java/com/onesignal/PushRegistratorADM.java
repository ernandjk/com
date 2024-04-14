package com.onesignal;

import android.content.Context;

public class PushRegistratorADM implements PushRegistrator
{
    private static boolean callbackSuccessful;
    private static PushRegistrator$RegisteredHandler registeredCallback;
    
    public static void fireCallback(final String s) {
        final PushRegistrator$RegisteredHandler registeredCallback = PushRegistratorADM.registeredCallback;
        if (registeredCallback == null) {
            return;
        }
        PushRegistratorADM.callbackSuccessful = true;
        registeredCallback.complete(s, 1);
    }
    
    public void registerForPush(final Context context, final String s, final PushRegistrator$RegisteredHandler registeredCallback) {
        PushRegistratorADM.registeredCallback = registeredCallback;
        new Thread((Runnable)new PushRegistratorADM$1(this, context, registeredCallback)).start();
    }
}
