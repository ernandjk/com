package com.onesignal;

import kotlin.jvm.internal.Intrinsics;
import android.content.Context;
import org.json.JSONObject;
import android.content.Intent;
import android.net.Uri;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0014\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0002J\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\bH\u0002¨\u0006\u000f" }, d2 = { "Lcom/onesignal/GenerateNotificationOpenIntentFromPushPayload;", "", "()V", "create", "Lcom/onesignal/GenerateNotificationOpenIntent;", "context", "Landroid/content/Context;", "fcmPayload", "Lorg/json/JSONObject;", "openBrowserIntent", "Landroid/content/Intent;", "uri", "Landroid/net/Uri;", "shouldOpenApp", "", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class GenerateNotificationOpenIntentFromPushPayload
{
    public static final GenerateNotificationOpenIntentFromPushPayload INSTANCE;
    
    static {
        INSTANCE = new GenerateNotificationOpenIntentFromPushPayload();
    }
    
    private GenerateNotificationOpenIntentFromPushPayload() {
    }
    
    private final Intent openBrowserIntent(final Uri uri) {
        if (uri == null) {
            return null;
        }
        return OSUtils.openURLInBrowserIntent(uri);
    }
    
    private final boolean shouldOpenApp(final boolean b, final JSONObject jsonObject) {
        return b | OSInAppMessagePreviewHandler.inAppPreviewPushUUID(jsonObject) != null;
    }
    
    public final GenerateNotificationOpenIntent create(final Context context, final JSONObject jsonObject) {
        Intrinsics.checkNotNullParameter((Object)context, "context");
        Intrinsics.checkNotNullParameter((Object)jsonObject, "fcmPayload");
        final OSNotificationOpenBehaviorFromPushPayload osNotificationOpenBehaviorFromPushPayload = new OSNotificationOpenBehaviorFromPushPayload(context, jsonObject);
        return new GenerateNotificationOpenIntent(context, this.openBrowserIntent(osNotificationOpenBehaviorFromPushPayload.getUri()), this.shouldOpenApp(osNotificationOpenBehaviorFromPushPayload.getShouldOpenApp(), jsonObject));
    }
}
