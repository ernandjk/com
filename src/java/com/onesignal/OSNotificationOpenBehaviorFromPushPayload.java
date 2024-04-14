package com.onesignal;

import android.net.Uri;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONObject;
import android.content.Context;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0007\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0013\u0010\u000b\u001a\u0004\u0018\u00010\f8F¢\u0006\u0006\u001a\u0004\b\r\u0010\u000e¨\u0006\u000f" }, d2 = { "Lcom/onesignal/OSNotificationOpenBehaviorFromPushPayload;", "", "context", "Landroid/content/Context;", "fcmPayload", "Lorg/json/JSONObject;", "(Landroid/content/Context;Lorg/json/JSONObject;)V", "shouldOpenApp", "", "getShouldOpenApp", "()Z", "uri", "Landroid/net/Uri;", "getUri", "()Landroid/net/Uri;", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSNotificationOpenBehaviorFromPushPayload
{
    private final Context context;
    private final JSONObject fcmPayload;
    
    public OSNotificationOpenBehaviorFromPushPayload(final Context context, final JSONObject fcmPayload) {
        Intrinsics.checkNotNullParameter((Object)context, "context");
        Intrinsics.checkNotNullParameter((Object)fcmPayload, "fcmPayload");
        this.context = context;
        this.fcmPayload = fcmPayload;
    }
    
    public final boolean getShouldOpenApp() {
        return OSNotificationOpenAppSettings.INSTANCE.getShouldOpenActivity(this.context) && this.getUri() == null;
    }
    
    public final Uri getUri() {
        if (!OSNotificationOpenAppSettings.INSTANCE.getShouldOpenActivity(this.context)) {
            return null;
        }
        if (OSNotificationOpenAppSettings.INSTANCE.getSuppressLaunchURL(this.context)) {
            return null;
        }
        final JSONObject jsonObject = new JSONObject(this.fcmPayload.optString("custom"));
        if (jsonObject.has("u")) {
            final String optString = jsonObject.optString("u");
            if (Intrinsics.areEqual((Object)optString, (Object)"") ^ true) {
                Intrinsics.checkNotNullExpressionValue((Object)optString, "url");
                final CharSequence charSequence = (CharSequence)optString;
                int n = charSequence.length() - 1;
                int i = 0;
                int n2 = 0;
                while (i <= n) {
                    int n3;
                    if (n2 == 0) {
                        n3 = i;
                    }
                    else {
                        n3 = n;
                    }
                    final boolean b = Intrinsics.compare((int)charSequence.charAt(n3), 32) <= 0;
                    if (n2 == 0) {
                        if (!b) {
                            n2 = 1;
                        }
                        else {
                            ++i;
                        }
                    }
                    else {
                        if (!b) {
                            break;
                        }
                        --n;
                    }
                }
                return Uri.parse(charSequence.subSequence(i, n + 1).toString());
            }
        }
        return null;
    }
}
