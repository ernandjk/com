package com.onesignal;

import android.content.Context;
import android.app.job.JobParameters;
import android.app.job.JobService;

public class SyncJobService extends JobService
{
    public boolean onStartJob(final JobParameters jobParameters) {
        OSSyncService.getInstance().doBackgroundSync((Context)this, (Runnable)new OSSyncService$LollipopSyncRunnable((JobService)this, jobParameters));
        return true;
    }
    
    public boolean onStopJob(final JobParameters jobParameters) {
        final boolean stopSyncBgThread = OSSyncService.getInstance().stopSyncBgThread();
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("SyncJobService onStopJob called, system conditions not available reschedule: ");
        sb.append(stopSyncBgThread);
        OneSignal.Log(debug, sb.toString());
        return stopSyncBgThread;
    }
}
