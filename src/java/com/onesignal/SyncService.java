package com.onesignal;

import android.content.Context;
import android.os.IBinder;
import android.content.Intent;
import android.app.Service;

public class SyncService extends Service
{
    public IBinder onBind(final Intent intent) {
        return null;
    }
    
    public int onStartCommand(final Intent intent, final int n, final int n2) {
        OSSyncService.getInstance().doBackgroundSync((Context)this, (Runnable)new OSSyncService$LegacySyncRunnable((Service)this));
        return 1;
    }
}
