package com.onesignal;

import android.os.SystemClock;

public class OSTimeImpl implements OSTime
{
    public long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    public long getElapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }
}
