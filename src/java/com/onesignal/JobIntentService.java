package com.onesignal;

import android.os.IBinder;
import android.os.AsyncTask;
import android.os.Build$VERSION;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Service;

abstract class JobIntentService extends Service
{
    static final boolean DEBUG = false;
    static final String TAG = "JobIntentService";
    static final HashMap<ComponentNameWithWakeful, WorkEnqueuer> sClassWorkEnqueuer;
    static final Object sLock;
    final ArrayList<JobIntentService.JobIntentService$CompatWorkItem> mCompatQueue;
    WorkEnqueuer mCompatWorkEnqueuer;
    CommandProcessor mCurProcessor;
    boolean mDestroyed;
    boolean mInterruptIfStopped;
    CompatJobEngine mJobImpl;
    boolean mStopped;
    
    static {
        sLock = new Object();
        sClassWorkEnqueuer = new HashMap();
    }
    
    public JobIntentService() {
        this.mInterruptIfStopped = false;
        this.mStopped = false;
        this.mDestroyed = false;
        this.mCompatQueue = (ArrayList<JobIntentService.JobIntentService$CompatWorkItem>)new ArrayList();
    }
    
    public static void enqueueWork(final Context context, final ComponentName componentName, final int n, final Intent intent, final boolean b) {
        if (intent != null) {
            final Object sLock = JobIntentService.sLock;
            synchronized (sLock) {
                final WorkEnqueuer workEnqueuer = getWorkEnqueuer(context, componentName, true, n, b);
                workEnqueuer.ensureJobId(n);
                try {
                    workEnqueuer.enqueueWork(intent);
                }
                catch (final IllegalStateException ex) {
                    if (!b) {
                        throw ex;
                    }
                    getWorkEnqueuer(context, componentName, true, n, false).enqueueWork(intent);
                }
                return;
            }
        }
        throw new IllegalArgumentException("work must not be null");
    }
    
    public static void enqueueWork(final Context context, final Class clazz, final int n, final Intent intent, final boolean b) {
        enqueueWork(context, new ComponentName(context, clazz), n, intent, b);
    }
    
    static WorkEnqueuer getWorkEnqueuer(final Context context, final ComponentName componentName, final boolean b, final int n, final boolean b2) {
        final ComponentNameWithWakeful componentNameWithWakeful = new ComponentNameWithWakeful(componentName, b2);
        final HashMap<ComponentNameWithWakeful, WorkEnqueuer> sClassWorkEnqueuer = JobIntentService.sClassWorkEnqueuer;
        WorkEnqueuer workEnqueuer;
        if ((workEnqueuer = (WorkEnqueuer)sClassWorkEnqueuer.get((Object)componentNameWithWakeful)) == null) {
            Object o;
            if (Build$VERSION.SDK_INT >= 26 && !b2) {
                if (!b) {
                    throw new IllegalArgumentException("Can't be here without a job id");
                }
                o = new JobIntentService.JobIntentService$JobWorkEnqueuer(context, componentName, n);
            }
            else {
                o = new JobIntentService.JobIntentService$CompatWorkEnqueuer(context, componentName);
            }
            sClassWorkEnqueuer.put((Object)componentNameWithWakeful, o);
            workEnqueuer = (WorkEnqueuer)o;
        }
        return workEnqueuer;
    }
    
    GenericWorkItem dequeueWork() {
        final CompatJobEngine mJobImpl = this.mJobImpl;
        if (mJobImpl != null) {
            final GenericWorkItem dequeueWork = mJobImpl.dequeueWork();
            if (dequeueWork != null) {
                return dequeueWork;
            }
        }
        final ArrayList<JobIntentService.JobIntentService$CompatWorkItem> mCompatQueue = this.mCompatQueue;
        synchronized (mCompatQueue) {
            if (this.mCompatQueue.size() > 0) {
                return (GenericWorkItem)this.mCompatQueue.remove(0);
            }
            return null;
        }
    }
    
    boolean doStopCurrentWork() {
        final CommandProcessor mCurProcessor = this.mCurProcessor;
        if (mCurProcessor != null) {
            mCurProcessor.cancel(this.mInterruptIfStopped);
        }
        this.mStopped = true;
        return this.onStopCurrentWork();
    }
    
