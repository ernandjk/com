package com.onesignal;

import android.os.Bundle;
import android.content.Context;
import org.json.JSONArray;
import android.app.Activity;
import kotlin.jvm.JvmStatic;
import org.json.JSONException;
import kotlin.jvm.internal.Intrinsics;
import org.json.JSONObject;
import kotlin.Metadata;

@Metadata(bv = { 1, 0, 3 }, d1 = { "\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c0\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0006H\u0007J\u001c\u0010\f\u001a\u00020\b2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0007J\b\u0010\u0011\u001a\u00020\bH\u0003¨\u0006\u0012" }, d2 = { "Lcom/onesignal/OSInAppMessagePreviewHandler;", "", "()V", "inAppPreviewPushUUID", "", "payload", "Lorg/json/JSONObject;", "notificationOpened", "", "activity", "Landroid/app/Activity;", "jsonData", "notificationReceived", "context", "Landroid/content/Context;", "bundle", "Landroid/os/Bundle;", "shouldDisplayNotification", "onesignal_release" }, k = 1, mv = { 1, 4, 2 })
public final class OSInAppMessagePreviewHandler
{
    public static final OSInAppMessagePreviewHandler INSTANCE;
    
    static {
        INSTANCE = new OSInAppMessagePreviewHandler();
    }
    
    private OSInAppMessagePreviewHandler() {
    }
    
    @JvmStatic
    public static final String inAppPreviewPushUUID(JSONObject customJSONObject) {
        Intrinsics.checkNotNullParameter((Object)customJSONObject, "payload");
        final String s = null;
        try {
            customJSONObject = NotificationBundleProcessor.getCustomJSONObject(customJSONObject);
            Intrinsics.checkNotNullExpressionValue((Object)customJSONObject, "NotificationBundleProces\u2026CustomJSONObject(payload)");
            if (!customJSONObject.has("a")) {
                return null;
            }
            final JSONObject optJSONObject = customJSONObject.optJSONObject("a");
            String optString = s;
            if (optJSONObject != null) {
                optString = s;
                if (optJSONObject.has("os_in_app_message_preview_id")) {
                    optString = optJSONObject.optString("os_in_app_message_preview_id");
                }
            }
            return optString;
        }
        catch (final JSONException ex) {
            return s;
        }
    }
    
    @JvmStatic
    public static final boolean notificationOpened(final Activity activity, final JSONObject jsonObject) {
        Intrinsics.checkNotNullParameter((Object)activity, "activity");
        Intrinsics.checkNotNullParameter((Object)jsonObject, "jsonData");
        final String inAppPreviewPushUUID = inAppPreviewPushUUID(jsonObject);
        if (inAppPreviewPushUUID != null) {
            OneSignal.openDestinationActivity(activity, new JSONArray().put((Object)jsonObject));
            OneSignal.getInAppMessageController().displayPreviewMessage(inAppPreviewPushUUID);
            return true;
        }
        return false;
    }
    
    @JvmStatic
    public static final boolean notificationReceived(final Context context, final Bundle bundle) {
        final JSONObject bundleAsJSONObject = NotificationBundleProcessor.bundleAsJSONObject(bundle);
        Intrinsics.checkNotNullExpressionValue((Object)bundleAsJSONObject, "NotificationBundleProces\u2026undleAsJSONObject(bundle)");
        final String inAppPreviewPushUUID = inAppPreviewPushUUID(bundleAsJSONObject);
        if (inAppPreviewPushUUID != null) {
            if (OneSignal.isAppActive()) {
                OneSignal.getInAppMessageController().displayPreviewMessage(inAppPreviewPushUUID);
            }
            else if (OSInAppMessagePreviewHandler.INSTANCE.shouldDisplayNotification()) {
                GenerateNotification.displayIAMPreviewNotification(new OSNotificationGenerationJob(context, bundleAsJSONObject));
            }
            return true;
        }
        return false;
    }
    
    private final boolean shouldDisplayNotification() {
        return true;
    }
}
