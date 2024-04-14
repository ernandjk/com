package com.onesignal;

import androidx.work.impl.WorkManagerImpl;
import kotlin.jvm.JvmStatic;
import androidx.work.Configuration$Builder;
import kotlin.jvm.internal.Intrinsics;
import androidx.work.WorkManager;
import android.content.Context;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\b\u0010\u0007\u001a\u00020\bH\u0003¨\u0006\t" }, d2 = { "Lcom/onesignal/OSWorkManagerHelper;", "", "()V", "getInstance", "Landroidx/work/WorkManager;", "context", "Landroid/content/Context;", "isInitialized", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSWorkManagerHelper
{
    public static final OSWorkManagerHelper INSTANCE;
    
    static {
        INSTANCE = new OSWorkManagerHelper();
    }
    
    private OSWorkManagerHelper() {
    }
    
    @JvmStatic
    public static final WorkManager getInstance(final Context context) {
        synchronized (OSWorkManagerHelper.class) {
            Intrinsics.checkNotNullParameter((Object)context, "context");
            if (!OSWorkManagerHelper.INSTANCE.isInitialized()) {
                WorkManager.initialize(context, new Configuration$Builder().build());
            }
            final WorkManager instance = WorkManager.getInstance(context);
            Intrinsics.checkNotNullExpressionValue((Object)instance, "WorkManager.getInstance(context)");
            return instance;
        }
    }
    
    private final boolean isInitialized() {
        return WorkManagerImpl.getInstance() != null;
    }
}
