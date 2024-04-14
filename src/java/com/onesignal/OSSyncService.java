package com.onesignal;

import android.app.job.JobService;
import android.app.job.JobParameters;
import android.app.Service;
import java.lang.ref.WeakReference;
import android.content.Context;

class OSSyncService extends OSBackgroundSync
{
    private static final Object INSTANCE_LOCK;
    private static final long SYNC_AFTER_BG_DELAY_MS = 30000L;
    private static final int SYNC_TASK_ID = 2071862118;
    private static final String SYNC_TASK_THREAD_ID = "OS_SYNCSRV_BG_SYNC";
    private static OSSyncService sInstance;
    private Long nextScheduledSyncTimeMs;
    
    static {
        INSTANCE_LOCK = new Object();
    }
    
    OSSyncService() {
        this.nextScheduledSyncTimeMs = 0L;
    }
    
    static OSSyncService getInstance() {
        if (OSSyncService.sInstance == null) {
            final Object instance_LOCK = OSSyncService.INSTANCE_LOCK;
            synchronized (instance_LOCK) {
                if (OSSyncService.sInstance == null) {
                    OSSyncService.sInstance = new OSSyncService();
                }
            }
        }
        return OSSyncService.sInstance;
    }
    
    void cancelSyncTask(final Context context) {
        final Object lock = OSSyncService.LOCK;
        synchronized (lock) {
            this.nextScheduledSyncTimeMs = 0L;
            if (LocationController.scheduleUpdate(context)) {
                return;
            }
            this.cancelBackgroundSyncTask(context);
        }
    }
    
    protected Class getSyncServiceJobClass() {
        return SyncJobService.class;
    }
    
    protected Class getSyncServicePendingIntentClass() {
        return SyncService.class;
    }
    
    protected int getSyncTaskId() {
        return 2071862118;
    }
    
    protected String getSyncTaskThreadId() {
        return "OS_SYNCSRV_BG_SYNC";
    }
    
    void scheduleLocationUpdateTask(final Context context, final long n) {
        final OneSignal$LOG_LEVEL verbose = OneSignal$LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("OSSyncService scheduleLocationUpdateTask:delayMs: ");
        sb.append(n);
        OneSignal.Log(verbose, sb.toString());
        this.scheduleSyncTask(context, n);
    }
    
    protected void scheduleSyncTask(final Context context) {
        OneSignal.Log(OneSignal$LOG_LEVEL.VERBOSE, "OSSyncService scheduleSyncTask:SYNC_AFTER_BG_DELAY_MS: 30000");
        this.scheduleSyncTask(context, 30000L);
    }
    
    protected void scheduleSyncTask(final Context context, final long n) {
        final Object lock = OSSyncService.LOCK;
        synchronized (lock) {
            if (this.nextScheduledSyncTimeMs != 0L && OneSignal.getTime().getCurrentTimeMillis() + n > this.nextScheduledSyncTimeMs) {
                final OneSignal$LOG_LEVEL verbose = OneSignal$LOG_LEVEL.VERBOSE;
                final StringBuilder sb = new StringBuilder("OSSyncService scheduleSyncTask already update scheduled nextScheduledSyncTimeMs: ");
                sb.append((Object)this.nextScheduledSyncTimeMs);
                OneSignal.Log(verbose, sb.toString());
                return;
            }
            long n2 = n;
            if (n < 5000L) {
                n2 = 5000L;
            }
            this.scheduleBackgroundSyncTask(context, n2);
            this.nextScheduledSyncTimeMs = OneSignal.getTime().getCurrentTimeMillis() + n2;
        }
    }
    
    static class LegacySyncRunnable extends OSSyncService$SyncRunnable
    {
        private WeakReference<Service> callerService;
        
        LegacySyncRunnable(final Service service) {
            this.callerService = (WeakReference<Service>)new WeakReference((Object)service);
        }
        
        protected void stopSync() {
            OneSignal.Log(OneSignal$LOG_LEVEL.DEBUG, "LegacySyncRunnable:Stopped");
            if (this.callerService.get() != null) {
                ((Service)this.callerService.get()).stopSelf();
            }
        }
    }
    
    static class LollipopSyncRunnable extends OSSyncService$SyncRunnable
    {
        private JobParameters jobParameters;
        private WeakReference<JobService> jobService;
        
        LollipopSyncRunnable(final JobService jobService, final JobParameters jobParameters) {
            this.jobService = (WeakReference<JobService>)new WeakReference((Object)jobService);
            this.jobParameters = jobParameters;
        }
        
        protected void stopSync() {
            final OneSignal$LOG_LEVEL debug = OneSignal$LOG_LEVEL.DEBUG;
            final StringBuilder sb = new StringBuilder("LollipopSyncRunnable:JobFinished needsJobReschedule: ");
            sb.append(OSSyncService.getInstance().needsJobReschedule);
            OneSignal.Log(debug, sb.toString());
            final boolean needsJobReschedule = OSSyncService.getInstance().needsJobReschedule;
            OSSyncService.getInstance().needsJobReschedule = false;
            if (this.jobService.get() != null) {
                ((JobService)this.jobService.get()).jobFinished(this.jobParameters, needsJobReschedule);
            }
        }
    }
}
