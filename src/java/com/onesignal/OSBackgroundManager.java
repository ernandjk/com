package com.onesignal;

import kotlin.jvm.internal.Intrinsics;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0016\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b¨\u0006\t" }, d2 = { "Lcom/onesignal/OSBackgroundManager;", "", "()V", "runRunnableOnThread", "", "runnable", "Ljava/lang/Runnable;", "threadName", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public class OSBackgroundManager
{
    public final void runRunnableOnThread(final Runnable runnable, final String s) {
        Intrinsics.checkNotNullParameter((Object)runnable, "runnable");
        Intrinsics.checkNotNullParameter((Object)s, "threadName");
        if (OSUtils.isRunningOnMainThread()) {
            new Thread(runnable, s).start();
        }
        else {
            runnable.run();
        }
    }
}
