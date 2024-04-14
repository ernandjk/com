package com.onesignal;

import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007" }, d2 = { "Lcom/onesignal/NavigateToAndroidSettingsForNotifications;", "", "()V", "show", "", "context", "Landroid/content/Context;", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class NavigateToAndroidSettingsForNotifications
{
    public static final NavigateToAndroidSettingsForNotifications INSTANCE;
    
    static {
        INSTANCE = new NavigateToAndroidSettingsForNotifications();
    }
    
    private NavigateToAndroidSettingsForNotifications() {
    }
    
    public final void show(final Context context) {
        Intrinsics.checkNotNullParameter((Object)context, "context");
        final Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.addFlags(268435456);
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        context.startActivity(intent);
    }
}
