package com.onesignal;

import android.app.job.JobParameters;
import android.app.job.JobService;

abstract class OneSignalJobServiceBase extends JobService
{
    public boolean onStartJob(final JobParameters jobParameters) {
        if (jobParameters.getExtras() == null) {
            return false;
        }
        new Thread((Runnable)new Runnable(this, this, jobParameters) {
            final OneSignalJobServiceBase this$0;
            final JobParameters val$finalJobParameters;
            final JobService val$jobService;
            
            public void run() {
                this.this$0.startProcessing(this.val$jobService, this.val$finalJobParameters);
                this.this$0.jobFinished(this.val$finalJobParameters, false);
            }
        }, "OS_JOBSERVICE_BASE").start();
        return true;
    }
    
    public boolean onStopJob(final JobParameters jobParameters) {
        return true;
    }
    
    abstract void startProcessing(final JobService p0, final JobParameters p1);
}
