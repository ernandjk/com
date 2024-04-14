package com.onesignal;

import android.content.Intent;
import android.app.PendingIntent;
import android.app.job.JobInfo$Builder;
import android.content.ComponentName;
import android.app.AlarmManager;
import java.util.Iterator;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;

abstract class OSBackgroundSync
{
    protected static final Object LOCK;
    protected boolean needsJobReschedule;
    private Thread syncBgThread;
    
    static {
        LOCK = new Object();
    }
    
    OSBackgroundSync() {
        this.needsJobReschedule = false;
    }
    
    private boolean hasBootPermission(final Context context) {
        return AndroidSupportV4Compat.ContextCompat.checkSelfPermission(context, "android.permission.RECEIVE_BOOT_COMPLETED") == 0;
    }
    
    private boolean isJobIdRunning(final Context context) {
        final Iterator iterator = ((JobScheduler)context.getSystemService("jobscheduler")).getAllPendingJobs().iterator();
        while (iterator.hasNext()) {
            if (((JobInfo)iterator.next()).getId() == this.getSyncTaskId()) {
                final Thread syncBgThread = this.syncBgThread;
                if (syncBgThread != null && syncBgThread.isAlive()) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private void scheduleSyncServiceAsAlarm(final Context context, final long n) {
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" scheduleServiceSyncTask:atTime: ");
        sb.append(n);
        OneSignal.Log(verbose, sb.toString());
        ((AlarmManager)context.getSystemService("alarm")).set(0, OneSignal.getTime().getCurrentTimeMillis() + n, this.syncServicePendingIntent(context));
    }
    
    private void scheduleSyncServiceAsJob(final Context context, final long minimumLatency) {
        final OneSignal.LOG_LEVEL verbose = OneSignal.LOG_LEVEL.VERBOSE;
        final StringBuilder sb = new StringBuilder("OSBackgroundSync scheduleSyncServiceAsJob:atTime: ");
        sb.append(minimumLatency);
        OneSignal.Log(verbose, sb.toString());
        if (this.isJobIdRunning(context)) {
            OneSignal.Log(OneSignal.LOG_LEVEL.VERBOSE, "OSBackgroundSync scheduleSyncServiceAsJob Scheduler already running!");
            this.needsJobReschedule = true;
            return;
        }
        final JobInfo$Builder jobInfo$Builder = new JobInfo$Builder(this.getSyncTaskId(), new ComponentName(context, this.getSyncServiceJobClass()));
        jobInfo$Builder.setMinimumLatency(minimumLatency).setRequiredNetworkType(1);
        if (this.hasBootPermission(context)) {
            jobInfo$Builder.setPersisted(true);
        }
        final JobScheduler jobScheduler = (JobScheduler)context.getSystemService("jobscheduler");
        try {
            final int schedule = jobScheduler.schedule(jobInfo$Builder.build());
            final OneSignal.LOG_LEVEL info = OneSignal.LOG_LEVEL.INFO;
            final StringBuilder sb2 = new StringBuilder("OSBackgroundSync scheduleSyncServiceAsJob:result: ");
            sb2.append(schedule);
            OneSignal.Log(info, sb2.toString());
        }
        catch (final NullPointerException ex) {
            OneSignal.Log(OneSignal.LOG_LEVEL.ERROR, "scheduleSyncServiceAsJob called JobScheduler.jobScheduler which triggered an internal null Android error. Skipping job.", (Throwable)ex);
        }
    }
    
    private PendingIntent syncServicePendingIntent(final Context context) {
        return PendingIntent.getService(context, this.getSyncTaskId(), new Intent(context, this.getSyncServicePendingIntentClass()), 201326592);
    }
    
    private static boolean useJob() {
        return true;
    }
    
    protected void cancelBackgroundSyncTask(final Context context) {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" cancel background sync");
        OneSignal.onesignalLog(debug, sb.toString());
        final Object lock = OSBackgroundSync.LOCK;
        synchronized (lock) {
            if (useJob()) {
                ((JobScheduler)context.getSystemService("jobscheduler")).cancel(this.getSyncTaskId());
            }
            else {
                ((AlarmManager)context.getSystemService("alarm")).cancel(this.syncServicePendingIntent(context));
            }
        }
    }
    
    void doBackgroundSync(final Context context, final Runnable runnable) {
        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "OSBackground sync, calling initWithContext");
        OneSignal.initWithContext(context);
        (this.syncBgThread = new Thread(runnable, this.getSyncTaskThreadId())).start();
    }
    
    protected abstract Class getSyncServiceJobClass();
    
    protected abstract Class getSyncServicePendingIntentClass();
    
    protected abstract int getSyncTaskId();
    
    protected abstract String getSyncTaskThreadId();
    
    protected void scheduleBackgroundSyncTask(final Context context, final long n) {
        final Object lock = OSBackgroundSync.LOCK;
        synchronized (lock) {
            if (useJob()) {
                this.scheduleSyncServiceAsJob(context, n);
            }
            else {
                this.scheduleSyncServiceAsAlarm(context, n);
            }
        }
    }
    
    protected abstract void scheduleSyncTask(final Context p0);
    
    boolean stopSyncBgThread() {
        final Thread syncBgThread = this.syncBgThread;
        if (syncBgThread == null) {
            return false;
        }
        if (!syncBgThread.isAlive()) {
            return false;
        }
        this.syncBgThread.interrupt();
        return true;
    }
}
