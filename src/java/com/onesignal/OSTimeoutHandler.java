package com.onesignal;

import android.os.Handler;
import android.os.HandlerThread;

class OSTimeoutHandler extends HandlerThread
{
    private static final Object SYNC_LOCK;
    private static final String TAG = "com.onesignal.OSTimeoutHandler";
    private static OSTimeoutHandler timeoutHandler;
    private final Handler mHandler;
    
    static {
        SYNC_LOCK = new Object();
    }
    
    private OSTimeoutHandler() {
        super(OSTimeoutHandler.TAG);
        this.start();
        this.mHandler = new Handler(this.getLooper());
    }
    
    static OSTimeoutHandler getTimeoutHandler() {
        if (OSTimeoutHandler.timeoutHandler == null) {
            final Object sync_LOCK = OSTimeoutHandler.SYNC_LOCK;
            synchronized (sync_LOCK) {
                if (OSTimeoutHandler.timeoutHandler == null) {
                    OSTimeoutHandler.timeoutHandler = new OSTimeoutHandler();
                }
            }
        }
        return OSTimeoutHandler.timeoutHandler;
    }
    
    void destroyTimeout(final Runnable runnable) {
        final Object sync_LOCK = OSTimeoutHandler.SYNC_LOCK;
        synchronized (sync_LOCK) {
            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("Running destroyTimeout with runnable: ");
            sb.append(runnable.toString());
            OneSignal.Log(debug, sb.toString());
            this.mHandler.removeCallbacks(runnable);
        }
    }
    
    void startTimeout(final long n, final Runnable runnable) {
        final Object sync_LOCK = OSTimeoutHandler.SYNC_LOCK;
        synchronized (sync_LOCK) {
            this.destroyTimeout(runnable);
            final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("Running startTimeout with timeout: ");
            sb.append(n);
            sb.append(" and runnable: ");
            sb.append(runnable.toString());
            OneSignal.Log(debug, sb.toString());
            this.mHandler.postDelayed(runnable, n);
        }
    }
}
