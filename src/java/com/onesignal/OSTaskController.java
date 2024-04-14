package com.onesignal;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

class OSTaskController
{
    static final String OS_PENDING_EXECUTOR = "OS_PENDING_EXECUTOR_";
    private final AtomicLong lastTaskId;
    protected final OSLogger logger;
    private ExecutorService pendingTaskExecutor;
    private final ConcurrentLinkedQueue<Runnable> taskQueueWaitingForInit;
    
    OSTaskController(final OSLogger logger) {
        this.taskQueueWaitingForInit = (ConcurrentLinkedQueue<Runnable>)new ConcurrentLinkedQueue();
        this.lastTaskId = new AtomicLong();
        this.logger = logger;
    }
    
    private void addTaskToQueue(final PendingTaskRunnable pendingTaskRunnable) {
        pendingTaskRunnable.taskId = this.lastTaskId.incrementAndGet();
        final ExecutorService pendingTaskExecutor = this.pendingTaskExecutor;
        if (pendingTaskExecutor == null) {
            final OSLogger logger = this.logger;
            final StringBuilder sb = new StringBuilder("Adding a task to the pending queue with ID: ");
            sb.append(pendingTaskRunnable.taskId);
            logger.debug(sb.toString());
            this.taskQueueWaitingForInit.add((Object)pendingTaskRunnable);
        }
        else if (!pendingTaskExecutor.isShutdown()) {
            final OSLogger logger2 = this.logger;
            final StringBuilder sb2 = new StringBuilder("Executor is still running, add to the executor with ID: ");
            sb2.append(pendingTaskRunnable.taskId);
            logger2.debug(sb2.toString());
            try {
                this.pendingTaskExecutor.submit((Runnable)pendingTaskRunnable);
            }
            catch (final RejectedExecutionException ex) {
                final OSLogger logger3 = this.logger;
                final StringBuilder sb3 = new StringBuilder("Executor is shutdown, running task manually with ID: ");
                sb3.append(pendingTaskRunnable.taskId);
                logger3.info(sb3.toString());
                pendingTaskRunnable.run();
                ex.printStackTrace();
            }
        }
    }
    
    private void onTaskRan(final long n) {
        if (this.lastTaskId.get() == n) {
            OneSignal.Log(OneSignal.LOG_LEVEL.INFO, "Last Pending Task has ran, shutting down");
            this.pendingTaskExecutor.shutdown();
        }
    }
    
    void addTaskToQueue(final Runnable runnable) {
        this.addTaskToQueue(new PendingTaskRunnable(this, runnable));
    }
    
    ConcurrentLinkedQueue<Runnable> getTaskQueueWaitingForInit() {
        return this.taskQueueWaitingForInit;
    }
    
    boolean shouldRunTaskThroughQueue() {
        return !Thread.currentThread().getName().contains((CharSequence)"OS_PENDING_EXECUTOR_") && (!OneSignal.isInitDone() || this.pendingTaskExecutor != null) && ((!OneSignal.isInitDone() && this.pendingTaskExecutor == null) || (this.pendingTaskExecutor.isShutdown() ^ true));
    }
    
    void shutdownNow() {
        final ExecutorService pendingTaskExecutor = this.pendingTaskExecutor;
        if (pendingTaskExecutor != null) {
            pendingTaskExecutor.shutdownNow();
        }
    }
    
    void startPendingTasks() {
        final OneSignal.LOG_LEVEL debug = OneSignal.LOG_LEVEL.DEBUG;
        final StringBuilder sb = new StringBuilder("startPendingTasks with task queue quantity: ");
        sb.append(this.taskQueueWaitingForInit.size());
        OneSignal.Log(debug, sb.toString());
        if (!this.taskQueueWaitingForInit.isEmpty()) {
            this.pendingTaskExecutor = Executors.newSingleThreadExecutor((ThreadFactory)new ThreadFactory(this) {
                final OSTaskController this$0;
                
                public Thread newThread(final Runnable runnable) {
                    final Thread thread = new Thread(runnable);
                    final StringBuilder sb = new StringBuilder("OS_PENDING_EXECUTOR_");
                    sb.append(thread.getId());
                    thread.setName(sb.toString());
                    return thread;
                }
            });
            while (!this.taskQueueWaitingForInit.isEmpty()) {
                this.pendingTaskExecutor.submit((Runnable)this.taskQueueWaitingForInit.poll());
            }
        }
    }
    
    private static class PendingTaskRunnable implements Runnable
    {
        private OSTaskController controller;
        private Runnable innerTask;
        private long taskId;
        
        PendingTaskRunnable(final OSTaskController controller, final Runnable innerTask) {
            this.controller = controller;
            this.innerTask = innerTask;
        }
        
        public void run() {
            this.innerTask.run();
            this.controller.onTaskRan(this.taskId);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PendingTaskRunnable{innerTask=");
            sb.append((Object)this.innerTask);
            sb.append(", taskId=");
            sb.append(this.taskId);
            sb.append('}');
            return sb.toString();
        }
    }
}
