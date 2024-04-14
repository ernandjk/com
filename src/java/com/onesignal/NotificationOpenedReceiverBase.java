package com.onesignal;

import kotlin.jvm.internal.Intrinsics;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import kotlin.Metadata;
import android.app.Activity;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b&\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0014J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\tH\u0014¨\u0006\n" }, d2 = { "Lcom/onesignal/NotificationOpenedReceiverBase;", "Landroid/app/Activity;", "()V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onNewIntent", "intent", "Landroid/content/Intent;", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public abstract class NotificationOpenedReceiverBase extends Activity
{
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        NotificationOpenedProcessor.processFromContext((Context)this, this.getIntent());
        this.finish();
    }
    
    protected void onNewIntent(final Intent intent) {
        Intrinsics.checkNotNullParameter((Object)intent, "intent");
        super.onNewIntent(intent);
        NotificationOpenedProcessor.processFromContext((Context)this, this.getIntent());
        this.finish();
    }
}