    void ensureProcessorRunningLocked(final boolean b) {
        if (this.mCurProcessor == null) {
            this.mCurProcessor = new CommandProcessor();
            final WorkEnqueuer mCompatWorkEnqueuer = this.mCompatWorkEnqueuer;
            if (mCompatWorkEnqueuer != null && b) {
                mCompatWorkEnqueuer.serviceProcessingStarted();
            }
            this.mCurProcessor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Object[])new Void[0]);
        }
    }
    
    public boolean isStopped() {
        return this.mStopped;
    }
    
    public IBinder onBind(final Intent intent) {
        final CompatJobEngine mJobImpl = this.mJobImpl;
        if (mJobImpl != null) {
            return mJobImpl.compatGetBinder();
        }
        return null;
    }
    
    public void onCreate() {
        super.onCreate();
        if (Build$VERSION.SDK_INT >= 26) {
            this.mJobImpl = (CompatJobEngine)new JobIntentService.JobIntentService$JobServiceEngineImpl(this);
            this.mCompatWorkEnqueuer = null;
        }
        this.mCompatWorkEnqueuer = getWorkEnqueuer((Context)this, new ComponentName((Context)this, (Class)this.getClass()), false, 0, true);
    }
    
    public void onDestroy() {
        super.onDestroy();
        this.doStopCurrentWork();
        final ArrayList<JobIntentService.JobIntentService$CompatWorkItem> mCompatQueue = this.mCompatQueue;
        synchronized (mCompatQueue) {
            this.mDestroyed = true;
            this.mCompatWorkEnqueuer.serviceProcessingFinished();
        }
    }
    
    protected abstract void onHandleWork(final Intent p0);
    
    public int onStartCommand(Intent intent, final int n, final int n2) {
        this.mCompatWorkEnqueuer.serviceStartReceived();
        final ArrayList<JobIntentService.JobIntentService$CompatWorkItem> mCompatQueue = this.mCompatQueue;
        synchronized (mCompatQueue) {
            final ArrayList<JobIntentService.JobIntentService$CompatWorkItem> mCompatQueue2 = this.mCompatQueue;
            if (intent == null) {
                intent = new Intent();
            }
            mCompatQueue2.add((Object)new JobIntentService.JobIntentService$CompatWorkItem(this, intent, n2));
            this.ensureProcessorRunningLocked(true);
            return 3;
        }
    }
    
    public boolean onStopCurrentWork() {
        return true;
    }
    
    void processorFinished() {
        final ArrayList<JobIntentService.JobIntentService$CompatWorkItem> mCompatQueue = this.mCompatQueue;
        if (mCompatQueue != null) {
            synchronized (mCompatQueue) {
                this.mCurProcessor = null;
                final ArrayList<JobIntentService.JobIntentService$CompatWorkItem> mCompatQueue2 = this.mCompatQueue;
                if (mCompatQueue2 != null && mCompatQueue2.size() > 0) {
                    this.ensureProcessorRunningLocked(false);
                }
                else if (!this.mDestroyed) {
                    this.mCompatWorkEnqueuer.serviceProcessingFinished();
                }
            }
        }
    }
    
    public void setInterruptIfStopped(final boolean mInterruptIfStopped) {
        this.mInterruptIfStopped = mInterruptIfStopped;
    }
    
    final class CommandProcessor extends AsyncTask<Void, Void, Void>
    {
        final JobIntentService this$0;
        
        CommandProcessor(final JobIntentService this$0) {
            this.this$0 = this$0;
        }
        
        protected Void doInBackground(final Void... array) {
            while (true) {
                final GenericWorkItem dequeueWork = this.this$0.dequeueWork();
                if (dequeueWork == null) {
                    break;
                }
                this.this$0.onHandleWork(dequeueWork.getIntent());
                dequeueWork.complete();
            }
            return null;
        }
        
        protected void onCancelled(final Void void1) {
            this.this$0.processorFinished();
        }
        
        protected void onPostExecute(final Void void1) {
            this.this$0.processorFinished();
        }
    }
    
    interface CompatJobEngine
    {
        IBinder compatGetBinder();
        
        GenericWorkItem dequeueWork();
    }
    
    private static class ComponentNameWithWakeful
    {
        private ComponentName componentName;
        private boolean useWakefulService;
        
        ComponentNameWithWakeful(final ComponentName componentName, final boolean useWakefulService) {
            this.componentName = componentName;
            this.useWakefulService = useWakefulService;
        }
    }
    
    interface GenericWorkItem
    {
        void complete();
        
        Intent getIntent();
    }
    
    abstract static class WorkEnqueuer
    {
        final ComponentName mComponentName;
        boolean mHasJobId;
        int mJobId;
        
        WorkEnqueuer(final ComponentName mComponentName) {
            this.mComponentName = mComponentName;
        }
        
        abstract void enqueueWork(final Intent p0);
        
        void ensureJobId(final int mJobId) {
            if (!this.mHasJobId) {
                this.mHasJobId = true;
                this.mJobId = mJobId;
            }
            else if (this.mJobId != mJobId) {
                final StringBuilder sb = new StringBuilder("Given job ID ");
                sb.append(mJobId);
                sb.append(" is different than previous ");
                sb.append(this.mJobId);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        
        public void serviceProcessingFinished() {
        }
        
        public void serviceProcessingStarted() {
        }
        
        public void serviceStartReceived() {
        }
    }
}
