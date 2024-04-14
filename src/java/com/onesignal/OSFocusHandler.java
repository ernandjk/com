package com.onesignal;

import kotlin.Unit;
import androidx.work.WorkRequest;
import androidx.work.OneTimeWorkRequest;
import androidx.work.ExistingWorkPolicy;
import java.util.concurrent.TimeUnit;
import androidx.work.OneTimeWorkRequest$Builder;
import android.content.Context;
import kotlin.jvm.internal.Intrinsics;
import androidx.work.NetworkType;
import androidx.work.Constraints$Builder;
import androidx.work.Constraints;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0005\u0018\u0000 \u00182\u00020\u0001:\u0002\u0018\u0019B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0002J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\u000eJ\u0006\u0010\u000f\u001a\u00020\u000eJ\b\u0010\u0010\u001a\u00020\bH\u0002J\b\u0010\u0011\u001a\u00020\bH\u0002J\u0006\u0010\u0012\u001a\u00020\bJ\u001e\u0010\u0013\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\u0016\u001a\u00020\bJ\u0006\u0010\u0017\u001a\u00020\bR\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a" }, d2 = { "Lcom/onesignal/OSFocusHandler;", "", "()V", "stopRunnable", "Ljava/lang/Runnable;", "buildConstraints", "Landroidx/work/Constraints;", "cancelOnLostFocusWorker", "", "tag", "", "context", "Landroid/content/Context;", "hasBackgrounded", "", "hasCompleted", "resetBackgroundState", "resetStopState", "startOnFocusWork", "startOnLostFocusWorker", "delay", "", "startOnStartFocusWork", "startOnStopFocusWork", "Companion", "OnLostFocusWorker", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSFocusHandler
{
    public static final Companion Companion;
    private static boolean backgrounded = false;
    private static boolean completed = false;
    private static final long stopDelay = 1500L;
    private static boolean stopped;
    private Runnable stopRunnable;
    
    static {
        Companion = new Companion(null);
    }
    
    public static final /* synthetic */ void access$setBackgrounded$cp(final boolean backgrounded) {
        OSFocusHandler.backgrounded = backgrounded;
    }
    
    public static final /* synthetic */ void access$setCompleted$cp(final boolean completed) {
        OSFocusHandler.completed = completed;
    }
    
    private final Constraints buildConstraints() {
        final Constraints build = new Constraints$Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        Intrinsics.checkNotNullExpressionValue((Object)build, "Constraints.Builder()\n  \u2026TED)\n            .build()");
        return build;
    }
    
    private final void resetBackgroundState() {
        this.resetStopState();
        OSFocusHandler.backgrounded = false;
    }
    
    private final void resetStopState() {
        OSFocusHandler.stopped = false;
        final Runnable stopRunnable = this.stopRunnable;
        if (stopRunnable != null) {
            OSTimeoutHandler.getTimeoutHandler().destroyTimeout(stopRunnable);
        }
    }
    
    public final void cancelOnLostFocusWorker(final String s, final Context context) {
        Intrinsics.checkNotNullParameter((Object)s, "tag");
        Intrinsics.checkNotNullParameter((Object)context, "context");
        OSWorkManagerHelper.getInstance(context).cancelAllWorkByTag(s);
    }
    
    public final boolean hasBackgrounded() {
        return OSFocusHandler.backgrounded;
    }
    
    public final boolean hasCompleted() {
        return OSFocusHandler.completed;
    }
    
    public final void startOnFocusWork() {
        this.resetBackgroundState();
        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "OSFocusHandler running onAppFocus");
        OneSignal.onAppFocus();
    }
    
    public final void startOnLostFocusWorker(final String s, final long n, final Context context) {
        Intrinsics.checkNotNullParameter((Object)s, "tag");
        Intrinsics.checkNotNullParameter((Object)context, "context");
        final WorkRequest build = ((OneTimeWorkRequest$Builder)((OneTimeWorkRequest$Builder)((OneTimeWorkRequest$Builder)new OneTimeWorkRequest$Builder((Class)OSFocusHandler.OSFocusHandler$OnLostFocusWorker.class).setConstraints(this.buildConstraints())).setInitialDelay(n, TimeUnit.MILLISECONDS)).addTag(s)).build();
        Intrinsics.checkNotNullExpressionValue((Object)build, "OneTimeWorkRequest.Build\u2026tag)\n            .build()");
        OSWorkManagerHelper.getInstance(context).enqueueUniqueWork(s, ExistingWorkPolicy.KEEP, (OneTimeWorkRequest)build);
    }
    
    public final void startOnStartFocusWork() {
        if (OSFocusHandler.stopped) {
            OSFocusHandler.stopped = false;
            final Runnable runnable = null;
            this.stopRunnable = null;
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "OSFocusHandler running onAppStartFocusLogic");
            OneSignal.onAppStartFocusLogic();
        }
        else {
            this.resetStopState();
        }
    }
    
    public final void startOnStopFocusWork() {
        final Runnable stopRunnable = (Runnable)OSFocusHandler$startOnStopFocusWork.OSFocusHandler$startOnStopFocusWork$1.INSTANCE;
        OSTimeoutHandler.getTimeoutHandler().startTimeout(1500L, stopRunnable);
        final Unit instance = Unit.INSTANCE;
        this.stopRunnable = stopRunnable;
    }
    
    @Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000b" }, d2 = { "Lcom/onesignal/OSFocusHandler$Companion;", "", "()V", "backgrounded", "", "completed", "stopDelay", "", "stopped", "onLostFocusDoWork", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
    public static final class Companion
    {
        private Companion() {
        }
        
        public final void onLostFocusDoWork() {
            final ActivityLifecycleHandler activityLifecycleHandler = ActivityLifecycleListener.getActivityLifecycleHandler();
            if (activityLifecycleHandler == null || activityLifecycleHandler.getCurActivity() == null) {
                OneSignal.setInForeground(false);
            }
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.DEBUG, "OSFocusHandler running onAppLostFocus");
            OSFocusHandler.access$setBackgrounded$cp(true);
            OneSignal.onAppLostFocus();
            OSFocusHandler.access$setCompleted$cp(true);
        }
    }
}
