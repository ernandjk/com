package com.onesignal;

import android.net.Uri;
import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007" }, d2 = { "Lcom/onesignal/NavigateToAndroidSettingsForLocation;", "", "()V", "show", "", "context", "Landroid/content/Context;", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class NavigateToAndroidSettingsForLocation
{
    public static final NavigateToAndroidSettingsForLocation INSTANCE;
    
    static {
        INSTANCE = new NavigateToAndroidSettingsForLocation();
    }
    
    private NavigateToAndroidSettingsForLocation() {
    }
    
    public final void show(final Context context) {
        Intrinsics.checkNotNullParameter((Object)context, "context");
        final Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        final StringBuilder sb = new StringBuilder("package:");
        sb.append(context.getPackageName());
        intent.setData(Uri.parse(sb.toString()));
        context.startActivity(intent);
    }
}
