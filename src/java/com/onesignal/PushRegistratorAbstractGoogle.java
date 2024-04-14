package com.onesignal;

import android.content.Context;
import java.io.IOException;

abstract class PushRegistratorAbstractGoogle implements PushRegistrator
{
    private static int REGISTRATION_RETRY_BACKOFF_MS = 10000;
    private static int REGISTRATION_RETRY_COUNT = 5;
    private boolean firedCallback;
    private Thread registerThread;
    private PushRegistrator$RegisteredHandler registeredHandler;
    
    private boolean attemptRegistration(final String s, int pushStatusFromThrowable) {
        try {
            final String token = this.getToken(s);
            final OneSignal$LOG_LEVEL info = OneSignal$LOG_LEVEL.INFO;
            final StringBuilder sb = new StringBuilder("Device registered, push token = ");
            sb.append(token);
            OneSignal.Log(info, sb.toString());
            this.registeredHandler.complete(token, 1);
            return true;
        }
        catch (final IOException ex) {
            final int pushStatusFromThrowable2 = pushStatusFromThrowable((Throwable)ex);
            final String rootCauseMessage = OSUtils.getRootCauseMessage((Throwable)ex);
            if ("SERVICE_NOT_AVAILABLE".equals((Object)rootCauseMessage) || "AUTHENTICATION_FAILED".equals((Object)rootCauseMessage)) {
                final Exception ex2 = new Exception((Throwable)ex);
                if (pushStatusFromThrowable >= PushRegistratorAbstractGoogle.REGISTRATION_RETRY_COUNT - 1) {
                    final OneSignal$LOG_LEVEL error = OneSignal$LOG_LEVEL.ERROR;
                    final StringBuilder sb2 = new StringBuilder("Retry count of ");
                    sb2.append(PushRegistratorAbstractGoogle.REGISTRATION_RETRY_COUNT);
                    sb2.append(" exceed! Could not get a ");
                    sb2.append(this.getProviderName());
                    sb2.append(" Token.");
                    OneSignal.Log(error, sb2.toString(), (Throwable)ex2);
                }
                else {
                    final OneSignal$LOG_LEVEL info2 = OneSignal$LOG_LEVEL.INFO;
                    final StringBuilder sb3 = new StringBuilder("'Google Play services' returned ");
                    sb3.append(rootCauseMessage);
                    sb3.append(" error. Current retry count: ");
                    sb3.append(pushStatusFromThrowable);
                    OneSignal.Log(info2, sb3.toString(), (Throwable)ex2);
                    if (pushStatusFromThrowable == 2) {
                        this.registeredHandler.complete((String)null, pushStatusFromThrowable2);
                        return this.firedCallback = true;
                    }
                }
                return false;
            }
            final Exception ex3 = new Exception((Throwable)ex);
            final OneSignal$LOG_LEVEL error2 = OneSignal$LOG_LEVEL.ERROR;
            final StringBuilder sb4 = new StringBuilder("Error Getting ");
            sb4.append(this.getProviderName());
            sb4.append(" Token");
            OneSignal.Log(error2, sb4.toString(), (Throwable)ex3);
            if (!this.firedCallback) {
                this.registeredHandler.complete((String)null, pushStatusFromThrowable2);
            }
            return true;
        }
        finally {
            final Throwable t;
            final Exception ex4 = new Exception(t);
            pushStatusFromThrowable = pushStatusFromThrowable(t);
            final OneSignal$LOG_LEVEL error3 = OneSignal$LOG_LEVEL.ERROR;
            final StringBuilder sb5 = new StringBuilder("Unknown error getting ");
            sb5.append(this.getProviderName());
            sb5.append(" Token");
            OneSignal.Log(error3, sb5.toString(), (Throwable)ex4);
            this.registeredHandler.complete((String)null, pushStatusFromThrowable);
            return true;
        }
    }
    
    private void internalRegisterForPush(final String s) {
        try {
            if (OSUtils.isGMSInstalledAndEnabled()) {
                this.registerInBackground(s);
            }
            else {
                GooglePlayServicesUpgradePrompt.showUpdateGPSDialog();
                OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, "'Google Play services' app not installed or disabled on the device.");
                this.registeredHandler.complete((String)null, -7);
            }
        }
        finally {
            final OneSignal$LOG_LEVEL error = OneSignal$LOG_LEVEL.ERROR;
            final StringBuilder sb = new StringBuilder("Could not register with ");
            sb.append(this.getProviderName());
            sb.append(" due to an issue with your AndroidManifest.xml or with 'Google Play services'.");
            final Throwable t;
            OneSignal.Log(error, sb.toString(), t);
            this.registeredHandler.complete((String)null, -8);
        }
    }
    
    private boolean isValidProjectNumber(final String s, final PushRegistrator$RegisteredHandler pushRegistrator$RegisteredHandler) {
        boolean b;
        try {
            Float.parseFloat(s);
        }
        finally {
            b = false;
        }
        if (!b) {
            OneSignal.Log(OneSignal$LOG_LEVEL.ERROR, "Missing Google Project number!\nPlease enter a Google Project number / Sender ID on under App Settings > Android > Configuration on the OneSignal dashboard.");
            pushRegistrator$RegisteredHandler.complete((String)null, -6);
            return false;
        }
        return true;
    }
    
    private static int pushStatusFromThrowable(final Throwable t) {
        final String rootCauseMessage = OSUtils.getRootCauseMessage(t);
        if (!(t instanceof IOException)) {
            return -12;
        }
        if ("SERVICE_NOT_AVAILABLE".equals((Object)rootCauseMessage)) {
            return -9;
        }
        if ("AUTHENTICATION_FAILED".equals((Object)rootCauseMessage)) {
            return -29;
        }
        return -11;
    }
    
    private void registerInBackground(final String s) {
        synchronized (this) {
            final Thread registerThread = this.registerThread;
            if (registerThread != null && registerThread.isAlive()) {
                return;
            }
            (this.registerThread = new Thread((Runnable)new PushRegistratorAbstractGoogle$1(this, s))).start();
        }
    }
    
    abstract String getProviderName();
    
    abstract String getToken(final String p0) throws Throwable;
    
    public void registerForPush(final Context context, final String s, final PushRegistrator$RegisteredHandler registeredHandler) {
        this.registeredHandler = registeredHandler;
        if (this.isValidProjectNumber(s, registeredHandler)) {
            this.internalRegisterForPush(s);
        }
    }
}
